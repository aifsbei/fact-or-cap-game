package com.tmvlg.factorcapgame.ui.menu

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tmvlg.factorcapgame.BaseTest
import com.tmvlg.factorcapgame.R
import com.tmvlg.factorcapgame.data.FactOrCapAuth
import com.tmvlg.factorcapgame.ui.MainActivity
import com.tmvlg.factorcapgame.ui.leaderboard.LeaderboardFragmentTest.Companion.leaderboardElems
import com.tmvlg.factorcapgame.ui.statisitics.StatisticsFragmentTest.Companion.statisticElems
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MenuFragmentTest : BaseTest() {

    private val singleGameElems: IntArray = intArrayOf(
        R.id.agree_button,
        R.id.disagree_button,
        R.id.tv_score,
        R.id.tv_fact
    )

    private val noInternetElems: IntArray = intArrayOf(
        R.id.tv_error_message,
        R.id.error_icon,
        R.id.tv_error_description,
        R.id.menu_button
    )

    @get:Rule
    var activityRule: ActivityScenarioRule<MainActivity> =
        ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun testMenuIsDisplayed() {
        checkDisplayedAll(*menuElems)
    }

    @Test
    fun testGoToStatic() {
        click(R.id.stat_button)
        checkDisplayedAll(*statisticElems)
    }

    @Test
    fun testGoToSingleGameWithOrNoInternet() {
        click(R.id.single_game_button)
        if (isConnected(ApplicationProvider.getApplicationContext())) {
            checkDisplayedAll(*singleGameElems)
        } else {
            checkDisplayedAll(*noInternetElems)
        }
    }

    @Test
    fun testGoToLeaderboard() {
        click(R.id.leaderboard_button)
        Thread.sleep(5000)
        checkDisplayedAll(*leaderboardElems)
    }

    @Test
    fun testGoToCreateRoom() {
        click(R.id.multiplayer_game_button)
        Thread.sleep(5000)
        click(R.id.create_room_button)
        Thread.sleep(5000)
        enterDataByHint("Room name", "TEST")
        Thread.sleep(5000)
        clickByText("CREATE")
    }

    @Test
    fun testGoToSignOutAndGoToMultiplayer() {
        Thread.sleep(3000)
        clickByText("Hello, ${FactOrCapAuth.currentUser.value?.name}")
        clickByText("Sign Out")
        Thread.sleep(3000)
        isDisplayed(R.id.google_sign_in_cardview)
        click(R.id.multiplayer_game_button)
        isDisplayedByText("Auth required")
    }

    private fun isConnected(context: Context): Boolean {
        val connectivityManager = context.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val nw = connectivityManager.activeNetwork
            val actNw = connectivityManager.getNetworkCapabilities(nw)
            when {
                nw == null -> false
                actNw == null -> false
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            val nwInfo = connectivityManager.activeNetworkInfo ?: return false
            nwInfo.isConnected
        }
    }

    companion object {
        val menuElems = intArrayOf(
            R.id.game_logo_textview,
            R.id.single_game_button,
            R.id.multiplayer_game_button,
            R.id.stat_button,
            R.id.leaderboard_button,
            R.id.change_volume_button,
            R.id.status_buttons_layout
        )
    }
}
