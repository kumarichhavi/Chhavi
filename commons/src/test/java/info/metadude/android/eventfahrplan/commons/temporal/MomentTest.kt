package info.metadude.android.eventfahrplan.commons.temporal

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneOffset
import org.threeten.bp.ZonedDateTime

class MomentTest {
    /**
     * Milliseconds representation of 2019-12-30T22:47:57.615Z.
     */
    private val DEC_30_22_47_2019 = 1577746077615

    @Test
    fun dateTimeFieldsAreCorrectlyMapped() {
        val moment = Moment(DEC_30_22_47_2019)

        assertThat(moment.year).isEqualTo(2019)
        assertThat(moment.month).isEqualTo(12)
        assertThat(moment.monthDay).isEqualTo(30)
        assertThat(moment.hour).isEqualTo(22)
        assertThat(moment.minute).isEqualTo(47)
    }

    @Test
    fun startOfDay() {
        val moment = Moment(DEC_30_22_47_2019)
        val startOfDay = moment.startOfDay()

        assertThat(startOfDay.year).isEqualTo(2019)
        assertThat(startOfDay.month).isEqualTo(12)
        assertThat(startOfDay.monthDay).isEqualTo(30)
        assertThat(startOfDay.hour).isEqualTo(0)
        assertThat(startOfDay.minute).isEqualTo(0)
    }

    @Test
    fun endOfDay() {
        val moment = Moment(DEC_30_22_47_2019)
        val endOfDayUTC = moment.endOfDay().toUTCDateTime()

        assertThat(endOfDayUTC.year).isEqualTo(2019)
        assertThat(endOfDayUTC.monthValue).isEqualTo(12)
        assertThat(endOfDayUTC.dayOfMonth).isEqualTo(30)
        assertThat(endOfDayUTC.hour).isEqualTo(23)
        assertThat(endOfDayUTC.minute).isEqualTo(59)
    }

    @Test
    fun getMinuteOfDayZonedInput() {
        val startsAtDate = ZonedDateTime.of(2019, 8, 27, 6, 30, 0, 0, ZoneOffset.ofHours(4))
        assertThat(Moment(startsAtDate).minuteOfDay).isEqualTo((6 - 4) * 60 + 30)
    }

    @Test
    fun getDayOfMonthWithLeapYearDay() {
        // Thursday, February 28, 2019 11:59:59 PM UTC
        assertThat(Moment(1551312000000).monthDay).isEqualTo(28)
    }

    @Test
    fun getDayOfMonthWithDayAfterLeapYear() {
        // Friday, March 1, 2019 12:00:00 AM UTC
        assertThat(Moment(1551398400000).monthDay).isEqualTo(1)
    }

    @Test
    fun startOfDayVsLocalDate() {
        val localDateString = "2019-12-30"
        val localDate = LocalDate.parse(localDateString)

        val startOfDay = Moment(DEC_30_22_47_2019).startOfDay().toUTCDateTime().toLocalDate()

        assertThat(startOfDay).isEqualTo(localDate)
    }

    @Test
    fun timeZoneHasNoEffectOnMilliseconds() {
        val nowUTC = Moment()
        val utcMillis = nowUTC.toMilliseconds()
        val utcSeconds = utcMillis / 1000

        val nowTZ = nowUTC.toZonedDateTime(ZoneOffset.ofHours(2))
        val tzSeconds = nowTZ.toEpochSecond()

        assertThat(utcSeconds).isEqualTo(tzSeconds)
    }

    @Test
    fun toLocalDate() {
        val localDateString = "2019-12-31"
        val localDate = LocalDate.parse(localDateString)

        val momentLocalDate = Moment(localDateString).toUTCDateTime().toLocalDate()

        assertThat(momentLocalDate).isEqualTo(localDate)
    }

    @Test
    fun isBefore() {
        val moment1 = Moment()
        val moment2 = Moment()
        moment2.plusSeconds(1)

        assertThat(moment1.isBefore(moment2)).isTrue()
        assertThat(moment2.isBefore(moment1)).isFalse()
        assertThat(moment1.isBefore(moment1)).isFalse()
    }

    @Test
    fun plusSeconds() {
        val moment = Moment(0)
        moment.plusSeconds(1)

        assertThat(moment.toMilliseconds()).isEqualTo(1000)

        moment.plusSeconds(-1)
        assertThat(moment.toMilliseconds()).isEqualTo(0)
    }

    @Test
    fun plusMinutes() {
        val moment = Moment(0)
        moment.plusMinutes(1)

        assertThat(moment.toMilliseconds()).isEqualTo(1000 * 60)

        moment.plusMinutes(-1)
        assertThat(moment.toMilliseconds()).isEqualTo(0)
    }

    @Test
    fun minusHours() {
        val moment = Moment(3600 * 1000)
        moment.minusHours(1)

        assertThat(moment.toMilliseconds()).isEqualTo(0)

        moment.minusHours(-1)
        assertThat(moment.toMilliseconds()).isEqualTo(3600 * 1000)
    }

    @Test
    fun minusMinutes() {
        val moment = Moment(60 * 1000)
        moment.minusMinutes(1)

        assertThat(moment.toMilliseconds()).isEqualTo(0)

        moment.minusMinutes(-1)
        assertThat(moment.toMilliseconds()).isEqualTo(60 * 1000)
    }
}
