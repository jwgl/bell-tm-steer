package cn.edu.bnuz.steer

import cn.edu.bnuz.bell.master.TermService


class PlaceTimeslotController {
    ScheduleService scheduleService
    TermService termService
    def index() {
        String building = params.building
        String q = params.q
        renderJson(scheduleService.findPlace(building, q))
    }

    def show(String id){
        def term =termService.activeTerm
        renderJson(scheduleService.getPlaceSchedules(id, term.id))
    }
}
