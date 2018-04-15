package tm.steer.api

class UrlMappings {

    static mappings = {
        "/places"(resources: "placeTimeslot", includes: ['index', 'show'])

        "/teachers"(resources: "teacher") {
            "/timeslots"(resources: "teacherTimeslot",includes: ['index', 'show'])
        }

        "/schedules"(resources: "schedule")
        "/unschedules"(resources: "taskScheduleTempForm"){
            collection {
                "/findtask"(controller: 'taskScheduleTempForm', action: 'findTask', method: 'GET')
            }
        }

        "/users"(resources: 'user') {
            "/observations"(resources: "observationForm"){
                collection {
                    "/term"(controller: 'schedule', action: 'getTerm', method: 'GET')
                    "/observationPriority"(controller: 'schedule', action: 'teacherActiveList', method: 'GET')
                }
            }
        }

        "/reports"(resources: "report")

        "/approvers"(resources: "approval",include: []){
            "/observations"(resources: "approval")
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
