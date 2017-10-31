menuGroup 'main', {
    observerAdmin 50,{
        observerSettings   10, 'PERM_OBSERVER_ADMIN', '/web/steer/settings'
        legacyData         20, 'PERM_OBSERVER_ADMIN', '/web/steer/legacies'
        observerDeptAdmin  30, 'PERM_OBSERVER_DEPT_ADMIN',  '/web/steer/departments/${departmentId}/settings'
    }
    observation 51,{
        observationForm     20, 'PERM_OBSERVATION_WRITE', '/web/steer/users/${userId}/observations'
        report              40, 'PERM_OBSERVATION_WRITE', '/web/steer/reports'
        approval            50, 'PERM_OBSERVATION_DEPT_APPROVE', '/web/steer/approvers/${userId}/observations'
    }
    process 20,{
        observationView      81, 'PERM_TASK_SCHEDULE_EXECUTE', '/web/steer/publics'
    }
}
