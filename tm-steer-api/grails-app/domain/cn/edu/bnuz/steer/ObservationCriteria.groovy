package cn.edu.bnuz.steer

class ObservationCriteria {

    String name
    String description
    Boolean activeted

    static hasMany = [items:ObservationCriteriaItem]

    static mapping = {
        comment        '评分体系'
        id             generator: 'identity', comment: 'ID'
        name           comment: '名称'
        description    comment: '描述'
        activeted      comment: '是否活动'
    }

    static constraints = {
        description nullable: true
        activeted nullable:  true
    }
}
