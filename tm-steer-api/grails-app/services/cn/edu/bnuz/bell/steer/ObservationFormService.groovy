package cn.edu.bnuz.bell.steer

import cn.edu.bnuz.bell.http.BadRequestException
import cn.edu.bnuz.bell.http.ForbiddenException
import cn.edu.bnuz.bell.http.NotFoundException
import cn.edu.bnuz.bell.master.TermService
import cn.edu.bnuz.bell.organization.Teacher
import cn.edu.bnuz.bell.security.SecurityService
import cn.edu.bnuz.bell.security.UserLogService
import cn.edu.bnuz.steer.ObservationFormCommand
import cn.edu.bnuz.steer.TeacherTimeslotCommand
import grails.converters.JSON
import grails.gorm.transactions.Transactional

@Transactional
class ObservationFormService {
    TermService termService
    TimeslotService timeslotService
    ObserverSettingService observerSettingService
    ObservationCriteriaService observationCriteriaService
    UserLogService userLogService
    SecurityService securityService

    ObservationForm create(String userId, ObservationFormCommand cmd) {
        def isAdmin = observerSettingService.isAdmin()

        // 暂时限定督导组组长只录入自己的
        if (securityService.hasRole("ROLE_OBSERVER_CAPTAIN")) {
            cmd.observerId = securityService.userId
        }
        if (isAdmin && !cmd.observerId ) {
            throw new BadRequestException("没有指定督导！")
        }
        //防止重复录入
        ObservationForm form =
            ObservationForm.findByObserverAndTeacherAndSupervisorDateAndPlaceAndStartSection(
                Teacher.load(isAdmin ? cmd.observerId : securityService.userId),
                Teacher.load(cmd.teacherId),
                cmd.supervisorDate,
                cmd.place,
                cmd.startSection
            )
        if (form) {
            throw new BadRequestException("重复录入！")
        }
        def now = new Date()
        form = new ObservationForm(
            observer: isAdmin ? Teacher.load(cmd.observerId) : Teacher.load(securityService.userId),
            teacher: Teacher.load(cmd.teacherId),
            dayOfWeek: cmd.dayOfWeek,
            startSection: cmd.startSection,
            lectureWeek: cmd.observationWeek,
            totalSection: cmd.totalSection,
            teachingMethods: cmd.teachingMethods,
            method: cmd.method,
            recommend: cmd.recommend,
            recommendReason: cmd.recommendReason,
            teachingEnvironment: cmd.teachingEnvironment,
            supervisorDate: cmd.supervisorDate,
            recordDate: now,
            observerType: cmd.observerType,
            place: cmd.place,
            status: cmd.status ?: 0,
            earlier: cmd.earlier,
            late:  cmd.late,
            leave:  cmd.leave,
            dueStds: cmd.dueStds,
            attendantStds:  cmd.attendantStds,
            lateStds: cmd.lateStds,
            leaveStds:  cmd.leaveStds,
            evaluateLevel:  cmd.evaluateLevel,
            evaluationText: cmd.evaluationText,
            suggest:  cmd.suggest,
            operator: isAdmin ? userId : null,
            isScheduleTemp: cmd.isScheduleTemp,
            termId: termService.activeTerm.id,
            observationCriteria: ObservationCriteria.findByActiveted(true)
        )
        cmd.evaluations.each { item ->
            form.addToObservationItem( new ObservationItem(
                    observationCriteriaItem: ObservationCriteriaItem.load(item.id),
                    value:  item.value
                )
            )
        }
        form.save()
    }

