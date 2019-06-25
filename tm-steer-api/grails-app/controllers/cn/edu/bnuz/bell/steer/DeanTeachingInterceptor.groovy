package cn.edu.bnuz.bell.steer

import cn.edu.bnuz.bell.security.SecurityService
import org.springframework.http.HttpStatus


class DeanTeachingInterceptor {
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
