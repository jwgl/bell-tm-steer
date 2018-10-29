package cn.edu.bnuz.bell.steer

import cn.edu.bnuz.bell.master.TermService
import cn.edu.bnuz.bell.security.SecurityService
import org.springframework.security.access.prepost.PreAuthorize

/**
 * 教学院长发布学院督导听课
 */
@PreAuthorize('hasAuthority("PERM_OBSERVATION_APPROVE")')
class ApprovalController {
    ApprovalService approvalService
    TermService termService
    SecurityService securityService

    def index(String approverId) {
        Integer termId = params.getInt('termId')
        termId = termId ?: termService.activeTerm.id
        Integer status = params.getInt('status')
        renderJson(approvalService.list(termId, status ?: 1))
    }

    def show(String approverId, Long id) {
        renderJson(approvalService.getFormForShow(id))
    }

    def update(String approverId, Long id) {
        def cmd = new FeedItems()
        bindData(cmd, request.JSON)
        approvalService.feed(cmd)
        renderOk()
    }

    class FeedItems {
        List<Integer> ids
    }
}
