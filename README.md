# Compose Multiplatform Preferences

A lightweight, developer-friendly Key-Value Storage library for **Kotlin Multiplatform (KMP)** and **Compose Multiplatform (CMP)** projects. It allows you to read, write, and manage preferences seamlessly across multiple targets (Android, iOS, Desktop/JVM, Web JS & Wasm) using a unified API and elegant Kotlin property delegates.

---

## 🌟 Why Choose This Library?

If you are familiar with the popular `multiplatform-settings` library, here is how **Compose Multiplatform Preferences** compares and why it might be the right choice for your project:

| Feature | `multiplatform-settings` | **`compose-multiplatform-preferences`** (Ours) |
| :--- | :--- | :--- |
| **Simple Key-Value Save** | ✅ Yes | **✅ Yes** |
| **Kotlin Property Delegates** | ✅ Yes | **✅ Yes** |
| **Out-of-the-box UI Editor** | ❌ No | **✅ Yes** (Drop-in Composable UI component) |
| **Instant Compose Previews** | ❌ Harder | **✅ Easy** (via `InMemoryKeyValueStorage`) |
| **Dynamic Key Namespacing** | ❌ Manual | **✅ Easy** (via `NamespacedKeyValueStorage`) |

* **Drop-in UI Component**: Quickly build debug screens or user settings menus using the pre-built, interactive Composable preferences editor UI.
* **Mocking & Preview Friendly**: Avoid complex platform mocks in tests and Compose UI previews by utilizing the native in-memory database adapter.
* **Dynamic Partitioning**: Group and prefix keys seamlessly using namespaced wrappers on the fly.

---

## 📦 Installation

To use this library in your Kotlin Multiplatform project:

### 1. Add JitPack Repository
Add JitPack to your dependency resolution repositories in `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

### 2. Add Dependency
Add the dependency to the `commonMain` source set in your shared module's `build.gradle.kts`:

```kotlin
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("io.github.kvarun701.compose-multiplatform-preferences:compose-pref:1.0.0")
        }
    }
}
```

---

## 🚀 Platform Support & Storage Adapters

This library leverages native storage mechanisms under the hood for maximum performance, robustness, and platform idiomatic behavior:

| Platform | Target | Underlying Storage Engine |
| :--- | :--- | :--- |
| **Android** | `androidMain` | `android.content.SharedPreferences` |
| **iOS** | `iosMain` | `NSUserDefaults` (with customizable suite name) |
| **Desktop** | `jvmMain` | `java.util.prefs.Preferences` |
| **Web** | `jsMain` / `wasmJsMain` | `window.localStorage` |

---

## 🛠️ Setup & Initialization

### 1. Android
On Android, `KeyValueStorageFactory` requires a `Context` parameter to initialize `SharedPreferences`:

```kotlin
// In your Android application or Activity
val factory = KeyValueStorageFactory(context)
val storage: KeyValueStorage = factory.create(name = "my_app_preferences")
```

### 2. iOS, JVM, and Web (JS / Wasm)
For all other platforms, the factory does not require any parameters:

```kotlin
val factory = KeyValueStorageFactory()
val storage: KeyValueStorage = factory.create(name = "my_app_preferences")
```

---

## 💡 Usage Guide

### 1. Basic CRUD Operations
The `KeyValueStorage` interface provides direct, type-safe getter and setter methods:

```kotlin
// Writing values
storage.putString("username", "JohnDoe")
storage.putInt("login_count", 5)
storage.putBoolean("is_premium", true)

// Reading values with default values
val username = storage.getString("username", defaultValue = "Guest")
val loginCount = storage.getInt("login_count", defaultValue = 0)
val isPremium = storage.getBoolean("is_premium", defaultValue = false)

// Checking presence and removal
if (storage.contains("username")) {
    storage.remove("username")
}

// Clear all preferences
storage.clear()
```

### 2. Elegant Property Delegates (Recommended)
You can declare preferences as property delegates to read and write them like regular properties. The delegate automatically updates and fetches from the underlying storage.

```kotlin
import com.ganesh.composepref.*

class UserSettings(storage: KeyValueStorage) {
    // Read-write string delegate
    var username by storage.string(key = "pref_username", defaultValue = "Guest")
    
    // Read-write integer delegate
    var score by storage.int(key = "pref_high_score", defaultValue = 0)
    
    // Read-write boolean delegate
    var isDarkTheme by storage.boolean(key = "pref_dark_theme", defaultValue = false)
}
```

Usage in code:
```kotlin
val settings = UserSettings(storage)

// Read values
println(settings.username) // Prints "Guest" (or the saved value)

// Write values (writes to disk/storage instantly)
settings.username = "JaneDoe"
settings.isDarkTheme = true
```

### 3. Namespaced Key-Value Storage
If you need to partition or isolate preferences (e.g. per-user settings, different modules), use `NamespacedKeyValueStorage`. It acts as a wrapper and prefixes all keys automatically:

```kotlin
val mainStorage = KeyValueStorageFactory().create("app_settings")

// Wrap storage with a custom namespace
val userAStorage = NamespacedKeyValueStorage(mainStorage, namespace = "user_A")
val userBStorage = NamespacedKeyValueStorage(mainStorage, namespace = "user_B")

// Keys will be stored as "ns.user_A.theme" and "ns.user_B.theme"
userAStorage.putString("theme", "dark")
userBStorage.putString("theme", "light")
```

### 4. Reusable Preferences Editor UI (Compose Multiplatform)
This library provides a fully functional Compose Multiplatform preferences editor/manager UI component out of the box. You can easily integrate this component into your UI to allow users to view, add, modify, and delete preferences under different namespaces:

```kotlin
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.ganesh.composepref.App
import com.ganesh.composepref.KeyValueStorageFactory

@Composable
fun SettingsScreen() {
    // 1. Initialize your platform-specific KeyValueStorage
    val factory = remember { KeyValueStorageFactory() } // (Pass context if on Android)
    val myStorage = remember { factory.create("my_settings") }

    // 2. Render the interactive preferences management interface
    App(storage = myStorage)
}
```

### 5. Unit Testing & UI Previews
Use `InMemoryKeyValueStorage` to mock settings or preferences during unit tests or Compose UI Previews without touching disk/local storage.

```kotlin
@Composable
@Preview
fun AppPreview() {
    // Pass in-memory storage for safe, sandboxed previews
    val previewStorage = InMemoryKeyValueStorage()
    App(storage = previewStorage)
}
```

---

## 🧪 Running Tests

You can verify the library logic on each platform using Gradle tasks:

* **Android Host Tests**: `./gradlew :shared:testAndroidHostTest`
* **Desktop (JVM) Tests**: `./gradlew :shared:jvmTest`
* **Web Wasm Tests**: `./gradlew :shared:wasmJsTest`
* **Web JS Tests**: `./gradlew :shared:jsTest`
* **iOS Simulator Tests**: `./gradlew :shared:iosSimulatorArm64Test`