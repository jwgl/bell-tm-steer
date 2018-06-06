package tm.steer.web

class UrlMappings {

    static mappings = {

        "/observers"(resources: 'user', includes: []) {
            "/observations"(resources: 'observationForm', includes: ['index','show'])
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

        "/teachers"(resources: 'teacher', includes: []) {
            "/observations"(resources: 'public', includes: ['index'])
        }

        group "/settings", {
            "/observers"(resources: 'observerSetting', includes: ['index'])
        }

        "/legacies"(resources:'legacyData')

        "/departments"(resources: 'department', includes: []){
            "/settings"(resources: 'setting', includes:[]) {
                collection {
                    "/observers"(resources: 'observerDepartment')
                }
            }
            "/wages"(controller: 'observerDepartment', action: 'wages', method: 'GET')
        }

        "/"(view:"/index")
        "500"(view:'/error')
        "404"(view:'/notFound')
    }
}
