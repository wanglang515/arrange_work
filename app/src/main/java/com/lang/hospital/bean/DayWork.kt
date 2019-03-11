package com.lang.hospital.bean

class DayWork {

    var day: String? = null
    var week: Int = -1
    val persons = arrayListOf(-1, -1, -1, -1, -1, -1, -1, -1)

    override fun toString(): String {
        return "$day $week "
    }
}