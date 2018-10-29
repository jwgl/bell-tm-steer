package cn.edu.bnuz.bell.steer

import cn.edu.bnuz.bell.master.TermService
import cn.edu.bnuz.bell.organization.Teacher
import cn.edu.bnuz.bell.steer.ObservationForm
import cn.edu.bnuz.bell.steer.Observer
import grails.gorm.transactions.Transactional

@Transactional
class ObserverDepartmentService {
    TermService termService

    def list(String departmentId) {
        Observer.executeQuery '''
select new Map(
  s.id as id,
  t.id as tId,
  t.name as tName,
  t.academicTitle as academicTitle,
  d.id as dId,
  d.name as dName,
  s.termId as termId,
  s.observerType as observerType
)
from Observer s join s.teacher t join s.department d
where d.id = :departmentId and s.observerType = 2
''',[departmentId:departmentId]
    }

    def findTeacher(String query, String departmentId) {
        Teacher.executeQuery '''
select new Map(
  t.id as id,
  t.name as name,
  d.name as department
)
from Teacher t
join t.department d
where t.atSchool = true and d.id = :departmentId
and (t.id like :query or t.name like :query)
''', [query: "%${query}%", departmentId: departmentId]
    }

    def countByObserver(String departmentId) {
        def term = termService.activeTerm
        def result = ObservationForm.executeQuery '''
select new map(
  observer.name as observer,
  count(*) as observeTimes,
  sum(form.totalSection) as totalSection
)
from ObservationForm form
join form.observer observer
join observer.department department
where form.status > 0
  and form.observerType != 1
  and form.termId = :termId
  and department.id = :departmentId
group by observer
''', [termId: term.id, departmentId: departmentId]
        return [
                list: result,
        ]
    }
}
