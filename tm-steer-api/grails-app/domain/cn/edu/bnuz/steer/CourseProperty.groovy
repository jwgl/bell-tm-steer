package cn.edu.bnuz.steer

class CourseProperty {
    /**
     * 教学班ID
     */
    UUID id

    /**
     * 课程性质
     */
    String propertyName

    static mapping = {
        table 'dv_observation_course_property'
    }
}