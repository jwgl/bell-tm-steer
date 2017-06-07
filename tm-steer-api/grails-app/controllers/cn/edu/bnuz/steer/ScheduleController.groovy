package cn.edu.bnuz.steer

import cn.edu.bnuz.bell.master.TermService
import cn.edu.bnuz.bell.security.SecurityService
import org.springframework.security.access.prepost.PreAuthorize
import cn.edu.bnuz.bell.organization.Teacher

/**
 * 课表
 */
@PreAuthorize('hasAuthority("PERM_OBSERVATION_WRITE")')
class ScheduleController {
    ScheduleService scheduleService
    TermService termService
    ObservationFormService observationFormService
    ReportService reportService
    SecurityService securityService

    def show(String id) {
        renderJson(scheduleService.getSchedule(securityService.userId, id))
    }

    def create() {
        renderJson(scheduleService.getFormForCreate(securityService.userId))
    }

    def save(String userId) {
        def cmd = new ObservationFormCommand()
        bindData(cmd, request.JSON)
        println cmd
        def form = observationFormService.create(userId, cmd)
        renderJson([id: form.id])
    }

    def update(String userId, Long id) {
        def cmd = new ObservationFormCommand()
        bindData(cmd, request.JSON)
        cmd.id = id
        observationFormService.update(userId, cmd)
        renderOk()
    }

    def findPlace(){
        String building = params.building
        println building
        String q = params.q
        renderJson(scheduleService.findPlace(building, q))
    }

    def findSchedule(){
        String type = params.type
        switch (type){
            case "multicriteria":  multiCriteria(); break;
            case "place":          findPlaceSchedule(); break;
            case "teacher":        findTeacherSchedule(); break;
        }
    }

    def multiCriteria(){
        SheduleOptionsCommand cmd = new SheduleOptionsCommand()
        cmd.teacherId = params['teacherId']
        cmd.place = params['place']
        cmd.departmentId = params['departmentId']
        cmd.weekOfTerm = params.getInt('weekOfTerm')?:0
        cmd.dayOfWeek = params.getInt('dayOfWeek')?:0
        cmd.startSection = params.getInt('startSection')
        cmd.endSection = params.getInt('endSection')
        println cmd.tostring()
        def schedule = scheduleService.getTeacherSchedules(securityService.userId, cmd)
        renderJson(schedule)
    }

    def isCurrentSupervisor(String userId){
        def term =termService.activeTerm
        def result = Observer.findByTermIdAndTeacher(term.id, Teacher.load(userId)) !=null
        renderJson([result:result])
    }

//    def findTeacherSchedule(){
//        String teacherId = params['teacherId']
//        Integer weekOfTerm = params.getInt('weekOfTerm')?:0
//        println(weekOfTerm)
//        def term =termService.activeTerm
//        def schedules = scheduleService.getTeacherSchedules(teacherId, term.id)
//        renderJson([schedules: schedules.grep{
//            it.startWeek <=weekOfTerm && it.endWeek >=weekOfTerm
//        }])
//    }

    def findPlaceSchedule(){
        String place = params['place']
        println place
        Integer weekOfTerm = params.getInt('weekOfTerm')?:0
        def term =termService.activeTerm
        renderJson(scheduleService.getPlaceSchedules(place,term.id,weekOfTerm))
    }

    def getTerm(){
        def term =termService.activeTerm
        renderJson([
                    startWeek  : term.startWeek,
                    maxWeek    : term.maxWeek,
                    currentWeek: term.currentWorkWeek,
                    startDate  : term.startDate,
                    swapDates  : term.swapDates,
                    endWeek    : term.endWeek,
                    ])
    }

    def teacherActiveList(String userId){
        def term =termService.activeTerm
        if(scheduleService.isCollegeSupervisor(userId,term.id))
            renderBadRequest()
        else renderJson(reportService.teacherActive(userId))
    }
}
