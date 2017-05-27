package cn.edu.bnuz.steer

import cn.edu.bnuz.bell.http.BadRequestException
import cn.edu.bnuz.bell.http.ForbiddenException
import cn.edu.bnuz.bell.organization.Teacher
import grails.transaction.Transactional

@Transactional
class ApprovalService {
    ScheduleService scheduleService
    ObserverSettingService observerSettingService
    def messageSource

    def list(String userId, Integer termId){
        def me = Teacher.load(userId)
        if (!me) return null
        def isAdmin = observerSettingService.isAdmin(userId)
        def dept=isAdmin? "%" : me.department.name
        def collegeSupervisor=
                isAdmin? messageSource.getMessage("main.supervisor.university",null, Locale.CHINA) :
                messageSource.getMessage("main.supervisor.college",null, Locale.CHINA)
        ObservationForm.executeQuery '''
select new map(
  form.id as id,
  form.supervisorDate as supervisorDate,
  form.evaluateLevel as evaluateLevel,
  form.status as status,
  observerType.name as typeName,
  schedule.id as scheduleId,
  courseClass.name as courseClassName,
  courseClass.term.id as termId,
  department.name as department,
  scheduleTeacher.id as teacherId,
  scheduleTeacher.name as teacherName,
  observer.id as supervisorId,
  observer.name as supervisorName,
  schedule.dayOfWeek as dayOfWeek,
  schedule.startSection as startSection,
  schedule.totalSection as totalSection,
  course.name as course,
  place.name as place
)
from ObservationForm form
join form.taskSchedule schedule
join form.observerType observerType
join form.observer observer
join schedule.task task
join task.courseClass courseClass
join courseClass.course course
join courseClass.department department
join schedule.teacher scheduleTeacher
left join schedule.place place
where observer.department.name like :dept
  and form.status > 0
  and observerType.name = :role
  and courseClass.term.id = :termId
order by form.supervisorDate
''', [dept: dept, role: collegeSupervisor, termId: termId]
    }

    def getFormForShow(String userId, Long id){
        def form = ObservationForm.get(id)
        def me = Teacher.load(userId)
        if (!me) return null
        if(form) {
            def isAdmin = observerSettingService.isAdmin(userId)
            if(!isAdmin && form.teacher?.department?.id !=me.department?.id){
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
                type: form.observerType.id,
                typeName: form.observerType.name,
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

    def feed(String userId, Long id){
        def form = ObservationForm.get(id)

        if(form) {
            def isAdmin = observerSettingService.isAdmin(userId)
            def me = Teacher.load(userId)
            if (!isAdmin && (!me || me.department?.id !=form.observer?.department?.id)) {
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
