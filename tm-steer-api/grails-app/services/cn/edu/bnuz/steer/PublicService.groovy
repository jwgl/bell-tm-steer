package cn.edu.bnuz.steer

import cn.edu.bnuz.bell.http.BadRequestException
import cn.edu.bnuz.bell.master.TermService
import grails.transaction.Transactional
import cn.edu.bnuz.bell.http.ForbiddenException

@Transactional
class PublicService {
    TermService termService
    ScheduleService scheduleService

    def list(String userId){
        ObservationPublic.executeQuery '''
select new map(
  form.id as id,
  form.isLegacy as isLegacy,
  form.supervisorDate as supervisorDate,
  form.evaluateLevel as evaluateLevel,
  form.typeName as typeName,
  form.termId as termId,
  form.departmentName as department,
  form.teacherId as teacherId,
  form.teacherName as teacherName,
  form.courseName as course,
  form.courseOtherInfo as courseOtherInfo
)
from ObservationPublic form
where form.teacherId = :userId
order by form.supervisorDate
''', [userId: userId]
    }

    def getFormForShow(String userId, Long id){
        def form = ObservationForm.get(id)

        if(form) {
            if(form.teacher.id !=userId){
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

    def legacylist(String userId){
        ObservationLegacyForm.executeQuery'''
select new map(
l.id as id,
l.teachercode as teachercode,
l.teachername as teachername,
l.coursename as coursename,
l.courseproperty as courseproperty,
l.classpostion as classpostion,
l.collegename as collegename,
l.listentime as listentime,
l.evaluategrade as evaluategrade
)
from ObservationLegacyForm l
where l.teachercode = :userId and l.state = 'yes'
''',[userId: userId]
    }

    def legacyShow(String userId, Long id){
        def form = ObservationLegacyForm.get(id)
        if(!form || form.teachercode != userId){
            throw new ForbiddenException()
        }else {
            form.inpectorcode =null
            form.inpectorname =null
            return form
        }
    }

}
