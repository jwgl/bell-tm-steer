package cn.edu.bnuz.bell.steer

import cn.edu.bnuz.bell.master.TermService
import cn.edu.bnuz.bell.organization.DepartmentService
import cn.edu.bnuz.steer.ObserverCommand
import org.springframework.security.access.prepost.PreAuthorize

/**
 * 督导员管理
 */
@PreAuthorize('hasAuthority("PERM_OBSERVER_ADMIN")')
class SettingController {
    ObserverSettingService observerSettingService
    DepartmentService departmentService
    TermService termService

    def index() {
        renderJson(observerSettingService.list())
    }

    /**
     * 保存数据
     */
    def save() {
        ObserverCommand cmd = new ObserverCommand()
        bindData cmd, request.JSON
        def form = observerSettingService.save(cmd)
        if (form) {
            renderJson([id:form.id])
        } else {
            renderBadRequest()
        }
    }

    /**
     * 创建
     */
    def create() {
        renderJson(
            departments: departmentService.teachingDepartments,
            activeTerm: termService.activeTerm.id,
            terms: observerSettingService.terms
        );
    }

    /**
     * 删除
     */
    def delete(Long id) {
        observerSettingService.delete(id)
        renderOk()
    }
}
