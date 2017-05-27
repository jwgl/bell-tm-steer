package cn.edu.bnuz.steer

import cn.edu.bnuz.bell.security.SecurityService
import org.springframework.http.HttpStatus


class ObservationFormInterceptor {
    SecurityService securityService

    boolean before() {
        if (params.userId != securityService.userId) {
            render(status: HttpStatus.FORBIDDEN)
            return false
        } else {
            return true
        }
    }

    boolean after() { true }

    void afterView() {
        // no-op
    }
}
