package cn.edu.bnuz.steer

class TeacherController {
    TeacherService teacherService

    def index() {
        String query = params.q
        renderJson teacherService.find(query)
    }
}
