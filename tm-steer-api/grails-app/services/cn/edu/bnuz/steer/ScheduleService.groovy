package cn.edu.bnuz.steer

import cn.edu.bnuz.bell.operation.TaskSchedule
import cn.edu.bnuz.bell.place.Place
import cn.edu.bnuz.bell.master.TermService
import cn.edu.bnuz.bell.organization.DepartmentService
import grails.gorm.transactions.Transactional

import java.time.LocalDate

@Transactional
class ScheduleService {

    TermService termService
    DepartmentService departmentService
    ObserverSettingService observerSettingService

    def getFormForCreate(String userId) {
        def term = termService.activeTerm
        return [
                term        : [
                        startWeek  : term.startWeek,
                        maxWeek    : term.maxWeek,
                        currentWeek: term.currentWorkWeek,
                        startDate  : term.startDate,
                        swapDates  : term.swapDates,
                        endWeek    : term.endWeek,
                ],
                departments : departmentService.teachingDepartments,
                sections    : Section.findAll(),
                today       : LocalDate.now(),
                buildings   : getBuildings(),
        ]
    }

    private getBuildings() {
        Place.executeQuery'''
select distinct p.building from Place p where p.enabled = true and p.isExternal=false
'''
    }

    def findPlace(String building, String placeName){
        Place.executeQuery'''
select new map(
    p.id as id,
    p.name as name,
    p.building as building,
    p.seat as seat,
    p.type as type
)
 from Place p where p.enabled = true and p.isExternal=false
  and p.building like :building and p.name like :query
''',[building: building == null ? "%" : building,
     query: "%${placeName}%"],[max: 10]
    }
    List getTeacherSchedules(String userId, SheduleOptionsCommand cmd) {
        def term = termService.activeTerm
        def result = TaskSchedule.executeQuery '''
select new map(
  schedule.id as id,
  department.name as department,
  scheduleTeacher.academicTitle as academicTitle,
  courseClass.name as courseClassName,
  scheduleTeacher.id as teacherId,
  scheduleTeacher.name as teacherName,
  schedule.startWeek as startWeek,
  schedule.endWeek as endWeek,
  schedule.oddEven as oddEven,
  schedule.dayOfWeek as dayOfWeek,
  schedule.startSection as startSection,
  schedule.totalSection as totalSection,
  course.name as course,
  course.credit as credit,
  property.name as property,
  courseItem.name as courseItem,
  place.name as place,
  (select superviseCount from ObservationCount where teacherId = scheduleTeacher.id) as superviseCount
)
from TaskSchedule schedule
join schedule.task task
join task.courseClass courseClass
join courseClass.course course
join courseClass.teacher courseTeacher
join courseClass.department department
join schedule.teacher scheduleTeacher
join course.property property
left join task.courseItem courseItem
left join schedule.place place
where courseClass.term.id = :termId
  and scheduleTeacher.id like :teacherId
  and place.name like :place
  and department.id like :department
  and :weekOfTerm >= schedule.startWeek
  and :weekOfTerm <= schedule.endWeek
  and ((schedule.oddEven = 0) or (schedule.oddEven = :weekOfTerm % 2))
''', [ termId: term.id,
       teacherId: cmd.teacherId == 'null' ? '%' : cmd.teacherId,
       place: cmd.place == 'null' ? '%' : "${cmd.place}%",
       department: cmd.departmentId == 'null' ? '%' : cmd.departmentId,
       weekOfTerm: cmd.weekOfTerm]
//      过滤不在时段、不在星期几的课
        def a= []
        for (i in cmd.startSection .. cmd.endSection) {
            a += [i]
        }

        result.grep{
            cmd.dayOfWeek == 0 ? true : (it.dayOfWeek == cmd.dayOfWeek)
        }.grep{item->
            def list = []
            for (i in item.startSection .. item.startSection + item.totalSection - 1) {
                list += [i]
            }
            return a - list != a
        }

    }


    List getPlaceSchedules(String placeId, Integer termId) {
        TaskSchedule.executeQuery '''
select new map(
  schedule.id as id,
  courseClass.name as courseClassName,
  scheduleTeacher.id as teacherId,
  scheduleTeacher.name as teacherName,
  scheduleTeacher.academicTitle as academicTitle,
  department.name as department,
  schedule.startWeek as startWeek,
  schedule.endWeek as endWeek,
  schedule.oddEven as oddEven,
  schedule.dayOfWeek as dayOfWeek,
  schedule.startSection as startSection,
  schedule.totalSection as totalSection,
  course.name as course,
  place.name as place,
  (select superviseCount from ObservationCount where teacherId = scheduleTeacher.id) as superviseCount
)
from TaskSchedule schedule
join schedule.task task
join task.courseClass courseClass
join courseClass.course course
join courseClass.department department
join schedule.teacher scheduleTeacher
left join schedule.place place
where place.id = :placeId
  and courseClass.term.id = :termId
''', [placeId: placeId, termId: termId]
    }

    List getTeacherSchedules(String teacherId, Integer termId) {
        TaskSchedule.executeQuery '''
select new map(
  schedule.id as id,
  courseClass.name as courseClassName,
  scheduleTeacher.id as teacherId,
  scheduleTeacher.name as teacherName,
  scheduleTeacher.academicTitle as academicTitle,
  department.name as department,
  schedule.startWeek as startWeek,
  schedule.endWeek as endWeek,
  schedule.oddEven as oddEven,
  schedule.dayOfWeek as dayOfWeek,
  schedule.startSection as startSection,
  schedule.totalSection as totalSection,
  course.name as course,
  place.name as place,
  courseItem.name as courseItem,
  (select superviseCount from ObservationCount where teacherId = scheduleTeacher.id) as superviseCount
)
from TaskSchedule schedule
join schedule.task task
join task.courseClass courseClass
join courseClass.course course
join courseClass.department department
join schedule.teacher scheduleTeacher
left join schedule.place place
left join task.courseItem courseItem
where scheduleTeacher.id = :teacherId
  and courseClass.term.id = :termId
''', [teacherId: teacherId, termId: termId]
    }

    boolean isCollegeSupervisor(String userId, Integer termId) {
        return observerSettingService.isCollegeSupervisor(userId, termId)
    }


}
