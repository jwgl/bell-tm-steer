package cn.edu.bnuz.steer

import cn.edu.bnuz.bell.master.TermService


class PlaceTimeslotController {
    ScheduleService scheduleService
    TermService termService
    def index() {
        String q = params.q
        renderJson(scheduleService.findPlace(null, q))
    }

    def show(String id){
        println id
        Integer weekOfTerm = params.getInt('weekOfTerm')?:0
        def term =termService.activeTerm
        renderJson(scheduleService.getPlaceSchedules(id,term.id,weekOfTerm))
    }
}
