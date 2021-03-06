package com.tmvlg.factorcapgame.data.repository.user

import android.util.Log
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.tmvlg.factorcapgame.data.preferences.PreferenceProvider
import com.tmvlg.factorcapgame.data.repository.firebase.Player

class UserRepository(
    private val preferenceProvider: PreferenceProvider
) {
    // saves statistics to shared pref
    @WorkerThread
    fun saveGame(statistics: Statistics) {
        preferenceProvider.saveStatistics(statistics)
    }

    // loads statistics to shared pref
    @WorkerThread
    fun getStats(): Statistics {
        return preferenceProvider.getStatistics()
    }

    @Deprecated("You should get user info from account")
    @WorkerThread
    fun saveUsername(username: String?) {
        preferenceProvider.saveUsername(username)
    }

    @Deprecated("You should get user info from account")
    @WorkerThread
    fun getUsername(): String? {
        return preferenceProvider.getUsername()
    }

    @Deprecated("You should get user info from account")
    @WorkerThread
    fun saveToken(token: String?) {
        preferenceProvider.setRegistrationToken(token)
    }

    @Deprecated("You should get user info from account")
    @WorkerThread
    fun getToken(): String? {
        return preferenceProvider.getRegistrationToken()
    }

    private val playersLD = MutableLiveData<List<Player>>()
    fun subscribeOnFoundPlayers(): LiveData<List<Player>> {
        return playersLD
    }

    fun unsubscribeOnFoundPlayers() {
        playersLD.postValue(emptyList<Player>())
    }

    @WorkerThread
    fun searchForPlayers(query: String) {
        val db = Firebase.firestore
        val usersReference = db.collection("users")
        val players = mutableListOf<Player>()
        val searchedPlayers = usersReference.orderBy("username")
            .startAt(query.trim())
            .endAt(query.trim() + '\uf8ff')
            .limit(SEARCH_FOR_PLAYERS_LIMIT)
        searchedPlayers.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                for (document in task.result) {
                    val username = document.data.get("username").toString()
                    val player = Player()
                    player.name = username
                    players.add(player)
                    Log.d("1", "getPlayers: $username")
                }
                playersLD.postValue(players)
            }
        }
    }

    companion object {
        const val TAG = "UserRepository"
        const val SEARCH_FOR_PLAYERS_LIMIT = 3L
    }
}
