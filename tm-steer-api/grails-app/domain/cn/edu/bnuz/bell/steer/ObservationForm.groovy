package cn.edu.bnuz.bell.steer

import cn.edu.bnuz.bell.organization.Teacher

class ObservationForm {

    Teacher teacher
    Teacher observer
    Integer observerType
    /**
     * 记录状态：0-未提交；1-提交；2-发布；
     */
    Integer status
    Date    recordDate
    /**
     * timeslot:周、星期几、开始节
     */
    Integer lectureWeek
    Integer dayOfWeek
    Integer startSection
    String  supervisorDate
    Integer totalSection
    String  evaluationText
    String  teachingMethods
    /**
     * 教学形式类别，与评分体系共同决定当前采用的评分细则
     */
    Integer method
    String  suggest
    BigDecimal   evaluateLevel
    Date    rewardDate
    String  place
    Integer earlier
    Integer late
    Integer leave
    Integer dueStds
    Integer lateStds
    Integer attendantStds
    Integer leaveStds
    String  operator
    Integer termId
    String  updateOperator
    Date    updateDate
    ObservationCriteria     observationCriteria
    Boolean isScheduleTemp
    /**
     * 推荐类型：1典型事例，2观摩课
     */
    Integer recommend
    /**
     * 推荐理由
     */
    String recommendReason
    /**
     * 教学环境
     */
    String teachingEnvironment

    static hasMany = [observationItem:ObservationItem]

    static mapping = {
        comment             '听课记录'
        id                  generator: 'identity', comment: 'ID'
        teacher             comment: '上课老师'
        observer            comment: '听课老师'
        observerType        comment: '督导类型'
        status              defaultValue: "0",comment: '状态'
        recordDate          comment: '录入日期'
        lectureWeek         comment:'上课周'
        dayOfWeek           comment:'星期几'
        startSection        comment:'起始节'
        supervisorDate      comment:'听课日期'
        teachingMethods     comment:'教学形式'
        totalSection        comment: '听课节数'
        evaluationText      length: 1500,    comment: '评价'
        suggest             length: 1500,    comment: '建议'
        evaluateLevel       precision: 3, scale: 1, comment: '评定等级'
        place               length: 50,      comment: '上课地点'
        earlier             defaultValue: "0",comment: '提前（分钟）'
        late                defaultValue: "0",comment: '迟到（分钟）'
        leave               defaultValue: "0",comment: '迟到（分钟）'
        dueStds             defaultValue: "0",comment: '应到人数'
        lateStds            defaultValue: "0",comment: '迟到人数'
        attendantStds       defaultValue: "0",comment: '实到人数'
        leaveStds           defaultValue: "0",comment: '早退人数'
        operator            length: 10,      comment: '录入人'
        rewardDate          comment: '计酬日期'
        termId              comment: '冗余学期Id'
        updateOperator      comment: '最后修改人'
        updateDate          comment: '最后修改日期'
        observationCriteria comment: '采用评分体系'
        isScheduleTemp      comment: '是否无时间地点排课'
        method              comment: '教学形式类别'
        recommend           comment: '推荐类型'
        recommendReason     type: 'text', comment: '推荐原因'
        teachingEnvironment type: 'text', comment: '教学环境设备情况及建议'
    }

    static constraints = {
        evaluationText      nullable: true
        suggest             nullable: true
        evaluateLevel       nullable: true
        supervisorDate      nullable: true
        earlier             nullable: true
        late                nullable: true
        leave               nullable: true
        dueStds             nullable: true
        attendantStds       nullable: true
        lateStds            nullable: true
        leaveStds           nullable: true
        teachingMethods     nullable: true
        status              nullable: true
        operator            nullable: true
        rewardDate          nullable: true
        termId              nullable: true
        updateDate          nullable: true
        updateOperator      nullable: true
        observationCriteria nullable: true
        dayOfWeek           nullable: true
        startSection        nullable: true
        isScheduleTemp      nullable: true
        method              nullable: true
        recommend           nullable: true
        recommendReason     nullable: true
        teachingEnvironment nullable: true
    }

    def methodLable() {
        def labels = [1: '教师讲授', 2: '学生讨论汇报', 3: '教师讲授+学生讨论汇报', 4: '其他']
        return labels[this.method]
    }
}
