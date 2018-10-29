package cn.edu.bnuz.bell.steer

class ObservationCount {
    String teacherId
    String teacherName
    String  departmentName
    Integer superviseCount

    static mapping = {
        table 'dv_observation_count'
    }
}
