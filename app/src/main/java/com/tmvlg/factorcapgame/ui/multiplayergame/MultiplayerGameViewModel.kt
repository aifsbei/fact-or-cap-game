package com.tmvlg.factorcapgame.ui.multiplayergame

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.tmvlg.factorcapgame.data.FactOrCapAuth
import com.tmvlg.factorcapgame.data.repository.fact.Fact
import com.tmvlg.factorcapgame.data.repository.fact.FactRepository
import com.tmvlg.factorcapgame.data.repository.firebase.FirebaseGameRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.ArrayDeque
import kotlin.NoSuchElementException

class MultiplayerGameViewModel(
    private val factRepository: FactRepository,
    private val firebaseGameRepository: FirebaseGameRepository
) : ViewModel() {
    private val _exception = MutableLiveData<IOException?>(null)
    val exception = _exception.map { it }

    private val _gameFinished = MutableLiveData(false)
    val gameFinished = _gameFinished.map { it }

    private val _rightAnswersCount = MutableLiveData(0)
    val rightAnswersCount = _rightAnswersCount.map { it }

    private val _timeLeftFormatted = MutableLiveData<String>()
    val timeLeftFormatted = _timeLeftFormatted.map { it }

    private val _fact = MutableLiveData<Fact>()
    val fact = _fact.map { it }

    private val _isAnswerCorrect = MutableLiveData<Boolean>()
    val isAnswerCorrect = _isAnswerCorrect.map { it }

    private var _factsList = ArrayDeque<Fact>()

    private val _factsLoadingState = MutableLiveData(true)
    val factsLoadingState = _factsLoadingState.map { it }

    private var timeLeft: Long = GAME_DURATION_MS

    fun sendAnswer(answer: Boolean) = viewModelScope.launch {
        if (fact.value?.isTrue == answer) {
            _rightAnswersCount.postValue(rightAnswersCount.value?.plus(1))
            _isAnswerCorrect.postValue(true)
            timeLeft += EXTRA_TIME_FOR_RIGHT_ANSWER
        } else {
            _isAnswerCorrect.postValue(false)
            timeLeft -= LOST_TIME_FOR_WRONG_ANSWER
        }
//        _timeLeftFormatted.value = formatTime(timeLeft)
        getFact()
        loadFactsList()
    }

    fun getFact() = viewModelScope.launch {
        while (true) {
            Log.d("1", "loadFactsList: ${_factsList.size}")
            try {
                val fact = _factsList.pop()
                _fact.postValue(fact)
                break
            } catch (e: NoSuchElementException) {
                delay(CHECK_TIME_IS_FACT_LOADED)
            }
        }
    }

    private fun loadFactsList() = viewModelScope.launch {
        while (_factsList.size < FACTS_TO_BE_LOADED_COUNT) {
            try {
                _factsList.push(factRepository.getFact())
                _exception.postValue(null)
            } catch (e: IOException) {
                _exception.postValue(e)
            }
        }
    }

    fun startGame(lobbyId: String) = viewModelScope.launch {
        firebaseGameRepository.listenLobbyPlayers(lobbyId)
        loadFactsList().join()
        val username = FactOrCapAuth.currentUser.value?.name
            ?: throw IllegalStateException("User is unauthorized")
        firebaseGameRepository.setPlayerLoaded(lobbyId, username)
        while (!firebaseGameRepository.isAllPlayersLoaded()) {
            delay(CHECK_TIME_IS_PLAYERS_LOADED)
        }
        Log.d("1", "waitForAllPlayersLoaded: ALL PLAYERS LOADED")
        if (_factsLoadingState.value == true) {
            getFact()
            _factsLoadingState.postValue(false)
        }
        startTimer()
    }

    private fun startTimer() = viewModelScope.launch {
        do {
            timeLeft -= MS_DELAY
            _timeLeftFormatted.postValue(formatTime(timeLeft))
            delay(MS_DELAY.toLong())
        } while (timeLeft > 0)
        _gameFinished.postValue(true)
    }

    private fun formatTime(timeLeftMs: Long): String {
        val minutes = timeLeftMs / MS_IN_MINUTE
        val seconds = (timeLeftMs / MS_IN_SECOND) - (minutes * SECONDS_IN_MINUTE)
        return String.format("%02d:%02d", minutes, seconds)
    }

    companion object {
        const val MS_DELAY = 100
        const val MS_IN_SECOND = 1_000
        const val SECONDS_IN_MINUTE = 60
        const val MS_IN_MINUTE = SECONDS_IN_MINUTE * MS_IN_SECOND
        const val GAME_DURATION_MS = 120_000L
        const val EXTRA_TIME_FOR_RIGHT_ANSWER = 3_000L
        const val LOST_TIME_FOR_WRONG_ANSWER = 15_000L
        const val FACTS_TO_BE_LOADED_COUNT = 20
        const val CHECK_TIME_IS_PLAYERS_LOADED = 2_000L
        const val CHECK_TIME_IS_FACT_LOADED = 300L
    }
}
