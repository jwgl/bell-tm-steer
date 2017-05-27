package cn.edu.bnuz.steer

import org.springframework.security.access.prepost.PreAuthorize

/**
 * 本学期督导听课记录
 */
@PreAuthorize('hasAuthority("PERM_SUPERVISOR_WRITE")')
class ObservationFormController {
    ObservationFormService observationFormService

    def index(String userId) {
        Integer termId = params.getInt('termId')?:0
        renderJson(observationFormService.list(userId, termId))
    }

    def edit(String userId, Long id) {
        renderJson(observationFormService.getFormForEdit(userId, id))
    }

    def show(String userId, Long id){
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

    def cancel(String userId){
        Integer id = params.getInt('id')
        observationFormService.cancel(userId, id)
        renderJson([ok:true])
    }

    def submit(String userId){
        Integer id = params.getInt('id')
        observationFormService.submit(userId, id)
        renderJson([ok:true])
    }

    def delete(String userId, Long id){
        observationFormService.delete(userId, id)
        renderOk()
    }

    def feed(String userId){
        Integer id = params.getInt('id')
        observationFormService.feed(userId, id)
        renderJson([ok:true])
    }


}
