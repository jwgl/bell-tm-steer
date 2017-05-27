package tm.steer.api

class UrlMappings {

    static mappings = {
        "/users"(resources: 'user') {
            "/schedules"(resources: "schedule", includes: ['show', 'create', 'save', 'update']) {
                collection {
                    "/findSchedule"(controller: 'schedule', action: 'findSchedule', method: 'GET')
                    "/place"(controller: 'schedule', action: 'findPlace', method: 'GET')
                    "/findTeacherSchedule"(controller: 'schedule', action: 'findTeacherSchedule', method: 'GET')
                    "/findPlaceSchedule"(controller: 'schedule', action: 'findPlaceSchedule', method: 'GET')
                    "/term"(controller: 'schedule', action: 'getTerm', method: 'GET')
                    "/teacherActive"(controller: 'schedule', action: 'teacherActiveList', method: 'GET')
                }
            }
            "/observations"(resources: "observationForm"){
                collection {
                    "/cancel"(controller: 'observationForm', action: 'cancel', method: 'GET')
                    "/submit"(controller: 'observationForm', action: 'submit', method: 'GET')
                    "/feed"(controller: 'observationForm', action: 'feed', method: 'GET')
                }
            }

            "/reports"(resources: "report"){
                collection {
                    "/groupByDepartment"(controller: 'report', action: 'departmentReport', method: 'GET')
                    "/countByObserver"(controller: 'report', action: 'countByObserver', method: 'GET')
                    "/teacherSupervised"(controller: 'report', action: 'teacherSupervisedReport', method: 'GET')
                    "/reward"(controller: 'report', action: 'reward', method: 'GET')
                    "/rewardDone"(controller: 'report', action: 'rewardDone', method: 'GET')
                }
            }
        }

        "/approvers"(resources: "approval",include: []){
            "/observations"(resources: "approval")
            collection {
                "/feed"(controller: 'approval', action: 'feed', method: 'GET')
            }
        }

        "/publics"(resources: "public"){
            collection {
                "/legacylist"(controller: 'public', action: 'legacyList', method: 'GET')
                "/legacyshow"(controller: 'public', action: 'legacyShow', method: 'GET')
            }
        }

        "/departments"(resources: 'department', includes: []){
            "/settings"(resources: 'observerDepartment'){
                collection {
                    "/teachers"(controller: 'observerDepartment', action: 'teachers', method: 'GET')
                    "/countByObserver"(controller: 'observerDepartment', action: 'countByObserver', method: 'GET')
                }
            }
        }

        "/settings"(resources:'setting')

        "/legacies"(resources:'legacyData')

        "500"(view: '/error')
        "404"(view: '/notFound')
    }
}
