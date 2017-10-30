package cn.edu.bnuz.steer

import cn.edu.bnuz.bell.http.BadRequestException
import cn.edu.bnuz.bell.master.TermService
import grails.gorm.transactions.Transactional
import cn.edu.bnuz.bell.http.ForbiddenException

@Transactional
class PublicService {
    TermService termService
    ObservationCriteriaService observationCriteriaService
    ObservationFormService observationFormService

    def list(String userId) {
        ObservationPublic.executeQuery '''
select new map(
  form.id as id,
  form.isLegacy as isLegacy,
  form.supervisorDate as supervisorDate,
  form.evaluateLevel as evaluateLevel,
  form.observerType as observerType,
  form.termId as termId,
  form.departmentName as departmentName,
  form.teacherId as teacherId,
  form.teacherName as teacherName,
  form.courseName as course,
  form.placeName as place,
  form.dayOfWeek as dayOfWeek,
  form.startSection as startSection,
  form.totalSection as totalSection
)
from ObservationPublic form
where form.teacherId = :userId
order by form.supervisorDate desc
''', [userId: userId]
    }

    def getFormForShow(String userId, Long id) {
        def form = ObservationForm.get(id)

        if (form) {
            if (form.teacher.id != userId || form.status != 2) {
                throw new BadRequestException()
            }
            def schedule = [
                    evaluationSystem: observationCriteriaService.getObservationCriteriaById(form.observationCriteria.id),
                    form: observationFormService.getFormInfo(form)
            ]
            schedule.evaluationSystem.each { group ->
                group.value.each { item ->
                    item.value = ObservationItem.findByObservationCriteriaItemAndObservationForm(ObservationCriteriaItem.load(item.id), form).value
                }
            }
            return schedule
        }
        return null
    }

    def legacylist(String userId) {
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
where l.teachercode = :userId and l.state
''',[userId: userId]
    }

    def legacyShow(String userId, Long id) {
        def form = ObservationLegacyForm.get(id)
        if (!form || form.teachercode != userId) {
            throw new ForbiddenException()
        }else {
            form.inpectorcode = null
            form.inpectorname = null
            return form
        }
    }

}
