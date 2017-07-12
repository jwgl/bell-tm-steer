package cn.edu.bnuz

import cn.edu.bnuz.bell.report.ReportClientService
import cn.edu.bnuz.bell.report.ReportRequest
import cn.edu.bnuz.bell.report.ReportResponse
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize

/**
 * 历史遗留数据
 */
@PreAuthorize('hasAuthority("PERM_OBSERVER_ADMIN")')
class LegacyDataController {
    ReportClientService reportClientService
    def index() { }
    def show(Long id){
        ReportResponse reportResponse = reportClientService.runAndRender(new ReportRequest(
                reportService: 'tm-report',
                reportName: 'steer-legacy-detail',
                format: 'pdf',
                parameters: [formId: id]
        ))

        if (reportResponse.statusCode == HttpStatus.OK) {
            response.setHeader('Content-Disposition', reportResponse.contentDisposition)
            response.outputStream << reportResponse.content
        } else {
            response.setStatus(reportResponse.statusCode.value())
        }
    }
}
