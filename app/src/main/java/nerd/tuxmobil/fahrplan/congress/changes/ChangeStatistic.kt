package nerd.tuxmobil.fahrplan.congress.changes

import info.metadude.android.eventfahrplan.commons.logging.Logging
import nerd.tuxmobil.fahrplan.congress.models.Session

/**
 * Statistic about session changes (canceled, changed, new).
 */
class ChangeStatistic @JvmOverloads constructor(

        val sessions: List<Session>,
        val logging: Logging = Logging.get()

) {

    /**
     * Returns how many sessions are marked as [canceled][Session.changedIsCanceled].
     */
    fun getCanceledSessionsCount() = sessions
            .count { it.changedIsCanceled }
            .also { log("$it canceled sessions") }

    /**
     * Returns how many sessions are marked as [changed][Session.isChanged].
     */
    fun getChangedSessionsCount() = sessions
            .count { it.isChanged }
            .also { log("$it changed sessions") }

    /**
     * Returns how many sessions are marked as [new][Session.changedIsNew].
     */
    fun getNewSessionsCount() = sessions
            .count { it.changedIsNew }
            .also { log("$it new sessions") }

    /**
     * Returns how many favorites are marked as [canceled][Session.changedIsCanceled],
     * [changed][Session.isChanged] or [new][Session.changedIsNew].
     */
    fun getChangedFavoritesCount() = sessions
            .count { it.highlight && (it.changedIsCanceled || it.isChanged || it.changedIsNew) }
            .also { log("$it changed favorites") }

    private fun log(message: String) = logging.d(javaClass.simpleName, message)

}
