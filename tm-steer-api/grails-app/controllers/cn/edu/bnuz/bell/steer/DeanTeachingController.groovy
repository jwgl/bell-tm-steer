package cn.edu.bnuz.bell.steer

import cn.edu.bnuz.bell.http.ServiceExceptionHandler
import org.springframework.security.access.prepost.PreAuthorize

@PreAuthorize('hasRole("ROLE_DEAN_OF_TEACHING")')
class DeanTeachingController implements ServiceExceptionHandler {
    DeanTeachingService deanTeachingService
    ObservationFormService observationFormService
    def index(String userId) {
        Integer termId = params.getInt('termId')?:0
        renderJson deanTeachingService.list(userId, termId)
    }

    def show(String userId, Long id) {
        renderJson(observationFormService.getFormForShow(userId, id))
    }
}
