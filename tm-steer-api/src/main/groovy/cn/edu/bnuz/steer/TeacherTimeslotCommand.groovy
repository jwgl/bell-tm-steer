package cn.edu.bnuz.steer

class TeacherTimeslotCommand {
    Integer termId
    String teacherId
    Integer week
    Integer dayOfWeek
    Integer startSection

    def setTimeslot(Integer timeslot) {
        this.dayOfWeek = timeslot.intdiv(10000)
        this.startSection = (timeslot % 10000).intdiv(100)
    }

    public <T> T asType(Class<T> clazz) {
        if (clazz == Map) {
            return [
                    termId      : termId,
                    teacherId   : teacherId,
                    week        : week,
                    dayOfWeek   : dayOfWeek,
                    startSection: startSection
            ]
        } else {
            super.asType(clazz)
        }
    }
}
