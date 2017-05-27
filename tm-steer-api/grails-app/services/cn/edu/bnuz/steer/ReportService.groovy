package cn.edu.bnuz.steer

import cn.edu.bnuz.bell.master.TermService
import cn.edu.bnuz.bell.organization.Teacher
import grails.transaction.Transactional

@Transactional
class ReportService {
    TermService termService
    ObserverSettingService observerSettingService
    def messageSource

    def groupByDepartment(String userId) {
        def term = termService.activeTerm

        def type=messageSource.getMessage("main.supervisor.university",null, Locale.CHINA)
        def result =ObservationForm.executeQuery '''
select new map(
  department.name as department,
  count(*) as supervisorTimes,
  sum(form.totalSection) as totalSection
)
from ObservationForm form
join form.taskSchedule schedule
join form.observerType observerType
join schedule.task task
join task.courseClass courseClass
join courseClass.department department
where form.status > 0
  and observerType.name = :type
  and courseClass.term.id = :termId
group by department.name
''', [termId: term.id, type: type]
        return [
                isAdmin:observerSettingService.isAdmin(userId),
                list: result,
        ]

    }

    def countByObserver(String userId) {
        def term = termService.activeTerm

        def type=messageSource.getMessage("main.supervisor.university",null, Locale.CHINA)
        def result =ObservationForm.executeQuery '''
select new map(
  observer.id as supervisorId,
  observer.name as supervisorName,
  department.name as departmentName,
  count(*) as supervisorTimes,
  sum(form.totalSection) as totalSection
)
from ObservationForm form
join form.observer observer
join form.taskSchedule schedule
join form.observerType observerType
join schedule.task task
join task.courseClass courseClass
join observer.department department
where form.status > 0
  and observerType.name = :type
  and courseClass.term.id = :termId
group by observer,department
''', [termId: term.id, type: type]
        return [
                list: result,
        ]

    }

    def teacherActive(String userId){
        def result=ObservationPriority.executeQuery'''
select new map(
ta.teacherId as teacherId,
ta.teacherName as teacherName,
ta.academicTitle as academicTitle,
ta.departmentName as departmentName,
ta.isnew as isnew
)
from ObservationPriority ta
where ta.hasSupervisor is null
order by ta.departmentName,teacherName
'''
        return [
                isAdmin:observerSettingService.isAdmin(userId),
                list: result,
        ]
    }

    def getSupervisorRole(String userId){
        def term = termService.activeTerm
        if(observerSettingService.isAdmin(userId)){
            def adminSupervisor=messageSource.getMessage("main.supervisor.admin",null, Locale.CHINA)
            return [adminSupervisor]
        } else return observerSettingService.getSupervisorRole(userId, term.id)
    }

    def byTeacherForCollege(String userId) {
        def term = termService.activeTerm
        def type=messageSource.getMessage("main.supervisor.college",null, Locale.CHINA)

        def dept = Teacher.load(userId)?.department.id

        ObservationForm.executeQuery '''
select new map(
  scheduleTeacher.id as teacherId,
  scheduleTeacher.name as teacherName,
  count(*) as supervisorTimes,
  department.name as departmentName
)
from ObservationForm form
join form.teacher scheduleTeacher
join form.taskSchedule schedule
join form.observerType observerType
join schedule.task task
join task.courseClass courseClass
join courseClass.department department
where form.status > 0
  and observerType.name = :type
  and courseClass.term.id = :termId
  and (scheduleTeacher.department.id = :dept or department.id = :dept)
group by scheduleTeacher,department
''', [termId: term.id, type: type, dept:dept]

    }

    def byTeacherForUniversity(){
        ObservationCount.executeQuery '''
select new map(
    view.teacherId as teacherId,
    view.teacherName as teacherName,
    view.departmentName as departmentName,
    view.superviseCount as supervisorTimes
)
from ObservationCount view
'''
    }

}
