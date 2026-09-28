// Modelos de datos
data class BooksResponse(
    val items: List<BookItem> = emptyList()
)

data class BookItem(
    val id: String,
    val volumeInfo: VolumeInfo
)

data class VolumeInfo(
    val title: String,
    val imageLinks: ImageLinks?
)

data class ImageLinks(
    val thumbnail: String?
)

// Retrofit - Google Books API
interface BooksApiService {

    @GET("books/v1/volumes")
    suspend fun searchBooks(
        @Query("q") query: String
    ): BooksResponse

    @GET("books/v1/volumes/{id}")
    suspend fun getBook(
        @Path("id") id: String
    ): BookItem
}

// Retrofit con Gson
private const val BASE_URL =
    "https://www.googleapis.com/"

private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(
        GsonConverterFactory.create()
    )
    .build()

// Repositorio
interface BooksRepository {
    suspend fun searchBooks(
        query: String
    ): List<BookItem>
}

class NetworkBooksRepository(
    private val apiService: BooksApiService
) : BooksRepository {

    override suspend fun searchBooks(
        query: String
    ): List<BookItem> {
        return apiService.searchBooks(query).items
    }
}

// Obtener información individual de cada libro
suspend fun getBookDetails(
    query: String
): List<BookItem> {

    val searchResult = apiService.searchBooks(query)
    val books = mutableListOf<BookItem>()

    for (item in searchResult.items) {
        val book = apiService.getBook(item.id)
        books.add(book)
    }

    return books
}

// Convertir http a https para miniaturas
val secureThumbnail =
    book.volumeInfo.imageLinks?.thumbnail
        ?.replace("http://", "https://")

// Estados de la interfaz
sealed interface BooksUiState {
    data object Loading : BooksUiState
    data class Success(
        val books: List<BookItem>
    ) : BooksUiState
    data object Error : BooksUiState
}

// ViewModel
class BooksViewModel(
    private val repository: BooksRepository
) : ViewModel() {

    var uiState: BooksUiState by mutableStateOf(
        BooksUiState.Loading
    )
        private set

    fun searchBooks(query: String) {
        viewModelScope.launch {
            uiState = try {
                BooksUiState.Success(
                    repository.searchBooks(query)
                )
            } catch (e: IOException) {
                BooksUiState.Error
            }
        }
    }
}

// Cuadrícula de libros
@Composable
fun BooksGrid(
    books: List<BookItem>
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(
            minSize = 150.dp
        )
    ) {
        items(
            items = books,
            key = { book -> book.id }
        ) { book ->
            BookCard(book)
        }
    }
}

// Mostrar miniatura con Coil
@Composable
fun BookCard(
    book: BookItem
) {
    val imageUrl =
        book.volumeInfo.imageLinks?.thumbnail
            ?.replace("http://", "https://")

    Column {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = book.volumeInfo.title,
            contentScale = ContentScale.Crop
        )

        Text(text = book.volumeInfo.title)
    }
}

// Servicio falso para pruebas
class FakeBooksApiService : BooksApiService {

    override suspend fun searchBooks(
        query: String
    ): BooksResponse {
        return BooksResponse(
            items = FakeBooksData.books
        )
    }

    override suspend fun getBook(
        id: String
    ): BookItem {
        return FakeBooksData.books.first {
            it.id == id
        }
    }
}

// Prueba del repositorio
@Test
fun networkBooksRepository_searchBooks_returnsBooks() = runTest {

    val repository = NetworkBooksRepository(
        apiService = FakeBooksApiService()
    )

    val result = repository.searchBooks(
        "jazz history"
    )

    assertEquals(
        FakeBooksData.books,
        result
    )
}
