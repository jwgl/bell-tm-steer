package cn.edu.bnuz.steer

class ObservationView {
    String teacherId
    String teacherName
    String supervisorId
    String supervisorName
    Integer status
    Date    recordDate
    Integer lectureWeek
    String  supervisorDate
    Integer totalSection
    String  evaluationText
    String  teachingMethods
    String  suggest
    String  evaluateLevel
    Date    rewardDate
    String  placeName
    Integer earlier
    Integer late
    Integer leave
    Integer dueStds
    Integer lateStds
    Integer attendantStds
    Integer leaveStds
    String  operator
    String  typeName
    String  courseClassName
    String  departmentName
    Integer termid


    static mapping = {
        table 'dv_observation_view'
    }
}
