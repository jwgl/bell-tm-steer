package cn.edu.bnuz.steer

class ObservationPublic {
    Long        id
    Boolean     isLegacy
    String      teacherId
    String      teacherName
    String      supervisorDate
    String      evaluateLevel
    Integer     observerType
    Integer     termId
    String      departmentName
    String      courseName
    String      placeName
    Integer     dayOfWeek
    Integer     startSection
    Integer     totalSection

    static mapping = {
        table 'dv_observation_public'
    }
}
