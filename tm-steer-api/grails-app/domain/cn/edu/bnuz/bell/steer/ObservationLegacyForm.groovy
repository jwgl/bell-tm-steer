package cn.edu.bnuz.bell.steer

class ObservationLegacyForm {
    Integer         id
    String          collegename
    String          teachername
    String          coursename
    String          teachercode
    String          listentime
    String          evaluatemessage
    String          improvemessage
    String          evaluategrade
    String          courseproperty
    String          teachobject
    String          teachpost
    String          classpostion
    String          teachtype
    String          pretime
    String          latetime
    String          leavetime
    String          duecounter
    String          latecounter
    String          solidcounter
    String          leavecounter
    String          teachattitude
    String          teachyear
    String          inpectorname
    String          inpectorcode
    Date            submittime
    Integer         listencount
    String          type
    Integer         observerType
    String          states
    Boolean         state
    Integer         termId
    String          a1
    String          a2
    String          a3
    String          a4
    String          a5
    String          a6
    String          a7
    String          a8
    String          a9
    String          a10
    String          a11
    String          a12


    static mapping = {
        table          'dv_observation_legacy_form'
        comment        '历史遗留数据'

    }

    static constraints = {
        collegename     nullable:true
        teachername     nullable:true
        coursename      nullable:true
        teachercode     nullable:true
        listentime      nullable:true
        evaluatemessage nullable:true
        improvemessage  nullable:true
        evaluategrade   nullable:true
        courseproperty  nullable:true
        teachobject     nullable:true
        teachpost       nullable:true
        classpostion    nullable:true
        teachtype       nullable:true
        pretime         nullable:true
        latetime        nullable:true
        leavetime       nullable:true
        duecounter      nullable:true
        latecounter     nullable:true
        solidcounter    nullable:true
        leavecounter    nullable:true
        teachattitude   nullable:true
        inpectorname    nullable:true
        inpectorcode    nullable:true
        submittime      nullable:true
        listencount     nullable:true
        states          nullable:true
        a1              nullable:true
        a2              nullable:true
        a3              nullable:true
        a4              nullable:true
        a5              nullable:true
        a6              nullable:true
        a7              nullable:true
        a8              nullable:true
        a9              nullable:true
        a10             nullable:true
        a11             nullable:true
        a12             nullable:true
    }
}
