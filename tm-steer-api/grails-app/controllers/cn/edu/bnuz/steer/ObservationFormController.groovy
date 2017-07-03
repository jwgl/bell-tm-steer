package cn.edu.bnuz.steer

import cn.edu.bnuz.bell.workflow.Event
import cn.edu.bnuz.bell.workflow.commands.SubmitCommand
import org.springframework.security.access.prepost.PreAuthorize

/**
 * 本学期督导听课记录
 */
@PreAuthorize('hasAuthority("PERM_OBSERVATION_WRITE")')
class ObservationFormController {
    ObservationFormService observationFormService

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
        println cmd.tostring()
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
                def cmd = new SubmitCommand()
                bindData(cmd, request.JSON)
                cmd.id = id
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
}
