package cn.edu.bnuz

import org.springframework.security.access.prepost.PreAuthorize

/**
 * 学院督导管理
 */
@PreAuthorize('hasAuthority("PERM_CO_SUPERVISOR_ADMIN")')
class ObserverDepartmentController {

    def index() {
    }
}
