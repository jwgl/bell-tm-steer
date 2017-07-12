package cn.edu.bnuz.steer

import cn.edu.bnuz.bell.http.ForbiddenException
import cn.edu.bnuz.bell.master.TermService
import grails.gorm.transactions.Transactional

@Transactional
class RewardService {
    TermService termService
    ObserverSettingService observerSettingService
    def list(String month) {
        def term = termService.activeTerm
        if (!observerSettingService.isAdmin()) {
            throw new ForbiddenException()
        }
        ObservationView.executeQuery '''
select new map(
  view.id as id,
  view.supervisorDate as supervisorDate,
  view.evaluateLevel as evaluateLevel,
  view.status as status,
  view.supervisorId as supervisorId,
  view.supervisorName as supervisorName,
  view.observerType as observerType,
  view.courseClassName as courseClassName,
  view.departmentName as departmentName,
  view.teacherId as teacherId,
  view.teacherName as teacherName,
  view.dayOfWeek as dayOfWeek,
  view.startSection as startSection,
  view.totalSection as totalSection,
  view.courseName as course,
  view.placeName as place,
  view.termId as termId,
  view.formTotalSection as observesCount
)
from ObservationView view
where view.termId = :termId
and view.status>0
and view.supervisorDate like :date
and view.observerType = :type
and view.rewardDate is null
order by view.supervisorDate
''', [ termId: term.id, date:"%-${month}-%", type:1]
    }

    def getMonthes() {
        def term = termService.activeTerm
        ObservationForm.executeQuery '''
select distinct substring(form.supervisorDate,6,2)
from ObservationForm form
where form.termId = :termId
order by substring(form.supervisorDate,6,2)
''', [ termId: term.id]
    }

    def done(String month) {
        def term = termService.activeTerm
        if (!observerSettingService.isAdmin()) {
            throw new ForbiddenException()
        }
        ObservationForm.executeUpdate'''
update ObservationForm f
set f.rewardDate = now()
where id in(
select v.id from ObservationView v
where v.termId = :termId and v.status > 0
and substring(v.supervisorDate,6,2) = :month
and v.observerType = 1
)
''',[termId: term.id, month: month]
    }
}
