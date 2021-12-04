package com.tmvlg.factorcapgame.ui.statisitics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tmvlg.factorcapgame.data.repository.game.GameRepository
import com.tmvlg.factorcapgame.data.repository.user.UserRepository

class StatisticsViewModelFactory(
    private val userRepository: UserRepository,
    private val gameRepository: GameRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StatisticsViewModel::class.java))
            return StatisticsViewModel(userRepository, gameRepository) as T
        throw IllegalArgumentException("Unknown view model class $modelClass")
    }
}
