package cn.edu.bnuz.steer

import cn.edu.bnuz.bell.organization.Teacher
import cn.edu.bnuz.bell.security.SecurityService
import grails.gorm.transactions.Transactional

@Transactional(readOnly = true)
class TeacherService {
    SecurityService securityService

    def find(String query) {
        Teacher.executeQuery '''
select new Map(
  t.id as id,
  t.name as name,
  d.name as department
)
from Teacher t
join t.department d
where t.atSchool = true
and ((t.id like :query or t.name like :query) and d.id = :departmentId)
''', [query: "%${query}%", departmentId: securityService.departmentId]
    }
}
