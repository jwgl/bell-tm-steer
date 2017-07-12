package cn.edu.bnuz

import org.springframework.security.access.prepost.PreAuthorize

/**
 * 学院督导管理
 */
@PreAuthorize('hasAuthority("PERM_OBSERVER_DEPT_ADMIN")')
class ObserverDepartmentController {

    def index() {
    }
}
