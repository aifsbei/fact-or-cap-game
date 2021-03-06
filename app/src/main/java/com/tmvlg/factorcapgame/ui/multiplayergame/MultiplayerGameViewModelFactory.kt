package com.tmvlg.factorcapgame.ui.multiplayergame

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tmvlg.factorcapgame.data.repository.fact.FactRepository
import com.tmvlg.factorcapgame.data.repository.firebase.FirebaseGameRepository

class MultiplayerGameViewModelFactory(
    private val factRepository: FactRepository,
    private val firebaseGameRepository: FirebaseGameRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return try {
            modelClass.getConstructor(
                FactRepository::class.java,
                FirebaseGameRepository::class.java
            ).newInstance(
                factRepository,
                firebaseGameRepository
            )
        } catch (e: ReflectiveOperationException) {
            val exception = IllegalArgumentException("Unknown view model class $modelClass")
            exception.addSuppressed(e)
            throw exception
        }
    }
}
