package cn.edu.bnuz.steer

class Section {
    Integer id
    String name
    Integer start
    Integer total
    Integer displayOrder

    static mapping = {
        table 'dv_course_section'
        sort  'displayOrder'
    }
}
