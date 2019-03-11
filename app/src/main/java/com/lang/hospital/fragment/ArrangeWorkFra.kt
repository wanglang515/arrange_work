package com.lang.hospital.fragment

import android.os.Bundle
import android.os.CountDownTimer
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.js.common.data.SpfManager
import com.lang.hospital.R
import com.lang.hospital.bean.DayWork
import com.lang.hospital.bean.WorkPerson
import com.lang.hospital.utils.ExcelUtil
import kotlinx.android.synthetic.main.arrange_work.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class ArrangeWorkFra : Fragment() {

    private var workCount = 0

    private var timer: Timer? = null
    private var bbreak = false
    private var countDownTimer: CountDownTimer? = null

    companion object {
        var weeks = arrayOf("星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六")
        val workPersonList = ArrayList<WorkPerson>()
        val dayWorkList = ArrayList<DayWork>()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.arrange_work, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val tmpDay = Calendar.getInstance()
        edit_year.setText(tmpDay.get(Calendar.YEAR).toString())
        edit_month.setText((tmpDay.get(Calendar.MONTH) + 1).toString())
        edit_day.setText(tmpDay.get(Calendar.DAY_OF_MONTH).toString())

        button_start_all_plan.setOnClickListener {
            load_arrange.visibility = View.VISIBLE
            bbreak = false
            timer?.cancel()
            timer = Timer()
            timer?.schedule(object : TimerTask() {
                override fun run() {
                    try {
                        initPerson()
                        startPlan()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }, 0)
            countDownTimer = object : CountDownTimer(5000, 1000) {
                override fun onFinish() {
                    button_start_all_plan.callOnClick()
                }

                override fun onTick(p0: Long) {
                    if (p0 / 1000 == 1L) {
                        load_arrange.visibility = View.GONE
                        timer?.cancel()
                        bbreak = true
                        Toast.makeText(context, "此次排班不成功,系统将自动再次排班", Toast.LENGTH_SHORT).show()
                    }
                }
            }.start()
        }

    }

    private fun startPlan() {
        if (workPersonList.size == 0) {
            button_start_all_plan.post {
                Toast.makeText(context, "无人员不能排班", Toast.LENGTH_SHORT).show()
            }
        }
        dayWorkList.clear()
        val tmpDay = Calendar.getInstance()
        if (edit_year.text.isBlank() || edit_month.text.isBlank() || edit_day.text.isBlank()) {
            button_start_all_plan.post {
                Toast.makeText(context, "日期填写不全", Toast.LENGTH_SHORT).show()
            }
            return
        }
        tmpDay.set(
            edit_year.text.toString().toInt(),
            edit_month.text.toString().toInt() - 1,
            edit_day.text.toString().toInt()
        ) //月份从0开始
        if (tmpDay.get(Calendar.DAY_OF_WEEK) != 2) {
            button_start_all_plan.post {
                Toast.makeText(context, "请以周一的日期开始", Toast.LENGTH_SHORT).show()
            }
            return
        }
        workCount = 0
        var index = 0
        do {
            val resultDate = tmpDay.time
            val sdf = SimpleDateFormat("yyyy-MM-dd")
            val dayWork = DayWork()
            dayWork.day = sdf.format(resultDate)
            dayWork.week = tmpDay.get(Calendar.DAY_OF_WEEK) - 1
            workCount += if (dayWork.week in 1..5)
                8
            else
                4
            dayWorkList.add(dayWork)
            tmpDay.add(Calendar.DATE, 1)
        } while (++index < 28)
        var dd = ""
        for (day in dayWorkList) {
            dd += "${day.day}|${day.week}"
        }
        getHhfWP() //何老师班
        getNightTime() //夜班
        if (bbreak)
            return
        getEightTime() //八点班
        if (bbreak)
            return
        getTenTime() //十点班
        if (bbreak)
            return
        countDownTimer?.cancel()
        load_arrange.post {
            load_arrange.visibility = View.GONE
        }
        writeExcel()
    }

    private fun getNightTime() {
        for (day in 0 until 28) {
            val dayWork = dayWorkList[day]
            if (dayWork.week == 5 || dayWork.week == 6) {//周末夜班
                for (dep in 0 until 2) {
                    var loopNum = 0
                    while (true) {
                        var person = workPersonList[(Math.random() * (workPersonList.size - 1) + 1).toInt()]
                        if (isRecentNight(day, person, 1) || isTodaySame(day, person)) {
                            continue
                        }
                        if (person.weekNight > 0) {
                            loopNum++
                            if (loopNum > 500) {
                                if (person.weekNight > 1) {
                                    continue
                                }
                            } else {
                                continue
                            }
                        }
                        dayWork.persons[dep * 4 + 3] = person.id
                        person.workTimes[dep * 4 + 3]++
                        person.countWork++
                        person.weekNight++
                        break
                    }
                }
            }
        }
        for (person in workPersonList) {
            Log.d("langlang", "$person")
        }
        Log.d("langlang", "#########################################")
        var initAdd = 1
        for (day in 0 until 28) {
            val dayWork = dayWorkList[day]
            if (dayWork.week == 5 || dayWork.week == 6)
                continue
            for (dep in 0 until 2) {
                var loopNum = 0
                while (true) {
                    if (bbreak)
                        return
                    var person = workPersonList[(Math.random() * (workPersonList.size - 1) + 1).toInt()]
                    if (dep == 1 && dayWork.persons[3] == person.id)
                        continue
                    if (isRecentNight(day, person, 3)) {
                        continue
                    }
                    if (person.workTimes[3] + person.workTimes[7] > initAdd) {
                        loopNum++
                        if (loopNum > 500) {
                            if (loopNum > 1000) {
                                initAdd++
                                continue
                            }
                            if (person.workTimes[3] + person.workTimes[7] > initAdd + 1)
                                continue
                        } else {
                            continue
                        }
                    }
                    dayWork.persons[dep * 4 + 3] = person.id
                    person.workTimes[dep * 4 + 3]++
                    person.countWork++
                    break
                }
            }
            Log.d(
                "langlang",
                "${dayWork.day} ${weeks[dayWork.week]} ${workPersonList[dayWork.persons[3]].name} ${workPersonList[dayWork.persons[7]].name}"
            )
        }
        for (person in workPersonList) {
            Log.d("langlang", "$person")
        }
    }

    private fun getEightTime() {
        for (day in 0 until 28) {
            val dayWork = dayWorkList[day]
            for (dep in 0 until 2) {
                if (dayWork.persons[dep * 4] != -1)
                    continue
                var eightS = 0
                var weekDayS = 0
                while (true) {
                    if (bbreak)
                        break
                    val person = workPersonList[(Math.random() * (workPersonList.size - 1) + 1).toInt()]
                    if (person.workTimes[0] > 3) { //每人不超过4个8点班
                        eightS++
                        if (eightS > 500) { //某人会在500次无果以后增加班数
                            if (person.workTimes[0] > 4)
                                continue
                        } else {
                            continue
                        }
                    }
                    if (isYesterdayNight(day, person)
                        || isTodaySame(day, person)
                        || isYesterdayTime(day, person, dep * 4)
                    ) {
                        continue
                    }
                    if (person.weekday > 0) {
                        weekDayS++
                        if (weekDayS > 500) {
                            if (person.weekday > 1)
                                continue
                        } else {
                            continue
                        }
                    }
                    dayWork.persons[dep * 4] = person.id
                    person.workTimes[0]++
                    person.countWork++
                    if (dayWork.week == 0 || dayWork.week == 6)
                        person.weekday++
                    break
                }
            }
        }
    }

    private fun getTenTime() {
        Log.d("langlang", "getTenTime")
        for (tt in 1..2) {
            for (day in 0 until 28) {
                val dayWork = dayWorkList[day]
                if (dayWork.week == 0 || dayWork.week == 6) //周末
                    continue
                for (dep in 0 until 2) {
                    if (dayWork.persons[dep * 4 + tt] != -1)
                        continue
                    var tenS = 0
                    while (true) {
                        if (bbreak)
                            break
                        val person = workPersonList[(Math.random() * (workPersonList.size - 1) + 1).toInt()]
                        if (person.workTimes[dep * 4 + tt] > 3) { //每人不超过4个班
                            tenS++
                            if (tenS > 500) { //某人会在500次无果以后增加班数
                                if (person.workTimes[dep * 4 + tt] > 4)
                                    continue
                            } else {
                                continue
                            }
                        }
                        if (isYesterdayNight(day, person)
                            || isTodaySame(day, person)
                            || isYesterdayTime(day, person, dep * 4 + tt)
                        ) {
                            continue
                        }
                        if (person.countWork > (workCount - 20) / (workPersonList.size - 1))
                            continue
                        dayWork.persons[dep * 4 + tt] = person.id
                        person.workTimes[dep * 4 + tt]++
                        person.countWork++
                        if (dayWork.week == 0 || dayWork.week == 6)
                            person.weekday++
                        break
                    }
                }
            }
        }
    }

    private fun isTodaySame(day: Int, person: WorkPerson): Boolean {
        val dayWork = dayWorkList[day]
        for (index in 0..7) {
            if (dayWork.persons[index] == person.id) {
                return true
            }
        }
        return false
    }

    private fun isYesterdayTime(day: Int, person: WorkPerson, time: Int): Boolean {
        if (day <= 0)
            return false
        val dayWork = dayWorkList[day - 1]
        if (dayWork.persons[time] == person.id) {
            return true
        }
        return false
    }

    private fun isYesterdayNight(day: Int, person: WorkPerson): Boolean {
        for (i in 1..if (day < 1) day else 1) {
            val dayWork = dayWorkList[day - i]
            if (dayWork.persons[3] == person.id || dayWork.persons[7] == person.id) {
                return true
            }
        }
        return false
    }

    private fun isRecentNight(day: Int, person: WorkPerson, recent: Int): Boolean {
        for (i in 1..if (day < recent) day else recent) {
            val dayWork = dayWorkList[day - i]
            if (dayWork.persons[3] == person.id || dayWork.persons[7] == person.id) {
                return true
            }
        }
        for (i in 1..if (day > 27 - recent) {
            27 - day
        } else recent) {
            val dayWork = dayWorkList[day + i]
            if (dayWork.persons[3] == person.id || dayWork.persons[7] == person.id) {
                return true
            }
        }
        return false
    }

    private fun getHhfWP() {
        val person = workPersonList[0]
        var weekWork = 0
        for (week in 0..3) {//先4周
            val rps = arrayOf(0, 0, 0, 0, 0, 0, 0)
            var num = 0
            while (num < 5) {//每周五天班
                val rp = (Math.random() * 7).toInt()
                if (rps[rp] != 0) {
                    continue
                }
                val dayWork = dayWorkList[week * 7 + rp]
                if (dayWork.week == 0 || dayWork.week == 6) { //最多2天周末班 且 最多7个八点班
                    if (weekWork > 1)
                        continue
                    //周六周日不连续
                    if (dayWork.week == 0 && dayWorkList[week * 7 + rp - 1].persons[0] == person.id)
                        continue
                    if (dayWork.week == 6 && dayWorkList[week * 7 + rp + 1].persons[0] == person.id)
                        continue
                    person.workTimes[0]++
                    weekWork++
                    person.weekday++
                }
                rps[rp] = 1
                num++
                person.countWork++
                dayWork.persons[0] = person.id
            }
        }
        getHhfTime()
    }

    private fun getHhfTime() {//均分每天, 为776
        val person = workPersonList[0]
        for (index in 0..27) {
            val dayWork = dayWorkList[index]
            if (dayWork.persons[0] != -1) {
                if (dayWork.week == 0 || dayWork.week == 6)//周末班已排
                    continue
                dayWork.persons[0] = -1
                while (true) {
                    var add = if ((Math.random() * 100).toInt() % 2 == 0) 0 else 4
                    val timePos = (Math.random() * 100).toInt() % 3
                    if (person.workTimes[timePos + add] >= 4
                        || person.workTimes[timePos] + person.workTimes[timePos + 4] >= 7
                        || isYesterdayTime(index, person, timePos + add)
                    ) //最多7个班
                        continue
                    //各院均分
                    if (person.workTimes[0 + add] + person.workTimes[1 + add] + person.workTimes[2 + add] >= 10)
                        continue
                    person.workTimes[timePos + add]++
                    dayWork.persons[timePos + add] = person.id
                    break
                }
            }
        }
    }

    private fun initPerson() {
        workPersonList.clear()
        val ss = SpfManager.getStringData("users")
        if (ss.isBlank())
            return
        val names = ss.split("#")
        for (i in names.indices) {
            val workPerson = WorkPerson()
            workPerson.id = i
            workPerson.name = names[i]
            workPerson.countWork = 0
            workPersonList.add(workPerson)
        }
    }

    private fun writeExcel() {
        val title = arrayOf(
            "年月日",
            "星期",
            "8:00-15:30",
            "10:00-17:30",
            "10:00-17:30",
            "17:30-8:00",
            "8:00-15:30",
            "10:00-17:30",
            "10:00-17:30",
            "17:30-8:00"
        )
        val dateFormat = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss")
        val fileName = "${dateFormat.format(Calendar.getInstance().time).replace("-", "")}-排班表.xls"
        var filePath = "/sdcard" + File.separator + fileName

        ExcelUtil.initExcel(filePath, fileName, title)

        ExcelUtil.writeObjListToExcel(filePath, activity?.findViewById(R.id.text_out_view))
    }
}