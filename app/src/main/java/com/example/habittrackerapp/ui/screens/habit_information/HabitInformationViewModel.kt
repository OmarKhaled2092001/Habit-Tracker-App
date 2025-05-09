package com.example.habittrackerapp.ui.screens.habit_information

import android.os.CountDownTimer
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habittrackerapp.data.HabitDailyProgress
import com.example.habittrackerapp.data.HabitInfo
import com.example.habittrackerapp.data.HabitProgressHistory
import com.example.habittrackerapp.data.Reminder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale
import kotlin.math.max
import kotlin.math.roundToInt

// Enum for Habit Type
enum class HabitType {
    COUNTER,
    TIMER,
    UNKNOWN // Default or if unit is not recognized
}

// Enum for Timer State
enum class TimerState {
    IDLE,      // Timer hasn't started or is reset
    RUNNING,   // Timer is active
    PAUSED,    // Timer is paused
    FINISHED   // Timer completed its duration
}

class HabitInformationViewModel : ViewModel() {

    private val TAG = "HabitInfoVM_Debug"

    private val _habitInfo = MutableStateFlow<HabitInfo?>(null)
    val habitInfo: StateFlow<HabitInfo?> = _habitInfo.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val firestore = Firebase.firestore
    private val auth = FirebaseAuth.getInstance()
    private val userId: String? get() = auth.currentUser?.uid

    private val _habitType = MutableStateFlow(HabitType.UNKNOWN)
    val habitType: StateFlow<HabitType> = _habitType.asStateFlow()

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    // For Calendar
    private val _currentDisplayMonth = MutableStateFlow(YearMonth.from(_selectedDate.value))
    val currentDisplayMonth: StateFlow<YearMonth> = _currentDisplayMonth.asStateFlow()

    private val _calendarData = MutableStateFlow<Map<Int, CalendarDayUIData>>(emptyMap())
    val calendarData: StateFlow<Map<Int, CalendarDayUIData>> = _calendarData.asStateFlow()

    private val _habitColors = MutableStateFlow<HabitInformationColors?>(null)
    val habitColors: StateFlow<HabitInformationColors?> = _habitColors.asStateFlow()

    private val _currentProgressCount = MutableStateFlow(0)
    val currentProgressCount: StateFlow<Int> = _currentProgressCount.asStateFlow()

    private val _targetGoalCount = MutableStateFlow(1)
    val targetGoalCount: StateFlow<Int> = _targetGoalCount.asStateFlow()

    private val _timerState = MutableStateFlow(TimerState.IDLE)
    val timerState: StateFlow<TimerState> = _timerState.asStateFlow()

    private val _currentTimeElapsedMillis = MutableStateFlow(0L)
    val currentTimeElapsedMillis: StateFlow<Long> = _currentTimeElapsedMillis.asStateFlow()

    private val _targetDurationMillis = MutableStateFlow(0L)
    val targetDurationMillis: StateFlow<Long> = _targetDurationMillis.asStateFlow()

    private var countDownTimer: CountDownTimer? = null

    private val _doneStat = MutableStateFlow("0%")
    val doneStat: StateFlow<String> = _doneStat.asStateFlow()

    private val _overdoneStat = MutableStateFlow("0 Days")
    val overdoneStat: StateFlow<String> = _overdoneStat.asStateFlow()

    private val _missedStat = MutableStateFlow("0 Days")
    val missedStat: StateFlow<String> = _missedStat.asStateFlow()

    private val _streakStat = MutableStateFlow("0/0 Days")
    val streakStat: StateFlow<String> = _streakStat.asStateFlow()

    private val _habitProgressHistory = MutableStateFlow<HabitProgressHistory?>(null)

