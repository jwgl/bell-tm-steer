package cn.edu.bnuz

import cn.edu.bnuz.bell.http.BadRequestException
import cn.edu.bnuz.bell.http.ForbiddenException
import cn.edu.bnuz.bell.report.ReportClientService
import cn.edu.bnuz.bell.report.ReportRequest
import cn.edu.bnuz.bell.report.ReportResponse
import cn.edu.bnuz.bell.security.SecurityService
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize

@PreAuthorize('hasAuthority("PERM_OBSERVATION_WRITE")')
class ReportController {
    ReportClientService reportClientService
    SecurityService securityService
    def index(String userId) { }

    def show(String userId, Long id) {
        report(new ReportRequest(
                reportService: 'tm-report',
                reportName: 'steer-observation-detail',
                format: 'pdf',
                parameters: [idKey:'formId', formId: id, userId: securityService.userId]
        ))

    }

    def observePriority() {
        report(new ReportRequest(
                reportService: 'tm-report',
                reportName: 'steer-observe-priority-list',
                format: 'xlsx'
        ))
    }

    def reward(){
        String month = params.month
        if (!month) throw new BadRequestException()
        if (securityService.hasRole('ROLE_OBSERVATION_ADMIN')) {
            report(new ReportRequest(
                    reportService: 'tm-report',
                    reportName: 'steer-reward-list',
                    format: 'xlsx',
                    parameters: [month: month]
            ))
        } else {
            throw new ForbiddenException()
        }
    }

    def wages() {
        Integer termId = params.int("termId") ?: 0
        String departmentId
        String reportName = 'steer-wages'
        Integer type = params.int("type") ?: 1
        boolean role = securityService.hasRole('ROLE_OBSERVATION_ADMIN')
        if (role) {
            departmentId = params.departmentId ?: '0'
        } else {
            departmentId = securityService.departmentId
            reportName = 'steer-wages-dept'
        }
        report(new ReportRequest(
                reportService: 'tm-report',
                reportName: reportName,
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
