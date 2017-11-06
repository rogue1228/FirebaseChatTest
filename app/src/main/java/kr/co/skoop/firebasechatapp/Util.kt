package kr.co.skoop.firebasechatapp

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

/**
 * Created by Administrator on 2017-10-14.
 */
object Util {
    fun getDateForMinute(date: String?): String {
        if (date == null) {
            return ""
        }

        if (date.isEmpty()) {
            return date
        }

        return dateForMinute(forDateTime(date))
    }

    fun getDateForMinute(timeStamp: Long): String {
        return dateForMinute(DateTime(timeStamp))
    }

    fun dateForMinute(dateTime: DateTime): String {
        val now = DateTime.now()
        when {
            dateTime.toString("yyyy-MM-dd") != now.toString("yyyy-MM-dd") ->
                return dateTime.toString("M월 d일")
            dateTime.toString("hh") != now.toString("hh") ->
                return (now.hourOfDay - dateTime.hourOfDay).toString() + "시간전"
            dateTime.toString("mm") != now.toString("mm") ->
                return (now.minuteOfHour - dateTime.minuteOfHour).toString() + "분전"
            else ->
                return "1분미만"
        }
    }

    fun forDateTime(date: String): DateTime {
        if (date == "0000-00-00 00:00:00")
            return DateTime.now()
        return if (date.isEmpty()) DateTime.now() else DateTime.parse(date, DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"))
    }



    fun userNames(map : Map<String, UserModel>) : String {
        var title = ""

        for (user in map.values) {
            title += user.nickname
            title += ", "
        }

        if (title.isEmpty()) {
            return "???"
        }

        return title.substring(0, title.length - 2)
    }
}