    ObservationForm  update(String userId, ObservationFormCommand cmd) {
        def isAdmin = observerSettingService.isAdmin()
        ObservationForm form = ObservationForm.get(cmd.id)
        if (!form) {
            throw new NotFoundException()
        }
        if (form.observer.id != userId && !isAdmin) {
            throw new ForbiddenException()
        }
        if (this.cantUpdate(form)){
            return null
        }
        // 如果是未安排实践课，需要同步修改TaskScheduleTemp表中的起止周
        if (form.isScheduleTemp) {
            def schedule = TaskScheduleTemp.findByCreatorAndTeacherAndDayOfWeekAndStartWeek(
                    form.observer, form.teacher, form.dayOfWeek, form.lectureWeek)
            schedule.startWeek = cmd.observationWeek
            schedule.endWeek = cmd.observationWeek
            schedule.save()
        }
        if (securityService.hasRole("ROLE_OBSERVATION_ADMIN")) {
            form.observer = Teacher.load(cmd.observerId)
        }
        form.lectureWeek = cmd.observationWeek
        form.totalSection = cmd.totalSection
        form.teachingMethods = cmd.teachingMethods
        form.supervisorDate = cmd.supervisorDate
        form.observerType = cmd.observerType
        form.earlier = cmd.earlier
        form.late =  cmd.late
        form.leave =  cmd.leave
        form.dueStds = cmd.dueStds
        form.attendantStds = cmd.attendantStds
        form.lateStds = cmd.lateStds
        form.leaveStds = cmd.leaveStds
        form.evaluateLevel = cmd.evaluateLevel
        form.evaluationText = cmd.evaluationText
        form.suggest = cmd.suggest
        form.place = cmd.place
        form.status = cmd.status ?: 0
        form.updateOperator = userId
        form.updateDate = new Date()
        form.method = cmd.method
        form.recommend = cmd.recommend
        form.recommendReason = cmd.recommendReason
        form.teachingEnvironment = cmd.teachingEnvironment
        cmd.evaluations.each { item ->
            def evaluation = ObservationItem.findByObservationCriteriaItemAndObservationForm(ObservationCriteriaItem.load(item.id),form)
            evaluation.value = item.value
        }
        form.save()
    }

    def list(String userId, Integer termId) {
        def term = termService.activeTerm
        def isAdmin = observerSettingService.isAdmin()
        def result = ObservationView.executeQuery '''
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
  view.formTotalSection as formTotalSection,
  view.courseName as course,
  view.placeName as place,
  view.recommend as recommend
)
from ObservationView view
where view.supervisorId like :userId
  and view.termId = :termId
order by view.supervisorDate desc
''', [userId: isAdmin ? '%' : userId, termId: termId ?: term.id]

        // 督导组组长只负责校督导
        if (securityService.hasRole("ROLE_OBSERVER_CAPTAIN")) {
            result = result.grep{
                it.observerType == 1
            }
        }
        return [isAdmin : isAdmin,
                list: result,
                activeTerm: termId ?: term.id,
                terms: observerSettingService.terms]
    }

    def getFormForEdit(String userId, Long id) {
        def form = ObservationForm.get(id)
        if (form) {
            if (userId != form.observer.id && !observerSettingService.isAdmin()) {
                throw new ForbiddenException()
            }
            if (form.status) {
                throw new BadRequestException()
            }

            def term = termService.activeTerm
            def isAdmin = observerSettingService.isAdmin()
            def type = isAdmin? [1,2,3]:observerSettingService.findRolesByUserIdAndTerm(userId,term.id)
            def evaluationSystem = observationCriteriaService.getObservationCriteriaByIdAndMethod(form.observationCriteria.id, realMethod(form.method))
            evaluationSystem.each { group ->
                group.value.each { item ->
                    item.value = ObservationItem.findByObservationCriteriaItemAndObservationForm(ObservationCriteriaItem.load(item.id), form).value
                }
            }
            return [
                    form: getFormInfo(form),
                    term : [
                            startWeek: term.startWeek,
                            maxWeek: term.maxWeek,
                            currentWeek: term.currentWorkWeek,
                            startDate: term.startDate,
                            swapDates: term.swapDates,
                            endWeek: term.endWeek,
                    ],
                    types: type,
                    evaluationSystem: evaluationSystem,
                    isAdmin: isAdmin,
                    isCaptain: securityService.hasRole("ROLE_OBSERVER_CAPTAIN"),
                    observers: securityService.hasRole("ROLE_OBSERVATION_ADMIN") ? observerSettingService.findCurrentObservers(term.id) : null
            ]
        }
        return null
    }

