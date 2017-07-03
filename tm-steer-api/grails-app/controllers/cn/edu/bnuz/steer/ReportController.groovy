package cn.edu.bnuz.steer

import cn.edu.bnuz.bell.http.BadRequestException
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
            default:
                renderJson([isAdmin:securityService.hasRole("ROLE_OBSERVATION_ADMIN")])
        }

    }

    def reward(String month, Boolean done){
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

//    def rewardDone(){
//        String month = params.month
//        if (!month || month == "null") {
//            throw new BadRequestException()
//        } else {
//            rewardService.done(month)
//            renderJson([ok:true])
//        }
//    }
}
