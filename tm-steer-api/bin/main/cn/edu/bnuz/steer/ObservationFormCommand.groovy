package cn.edu.bnuz.steer

/**
 * Created by jerry on 2017/3/29.
 */
class ObservationFormCommand {
    Long                    id
    String                  teacherId
    Integer                 observationWeek
    Integer                 totalSection
    String                  teachingMethods
    String                  supervisorDate
    Integer                 observerType
    String                  place
    Integer                 earlier
    Integer                 late
    Integer                 leave
    Integer                 dueStds
    Integer                 attendantStds
    Integer                 lateStds
    Integer                 leaveStds
    BigDecimal              evaluateLevel
    List<EvaluationsItem>   evaluations
    String                  evaluationText
    String                  suggest
    Integer                 status
    String                  observerId
    Integer                 dayOfWeek
    Integer                 startSection
    Boolean                 isScheduleTemp

    class EvaluationsItem {
        Integer id
        Integer value
    }

    def tostring(){

    "${teacherId},"+
    "${observationWeek},"+
    "${totalSection},"+
    "${teachingMethods},"+
    "${supervisorDate},"+
    "${observerType},"+
    "${place},"
    "${earlier},"+
    "${late},"+
    "${leave},"+
    "${dueStds},"+
    "${evaluateLevel},"+
    "${evaluationText},"+
    "${suggest},"+
    "${status},"+
    "${observerId},"+
    "${dayOfWeek},"+
    "${startSection}"

    }
}
