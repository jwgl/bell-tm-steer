package cn.edu.bnuz

import cn.edu.bnuz.bell.http.ForbiddenException
import cn.edu.bnuz.bell.report.ReportClientService
import cn.edu.bnuz.bell.report.ReportRequest
import cn.edu.bnuz.bell.report.ReportResponse
import cn.edu.bnuz.bell.security.SecurityService
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize

@PreAuthorize('hasAuthority("PERM_OBSERVATION_WRITE")')
class ObservationFormController {
    ReportClientService reportClientService
    SecurityService securityService

    def index() { }
    def show(Long id) {
        if (securityService.hasRole("ROLE_OBSERVATION_ADMIN") || !id ){
            report(new ReportRequest(
                    reportService: 'tm-report',
                    reportName: 'steer-observations-all',
                    format: 'xlsx',
                    parameters: [termId: id]
            ))
        } else {
             throw new ForbiddenException()
        }

    }

    private report(ReportRequest reportRequest) {
        ReportResponse reportResponse = reportClientService.runAndRender(reportRequest)

        if (reportResponse.statusCode == HttpStatus.OK) {
            response.setHeader('Content-Disposition', reportResponse.contentDisposition)
            response.outputStream << reportResponse.content
        } else {
            response.setStatus(reportResponse.statusCode.value())
        }
    }
}
