package cn.edu.bnuz.steer

import cn.edu.bnuz.bell.http.BadRequestException
import cn.edu.bnuz.bell.http.ForbiddenException
import cn.edu.bnuz.bell.master.Term
import cn.edu.bnuz.bell.master.TermService
import cn.edu.bnuz.bell.organization.Teacher
import cn.edu.bnuz.bell.security.SecurityService
import grails.gorm.transactions.Transactional

@Transactional
class ApprovalService {
    TermService termService
    ObservationCriteriaService observationCriteriaService
    ObservationFormService observationFormService
    ObserverSettingService observerSettingService
    SecurityService securityService

    def list(Integer termId, Integer status) {
        def isAdmin = observerSettingService.isAdmin()
        def dept = isAdmin ? "%" : Teacher.load(securityService.userId).department.name
        List<Integer> types = isAdmin ? [1, 3] : [2]
        def result = ObservationView.executeQuery '''
select new map(
  view.id as id,
  view.supervisorDate as supervisorDate,
  view.evaluateLevel as evaluateLevel,
  view.status as status,
  view.supervisorId as supervisorId,
  view.supervisorName as supervisorName,
  view.observerType as observerType,
  view.courseClassName as courseClassName,
  view.departmentName as departmentName,
  view.teacherId as teacherId,
  view.teacherName as teacherName,
  view.dayOfWeek as dayOfWeek,
  view.startSection as startSection,
  view.totalSection as totalSection,
  view.courseName as course,
  view.termId as termId,
  view.placeName as place
)
from ObservationView view
where view.termId = :termId
  and view.observerType in (:types)
  and view.status = :status
  and view.departmentName like :dept
order by view.supervisorDate
''', [dept: dept, types: types, termId: termId , status: status]

        def counts = ObservationView.executeQuery '''
select new map(
sum(case view.status when 2 then 1 else 0 end) as done,
sum(case view.status when 1 then 1 else 0 end) as todo
)
from ObservationView view
where view.termId = :termId
  and view.observerType in (:types)
  and view.departmentName like :dept
''', [dept: dept, types: types, termId: termId ]
        return [
            term:           Term.findAll("from Term t order by t.id desc"),
            activeTermId:   termId,
            counts:         counts[0],
            list:           result
        ]
    }

    def getFormForShow(Long id) {
        def form = ObservationForm.get(id)
        if (form) {
            def isAdmin = observerSettingService.isAdmin()
            if (!isAdmin && form.teacher.department.id != securityService.departmentId) {
                throw new BadRequestException()
            }
            def evaluationSystem = observationCriteriaService.getObservationCriteriaById(form.observationCriteria?.id)
            evaluationSystem.each { group ->
                group.value.each { item ->
                    item.value = ObservationItem.findByObservationCriteriaItemAndObservationForm(ObservationCriteriaItem.load(item.id), form).value
                }
            }
            return [
                form:             observationFormService.getFormInfo(form),
                evaluationSystem: evaluationSystem,
                isAdmin:          observerSettingService.isAdmin()
            ]
        }
        return null
    }

    def feed(ApprovalController.FeedItems cmd) {
        cmd.ids.each { id->
            def form = ObservationForm.get(id)
            if (form) {
                def isAdmin = observerSettingService.isAdmin()
                if (!isAdmin && Teacher.load(securityService.userId).department.name != findDepartmentName(id)) {
                    throw new ForbiddenException()
                }
                if (form.status != 1) {
                    throw new BadRequestException()
                }
                form.setStatus(2)
                form.save()
            }
        }
    }

    private findDepartmentName(Long id) {
        def view = ObservationView.executeQuery '''
select view.departmentName from ObservationView view where view.id = :id
''',[id: id]
        if (view) {
            return view[0]
        }
    }
}
