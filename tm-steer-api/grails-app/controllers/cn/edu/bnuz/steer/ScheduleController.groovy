package cn.edu.bnuz.steer

import cn.edu.bnuz.bell.master.TermService
import cn.edu.bnuz.bell.security.SecurityService
import org.springframework.security.access.prepost.PreAuthorize

/**
 * 课表
 */
@PreAuthorize('hasAuthority("PERM_OBSERVATION_WRITE")')
class ScheduleController {
    ScheduleService scheduleService
    TermService termService
    ReportService reportService
    SecurityService securityService

    def index() {
        SheduleOptionsCommand cmd = new SheduleOptionsCommand([
            teacherId:      params['teacherId'],
            place:          params['place'],
            departmentId:   params['departmentId'],
            weekOfTerm:     params.getInt('weekOfTerm')?:0,
            dayOfWeek:      params.getInt('dayOfWeek')?:0,
            startSection:   params.getInt('startSection'),
            endSection:     params.getInt('endSection')
        ])
        def schedule = scheduleService.getTeacherSchedules(securityService.userId, cmd)
        scheduleService.observationPermission(schedule)
        renderJson(schedule)
    }

    def create() {
        renderJson(scheduleService.getFormForCreate(securityService.userId))
    }

    def getTerm() {
        def term = termService.activeTerm
        renderJson([
            startWeek:      term.startWeek,
            maxWeek:        term.maxWeek,
            currentWeek:    term.currentWorkWeek,
            startDate:      term.startDate,
            swapDates:      term.swapDates,
            endWeek:        term.endWeek,
        ])
    }

    def teacherActiveList(String userId) {
        def term = termService.activeTerm
        if (scheduleService.isCollegeSupervisor(userId,term.id)) {
            renderBadRequest()
        } else {
            renderJson(reportService.teacherActive())
        }
    }
}
