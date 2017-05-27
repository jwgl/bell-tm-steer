package cn.edu.bnuz.steer

/**
 * Created by jerry on 2017/3/20.
 */
class SheduleOptionsCommand {
    String teacherId
    String place
    String departmentId
    Integer weekOfTerm
    Integer dayOfWeek
    Integer startSection
    Integer endSection
    def tostring(){
        return "teacherId:${teacherId},place:${place},departmentId:${departmentId},weekOfTerm:${weekOfTerm},dayOfWeek:${dayOfWeek},startSection:${startSection},endSection:${endSection};"
    }

}
