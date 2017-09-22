package cn.edu.bnuz.steer

import cn.edu.bnuz.bell.operation.TaskSchedule
import grails.gorm.transactions.Transactional

@Transactional
class TimeslotService {

    def timeslot(TeacherTimeslotCommand cmd) {
        TaskSchedule.executeQuery '''
select distinct new map(
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
  cp.propertyName as property,
  place.name as place,
  (select count(*) from TaskStudent tst where tst.task = task) as studentCount
)
from TaskSchedule schedule
join schedule.task task
join task.courseClass courseClass
join courseClass.course course
join courseClass.teacher courseTeacher
join courseClass.department department
join schedule.teacher scheduleTeacher
left join schedule.place place, CourseClassProperty cp
where courseClass.term.id = :termId
  and schedule.teacher.id = :teacherId
  and :week between schedule.startWeek and schedule.endWeek
  and (schedule.oddEven = 0
   or schedule.oddEven = 1 and :week % 2 = 1
   or schedule.oddEven = 2 and :week % 2 = 0)
  and schedule.dayOfWeek = :dayOfWeek
  and schedule.startSection = :startSection
  and courseClass.id=cp.id
''', cmd as Map
    }
}
