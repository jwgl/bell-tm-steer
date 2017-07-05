package cn.edu.bnuz.steer

import grails.gorm.transactions.Transactional

@Transactional
class ObservationCriteriaService {

    def getObservationCriteria() {
        def result = ObservationCriteria.executeQuery'''
select new map(
item.id as id,
item.title as title,
item.name as name
)
from ObservationCriteria es join es.items item
where es.activeted is true
'''
        return result.groupBy {it.title}.entrySet()
    }

    def getObservationCriteriaById(Long id) {
        def result = ObservationCriteria.executeQuery'''
select new map(
item.id as id,
item.title as title,
item.name as name
)
from ObservationCriteria es join es.items item
where es.id = :id
''',[id: id]
        return result.groupBy {it.title}.entrySet()
    }
}
