package cn.edu.bnuz.bell.steer

import cn.edu.bnuz.bell.master.TermService
import cn.edu.bnuz.bell.organization.Teacher
import cn.edu.bnuz.bell.security.SecurityService
import grails.gorm.transactions.Transactional

@Transactional
class DeanTeachingService {
    TermService termService
    ObserverSettingService observerSettingService
    SecurityService securityService

    def list(String userId, Integer termId) {
        def term = termService.activeTerm
        def dept = Teacher.load(securityService.userId).department.name
        def result = listForDean(termId, dept)
        if (securityService.departmentId in ["21", "17"]) {
            result += listForDean(termId, observerSettingService.otherDepartment)
        }
        return [list: result,
                activeTerm: termId ?: term.id,
                terms: observerSettingService.terms]
    }

    def listForDean(Integer termId, String dept) {
        def term = termService.activeTerm
        ObservationView.executeQuery '''
select new map(
  view.id as id,
  view.supervisorDate as supervisorDate,
  view.evaluateLevel as evaluateLevel,
  view.status as status,
  view.observerType as observerType,
  view.courseClassName as courseClassName,
  view.departmentName as departmentName,
  view.teacherId as teacherId,
  view.teacherName as teacherName,
  view.dayOfWeek as dayOfWeek,
  view.startSection as startSection,
  view.totalSection as totalSection,
  view.formTotalSection as formTotalSection,
  view.courseName as course,
  view.placeName as place
)
from ObservationView view
where view.termId = :termId and view.departmentName = :detp and view.status = 2
order by view.supervisorDate desc
''', [detp: dept, termId: termId ?: term.id]
    }
}
