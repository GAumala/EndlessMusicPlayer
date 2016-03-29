package utils

/**
 * functions to convert timestamps to strings
 * Created by gabriel on 3/16/16.
 */

class TimeConverter() {

    companion object {
        fun timeToMinutes(time : Long) : String {
            val mins = time / 60000
            val secs = (time % 60000) / 1000

            val leftValue = if(mins < 10) "0" + mins else mins
            val rightValue = if(secs < 10) "0" + secs else secs

            return "$leftValue:$rightValue"
        }
    }
}
