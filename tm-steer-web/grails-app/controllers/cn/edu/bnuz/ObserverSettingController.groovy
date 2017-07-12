package cn.edu.bnuz

import org.springframework.security.access.prepost.PreAuthorize

/**
 * 督导员设置
 */
@PreAuthorize('hasAuthority("PERM_OBSERVER_ADMIN")')
class ObserverSettingController {

    def index() { }

}
