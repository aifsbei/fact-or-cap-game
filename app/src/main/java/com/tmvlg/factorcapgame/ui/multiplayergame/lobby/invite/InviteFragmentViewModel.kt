package com.tmvlg.factorcapgame.ui.multiplayergame.lobby.invite

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.tmvlg.factorcapgame.data.FactOrCapAuth
import com.tmvlg.factorcapgame.data.network.fcm.FCMClientApi
import com.tmvlg.factorcapgame.data.network.fcm.models.DataModel
import com.tmvlg.factorcapgame.data.network.fcm.models.NotificationModel
import com.tmvlg.factorcapgame.data.network.fcm.models.RootModel
import com.tmvlg.factorcapgame.data.repository.user.UserRepository
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback

class InviteFragmentViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    var searchedPlayers = userRepository.subscribeOnFoundPlayers()

    fun invite(playerName: String, lobbyId: String) {
        val db = Firebase.firestore

        db.collection("users").document(playerName)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val token = document.data?.get("token").toString()
                    Log.d("1", "invite: SUCESS! token = $token")

                    val sender = FactOrCapAuth.currentUser.value?.name

                    val rootModel = RootModel(
                        token,
                        NotificationModel("Invite", "$sender invites you to lobby! Tap to join"),
                        DataModel(lobbyId),
                        "high"
                    )

                    val responseBodyCall: Call<ResponseBody> = FCMClientApi.retrofitService.sendNotification(rootModel)

                    responseBodyCall.enqueue(
                        object : Callback<ResponseBody> {
                            override fun onResponse(
                                call: Call<ResponseBody>,
                                response: retrofit2.Response<ResponseBody>
                            ) {
                                Log.d("1", "Successfully notification send by using retrofit.")
                            }

                            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                                Log.d("1", "retrofit failure.")
                            }
                        }
                    )
                }
            }
    }

    fun clearPlayerList() {
        userRepository.unsubscribeOnFoundPlayers()
    }

    fun findPlayers(query: String) = viewModelScope.launch {
        userRepository.searchForPlayers(query)
        Log.d("1", "getPlayers players = ${searchedPlayers.value}")
    }
}
