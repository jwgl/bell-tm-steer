package cn.edu.bnuz.bell.steer

import cn.edu.bnuz.bell.steer.ObservationLegacyForm
import grails.gorm.transactions.Transactional

@Transactional
class LegacyDataService {

    def getTerms() {
        ObservationLegacyForm.executeQuery'''
select DISTINCT l.termId
from ObservationLegacyForm l
order by l.termId desc
'''
    }

    def list(Integer termId) {
        ObservationLegacyForm.executeQuery'''
select DISTINCT l
from ObservationLegacyForm l
where l.termId=:termId
order by l.listentime desc
''',[termId: termId]
    }

    def types(Integer termId) {
        ObservationLegacyForm.executeQuery'''
select l.observerType
from ObservationLegacyForm l
where l.termId=:termId
group by l.observerType
''',[termId: termId]
    }
}
