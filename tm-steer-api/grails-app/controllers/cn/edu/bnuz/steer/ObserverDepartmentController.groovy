package cn.edu.bnuz.steer

import cn.edu.bnuz.bell.http.BadRequestException
import cn.edu.bnuz.bell.http.ForbiddenException
import cn.edu.bnuz.bell.master.TermService
import cn.edu.bnuz.bell.security.SecurityService
import org.springframework.security.access.prepost.PreAuthorize

/**
 * 学院督导管理
 */
@PreAuthorize('hasAuthority("PERM_OBSERVER_DEPT_ADMIN")')
class ObserverDepartmentController {
    ObserverSettingService observerSettingService
    SecurityService securityService
    ObserverDepartmentService observerDepartmentService
    TermService termService
    def index() {
        renderJson(observerDepartmentService.list(securityService.departmentId))
    }

    /**
     * 保存数据
     */
    def save() {
        ObserverCommand cmd = new ObserverCommand()
        bindData cmd, request.JSON
        cmd.observerType = 2 //院督导
        def form = observerSettingService.save(cmd)
        if (form) {
            renderJson([id:form?.id])
        } else {
            renderBadRequest()
        }
    }

    /**
     * 创建
     */
    def create() {
        renderJson(
                activeTerm: termService.activeTerm?.id,
                terms: observerSettingService.terms

        );
    }

    def teachers() {
        String query = params.q
        renderJson(observerDepartmentService.findTeacher(query, securityService.departmentId))

    }

    def countByObserver() {
        renderJson(observerDepartmentService.countByObserver(securityService.departmentId))
    }

    /**
     * 删除
     */
    def delete(Long id) {
        def supervisor = Observer.load(id)
        if (!supervisor) {
            throw new BadRequestException()
        }
        if (supervisor.department.id != securityService.departmentId) {
            throw new ForbiddenException()
        }
        observerSettingService.delete(id)
        renderOk()
    }
}
