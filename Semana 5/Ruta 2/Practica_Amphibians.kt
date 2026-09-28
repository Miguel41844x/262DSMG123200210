// Modelo de datos
@Serializable
data class Amphibian(
    val name: String,
    val type: String,
    val description: String,
    @SerialName("img_src") val imgSrc: String
)

// Servicio de red
interface AmphibiansApiService {
    @GET("amphibians")
    suspend fun getAmphibians(): List<Amphibian>
}

// Repositorio
interface AmphibiansRepository {
    suspend fun getAmphibians(): List<Amphibian>
}

class NetworkAmphibiansRepository(
    private val apiService: AmphibiansApiService
) : AmphibiansRepository {

    override suspend fun getAmphibians(): List<Amphibian> {
        return apiService.getAmphibians()
    }
}

// Estados de la interfaz
sealed interface AmphibiansUiState {
    data object Loading : AmphibiansUiState
    data class Success(
        val amphibians: List<Amphibian>
    ) : AmphibiansUiState
    data object Error : AmphibiansUiState
}

// ViewModel
class AmphibiansViewModel(
    private val repository: AmphibiansRepository
) : ViewModel() {

    var uiState: AmphibiansUiState by mutableStateOf(
        AmphibiansUiState.Loading
    )
        private set

    fun getAmphibians() {
        viewModelScope.launch {
            uiState = try {
                AmphibiansUiState.Success(
                    repository.getAmphibians()
                )
            } catch (e: IOException) {
                AmphibiansUiState.Error
            }
        }
    }
}

// Mostrar pantalla según UiState
@Composable
fun AmphibiansScreen(
    uiState: AmphibiansUiState
) {
    when (uiState) {
        is AmphibiansUiState.Loading -> LoadingScreen()
        is AmphibiansUiState.Success -> AmphibiansList(
            amphibians = uiState.amphibians
        )
        is AmphibiansUiState.Error -> ErrorScreen()
    }
}

// Lista desplazable
@Composable
fun AmphibiansList(
    amphibians: List<Amphibian>
) {
    LazyColumn {
        items(amphibians) { amphibian ->
            AmphibianCard(amphibian = amphibian)
        }
    }
}

// Tarjeta de un anfibio
@Composable
fun AmphibianCard(
    amphibian: Amphibian
) {
    Card {
        Column {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(amphibian.imgSrc)
                    .crossfade(true)
                    .build(),
                contentDescription = amphibian.name,
                contentScale = ContentScale.Crop
            )

            Text(text = amphibian.name)
            Text(text = amphibian.type)
            Text(text = amphibian.description)
        }
    }
}

// Contenedor de dependencias
interface AppContainer {
    val amphibiansRepository: AmphibiansRepository
}

class DefaultAppContainer : AppContainer {

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(
            Json.asConverterFactory(
                "application/json".toMediaType()
            )
        )
        .baseUrl(
            "https://android-kotlin-fun-mars-server.appspot.com/"
        )
        .build()

    private val apiService: AmphibiansApiService by lazy {
        retrofit.create(AmphibiansApiService::class.java)
    }

    override val amphibiansRepository: AmphibiansRepository by lazy {
        NetworkAmphibiansRepository(apiService)
    }
}
