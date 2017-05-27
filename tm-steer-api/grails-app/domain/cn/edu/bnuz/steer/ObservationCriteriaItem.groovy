package cn.edu.bnuz.steer

class ObservationCriteriaItem {

    String title
    String name
    String description
    static belongsTo =[observationCriteria:ObservationCriteria]
    static mapping = {
        comment        '评分细则'
        id             generator: 'identity', comment: 'ID'
        title          comment: '标题'
        name           comment: '名称'
        description    comment: '描述'
    }
    static constraints = {
        description nullable: true
    }
}
