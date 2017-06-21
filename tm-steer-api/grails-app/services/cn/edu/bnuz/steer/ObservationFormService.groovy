package cn.edu.bnuz.steer

import cn.edu.bnuz.bell.http.BadRequestException
import cn.edu.bnuz.bell.http.ForbiddenException
import cn.edu.bnuz.bell.http.NotFoundException
import cn.edu.bnuz.bell.master.TermService
import cn.edu.bnuz.bell.operation.TaskSchedule
import cn.edu.bnuz.bell.organization.Teacher
import cn.edu.bnuz.bell.security.SecurityService
import cn.edu.bnuz.bell.security.UserLogService
import grails.converters.JSON
import grails.transaction.Transactional

@Transactional
class ObservationFormService {
    TermService termService
    TimeslotService timeslotService
    ObserverSettingService observerSettingService
    ObservationCriteriaService observationCriteriaService
    UserLogService userLogService
    SecurityService securityService

    ObservationForm create(String userId, ObservationFormCommand cmd){
        //防止重复录入
        ObservationForm form =
                ObservationForm.findByObserverAndSupervisorDateAndPlace(
                        Teacher.load(userId),
                        cmd.supervisorDate,
                        cmd.place
                )
        if(form) {
            throw new BadRequestException()
        }
        def isAdmin = observerSettingService.isAdmin()
        if(isAdmin && !cmd.supervisorId){
            throw new BadRequestException()
        }
        def now = new Date()
        form = new ObservationForm(
                observer: isAdmin?Teacher.load(cmd.supervisorId):Teacher.load(userId),
                teacher: Teacher.load(cmd.teacherId),
                dayOfWeek: cmd.dayOfWeek,
                startSection: cmd.startSection,
                lectureWeek: cmd.supervisorWeek,
                totalSection: cmd.totalSection,
                teachingMethods: cmd.teachingMethods,
                supervisorDate: cmd.supervisorDate,
                recordDate: now,
                observerType: cmd.type,
                place: cmd.place,
                status: cmd.status?:0,
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
                operator: isAdmin?userId:null,
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

    ObservationForm  update(String userId, ObservationFormCommand cmd){

        ObservationForm form = ObservationForm.get(cmd.id)
        if(!form) {
            throw new NotFoundException()
        }
        if (form.observer.id != userId && !observerSettingService.isAdmin()) {
            throw new ForbiddenException()
        }
        if(this.cantUpdate(form)){
//            println form.status
            return null
        }

        form.lectureWeek=cmd.supervisorWeek
        form.totalSection= cmd.totalSection
        form.teachingMethods= cmd.teachingMethods
        form.supervisorDate= cmd.supervisorDate
        form.observerType= cmd.type
        form.earlier= cmd.earlier
        form.late=  cmd.late
        form.leave=  cmd.leave
        form.dueStds= cmd.dueStds
        form.attendantStds=  cmd.attendantStds
        form.lateStds= cmd.lateStds
        form.leaveStds=  cmd.leaveStds
        form.evaluateLevel=  cmd.evaluateLevel
        form.evaluationText= cmd.evaluationText
        form.suggest=  cmd.suggest
        form.status = cmd.status?:0
        form.updateOperator = userId
        form.updateDate = new Date()

        cmd.evaluations.each { item ->
            def evaluation = ObservationItem.findByObservationCriteriaItemAndObservationForm(ObservationCriteriaItem.load(item.id),form)
            evaluation.value=item.value
        }

        form.save()
    }

    def list(String userId, Integer termId){
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
  view.courseName as course,
  view.placeName as place
)
from ObservationView view
where view.supervisorId like :userId
  and view.termId = :termId
order by view.supervisorDate
''', [userId: isAdmin?'%':userId, termId: termId?:term.id]
        return [isAdmin : isAdmin,
                list: result,
                activeTerm: termId?:term.id,
                terms: observerSettingService.terms]
    }

    def getFormForEdit(String userId, Long id) {
        def form = ObservationForm.get(id)
        if(form) {
            if (userId != form.observer.id && !observerSettingService.isAdmin()) {
                throw new ForbiddenException()
            }
            if (form.status) {
                throw new BadRequestException()
            }

            def term = termService.activeTerm
            def isAdmin = observerSettingService.isAdmin()
            def type = isAdmin? [1,2,3]:observerSettingService.findRolesByUserIdAndTerm(userId,term.id)
            def schedule = [
                    term : [
                            startWeek  : term.startWeek,
                            maxWeek    : term.maxWeek,
                            currentWeek: term.currentWorkWeek,
                            startDate  : term.startDate,
                            swapDates  : term.swapDates,
                            endWeek    : term.endWeek,
                    ],
                    timeslot: getFormTimeslot(term.id, form),
                    type        : type,
                    evaluationSystem    : observationCriteriaService.getObservationCriteriaById(form.observationCriteria?.id),
                    isAdmin             : isAdmin,
                    supervisors         : isAdmin? observerSettingService.findCurrentObservers(term.id):null
            ]
            schedule.form = getFormInfo(form)
            schedule.evaluationSystem.each { group ->
                group.value.each { item ->
                    item.value = ObservationItem.findByObservationCriteriaItemAndObservationForm(ObservationCriteriaItem.load(item.id), form)?.value
                }
            }
            return schedule
        }
        return null
    }

    def getFormForShow(String userId, Long id) {
        def form = ObservationForm.get(id)

        if(form) {
            if (userId != form.observer.id && !observerSettingService.isAdmin()) {
                throw new ForbiddenException()
            }

            def schedule = [
                    schedule: getFormTimeslot(termService.activeTerm.id,form)[0],
                    evaluationSystem: observationCriteriaService.getObservationCriteriaById(form.observationCriteria?.id),
                    form: getFormInfo(form)
                    ]
            schedule.evaluationSystem.each { group ->
                group.value.each { item ->
                    item.value = ObservationItem.findByObservationCriteriaItemAndObservationForm(ObservationCriteriaItem.load(item.id), form)?.value
                }
            }
            schedule.isAdmin = observerSettingService.isAdmin()
            return schedule
        }
        return null
    }

    def delete(String userId, Long id){
        def form = ObservationForm.get(id)
        if(form) {
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

    def cancel(String userId, Long id){
        def form = ObservationForm.get(id)
        if(form) {
            /*只有管理员可以撤销*/
            if (!observerSettingService.isAdmin() ) {
                throw new ForbiddenException()
            }
            if (form.status!=1) {
                throw new BadRequestException()
            }
            form.setStatus(0)
            form.save()
        }
    }


    def submit(String userId, Long id){
        def form = ObservationForm.get(id)

        if(form) {
            if (userId != form.observer.id && !observerSettingService.isAdmin()) {
                throw new ForbiddenException()
            }
            if (form.status) {
//                println form.status
                throw new BadRequestException()
            }
            form.setStatus(1)
            form.save()
        }
    }

    def feed(String userId, Long id){
        def form = ObservationForm.get(id)

        if(form) {
            if (!observerSettingService.isAdmin()) {
                throw new ForbiddenException()
            }
            if (form.status!=1) {
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
                supervisorId: form.observer.id,
                supervisorName: form.observer.name,
                supervisorWeek: form.lectureWeek,
                totalSection: form.totalSection,
                teachingMethods: form.teachingMethods,
                supervisorDate: form.supervisorDate,
                type: form.observerType,
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
        ]

    }

    private cantUpdate(ObservationForm form){
        return form.status
    }

    def getFormTimeslot(Integer termId, ObservationForm form){
        TeacherTimeslotCommand cmd = new TeacherTimeslotCommand(
                termId      : termId,
                teacherId   : form.teacher.id,
                week        : form.lectureWeek,
                timeslot    : form.dayOfWeek * 100 + form.startSection,
        )
        def result = timeslotService.timeslot(cmd)
        if( !result){
            throw new BadRequestException()
        }
        return result
    }

}
