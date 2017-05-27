# 督导听课
```
/schedules 课表
    /{scheduleId}
    /schedule?type=  课表查询（组合查询、教师课表、教室课表）
    /place 地点查询
    /observationPriority 优先听课名单

/users
    /{userId}
        /observation 督导员听课记录
            /{observationId}

/approvers  督导管理员、教学院长
    /approverId
        /observations 本人权限范围内的督导听课记录（查看、发布）
            /{observationId}

/reports 报表
    /count?type = 听课统计（按学院、督导员、教师——

/rewards 计酬
    /observations

/teachers
    /{teacherId}
        /observations 老师被听课记录

/departments
    /{departmentId} 学院管理
        /settings   学院督导设置
    /teacher?
    /countByObserver

/settings   校管理员（校督导、院督导设置）


```