    def getFormForShow(String userId, Long id) {
        def form = ObservationForm.get(id)

        if (form) {
            if (userId != form.observer.id
                    && !observerSettingService.isAdmin()
                    && !securityService.hasRole("ROLE_DEAN_OF_TEACHING")
            ) {
                throw new ForbiddenException()
            }
            def evaluationSystem = observationCriteriaService.getObservationCriteriaByIdAndMethod(form.observationCriteria.id, realMethod(form.method))
            evaluationSystem.each { group ->
                group.value.each { item ->
                    item.value = ObservationItem.findByObservationCriteriaItemAndObservationForm(ObservationCriteriaItem.load(item.id), form).value
                }
            }

            return [
                    form:             getFormInfo(form),
                    evaluationSystem: evaluationSystem,
                    isAdmin:          observerSettingService.isAdmin()
            ]
        }
        return null
    }

    def delete(String userId, Long id) {
        def form = ObservationForm.get(id)
        if (form) {
            if (userId != form.observer.id && !observerSettingService.isAdmin()) {
                throw new ForbiddenException()
            }
            if (form.status) {
                throw new BadRequestException()
            }
            userLogService.log(securityService.userId,securityService.ipAddress,"DELETE", form,"${form as JSON}")
            form.delete()
        }
    }

    def cancel(Long id) {
        def form = ObservationForm.get(id)
        if (form) {
            /*只有管理员可以撤销*/
            if (!observerSettingService.isAdmin() ) {
                throw new ForbiddenException()
            }
            if (form.status !=1 ) {
                throw new BadRequestException()
            }
            form.setStatus(0)
            form.save()
        }
    }


    def submit(String userId, Long id) {
        def form = ObservationForm.get(id)

        if (form) {
            if (userId != form.observer.id && !observerSettingService.isAdmin()) {
                throw new ForbiddenException()
            }
            if (form.status) {
                throw new BadRequestException()
            }
            form.setStatus(1)
            form.save()
        }
    }

    def feed(Long id) {
        def form = ObservationForm.get(id)

        if (form) {
            if (!observerSettingService.isAdmin()) {
                throw new ForbiddenException()
            }
            if (form.status !=1 ) {
                throw new BadRequestException()
            }
            form.setStatus(2)
            form.save()
        }
    }

    Map getFormInfo(ObservationForm form) {
        return [
                id: form.id,
                teacherId: form.teacher.id,
                observerId: form.observer.id,
                teacherName: form.teacher.name,
                observationWeek: form.lectureWeek,
                totalSection: form.totalSection,
                teachingMethods: form.teachingMethods ?: form.methodLable(),
                supervisorDate: form.supervisorDate,
                observerType: form.observerType,
                place: form.place,
                earlier: form.earlier,
                late: form.late,
                leave: form.leave,
                dueStds: form.dueStds,
                attendantStds: form.attendantStds,
                lateStds: form.lateStds,
                leaveStds: form.leaveStds,
                evaluateLevel: form.evaluateLevel,
                evaluationText: form.evaluationText,
                suggest: form.suggest,
                status: form.status,
                isActive: form.termId == termService.activeTerm.id,
                timeslot: getFormTimeslot(form),
                method: form.method,
                recommend: form.recommend,
                recommendReason: form.recommendReason,
                teachingEnvironment: form.teachingEnvironment,
        ]
    }

    private cantUpdate(ObservationForm form) {
        return form.status
    }

    def getFormTimeslot(ObservationForm form) {
        TeacherTimeslotCommand cmd = new TeacherTimeslotCommand(
                termId      : form.termId,
                teacherId   : form.teacher.id,
                week        : form.lectureWeek,
                timeslot    : form.dayOfWeek * 10000 + form.startSection * 100,
        )
        def result
        if (form.isScheduleTemp) {
            result = timeslotService.timeslotForScheduleTemp(cmd)
        } else {
            result = timeslotService.timeslot(cmd)
        }
        if (!result){
            throw new BadRequestException()
        }
        return result
    }

    static Integer realMethod(Integer method) {
        return method == 3 ? 2 : method
    }
}