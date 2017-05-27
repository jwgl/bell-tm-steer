package cn.edu.bnuz.steer

import cn.edu.bnuz.bell.master.Term
import cn.edu.bnuz.bell.master.TermService
import cn.edu.bnuz.bell.security.SecurityService
import org.springframework.security.access.prepost.PreAuthorize

/**
 * 教学院长发布学院督导听课
 */
@PreAuthorize('hasAuthority("PERM_CO_SUPERVISOR_APPROVE")')
class ApprovalController {
    ApprovalService approvalService
    TermService termService
    SecurityService securityService

    def index(String approverId) {
        Integer termId = params.getInt('termId')
        termId = termId?:termService.activeTerm.id
        renderJson([term: Term.all,
                    activeTermId: termId,
                    list:approvalService.list(securityService.userId, termId)])
    }

    def show(String approverId, Long id){
        renderJson(approvalService.getFormForShow(securityService.userId, id))
    }

    def update(String approverId, Long id) {
        def cmd = new FeedItems()
        bindData(cmd, request.JSON)
        cmd?.ids?.each { item->
            approvalService.feed(securityService.userId, item)
        }
        renderOk()
    }

    def feed(String approverId){
        Integer id = params.getInt('id')
        approvalService.feed(securityService.userId, id)
        renderJson([ok:true])
    }

    class FeedItems{
        List<Integer> ids
    }
}
