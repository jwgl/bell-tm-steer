package cn.edu.bnuz.bell.steer

class ObservationPriority {
    String teacherId
    String teacherName
    String departmentName
    String academicTitle
    String courseName
    String isnew
    String hasSupervisor

    static mapping = {
        table 'dv_observation_priority'
    }
}
