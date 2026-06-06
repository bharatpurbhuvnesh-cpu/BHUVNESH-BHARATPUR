package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.api.GeminiClient
import com.example.data.AppDatabase
import com.example.data.RunRecord
import com.example.data.RunRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

enum class RaceState {
    IDLE, RUNNING, FINISHED
}

data class Opponent(
    val name: String,
    val description: String,
    val averageSpeedKmh: Double,
    val difficulty: String,
    val icon: String
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val runDao = AppDatabase.getDatabase(application).runDao()
    private val repository = RunRepository(runDao)

    // Historical runs reactive flow
    val runHistory: StateFlow<List<RunRecord>> = MutableStateFlow<List<RunRecord>>(emptyList()).apply {
        viewModelScope.launch {
            repository.allRuns.collect { value = it }
        }
    }

    // Racing opponents
    val opponents = listOf(
        Opponent("Pacer Pete", "Steady jogger pacing standard run targets", 12.0, "Easy", "🏃"),
        Opponent("Stroller Papa Champ", "Experienced runner boasting triple-wheel speed", 18.0, "Medium", "🏃‍♂️👶"),
        Opponent("Sonic Stroller", "High-tier marathoner who lives on the asphalt", 25.0, "Hard", "⚡👶"),
        Opponent("Usain Bolt (Double)", "Legendary runner pushing bounds. Elite speed!", 36.0, "Extremely Hard", "⚡🏃‍♂️")
    )

    // Distances
    val distances = listOf(100.0, 400.0, 1000.0)

    // Current setup State
    val selectedOpponent = MutableStateFlow(opponents[0])
    val selectedDistance = MutableStateFlow(distances[0])
    val runTitle = MutableStateFlow("Morning Championship Dash")

    // Live Race State Variables
    private val _raceState = MutableStateFlow(RaceState.IDLE)
    val raceState: StateFlow<RaceState> = _raceState.asStateFlow()

    private val _parentDistance = MutableStateFlow(0.0)
    val parentDistance: StateFlow<Double> = _parentDistance.asStateFlow()

    private val _opponentDistance = MutableStateFlow(0.0)
    val opponentDistance: StateFlow<Double> = _opponentDistance.asStateFlow()

    private val _parentSpeed = MutableStateFlow(0.0)
    val parentSpeed: StateFlow<Double> = _parentSpeed.asStateFlow()

    private val _opponentSpeed = MutableStateFlow(0.0)
    val opponentSpeed: StateFlow<Double> = _opponentSpeed.asStateFlow()

    private val _parentHeartRate = MutableStateFlow(75)
    val parentHeartRate: StateFlow<Int> = _parentHeartRate.asStateFlow()

    private val _babyHeartRate = MutableStateFlow(120)
    val babyHeartRate: StateFlow<Int> = _babyHeartRate.asStateFlow()

    private val _babyState = MutableStateFlow("Calm & Sleeping 😴")
    val babyState: StateFlow<String> = _babyState.asStateFlow()

    private val _vibrationState = MutableStateFlow("Super Smooth ✨")
    val vibrationState: StateFlow<String> = _vibrationState.asStateFlow()

    // Real-Time Comfort, Sound, and Motion Metrics
    private val _babySoundLevelDb = MutableStateFlow(32) // range: 25 - 90 dB
    val babySoundLevelDb: StateFlow<Int> = _babySoundLevelDb.asStateFlow()

    private val _babyMotionG = MutableStateFlow(0.12) // range: 0.05 - 2.5 G
    val babyMotionG: StateFlow<Double> = _babyMotionG.asStateFlow()

    private val _motionSmoothnessPct = MutableStateFlow(100) // 0-100%
    val motionSmoothnessPct: StateFlow<Int> = _motionSmoothnessPct.asStateFlow()

    private val _acousticPeacePct = MutableStateFlow(100) // 0-100%
    val acousticPeacePct: StateFlow<Int> = _acousticPeacePct.asStateFlow()

    private val _overallComfortPct = MutableStateFlow(100) // 0-100%
    val overallComfortPct: StateFlow<Int> = _overallComfortPct.asStateFlow()

    // Configurable Safety Threshold Limits
    val comfortThreshold = MutableStateFlow(70) // Overall comfort score drops below this -> trigger warning
    val soundThreshold = MutableStateFlow(65)     // Sound level dB exceeds this -> trigger warning
    val motionThreshold = MutableStateFlow(1.2)   // Motion stroller friction Gs exceeds this -> trigger warning

    // Threshold Alert Notification Engine
    private val _activeAlert = MutableStateFlow<String?>(null)
    val activeAlert: StateFlow<String?> = _activeAlert.asStateFlow()

    private val _alertHistoryList = MutableStateFlow<List<String>>(emptyList())
    val alertHistoryList: StateFlow<List<String>> = _alertHistoryList.asStateFlow()

    // Commentary, loading indicators & final results
    private val _aiCommentary = MutableStateFlow("")
    val aiCommentary: StateFlow<String> = _aiCommentary.asStateFlow()

    private val _aiLoading = MutableStateFlow(false)
    val aiLoading: StateFlow<Boolean> = _aiLoading.asStateFlow()

    private val _raceWinner = MutableStateFlow("")
    val raceWinner: StateFlow<String> = _raceWinner.asStateFlow()

    private val _raceDurationSecs = MutableStateFlow(0)
    val raceDurationSecs: StateFlow<Int> = _raceDurationSecs.asStateFlow()

    private var simulationJob: Job? = null
    private var baseSpeedKmh = 10.0
    private var sprintBoostValue = 0.0

    // High performance cumulative averages for saving
    private var heartRateSum = 0
    private var babyHeartRateSum = 0
    private var tickCount = 0

    fun selectOpponent(opponent: Opponent) {
        if (_raceState.value == RaceState.IDLE) {
            selectedOpponent.value = opponent
        }
    }

    fun selectDistance(dist: Double) {
        if (_raceState.value == RaceState.IDLE) {
            selectedDistance.value = dist
        }
    }

    fun updateTitle(newTitle: String) {
        if (_raceState.value == RaceState.IDLE) {
            runTitle.value = newTitle
        }
    }

    fun startRace() {
        if (_raceState.value != RaceState.IDLE) return

        _raceState.value = RaceState.RUNNING
        _parentDistance.value = 0.0
        _opponentDistance.value = 0.0
        _parentSpeed.value = 8.0
        _opponentSpeed.value = selectedOpponent.value.averageSpeedKmh
        _parentHeartRate.value = 80
        _babyHeartRate.value = 122
        _babyState.value = "Calm & Sleeping 😴"
        _vibrationState.value = "Super Smooth ✨"
        _babySoundLevelDb.value = 32
        _babyMotionG.value = 0.12
        _motionSmoothnessPct.value = 100
        _acousticPeacePct.value = 100
        _overallComfortPct.value = 100
        _activeAlert.value = null
        _aiCommentary.value = ""
        _raceWinner.value = ""
        _raceDurationSecs.value = 0
        sprintBoostValue = 0.0
        baseSpeedKmh = 10.0
        heartRateSum = 0
        babyHeartRateSum = 122
        tickCount = 0

        simulationJob = viewModelScope.launch {
            var msElapsedTime = 0L
            val opponentTarget = selectedOpponent.value

            while (_raceState.value == RaceState.RUNNING) {
                delay(100)
                msElapsedTime += 100
                _raceDurationSecs.value = (msElapsedTime / 1000).toInt()

                // Decay the manual sprint boost
                if (sprintBoostValue > 0.0) {
                    sprintBoostValue -= 0.2 // Slow decay of speed burst
                    if (sprintBoostValue < 0.0) sprintBoostValue = 0.0
                }

                // Parent's real speed
                val parentLiveSpeed = baseSpeedKmh + sprintBoostValue + Random.nextDouble(-0.5, 0.5)
                val finalParentSpeed = parentLiveSpeed.coerceIn(5.0, 42.0)
                _parentSpeed.value = finalParentSpeed

                // Opponent's live speed
                val opponentLiveSpeed = opponentTarget.averageSpeedKmh + Random.nextDouble(-1.2, 1.2)
                val finalOpponentSpeed = opponentLiveSpeed.coerceIn(5.0, 40.0)
                _opponentSpeed.value = finalOpponentSpeed

                // Advancing distances: dist = current + (speedKmh * 1000 / 3600) * deltaSecs
                // deltaSecs = 0.1 sec (100ms)
                val pDelta = (finalParentSpeed * 1000.0 / 3600.0) * 0.1
                val oDelta = (finalOpponentSpeed * 1000.0 / 3600.0) * 0.1

                _parentDistance.value = (_parentDistance.value + pDelta).coerceAtMost(selectedDistance.value)
                _opponentDistance.value = (_opponentDistance.value + oDelta).coerceAtMost(selectedDistance.value)

                // Heart rate tracking logic
                // Rising based on speed & effort
                val extraHeartEffort = (sprintBoostValue * 2.5).toInt()
                val targetHeartRate = (120 + extraHeartEffort + (finalParentSpeed * 1.5).toInt()).coerceIn(80, 195)
                // Linear blend for smooth heart rate changes
                val currentHR = _parentHeartRate.value
                val nextHR = currentHR + if (targetHeartRate > currentHR) 1 else if (targetHeartRate < currentHR) -1 else 0
                _parentHeartRate.value = nextHR

                // Baby vitals - heart rate stays normal or rises if bumpy
                val extraBabyVibeHR = if (finalParentSpeed > 22.0) 8 else 0
                val targetBabyHR = (120 + extraBabyVibeHR + Random.nextInt(-2, 3)).coerceIn(115, 142)
                _babyHeartRate.value = targetBabyHR

                // Update baby state based on speed and vibrations
                if (finalParentSpeed > 28.0) {
                    _vibrationState.value = "Championship Vibration! 🚨"
                    _babyState.value = "Giggling & Flying High! 😂🚁"
                } else if (finalParentSpeed > 18.0) {
                    _vibrationState.value = "Bumpy Race Rhythm 🌊"
                    _babyState.value = "Woke Up & Cheering! 🥳👋"
                } else if (finalParentSpeed > 12.0) {
                    _vibrationState.value = "Active Breeze 💨"
                    _babyState.value = "Curious Eyes Awake 👀"
                } else {
                    _vibrationState.value = "Super Smooth ✨"
                    _babyState.value = "Calm & Sleeping 😴"
                }

                // Threshold Alert Core Simulation
                // Simulated baby noise: sleep coos (30dB) vs crying (80dB+)
                val isFast = finalParentSpeed > 20.0
                val randomCry = (tickCount % 45 == 0 && tickCount > 10) // occasional crying spells
                val simDb = when {
                    isFast && Random.nextInt(100) < 15 -> Random.nextInt(72, 90) // high speed distress
                    randomCry -> Random.nextInt(78, 88) // spontaneous screaming
                    else -> Random.nextInt(28, 45) // calm resting coos
                }
                _babySoundLevelDb.value = simDb

                // Simulated stroller vibration acceleration in Gs (0.05G to 2.5G)
                val simG = 0.05 + (finalParentSpeed * 0.065) + Random.nextDouble(-0.06, 0.08)
                val finalG = simG.coerceIn(0.05, 3.0)
                _babyMotionG.value = finalG

                // Calculate comfort scores
                // Motion smoothness drops from 100% as vibration G-force increases
                val designSmoothness = (100 - (finalG * 35)).toInt().coerceIn(0, 100)
                _motionSmoothnessPct.value = designSmoothness

                // Acoustic peace drops from 100% as sound decibels rise
                val designAcoustics = (100 - ((simDb - 28) * 1.4)).toInt().coerceIn(0, 100)
                _acousticPeacePct.value = designAcoustics

                // Overall Comfort Rating
                val overallComfort = ((designSmoothness * 0.45) + (designAcoustics * 0.55)).toInt()
                _overallComfortPct.value = overallComfort

                // Evaluate limits against user safety thresholds
                var warningMessage: String? = null
                when {
                    overallComfort < comfortThreshold.value -> {
                        warningMessage = "⚠️ SAFE ZONE DEFICIT: Baby overall comfort score has fallen to $overallComfort% (Threshold: ${comfortThreshold.value}%)"
                    }
                    simDb > soundThreshold.value -> {
                        warningMessage = "🚨 HIGH NOISE DETECTED: Baby audio level is $simDb dB, exceeding safe limit of ${soundThreshold.value} dB"
                    }
                    finalG > motionThreshold.value -> {
                        warningMessage = "🌊 HIGH ACCELERATION: Stroller motion intensity is ${String.format("%.2f", finalG)} Gs, exceeding safe limit of ${String.format("%.2f", motionThreshold.value)} Gs"
                    }
                }

                if (warningMessage != null) {
                    if (_activeAlert.value != warningMessage) {
                        _activeAlert.value = warningMessage
                        val currentGrid = _alertHistoryList.value.toMutableList()
                        val hhmmss = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
                        currentGrid.add(0, "[$hhmmss] $warningMessage")
                        _alertHistoryList.value = currentGrid.take(15) // Keep last 15 reports
                    }
                } else {
                    // Automatically clear active alarm if metric recovers
                    _activeAlert.value = null
                }

                // Cumulative statistics
                heartRateSum += nextHR
                babyHeartRateSum += targetBabyHR
                tickCount++

                // Check finish line
                val targetDist = selectedDistance.value
                if (_parentDistance.value >= targetDist || _opponentDistance.value >= targetDist) {
                    // Somebody crossed the finish line!
                    declareWinner()
                    break
                }
            }
        }
    }

    // Rapid Sprint Boosting button action
    fun triggerSprintBoost() {
        if (_raceState.value == RaceState.RUNNING) {
            sprintBoostValue += 2.2 // Spike the speed
            if (sprintBoostValue > 30.0) {
                // Diminishing returns above 30km/h sprint burst
                sprintBoostValue = 30.0
            }
        }
    }

    private fun declareWinner() {
        val targetDist = selectedDistance.value
        val parentDistVal = _parentDistance.value
        val opponentDistVal = _opponentDistance.value

        val winner = if (parentDistVal >= targetDist && opponentDistVal >= targetDist) {
            // Both crossed in the same frame, winner has larger distance margin
            if (parentDistVal >= opponentDistVal) "YOU (Hero Parent)🏆" else selectedOpponent.value.name
        } else if (parentDistVal >= targetDist) {
            "YOU (Hero Parent)🏆"
        } else {
            selectedOpponent.value.name
        }

        _raceWinner.value = winner
        _raceState.value = RaceState.FINISHED

        // Fetch Gemini Commentator Review asynchronously
        fetchGeminiRaceReview(winner)
    }

    private fun fetchGeminiRaceReview(winner: String) {
        _aiLoading.value = true
        _aiCommentary.value = "The commentator tower is calculating official photo finishes & telemetry records..."

        viewModelScope.launch {
            val avgHR = if (tickCount > 0) heartRateSum / tickCount else 135
            val avgBHR = if (tickCount > 0) babyHeartRateSum / tickCount else 130
            val finalSpeed = _parentSpeed.value
            val duration = _raceDurationSecs.value
            val distance = selectedDistance.value
            val babyStatus = _babyState.value
            val vibeStatus = _vibrationState.value
            val opponent = selectedOpponent.value.name

            val commentary = GeminiClient.generateRaceCommentary(
                parentHeartRate = avgHR,
                parentSpeedKmh = finalSpeed,
                distanceMeters = distance,
                durationSecs = duration,
                babyState = babyStatus,
                babyHeartRate = avgBHR,
                vibrationState = vibeStatus,
                opponentName = opponent,
                winnerName = winner
            )

            _aiCommentary.value = commentary
            _aiLoading.value = false

            // Automatically persist to local Room Database
            saveRunRecordToHistory(
                title = runTitle.value,
                duration = duration,
                distance = distance,
                avgSpeed = finalSpeed,
                avgParentHR = avgHR,
                avgBabyHR = avgBHR,
                babySt = babyStatus,
                vibeSt = vibeStatus,
                opponent = opponent,
                winner = winner,
                com = commentary
            )
        }
    }

    private suspend fun saveRunRecordToHistory(
        title: String,
        duration: Int,
        distance: Double,
        avgSpeed: Double,
        avgParentHR: Int,
        avgBabyHR: Int,
        babySt: String,
        vibeSt: String,
        opponent: String,
        winner: String,
        com: String
    ) {
        try {
            val record = RunRecord(
                title = title,
                durationSecs = duration,
                distanceMeters = distance,
                avgSpeedKmh = avgSpeed,
                avgParentHeartRate = avgParentHR,
                avgBabyHeartRate = avgBabyHR,
                babyState = babySt,
                strollerVibration = vibeSt,
                opponentName = opponent,
                gameWinner = winner,
                aiCommentary = com
            )
            repository.insert(record)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun deleteHistoryRecord(id: Int) {
        viewModelScope.launch {
            repository.deleteById(id)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            repository.clear()
        }
    }

    fun cancelRaceAndReset() {
        simulationJob?.cancel()
        _raceState.value = RaceState.IDLE
        _parentDistance.value = 0.0
        _opponentDistance.value = 0.0
        _parentSpeed.value = 0.0
        _opponentSpeed.value = 0.0
        _parentHeartRate.value = 75
        _babyHeartRate.value = 120
        _babyState.value = "Calm & Sleeping 😴"
        _vibrationState.value = "Super Smooth ✨"
        _aiCommentary.value = ""
        _raceWinner.value = ""
        _raceDurationSecs.value = 0
        _babySoundLevelDb.value = 32
        _babyMotionG.value = 0.12
        _motionSmoothnessPct.value = 100
        _acousticPeacePct.value = 100
        _overallComfortPct.value = 100
        _activeAlert.value = null
    }

    fun dismissActiveAlert() {
        _activeAlert.value = null
    }

    fun clearAlertHistory() {
        _alertHistoryList.value = emptyList()
    }

    override fun onCleared() {
        super.onCleared()
        simulationJob?.cancel()
    }
}
