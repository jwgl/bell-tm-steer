package cn.edu.bnuz.bell.steer

import cn.edu.bnuz.bell.master.TermService
import cn.edu.bnuz.bell.organization.Teacher
import cn.edu.bnuz.bell.security.SecurityService
import cn.edu.bnuz.steer.TeacherTimeslotCommand

class TeacherTimeslotController {
    ScheduleService scheduleService
    TermService termService
    SecurityService securityService
    TimeslotService timeslotService
    ObserverSettingService observerSettingService
    ObservationCriteriaService observationCriteriaService

    def index(String teacherId) {
        def term = termService.activeTerm
        def schedules = scheduleService.getTeacherSchedules(teacherId, term.id)
        scheduleService.observationPermission(schedules)
        renderJson([schedules: schedules])
    }

    def show(String teacherId, Long id) {
        Integer week = params.getInt('week') ?: 0
        TeacherTimeslotCommand cmd = new TeacherTimeslotCommand(
            termId      : termService.activeTerm.id,
            teacherId   : teacherId,
            week        : week,
            timeslot    : id,
        )
        def term = termService.activeTerm
        def isAdmin = securityService.hasRole("ROLE_OBSERVATION_ADMIN")
        def type = isAdmin ? [1,2,3]:observerSettingService.findRolesByUserIdAndTerm(securityService.userId,term.id)
        def timeslot = timeslotService.timeslot(cmd)
        ObservationForm form = null
        if (!isAdmin && timeslot) {
             form = ObservationForm.findByObserverAndTeacherAndLectureWeekAndDayOfWeekAndPlaceAndStartSection(
                            Teacher.load(securityService.userId),
                            Teacher.load(teacherId),
                            cmd.week,
                            cmd.dayOfWeek,
                            timeslot[0].place as String,
                            cmd.startSection
                    )
        }

        renderJson([
            term : [
                startWeek: term.startWeek,
                maxWeek: term.maxWeek,
                currentWeek: term.currentWorkWeek,
                startDate: term.startDate,
                swapDates: term.swapDates,
                endWeek: term.endWeek,
            ],
            timeslot: timeslot,
            types: type,
            evaluationSystem: observationCriteriaService.observationCriteria,
            isAdmin: isAdmin,
            observers: isAdmin ? observerSettingService.findCurrentObservers(term.id) : null,
            formId: form?.id
        ])
    }
}
