// Dependencia de Coil
/*
implementation("io.coil-kt:coil-compose:2.4.0")
*/

// AsyncImage básico
AsyncImage(
    model = "https://android.com/sample_image.jpg",
    contentDescription = null
)

// ImageRequest
AsyncImage(
    model = ImageRequest.Builder(LocalContext.current)
        .data("https://example.com/image.jpg")
        .crossfade(true)
        .build(),
    contentDescription = stringResource(R.string.description)
)

// Tarjeta para una foto de Marte
@Composable
fun MarsPhotoCard(
    photo: MarsPhoto,
    modifier: Modifier = Modifier
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(photo.imgSrc)
            .crossfade(true)
            .build(),
        contentDescription = stringResource(R.string.mars_photo),
        modifier = modifier.fillMaxWidth()
    )
}

// Success con una foto
sealed interface MarsUiState {
    data class Success(val photos: MarsPhoto) : MarsUiState
    object Error : MarsUiState
    object Loading : MarsUiState
}

// Mostrar pantalla según estado
@Composable
fun HomeScreen(
    marsUiState: MarsUiState,
    modifier: Modifier = Modifier
) {
    when (marsUiState) {
        is MarsUiState.Loading -> LoadingScreen(modifier.fillMaxSize())
        is MarsUiState.Success -> MarsPhotoCard(
            photo = marsUiState.photos,
            modifier = modifier.fillMaxSize()
        )
        is MarsUiState.Error -> ErrorScreen(modifier.fillMaxSize())
    }
}

// Recuperar la primera foto
marsUiState = try {
    MarsUiState.Success(
        marsPhotosRepository.getMarsPhotos()[0]
    )
} catch (e: IOException) {
    MarsUiState.Error
}

// ContentScale.Crop + carga y error
AsyncImage(
    model = ImageRequest.Builder(LocalContext.current)
        .data(photo.imgSrc)
        .crossfade(true)
        .build(),
    error = painterResource(R.drawable.ic_broken_image),
    placeholder = painterResource(R.drawable.loading_img),
    contentDescription = stringResource(R.string.mars_photo),
    contentScale = ContentScale.Crop
)

// Success ahora contiene una lista
sealed interface MarsUiState {
    data class Success(
        val photos: List<MarsPhoto>
    ) : MarsUiState

    object Error : MarsUiState
    object Loading : MarsUiState
}

// LazyVerticalGrid
@Composable
fun PhotosGridScreen(
    photos: List<MarsPhoto>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(150.dp),
        modifier = modifier.padding(horizontal = 4.dp),
        contentPadding = contentPadding
    ) {
        items(
            items = photos,
            key = { photo -> photo.id }
        ) { photo ->
            MarsPhotoCard(photo = photo)
        }
    }
}

// Mostrar cuadrícula al recibir Success
when (marsUiState) {
    is MarsUiState.Loading -> LoadingScreen()
    is MarsUiState.Success -> PhotosGridScreen(marsUiState.photos)
    is MarsUiState.Error -> ErrorScreen()
}

// Card alrededor de AsyncImage
@Composable
fun MarsPhotoCard(
    photo: MarsPhoto,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        )
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(photo.imgSrc)
                .crossfade(true)
                .build(),
            error = painterResource(R.drawable.ic_broken_image),
            placeholder = painterResource(R.drawable.loading_img),
            contentDescription = stringResource(R.string.mars_photo),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.5f)
        )
    }
}
