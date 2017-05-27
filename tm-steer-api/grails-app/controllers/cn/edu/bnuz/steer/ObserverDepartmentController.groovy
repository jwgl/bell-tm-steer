package cn.edu.bnuz.steer

import cn.edu.bnuz.bell.http.BadRequestException
import cn.edu.bnuz.bell.http.ForbiddenException
import cn.edu.bnuz.bell.master.TermService
import cn.edu.bnuz.bell.security.SecurityService
import org.springframework.security.access.prepost.PreAuthorize


/**
 * 学院督导管理
 */
@PreAuthorize('hasAuthority("PERM_CO_SUPERVISOR_ADMIN")')
class ObserverDepartmentController {
    ObserverSettingService observerSettingService
    SecurityService securityService
    ObserverDepartmentService observerDepartmentService
    def messageSource
    TermService termService
    def index() {
        def collegeSupervisor = messageSource.getMessage("main.supervisor.college",null, Locale.CHINA)
        renderJson(observerDepartmentService.list(securityService.departmentId, collegeSupervisor))
    }


    /**
     * 保存数据
     */
    def save(){
        ObserverCommand cmd = new ObserverCommand()
        bindData cmd, request.JSON
        log.debug cmd.tostring()
        cmd.departmentId = ''
        def type = messageSource.getMessage("main.supervisor.college",null, Locale.CHINA)
        def supervisorRole=ObserverType.findByName(type)
        cmd.roleType = supervisorRole?.id
        println cmd.tostring()
        def form=observerSettingService.save(cmd)
        if(form)  renderJson([id:form?.id])
        else renderBadRequest()
    }


    /**
     * 创建
     */
    def create(){
        println "ObserverDepartmentController"
        def type = messageSource.getMessage("main.supervisor.college",null, Locale.CHINA)
        renderJson(
                roles: observerSettingService.roleTypes().grep{it.name == type},
                activeTerm: termService.activeTerm?.id,
                terms: observerSettingService.terms

        );
    }

    def teachers(){
        String query=params.q
        renderJson(observerDepartmentService.findTeacher(query, securityService.departmentId))

    }

    def countByObserver(){
        renderJson(observerDepartmentService.countByObserver(securityService.departmentId))
    }

    /**
     * 删除
     */
    def delete(Long id){
        def supervisor = Observer.load(id)
        if(!supervisor){
            throw new BadRequestException()
        }
        if(supervisor.department.id != securityService.departmentId){
            throw new ForbiddenException()
        }
        observerSettingService.delete(id)
        renderOk()
    }
}
