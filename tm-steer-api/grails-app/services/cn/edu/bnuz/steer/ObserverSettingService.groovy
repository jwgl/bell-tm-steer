package cn.edu.bnuz.steer

import cn.edu.bnuz.bell.http.NotFoundException
import cn.edu.bnuz.bell.master.Term
import cn.edu.bnuz.bell.organization.Teacher
import cn.edu.bnuz.bell.security.SecurityService
import cn.edu.bnuz.bell.security.UserLogService
import grails.converters.JSON
import grails.transaction.Transactional

@Transactional
class ObserverSettingService {
    SecurityService securityService
    UserLogService userLogService

    def save(ObserverCommand cmd) {
        Observer supervisor=Observer.get(cmd.supervisorId)
        if(supervisor){
            supervisor.setObserverType(cmd.roleType)
            supervisor.setTermId(cmd.termId)
        }else{
            def teacher = Teacher.get(cmd.userId)
            if(!teacher)  throw new NotFoundException()
            supervisor = Observer.findByTeacherAndTermIdAndObserverType(teacher,cmd.termId,ObserverType.load(cmd.roleType))
            if(supervisor) return null
            supervisor = new Observer(
                    teacher: teacher,
                    department: teacher.department,
                    termId: cmd.termId,
                    observerType: ObserverType.load(cmd.roleType)
            )
        }
        supervisor?.save(flush:true)
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
  r.name as roleType
)
from Observer s join s.teacher t join s.department d join s.observerType r
'''
    }


    def roleTypes(){
        ObserverType.executeQuery'''
select new Map(
  r.id as id,
  r.name as name
)
from ObserverType r
'''
    }

    def isCollegeSupervisor(String userId, Integer termId, String type){
        def result=Observer.executeQuery '''
select r.name
from Observer s join s.teacher t,ObserverType r
where s.observerType = r.id and s.termId = :termId and t.id = :userId
''',[userId:userId, termId: termId]
        return result ==[type]
    }

    def isAdmin(String userId){
        Observer supervisor=Observer.findByObserverType(ObserverType.load(0))
        return supervisor?.teacher.id == userId
    }

    def findRolesByUserIdAndTerm(String userId, Integer termId){
        Observer.executeQuery'''
select distinct new map(
role.id as id,
role.name as name
)
from Observer s, ObserverType role
where s.observerType = role.id and s.teacher.id = :userId and s.termId = :termId
''',[userId: userId,termId: termId]
    }

    def findAllRoles(){
        ObserverType.executeQuery'''
select new map(
role.id as id,
role.name as name
)
from ObserverType role
'''
    }

    def findCurrentSupervisors(Integer termId){
        def result= Observer.executeQuery'''
select distinct new map(
t.id as teacherId,
t.name as teacherName,
role.id as roleId,
role.name as roleName
)
from Observer s join s.teacher t, ObserverType role
where s.observerType = role.id and s.termId = :termId
''',[termId: termId]
        return result.groupBy {it.roleId}.entrySet()
    }

    def getSupervisorRole(String userId, Integer termId){
        Observer.executeQuery '''
select r.name
from Observer s join s.teacher t,ObserverType r
where s.observerType = r.id and s.termId = :termId and t.id = :userId
''',[userId:userId, termId: termId]
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
        if(form) {
            userLogService.log(securityService.userId,securityService.ipAddress,"DELETE", form,"${form as JSON}")
            form.delete()
        }
    }

}
