package cn.edu.bnuz.bell.steer

class ObservationView {
    Long    id
    String  teacherId
    String  teacherName
    String  supervisorId
    String  supervisorName
    Integer status
    Date    recordDate
    Integer lectureWeek
    String  supervisorDate
    Integer dayOfWeek
    Integer startSection
    Integer totalSection
    Integer formTotalSection
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
    Integer observerType
    String  courseClassName
    String  departmentName
    String  courseName
    Integer termId

    static mapping = {
        table 'dv_observation_view'
    }
}
