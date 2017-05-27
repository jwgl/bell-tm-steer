package cn.edu.bnuz.steer

import grails.transaction.Transactional

@Transactional
class LegacyDataService {

    def getTerms(){
        ObservationLegacyForm.executeQuery'''
select DISTINCT l.termId
from ObservationLegacyForm l
order by l.termId desc
'''
    }

    def list(Integer termId){
        ObservationLegacyForm.executeQuery'''
select DISTINCT l
from ObservationLegacyForm l
where l.termId=:termId
order by l.listentime desc
''',[termId: termId]
    }

    def types(Integer termId){
        ObservationLegacyForm.executeQuery'''
select l.type
from ObservationLegacyForm l
where l.termId=:termId
group by l.type
''',[termId: termId]
    }
}
