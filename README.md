# App Biblioteca — Práctico 2

Aplicacion Android universitaria para la gestion de una biblioteca digital. Permite listar, crear, editar y eliminar libros y generos, consumiendo una API REST externa.

---

## Capturas de pantalla

> *(Agregar capturas aqui una vez disponibles)*

---

## Funcionalidades

**Libros**
- Listado de libros con imagen, nombre y autor
- Detalle completo: nombre, autor, editorial, ISBN, sinopsis y calificacion
- Formulario de creacion con validacion de campos (nombre, autor, editorial, sinopsis, ISBN de 10 o 13 digitos, URL de imagen) y seleccion de genero
- Edicion de libro existente con precarga de datos actuales
- Eliminacion con dialogo de confirmacion

**Generos**
- Listado de generos
- Creacion con validacion de nombre
- Eliminacion con dialogo de confirmacion

**Navegacion y UX**
- Barra de navegacion inferior entre secciones Libros y Generos
- Estados de carga, error y lista vacia en todas las pantallas
- Actualizacion automatica de listas al volver a una pantalla (via `DisposableEffect` + `ON_RESUME`)

---

## Tecnologias utilizadas

| Categoria | Tecnologia |
|---|---|
| Lenguaje | Kotlin 2.2.10 |
| UI | Jetpack Compose + Material3 |
| Arquitectura | MVVM |
| Networking | Retrofit 2.11.0 + Gson |
| HTTP Client | OkHttp 4.12.0 + Logging Interceptor |
| Imagenes | Coil 3.x (`coil3.compose.AsyncImage`) |
| Navegacion | Navigation Compose 2.8.4 |
| ViewModel | lifecycle-viewmodel-compose + lifecycle-runtime-compose |
| Build | AGP 9.1.1 |

---

## Estructura del proyecto

```
app/src/main/java/com/example/practico_2_app_biblioteca/
├── data/
│   ├── remote/         # RetrofitClient, ApiService
│   ├── model/          # LibroDto, GeneroDto, LibroRequest, GeneroRequest, LibroGeneroRequest
│   └── repository/     # LibroRepository, GeneroRepository
├── ui/
│   ├── navigation/     # Routes, AppNavHost
│   ├── components/     # LoadingView, ErrorView, EmptyView, ConfirmDialog
│   └── screen/
│       ├── libros/     # LibroListScreen, LibroDetailScreen, LibroFormScreen
│       └── generos/    # GeneroListScreen, GeneroFormScreen
├── viewmodel/          # LibroViewModel, GeneroViewModel, UiEvent
├── validation/         # LibroValidator, GeneroValidator
└── MainActivity.kt
```

---

## Arquitectura

El proyecto sigue el patron **MVVM (Model-View-ViewModel)** sin inyeccion de dependencias:

- **Model**: las clases en `data/` se encargan del acceso a la API REST y la representacion de datos (`Dto`, `Request`).
- **ViewModel**: cada `ViewModel` expone el estado de la pantalla mediante `StateFlow<UiState>` y eventos de navegacion o notificacion mediante `SharedFlow<UiEvent>`. Las instancias de repositorio se crean directamente sin Hilt.
- **View**: cada `Screen` composable observa el estado con `collectAsStateWithLifecycle()` y delega toda la logica al `ViewModel`.

---

## API

**Base URL:** `https://apilibreria.jmacboy.com/api`

| Metodo | Endpoint | Descripcion |
|---|---|---|
| GET | `/libros` | Listar todos los libros |
| GET | `/libros/{id}` | Obtener detalle de un libro |
| POST | `/libros` | Crear un libro |
| PUT | `/libros/{id}` | Editar un libro |
| DELETE | `/libros/{id}` | Eliminar un libro |
| GET | `/generos` | Listar todos los generos |
| POST | `/generos` | Crear un genero |
| DELETE | `/generos/{id}` | Eliminar un genero |
| POST | `/libro-generos` | Asignar un genero a un libro |

---

## Requisitos

- Android Studio Meerkat o posterior
- JDK 11
- Android SDK: minimo API 24, objetivo API 36
- Conexion a internet en el dispositivo o emulador

---

## Como ejecutar

1. Clonar el repositorio:
   ```bash
   git clone https://github.com/LuisRibera/practico-2-app-biblioteca.git
   ```
2. Abrir el proyecto en Android Studio.
3. Sincronizar Gradle (File > Sync Project with Gradle Files).
4. Ejecutar en un emulador o dispositivo fisico con API 24 o superior.

---

## Equipo

| Nombre | GitHub |
|---|---|
| Diego Arcani | [@Dip1224](https://github.com/Dip1224) |
| Luis Rivera | [@LuisRibera](https://github.com/LuisRibera) |
