package cn.edu.bnuz

import cn.edu.bnuz.bell.report.ReportClientService
import cn.edu.bnuz.bell.report.ReportRequest
import cn.edu.bnuz.bell.report.ReportResponse
import cn.edu.bnuz.bell.security.SecurityService
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize

/**
 * 学院督导管理
 */
@PreAuthorize('hasAuthority("PERM_OBSERVER_DEPT_ADMIN")')
class ObserverDepartmentController {
    ReportClientService reportClientService
    SecurityService securityService

    def index() {
    }

    def wages() {
        Integer termId = params.int("termId") ?: 0
        String departmentId = securityService.departmentId
        Integer type = 2
        report(new ReportRequest(
                reportService: 'tm-report',
                reportName: 'steer-wages',
                format: 'xlsx',
                parameters: [term_id: termId, department_id: departmentId, type: type]
        ))

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
