package cn.edu.bnuz.steer

import cn.edu.bnuz.bell.http.ForbiddenException
import cn.edu.bnuz.bell.master.TermService
import grails.transaction.Transactional

@Transactional
class RewardService {
    TermService termService
    ObserverSettingService observerSettingService
    def messageSource
    def list(String userId, String month){
        def term = termService.activeTerm
        if(!observerSettingService.isAdmin()) {
            throw new ForbiddenException()
        }
        def supervisor=messageSource.getMessage("main.supervisor.university",null, Locale.CHINA)
        ObservationForm.executeQuery '''
select new map(
  form.id as id,
  form.supervisorDate as supervisorDate,
  form.status as status,
  observer.id as supervisorId,
  observer.name as supervisorName,
  observerType.name as typeName,
  schedule.id as scheduleId,
  department.name as department,
  scheduleTeacher.id as teacherId,
  scheduleTeacher.name as teacherName,
  schedule.dayOfWeek as dayOfWeek,
  schedule.startSection as startSection,
  schedule.totalSection as totalSection,
  course.name as course,
  courseClass.term.id as termId
)
from ObservationForm form
join form.observer observer
join form.taskSchedule schedule
join form.observerType observerType
join schedule.task task
join task.courseClass courseClass
join courseClass.course course
join courseClass.department department
join schedule.teacher scheduleTeacher
where courseClass.term.id = :termId
and form.status>0
and form.supervisorDate like :date
and observerType.name = :role
and form.rewardDate is null
order by form.supervisorDate
''', [ termId: term.id, date:"%-${month}-%", role:supervisor]
    }

    def getMonthes(){
        def term = termService.activeTerm
        ObservationForm.executeQuery '''
select distinct substring(form.supervisorDate,6,2)
from ObservationForm form
join form.taskSchedule schedule
join schedule.task task
join task.courseClass courseClass
where courseClass.term.id = :termId
order by substring(form.supervisorDate,6,2)
''', [ termId: term.id]
    }

    def done(String userId, String month){
        def term = termService.activeTerm
        if(!observerSettingService.isAdmin()) {
            throw new ForbiddenException()
        }
        def type=messageSource.getMessage("main.supervisor.university",null, Locale.CHINA)
        ObservationForm.executeUpdate'''
update ObservationForm f
set f.rewardDate = now()
where id in(
select v.id from ObservationView v
where v.termid = :termId and v.status > 0
and substring(v.supervisorDate,6,2) = :month
and v.typeName = :type
)
''',[termId: term.id, month: month, type: type]
    }
}
