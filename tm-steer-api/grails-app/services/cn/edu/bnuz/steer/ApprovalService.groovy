package cn.edu.bnuz.steer

import cn.edu.bnuz.bell.http.BadRequestException
import cn.edu.bnuz.bell.http.ForbiddenException
import cn.edu.bnuz.bell.organization.Teacher
import cn.edu.bnuz.bell.security.SecurityService
import grails.transaction.Transactional

@Transactional
class ApprovalService {
    ScheduleService scheduleService
    ObserverSettingService observerSettingService
    SecurityService securityService

    def list(String userId, Integer termId){
        def me = Teacher.load(userId)
        if (!me) return null
        def isAdmin = observerSettingService.isAdmin()
        def dept=isAdmin? "%" : me.department.name
        def type= isAdmin? 1 : 2
        ObservationForm.executeQuery '''
select new map(
  form.id as id,
  form.supervisorDate as supervisorDate,
  form.evaluateLevel as evaluateLevel,
  form.status as status,
  form.observerType as observerType,
  schedule.id as scheduleId,
  courseClass.name as courseClassName,
  courseClass.term.id as termId,
  department.name as department,
  scheduleTeacher.id as teacherId,
  scheduleTeacher.name as teacherName,
  observer.id as observerId,
  observer.name as observerName,
  schedule.dayOfWeek as dayOfWeek,
  schedule.startSection as startSection,
  schedule.totalSection as totalSection,
  course.name as course,
  place.name as place
)
from ObservationForm form
join form.taskSchedule schedule
join form.observer observer
join schedule.task task
join task.courseClass courseClass
join courseClass.course course
join courseClass.department department
join schedule.teacher scheduleTeacher
left join schedule.place place
where observer.department.name like :dept
  and form.status > 0
  and form.observerType = :type
  and courseClass.term.id = :termId
order by form.supervisorDate
''', [dept: dept, type: type, termId: termId]
    }

    def getFormForShow(Long id){
        def form = ObservationForm.get(id)
        if(form) {
            def isAdmin = observerSettingService.isAdmin()
            if(!isAdmin && form.teacher?.department?.id !=securityService.departmentId){
                throw new BadRequestException()
            }
            def schedule = scheduleService.showSchedule(form.taskSchedule.id.toString())
            schedule.form = getFormInfo(form)
            schedule.evaluationSystem.each { group ->
                group.value.each { item ->
                    item.value = ObservationItem.findByObservationCriteriaItemAndObservationForm(ObservationCriteriaItem.load(item.id), form)?.value
                }
            }
            return schedule
        }
        return null
    }

    Map getFormInfo(ObservationForm form) {
        return [
                id: form.id,
                scheduleId: form.taskSchedule.id,
                teacherId: form.teacher.id,
                supervisorName: form.observer.name,
                supervisorWeek: form.lectureWeek,
                totalSection: form.totalSection,
                teachingMethods: form.teachingMethods,
                supervisorDate: form.supervisorDate,
                type: form.observerType,
                place: form.place,
                earlier: form.earlier,
                late: form.late,
                leave: form.leave,
                dueStds: form.dueStds,
                attendantStds: form.attendantStds,
                lateStds: form.lateStds,
                leaveStds: form.leaveStds,
                evaluateLevel: form.evaluateLevel,
                evaluationText: form.evaluationText,
                suggest: form.suggest,
                status: form.status,
        ]

    }

    def feed(Long id){
        def form = ObservationForm.get(id)

        if(form) {
            def isAdmin = observerSettingService.isAdmin()
            if (!isAdmin && securityService.departmentId !=form.observer?.department?.id) {
                throw new ForbiddenException()
            }
            if (form.status!=1) {
                throw new BadRequestException()
            }
            form.setStatus(2)
            form.save()
        }
    }
}