    private val firestoreDateFormatter = DateTimeFormatter.ISO_LOCAL_DATE // YYYY-MM-DD
    private val appDateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.US)

    init {
        viewModelScope.launch {
            combine(
                _habitInfo,
                _habitProgressHistory,
                _currentDisplayMonth,
                _selectedDate // React to selectedDate for calendar highlighting if needed
            ) { habitInfo, progressHistory, displayMonth, selectedDateVal -> // Renamed selectedDate to avoid conflict
                if (habitInfo != null) {
                    val currentHabitColors = _habitColors.value
                    if (currentHabitColors == null || currentHabitColors.primary != habitInfo.selectedColor) {
                        _habitColors.value = generateHabitInformationColors(habitInfo.selectedColor)
                    }
                    _habitColors.value?.let {
                        generateCalendarDataForMonth(displayMonth, habitInfo, progressHistory, it, selectedDateVal)
                    }
                }
                Unit // Return Unit for combine
            }.collect { /* Trigger the flow */ }
        }
    }

    fun fetchHabitDetails(habitName: String) {
        Log.d(TAG, "fetchHabitDetails called for habit: $habitName")
        if (userId == null) {
            Log.w(TAG, "User not logged in, aborting fetchHabitDetails.")
            _habitInfo.value = null; _isLoading.value = false; return
        }
        if (habitName.isBlank()) {
            Log.w(TAG, "Habit name is blank, aborting fetchHabitDetails.")
            _habitInfo.value = null; _isLoading.value = false; return
        }

        viewModelScope.launch {
            _isLoading.value = true
            Log.d(TAG, "Starting to fetch details for habit: $habitName from Firestore.")
            try {
                val userDocRef = firestore.collection("users").document(userId!!)
                val snapshot = userDocRef.get().await()
                val habitsList = snapshot.get("selectedHabitsFullInfo") as? List<Map<String, Any>>
                val foundHabitMap = habitsList?.firstOrNull { it["habitName"] as? String == habitName }

                if (foundHabitMap != null) {
                    Log.d(TAG, "Habit 	'$habitName	' found in user's document.")
                    val parsedHabitInfo = parseHabitInfoFromMap(foundHabitMap)
                    _habitInfo.value = parsedHabitInfo
                    // _habitColors.value is set by the combine flow now
                    Log.d(TAG, "Parsed HabitInfo: $parsedHabitInfo")
                    val type = determineHabitType(parsedHabitInfo.unit)
                    _habitType.value = type
                    Log.d(TAG, "Determined habit type: $type")

                    when (type) {
                        HabitType.COUNTER -> _targetGoalCount.value = parsedHabitInfo.goalCount.toIntOrNull() ?: 1
                        HabitType.TIMER -> _targetDurationMillis.value = parseDurationToMillis(parsedHabitInfo.goalCount, parsedHabitInfo.unit)
                        HabitType.UNKNOWN -> _targetGoalCount.value = parsedHabitInfo.goalCount.toIntOrNull() ?: 1
                    }
                    Log.d(TAG, "Target goal count: ${_targetGoalCount.value}, Target duration millis: ${_targetDurationMillis.value}")
                    _currentDisplayMonth.value = YearMonth.from(_selectedDate.value) // Ensure display month is current
                    loadHabitProgressHistoryAndCalculateStats(parsedHabitInfo)
                } else {
                    Log.w(TAG, "Habit 	'$habitName	' not found in user's document.")
                    _habitInfo.value = null; _habitType.value = HabitType.UNKNOWN
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching habit 	'$habitName	': ${e.message}", e)
                _habitInfo.value = null; _habitType.value = HabitType.UNKNOWN
            } finally {
                _isLoading.value = false
                Log.d(TAG, "Finished fetching habit details for 	'$habitName	'. isLoading: ${_isLoading.value}")
            }
        }
    }

    private fun parseHabitInfoFromMap(habitMap: Map<String, Any>): HabitInfo {
        val name = habitMap["habitName"] as? String ?: ""
        val selectedIcon = habitMap["selectedIcon"] as? String ?: ""
        val colorLong = habitMap["selectedColor"] as? Long
        val colorInt = colorLong?.toInt() ?: Color.Gray.toArgb()
        val selectedColor = Color(colorInt)
        val goalCountStr = habitMap["goalCount"] as? String ?: "1"
        val unit = habitMap["unit"] as? String ?: "Time"
        val frequency = habitMap["frequency"] as? String ?: "Daily"
        val selectedDays = habitMap["selectedDays"] as? List<String> ?: emptyList()
        val note = habitMap["note"] as? String ?: ""
        val startDateStr = habitMap["startDate"] as? String ?: ""
        val endDateStr = habitMap["endDate"] as? String ?: ""
        val remindersList = habitMap["reminders"] as? List<Map<String, Any>> ?: emptyList()
        val parsedReminders = remindersList.mapNotNull { reminderMap ->
            try {
                Reminder(
                    title = reminderMap["title"] as? String ?: "Reminder",
                    time = reminderMap["time"] as? String ?: "09:00",
                    frequency = reminderMap["frequency"] as? String ?: "Daily",
                    selectedDays = reminderMap["selectedDays"] as? List<String> ?: emptyList(),
                    isEnabled = reminderMap["isEnabled"] as? Boolean ?: true
                )
            } catch (e: Exception) {
                Log.e(TAG, "Error parsing reminder: ${e.message}", e)
                null
            }
        }
        return HabitInfo(habitName = name, selectedIcon = selectedIcon, selectedColor = selectedColor,
            goalCount = goalCountStr, unit = unit, frequency = frequency, selectedDays = selectedDays,
            reminders = parsedReminders, note = note, startDate = startDateStr, endDate = endDateStr)
    }

    private fun loadHabitProgressHistoryAndCalculateStats(habit: HabitInfo) {
        Log.d(TAG, "loadHabitProgressHistoryAndCalculateStats called for habit: ${habit.habitName}")
        if (userId == null || habit.habitName.isBlank()) {
            Log.w(TAG, "User ID or habit name is blank, aborting loadHabitProgressHistory.")
            return
        }
        viewModelScope.launch {
            Log.d(TAG, "Starting to load progress history for '${habit.habitName}' from Firestore.")
            try {
                val progressEntriesRef = firestore.collection("users").document(userId!!)
                    .collection("habitProgress").document(habit.habitName)
                    .collection("dailyEntries")

                val querySnapshot = progressEntriesRef.get().await()
                val dailyEntries = mutableListOf<HabitDailyProgress>()
                Log.d(TAG, "Fetched ${querySnapshot.documents.size} progress documents from Firestore for '${habit.habitName}'.")
                for (document in querySnapshot.documents) {
                    try {
                        val progress = document.toObject<HabitDailyProgress>()
                        if (progress != null) {
                            progress.date = LocalDate.parse(document.id, firestoreDateFormatter)
                            dailyEntries.add(progress)
                            Log.v(TAG, "Parsed progress entry for date ${document.id}: $progress")
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error parsing daily progress for document ${document.id} of habit '${habit.habitName}': ${e.message}", e)
                    }
                }
                val history = HabitProgressHistory(habitName = habit.habitName, dailyEntries = dailyEntries.sortedBy { it.date })
                _habitProgressHistory.value = history
                Log.d(TAG, "Successfully loaded and set _habitProgressHistory for '${habit.habitName}'. Total entries: ${history.dailyEntries.size}.")
                calculateAllStatsAndUpdateUIDisplay(history, habit, _selectedDate.value)
            } catch (e: Exception) {
                Log.e(TAG, "Error loading progress history for '${habit.habitName}': ${e.message}", e)
                val emptyHistory = HabitProgressHistory(habit.habitName, emptyList())
                _habitProgressHistory.value = emptyHistory
                Log.w(TAG, "Set _habitProgressHistory to empty for '${habit.habitName}' due to error.")
                calculateAllStatsAndUpdateUIDisplay(emptyHistory, habit, _selectedDate.value)
            }
        }
    }

    private fun calculateAllStatsAndUpdateUIDisplay(history: HabitProgressHistory?, habit: HabitInfo, dateForDoneStat: LocalDate) {
        Log.d(TAG, "calculateAllStatsAndUpdateUIDisplay called for habit: ${habit.habitName}, dateForDoneStat: $dateForDoneStat")
        calculateOverdoneStat(history, habit)
        calculateMissedStat(history, habit)
        calculateStreakStats(history, habit)
        updateProgressDisplayForDate(habit, dateForDoneStat, history)
        // Calendar data generation is handled by the combine flow
    }

    fun updateSelectedDate(newDate: LocalDate) {
        Log.d(TAG, "updateSelectedDate called with newDate: $newDate. Current habit: ${_habitInfo.value?.habitName}")
        val oldSelectedDateMonth = YearMonth.from(_selectedDate.value)
        _selectedDate.value = newDate
        val newSelectedDateMonth = YearMonth.from(newDate)
        if (newSelectedDateMonth != oldSelectedDateMonth) {
            _currentDisplayMonth.value = newSelectedDateMonth
        }
        _habitInfo.value?.let {
            Log.d(TAG, "HabitInfo is not null, calling updateProgressDisplayForDate for ${it.habitName} and date $newDate")
            updateProgressDisplayForDate(it, newDate, _habitProgressHistory.value)
        } ?: Log.w(TAG, "HabitInfo is null in updateSelectedDate, cannot update display.")
    }

    fun onPreviousMonthClicked() {
        _currentDisplayMonth.value = _currentDisplayMonth.value.minusMonths(1)
        Log.d(TAG, "onPreviousMonthClicked: New display month: ${_currentDisplayMonth.value}")
    }

    fun onNextMonthClicked() {
        _currentDisplayMonth.value = _currentDisplayMonth.value.plusMonths(1)
        Log.d(TAG, "onNextMonthClicked: New display month: ${_currentDisplayMonth.value}")
    }

    private fun generateCalendarDataForMonth(
        yearMonth: YearMonth,
        habit: HabitInfo,
        history: HabitProgressHistory?,
        colors: HabitInformationColors,
        selectedDay: LocalDate // Parameter name changed for clarity
    ) {
        Log.d(TAG, "generateCalendarDataForMonth for $yearMonth, habit: ${habit.habitName}")
        val today = LocalDate.now()
        val daysInMonth = yearMonth.lengthOfMonth()
        val newCalendarData = mutableMapOf<Int, CalendarDayUIData>()

        for (dayOfMonth in 1..daysInMonth) {
            val currentDate = yearMonth.atDay(dayOfMonth)
            var status: CalendarDayStatus
            var displayColor: Color
            var showOverdoneIndicator = false
            var showFutureScheduledIndicator = false

            val isActive = isHabitActiveOnDate(currentDate, habit)
            val isScheduled = isActive && isScheduledDay(currentDate, habit)

            if (!isActive) {
                status = CalendarDayStatus.NOT_SCHEDULED
                displayColor = Color.Transparent
            } else if (isScheduled) {
                if (currentDate.isAfter(today)) {
                    status = CalendarDayStatus.FUTURE_SCHEDULED
                    displayColor = colors.primary // Or a specific color for future scheduled border
                    showFutureScheduledIndicator = true
                } else {
                    val dailyEntry = history?.dailyEntries?.find { it.date == currentDate }
                    if (dailyEntry != null) {
                        val targetMet = dailyEntry.goalMet
                        val goalExceeded = dailyEntry.goalExceeded
                        val progressMade = (dailyEntry.actualCount ?: 0) > 0 || (dailyEntry.actualDurationMillis ?: 0L) > 0

                        if (goalExceeded) {
                            status = CalendarDayStatus.OVERDONE
                            displayColor = colors.primary
                            showOverdoneIndicator = true
                        } else if (targetMet) {
                            status = CalendarDayStatus.GOAL_MET
                            displayColor = colors.primary
                        } else if (progressMade) {
                            status = CalendarDayStatus.PARTIALLY_MET
                            displayColor = colors.primaryLight
                        } else {
                            status = CalendarDayStatus.MISSED
                            displayColor = Color.LightGray.copy(alpha = 0.6f)
                        }
                    } else {
                        status = CalendarDayStatus.MISSED
                        displayColor = Color.LightGray.copy(alpha = 0.6f)
                    }
                }
            } else { // Not scheduled for this day (e.g. weekly habit on an off-day)
                status = CalendarDayStatus.NOT_SCHEDULED
                displayColor = Color.Transparent
            }

            newCalendarData[dayOfMonth] = CalendarDayUIData(status, displayColor, showOverdoneIndicator, showFutureScheduledIndicator)
        }
        _calendarData.value = newCalendarData
        Log.d(TAG, "Generated calendar data for $yearMonth: ${newCalendarData.size} entries.")
    }

    private fun updateProgressDisplayForDate(habit: HabitInfo, date: LocalDate, history: HabitProgressHistory?) {
        Log.d(TAG, "updateProgressDisplayForDate called for habit: ${habit.habitName}, date: $date")
        val dailyEntry = history?.dailyEntries?.find { it.date == date }
        Log.d(TAG, "Found dailyEntry for date $date: $dailyEntry")

        val currentCount = dailyEntry?.actualCount ?: 0
        val currentMillis = dailyEntry?.actualDurationMillis ?: 0L

        _currentProgressCount.value = currentCount
        _currentTimeElapsedMillis.value = currentMillis

        if (_habitType.value == HabitType.TIMER) {
            val targetMillisValue = _targetDurationMillis.value
            val newTimerState = if (currentMillis > 0 && currentMillis < targetMillisValue) TimerState.PAUSED
            else if (currentMillis >= targetMillisValue && targetMillisValue > 0) TimerState.FINISHED
            else TimerState.IDLE
            _timerState.value = newTimerState
            if (_timerState.value == TimerState.IDLE) resetTimerInternal(false)
        }

        var donePercentage = 0
        if (dailyEntry != null || (habitType.value == HabitType.TIMER && currentMillis == 0L && (_targetDurationMillis.value == 0L && dailyEntry?.goalMet == true))) { // Handle 0 duration timer completed
            when (habitType.value) {
                HabitType.COUNTER -> {
                    val target = habit.goalCount.toIntOrNull() ?: 1
                    if (target > 0) {
                        donePercentage = ((currentCount.toFloat() / target.toFloat()) * 100).roundToInt().coerceIn(0,100)
                    }
                }
                HabitType.TIMER -> {
                    val targetMillisValue = _targetDurationMillis.value
                    if (targetMillisValue > 0) {
                        donePercentage = ((currentMillis.toFloat() / targetMillisValue.toFloat()) * 100).roundToInt().coerceIn(0,100)
                    } else if (targetMillisValue == 0L && dailyEntry?.goalMet == true) { // 0 duration timer is 100% if met
                        donePercentage = 100
                    }
                }
                HabitType.UNKNOWN -> { /* No percentage */ }
            }
        }
        _doneStat.value = "$donePercentage%"
        Log.d(TAG, "Final values for date $date: _currentProgressCount=${_currentProgressCount.value}, _currentTimeElapsedMillis=${_currentTimeElapsedMillis.value}, _doneStat=${_doneStat.value}")
    }

    private fun saveOrUpdateDailyProgress() {
        val currentHabit = _habitInfo.value ?: return
        val currentUserId = userId ?: return
        val dateToSave = _selectedDate.value
        Log.d(TAG, "saveOrUpdateDailyProgress called for habit: ${currentHabit.habitName}, date: $dateToSave")

        if (dateToSave.isAfter(LocalDate.now()) || !isHabitActiveOnDate(dateToSave, currentHabit)) {
            Log.w(TAG, "Not saving progress: Future date or inactive habit day.")
            return
        }

        val targetCount = currentHabit.goalCount.toIntOrNull() ?: 1
        val targetMillisValue = _targetDurationMillis.value
        val currentActualCount = if (habitType.value == HabitType.COUNTER) _currentProgressCount.value else null
        val currentActualDuration = if (habitType.value == HabitType.TIMER) _currentTimeElapsedMillis.value else null

        val goalIsMet = when (habitType.value) {
            HabitType.COUNTER -> (currentActualCount ?: 0) >= targetCount
            HabitType.TIMER -> (currentActualDuration ?: 0L) >= targetMillisValue // For 0 duration, this is true if current is 0
            else -> false
        }
        val goalIsExceeded = when (habitType.value) {
            HabitType.COUNTER -> (currentActualCount ?: 0) > targetCount
            HabitType.TIMER -> (currentActualDuration ?: 0L) > targetMillisValue && targetMillisValue > 0 // Exceeded only if target > 0
            else -> false
        }

        val newProgressEntry = HabitDailyProgress(
            date = dateToSave,
            actualCount = currentActualCount,
            actualDurationMillis = currentActualDuration,
            goalMet = goalIsMet,
            goalExceeded = goalIsExceeded
        )
        Log.d(TAG, "Constructed newProgressEntry for saving: $newProgressEntry")

        val currentHistory = _habitProgressHistory.value
        val updatedEntries = currentHistory?.dailyEntries?.filterNot { it.date == dateToSave }?.toMutableList() ?: mutableListOf()

        var shouldAddEntry = false
        if (habitType.value == HabitType.COUNTER) {
            if ((currentActualCount ?: 0) > 0 || goalIsMet) shouldAddEntry = true
        } else if (habitType.value == HabitType.TIMER) {
            if ((currentActualDuration ?: 0L) > 0 || goalIsMet || _timerState.value != TimerState.IDLE || (targetMillisValue == 0L && goalIsMet) ) {
                shouldAddEntry = true
            }
        }

        if (shouldAddEntry) {
            updatedEntries.add(newProgressEntry)
        }

        val updatedHistory = HabitProgressHistory(currentHabit.habitName, updatedEntries.sortedBy { it.date })
        _habitProgressHistory.value = updatedHistory
        Log.d(TAG, "Optimistically updated _habitProgressHistory: $updatedHistory")

        calculateAllStatsAndUpdateUIDisplay(updatedHistory, currentHabit, dateToSave)
        Log.d(TAG, "Called calculateAllStatsAndUpdateUIDisplay after optimistic update.")

        viewModelScope.launch {
            try {
                val dailyProgressDocRef = firestore.collection("users").document(currentUserId)
                    .collection("habitProgress").document(currentHabit.habitName)
                    .collection("dailyEntries").document(dateToSave.format(firestoreDateFormatter))

                val progressMap = mutableMapOf<String, Any?>(
                    "goalMet" to newProgressEntry.goalMet,
                    "goalExceeded" to newProgressEntry.goalExceeded
                )
                if (habitType.value == HabitType.COUNTER) {
                    progressMap["actualCount"] = newProgressEntry.actualCount
                    progressMap["actualDurationMillis"] = FieldValue.delete()
                } else if (habitType.value == HabitType.TIMER) {
                    progressMap["actualDurationMillis"] = newProgressEntry.actualDurationMillis
                    progressMap["actualCount"] = FieldValue.delete()
                }

                if (!shouldAddEntry && currentHistory?.dailyEntries?.any { it.date == dateToSave } == true) {
                    dailyProgressDocRef.delete().await()
                    Log.i(TAG, "Deleted progress from Firestore for ${currentHabit.habitName} on $dateToSave as progress is zero.")
                } else if (shouldAddEntry) {
                    dailyProgressDocRef.set(progressMap, SetOptions.merge()).await()
                    Log.i(TAG, "Successfully saved/merged progress to Firestore for ${currentHabit.habitName} on $dateToSave. Data: $progressMap")
                } else {
                    Log.i(TAG, "No Firestore update for ${currentHabit.habitName} on $dateToSave as no progress and no prior entry to delete.")
                }

            } catch (e: Exception) {
                Log.e(TAG, "Error saving/deleting daily progress to Firestore for ${currentHabit.habitName} on $dateToSave: ${e.message}", e)
            }
        }
    }

    private fun isHabitActiveOnDate(date: LocalDate, habit: HabitInfo): Boolean {
        if (habit.startDate.isBlank() && habit.endDate.isBlank()) return true // Active indefinitely if no dates

        val habitStartDate = try { if (habit.startDate.isNotBlank()) LocalDate.parse(habit.startDate, appDateFormatter) else null } catch (e: Exception) { null }
        val habitEndDate = try { if (habit.endDate.isNotBlank()) LocalDate.parse(habit.endDate, appDateFormatter) else null } catch (e: Exception) { null }

        if (habitStartDate != null && date.isBefore(habitStartDate)) return false
        if (habitEndDate != null && date.isAfter(habitEndDate)) return false

        return true
    }

    fun incrementProgressCount() {
        if (_selectedDate.value.isAfter(LocalDate.now())) return
        if (habitType.value == HabitType.COUNTER) {
            _currentProgressCount.value++
            Log.d(TAG, "incrementProgressCount: _currentProgressCount new value: ${_currentProgressCount.value}")
            saveOrUpdateDailyProgress()
        }
    }

    fun decrementProgressCount() {
        if (_selectedDate.value.isAfter(LocalDate.now())) return
        if (habitType.value == HabitType.COUNTER && _currentProgressCount.value > 0) {
            _currentProgressCount.value--
            Log.d(TAG, "decrementProgressCount: _currentProgressCount new value: ${_currentProgressCount.value}")
            saveOrUpdateDailyProgress()
        }
    }

    fun startTimer() {
        if (_selectedDate.value.isAfter(LocalDate.now())) return
        if (habitType.value == HabitType.TIMER && _timerState.value != TimerState.RUNNING && _timerState.value != TimerState.FINISHED) {
            _timerState.value = TimerState.RUNNING
            Log.d(TAG, "startTimer: Timer state set to RUNNING.")
            val targetMillisValue = _targetDurationMillis.value
            val remainingTime = targetMillisValue - _currentTimeElapsedMillis.value

            if (remainingTime <= 0 && targetMillisValue > 0) {
                _currentTimeElapsedMillis.value = targetMillisValue
                _timerState.value = TimerState.FINISHED
                Log.d(TAG, "startTimer: Timer already met/exceeded. State set to FINISHED.")
                saveOrUpdateDailyProgress()
                return
            }
            if (targetMillisValue <= 0) { // Allow 0 duration habits to be marked complete instantly
                _currentTimeElapsedMillis.value = 0L
                _timerState.value = TimerState.FINISHED
                Log.d(TAG, "startTimer: Target duration is 0. Timer marked as FINISHED.")
                saveOrUpdateDailyProgress()
                return
            }

            countDownTimer?.cancel()
            countDownTimer = object : CountDownTimer(remainingTime, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    _currentTimeElapsedMillis.value = targetMillisValue - millisUntilFinished
                }
                override fun onFinish() {
                    _currentTimeElapsedMillis.value = targetMillisValue
                    _timerState.value = TimerState.FINISHED
                    Log.d(TAG, "Timer onFinish: State set to FINISHED. _currentTimeElapsedMillis=${_currentTimeElapsedMillis.value}")
                    saveOrUpdateDailyProgress()
                }
            }.start()
            Log.d(TAG, "Timer started with remainingTime: $remainingTime ms.")
        }
    }

    fun pauseTimer() {
        if (habitType.value == HabitType.TIMER && _timerState.value == TimerState.RUNNING) {
            countDownTimer?.cancel()
            _timerState.value = TimerState.PAUSED
            Log.d(TAG, "pauseTimer: Timer paused. _currentTimeElapsedMillis=${_currentTimeElapsedMillis.value}")
            saveOrUpdateDailyProgress() // Save paused state
        }
    }

    fun stopTimer() { // Acts as "Mark as Done" for timer
        if (_selectedDate.value.isAfter(LocalDate.now())) return
        if (habitType.value == HabitType.TIMER) {
            countDownTimer?.cancel()
            _currentTimeElapsedMillis.value = _targetDurationMillis.value // Mark as full duration met
            _timerState.value = TimerState.FINISHED
            Log.d(TAG, "stopTimer: Timer marked as FINISHED. _currentTimeElapsedMillis=${_currentTimeElapsedMillis.value}")
            saveOrUpdateDailyProgress()
        }
    }

    fun resetTimer() {
        if (_selectedDate.value.isAfter(LocalDate.now())) return
        if (habitType.value == HabitType.TIMER) {
            Log.d(TAG, "resetTimer called.")
            resetTimerInternal(true)
        }
    }

    private fun resetTimerInternal(saveToFirestore: Boolean) {
        countDownTimer?.cancel()
        _currentTimeElapsedMillis.value = 0L
        _timerState.value = TimerState.IDLE
        Log.d(TAG, "resetTimerInternal: Timer reset. SaveToFirestore: $saveToFirestore")
        if (saveToFirestore) {
            saveOrUpdateDailyProgress()
        }
    }

    private fun determineHabitType(unit: String): HabitType {
        return when (unit.lowercase().trim()) {
            "sec", "secs", "second", "seconds",
            "min", "mins", "minute", "minutes",
            "hour", "hours" -> HabitType.TIMER
            else -> HabitType.COUNTER
        }
    }

    private fun parseDurationToMillis(goalCountStr: String, unit: String): Long {
        val count = goalCountStr.toLongOrNull() ?: 0L
        return when (unit.lowercase().trim()) {
            "sec", "secs", "second", "seconds" -> count * 1000
            "min", "mins", "minute", "minutes" -> count * 60 * 1000
            "hour", "hours" -> count * 60 * 60 * 1000
            else -> 0L
        }
    }

    // Corrected isScheduledDay function
    private fun isScheduledDay(date: LocalDate, habit: HabitInfo): Boolean {
        return when (habit.frequency.lowercase()) {
            "daily" -> true
            "weekly" -> {
                val currentDayOfWeek = date.dayOfWeek
                habit.selectedDays.any { selectedDayString ->
                    // Convert selectedDayString (e.g., "Mon", "Tue") to DayOfWeek enum
                    val selectedDayOfWeek = when (selectedDayString.uppercase().take(3)) {
                        "MON" -> DayOfWeek.MONDAY
                        "TUE" -> DayOfWeek.TUESDAY
                        "WED" -> DayOfWeek.WEDNESDAY
                        "THU" -> DayOfWeek.THURSDAY
                        "FRI" -> DayOfWeek.FRIDAY
                        "SAT" -> DayOfWeek.SATURDAY
                        "SUN" -> DayOfWeek.SUNDAY
                        else -> null // Or throw an exception for invalid day string
                    }
                    selectedDayOfWeek == currentDayOfWeek
                }
            }
            else -> false
        }
    }

    // Ensure these stat calculation functions are defined within the class
    private fun calculateOverdoneStat(history: HabitProgressHistory?, habit: HabitInfo) {
        val count = history?.dailyEntries?.count { entry ->
            isHabitActiveOnDate(entry.date, habit) && entry.goalExceeded
        } ?: 0
        _overdoneStat.value = "$count Days"
    }

    private fun calculateMissedStat(history: HabitProgressHistory?, habit: HabitInfo) {
        if (habit.startDate.isBlank()) {
            _missedStat.value = "0 Days"; return
        }
        val habitStartDate = try { LocalDate.parse(habit.startDate, appDateFormatter) } catch (e: Exception) { _missedStat.value = "0 Days"; return }
        var missedCount = 0
        var currentDate = habitStartDate
        val today = LocalDate.now()

        // Iterate from habit start date up to yesterday
        while (!currentDate.isAfter(today.minusDays(1))) {
            if (isHabitActiveOnDate(currentDate, habit) && isScheduledDay(currentDate, habit)) {
                val entry = history?.dailyEntries?.find { it.date == currentDate }
                val goalMet = entry?.goalMet ?: false
                if (!goalMet) {
                    missedCount++
                }
            }
            currentDate = currentDate.plusDays(1)
        }
        _missedStat.value = "$missedCount Days"
    }

    private fun calculateStreakStats(history: HabitProgressHistory?, habit: HabitInfo) {
        if (history == null || history.dailyEntries.isEmpty()) {
            _streakStat.value = "0/0 Days"; return
        }

        val metGoalEntries = history.dailyEntries
            .filter { it.goalMet && isHabitActiveOnDate(it.date, habit) && isScheduledDay(it.date, habit) }
            .sortedBy { it.date }

        if (metGoalEntries.isEmpty()) {
            _streakStat.value = "0/0 Days"; return
        }

        var currentStreakVal = 0
        var bestStreakVal = 0
        var lastStreakDate: LocalDate? = null

        for (entry in metGoalEntries) {
            if (lastStreakDate == null) {
                currentStreakVal = 1
            } else {
                val daysBetween = ChronoUnit.DAYS.between(lastStreakDate, entry.date)
                if (daysBetween == 1L) {
                    currentStreakVal++
                } else if (daysBetween > 1L) {
                    bestStreakVal = max(bestStreakVal, currentStreakVal)
                    currentStreakVal = 1 // Reset for new streak
                } // If daysBetween is 0 or less (should not happen with sorted unique dates), streak continues
            }
            lastStreakDate = entry.date
            bestStreakVal = max(bestStreakVal, currentStreakVal)
        }

        // Determine final current streak based on today/yesterday
        val today = LocalDate.now()
        val yesterday = today.minusDays(1)
        val lastMetEntryDate = metGoalEntries.lastOrNull()?.date

        val finalCurrentStreak = if (lastMetEntryDate == null) {
            0
        } else if (lastMetEntryDate == today) {
            currentStreakVal
        } else if (lastMetEntryDate == yesterday) {
            // If today is scheduled and not met, or not active, streak is broken from today's perspective
            if (isHabitActiveOnDate(today, habit) && isScheduledDay(today, habit) && history.dailyEntries.find { it.date == today }?.goalMet != true) {
                0
            } else {
                currentStreakVal // Streak continues if today is not a scheduled day or is met
            }
        } else {
            0 // Streak broken before yesterday
        }

        _streakStat.value = "$finalCurrentStreak/$bestStreakVal Days"
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "onCleared called, cancelling timer.")
        countDownTimer?.cancel()
    }
}

