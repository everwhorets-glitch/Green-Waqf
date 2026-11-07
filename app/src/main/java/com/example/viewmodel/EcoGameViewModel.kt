package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.BinType
import com.example.data.model.EcoGameItem
import com.example.data.model.EcoGameItemCatalog
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class GameState {
    READY,
    PLAYING,
    GAME_OVER
}

data class EcoGameUiState(
    val gameState: GameState = GameState.READY,
    val currentItem: EcoGameItem? = null,
    val score: Int = 0,
    val comboStreak: Int = 0,
    val lives: Int = 3,
    val timeLeftSeconds: Int = 45,
    val totalAnswered: Int = 0,
    val correctCount: Int = 0,
    val lastFeedbackCorrect: Boolean? = null,
    val feedbackMessage: String? = null,
    val educationalExplanation: String? = null,
    val lastFunFact: String? = null,
    val isShowingExplanation: Boolean = false,
    val highScore: Int = 180,
    val pointsClaimed: Boolean = false
)

class EcoGameViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(EcoGameUiState())
    val uiState: StateFlow<EcoGameUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var availableItems: MutableList<EcoGameItem> = mutableListOf()

    init {
        prepareNewItem()
    }

    fun startGame() {
        timerJob?.cancel()
        availableItems = EcoGameItemCatalog.items.shuffled().toMutableList()
        val firstItem = availableItems.removeFirstOrNull() ?: EcoGameItemCatalog.items.random()

        _uiState.update {
            it.copy(
                gameState = GameState.PLAYING,
                currentItem = firstItem,
                score = 0,
                comboStreak = 0,
                lives = 3,
                timeLeftSeconds = 45,
                totalAnswered = 0,
                correctCount = 0,
                lastFeedbackCorrect = null,
                feedbackMessage = null,
                educationalExplanation = null,
                lastFunFact = null,
                isShowingExplanation = false,
                pointsClaimed = false
            )
        }

        startTimer()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_uiState.value.timeLeftSeconds > 0 && _uiState.value.gameState == GameState.PLAYING) {
                delay(1000L)
                if (!_uiState.value.isShowingExplanation) {
                    _uiState.update { current ->
                        val newTime = current.timeLeftSeconds - 1
                        if (newTime <= 0) {
                            current.copy(
                                timeLeftSeconds = 0,
                                gameState = GameState.GAME_OVER,
                                highScore = maxOf(current.highScore, current.score)
                            )
                        } else {
                            current.copy(timeLeftSeconds = newTime)
                        }
                    }
                }
            }
        }
    }

    fun submitAnswer(selectedBin: BinType) {
        val state = _uiState.value
        if (state.gameState != GameState.PLAYING || state.currentItem == null || state.isShowingExplanation) return

        val item = state.currentItem
        val isCorrect = item.correctBin == selectedBin

        if (isCorrect) {
            val newStreak = state.comboStreak + 1
            val multiplier = when {
                newStreak >= 5 -> 2.0
                newStreak >= 3 -> 1.5
                else -> 1.0
            }
            val earnedPoints = (item.points * multiplier).toInt()
            val newScore = state.score + earnedPoints

            _uiState.update {
                it.copy(
                    score = newScore,
                    comboStreak = newStreak,
                    totalAnswered = it.totalAnswered + 1,
                    correctCount = it.correctCount + 1,
                    lastFeedbackCorrect = true,
                    feedbackMessage = "Betul! ${item.name} termasuk ke dalam sampah ${item.correctBin.title}.",
                    educationalExplanation = item.reasonWhy,
                    lastFunFact = item.funFact,
                    isShowingExplanation = true,
                    highScore = maxOf(it.highScore, newScore)
                )
            }
        } else {
            val newLives = state.lives - 1
            val isOver = newLives <= 0

            _uiState.update {
                it.copy(
                    lives = newLives,
                    comboStreak = 0,
                    totalAnswered = it.totalAnswered + 1,
                    lastFeedbackCorrect = false,
                    feedbackMessage = "Kurang tepat! ${item.name} seharusnya dibuang ke tong ${item.correctBin.title}.",
                    educationalExplanation = item.reasonWhy,
                    lastFunFact = item.funFact,
                    isShowingExplanation = true,
                    gameState = if (isOver) GameState.GAME_OVER else it.gameState,
                    highScore = maxOf(it.highScore, it.score)
                )
            }

            if (isOver) {
                timerJob?.cancel()
            }
        }
    }

    fun proceedToNextItem() {
        if (_uiState.value.gameState != GameState.PLAYING) return
        _uiState.update {
            it.copy(
                isShowingExplanation = false,
                feedbackMessage = null,
                educationalExplanation = null
            )
        }
        prepareNewItem()
    }

    private fun prepareNewItem() {
        if (availableItems.isEmpty()) {
            availableItems = EcoGameItemCatalog.items.shuffled().toMutableList()
        }
        val next = availableItems.removeFirstOrNull() ?: EcoGameItemCatalog.items.random()
        _uiState.update { it.copy(currentItem = next) }
    }

    fun markPointsClaimed() {
        _uiState.update { it.copy(pointsClaimed = true) }
    }

    fun resetToMenu() {
        timerJob?.cancel()
        _uiState.update {
            it.copy(
                gameState = GameState.READY,
                lastFeedbackCorrect = null,
                feedbackMessage = null
            )
        }
        prepareNewItem()
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
