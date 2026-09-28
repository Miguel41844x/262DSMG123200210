// Función suspend para ejecutar la carrera
class RaceParticipant(
    val name: String,
    val maxProgress: Int = 100,
    val progressDelayMillis: Long = 500L,
    private val progressIncrement: Int = 1
) {
    var currentProgress by mutableStateOf(0)
        private set

    suspend fun run() {
        while (currentProgress < maxProgress) {
            delay(progressDelayMillis)
            currentProgress += progressIncrement
        }
    }
}

// LaunchedEffect
@Composable
fun RaceTrackerApp(
    playerOne: RaceParticipant,
    playerTwo: RaceParticipant
) {
    LaunchedEffect(playerOne, playerTwo) {
        playerOne.run()
        playerTwo.run()
    }
}

// Ejecutar según un estado
@Composable
fun RaceTrackerApp(
    playerOne: RaceParticipant,
    playerTwo: RaceParticipant
) {
    var raceInProgress by remember {
        mutableStateOf(false)
    }

    if (raceInProgress) {
        LaunchedEffect(playerOne, playerTwo) {
            playerOne.run()
            playerTwo.run()
        }
    }
}

// launch - dos participantes simultáneamente
LaunchedEffect(playerOne, playerTwo) {
    launch { playerOne.run() }
    launch { playerTwo.run() }
}

// coroutineScope - esperar a ambos participantes
LaunchedEffect(playerOne, playerTwo) {
    coroutineScope {
        launch { playerOne.run() }
        launch { playerTwo.run() }
    }

    raceInProgress = false
}

// CancellationException
suspend fun runRace() {
    try {
        while (currentProgress < maxProgress) {
            delay(progressDelayMillis)
            currentProgress += progressIncrement
        }
    } catch (e: CancellationException) {
        Log.e("RaceParticipant", "$name: ${e.message}")
        throw e
    }
}

// Dependencia de pruebas
/*
testImplementation(
    "org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4"
)
*/

// runTest
class RaceParticipantTest {

    private val raceParticipant = RaceParticipant(
        name = "Player 1",
        progressDelayMillis = 500L
    )

    @Test
    fun raceStarted_progressUpdated() = runTest {
        launch {
            raceParticipant.run()
        }
    }
}

// advanceTimeBy y runCurrent
@Test
fun raceStarted_progressIsOne() = runTest {
    val expectedProgress = 1

    launch {
        raceParticipant.run()
    }

    advanceTimeBy(
        raceParticipant.progressDelayMillis
    )

    runCurrent()

    assertEquals(
        expectedProgress,
        raceParticipant.currentProgress
    )
}

// Probar el final de la carrera
@Test
fun raceFinished_progressIsMax() = runTest {

    launch {
        raceParticipant.run()
    }

    advanceTimeBy(
        raceParticipant.maxProgress *
            raceParticipant.progressDelayMillis
    )

    runCurrent()

    assertEquals(
        raceParticipant.maxProgress,
        raceParticipant.currentProgress
    )
}
