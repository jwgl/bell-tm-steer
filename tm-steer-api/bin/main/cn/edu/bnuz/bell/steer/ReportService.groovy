package cn.edu.bnuz.bell.steer

import cn.edu.bnuz.bell.master.TermService
import cn.edu.bnuz.bell.organization.Teacher
import grails.gorm.transactions.Transactional

@Transactional
class ReportService {
    TermService termService
    ObserverSettingService observerSettingService

    def groupByDepartment(Integer type) {
        def term = termService.activeTerm
        def result = ObservationView.executeQuery '''
select new map(
  view.departmentName as departmentName,
  sum(case view.status when 1 then 1 else 0 end) as unApproved,
  count(*) as supervisorTimes,
  sum(view.formTotalSection) as totalSection
)
from ObservationView view
where view.termId = :termId
 and view.status > 0
 and view.observerType = :type
group by view.departmentName
''', [termId: term.id, type: type]
        return [
                isAdmin:observerSettingService.isAdmin(),
                list: result,
        ]

    }

    def countByObserver() {
        def term = termService.activeTerm
        def result = ObservationForm.executeQuery '''
select new map(
  observer.id as supervisorId,
  observer.name as supervisorName,
  department.name as departmentName,
  count(*) as supervisorTimes,
  sum(form.totalSection) as totalSection
)
from ObservationForm form
join form.observer observer
join observer.department department
where form.termId = :termId
 and form.status > 0
 and form.observerType = :type
group by observer.id, observer.name, department.name
''', [termId: term.id, type: 1]
        return [
                list: result,
        ]

    }

    def countByDeptObserver(String userId) {
        def term = termService.activeTerm
        def dept = Teacher.load(userId).department.name
        def result = ObservationForm.executeQuery '''
select new map(
  observer.id as supervisorId,
  observer.name as supervisorName,
  department.name as departmentName,
  count(*) as supervisorTimes,
  sum(form.totalSection) as totalSection
)
from ObservationForm form
join form.observer observer
join observer.department department
where form.termId = :termId
 and form.status > 0
 and form.observerType = :type
 and department.name like :dept
group by observer.id, observer.name, department.name
order by department.name
''', [termId: term.id, type: 2, dept: observerSettingService.isAdmin() ? "%" : dept]
        return [
                list: result,
        ]

    }

    def teacherActive(){
        def result = ObservationPriority.executeQuery'''
select new map(
ta.teacherId as teacherId,
ta.teacherName as teacherName,
ta.academicTitle as academicTitle,
ta.departmentName as departmentName,
ta.courseName as courseName,
ta.isnew as isnew
)
from ObservationPriority ta
where ta.hasSupervisor is null
order by ta.departmentName,teacherName
'''
        return [
                isAdmin:observerSettingService.isAdmin(),
                list: result,
        ]
    }

    def byTeacherForCollege(String userId) {
        def term = termService.activeTerm
        def dept = Teacher.load(userId).department.name
        ObservationView.executeQuery '''
select new map(
  view.teacherId as teacherId,
  view.teacherName as teacherName,
  view.departmentName as departmentName,
  count(*) as supervisorTimes,
  sum(view.formTotalSection) as totalSection
)
from ObservationView view
where view.termId = :termId
 and view.status > 0
 and view.observerType = :type
 and view.departmentName like :dept
group by view.teacherId, view.teacherName, view.departmentName
''', [termId: term.id, type: 2, dept: observerSettingService.isAdmin()? "%" : dept]

    }

    def byTeacherForUniversity(){
        ObservationCount.executeQuery '''
select new map(
    view.teacherId as teacherId,
    view.teacherName as teacherName,
    view.departmentName as departmentName,
    view.superviseCount as supervisorTimes
)
from ObservationCount view
'''
    }

}
