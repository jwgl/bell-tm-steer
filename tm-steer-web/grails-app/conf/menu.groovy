menuGroup 'main', {
    observerAdmin 50,{
        observerSettings   10, 'PERM_ADMIN_SUPERVISOR_WRITE', '/web/steer/settings'
        legacyData         20, 'PERM_ADMIN_SUPERVISOR_WRITE', '/web/steer/legacies'
        observerDeptAdmin  30, 'PERM_CO_SUPERVISOR_ADMIN',  '/web/steer/departments/${departmentId}/settings'
    }
    observation 51,{
        courseView          10, 'PERM_SUPERVISOR_WRITE', '/web/steer/users/${userId}/schedules'
        observationForm     20, 'PERM_SUPERVISOR_WRITE', '/web/steer/users/${userId}/observations'
        report              40, 'PERM_SUPERVISOR_WRITE', '/web/steer/users/${userId}/reports'
        approval            50, 'PERM_CO_SUPERVISOR_APPROVE', '/web/steer/approvers/${userId}/observations'
    }
    affair 40,{
        observationView      10, 'PERM_SUPERVISOR_PUBLIC', '/web/steer/publics'
    }



}