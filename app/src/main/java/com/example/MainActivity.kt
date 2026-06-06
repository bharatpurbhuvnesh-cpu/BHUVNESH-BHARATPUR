package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.MainViewModel
import com.example.ui.Opponent
import com.example.ui.RaceState
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = MaterialTheme.colorScheme.background
                ) { innerPadding ->
                    MainScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = viewModel()
) {
    var activeTab by remember { mutableIntStateOf(0) }
    
    // Collect UI state from ViewModel
    val raceState by viewModel.raceState.collectAsState()
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // App Header
        ArenaHeader()

        // Tab Content
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (activeTab) {
                0 -> RaceTrackTab(viewModel = viewModel)
                1 -> LiveTowerTab(viewModel = viewModel)
                2 -> TrophyHistoryTab(viewModel = viewModel)
            }
        }

        // Tab Selection Row - Positioned at bottom for true modern bottom-navigation
        ArenaTabs(
            activeTab = activeTab,
            onTabSelected = { activeTab = it }
        )

        // Security Warning required by android-secret-management skill
        SecurityWarningText()
    }
}

@Composable
fun ArenaHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(LighterAccent, shape = RoundedCornerShape(12.dp))
                    .padding(10.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Championship Icon",
                    tint = DeepPurple,
                    modifier = Modifier.size(24.dp)
                )
            }
            Column {
                Text(
                    text = "My Fitness",
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp,
                    letterSpacing = (-0.5).sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Live Telemetry Tracker",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = PaleText
                )
            }
        }
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(ActiveAccent, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "User profile",
                tint = DeepPurple,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun ArenaTabs(
    activeTab: Int,
    onTabSelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(MaterialTheme.colorScheme.surface)
            .border(width = 1.dp, color = BorderOutline, shape = RectangleShape)
            .navigationBarsPadding(),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Tab 1: TRACK
        val isTab0 = activeTab == 0
        Column(
            modifier = Modifier
                .weight(1f)
                .clickable { onTabSelected(0) }
                .testTag("tab_track"),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (isTab0) {
                Box(
                    modifier = Modifier
                        .background(LighterAccent, shape = RoundedCornerShape(16.dp))
                        .padding(horizontal = 20.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🏟️", fontSize = 20.sp)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Track",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = DarkText
                )
            } else {
                Text("🏟️", fontSize = 22.sp, modifier = Modifier.scale(0.9f))
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Track",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DarkText.copy(alpha = 0.5f)
                )
            }
        }

        // Tab 2: TOWER
        val isTab1 = activeTab == 1
        Column(
            modifier = Modifier
                .weight(1f)
                .clickable { onTabSelected(1) }
                .testTag("tab_tower"),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (isTab1) {
                Box(
                    modifier = Modifier
                        .background(LighterAccent, shape = RoundedCornerShape(16.dp))
                        .padding(horizontal = 20.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🎙️", fontSize = 20.sp)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Tower",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = DarkText
                )
            } else {
                Text("🎙️", fontSize = 22.sp, modifier = Modifier.scale(0.9f))
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Tower",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DarkText.copy(alpha = 0.5f)
                )
            }
        }

        // Tab 3: TROPHIES
        val isTab2 = activeTab == 2
        Column(
            modifier = Modifier
                .weight(1f)
                .clickable { onTabSelected(2) }
                .testTag("tab_trophies"),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (isTab2) {
                Box(
                    modifier = Modifier
                        .background(LighterAccent, shape = RoundedCornerShape(16.dp))
                        .padding(horizontal = 20.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🏆", fontSize = 20.sp)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Trophies",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = DarkText
                )
            } else {
                Text("🏆", fontSize = 22.sp, modifier = Modifier.scale(0.9f))
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Trophies",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DarkText.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Composable
fun RaceTrackTab(viewModel: MainViewModel) {
    val raceState by viewModel.raceState.collectAsState()

    AnimatedContent(
        targetState = raceState,
        transitionSpec = {
            fadeIn(animationSpec = spring(stiffness = Spring.StiffnessLow)) togetherWith
            fadeOut(animationSpec = spring(stiffness = Spring.StiffnessLow))
        },
        label = "race_state_transition"
    ) { state ->
        when (state) {
            RaceState.IDLE -> SetupRaceScreen(viewModel)
            RaceState.RUNNING -> ActiveRaceScreen(viewModel)
            RaceState.FINISHED -> FinishedRaceScreen(viewModel)
        }
    }
}

@Composable
fun SetupRaceScreen(viewModel: MainViewModel) {
    val selectedOpponent by viewModel.selectedOpponent.collectAsState()
    val selectedDistance by viewModel.selectedDistance.collectAsState()
    val runTitle by viewModel.runTitle.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "RACE REGISTRATION",
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.SansSerif,
                color = DeepPurple,
                letterSpacing = 1.2.sp
            )
        }

        // Custom Run Title input
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, BorderOutline),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🏆 CHAMPIONSHIP ROUND TITLE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = PaleText,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = runTitle,
                        onValueChange = { viewModel.updateTitle(it) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryPurple,
                            unfocusedBorderColor = BorderOutline,
                            focusedTextColor = DarkText,
                            unfocusedTextColor = DarkText,
                            focusedContainerColor = LavenderBg,
                            unfocusedContainerColor = LavenderBg
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                        placeholder = { Text("E.g., Sunset Stroller Dash", color = PaleText.copy(alpha = 0.6f)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("run_title_input")
                    )
                }
            }
        }

        // Select Distance
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, BorderOutline),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "📐 RACING TRACK DISTANCE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = PaleText,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        viewModel.distances.forEach { dist ->
                            val isSelected = selectedDistance == dist
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        if (isSelected) PrimaryPurple else BorderOutline,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .border(
                                        width = 2.dp,
                                        color = if (isSelected) ActiveAccent else Color.Transparent,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable { viewModel.selectDistance(dist) }
                                    .padding(vertical = 12.dp)
                                    .testTag("dist_button_${dist.toInt()}"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${dist.toInt()}m",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 14.sp,
                                    color = if (isSelected) BrightWhite else DarkText
                                )
                            }
                        }
                    }
                }
            }
        }

        // Baby Safety Comfort Guardian Preference Settings
        item {
            val sThreshold by viewModel.soundThreshold.collectAsState()
            val mThreshold by viewModel.motionThreshold.collectAsState()
            val cThreshold by viewModel.comfortThreshold.collectAsState()

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, BorderOutline),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🛡️ BABY COMFORT GUARDIAN PREFERENCES",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = DeepPurple,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Configure custom real-time comfort safety limits to automatically trigger warning notifications during active sessions.",
                        fontSize = 11.sp,
                        color = PaleText,
                        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                    )

                    // Sound Alert Preference Slider
                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("🔊 Max Sound Decibels Limit", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkText)
                            Text("$sThreshold dB", fontSize = 12.sp, fontWeight = FontWeight.Black, color = PrimaryPurple)
                        }
                        Slider(
                            value = sThreshold.toFloat(),
                            onValueChange = { viewModel.soundThreshold.value = it.toInt() },
                            valueRange = 40f..95f,
                            colors = SliderDefaults.colors(
                                thumbColor = PrimaryPurple,
                                activeTrackColor = PrimaryPurple,
                                inactiveTrackColor = BorderOutline
                            ),
                            modifier = Modifier.testTag("sound_threshold_slider")
                        )
                    }

                    // Motion Intensity (Gs) Preference Slider
                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("🌊 Max Stroller Acceleration", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkText)
                            Text(String.format(Locale.getDefault(), "%.2f Gs", mThreshold), fontSize = 12.sp, fontWeight = FontWeight.Black, color = PrimaryPurple)
                        }
                        Slider(
                            value = mThreshold.toFloat(),
                            onValueChange = { viewModel.motionThreshold.value = it.toDouble() },
                            valueRange = 0.5f..2.5f,
                            colors = SliderDefaults.colors(
                                thumbColor = PrimaryPurple,
                                activeTrackColor = PrimaryPurple,
                                inactiveTrackColor = BorderOutline
                            ),
                            modifier = Modifier.testTag("motion_threshold_slider")
                        )
                    }

                    // Comfort Score Preference Slider
                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("⚠️ Min Safety Comfort Rating", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkText)
                            Text("$cThreshold%", fontSize = 12.sp, fontWeight = FontWeight.Black, color = PrimaryPurple)
                        }
                        Slider(
                            value = cThreshold.toFloat(),
                            onValueChange = { viewModel.comfortThreshold.value = it.toInt() },
                            valueRange = 50f..90f,
                            colors = SliderDefaults.colors(
                                thumbColor = PrimaryPurple,
                                activeTrackColor = PrimaryPurple,
                                inactiveTrackColor = BorderOutline
                            ),
                            modifier = Modifier.testTag("comfort_threshold_slider")
                        )
                    }
                }
            }
        }

        // Select Opponent Header
        item {
            Text(
                text = "👥 SELECT AN OPPONENT ON THE FIELD",
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                color = PaleText,
                letterSpacing = 1.sp
            )
        }

        items(viewModel.opponents) { opponent ->
            val isSelected = selectedOpponent == opponent
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) LighterAccent else MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) PrimaryPurple else BorderOutline,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .clickable { viewModel.selectOpponent(opponent) }
                    .testTag("opponent_card_${opponent.name.replace(" ", "_")}")
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .background(LavenderBg, shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = opponent.icon, fontSize = 24.sp)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = opponent.name,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp,
                            color = DarkText
                        )
                        Text(
                            text = opponent.description,
                            fontSize = 11.sp,
                            color = PaleText
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 6.dp)
                        ) {
                            Badge(
                                containerColor = when (opponent.difficulty) {
                                    "Easy" -> StableTeal
                                    "Medium" -> PrimaryPurple
                                    "Hard" -> AlertRed
                                    else -> AlertRed
                                },
                                contentColor = BrightWhite
                            ) {
                                Text(
                                    text = opponent.difficulty,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Avg Speed: ${opponent.averageSpeedKmh} km/h",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = PrimaryPurple
                            )
                        }
                    }
                }
            }
        }

        // Giant Enter Arena Button
        item {
            Button(
                onClick = { viewModel.startRace() },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple, contentColor = BrightWhite),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(top = 8.dp)
                    .testTag("start_race_button")
            ) {
                Text(
                    text = "🟢 ENTER ATHLETIC ARENA TRACK",
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

@Composable
fun ActiveRaceScreen(viewModel: MainViewModel) {
    val selectedDistance by viewModel.selectedDistance.collectAsState()
    val opponent by viewModel.selectedOpponent.collectAsState()
    val pDist by viewModel.parentDistance.collectAsState()
    val oDist by viewModel.opponentDistance.collectAsState()
    val pSpeed by viewModel.parentSpeed.collectAsState()
    val oSpeed by viewModel.opponentSpeed.collectAsState()
    val pHR by viewModel.parentHeartRate.collectAsState()
    val bHR by viewModel.babyHeartRate.collectAsState()
    val bState by viewModel.babyState.collectAsState()
    val vState by viewModel.vibrationState.collectAsState()
    val duration by viewModel.raceDurationSecs.collectAsState()

    // Safety Alert Telemetry states
    val soundDb by viewModel.babySoundLevelDb.collectAsState()
    val motionG by viewModel.babyMotionG.collectAsState()
    val overallComfort by viewModel.overallComfortPct.collectAsState()
    
    val comfortLimit by viewModel.comfortThreshold.collectAsState()
    val soundLimit by viewModel.soundThreshold.collectAsState()
    val motionLimit by viewModel.motionThreshold.collectAsState()

    val activeAlert by viewModel.activeAlert.collectAsState()
    val alertHistory by viewModel.alertHistoryList.collectAsState()

    // Pulse animation logic for the parent heart rate icon
    val hrDelayMs = remember(pHR) {
        val beatsPerSec = pHR.toDouble() / 60.0
        val msPerBeat = if (beatsPerSec > 0) (1000.0 / beatsPerSec).toLong() else 1000L
        msPerBeat.coerceIn(250, 1500)
    }
    
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_trans")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = hrDelayMs.toInt() / 2, 
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Live Timing & Title
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "⚡ TELEMETRY TRACKING FIELD",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.ExtraBold,
                    color = PaleText,
                    letterSpacing = 0.5.sp
                )
                Card(
                    colors = CardDefaults.cardColors(containerColor = AlertRed),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "RUNNING SECS: ${duration}s",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = BrightWhite,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // Live Safety Warning Overlay Alert Banner
        item {
            AnimatedVisibility(
                visible = activeAlert != null,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                activeAlert?.let { alertText ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = AlertRed),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("live_alert_banner")
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text("🚨", fontSize = 28.sp)
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = alertText,
                                    color = BrightWhite,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    text = "TAP DISMISS TO SNOOZE THIS NOTIFICATION",
                                    color = BrightWhite.copy(alpha = 0.8f),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                            Button(
                                onClick = { viewModel.dismissActiveAlert() },
                                colors = ButtonDefaults.buttonColors(containerColor = BrightWhite, contentColor = AlertRed),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .height(32.dp)
                                    .testTag("dismiss_alert_button")
                            ) {
                                Text("DISMISS", fontSize = 10.sp, fontWeight = FontWeight.Black)
                            }
                        }
                    }
                }
            }
        }

        // Live Race Canvas (The High Running Field!)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, BorderOutline)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "🏁 STADIUM CHRONICLES (Target: ${selectedDistance.toInt()} meters)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = DeepPurple
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Track Arena Canvas showing parallel runners
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val trackW = size.width
                            val trackH = size.height
                            val paddingX = 40.dp.toPx()
                            
                            // Draw lanes background
                            val laneH = trackH / 3.0f
                            
                            // Ground lane lines
                            for (i in 0..3) {
                                val y = i * laneH
                                drawLine(
                                    color = BorderOutline,
                                    start = Offset(0f, y),
                                    end = Offset(trackW, y),
                                    strokeWidth = 1.5.dp.toPx()
                                )
                            }

                            // Start grid marker
                            drawLine(
                                color = DarkText,
                                start = Offset(paddingX, 0f),
                                end = Offset(paddingX, trackH),
                                strokeWidth = 3.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 10f))
                            )

                            // Finish line checker lines
                            val finishX = trackW - 20.dp.toPx()
                            drawLine(
                                color = PrimaryPurple,
                                start = Offset(finishX, 0f),
                                end = Offset(finishX, trackH),
                                strokeWidth = 5.dp.toPx()
                            )
                        }

                        // Lanes with real-time avatars!
                        val parentPct = (pDist / selectedDistance).coerceIn(0.0, 1.0)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .align(Alignment.TopStart)
                                .padding(top = 10.dp)
                                .offset(
                                    x = (parentPct * 220).dp
                                )
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "👶🏃", fontSize = 22.sp)
                                Text(
                                    text = "YOU (${String.format(Locale.getDefault(), "%.1f", pDist)}m)",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = DeepPurple,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        // Lane 2: Opponent
                        val opponentPct = (oDist / selectedDistance).coerceIn(0.0, 1.0)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .align(Alignment.BottomStart)
                                .padding(bottom = 15.dp)
                                .offset(
                                    x = (opponentPct * 220).dp
                                )
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = opponent.icon, fontSize = 22.sp)
                                Text(
                                    text = "${opponent.name} (${String.format(Locale.getDefault(), "%.1f", oDist)}m)",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = StableTeal,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }

        // Live Speed displays
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, BorderOutline),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("PARENT SPEED", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = PaleText)
                        Text(
                            "${String.format(Locale.getDefault(), "%.1f", pSpeed)} km/h",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = PrimaryPurple
                        )
                    }
                }
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, BorderOutline),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("OPPONENT SPEED", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = PaleText)
                        Text(
                            "${String.format(Locale.getDefault(), "%.1f", oSpeed)} km/h",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = StableTeal
                        )
                    }
                }
            }
        }

        // Vitals Dashboard
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, BorderOutline),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "📡 LIVE BIOMETRIC TELEMETRY",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = DeepPurple
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Parent BPM
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = "Pulsing Heart",
                                tint = AlertRed,
                                modifier = Modifier
                                    .size(28.dp)
                                    .scale(pulseScale)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("PARENT HEART RATE", fontSize = 9.sp, color = PaleText)
                            Text(
                                "$pHR BPM",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = AlertRed
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (pHR > 165) "CRITICAL EFFORT 🔥" else if (pHR > 130) "CARDIO ZONE ⚡" else "WARMUP ZONE 🍃",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (pHR > 165) AlertRed else if (pHR > 130) PrimaryPurple else StableTeal
                            )
                        }

                        // Split Divider
                        Box(modifier = Modifier.width(1.dp).height(50.dp).background(BorderOutline).align(Alignment.CenterVertically))

                        // Baby BPM
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "👶❤️", fontSize = 22.sp, modifier = Modifier.padding(bottom = 6.dp))
                            Text("BABY HEART RATE", fontSize = 9.sp, color = PaleText)
                            Text(
                                "$bHR BPM",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = StableTeal
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "SAFE HEALTH BEAT",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = StableTeal
                            )
                        }
                    }
                }
            }
        }

        // Real-Time Safety Guardrails Dashboard
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, BorderOutline),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🛡️ REAL-TIME COMFORT & VIBRATION MATRIX",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = DeepPurple,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Line 1: Overall Comfort Index
                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("💖 Overall Comfort Rating", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkText)
                                Badge(
                                    containerColor = if (overallComfort >= comfortLimit) StableTeal else AlertRed,
                                    contentColor = BrightWhite
                                ) {
                                    Text(
                                        text = if (overallComfort >= comfortLimit) "COMFORTABLE" else "ALERT! 🚨",
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text("$overallComfort% / Min $comfortLimit%", fontSize = 12.sp, fontWeight = FontWeight.Black, color = if (overallComfort >= comfortLimit) StableTeal else AlertRed)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { overallComfort.toFloat() / 100f },
                            modifier = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(5.dp)),
                            color = if (overallComfort >= comfortLimit) StableTeal else AlertRed,
                            trackColor = BorderOutline
                        )
                        Text(
                            text = "Status: $bState",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepPurple,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    HorizontalDivider(color = BorderOutline, modifier = Modifier.padding(vertical = 10.dp))

                    // Line 2: Audio Decibels
                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("🔊 Baby Noise Level", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkText)
                                if (soundDb > soundLimit) {
                                    Badge(containerColor = AlertRed, contentColor = BrightWhite) {
                                        Text("DISTRESSED", fontSize = 8.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                    }
                                }
                            }
                            Text("$soundDb dB | Limit: $soundLimit dB", fontSize = 12.sp, fontWeight = FontWeight.Black, color = if (soundDb <= soundLimit) DarkText else AlertRed)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { (soundDb / 100f).coerceIn(0f, 1f) },
                            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                            color = if (soundDb <= soundLimit) StableTeal else AlertRed,
                            trackColor = BorderOutline
                        )
                    }

                    HorizontalDivider(color = BorderOutline, modifier = Modifier.padding(vertical = 10.dp))

                    // Line 3: Motion Intensity (G-force / Smoothness)
                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("🌊 Stroller Smoothness", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkText)
                                if (motionG > motionLimit) {
                                    Badge(containerColor = AlertRed, contentColor = BrightWhite) {
                                        Text("ROUGH GAIT", fontSize = 8.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                    }
                                }
                            }
                            Text("${String.format(Locale.getDefault(), "%.2f Gs", motionG)} | Safe Limit: ${String.format(Locale.getDefault(), "%.2f Gs", motionLimit)}", fontSize = 12.sp, fontWeight = FontWeight.Black, color = if (motionG <= motionLimit) DarkText else AlertRed)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { (motionG / 2.5f).toFloat().coerceIn(0f, 1f) },
                            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                            color = if (motionG <= motionLimit) StableTeal else AlertRed,
                            trackColor = BorderOutline
                        )
                        Text(
                            text = "Turbulence Index: $vState",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (motionG > motionLimit) AlertRed else StableTeal,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }

        // Live terminal event list of triggered safety guardrail warnings
        if (alertHistory.isNotEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SoftContainer),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "📜 TELEMETRY ALERTS HISTORY LOG",
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = DeepPurple
                            )
                            TextButton(
                                onClick = { viewModel.clearAlertHistory() },
                                contentPadding = PaddingValues(0.dp),
                                modifier = Modifier.height(20.dp)
                            ) {
                                Text("Clear Live Log", fontSize = 9.sp, color = AlertRed, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            alertHistory.take(3).forEach { log ->
                                Text(
                                    text = log,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = DarkText,
                                    lineHeight = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Spacer push
        item {
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Large Tap Sprint Boost Trigger
        item {
            Button(
                onClick = { viewModel.triggerSprintBoost() },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple, contentColor = BrightWhite),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .testTag("sprint_boost_button")
            ) {
                Text(
                    text = "⚡ TAP TO SPRINT BOOST ⚡",
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    letterSpacing = 1.5.sp
                )
            }
        }

        item {
            OutlinedButton(
                onClick = { viewModel.cancelRaceAndReset() },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = AlertRed),
                border = BorderStroke(1.dp, AlertRed),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("abandon_race_button")
            ) {
                Text("🛑 ABANDON CHAMPIONSHIP MATCH", fontSize = 11.sp, fontFamily = FontFamily.Monospace)
            }
        }
    }
}

@Composable
fun FinishedRaceScreen(viewModel: MainViewModel) {
    val winner by viewModel.raceWinner.collectAsState()
    val opponent by viewModel.selectedOpponent.collectAsState()
    val selectedDistance by viewModel.selectedDistance.collectAsState()
    val duration by viewModel.raceDurationSecs.collectAsState()
    val parentSpeed by viewModel.parentSpeed.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(90.dp)
                .background(LighterAccent, shape = CircleShape)
                .border(2.dp, PrimaryPurple, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "🏆", fontSize = 48.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "CHAMPIONSHIP COMPLETED!",
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Black,
            fontSize = 18.sp,
            color = DeepPurple,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            border = BorderStroke(1.dp, BorderOutline),
            shape = RoundedCornerShape(28.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "CHAMPIONSHIP SECURED WINNER",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = PaleText
                )
                Text(
                    text = winner,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = DeepPurple,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                HorizontalDivider(color = BorderOutline, modifier = Modifier.padding(vertical = 12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("DISTANCE", fontSize = 9.sp, color = PaleText)
                        Text("${selectedDistance.toInt()}m", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = PrimaryPurple)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("DURATION", fontSize = 9.sp, color = PaleText)
                        Text("${duration}s", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = PrimaryPurple)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("TOP SPEED", fontSize = 9.sp, color = PaleText)
                        Text("${String.format("%.1f", parentSpeed)}km/h", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = PrimaryPurple)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { viewModel.cancelRaceAndReset() },
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple, contentColor = BrightWhite),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("new_race_button")
        ) {
            Text("🔄 STAGE ANOTHER RACE ON THE TRACK", fontWeight = FontWeight.Black, fontSize = 14.sp)
        }
    }
}

@Composable
fun LiveTowerTab(viewModel: MainViewModel) {
    val aiCommentary by viewModel.aiCommentary.collectAsState()
    val aiLoading by viewModel.aiLoading.collectAsState()

    // Commentary Broadcaster audio bars simulator
    val infiniteTransition = rememberInfiniteTransition(label = "audio_vibe")
    val barScales = List(12) { index ->
        infiniteTransition.animateFloat(
            initialValue = 0.2f,
            targetValue = 1.0f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 300 + index * 90,
                    easing = FastOutLinearInEasing
                ),
                repeatMode = RepeatMode.Reverse
            ),
            label = "bar_scale_$index"
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "🎙️ AI ARENA COMMENTARY TOWER",
            fontSize = 15.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = FontFamily.SansSerif,
            color = DeepPurple,
            letterSpacing = 1.sp
        )

        if (aiLoading) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                border = BorderStroke(1.dp, BorderOutline),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = PrimaryPurple)
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = "LIVE BROADCAST COMPILING...",
                        fontSize = 14.sp,
                        fontFamily = FontFamily.Monospace,
                        color = PrimaryPurple,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Display funny commentator stages
                    Text(
                        text = aiCommentary,
                        fontSize = 12.sp,
                        color = PaleText,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else if (aiCommentary.isNotEmpty()) {
            // Broadcaster visualizer header
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, BorderOutline),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(LighterAccent, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🎙️", fontSize = 18.sp)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("P.A. BROADCASTER ANNOUNCING", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = DeepPurple)
                        Text("Sensors: Smart Runner Active Pro", fontSize = 9.sp, color = PaleText)
                    }
                    
                    // Audio bouncing bars
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.height(24.dp)
                    ) {
                        barScales.forEach { scale ->
                            Box(
                                modifier = Modifier
                                    .width(3.dp)
                                    .fillMaxHeight(scale.value)
                                    .background(PrimaryPurple)
                            )
                        }
                    }
                }
            }

            // Detailed comment card
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                border = BorderStroke(1.dp, BorderOutline),
                shape = RoundedCornerShape(24.dp)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Text(
                            text = "OFFICIAL ARENA ANALYSIS",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.ExtraBold,
                            color = PaleText
                        )
                    }
                    item {
                        Text(
                            text = aiCommentary,
                            fontSize = 15.sp,
                            lineHeight = 22.sp,
                            fontWeight = FontWeight.Medium,
                            color = DarkText
                        )
                    }
                }
            }
        } else {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                border = BorderStroke(1.dp, BorderOutline),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🏟️",
                        fontSize = 54.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    Text(
                        text = "ARENA SPEAKER SILENT",
                        fontSize = 14.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black,
                        color = DarkText
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "No active race recorded. Initiate and finish your run/stroller race on the TRACK to trigger live smart commentary!",
                        fontSize = 12.sp,
                        color = PaleText,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun TrophyHistoryTab(viewModel: MainViewModel) {
    val runHistory by viewModel.runHistory.collectAsState()
    var expandedRecordId by remember { mutableStateOf<Int?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "🏆 GRAND ARENA TROPHY CASE",
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily.SansSerif,
                color = DeepPurple,
                letterSpacing = 1.sp
            )
            if (runHistory.isNotEmpty()) {
                TextButton(
                    onClick = { viewModel.clearAllHistory() },
                    modifier = Modifier.testTag("clear_history_button")
                ) {
                    Text("Clear Case", color = AlertRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (runHistory.isEmpty()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                border = BorderStroke(1.dp, BorderOutline),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🕸️",
                        fontSize = 54.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    Text(
                        text = "TROPHY CASE VACANT",
                        fontSize = 14.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black,
                        color = DarkText
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Complete real runs on the high running field against rivals to secure your historical championship trophies here!",
                        fontSize = 11.sp,
                        color = PaleText,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(runHistory, key = { it.id }) { record ->
                    val isExpanded = expandedRecordId == record.id
                    val formatter = remember { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()) }
                    val dateFormatted = remember(record.timestamp) { formatter.format(Date(record.timestamp)) }

                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                1.dp,
                                if (record.gameWinner.contains("YOU")) PrimaryPurple else BorderOutline,
                                RoundedCornerShape(16.dp)
                            )
                            .clickable {
                                expandedRecordId = if (isExpanded) null else record.id
                            }
                            .testTag("history_card_${record.id}")
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = record.title,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 15.sp,
                                        color = DarkText
                                    )
                                    Text(
                                        text = "Ran on $dateFormatted | vs: ${record.opponentName}",
                                        fontSize = 11.sp,
                                        color = PaleText
                                    )
                                }
                                
                                IconButton(
                                    onClick = { viewModel.deleteHistoryRecord(record.id) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete run",
                                        tint = AlertRed.copy(alpha = 0.7f),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Column {
                                        Text("DIST", fontSize = 8.sp, color = PaleText)
                                        Text("${record.distanceMeters.toInt()}m", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryPurple)
                                    }
                                    Column {
                                        Text("PACE", fontSize = 8.sp, color = PaleText)
                                        Text("${String.format("%.1f", record.avgSpeedKmh)} km/h", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryPurple)
                                    }
                                    Column {
                                        Text("BABY", fontSize = 8.sp, color = PaleText)
                                        Text(record.babyState.take(11), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DarkText)
                                    }
                                }
                                Box(
                                    modifier = Modifier
                                        .background(
                                            if (record.gameWinner.contains("YOU")) PrimaryPurple else BorderOutline,
                                            RoundedCornerShape(6.dp)
                                        )
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = if (record.gameWinner.contains("YOU")) "VICTORY 🏆" else "DEFEAT 🤖",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        color = if (record.gameWinner.contains("YOU")) BrightWhite else DarkText
                                    )
                                }
                            }

                            // Expanded AI Play-by-Play commentary
                            AnimatedVisibility(
                                visible = isExpanded,
                                enter = expandVertically() + fadeIn(),
                                exit = shrinkVertically() + fadeOut()
                            ) {
                                Column(modifier = Modifier.padding(top = 12.dp)) {
                                    HorizontalDivider(color = BorderOutline, modifier = Modifier.padding(bottom = 8.dp))
                                    Text(
                                        text = "📻 BROADCASTER ARCHIVE FEED:",
                                        fontSize = 9.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = DeepPurple,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = record.aiCommentary,
                                        fontSize = 12.sp,
                                        letterSpacing = 0.5.sp,
                                        color = DarkText,
                                        modifier = Modifier.padding(top = 4.dp),
                                        lineHeight = 17.sp
                                    )
                                    
                                    Spacer(modifier = Modifier.height(8.dp))
                                    
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(SoftContainer, RoundedCornerShape(8.dp))
                                            .padding(6.dp),
                                        horizontalArrangement = Arrangement.SpaceAround
                                    ) {
                                        Text("Parent HR: ${record.avgParentHeartRate} BPM", fontSize = 10.sp, color = PaleText)
                                        Text("Baby HR: ${record.avgBabyHeartRate} BPM", fontSize = 10.sp, color = PaleText)
                                        Text("Vibes: ${record.strollerVibration.take(6)}", fontSize = 10.sp, color = PaleText)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SecurityWarningText() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "⚠️ Security Warning: I have included your API keys in the generated APK file for this prototype. Please be aware that Android APKs can be easily decompiled, and these keys can be extracted by anyone who has access to the file. Do not share this APK file publicly or with unauthorized individuals to prevent potential misuse.",
            fontSize = 8.sp,
            color = PaleText,
            textAlign = TextAlign.Center,
            lineHeight = 11.sp,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
    }
}
