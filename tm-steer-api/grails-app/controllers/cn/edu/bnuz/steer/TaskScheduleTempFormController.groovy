package cn.edu.bnuz.steer

import cn.edu.bnuz.bell.master.TermService
import org.springframework.security.access.prepost.PreAuthorize

/**
 * 本学期无教学时间地点的实践课安排补录
 */
@PreAuthorize('hasAuthority("PERM_OBSERVATION_WRITE")')
class TaskScheduleTempFormController {
    TaskScheduleTempFormService taskScheduleTempFormService

    def index() { }

    def create(String taskId, String teacherId) {
        renderJson(taskScheduleTempFormService.getFormForCreate(UUID.fromString(taskId), teacherId))
    }

    def save() {
        def cmd = new ScheduleTempCommand()
        bindData(cmd, request.JSON)
        def form = taskScheduleTempFormService.create(cmd)
        renderJson([id: form.id])
    }

    def edit(Long id) {
        renderJson(taskScheduleTempFormService.getFormForEdit(id))
    }

    def update(String id) {
        def cmd = new ScheduleTempCommand()
        bindData(cmd, request.JSON)
        cmd.id = UUID.fromString(id)
        taskScheduleTempFormService.update(cmd)
        renderOk()
    }

    def findTask(String teacherId) {
        renderJson(taskScheduleTempFormService.findTask(teacherId))
    }
}
