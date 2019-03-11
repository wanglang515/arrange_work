package com.lang.hospital.bean

class WorkPerson {
    var name: String = ""
    var id: Int = -1
    var countWork = 0
    var workTimes = arrayOf(0, 0, 0, 0, 0, 0, 0, 0)  //每个时间点总班数
    var weekNight = 0
    var weekday = 0

    override fun toString(): String {
        return "$id $name $countWork $weekNight $weekday " +
                "${workTimes[0]} ${workTimes[1]} ${workTimes[2]} ${workTimes[3]} " +
                "${workTimes[4]} ${workTimes[5]} ${workTimes[6]} ${workTimes[7]}"
    }
}