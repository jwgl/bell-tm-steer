package cn.edu.bnuz.steer

import cn.edu.bnuz.bell.http.BadRequestException
import cn.edu.bnuz.bell.http.ForbiddenException
import cn.edu.bnuz.bell.report.ReportClientService
import cn.edu.bnuz.bell.report.ReportRequest
import cn.edu.bnuz.bell.security.SecurityService
import org.springframework.security.access.prepost.PreAuthorize

/**
 * 统计报表
 */
@PreAuthorize('hasAuthority("PERM_OBSERVATION_WRITE")')
class ReportController {
    ReportService reportService
    RewardService rewardService
    SecurityService securityService
    ReportClientService reportClientService

    def index(String type) {
        switch (type) {
            case "DEPARTMENT-U":
                renderJson(reportService.groupByDepartment(1))
                break
            case "DEPARTMENT-C":
                renderJson(reportService.groupByDepartment(2))
                break
            case "OBSERVER-U":
                renderJson(reportService.countByObserver())
                break
            case "OBSERVER-C":
                renderJson(reportService.countByDeptObserver(securityService.userId))
                break
            case "TEACHER-U":
                renderJson(reportService.byTeacherForUniversity())
                break
            case "TEACHER-C":
                renderJson(reportService.byTeacherForCollege(securityService.userId))
                break
            case "REWARD":
                String month = params.month
                Boolean done = params.getBoolean('done')
                getReward(month, done)
                break
            default:
                renderJson([isAdmin:securityService.hasRole("ROLE_OBSERVATION_ADMIN")])
        }

    }

    def show(Long id) {
        def reportRequest = new ReportRequest(
                reportName: 'steer-observation-detail',
                format: 'pdf',
                parameters: [idKey:'formId', formId: id, userId: securityService.userId]
        )
        reportClientService.runAndRender(reportRequest, response)
    }

    def observePriority() {
        def reportRequest = new ReportRequest(
                reportName: 'steer-observe-priority-list'
        )
        reportClientService.runAndRender(reportRequest, response)
    }

    def reward(String month){
        if (!month) throw new BadRequestException()
        if (securityService.hasRole('ROLE_OBSERVATION_ADMIN')) {
            def reportRequest = new ReportRequest(
                    reportName: 'steer-reward-list',
                    parameters: [month: month]
            )
            reportClientService.runAndRender(reportRequest, response)
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
        def reportRequest = new ReportRequest(
                reportName: reportName,
                parameters: [term_id: termId, department_id: departmentId, type: type]
        )
        reportClientService.runAndRender(reportRequest, response)
    }

    private getReward(String month, Boolean done){
        if (!month || month == "null") {
            def now = new Date().format("MM")
            renderJson([
                monthes: rewardService.monthes,
                month: now,
                list: rewardService.list(now)
            ])
        } else {
            if (done) {
                rewardService.done(month)
                renderJson([ok:true])
            } else {
                renderJson([
                    month: month,
                    list: rewardService.list(month)
                ])
            }
        }
    }
}
