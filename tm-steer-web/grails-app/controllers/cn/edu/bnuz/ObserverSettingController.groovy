package cn.edu.bnuz

import org.springframework.security.access.prepost.PreAuthorize

/**
 * 督导员设置
 */
@PreAuthorize('hasAuthority("PERM_ADMIN_SUPERVISOR_WRITE")')
class ObserverSettingController {

    def index() { }

}
