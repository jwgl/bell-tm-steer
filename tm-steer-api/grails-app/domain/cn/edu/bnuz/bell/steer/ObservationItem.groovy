package cn.edu.bnuz.bell.steer

class ObservationItem {
    ObservationCriteriaItem observationCriteriaItem
    Integer value

    static belongsTo = [observationForm: ObservationForm]

    static mapping = {
        comment                 '评分'
        id                      generator: 'identity', comment: 'ID'
        value                   comment: '分值'
        observationCriteriaItem comment: '参考评分细则'
        observationForm         comment: '所属听课记录'
    }

    static constraints = {
        value   nullable: true
    }
}
