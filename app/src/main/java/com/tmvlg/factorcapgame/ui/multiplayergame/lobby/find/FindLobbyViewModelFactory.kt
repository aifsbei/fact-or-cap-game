package com.tmvlg.factorcapgame.ui.multiplayergame.lobby.find

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tmvlg.factorcapgame.data.repository.firebase.FirebaseLobbyRepository

class FindLobbyViewModelFactory(
    private val firebaseLobbyRepository: FirebaseLobbyRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return try {
            modelClass.getConstructor(
                FirebaseLobbyRepository::class.java
            ).newInstance(firebaseLobbyRepository)
        } catch (e: ReflectiveOperationException) {
            val exception = IllegalArgumentException("Unknown view model class $modelClass")
            exception.addSuppressed(e)
            throw exception
        }
    }
}
