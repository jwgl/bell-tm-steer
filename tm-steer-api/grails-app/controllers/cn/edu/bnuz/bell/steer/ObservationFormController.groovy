package cn.edu.bnuz.bell.steer

import cn.edu.bnuz.bell.http.ForbiddenException
import cn.edu.bnuz.bell.http.ServiceExceptionHandler
import cn.edu.bnuz.bell.report.ReportClientService
import cn.edu.bnuz.bell.report.ReportRequest
import cn.edu.bnuz.bell.security.SecurityService
import cn.edu.bnuz.bell.workflow.Event
import cn.edu.bnuz.steer.ObservationFormCommand
import org.springframework.security.access.prepost.PreAuthorize

/**
 * 本学期督导听课记录
 */
@PreAuthorize('hasAuthority("PERM_OBSERVATION_WRITE")')
class ObservationFormController implements ServiceExceptionHandler{
    ObservationFormService observationFormService
    ReportClientService reportClientService
    SecurityService securityService

    def index(String userId) {
        Integer termId = params.getInt('termId')?:0
        renderJson(observationFormService.list(userId, termId))
    }

    def save(String userId) {
        def cmd = new ObservationFormCommand()
        bindData(cmd, request.JSON)
        def form = observationFormService.create(userId, cmd)
        renderJson([id: form.id])
    }

    def edit(String userId, Long id) {
        renderJson(observationFormService.getFormForEdit(userId, id))
    }

    def show(String userId, Long id) {
        renderJson(observationFormService.getFormForShow(userId, id))
    }

    def update(String userId, Long id) {
        def cmd = new ObservationFormCommand()
        bindData(cmd, request.JSON)
        cmd.id = id
        observationFormService.update(userId, cmd)
        renderOk()
    }

    def delete(String userId, Long id) {
        observationFormService.delete(userId, id)
        renderOk()
    }

    def patch(String userId, Long id, String op) {
        def operation = Event.valueOf(op)
        switch (operation) {
            case Event.SUBMIT:
                observationFormService.submit(userId, id)
                break
            case Event.FINISH:
                observationFormService.feed(id)
                break
            case Event.CANCEL:
                observationFormService.cancel(id)
                break
        }
        renderJson([ok:true])
    }

    def report(Integer termId) {
        def reportName
        if (securityService.hasRole("ROLE_OBSERVATION_ADMIN") || !termId ){
            reportName = 'steer-observations-all'

        } else if (securityService.hasRole("ROLE_OBSERVER_CAPTAIN") && termId ){
            reportName = 'steer-list-for-captain'
        } else {
            throw new ForbiddenException()
        }
        def reportRequest = new ReportRequest(
                reportName: reportName,
                parameters: [termId: termId]
        )
        reportClientService.runAndRender(reportRequest, response)
    }
}
