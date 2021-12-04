package com.tmvlg.factorcapgame.ui.multiplayergame

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tmvlg.factorcapgame.data.repository.fact.FactRepository
import com.tmvlg.factorcapgame.data.repository.firebase.FirebaseLobbyRepository
import com.tmvlg.factorcapgame.data.repository.game.GameRepository
import com.tmvlg.factorcapgame.data.repository.user.UserRepository
import com.tmvlg.factorcapgame.ui.singlegame.SingleGameFragment

class MultiplayerGameViewModelFactory(
    private val gameRepository: GameRepository,
    private val factRepository: FactRepository,
    private val userRepository: UserRepository,
    private val firebaseLobbyRepository: FirebaseLobbyRepository,
    private val fragment: MultiplayerGameFragment
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MultiplayerGameViewModel::class.java))
            return MultiplayerGameViewModel(gameRepository, factRepository, userRepository, firebaseLobbyRepository, fragment) as T
        throw IllegalArgumentException("Unknown view model class $modelClass")
    }
}
