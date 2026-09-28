// Código síncrono
fun main() {
    println("Weather forecast")
    println("Sunny")
}

// delay() dentro de runBlocking
import kotlinx.coroutines.*

fun main() {
    runBlocking {
        println("Weather forecast")
        delay(1000)
        println("Sunny")
    }
}

// Funciones suspend
suspend fun printForecast() {
    delay(1000)
    println("Sunny")
}

suspend fun printTemperature() {
    delay(1000)
    println("30°C")
}

// launch - corrutinas simultáneas
fun main() {
    runBlocking {
        println("Weather forecast")

        launch { printForecast() }
        launch { printTemperature() }

        println("Have a good day!")
    }
}

// async y await
suspend fun getForecast(): String {
    delay(1000)
    return "Sunny"
}

suspend fun getTemperature(): String {
    delay(1000)
    return "30°C"
}

fun main() {
    runBlocking {
        val forecast = async { getForecast() }
        val temperature = async { getTemperature() }

        println("${forecast.await()} ${temperature.await()}")
    }
}

// coroutineScope
suspend fun getWeatherReport(): String = coroutineScope {
    val forecast = async { getForecast() }
    val temperature = async { getTemperature() }

    "${forecast.await()} ${temperature.await()}"
}

// Manejo de excepciones
fun main() {
    runBlocking {
        try {
            println(getWeatherReport())
        } catch (e: Exception) {
            println("Error: ${e.message}")
        }
    }
}

// Cancelar una corrutina
suspend fun getWeatherReport(): String = coroutineScope {
    val forecast = async { getForecast() }
    val temperature = async { getTemperature() }

    delay(200)
    temperature.cancel()

    forecast.await()
}

// Job
fun main() {
    runBlocking {
        val job = launch {
            delay(1000)
            println("Task finished")
        }

        job.cancel()
    }
}

// Jerarquía de Jobs
fun main() {
    runBlocking {
        val parentJob = launch {
            val childJob = launch {
                println("Child coroutine")
            }

            println("Parent coroutine")
        }
    }
}

// CoroutineContext
val context = Job() + Dispatchers.Main

// Dispatchers
val mainDispatcher = Dispatchers.Main
val ioDispatcher = Dispatchers.IO
val defaultDispatcher = Dispatchers.Default

// withContext - cambiar de dispatcher
fun main() {
    runBlocking {
        launch {
            println("${Thread.currentThread().name} - launch")

            withContext(Dispatchers.Default) {
                println("${Thread.currentThread().name} - withContext")
                delay(1000)
                println("10 results found.")
            }

            println("${Thread.currentThread().name} - end")
        }

        println("Loading...")
    }
}
