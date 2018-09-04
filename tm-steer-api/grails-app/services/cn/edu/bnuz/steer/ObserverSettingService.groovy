package cn.edu.bnuz.steer

import cn.edu.bnuz.bell.http.NotFoundException
import cn.edu.bnuz.bell.master.Term
import cn.edu.bnuz.bell.organization.Teacher
import cn.edu.bnuz.bell.security.SecurityService
import cn.edu.bnuz.bell.security.UserLogService
import grails.converters.JSON
import grails.gorm.transactions.Transactional

@Transactional
class ObserverSettingService {
    SecurityService securityService
    UserLogService userLogService

    def save(ObserverCommand cmd) {
        Observer observer = Observer.get(cmd.supervisorId)
        if (observer) {
            observer.setObserverType(cmd.observerType)
            observer.setTermId(cmd.termId)
        } else {
            def teacher = Teacher.get(cmd.userId)
            if (!teacher) {
                throw new NotFoundException()
            }
            observer = Observer.findByTeacherAndTermIdAndObserverType(teacher,cmd.termId,cmd.observerType)
            if (observer) {
                return null
            }
            observer = new Observer(
                    teacher: teacher,
                    department: teacher.department,
                    termId: cmd.termId,
                    observerType: cmd.observerType
            )
        }
        observer.save(flush:true)
    }

    def list() {
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
'''
    }

    def isCollegeSupervisor(String userId, Integer termId) {
        def result = Observer.executeQuery '''
select s.observerType
from Observer s join s.teacher t
where s.termId = :termId and t.id = :userId
''',[userId:userId, termId: termId]
        return result == [2]
    }

    def isAdmin(){
        return securityService.hasRole("ROLE_OBSERVATION_ADMIN") ||
                securityService.hasRole("ROLE_OBSERVER_CAPTAIN")
    }

    def findRolesByUserIdAndTerm(String userId, Integer termId){
        Observer.executeQuery'''
select distinct s.observerType
from Observer s
where s.teacher.id = :userId and s.termId = :termId
''',[userId: userId,termId: termId]
    }

    def findCurrentObservers(Integer termId){
        Observer.executeQuery'''
select distinct new map(
t.id as teacherId,
t.name as teacherName,
s.observerType as observerType
)
from Observer s join s.teacher t
where s.termId = :termId and s.observerType != 2
''',[termId: termId]
    }

    def getTerms(){
        Term.executeQuery'''
select DISTINCT t.id as termId
from Term t
order by t.id desc
'''
    }

    def delete(Long id){
        def form = Observer.get(id)
        if (form) {
            userLogService.log(securityService.userId,securityService.ipAddress,"DELETE", form,"${form as JSON}")
            form.delete()
        }
    }

    def findDeptOfObserver(Integer termId) {
        Observer.executeQuery'''
select distinct s.department.id
from Observer s join s.teacher t
where s.termId = :termId and s.observerType = 2 and t.id = :userId 
''',[termId: termId, userId: securityService.userId]
    }

    String getOtherDepartment() {
        Map departmentMap = ["21": "公共体育教研部", "17": "数学教研部"]
        return departmentMap[securityService.departmentId]
    }
}