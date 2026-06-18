package com.example.ui

import android.app.Application
import android.content.Context
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.Mission
import com.example.data.Task
import com.example.data.TaskRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar

// Haptics supporting high-intensity soft custom vibrating patterns
enum class HapticType {
    THEME_SWITCH,
    CONSOLE_COLLAPSE,
    CONSOLE_EXPAND,
    MISSION_LOAD,
    DIRECTIVE_CYCLE_0,
    DIRECTIVE_CYCLE_1,
    DIRECTIVE_CYCLE_2,
    STRIKE_OUT,
    DELETE_ABORT,
    CARD_NAVIGATE,
    SCORE_RESET,
    SELECTOR_TAP_REPEAT,
    SELECTOR_TAP_WEEKDAY,
    SELECTOR_TAP_TIMER,
    BUTTON_CANCEL,
    TIMER_PLAY,
    TIMER_PAUSE,
    TIMER_RESET,
    TIMER_REMOVE,
    TIMER_ADD_PANEL_OPEN,
    LEVEL_UP_CELEBRATION
}

object CyberHaptics {
    fun trigger(context: Context, type: HapticType) {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? android.os.Vibrator ?: return
        if (!vibrator.hasVibrator()) return

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val effect = when (type) {
                HapticType.THEME_SWITCH -> {
                    android.os.VibrationEffect.createWaveform(
                        longArrayOf(0, 15, 120, 25),
                        intArrayOf(0, 220, 0, 255),
                        -1
                    )
                }
                HapticType.CONSOLE_COLLAPSE -> {
                    android.os.VibrationEffect.createOneShot(10, 180)
                }
                HapticType.CONSOLE_EXPAND -> {
                    android.os.VibrationEffect.createWaveform(
                        longArrayOf(0, 12, 40, 10),
                        intArrayOf(0, 150, 0, 180),
                        -1
                    )
                }
                HapticType.MISSION_LOAD -> {
                    android.os.VibrationEffect.createWaveform(
                        longArrayOf(0, 15, 50, 20, 50, 35),
                        intArrayOf(0, 180, 0, 220, 0, 255),
                        -1
                    )
                }
                HapticType.DIRECTIVE_CYCLE_0 -> {
                    android.os.VibrationEffect.createOneShot(12, 140)
                }
                HapticType.DIRECTIVE_CYCLE_1 -> {
                    android.os.VibrationEffect.createWaveform(
                        longArrayOf(0, 15, 60, 15),
                        intArrayOf(0, 200, 0, 220),
                        -1
                    )
                }
                HapticType.DIRECTIVE_CYCLE_2 -> {
                    android.os.VibrationEffect.createOneShot(32, 255)
                }
                HapticType.STRIKE_OUT -> {
                    android.os.VibrationEffect.createOneShot(18, 220)
                }
                HapticType.DELETE_ABORT -> {
                    android.os.VibrationEffect.createWaveform(
                        longArrayOf(0, 10, 30, 10, 30, 12),
                        intArrayOf(0, 255, 0, 240, 0, 255),
                        -1
                    )
                }
                HapticType.CARD_NAVIGATE -> {
                    android.os.VibrationEffect.createWaveform(
                        longArrayOf(0, 10, 35, 15),
                        intArrayOf(0, 140, 0, 200),
                        -1
                    )
                }
                HapticType.SCORE_RESET -> {
                    android.os.VibrationEffect.createWaveform(
                        longArrayOf(0, 20, 50, 20, 50, 20, 150, 40),
                        intArrayOf(0, 220, 0, 180, 0, 140, 0, 100),
                        -1
                    )
                }
                HapticType.SELECTOR_TAP_REPEAT -> {
                    android.os.VibrationEffect.createOneShot(15, 170)
                }
                HapticType.SELECTOR_TAP_WEEKDAY -> {
                    android.os.VibrationEffect.createOneShot(8, 120)
                }
                HapticType.SELECTOR_TAP_TIMER -> {
                    android.os.VibrationEffect.createOneShot(11, 150)
                }
                HapticType.BUTTON_CANCEL -> {
                    android.os.VibrationEffect.createWaveform(
                        longArrayOf(0, 10, 50, 8),
                        intArrayOf(0, 160, 0, 100),
                        -1
                    )
                }
                HapticType.TIMER_PLAY -> {
                    android.os.VibrationEffect.createWaveform(
                        longArrayOf(0, 15, 100, 20),
                        intArrayOf(0, 240, 0, 200),
                        -1
                    )
                }
                HapticType.TIMER_PAUSE -> {
                    android.os.VibrationEffect.createOneShot(25, 190)
                }
                HapticType.TIMER_RESET -> {
                    android.os.VibrationEffect.createWaveform(
                        longArrayOf(0, 8, 80, 8),
                        intArrayOf(0, 130, 0, 130),
                        -1
                    )
                }
                HapticType.TIMER_REMOVE -> {
                    android.os.VibrationEffect.createWaveform(
                        longArrayOf(0, 10, 30, 12),
                        intArrayOf(0, 180, 0, 120),
                        -1
                    )
                }
                HapticType.TIMER_ADD_PANEL_OPEN -> {
                    android.os.VibrationEffect.createOneShot(15, 160)
                }
                HapticType.LEVEL_UP_CELEBRATION -> {
                    android.os.VibrationEffect.createWaveform(
                        longArrayOf(0, 30, 80, 30, 80, 30, 150, 60, 100, 80),
                        intArrayOf(0, 180, 0, 200, 0, 220, 0, 255, 0, 255),
                        -1
                    )
                }
            }
            vibrator.vibrate(effect)
        } else {
            val pattern = when (type) {
                HapticType.THEME_SWITCH -> longArrayOf(0, 15, 120, 25)
                HapticType.CONSOLE_COLLAPSE -> longArrayOf(0, 10)
                HapticType.CONSOLE_EXPAND -> longArrayOf(0, 12, 40, 10)
                HapticType.MISSION_LOAD -> longArrayOf(0, 15, 50, 20, 50, 35)
                HapticType.DIRECTIVE_CYCLE_0 -> longArrayOf(0, 12)
                HapticType.DIRECTIVE_CYCLE_1 -> longArrayOf(0, 15, 60, 15)
                HapticType.DIRECTIVE_CYCLE_2 -> longArrayOf(0, 32)
                HapticType.STRIKE_OUT -> longArrayOf(0, 18)
                HapticType.DELETE_ABORT -> longArrayOf(0, 10, 30, 10, 30, 12)
                HapticType.CARD_NAVIGATE -> longArrayOf(0, 10, 35, 15)
                HapticType.SCORE_RESET -> longArrayOf(0, 20, 50, 20, 50, 20, 150, 40)
                HapticType.SELECTOR_TAP_REPEAT -> longArrayOf(0, 15)
                HapticType.SELECTOR_TAP_WEEKDAY -> longArrayOf(0, 8)
                HapticType.SELECTOR_TAP_TIMER -> longArrayOf(0, 11)
                HapticType.BUTTON_CANCEL -> longArrayOf(0, 10, 50, 8)
                HapticType.TIMER_PLAY -> longArrayOf(0, 15, 100, 20)
                HapticType.TIMER_PAUSE -> longArrayOf(0, 25)
                HapticType.TIMER_RESET -> longArrayOf(0, 8, 80, 8)
                HapticType.TIMER_REMOVE -> longArrayOf(0, 10, 30, 12)
                HapticType.TIMER_ADD_PANEL_OPEN -> longArrayOf(0, 15)
                HapticType.LEVEL_UP_CELEBRATION -> longArrayOf(0, 30, 80, 30, 80, 30, 150, 60, 100, 80)
            }
            @Suppress("DEPRECATION")
            vibrator.vibrate(pattern, -1)
        }
    }
}

