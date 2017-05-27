package cn.edu.bnuz.steer

/**
 * Created by jerry on 2017/3/29.
 */
class ObservationFormCommand {
    Long id
    String scheduleId
    String teacherId
    Integer supervisorWeek
    Integer totalSection
    String teachingMethods
    String supervisorDate
    Integer type
    String place
    Integer earlier
    Integer late
    Integer leave
    Integer dueStds
    Integer attendantStds
    Integer lateStds
    Integer leaveStds
    String evaluateLevel
    List<EvaluationsItem> evaluations
    String evaluationText
    String suggest
    Integer status
    String supervisorId

    class EvaluationsItem {
        Integer id
        Integer value
    }

    def tostring(){

    "${scheduleId},"+
    "${teacherId},"+
    "${supervisorWeek},"+
    "${totalSection},"+
    "${teachingMethods},"+
    "${supervisorDate},"+
    "${type},"+
    "${place},"
    "${earlier},"+
    "${late},"+
    "${leave},"+
    "${dueStds},"+
    "${evaluateLevel},"+
    "${evaluationText},"+
    "${suggest},"+
    "${status},"+
    "${supervisorId}"

    }
}
