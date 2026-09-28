// Interfaz del repositorio
interface MarsPhotosRepository {
    suspend fun getMarsPhotos(): List<MarsPhoto>
}

// Implementación del repositorio
class NetworkMarsPhotosRepository(
    private val marsApiService: MarsApiService
) : MarsPhotosRepository {
    override suspend fun getMarsPhotos(): List<MarsPhoto> =
        marsApiService.getPhotos()
}

// Ejemplo simple de dependencia
interface Engine { fun start() }

class GasEngine : Engine {
    override fun start() {
        println("GasEngine started!")
    }
}

class Car(private val engine: Engine) {
    fun start() {
        engine.start()
    }
}

fun main() {
    val engine = GasEngine()
    val car = Car(engine)
    car.start()
}

// Contenedor de dependencias
interface AppContainer {
    val marsPhotosRepository: MarsPhotosRepository
}

class DefaultAppContainer : AppContainer {

    private val baseUrl =
        "https://android-kotlin-fun-mars-server.appspot.com"

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(
            Json.asConverterFactory(
                "application/json".toMediaType()
            )
        )
        .baseUrl(baseUrl)
        .build()

    private val retrofitService: MarsApiService by lazy {
        retrofit.create(MarsApiService::class.java)
    }

    override val marsPhotosRepository: MarsPhotosRepository by lazy {
        NetworkMarsPhotosRepository(retrofitService)
    }
}

// Application personalizada
class MarsPhotosApplication : Application() {

    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer()
    }
}

// AndroidManifest.xml
/*
<application
    android:name=".MarsPhotosApplication"
    ... >
</application>
*/

// Inyectar repositorio en ViewModel
class MarsViewModel(
    private val marsPhotosRepository: MarsPhotosRepository
) : ViewModel() {

    private fun getMarsPhotos() {
        viewModelScope.launch {
            marsUiState = try {
                MarsUiState.Success(
                    marsPhotosRepository.getMarsPhotos()
                )
            } catch (e: IOException) {
                MarsUiState.Error
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    this[APPLICATION_KEY] as MarsPhotosApplication

                val repository =
                    application.container.marsPhotosRepository

                MarsViewModel(
                    marsPhotosRepository = repository
                )
            }
        }
    }
}

// Usar Factory desde Compose
@Composable
fun MarsPhotosApp() {
    val marsViewModel: MarsViewModel = viewModel(
        factory = MarsViewModel.Factory
    )
}

// Dependencias para pruebas locales
/*
testImplementation("junit:junit:4.13.2")
testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.1")
*/

// Datos falsos
object FakeDataSource {
    val photosList = listOf(
        MarsPhoto(id = "img1", imgSrc = "url.1"),
        MarsPhoto(id = "img2", imgSrc = "url.2")
    )
}

// Servicio API falso
class FakeMarsApiService : MarsApiService {
    override suspend fun getPhotos(): List<MarsPhoto> {
        return FakeDataSource.photosList
    }
}

// Prueba del repositorio
@Test
fun networkMarsPhotosRepository_getMarsPhotos_verifyPhotoList() = runTest {

    val repository = NetworkMarsPhotosRepository(
        marsApiService = FakeMarsApiService()
    )

    assertEquals(
        FakeDataSource.photosList,
        repository.getMarsPhotos()
    )
}

// Repositorio falso para probar ViewModel
class FakeNetworkMarsPhotosRepository : MarsPhotosRepository {
    override suspend fun getMarsPhotos(): List<MarsPhoto> {
        return FakeDataSource.photosList
    }
}

// Regla para Dispatchers.Main en pruebas
class TestDispatcherRule(
    val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
) : TestWatcher() {

    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}

class MarsViewModelTest {
    @get:Rule
    val testDispatcher = TestDispatcherRule()
}