// VM supporting multiple missions with auto setup templates
@OptIn(ExperimentalCoroutinesApi::class)
class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TaskRepository
    val allMissions: StateFlow<List<Mission>>
    
    private val _selectedMissionId = MutableStateFlow<Int?>(null)
    val selectedMissionId: StateFlow<Int?> = _selectedMissionId.asStateFlow()

    // Completely reactive mapping preventing flow locks during card selection
    val tasksForSelectedMission: StateFlow<List<Task>> = _selectedMissionId
        .flatMapLatest { id ->
            if (id != null) {
                repository.getTasksForMission(id)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val sharedPrefs = application.getSharedPreferences("mission_tracker_prefs", Context.MODE_PRIVATE)

    // Mode: true = PS5 Neon Dark (default), false = PS5 Tech Light
    private val _isDarkMode = MutableStateFlow(sharedPrefs.getBoolean("is_dark_mode", true))
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    // Console Directive drafts
    private val _draftChecklist = MutableStateFlow(sharedPrefs.getString("draft_checklist", DEFAULT_CHECKLIST) ?: DEFAULT_CHECKLIST)
    val draftChecklist: StateFlow<String> = _draftChecklist.asStateFlow()

    // Persistent Scores and gaming Levels (1000 XP per rank scale)
    private val _totalScore = MutableStateFlow(sharedPrefs.getInt("total_score", 0))
    val totalScore: StateFlow<Int> = _totalScore.asStateFlow()

    val currentLevel: StateFlow<Int> = _totalScore
        .map { (it / 1000) + 1 }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 1
        )

    val currentLevelProgress: StateFlow<Float> = _totalScore
        .map { (it % 1000).toFloat() / 1000f }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0f
        )

    // Live Transient Timers memory map (taskId -> remainingSeconds)
    private val _activeTimers = MutableStateFlow<Map<Int, Int>>(emptyMap())
    val activeTimers: StateFlow<Map<Int, Int>> = _activeTimers.asStateFlow()

    private val _runningTimerIds = MutableStateFlow<Set<Int>>(emptySet())
    val runningTimerIds: StateFlow<Set<Int>> = _runningTimerIds.asStateFlow()

    init {
        val taskDao = AppDatabase.getDatabase(application).taskDao()
        repository = TaskRepository(taskDao)
        allMissions = repository.allMissions.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

        // Prepopulate missions if none are present in Room database
        viewModelScope.launch {
            allMissions.collect { missions ->
                if (missions.isNotEmpty()) {
                    val currentId = _selectedMissionId.value
                    if (currentId == null || !missions.any { it.id == currentId }) {
                        _selectedMissionId.value = missions.first().id
                    }
                    // Trigger daily repeat checking
                    checkAndResetRepeatingMissions(missions)
                } else {
                    prepopulateDefaultMissions()
                }
            }
        }

        // Live Timer Countdown Loop Tick
        viewModelScope.launch {
            while (true) {
                delay(1000L)
                val runningIds = _runningTimerIds.value
                if (runningIds.isNotEmpty()) {
                    val currentMap = _activeTimers.value.toMutableMap()
                    val newRunningIds = runningIds.toMutableSet()
                    
                    runningIds.forEach { taskId ->
                        val remaining = currentMap[taskId] ?: 0
                        if (remaining > 1) {
                            currentMap[taskId] = remaining - 1
                        } else {
                            currentMap[taskId] = 0
                            newRunningIds.remove(taskId)
                            // Auto save expired timer
                            viewModelScope.launch {
                                saveTimerElapsed(taskId, 0)
                            }
                        }
                    }
                    _activeTimers.value = currentMap
                    _runningTimerIds.value = newRunningIds
                }
            }
        }

        // Synchronize timers when selected checklist updates
        viewModelScope.launch {
            tasksForSelectedMission.collect { tasksList ->
                val currentMap = _activeTimers.value.toMutableMap()
                tasksList.forEach { task ->
                    if (task.timerDurationSeconds > 0 && !currentMap.containsKey(task.id)) {
                        val remaining = maxOf(0, task.timerDurationSeconds - task.timerElapsedSeconds)
                        currentMap[task.id] = remaining
                    }
                }
                _activeTimers.value = currentMap
            }
        }
    }

    private fun checkAndResetRepeatingMissions(missions: List<Mission>) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            missions.forEach { mission ->
                if (shouldResetMission(mission, now)) {
                    repository.resetTasksForMission(mission.id)
                    repository.updateMission(mission.copy(lastResetTimestamp = now))
                }
            }
        }
    }

    private fun shouldResetMission(mission: Mission, now: Long): Boolean {
        if (mission.repeatType == "NONE") return false
        val lastReset = mission.lastResetTimestamp
        if (lastReset == 0L) return true // brand new reset initial sync

        val calNow = Calendar.getInstance()
        calNow.timeInMillis = now

        val calLast = Calendar.getInstance()
        calLast.timeInMillis = lastReset

        val dayNow = calNow.get(Calendar.DAY_OF_YEAR)
        val yearNow = calNow.get(Calendar.YEAR)
        
        val dayLast = calLast.get(Calendar.DAY_OF_YEAR)
        val yearLast = calLast.get(Calendar.YEAR)

        if (yearNow != yearLast || dayNow != dayLast) {
            if (mission.repeatType == "DAILY") {
                return true
            }
            if (mission.repeatType == "WEEKDAY") {
                val todayDayOfWeek = calNow.get(Calendar.DAY_OF_WEEK)
                return todayDayOfWeek == mission.repeatDayOfWeek
            }
        }
        return false
    }

    private suspend fun prepopulateDefaultMissions() {
        val firstId = repository.insertMission(
            Mission(
                title = "ASTRO'S PLAYROOM CONSOLE",
                repeatType = "DAILY",
                lastResetTimestamp = System.currentTimeMillis()
            )
        ).toInt()
        repository.insertTasks(
            listOf(
                Task(missionId = firstId, title = "Reboot CPU Plasma Core Sensors", status = 0, orderIndex = 0, timerDurationSeconds = 120),
                Task(missionId = firstId, title = "Collect Golden Chipset Memory Unit", status = 1, orderIndex = 1),
                Task(missionId = firstId, title = "Recalibrate dual-sense haptic stabilizers", status = 2, orderIndex = 2),
                Task(missionId = firstId, title = "Defeat the giant GPU rendering construct", status = 0, orderIndex = 3, timerDurationSeconds = 300)
            )
        )

        val secondId = repository.insertMission(
            Mission(
                title = "SPIDER-MAN MANHATTAN GRID",
                repeatType = "WEEKDAY",
                repeatDayOfWeek = Calendar.FRIDAY,
                lastResetTimestamp = System.currentTimeMillis()
            )
        ).toInt()
        repository.insertTasks(
            listOf(
                Task(missionId = secondId, title = "Sync neural-mesh web shooters", status = 1, orderIndex = 0),
                Task(missionId = secondId, title = "Isolate radioactive Oscorp mainframe signals", status = 0, orderIndex = 1, timerDurationSeconds = 60),
                Task(missionId = secondId, title = "Locate underground sand crystal core", status = 2, orderIndex = 2),
                Task(missionId = secondId, title = "Execute point-to-point web catapult launch", status = 0, orderIndex = 3)
            )
        )
        _selectedMissionId.value = firstId
    }

    fun toggleTheme() {
        viewModelScope.launch {
            val newValue = !_isDarkMode.value
            _isDarkMode.value = newValue
            sharedPrefs.edit().putBoolean("is_dark_mode", newValue).apply()
        }
    }

    fun selectMission(id: Int) {
        _selectedMissionId.value = id
    }

    // Custom inline parser to allow easy countdown setting e.g. "Mission task A [5m]" or "[120s]"
    private fun parseTimerDuration(text: String): Int {
        val pattern = "\\[(\\d+)\\s*(m|min|s|sec)\\]".toRegex(RegexOption.IGNORE_CASE)
        val matchResult = pattern.find(text) ?: return 0
        val amount = matchResult.groupValues[1].toIntOrNull() ?: return 0
        val unit = matchResult.groupValues[2].lowercase()
        return if (unit.startsWith("m")) amount * 60 else amount
    }

    fun createMission(
        title: String,
        textChecklist: String,
        repeatType: String = "NONE",
        repeatDayOfWeek: Int = Calendar.MONDAY,
        defaultTimerSeconds: Int = 0
    ) {
        viewModelScope.launch {
            val finalTitle = title.trim().ifEmpty { "CODENAME MISSION #${(System.currentTimeMillis() % 1000)}" }
            val newMissionId = repository.insertMission(
                Mission(
                    title = finalTitle,
                    repeatType = repeatType,
                    repeatDayOfWeek = repeatDayOfWeek,
                    lastResetTimestamp = System.currentTimeMillis()
                )
            ).toInt()

            val lines = parseChecklist(textChecklist)
            if (lines.isNotEmpty()) {
                val tasks = lines.mapIndexed { index, text ->
                    val inlineTimer = parseTimerDuration(text)
                    val timerSecs = if (inlineTimer > 0) inlineTimer else defaultTimerSeconds
                    val cleanText = text.replace("\\[(\\d+)\\s*(m|min|s|sec)\\]".toRegex(RegexOption.IGNORE_CASE), "").trim()
                    
                    Task(
                        missionId = newMissionId,
                        title = cleanText,
                        status = 0,
                        orderIndex = index,
                        timerDurationSeconds = timerSecs,
                        timerElapsedSeconds = 0
                    )
                }
                repository.insertTasks(tasks)
            } else {
                repository.insertTask(
                    Task(
                        missionId = newMissionId,
                        title = "Initialize tactical mission variables",
                        status = 0,
                        orderIndex = 0,
                        timerDurationSeconds = defaultTimerSeconds,
                        timerElapsedSeconds = 0
                    )
                )
            }
            _selectedMissionId.value = newMissionId
        }
    }

    fun deleteMission(id: Int) {
        viewModelScope.launch {
            val currentList = allMissions.value
            val missionToDelete = currentList.find { it.id == id } ?: return@launch
            repository.deleteMission(missionToDelete)

            // Auto navigate to another mission if present
            val remaining = currentList.filter { it.id != id }
            if (remaining.isNotEmpty()) {
                _selectedMissionId.value = remaining.first().id
            } else {
                _selectedMissionId.value = null
            }
        }
    }

    fun cycleTaskStatus(task: Task) {
        viewModelScope.launch {
            val nextStatus = when (task.status) {
                0 -> 1 // 0% -> 50% (Active)
                1 -> 2 // 50% -> 100% (Concluded)
                else -> 0 // 100% -> 0% (Dormant)
            }

            if (nextStatus == 2) {
                // Timer evaluation
                val remaining = _activeTimers.value[task.id] ?: 0
                val wasTimerActive = task.timerDurationSeconds > 0
                val withinTime = wasTimerActive && remaining > 0

                val points = if (withinTime) {
                    250 // Speed objective bonus
                } else {
                    100 // Standard target score
                }
                addScore(points)

                // Freeze timer & save elapsed
                val currentRunning = _runningTimerIds.value.toMutableSet()
                if (currentRunning.contains(task.id)) {
                    currentRunning.remove(task.id)
                    _runningTimerIds.value = currentRunning
                }
                val elapsed = if (wasTimerActive) maxOf(0, task.timerDurationSeconds - remaining) else 0
                repository.updateTask(task.copy(status = nextStatus, timerElapsedSeconds = elapsed))
            } else {
                repository.updateTask(task.copy(status = nextStatus))
            }
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    fun saveDraftChecklist(text: String) {
        _draftChecklist.value = text
        sharedPrefs.edit().putString("draft_checklist", text).apply()
    }

    // Interactive Timer Management
    fun toggleTimer(taskId: Int) {
        val currentRunning = _runningTimerIds.value.toMutableSet()
        if (currentRunning.contains(taskId)) {
            currentRunning.remove(taskId)
            saveTimerElapsed(taskId)
        } else {
            currentRunning.add(taskId)
        }
        _runningTimerIds.value = currentRunning
    }

    fun resetTimer(task: Task) {
        val currentRunning = _runningTimerIds.value.toMutableSet()
        currentRunning.remove(task.id)
        _runningTimerIds.value = currentRunning

        val currentMap = _activeTimers.value.toMutableMap()
        currentMap[task.id] = task.timerDurationSeconds
        _activeTimers.value = currentMap

        viewModelScope.launch {
            repository.updateTask(task.copy(timerElapsedSeconds = 0))
        }
    }

    fun updateTaskTimer(task: Task, durationSeconds: Int) {
        viewModelScope.launch {
            repository.updateTask(
                task.copy(
                    timerDurationSeconds = durationSeconds,
                    timerElapsedSeconds = 0
                )
            )
            val currentMap = _activeTimers.value.toMutableMap()
            currentMap[task.id] = durationSeconds
            _activeTimers.value = currentMap

            val currentRunning = _runningTimerIds.value.toMutableSet()
            currentRunning.remove(task.id)
            _runningTimerIds.value = currentRunning
        }
    }

    private fun saveTimerElapsed(taskId: Int, fallbackValue: Int? = null) {
        viewModelScope.launch {
            val task = tasksForSelectedMission.value.find { it.id == taskId } ?: return@launch
            val remaining = fallbackValue ?: _activeTimers.value[taskId] ?: task.timerDurationSeconds
            val elapsed = maxOf(0, task.timerDurationSeconds - remaining)
            repository.updateTask(task.copy(timerElapsedSeconds = elapsed))
        }
    }

    private fun addScore(points: Int) {
        val newScore = _totalScore.value + points
        _totalScore.value = newScore
        sharedPrefs.edit().putInt("total_score", newScore).apply()
    }

    fun resetScore() {
        _totalScore.value = 0
        sharedPrefs.edit().putInt("total_score", 0).apply()
    }

    private fun parseChecklist(text: String): List<String> {
        return text.lineSequence()
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .map { line ->
                val regex = "^(?:\\d+[\\.\\)]\\s*|[-*+•]\\s*|\\[[xX ]?\\]\\s*)+".toRegex()
                val cleaned = line.replace(regex, "")
                cleaned.trim()
            }
            .filter { it.isNotEmpty() }
            .toList()
    }

    companion object {
        const val DEFAULT_CHECKLIST = """1. Deploy Astro CPU plasma core scanners [2m]
2. Secure sub-level network server room [5m]
* Clean structural ventilation filter arrays [120s]
- Extract core logic key to dual-sense HUD"""
    }
}

class TaskViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TaskViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

// PS5 Futuristic UI palette
object PS5Theme {
    val ObsidianDark = Color(0xFF04060E) // PS5 dynamic dashboard background
    val TranslucentCardDark = Color(0xFF131726) // Activity base card
    val AccentCosmicBlue = Color(0xFF0072FF) // Glowing cosmic blue laser
    val AccentNeonPink = Color(0xFFFF007F) // Secondary magenta laser glow
    val SlateGlow = Color(0xFF263354)
    val ActiveHighlight = Color(0xFF00D2FF) // Dynamic blue highlight

    val LightBackdrop = Color(0xFFECEFF5)
    val LightCard = Color(0xFFFFFFFF)
    val LightBorder = Color(0xFFD0D7E4)
    val LightText = Color(0xFF0C101D)

    val PS5White = Color(0xFFF5F5FA)
    val PS5MutedText = Color(0xFF7B88A0)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MissionTrackerScreen(viewModel: TaskViewModel) {
    val context = LocalContext.current
    val isDarkMode = isSystemInDarkTheme()
    val missions by viewModel.allMissions.collectAsState()
    val selectedMissionId by viewModel.selectedMissionId.collectAsState()
    val activeTasks by viewModel.tasksForSelectedMission.collectAsState()
    val draftChecklist by viewModel.draftChecklist.collectAsState()

    // Persistent XP and game stats
    val totalScore by viewModel.totalScore.collectAsState()
    val currentLevel by viewModel.currentLevel.collectAsState()
    val currentLevelProgress by viewModel.currentLevelProgress.collectAsState()

    var previousLevel by remember { mutableIntStateOf(currentLevel) }
    LaunchedEffect(currentLevel) {
        if (currentLevel > previousLevel) {
            CyberHaptics.trigger(context, HapticType.LEVEL_UP_CELEBRATION)
        }
        previousLevel = currentLevel
    }

    // Active Timers State
    val activeTimers by viewModel.activeTimers.collectAsState()
    val runningTimerIds by viewModel.runningTimerIds.collectAsState()

    // Determine colors based on PS5 theme guidelines
    val backgroundBase = if (isDarkMode) PS5Theme.ObsidianDark else PS5Theme.LightBackdrop
    val cardBackground = if (isDarkMode) PS5Theme.TranslucentCardDark else PS5Theme.LightCard
    val borderBase = if (isDarkMode) PS5Theme.SlateGlow else PS5Theme.LightBorder
    val textBase = if (isDarkMode) PS5Theme.PS5White else PS5Theme.LightText
    val textMuted = if (isDarkMode) PS5Theme.PS5MutedText else PS5Theme.PS5MutedText.copy(alpha = 0.8f)
    val primaryBlue = PS5Theme.AccentCosmicBlue
    val brightCyan = PS5Theme.ActiveHighlight

    var showConfigPanel by remember { mutableStateOf(false) }
    var inputMissionTitle by remember { mutableStateOf("") }
    var inputChecklistText by remember { mutableStateOf(draftChecklist) }

    // Repetition options
    var inputRepeatType by remember { mutableStateOf("NONE") }
    var inputRepeatDayOfWeek by remember { mutableIntStateOf(Calendar.MONDAY) }

    // Objective timer defaults
    var inputDefaultTimerMinutes by remember { mutableIntStateOf(0) }

    // Synchronize checklist text draft
    LaunchedEffect(draftChecklist) {
        if (inputChecklistText != draftChecklist) {
            inputChecklistText = draftChecklist
        }
    }

    // Active mission completion calculation
    val activeMissionProgress = remember(activeTasks) {
        if (activeTasks.isEmpty()) 0f
        else {
            val total = activeTasks.sumOf { it.progressPercent }
            total.toFloat() / (activeTasks.size * 100f)
        }
    }

    val animatedProgressBar by animateFloatAsState(
        targetValue = activeMissionProgress,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "PS5ProgressBarSpeed"
    )

    val animatedRankProgress by animateFloatAsState(
        targetValue = currentLevelProgress,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "PS5LevelBarSpeed"
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = backgroundBase,
        topBar = {
            // Elegant SONY PS5 Dashboard Status Bar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = if (isDarkMode) {
                                listOf(Color(0xFF03050B), backgroundBase)
                            } else {
                                listOf(Color(0xFFDFE4EE), backgroundBase)
                            }
                        )
                    )
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "COMMAND HUB CONSOLE",
                            style = TextStyle(
                                fontFamily = FontFamily.SansSerif,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                color = textMuted,
                                letterSpacing = 1.2.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "TACTICAL DIRECTIVE TRACKER",
                            style = TextStyle(
                                fontFamily = FontFamily.SansSerif,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 20.sp,
                                color = textBase,
                                letterSpacing = 1.8.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Gamified Rank Level Indicator Rows Custom Designed Conforming to PS5
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "🏆 PROFILE RANK",
                            style = TextStyle(
                                fontFamily = FontFamily.SansSerif,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 11.sp,
                                color = brightCyan,
                                letterSpacing = 0.5.sp
                            )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "LVL $currentLevel",
                            style = TextStyle(
                                fontFamily = FontFamily.SansSerif,
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                color = textBase
                            )
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "$totalScore XP",
                            style = TextStyle(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = textBase
                            )
                        )
                        Box(
                            modifier = Modifier
                                .border(BorderStroke(1.dp, PS5Theme.AccentNeonPink.copy(alpha = 0.4f)), RoundedCornerShape(4.dp))
                                .clickable {
                                    CyberHaptics.trigger(context, HapticType.SCORE_RESET)
                                    viewModel.resetScore()
                                }
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text(
                                "RESET", 
                                fontSize = 8.sp, 
                                fontWeight = FontWeight.Bold,
                                color = PS5Theme.AccentNeonPink,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Level Progress Line (Neon Pink)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .clip(RoundedCornerShape(1.5.dp))
                        .background(if (isDarkMode) Color(0xFF141A29) else Color(0xFFD6DBE4))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(animatedRankProgress)
                            .background(PS5Theme.AccentNeonPink)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Active Mission Progress bar (Glowing Blue)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🎯 CURRENT CARD COMPLETION",
                        style = TextStyle(
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            color = textMuted
                        )
                    )

                    Text(
                        text = "PROGRESS: ${(animatedProgressBar * 100).toInt()}%",
                        style = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = primaryBlue
                        )
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .clip(RoundedCornerShape(2.5.dp))
                        .background(if (isDarkMode) Color(0xFF141A29) else Color(0xFFD6DBE4))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(animatedProgressBar)
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(primaryBlue, brightCyan)
                                )
                            )
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .navigationBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Adaptive layout limiting broad size stretching
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .widthIn(max = 620.dp)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {

                    // Collapsible PS5 Add Mission Overlay Console Panel
                    AnimatedVisibility(
                        visible = showConfigPanel,
                        enter = expandVertically(animationSpec = spring(stiffness = Spring.StiffnessMedium)) + fadeIn(),
                        exit = shrinkVertically(animationSpec = spring(stiffness = Spring.StiffnessMedium)) + fadeOut()
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(BorderStroke(1.dp, borderBase), RoundedCornerShape(16.dp)),
                            colors = CardDefaults.cardColors(containerColor = cardBackground),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                item {
                                    Text(
                                        text = "INITIALIZE NEW OPERATIONAL CARD",
                                        style = TextStyle(
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontFamily = FontFamily.SansSerif,
                                            color = brightCyan,
                                            letterSpacing = 1.sp
                                        )
                                    )
                                }

                                item {
                                    OutlinedTextField(
                                        value = inputMissionTitle,
                                        onValueChange = { inputMissionTitle = it },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("mission_title_input"),
                                        label = { Text("MISSION / CARD NAME") },
                                        placeholder = { Text("e.g., God of War: Side Objective") },
                                        singleLine = true,
                                        textStyle = TextStyle(color = textBase, fontSize = 13.sp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = primaryBlue,
                                            unfocusedBorderColor = borderBase,
                                            focusedLabelColor = primaryBlue,
                                            cursorColor = primaryBlue,
                                            focusedContainerColor = backgroundBase,
                                            unfocusedContainerColor = backgroundBase.copy(0.4f)
                                        ),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                }

                                item {
                                    OutlinedTextField(
                                        value = inputChecklistText,
                                        onValueChange = {
                                            inputChecklistText = it
                                            viewModel.saveDraftChecklist(it)
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(95.dp)
                                            .testTag("mission_text_input"),
                                        label = { Text("MISSION OBJECTIVES (Type line-by-line)") },
                                        placeholder = { Text("Complete target task A [5m]\nSolve laser security core [2m]") },
                                        textStyle = TextStyle(fontFamily = FontFamily.Monospace, color = textBase, fontSize = 12.sp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = primaryBlue,
                                            unfocusedBorderColor = borderBase,
                                            focusedLabelColor = primaryBlue,
                                            cursorColor = primaryBlue,
                                            focusedContainerColor = backgroundBase,
                                            unfocusedContainerColor = backgroundBase.copy(0.4f)
                                        ),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                }

                                // REPETITION ENGINE SELECTOR
                                item {
                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text(
                                            text = "REPETITION CYCLE",
                                            style = TextStyle(
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = brightCyan,
                                                letterSpacing = 0.5.sp
                                            )
                                        )
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            listOf("NONE", "DAILY", "WEEKDAY").forEach { type ->
                                                val isSelected = inputRepeatType == type
                                                Box(
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .height(30.dp)
                                                        .clip(RoundedCornerShape(6.dp))
                                                        .background(if (isSelected) primaryBlue else backgroundBase)
                                                        .border(BorderStroke(1.dp, if (isSelected) primaryBlue else borderBase), RoundedCornerShape(6.dp))
                                                        .clickable {
                                                            CyberHaptics.trigger(context, HapticType.SELECTOR_TAP_REPEAT)
                                                            inputRepeatType = type
                                                        },
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        text = when (type) {
                                                            "NONE" -> "OMIT"
                                                            "DAILY" -> "DAILY"
                                                            "WEEKDAY" -> "WEEKDAY"
                                                            else -> type
                                                        },
                                                        color = if (isSelected) Color.White else textMuted,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 10.sp
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                // WEEKDAYS DOT ROW SELECTOR
                                item {
                                    AnimatedVisibility(visible = inputRepeatType == "WEEKDAY") {
                                        Column(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text(
                                                text = "RECURRING SPECIFIC DAY OF WEEK",
                                                style = TextStyle(fontSize = 10.sp, color = textMuted, fontWeight = FontWeight.Bold)
                                            )
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                // M=2, T=3, W=4, T=5, F=6, S=7, S=1
                                                val weekdays = listOf(
                                                    "M" to Calendar.MONDAY,
                                                    "T" to Calendar.TUESDAY,
                                                    "W" to Calendar.WEDNESDAY,
                                                    "T" to Calendar.THURSDAY,
                                                    "F" to Calendar.FRIDAY,
                                                    "S" to Calendar.SATURDAY,
                                                    "S" to Calendar.SUNDAY
                                                )
                                                weekdays.forEach { (label, calValue) ->
                                                    val isSelected = inputRepeatDayOfWeek == calValue
                                                    Box(
                                                        modifier = Modifier
                                                            .size(32.dp)
                                                            .clip(CircleShape)
                                                            .background(if (isSelected) PS5Theme.AccentNeonPink else backgroundBase)
                                                            .border(BorderStroke(1.dp, if (isSelected) PS5Theme.AccentNeonPink else borderBase), CircleShape)
                                                            .clickable {
                                                                CyberHaptics.trigger(context, HapticType.SELECTOR_TAP_WEEKDAY)
                                                                inputRepeatDayOfWeek = calValue
                                                            },
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Text(
                                                            text = label,
                                                            color = if (isSelected) Color.White else textBase,
                                                            fontWeight = FontWeight.Bold,
                                                            fontSize = 10.sp
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                // DEFAULT COUNTDOWN ASSIGNMENT
                                item {
                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text(
                                            text = "DEFAULT OBJECTIVE TIMERDURATION (APPLIES IF OMITTED IN LINE)",
                                            style = TextStyle(
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = brightCyan,
                                                letterSpacing = 0.5.sp
                                            )
                                        )
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            listOf(0, 1, 2, 5, 10, 25).forEach { mins ->
                                                val isSelected = inputDefaultTimerMinutes == mins
                                                Box(
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .height(28.dp)
                                                        .clip(RoundedCornerShape(6.dp))
                                                        .background(if (isSelected) brightCyan else backgroundBase)
                                                        .border(BorderStroke(1.dp, if (isSelected) brightCyan else borderBase), RoundedCornerShape(6.dp))
                                                        .clickable {
                                                            CyberHaptics.trigger(context, HapticType.SELECTOR_TAP_TIMER)
                                                            inputDefaultTimerMinutes = mins
                                                        },
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        text = if (mins == 0) "NONE" else "${mins}M",
                                                        color = if (isSelected) Color.Black else textBase,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 9.sp,
                                                        fontFamily = FontFamily.Monospace
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                // COMMAND ACTIONS BUTTONS
                                item {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Button(
                                            onClick = {
                                                viewModel.createMission(
                                                    inputMissionTitle,
                                                    inputChecklistText,
                                                    repeatType = inputRepeatType,
                                                    repeatDayOfWeek = inputRepeatDayOfWeek,
                                                    defaultTimerSeconds = inputDefaultTimerMinutes * 60
                                                )
                                                inputMissionTitle = ""
                                                showConfigPanel = false
                                                CyberHaptics.trigger(context, HapticType.MISSION_LOAD)
                                            },
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(42.dp)
                                                .testTag("submit_mission_button"),
                                            colors = ButtonDefaults.buttonColors(containerColor = primaryBlue),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text(
                                                "LAUNCH ACTIVITY CARD",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp
                                            )
                                        }

                                        OutlinedButton(
                                            onClick = {
                                                CyberHaptics.trigger(context, HapticType.BUTTON_CANCEL)
                                                showConfigPanel = false
                                            },
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.height(42.dp),
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = textBase),
                                            border = BorderStroke(1.dp, borderBase)
                                        ) {
                                            Text("CANCEL", fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Clean, professional Activity Cards header with inline action
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp, start = 4.dp, end = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ACTIVITY CARDS",
                            style = TextStyle(
                                fontFamily = FontFamily.SansSerif,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 10.sp,
                                color = textMuted,
                                letterSpacing = 1.3.sp
                            )
                        )

                        TextButton(
                            onClick = {
                                CyberHaptics.trigger(context, HapticType.CONSOLE_COLLAPSE)
                                showConfigPanel = !showConfigPanel
                            },
                            modifier = Modifier.testTag("show_config_panel"),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Icon(
                                imageVector = if (showConfigPanel) Icons.Default.Close else Icons.Default.Add,
                                contentDescription = null,
                                tint = primaryBlue,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (showConfigPanel) "CLOSE" else "NEW ACTIVITY",
                                style = TextStyle(
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = primaryBlue
                                )
                            )
                        }
                    }

                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("missions_row"),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        items(items = missions, key = { it.id }) { mission ->
                            val isSelected = mission.id == selectedMissionId
                            PS5ActivityCard(
                                mission = mission,
                                isSelected = isSelected,
                                isDarkMode = isDarkMode,
                                borderBase = borderBase,
                                cardBackground = cardBackground,
                                primaryBlue = primaryBlue,
                                brightCyan = brightCyan,
                                textBase = textBase,
                                textMuted = textMuted,
                                onSelect = {
                                    CyberHaptics.trigger(context, HapticType.CARD_NAVIGATE)
                                    viewModel.selectMission(mission.id)
                                },
                                onDelete = {
                                    CyberHaptics.trigger(context, HapticType.DELETE_ABORT)
                                    viewModel.deleteMission(mission.id)
                                }
                            )
                        }
                    }

                    // Divider segment
                    Spacer(modifier = Modifier.height(2.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ACTIVE MISSION OBJECTIVES",
                            style = TextStyle(
                                fontFamily = FontFamily.SansSerif,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                color = textMuted,
                                letterSpacing = 1.2.sp
                            )
                        )

                        Text(
                            text = "OBJECTIVES: ${activeTasks.size}",
                            style = TextStyle(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                color = brightCyan,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    // Empty screen banner fallback
                    if (activeTasks.isEmpty()) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                                .border(BorderStroke(1.dp, borderBase), RoundedCornerShape(16.dp)),
                            colors = CardDefaults.cardColors(containerColor = cardBackground),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = brightCyan,
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "SELECT OR CREATE CARD TO START TRACKERS",
                                    style = TextStyle(
                                        fontFamily = FontFamily.SansSerif,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = textBase
                                    )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Create custom game activity cards or use default templates to trigger live countdown parameters.",
                                    style = TextStyle(fontSize = 11.sp, color = textMuted, textAlign = TextAlign.Center),
                                    modifier = Modifier.padding(horizontal = 14.dp)
                                )
                            }
                        }
                    } else {
                        // Current checklist items
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .testTag("tasks_list"),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(bottom = 20.dp)
                        ) {
                            items(items = activeTasks, key = { it.id }) { task ->
                                val remaining = activeTimers[task.id] ?: task.timerDurationSeconds
                                val isRunning = runningTimerIds.contains(task.id)

                                TaskItemCard(
                                    task = task,
                                    isDarkMode = isDarkMode,
                                    cardBackground = cardBackground,
                                    borderBase = borderBase,
                                    primaryCyan = brightCyan,
                                    accentPink = PS5Theme.AccentNeonPink,
                                    textBase = textBase,
                                    textMuted = textMuted,
                                    remainingSeconds = remaining,
                                    isTimerRunning = isRunning,
                                    onToggleTimer = {
                                        val hapticType = if (isRunning) HapticType.TIMER_PAUSE else HapticType.TIMER_PLAY
                                        CyberHaptics.trigger(context, hapticType)
                                        viewModel.toggleTimer(task.id)
                                    },
                                    onResetTimer = {
                                        CyberHaptics.trigger(context, HapticType.TIMER_RESET)
                                        viewModel.resetTimer(task)
                                    },
                                    onEditTimer = { secs ->
                                        val hapticType = if (secs == 0) HapticType.TIMER_REMOVE else HapticType.SELECTOR_TAP_TIMER
                                        CyberHaptics.trigger(context, hapticType)
                                        viewModel.updateTaskTimer(task, secs)
                                    },
                                    onCycleStatus = {
                                        val hapticType = when (task.status) {
                                            0 -> HapticType.DIRECTIVE_CYCLE_1
                                            1 -> HapticType.DIRECTIVE_CYCLE_2
                                            else -> HapticType.DIRECTIVE_CYCLE_0
                                        }
                                        CyberHaptics.trigger(context, hapticType)
                                        viewModel.cycleTaskStatus(task)
                                    },
                                    onDelete = {
                                        CyberHaptics.trigger(context, HapticType.STRIKE_OUT)
                                        viewModel.deleteTask(task)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// Gorgeous PS5 Themed Horizontal Card representing dynamic PlayStation active game/mission units
@Composable
fun PS5ActivityCard(
    mission: Mission,
    isSelected: Boolean,
    isDarkMode: Boolean,
    borderBase: Color,
    cardBackground: Color,
    primaryBlue: Color,
    brightCyan: Color,
    textBase: Color,
    textMuted: Color,
    onSelect: () -> Unit,
    onDelete: () -> Unit
) {
    // Glowing border highlight color for selecting high contrast activities
    val animatedBorderColor by animateColorAsState(
        targetValue = if (isSelected) brightCyan else borderBase,
        animationSpec = tween(300),
        label = "PS5BorderHighlight"
    )

    val shadowElevation by animateDpAsState(
        targetValue = if (isSelected) 8.dp else 1.dp,
        animationSpec = tween(300),
        label = "ActivityShadowSpeed"
    )

    val weekdaysShort = remember {
        mapOf(
            1 to "SUN", 2 to "MON", 3 to "TUE", 4 to "WED", 
            5 to "THU", 6 to "FRI", 7 to "SAT"
        )
    }

    val repeatBadgeText = remember(mission.repeatType, mission.repeatDayOfWeek) {
        when (mission.repeatType) {
            "DAILY" -> "🔁 DAILY"
            "WEEKDAY" -> "🔁 ${weekdaysShort[mission.repeatDayOfWeek] ?: "WEEKDAY"}"
            else -> null
        }
    }

    Card(
        modifier = Modifier
            .width(172.dp)
            .height(110.dp)
            .shadow(shadowElevation, shape = RoundedCornerShape(12.dp), spotColor = if (isSelected) brightCyan else Color.Transparent)
            .border(BorderStroke(if (isSelected) 2.dp else 1.dp, animatedBorderColor), RoundedCornerShape(12.dp))
            .clickable { onSelect() },
        colors = CardDefaults.cardColors(containerColor = cardBackground),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Header of individual activity cards with beautiful PS indicators
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .background(
                                    if (isSelected) brightCyan.copy(0.12f) else textMuted.copy(0.08f),
                                    RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (isSelected) "ACTIVE" else "READY",
                                style = TextStyle(
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) brightCyan else textMuted
                                )
                            )
                        }

                        if (repeatBadgeText != null) {
                            Box(
                                modifier = Modifier
                                    .background(PS5Theme.AccentNeonPink.copy(0.08f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = repeatBadgeText,
                                    style = TextStyle(
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PS5Theme.AccentNeonPink
                                    )
                                )
                            }
                        }
                    }

                    // Delete active mission safely
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete mission",
                        tint = textMuted.copy(alpha = 0.5f),
                        modifier = Modifier
                            .size(16.dp)
                            .clickable { onDelete() }
                    )
                }

                // Title details limited to 2 lines
                Text(
                    text = mission.title.uppercase(),
                    style = TextStyle(
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 11.sp,
                        color = textBase
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(vertical = 2.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isSelected) "ENGAGED" else "TAP CARD",
                        style = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 8.sp,
                            color = if (isSelected) brightCyan else primaryBlue
                        )
                    )
                    // Custom PlayStation controller styled cross/circle glow
                    Box(
                        modifier = Modifier
                            .size(9.dp)
                            .border(BorderStroke(1.2.dp, if (isSelected) brightCyan else textMuted), CircleShape)
                    )
                }
            }
        }
    }
}

@Composable
fun TaskItemCard(
    task: Task,
    isDarkMode: Boolean,
    cardBackground: Color,
    borderBase: Color,
    primaryCyan: Color,
    accentPink: Color,
    textBase: Color,
    textMuted: Color,
    remainingSeconds: Int,
    isTimerRunning: Boolean,
    onToggleTimer: () -> Unit,
    onResetTimer: () -> Unit,
    onEditTimer: (Int) -> Unit,
    onCycleStatus: () -> Unit,
    onDelete: () -> Unit
) {
    val activeBorderColor by animateColorAsState(
        targetValue = when (task.status) {
            1 -> primaryCyan
            2 -> accentPink
            else -> borderBase
        },
        animationSpec = tween(250),
        label = "TaskBorderColor"
    )

    val softIntensityElevation by animateDpAsState(
        targetValue = if (task.status == 1) 4.dp else 0.dp,
        animationSpec = tween(250),
        label = "soft_elevation"
    )

    val context = LocalContext.current
    var showTimerSetter by remember { mutableStateOf(false) }

    fun formatTime(seconds: Int): String {
        val m = seconds / 60
        val s = seconds % 60
        return String.format("%02d:%02d", m, s)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                softIntensityElevation,
                shape = RoundedCornerShape(10.dp),
                spotColor = if (task.status == 1) primaryCyan else Color.Transparent
            )
            .border(BorderStroke(1.dp, activeBorderColor), RoundedCornerShape(10.dp)),
        colors = CardDefaults.cardColors(containerColor = cardBackground),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // High intensity clicky 3 State Switch Button conforming strictly to requirements
                ThreeStateButton(
                    status = task.status,
                    primaryCyan = primaryCyan,
                    accentPink = accentPink,
                    textMuted = textMuted,
                    onCycle = onCycleStatus,
                    modifier = Modifier.testTag("status_cycle_button_${task.id}")
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Objective Core details text
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onCycleStatus() }
                ) {
                    Text(
                        text = "OBJECTIVE UNIT #${task.orderIndex + 1}",
                        style = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = when (task.status) {
                                1 -> primaryCyan
                                2 -> accentPink
                                else -> textMuted
                            }
                        )
                    )

                    Text(
                        text = task.title,
                        style = TextStyle(
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (task.status == 2) textMuted else textBase,
                            textDecoration = if (task.status == 2) TextDecoration.LineThrough else null
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Delete item button
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .size(34.dp)
                        .border(BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.2f)), RoundedCornerShape(6.dp))
                        .background(Color(0xFFEF4444).copy(alpha = 0.05f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remove objective",
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            // NEON SECTOR OBJECTIVE TIMER PANEL
            if (task.timerDurationSeconds > 0) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isDarkMode) Color(0xFF0F121E) else Color(0xFFF0F2F6))
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val isExpired = remainingSeconds == 0
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(
                                    if (isExpired) Color(0xFFEF4444) else if (isTimerRunning) primaryCyan else textMuted,
                                    CircleShape
                                )
                        )
                        Text(
                            text = if (isExpired) "TIMER EXPIRED (BASE XP)" else "${formatTime(remainingSeconds)} REMAINING",
                            style = TextStyle(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isExpired) Color(0xFFEF4444) else if (isTimerRunning) primaryCyan else textBase
                            )
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Play/Pause Action
                        IconButton(
                            onClick = onToggleTimer,
                            modifier = Modifier.size(24.dp)
                        ) {
                            if (isTimerRunning) {
                                // Dynamicpause icon drawn on canvas
                                Canvas(modifier = Modifier.size(10.dp)) {
                                    val barW = 3.dp.toPx()
                                    val spacing = 3.dp.toPx()
                                    drawRect(
                                        color = if (isDarkMode) Color.White else Color.Black,
                                        topLeft = androidx.compose.ui.geometry.Offset(x = size.width / 2f - barW - spacing / 2f, y = 0f),
                                        size = androidx.compose.ui.geometry.Size(width = barW, height = size.height)
                                    )
                                    drawRect(
                                        color = if (isDarkMode) Color.White else Color.Black,
                                        topLeft = androidx.compose.ui.geometry.Offset(x = size.width / 2f + spacing / 2f, y = 0f),
                                        size = androidx.compose.ui.geometry.Size(width = barW, height = size.height)
                                    )
                                }
                            } else {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Resume countdown timer",
                                    tint = primaryCyan,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        // Reset Action
                        IconButton(
                            onClick = onResetTimer,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Reset countdown timer",
                                tint = textMuted,
                                modifier = Modifier.size(14.dp)
                            )
                        }

                        // Remove Timer Action
                        IconButton(
                            onClick = { onEditTimer(0) },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Clear timer",
                                tint = Color(0xFFEF4444).copy(alpha = 0.6f),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            } else {
                // If no timer assigned, permit manual creation on the fly
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (!showTimerSetter) {
                        TextButton(
                            onClick = {
                                CyberHaptics.trigger(context, HapticType.TIMER_ADD_PANEL_OPEN)
                                showTimerSetter = true
                            },
                            contentPadding = PaddingValues(0.dp),
                            modifier = Modifier.height(20.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = primaryCyan,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "ADD TIMER OBJECTIVE",
                                style = TextStyle(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = primaryCyan
                                )
                            )
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "DURATION:",
                                style = TextStyle(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 9.sp,
                                    color = textMuted,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            listOf(1, 2, 5, 25).forEach { mins ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(primaryCyan.copy(alpha = 0.12f))
                                        .border(BorderStroke(1.dp, primaryCyan.copy(alpha = 0.3f)), RoundedCornerShape(4.dp))
                                        .clickable {
                                            onEditTimer(mins * 60)
                                            showTimerSetter = false
                                        }
                                        .padding(horizontal = 6.dp, vertical = 2.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${mins}M",
                                        color = primaryCyan,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                            IconButton(
                                onClick = {
                                    CyberHaptics.trigger(context, HapticType.BUTTON_CANCEL)
                                    showTimerSetter = false
                                },
                                modifier = Modifier.size(18.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Cancel setting timer",
                                    tint = Color(0xFFEF4444),
                                    modifier = Modifier.size(10.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// 3-State Mission Action cycle button beautifully themed on premium PlayStation geometric indicators
@Composable
fun ThreeStateButton(
    status: Int,
    primaryCyan: Color,
    accentPink: Color,
    textMuted: Color,
    onCycle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val buttonWidthState = 120.dp
    val buttonHeightState = 34.dp

    Row(
        modifier = modifier
            .width(buttonWidthState)
            .height(buttonHeightState)
            .clip(RoundedCornerShape(8.dp))
            .clickable { onCycle() }
            .border(
                BorderStroke(
                    1.2.dp,
                    when (status) {
                        1 -> primaryCyan
                        2 -> accentPink
                        else -> textMuted.copy(alpha = 0.6f)
                    }
                ),
                shape = RoundedCornerShape(8.dp)
            )
            .background(
                when (status) {
                    2 -> accentPink // Neon pink for accomplished tier status
                    1 -> primaryCyan.copy(alpha = 0.08f)
                    else -> Color.Transparent
                }
            )
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon rendering
        when (status) {
            0 -> {
                Box(
                    modifier = Modifier
                        .size(13.dp)
                        .border(BorderStroke(1.5.dp, textMuted), CircleShape)
                )
            }
            1 -> {
                Canvas(modifier = Modifier.size(13.dp)) {
                    drawCircle(
                        color = primaryCyan,
                        style = Stroke(width = 1.5.dp.toPx())
                    )
                    drawArc(
                        color = primaryCyan,
                        startAngle = -90f,
                        sweepAngle = 180f,
                        useCenter = true
                    )
                }
            }
            2 -> {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(6.dp))

        // Label indicator conforming precisely to game aesthetics
        Text(
            text = when (status) {
                1 -> "ACTIVE"
                2 -> "CONCLUDED"
                else -> "DORMANT"
            },
            style = TextStyle(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 9.sp,
                color = when (status) {
                    1 -> primaryCyan
                    2 -> Color.Black
                    else -> textMuted
                },
                letterSpacing = 0.2.sp
            )
        )
    }
}
