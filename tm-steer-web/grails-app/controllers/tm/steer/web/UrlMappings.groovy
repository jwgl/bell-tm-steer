package tm.steer.web

class UrlMappings {

    static mappings = {

        "/users"(resources: 'user', includes: []) {
            "/observations"(resources: 'observationForm', includes: ['index','show']){
                collection {
                    "/schedules"(resources: 'schedule', includes: ['index'])
                    "/teachers"(controller: 'schedule', action: 'teacher', method: 'GET')
                    "/places"(controller: 'schedule',  action: 'places', method: 'GET')
                }
            }
        }

        "/reports"(resources: 'report', includes: ['index','show']){
            collection {
                "/observe-priority"(controller: 'report', action: 'observePriority', method: 'GET')
                "/reward"(controller: 'report', action: 'reward', method: 'GET')
                "/wages"(controller: 'report', action: 'wages', method: 'GET')
            }
        }

        "/approvers"(resources: 'approval', includes: []){
            "/observations"(resources: 'approval', includes: ['index'])
        }

        "/publics"(resources: 'public', includes: ['index']){
            collection {
                "/legacies"(controller: 'public', action: 'legacies', method: 'GET')
            }
        }

        "/settings"(resources: 'observerSetting', includes: ['index'])

        "/legacies"(resources:'legacyData')

        "/departments"(resources: 'department', includes: []){
            "/settings"(resources: 'observerDepartment')
            "/wages"(controller: 'observerDepartment', action: 'wages', method: 'GET')
        }

        "/"(view:"/index")
        "500"(view:'/error')
        "404"(view:'/notFound')
    }
}
