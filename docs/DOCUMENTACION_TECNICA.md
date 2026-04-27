# Documentacion Tecnica — App Biblioteca (Practico 2)

**Asignatura:** Desarrollo de Aplicaciones Moviles  
**Proyecto:** App Biblioteca — Practico 2  
**Paquete:** `com.example.practico_2_app_biblioteca`  
**Equipo:** Diego Arcani (Dip1224), Luis Rivera (LuisRibera)  
**Fecha:** Abril 2026  

---

## Tabla de contenidos

1. [Resumen del proyecto](#1-resumen-del-proyecto)
2. [Stack tecnologico](#2-stack-tecnologico)
3. [Estructura de archivos](#3-estructura-de-archivos)
4. [Arquitectura](#4-arquitectura)
5. [Capa de datos](#5-capa-de-datos)
6. [ViewModels y estados de UI](#6-viewmodels-y-estados-de-ui)
7. [Validacion de formularios](#7-validacion-de-formularios)
8. [Navegacion](#8-navegacion)
9. [Pantallas](#9-pantallas)
10. [Componentes reutilizables](#10-componentes-reutilizables)
11. [Flujos de usuario](#11-flujos-de-usuario)
12. [Decisiones de diseno](#12-decisiones-de-diseno)

---

## 1. Resumen del proyecto

App Biblioteca es una aplicacion Android para la gestion de un catalogo de libros y sus generos. Permite al usuario listar, ver el detalle, crear, editar y eliminar libros, y administrar los generos disponibles para asignarlos a los libros al momento de la creacion.

La aplicacion consume una API REST externa y sigue el patron MVVM de forma estricta. Toda la interfaz se construye con Jetpack Compose y Material 3.

**Capacidades principales:**

- Listado paginado de libros con imagen de portada, titulo y autor.
- Vista de detalle con imagen a pantalla completa, sinopsis, ISBN y calificacion.
- Formulario unificado para crear y editar libros, con seleccion de generos por chips.
- Listado de generos con eliminacion en linea mediante dialogo de confirmacion.
- Formulario de creacion de generos.
- Recarga automatica de listas al volver a la pantalla (ciclo de vida ON_RESUME).
- Prevencion de doble envio en todos los formularios.

---

## 2. Stack tecnologico

### Plataforma

| Componente | Valor |
|---|---|
| Lenguaje | Kotlin 2.2.10 |
| Android Gradle Plugin | 9.1.0 |
| Min SDK | 24 (Android 7.0) |
| Target / Compile SDK | 36 |
| UI | Jetpack Compose + Material 3 |
| Arquitectura | MVVM |

### Dependencias principales

| Libreria | Version | Proposito |
|---|---|---|
| `androidx.compose:compose-bom` | 2024.09.00 | BOM de Compose |
| `androidx.compose.material3` | (BOM) | Componentes de Material 3 |
| `androidx.compose.material:material-icons-extended` | (BOM) | Iconos adicionales (MenuBook, ErrorOutline, Label, etc.) |
| `androidx.navigation:navigation-compose` | 2.8.4 | Navegacion entre pantallas |
| `androidx.lifecycle:lifecycle-viewmodel-compose` | 2.8.7 | Provee `viewModel()` en Composables |
| `androidx.lifecycle:lifecycle-runtime-compose` | 2.8.7 | Provee `collectAsStateWithLifecycle()` |
| `com.squareup.retrofit2:retrofit` | 2.11.0 | Cliente HTTP y serializacion |
| `com.squareup.retrofit2:converter-gson` | 2.11.0 | Conversion JSON via Gson |
| `com.google.code.gson:gson` | 2.11.0 | Serializacion/deserializacion JSON |
| `com.squareup.okhttp3:logging-interceptor` | 4.12.0 | Logging de requests HTTP |
| `io.coil-kt.coil3:coil-compose` | 3.0.4 | Carga asincrona de imagenes |
| `io.coil-kt.coil3:coil-network-okhttp` | 3.0.4 | Backend de red para Coil 3 |

> **Nota sobre Coil 3:** el paquete de importacion es `coil3.compose.AsyncImage`, no `coil.compose`. Cualquier referencia a `coil.compose` es de la version 2 y no es compatible.

---

## 3. Estructura de archivos

```
app/src/main/java/com/example/practico_2_app_biblioteca/
|
|-- MainActivity.kt                         # Entry point: NavController + LibroViewModel raiz
|
|-- data/
|   |-- model/
|   |   |-- LibroDto.kt                     # DTO de lectura para libros
|   |   |-- GeneroDto.kt                    # DTO de lectura para generos
|   |   |-- LibroRequest.kt                 # Body para POST/PUT de libros
|   |   |-- GeneroRequest.kt                # Body para POST de generos
|   |   `-- LibroGeneroRequest.kt           # Body para POST libro-generos
|   |
|   |-- remote/
|   |   |-- ApiService.kt                   # Interface Retrofit con 9 endpoints
|   |   `-- RetrofitClient.kt               # Singleton OkHttp + Retrofit
|   |
|   `-- repository/
|       |-- LibroRepository.kt              # 6 funciones, retorna Result<T>
|       `-- GeneroRepository.kt             # 3 funciones, retorna Result<T>
|
|-- viewmodel/
|   |-- UiEvent.kt                          # sealed interface de eventos unicos
|   |-- LibroViewModel.kt                   # ViewModel + estados sealed para libros
|   `-- GeneroViewModel.kt                  # ViewModel + estados sealed para generos
|
|-- validation/
|   |-- LibroValidator.kt                   # Reglas de validacion para libros
|   `-- GeneroValidator.kt                  # Regla de validacion para generos
|
`-- ui/
    |-- navigation/
    |   |-- Routes.kt                       # Constantes de rutas y constructores
    |   `-- AppNavHost.kt                   # NavHost con 6 destinos
    |
    |-- screen/
    |   |-- libros/
    |   |   |-- LibroListScreen.kt          # Lista de libros con recarga ON_RESUME
    |   |   |-- LibroDetailScreen.kt        # Detalle con acciones Editar/Eliminar
    |   |   `-- LibroFormScreen.kt          # Formulario crear/editar unificado
    |   |
    |   `-- generos/
    |       |-- GeneroListScreen.kt         # Lista de generos con eliminacion inline
    |       `-- GeneroFormScreen.kt         # Formulario de creacion de genero
    |
    |-- components/
    |   |-- LoadingView.kt                  # CircularProgressIndicator centrado
    |   |-- ErrorView.kt                    # Error con icono y boton Reintentar
    |   |-- EmptyView.kt                    # Estado vacio con icono
    |   `-- ConfirmDialog.kt                # AlertDialog generico de confirmacion
    |
    `-- theme/
        |-- Color.kt
        |-- Theme.kt
        `-- Type.kt
```

---

## 4. Arquitectura

### Patron MVVM

El proyecto implementa MVVM de forma estricta sin Hilt, sin casos de uso y sin interfaces de repositorio. La decision de simplicidad es deliberada: el alcance universitario del proyecto no requiere capas adicionales de abstraccion.

```
Composable (View)
     |  observa StateFlow / colecta SharedFlow
     v
  ViewModel
     |  llama funciones suspend
     v
  Repository
     |  llama ApiService
     v
  RetrofitClient --> API REST
```

**Responsabilidades por capa:**

| Capa | Responsabilidad | Restricciones |
|---|---|---|
| Composable | Observar `StateFlow`, despachar acciones al ViewModel, renderizar | Sin logica de negocio |
| ViewModel | Exponer estado, orquestar llamadas al repositorio, emitir eventos | Sin referencias a Context |
| Repository | Encapsular llamadas de red, envolver en `Result<T>` | Nunca expone `Response<T>` |
| ApiService | Declarar contratos HTTP | Solo anotaciones Retrofit |
| RetrofitClient | Configurar OkHttp y Retrofit | Singleton object de Kotlin |

### Estados de UI

Cada pantalla modela sus estados posibles como `sealed interface` con exactamente tres variantes:

```kotlin
sealed interface LibroListUiState {
    object Loading : LibroListUiState
    data class Success(val libros: List<LibroDto>) : LibroListUiState
    data class Error(val message: String) : LibroListUiState
}
```

Este patron se repite para `LibroDetailUiState`, `GenerosFormUiState` y `GeneroListUiState`. El uso de `sealed interface` (en lugar de `sealed class`) permite que los estados implementen multiples interfaces si fuera necesario.

### Eventos unicos (UiEvent)

Los eventos que deben ocurrir exactamente una vez, como mostrar un Toast o navegar hacia atras, se transmiten por un `SharedFlow` sin replay:

```kotlin
// UiEvent.kt
sealed interface UiEvent {
    data class ShowMessage(val message: String) : UiEvent
    object NavigateBack : UiEvent
}

// En el ViewModel
private val _uiEvent = MutableSharedFlow<UiEvent>()
val uiEvent: SharedFlow<UiEvent> = _uiEvent.asSharedFlow()
```

Los Composables colectan este flujo dentro de un `LaunchedEffect(Unit)` para garantizar que el collector vive en el scope de la composicion y se cancela automaticamente.

### Prevencion de doble envio

Todo formulario sigue el mismo patron de guard:

```kotlin
fun crearLibro() {
    if (_isSubmitting.value) return   // guard de entrada
    viewModelScope.launch {
        _isSubmitting.value = true
        try {
            // operacion de red
        } finally {
            _isSubmitting.value = false  // reset garantizado
        }
    }
}
```

El boton en la UI tiene `enabled = !isSubmitting` y muestra un `CircularProgressIndicator` mientras el envio esta en curso.

### Recarga automatica de listas

Las pantallas de lista usan `DisposableEffect` para observar el ciclo de vida y recargar al volver a la pantalla:

```kotlin
val lifecycleOwner = LocalLifecycleOwner.current
DisposableEffect(lifecycleOwner) {
    val observer = LifecycleEventObserver { _, event ->
        if (event == Lifecycle.Event.ON_RESUME) viewModel.cargarLibros()
    }
    lifecycleOwner.lifecycle.addObserver(observer)
    onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
}
```

Los formularios usan `LaunchedEffect(libroId)` o `LaunchedEffect(Unit)` para la carga inicial.

---

## 5. Capa de datos

### API base

```
https://apilibreria.jmacboy.com/api/
```

### Modelos de datos

**LibroDto** — representa un libro devuelto por la API:

```kotlin
data class LibroDto(
    @SerializedName("id")           val id: Int,
    @SerializedName("nombre")       val nombre: String,
    @SerializedName("autor")        val autor: String,
    @SerializedName("editorial")    val editorial: String,
    @SerializedName("imagen")       val imageUrl: String,
    @SerializedName("sinopsis")     val sinopsis: String,
    @SerializedName("isbn")         val isbn: String,
    @SerializedName("calificacion") val calificacion: Int
)
```

**GeneroDto** — representa un genero:

```kotlin
data class GeneroDto(
    @SerializedName("id")     val id: Int,
    @SerializedName("nombre") val nombre: String
)
```

**LibroRequest** — body para crear o actualizar un libro (POST y PUT):

```kotlin
data class LibroRequest(
    @SerializedName("nombre")       val nombre: String,
    @SerializedName("autor")        val autor: String,
    @SerializedName("editorial")    val editorial: String,
    @SerializedName("imagen")       val imagen: String,
    @SerializedName("sinopsis")     val sinopsis: String,
    @SerializedName("isbn")         val isbn: String,
    @SerializedName("calificacion") val calificacion: Int = 0
)
```

**GeneroRequest** — body para crear un genero:

```kotlin
data class GeneroRequest(@SerializedName("nombre") val nombre: String)
```

**LibroGeneroRequest** — body para asignar un genero a un libro:

```kotlin
data class LibroGeneroRequest(
    @SerializedName("libro_id")  val libroId: Int,
    @SerializedName("genero_id") val generoId: Int
)
```

### Endpoints

| Metodo | Ruta | Funcion en ApiService | Request body | Tipo de retorno |
|---|---|---|---|---|
| GET | `/libros` | `getLibros()` | — | `List<LibroDto>` |
| GET | `/libros/{id}` | `getLibroById(id)` | — | `LibroDto` |
| POST | `/libros` | `crearLibro(libro)` | `LibroRequest` | `LibroDto` |
| PUT | `/libros/{id}` | `actualizarLibro(id, libro)` | `LibroRequest` | `LibroDto` |
| DELETE | `/libros/{id}` | `eliminarLibro(id)` | — | `ResponseBody` |
| GET | `/generos` | `getGeneros()` | — | `List<GeneroDto>` |
| POST | `/generos` | `crearGenero(genero)` | `GeneroRequest` | `GeneroDto` |
| DELETE | `/generos/{id}` | `eliminarGenero(id)` | — | `ResponseBody` |
| POST | `/libro-generos` | `asignarGeneroALibro(request)` | `LibroGeneroRequest` | `ResponseBody` |

Todas las funciones de `ApiService` son `suspend`. No se usa `Call<T>` ni callbacks.

### RetrofitClient

Singleton `object` de Kotlin que configura OkHttp con dos interceptores y construye la instancia de Retrofit:

- **HttpLoggingInterceptor** en nivel BODY: registra headers y cuerpo completo de cada request/response en Logcat.
- **Interceptor de headers**: inyecta `Accept: application/json` en cada request para garantizar que la API devuelva JSON y no HTML.

```kotlin
object RetrofitClient {
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Accept", "application/json")
                .build()
            chain.proceed(request)
        }
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://apilibreria.jmacboy.com/api/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build()

    val apiService: ApiService = retrofit.create(ApiService::class.java)
}
```

### Repositorios

Cada funcion del repositorio envuelve la llamada en `try/catch` y devuelve `Result<T>` de la biblioteca estandar de Kotlin. El ViewModel trabaja con `Result.fold { onSuccess / onFailure }`.

**LibroRepository** — funciones disponibles:

| Funcion | Endpoint llamado | Tipo de retorno |
|---|---|---|
| `getLibros()` | GET /libros | `Result<List<LibroDto>>` |
| `getLibroById(id)` | GET /libros/{id} | `Result<LibroDto>` |
| `crearLibro(request)` | POST /libros | `Result<LibroDto>` |
| `actualizarLibro(id, request)` | PUT /libros/{id} | `Result<LibroDto>` |
| `eliminarLibro(id)` | DELETE /libros/{id} | `Result<Unit>` |
| `asignarGenero(libroId, generoId)` | POST /libro-generos | `Result<Unit>` |

**GeneroRepository** — funciones disponibles:

| Funcion | Endpoint llamado | Tipo de retorno |
|---|---|---|
| `getGeneros()` | GET /generos | `Result<List<GeneroDto>>` |
| `crearGenero(request)` | POST /generos | `Result<GeneroDto>` |
| `eliminarGenero(id)` | DELETE /generos/{id} | `Result<Unit>` |

Ambos repositorios reciben `ApiService` como parametro con valor por defecto `RetrofitClient.apiService`. Esta firma facilita pruebas unitarias sin necesidad de un framework de inyeccion de dependencias.

---

## 6. ViewModels y estados de UI

### LibroViewModel

Instancia `LibroRepository` y `GeneroRepository` directamente. Es el ViewModel de mayor responsabilidad del proyecto: gestiona tanto la lista de libros como el formulario y el detalle.

**Estados expuestos:**

| StateFlow | Tipo del estado | Descripcion |
|---|---|---|
| `listState` | `LibroListUiState` | Estado de la lista de libros (Loading / Success / Error) |
| `detailState` | `LibroDetailUiState` | Estado del detalle y del formulario en modo editar |
| `generosFormState` | `GenerosFormUiState` | Lista de generos disponibles para el formulario de creacion |
| `formNombre` | `String` | Campo nombre del formulario |
| `formAutor` | `String` | Campo autor del formulario |
| `formEditorial` | `String` | Campo editorial del formulario |
| `formSinopsis` | `String` | Campo sinopsis del formulario |
| `formIsbn` | `String` | Campo ISBN del formulario |
| `formImagen` | `String` | Campo URL de imagen del formulario |
| `formGenerosSeleccionados` | `Set<Int>` | IDs de los generos seleccionados (solo en modo crear) |
| `isSubmitting` | `Boolean` | Guard de doble envio |
| `uiEvent` (SharedFlow) | `UiEvent` | Eventos unicos: navegacion y mensajes |

**Funciones publicas:**

| Funcion | Descripcion |
|---|---|
| `cargarLibros()` | GET /libros, actualiza `listState` |
| `cargarLibro(id)` | GET /libros/{id}, actualiza `detailState` |
| `cargarLibroParaEditar(id)` | GET /libros/{id}, actualiza `detailState` y puebla los campos del formulario |
| `cargarGenerosParaFormulario()` | GET /generos, actualiza `generosFormState` |
| `crearLibro()` | Valida, POST /libros, luego N x POST /libro-generos por cada genero seleccionado |
| `editarLibro(id)` | Valida, PUT /libros/{id} |
| `eliminarLibro(id)` | DELETE /libros/{id}, emite `ShowMessage` + `NavigateBack` |
| `setNombre(v)` / `setAutor(v)` / etc. | Actualiza el campo correspondiente del formulario |
| `toggleGenero(generoId)` | Agrega o quita el ID del `Set` de seleccionados |
| `limpiarFormulario()` | Resetea todos los campos, `formGenerosSeleccionados` e `isSubmitting` |

**Detalle del flujo `crearLibro()`:**

```
1. Guard: si isSubmitting, retorna inmediatamente
2. Captura valores actuales de los StateFlow del formulario
3. Llama LibroValidator.validarTodo(...) — si falla, emite ShowMessage y retorna
4. _isSubmitting.value = true
5. try:
   a. repository.crearLibro(request) → obtiene libroCreado con su ID asignado
   b. Para cada generoId en formGenerosSeleccionados:
         repository.asignarGenero(libroCreado.id, generoId)
   c. Emite ShowMessage("Libro creado exitosamente")
   d. Emite NavigateBack
   e. limpiarFormulario()
6. finally: _isSubmitting.value = false
```

### GeneroViewModel

Instancia `GeneroRepository` directamente.

**Estados expuestos:**

| StateFlow | Tipo del estado | Descripcion |
|---|---|---|
| `listState` | `GeneroListUiState` | Estado de la lista de generos |
| `formNombre` | `String` | Campo nombre del formulario |
| `isSubmitting` | `Boolean` | Guard de doble envio |
| `uiEvent` (SharedFlow) | `UiEvent` | Eventos unicos |

**Funciones publicas:**

| Funcion | Descripcion |
|---|---|
| `cargarGeneros()` | GET /generos, actualiza `listState` |
| `crearGenero()` | Valida nombre, POST /generos, emite `NavigateBack` |
| `eliminarGenero(id)` | DELETE /generos/{id}, llama `cargarGeneros()` sin navegar |
| `setNombre(v)` | Actualiza `formNombre` |
| `limpiarFormulario()` | Resetea `formNombre` e `isSubmitting` |

---

## 7. Validacion de formularios

### LibroValidator

`object` de Kotlin. Cada funcion acepta un `String` y retorna `String?` (el mensaje de error, o `null` si es valido).

| Funcion | Condicion de error | Mensaje |
|---|---|---|
| `validarNombre(nombre)` | `nombre.isBlank()` | "El nombre es obligatorio" |
| `validarAutor(autor)` | `autor.isBlank()` | "El autor es obligatorio" |
| `validarEditorial(editorial)` | `editorial.isBlank()` | "La editorial es obligatoria" |
| `validarSinopsis(sinopsis)` | `sinopsis.isBlank()` | "La sinopsis es obligatoria" |
| `validarIsbn(isbn)` | No cumple regex `\d{10}\|\d{13}` | "El ISBN debe tener 10 o 13 digitos numericos" |
| `validarImagen(url)` | No empieza con `http://` ni `https://` | "La URL debe comenzar con http:// o https://" |
| `validarTodo(...)` | Alguna de las anteriores no es null | Retorna `false` |

`validarTodo` invoca todas las funciones individuales y retorna `true` solo si todas devuelven `null`.

### GeneroValidator

```kotlin
object GeneroValidator {
    fun validarNombre(nombre: String): String? =
        if (nombre.isBlank()) "El nombre del genero es obligatorio" else null
}
```

### Comportamiento de errores en UI

La validacion en tiempo real sigue esta regla: el error solo se muestra si el campo ya fue tocado (es no vacio) o si el usuario intento enviar el formulario:

```kotlin
var submitAttempted by remember { mutableStateOf(false) }

// En cada campo:
isError = (submitAttempted || campo.isNotEmpty()) && errorCampo != null
supportingText = {
    if ((submitAttempted || campo.isNotEmpty()) && errorCampo != null)
        Text(errorCampo)
}

// En el boton de envio:
onClick = {
    submitAttempted = true
    viewModel.crearLibro() // o editarLibro
}
```

Esta logica garantiza que:
- Un campo vacio no muestra error al cargar el formulario por primera vez.
- Si el usuario escribe y luego borra el contenido, el error aparece inmediatamente.
- Al tocar "Guardar" todos los campos vacios con error se marcan al mismo tiempo.

---

## 8. Navegacion

### Rutas disponibles

```kotlin
object Routes {
    const val LIBRO_LIST   = "libro_list"
    const val LIBRO_DETAIL = "libro_detail/{libroId}"
    const val LIBRO_CREATE = "libro_create"
    const val LIBRO_EDIT   = "libro_edit/{libroId}"
    const val GENERO_LIST  = "genero_list"
    const val GENERO_CREATE = "genero_create"

    fun libroDetail(id: Int) = "libro_detail/$id"
    fun libroEdit(id: Int)   = "libro_edit/$id"
}
```

Las rutas con argumento `{libroId}` usan `NavType.IntType`. Los helpers `libroDetail(id)` y `libroEdit(id)` construyen la ruta con el ID concreto para la llamada a `navController.navigate(...)`.

### AppNavHost

El `NavHost` se instancia en `MainActivity` junto con el `NavController` y el `LibroViewModel` raiz. El destino inicial es `LIBRO_LIST`.

| Ruta | Pantalla | Argumento |
|---|---|---|
| `libro_list` | `LibroListScreen` | — |
| `libro_detail/{libroId}` | `LibroDetailScreen` | `libroId: Int` |
| `libro_create` | `LibroFormScreen(libroId = null)` | — |
| `libro_edit/{libroId}` | `LibroFormScreen(libroId = Int)` | `libroId: Int` |
| `genero_list` | `GeneroListScreen` | — |
| `genero_create` | `GeneroFormScreen` | — |

`LibroDetailScreen` y `LibroFormScreen` reciben el `LibroViewModel` compartido que fue instanciado en `MainActivity`. `GeneroListScreen` y `GeneroFormScreen` crean su propio `GeneroViewModel` internamente con `viewModel()`.

### Barra de navegacion inferior

`LibroListScreen` y `GeneroListScreen` comparten el mismo `NavigationBar` con dos items: Libros y Generos. La navegacion entre estas dos pantallas usa `popUpTo` para evitar acumulacion en el back stack.

---

## 9. Pantallas

### LibroListScreen

**Archivo:** `ui/screen/libros/LibroListScreen.kt`

Instancia su propio `LibroViewModel` con `viewModel()`. Recarga la lista en cada `ON_RESUME` via `DisposableEffect + LifecycleEventObserver`.

**Layout:** `Scaffold` con `TopAppBar` ("Biblioteca"), `NavigationBar` (Libros/Generos), `FloatingActionButton` (+) que navega a `LIBRO_CREATE`. El contenido central conmuta entre:

| Estado | Composable mostrado |
|---|---|
| `Loading` | `LoadingView` |
| `Error` | `ErrorView` con boton Reintentar |
| `Success` (lista vacia) | `EmptyView` con mensaje |
| `Success` (con datos) | `LazyColumn` de `LibroItem` |

Cada `LibroItem` es una `Card` con imagen (72x100dp, `ContentScale.Crop`), titulo (`titleMedium`) y autor (`bodyMedium`). Al hacer clic navega a `libro_detail/{id}`.

---

### LibroDetailScreen

**Archivo:** `ui/screen/libros/LibroDetailScreen.kt`

Recibe `libroId: Int`, `navController` y `viewModel: LibroViewModel`. Carga el libro con `LaunchedEffect(libroId)` — el efecto se re-lanza si cambia el ID.

**Layout:** `Scaffold` con `TopAppBar` con boton de retroceso. El contenido conmuta entre `LoadingView`, `ErrorView` (con boton Reintentar que vuelve a llamar `cargarLibro(libroId)`) y la vista de exito. En modo exito:

- `AsyncImage` a ancho completo, altura 250dp, `ContentScale.Crop`.
- Nombre del libro en estilo `headlineMedium`.
- Campos Autor, Editorial, ISBN y Calificacion como `Text`.
- Seccion Sinopsis con titulo `titleMedium`.
- `Row` con `OutlinedButton` "Editar" (navega a `libro_edit/{id}`) y `Button` "Eliminar" (color `error`).

Al tocar "Eliminar" se muestra `ConfirmDialog`. Al confirmar, se llama `viewModel.eliminarLibro(libroId)`, que emite `NavigateBack` una vez completada la operacion.

---

### LibroFormScreen

**Archivo:** `ui/screen/libros/LibroFormScreen.kt`

Composable unificado para crear y editar. Recibe `libroId: Int?` (null para crear).

**Modo crear (`libroId == null`):**
- `LaunchedEffect(libroId)` llama `limpiarFormulario()` y `cargarGenerosParaFormulario()`.
- Muestra seccion de generos: `LazyRow` con `FilterChip` por cada genero disponible.
- El titulo de la `TopAppBar` es "Nuevo libro" y el boton dice "Guardar libro".

**Modo editar (`libroId != null`):**
- `LaunchedEffect(libroId)` llama `cargarLibroParaEditar(libroId)`.
- Mientras `detailState` es `Loading`, muestra un `CircularProgressIndicator` global en lugar del formulario (early return del `Scaffold`).
- No muestra la seccion de generos; en su lugar muestra un texto informativo indicando que los generos ya asignados no se pueden modificar desde este formulario.
- El titulo de la `TopAppBar` es "Editar libro" y el boton dice "Actualizar libro".

**Campos del formulario:**

| Campo | Tipo de teclado | Validacion |
|---|---|---|
| Nombre | Default | No puede estar en blanco |
| Autor | Default | No puede estar en blanco |
| Editorial | Default | No puede estar en blanco |
| ISBN | Number | Regex `\d{10}\|\d{13}` |
| URL de imagen | Uri | Debe empezar con http:// o https:// |
| Sinopsis | Default (multilinea, 3-5 lineas) | No puede estar en blanco |

---

### GeneroListScreen

**Archivo:** `ui/screen/generos/GeneroListScreen.kt`

Instancia su propio `GeneroViewModel`. Recarga en `ON_RESUME` via `DisposableEffect`.

**Layout:** igual a `LibroListScreen` (mismo `NavigationBar`, mismo patron de estados). Cada `GeneroItem` es una `Card` con el nombre del genero y un `IconButton` de eliminacion con el icono `Delete` en color `error`.

La eliminacion es inline: al tocar el icono, se guarda el genero en la variable local `generoAEliminar: GeneroDto?` y se muestra `ConfirmDialog`. El mensaje del dialogo incluye el nombre del genero. Al confirmar, se llama `viewModel.eliminarGenero(id)`, que internamente recarga la lista sin navegar.

---

### GeneroFormScreen

**Archivo:** `ui/screen/generos/GeneroFormScreen.kt`

Instancia su propio `GeneroViewModel`. Llama `limpiarFormulario()` al entrar via `LaunchedEffect(Unit)`.

Formulario minimo: un solo `OutlinedTextField` para el nombre con validacion en tiempo real (solo si el campo no esta vacio), y un `Button` "Guardar genero" con spinner de `isSubmitting`. Al completarse la operacion, el ViewModel emite `NavigateBack`.

---

## 10. Componentes reutilizables

### LoadingView

**Archivo:** `ui/components/LoadingView.kt`

`Box` con `fillMaxSize()` y `contentAlignment = Alignment.Center` que muestra un `CircularProgressIndicator`. Acepta un `Modifier` opcional para permitir que el llamador aplique padding de `Scaffold`.

### ErrorView

**Archivo:** `ui/components/ErrorView.kt`

`Column` centrado con:
- Icono `ErrorOutline` de 72dp en color `error`.
- Texto del mensaje en `bodyLarge` color `error`, alineacion centrada.
- `Button` "Reintentar" con icono `Refresh`, llama `onRetry`.

Parametros: `message: String`, `onRetry: () -> Unit`, `modifier: Modifier`.

### EmptyView

**Archivo:** `ui/components/EmptyView.kt`

`Column` centrado con icono `MenuBook` de 72dp en color `onSurfaceVariant` y texto del mensaje. Se usa cuando la lista se cargo correctamente pero esta vacia.

Parametros: `message: String`, `modifier: Modifier`.

### ConfirmDialog

**Archivo:** `ui/components/ConfirmDialog.kt`

`AlertDialog` generico reutilizable en todas las pantallas que requieren confirmacion antes de una accion destructiva.

```kotlin
@Composable
fun ConfirmDialog(
    title: String,
    message: String,
    confirmText: String = "Eliminar",
    dismissText: String = "Cancelar",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
)
```

El texto de confirmacion se muestra en color `error` para reforzar el caracter destructivo de la accion. El dialogo se descarta tanto al presionar "Cancelar" como al tocar fuera de el (`onDismissRequest = onDismiss`).

---

## 11. Flujos de usuario

### Crear libro

```
LibroListScreen
  --> FAB (+)
    --> LibroFormScreen (libroId = null)
          LaunchedEffect: limpiarFormulario() + cargarGenerosParaFormulario()
          Usuario completa: nombre, autor, editorial, ISBN, URL imagen, sinopsis
          Usuario selecciona generos via FilterChip (opcional)
          Toca "Guardar libro"
            submitAttempted = true
            ViewModel: validarTodo()
              [si falla] -> Toast con "Revisa los campos marcados en rojo"
              [si pasa]  -> POST /libros
                            N x POST /libro-generos (uno por genero seleccionado)
                            Toast "Libro creado exitosamente"
                            UiEvent.NavigateBack -> popBackStack()
  --> LibroListScreen (ON_RESUME dispara cargarLibros() automaticamente)
```

### Editar libro

```
LibroDetailScreen
  --> OutlinedButton "Editar"
    --> LibroFormScreen (libroId = id)
          LaunchedEffect: cargarLibroParaEditar(id)
            Spinner global mientras Loading
            Al Success: campos del formulario precargados con datos del libro
          Usuario modifica los campos deseados
          Toca "Actualizar libro"
            submitAttempted = true
            ViewModel: validarTodo()
              [si falla] -> Toast con "Revisa los campos marcados en rojo"
              [si pasa]  -> PUT /libros/{id}
                            Toast "Libro actualizado exitosamente"
                            UiEvent.NavigateBack -> popBackStack()
  --> LibroDetailScreen
```

### Eliminar libro

```
LibroDetailScreen
  --> Button "Eliminar"
    --> ConfirmDialog: "¿Estas seguro de eliminar este libro?"
          [Cancelar] -> cierra dialogo, sin cambios
          [Eliminar] -> viewModel.eliminarLibro(id)
                          DELETE /libros/{id}
                          Toast "Libro eliminado exitosamente"
                          UiEvent.NavigateBack -> popBackStack()
  --> LibroListScreen (ON_RESUME dispara cargarLibros() automaticamente)
```

### Crear genero

```
GeneroListScreen
  --> FAB (+)
    --> GeneroFormScreen
          LaunchedEffect: limpiarFormulario()
          Usuario escribe nombre del genero
          Toca "Guardar genero"
            ViewModel: validarNombre()
              [si falla] -> Toast con mensaje de error
              [si pasa]  -> POST /generos
                            Toast "Genero creado exitosamente"
                            UiEvent.NavigateBack -> popBackStack()
  --> GeneroListScreen (ON_RESUME dispara cargarGeneros() automaticamente)
```

### Eliminar genero

```
GeneroListScreen
  --> IconButton Delete en un GeneroItem
    --> generoAEliminar = genero (variable local)
    --> ConfirmDialog: "¿Eliminar el genero [nombre]?"
          [Cancelar] -> generoAEliminar = null, cierra dialogo
          [Eliminar] -> viewModel.eliminarGenero(id)
                          DELETE /generos/{id}
                          Toast "Genero eliminado exitosamente"
                          cargarGeneros() [sin navegar]
  --> GeneroListScreen actualizada en la misma pantalla
```

---

## 12. Decisiones de diseno

### Sin Hilt / sin inyeccion de dependencias

El proyecto no utiliza Hilt ni ninguna libreria de DI. Los ViewModels instancian los repositorios directamente y los repositorios reciben `ApiService` con valor por defecto. Esta decision simplifica la configuracion del proyecto para un contexto universitario sin sacrificar la separacion de capas.

### ViewModel compartido para libros

`LibroViewModel` es instanciado una sola vez en `MainActivity` y pasado como parametro a `LibroDetailScreen` y `LibroFormScreen`. Esto permite que el formulario de edicion acceda directamente al estado del detalle sin necesidad de un mecanismo de comunicacion entre ViewModels. `LibroListScreen` instancia su propio `LibroViewModel` independiente porque no necesita compartir estado con las otras pantallas de libros.

### `GeneroViewModel` local en cada pantalla de generos

`GeneroListScreen` y `GeneroFormScreen` crean su propio `GeneroViewModel` con `viewModel()`. Esto es correcto porque el ciclo de vida del ViewModel de lista de generos no necesita sobrevivir a la navegacion hacia el formulario de creacion ni compartir estado con el.

### Generos no editables post-creacion

El formulario de edicion de libros no permite modificar los generos asignados. Esta limitacion esta documentada en la pantalla con un texto informativo. La decision evita la complejidad de manejar la diferencia entre generos actuales y nuevos (requeriria obtener los generos actuales del libro via un endpoint adicional que la API no ofrece directamente en la respuesta de `GET /libros/{id}`).

### `Result<T>` como contrato del repositorio

El uso de `Result<T>` de Kotlin estandar (sin librerias adicionales) mantiene la capa de repositorio simple y sin dependencias. El ViewModel usa `.fold { onSuccess / onFailure }` para manejar ambas ramas de forma explicita, lo que hace el codigo de manejo de errores visible en cada funcion del ViewModel.

### Coil 3 con backend OkHttp

La version 3 de Coil requiere configurar explicitamente el backend de red. Se agrega la dependencia `coil-network-okhttp` para que Coil reutilice la misma instancia de OkHttp que Retrofit, con lo que los headers de autenticacion o configuracion de red quedan centralizados.
