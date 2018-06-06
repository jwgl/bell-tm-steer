menuGroup 'main', {
    steer 30,{
        observationForm       10, 'PERM_OBSERVATION_WRITE', '/web/steer/users/${userId}/observations'
        observationApproval   11, 'PERM_OBSERVATION_DEPT_APPROVE', '/web/steer/approvers/${userId}/observations'
        observationReport     12, 'PERM_OBSERVATION_WRITE', '/web/steer/reports'
        observationLegacy     13, 'PERM_OBSERVER_ADMIN', '/web/steer/legacies'
    }
    process 20,{
        observationView       60, 'PERM_TASK_SCHEDULE_EXECUTE', '/web/steer/teachers/${userId}/observations'
    }
    settings 90, {
        observers             40, 'PERM_OBSERVER_ADMIN', '/web/steer/settings/observers'
        deptObserver          41, 'PERM_OBSERVER_DEPT_ADMIN',  '/web/steer/departments/${departmentId}/settings/observers'

    }
}
