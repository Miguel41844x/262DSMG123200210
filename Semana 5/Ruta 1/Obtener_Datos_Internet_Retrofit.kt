// Dependencias Retrofit
/*
implementation("com.squareup.retrofit2:retrofit:2.9.0")
implementation("com.squareup.retrofit2:converter-scalars:2.9.0")
*/

// URL base y Retrofit
private const val BASE_URL =
    "https://android-kotlin-fun-mars-server.appspot.com"

private val retrofit = Retrofit.Builder()
    .addConverterFactory(
        ScalarsConverterFactory.create()
    )
    .baseUrl(BASE_URL)
    .build()

// Servicio API con GET
interface MarsApiService {

    @GET("photos")
    suspend fun getPhotos(): String
}

// Singleton y lazy
object MarsApi {

    val retrofitService: MarsApiService by lazy {
        retrofit.create(
            MarsApiService::class.java
        )
    }
}

// viewModelScope
class MarsViewModel : ViewModel() {

    private fun getMarsPhotos() {

        viewModelScope.launch {
            val listResult =
                MarsApi
                    .retrofitService
                    .getPhotos()

            println(listResult)
        }
    }
}

// Permiso de Internet - AndroidManifest.xml
/*
<uses-permission
    android:name="android.permission.INTERNET" />
*/

// Manejo de excepciones
private fun getMarsPhotos() {

    viewModelScope.launch {

        try {
            val result =
                MarsApi
                    .retrofitService
                    .getPhotos()

        } catch (e: IOException) {
            println("Network error")
        }
    }
}

// Estados de la interfaz
sealed interface MarsUiState {

    data class Success(
        val photos: String
    ) : MarsUiState

    object Error : MarsUiState

    object Loading : MarsUiState
}

// Estado inicial
var marsUiState: MarsUiState by
    mutableStateOf(
        MarsUiState.Loading
    )
    private set

// Loading, Success y Error
private fun getMarsPhotos() {

    viewModelScope.launch {

        marsUiState = try {

            val result =
                MarsApi
                    .retrofitService
                    .getPhotos()

            MarsUiState.Success(result)

        } catch (e: IOException) {

            MarsUiState.Error
        }
    }
}

// Mostrar pantalla según estado
@Composable
fun HomeScreen(
    marsUiState: MarsUiState
) {

    when (marsUiState) {

        is MarsUiState.Loading -> {
            LoadingScreen()
        }

        is MarsUiState.Success -> {
            ResultScreen(
                marsUiState.photos
            )
        }

        is MarsUiState.Error -> {
            ErrorScreen()
        }
    }
}

// kotlinx.serialization
/*
plugins {
    id("org.jetbrains.kotlin.plugin.serialization")
}

implementation(
    "org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1"
)

implementation(
    "com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:1.0.0"
)

implementation(
    "com.squareup.okhttp3:okhttp:4.11.0"
)
*/

// Clase de datos para JSON
@Serializable
data class MarsPhoto(
    val id: String,

    @SerialName("img_src")
    val imgSrc: String
)

// Retrofit con Kotlin Serialization
private val retrofit = Retrofit.Builder()
    .addConverterFactory(
        Json.asConverterFactory(
            "application/json".toMediaType()
        )
    )
    .baseUrl(BASE_URL)
    .build()

// Lista de objetos MarsPhoto
interface MarsApiService {

    @GET("photos")
    suspend fun getPhotos(): List<MarsPhoto>
}

// Usar la lista recibida
private fun getMarsPhotos() {

    viewModelScope.launch {

        marsUiState = try {

            val photos =
                MarsApi
                    .retrofitService
                    .getPhotos()

            MarsUiState.Success(
                "Success: ${photos.size} Mars photos retrieved"
            )

        } catch (e: IOException) {

            MarsUiState.Error
        }
    }
}
