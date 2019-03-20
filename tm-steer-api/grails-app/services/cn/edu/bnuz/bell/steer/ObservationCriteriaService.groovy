package cn.edu.bnuz.bell.steer

import grails.gorm.transactions.Transactional

@Transactional
class ObservationCriteriaService {

    def getObservationCriteria() {
        def result = ObservationCriteria.executeQuery'''
select new map(
item.id as id,
item.title as title,
item.method as method,
item.name as name
)
from ObservationCriteria es join es.items item
where es.activeted is true
'''
        def groupby = result.groupBy {it.method}.collectEntries {k, v ->
            [(k): v.groupBy {
                it.title
            }.entrySet()]
        }
        return groupby
    }

    def getObservationCriteriaByIdAndMethod(Long id, Integer method) {
        def result = ObservationCriteria.executeQuery'''
select new map(
item.id as id,
item.title as title,
item.method as method,
item.name as name
)
from ObservationCriteria es join es.items item
where es.id = :id and item.method = :method
''',[id: id, method: method]
        return result.groupBy {it.title}.entrySet()
    }
}
