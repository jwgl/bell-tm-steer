package cn.edu.bnuz.bell.steer

import cn.edu.bnuz.bell.operation.Task
import cn.edu.bnuz.bell.organization.Teacher

/**
 * 临时教学安排
 * @author ZQ
 */
class TaskScheduleTemp {

    UUID id
    /**
     * 开始周
     */
    Integer startWeek

    /**
     * 结束周
     */
    Integer endWeek

    /**
     * 单双周-0:全部;1:单周;2:双周
     */
    Integer oddEven

    /**
     * 星期几-1:星期一;...;7:星期日
     */
    Integer dayOfWeek

    /**
     * 开始节
     */
    Integer startSection

    /**
     * 上课长度
     */
    Integer totalSection

    /**
     * 场地
     */
    String place

    /**
     * 教师
     */
    Teacher teacher

    /**
     * 录入人
     */
    Teacher creator

    /**
     * 录入时间
     */
    Date dateCreated

    static belongsTo = [task: Task]

    static mapping = {
        comment      '教学安排'
        table        schema: 'tm'
        id           generator: 'uuid2', type:'pg-uuid', comment: '教学安排ID'
        endWeek      comment: '结束周'
        oddEven      comment: '单双周-0:全部;1:单周;2:双周'
        startWeek    comment: '开始周'
        dayOfWeek    comment: '星期几-1:星期一;...;7:星期日'
        startSection comment: '开始节'
        totalSection comment: '上课长度'
        place        length: 50,    comment: '上课地点'
        teacher      index: 'task_schedule_teacher_idx', comment: '教师'
        task         comment: '所属教学任务'
        creator      comment: '录入人'
        dateCreated  comment: '录入时间'
    }
}
