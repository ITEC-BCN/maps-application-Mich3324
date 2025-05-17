# MapApp - Android con Google Maps y Supabase
 
 Aplicación Android que muestra un mapa interactivo usando la API de Google Maps e integra Supabase como backend para gestionar datos como ubicaciones, usuarios o marcadores.

##  ¿Qué hace esta app?

Esta app permite a los usuarios visualizar un mapa en tiempo real, con funcionalidades como:

- Visualización de marcadores desde Supabase
- Autenticación de usuarios mediante Supabase
- Interacción con el mapa (toques, zoom, etc.)
- Sincronización en la nube con Supabase
- Usa una arquitectura modular basada en paquetes.

##  Tecnologías utilizadas

- **Kotlin** (lenguaje principal)
- **Google Maps API** (visualización de mapas)
- **Supabase (Database + Auth)**
- **Jetpack Compose / XML** (interfaz de usuario) 
- **MVVM** (Model - View - ViewModel)

##  Estructura del proyecto

├── 📁 data
│ ├── 📁 model
│ │ └── Marcador.kt # Modelo de datos del marcador
│ └── 📁 basededatos
│ ├── MySupabaseClient.kt # Todas las funciones relacionadas con los marcadores
│ └── MySupabaseAuthentication.kt # Lógica de autenticación con Supabase
│
├── 📁 ui
│ ├── 📁 navigation # Navegación entre pantallas
│ ├── 📁 screens # Pantallas de la app (ej. MapScreen, LoginScreen)
│ └── 📁 theme # Tema visual de la app
│
├── 📁 utils # Funciones y clases auxiliares
├── 📁 viewmodel # ViewModels con lógica de presentación
│
├── MainActivity.kt # Punto de entrada principal
└── MyAppSingleton.kt # Inicialización global de Supabase


## 🚀 Instalación y ejecución

1. **Clona el repositorio:**
   ```bash
   git clone https://github.....
   ```

2. **Abre el proyecto en Android Studio.**

3. **Configura tus claves privadas (NO las subas a GitHub):**
    - Clave de Google Maps API: puedes guardarla en `local.properties` o usar un archivo `secrets.properties` que esté en `.gitignore`.
    - URL y Public Key de Supabase: colócalas en una clase segura como `Constants.kt` y exclúyela con `.gitignore`.

## 🔐 Autenticación y Base de Datos (Supabase)

La clase `MyAppSingleton.kt` gestiona la inicialización global de Supabase para que esté disponible en toda la app:

```kotlin
class MyAppSingleton : Application() {
    companion object {
        lateinit var database: MySupabaseClient
        lateinit var auth: SupabaseAuthentication
    }

    override fun onCreate() {
        super.onCreate()
        auth = SupabaseAuthentication()
        database = MySupabaseClient(auth.getSupabaseClient())
    }
}
```

Esto permite acceder a Supabase desde cualquier parte de la app mediante `MyAppSingleton.auth` y `MyAppSingleton.database`.

---

## Estado del proyecto

- Mapa funcional con marcadores
- Conexión con Supabase
- Autenticación
- Filtros y categorías de marcadores
- Perfil de usuario

## Autor

Michelle Posligua (@Mcarolinapc)
