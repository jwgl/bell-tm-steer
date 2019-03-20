package cn.edu.bnuz.bell.steer

import cn.edu.bnuz.bell.master.TermService
import cn.edu.bnuz.bell.operation.Task
import cn.edu.bnuz.bell.organization.Teacher
import cn.edu.bnuz.bell.security.SecurityService
import cn.edu.bnuz.steer.ScheduleTempCommand
import grails.gorm.transactions.Transactional

@Transactional
class TaskScheduleTempFormService {
    TermService termService
    SecurityService securityService
    ObserverSettingService observerSettingService
    ObservationCriteriaService observationCriteriaService

    def list(String teacherId) {
        def term = termService.activeTerm
        TaskScheduleTemp.executeQuery '''
select new map(
  schedule.id as id,
  courseClass.name as courseClassName,
  scheduleTeacher.id as teacherId,
  scheduleTeacher.name as teacherName,
  scheduleTeacher.academicTitle as academicTitle,
  department.name as department,
  department.id as departmentId,
  schedule.startWeek as startWeek,
  schedule.endWeek as endWeek,
  schedule.oddEven as oddEven,
  schedule.dayOfWeek as dayOfWeek,
  schedule.startSection as startSection,
  schedule.totalSection as totalSection,
  course.name as course,
  schedule.place as place,
  courseItem.name as courseItem
)
from TaskScheduleTemp schedule
join schedule.task task
join task.courseClass courseClass
join courseClass.course course
join courseClass.department department
join schedule.teacher scheduleTeacher
left join task.courseItem courseItem
where scheduleTeacher.id = :teacherId
  and courseClass.term.id = :termId
''', [teacherId: teacherId, termId: term.id]
    }

    TaskScheduleTemp create(ScheduleTempCommand cmd) {
        // 避免重复录入
        def form = TaskScheduleTemp.findByTeacherAndStartWeekAndDayOfWeekAndStartSection(
                Teacher.load(cmd.teacherId),
                cmd.startWeek,
                cmd.dayOfWeek,
                cmd.startSection
        )
        if (!form) {
            def now = new Date()
            form = new TaskScheduleTemp(
                    endWeek: cmd.endWeek,
                    oddEven: 0,
                    startWeek: cmd.startWeek,
                    dayOfWeek: cmd.dayOfWeek,
                    startSection: cmd.startSection,
                    totalSection: cmd.totalSection,
                    place: cmd.place,
                    teacher: Teacher.load(cmd.teacherId),
                    task: Task.load(cmd.taskId),
                    creator: Teacher.load(securityService.userId),
                    dateCreated: now
            )
            form.save()
        }
        return form
    }

    def getFormForEdit(Long id) {
        TaskScheduleTemp.get(id)
    }

    def update(ScheduleTempCommand cmd) {
        def form = TaskScheduleTemp.get(cmd.id)
        if (form && form.creator.id == securityService.userId) {
            form.setDayOfWeek(cmd.dayOfWeek)
            form.setStartSection(cmd.startSection)
            form.setTotalSection(cmd.totalSection)
            form.setStartWeek(cmd.startWeek)
            form.setEndWeek(cmd.endWeek)
            form.setPlace(cmd.place)
            if (!form.save()) {
                form.errors.each {
                    println it
                }
            }
        }
    }

    def getFormForCreate(UUID id, String teacherId) {
        def term = termService.activeTerm
        def type = observerSettingService.findRolesByUserIdAndTerm(securityService.userId,term.id)
        def timeslot = Task.executeQuery'''
select distinct new map(
  task.id as taskId,
  department.name as department,
  teacher.academicTitle as academicTitle,
  courseClass.name as courseClassName,
  teacher.id as teacherId,
  teacher.name as teacherName,
  course.name as course,
  course.credit as credit,
  (select count(*) from TaskStudent tst where tst.task = task) as studentCount
)
from Task task
join task.courseClass courseClass
join courseClass.course course
join courseClass.department department
join task.teachers tt
join tt.teacher teacher
where task.id = :id and teacher.id = :teacherId
''', [id: id, teacherId: teacherId]
        return [
            term : [
                startWeek  : term.startWeek,
                maxWeek    : term.maxWeek,
                currentWeek: term.currentWorkWeek,
                startDate  : term.startDate,
                swapDates  : term.swapDates,
                endWeek    : term.endWeek,
            ],
            timeslot            : timeslot,
            types               : type,
            sections            : Section.findAll(),
            evaluationSystem    : observationCriteriaService.observationCriteria,
        ]
    }

    def findTask(String teacherId) {
        def term = termService.activeTerm
        Task.executeQuery'''
select new map(
    task.id as id,
    cc.code as code,
    cc.name as className,
    course.name as courseName,
    course.credit as credit,
    teacher.name as teacherName,
    count(*) as studentCount
)
from Task task
join task.courseClass cc
join cc.course course
join task.teachers tt
join tt.teacher teacher
join task.students student
where cc.term.id = :term 
and teacher.id = :teacherId 
and course.isPractical is true
and course.scheduleType = 0
group by task.id, cc.code, cc.name, course.name, course.credit, teacher.name
''', [term: term.id, teacherId: teacherId]
    }
}
