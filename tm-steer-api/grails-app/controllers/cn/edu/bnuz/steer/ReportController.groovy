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

    def index(String userId){
        renderJson([isadmin:securityService.hasRole("ROLE_OBSERVATION_ADMIN")])
    }

    def departmentReport(String userId){
        renderJson(reportService.groupByDepartment(userId))
    }

    def countByObserver(String userId){
        renderJson(reportService.countByObserver(userId))
    }

    def teacherReport(String userId){
        renderJson(reportService.byTeacherForUniversity())
    }

    def teacherSupervisedReport(String userId){
        String type = params.t
        if("university"==type) renderJson(reportService.byTeacherForUniversity())
        else if("college"==type) renderJson(reportService.byTeacherForCollege(userId))
        else renderBadRequest()
    }

    def reward(String userId){
        String month = params.month
        if(!month || month=="null"){
            def now = new Date().format("MM")
            renderJson([
                    monthes: rewardService.monthes,
                    month: now,
                    list: rewardService.list(userId, now)
            ])
        }else{
            renderJson([
                    month: month,
                    list: rewardService.list(userId, month)
            ])
        }

    }

    def rewardDone(String userId){
        String month = params.month
        println month
        if(!month || month=="null"){
            throw new BadRequestException()
        }else{
            rewardService.done(userId, month)
            renderOk()
        }

    }
}
