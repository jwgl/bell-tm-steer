package cn.edu.bnuz

import org.springframework.security.access.prepost.PreAuthorize

/**
 * 学院督导听课记录发布
 */
@PreAuthorize('hasAuthority("PERM_CO_SUPERVISOR_APPROVE")')
class ApprovalController {

    def index() { }
}
