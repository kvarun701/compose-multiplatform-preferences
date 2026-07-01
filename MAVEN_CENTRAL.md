# Publishing to Maven Central (via Sonatype Central Portal)

This guide walks you through publishing this Kotlin Multiplatform library to Maven Central using the new **Sonatype Central Portal** (`https://central.sonatype.com/`).

---

## 🛠️ Step 1: Pre-requisites on Sonatype Central

1. **Create an Account**: Sign up at [central.sonatype.com](https://central.sonatype.com/).
2. **Claim your Namespace (Group ID)**: 
   - Go to your profile -> **Namespaces**.
   - Add your namespace (e.g., `com.github.kvarun701` or your custom domain).
   - If using `com.github.username`, you must verify ownership by creating a temporary public repository named as requested by Sonatype.
3. **Generate User Token**:
   - Go to your profile -> **Account** -> **Generate User Token**.
   - Save the username and password tokens; you will need these in your Gradle settings.

---

## 🔑 Step 2: GPG Key Generation & Signing

Maven Central requires all artifacts to be signed with a GPG key.

1. **Install GPG**:
   - macOS: `brew install gpg pinentry-mac`
2. **Generate a Key Pair**:
   - Run `gpg --full-generate-key`.
   - Choose RSA and RSA (default), size 4096, and set a passphrase.
3. **List Keys**:
   - Run `gpg --list-keys --keyid-format SHORT` to find your 8-character Key ID (e.g., `1234ABCD`).
4. **Publish your Public Key**:
   - Send the key to a public keyserver so Sonatype can verify it:
     ```bash
     gpg --keyserver keyserver.ubuntu.com --send-keys YOUR_KEY_ID
     gpg --keyserver keys.openpgp.org --send-keys YOUR_KEY_ID
     ```
5. **Export Private Key**:
   - Export your armored private key (or configure in-memory signing):
     ```bash
     gpg --export-secret-keys YOUR_KEY_ID > secring.gpg
     ```

---

## 📝 Step 3: Gradle Configuration

To configure signing and publishing, update your project's gradle scripts.

### 1. Configure the `shared/build.gradle.kts`
Apply the `signing` plugin and set up POM details required by Maven Central:

```kotlin
plugins {
    // ... other plugins
    `maven-publish`
    signing
}

group = "com.github.kvarun701" // Your verified namespace
version = "1.0.0"

// Configure POM details (mandatory for Maven Central)
publishing {
    publications.withType<MavenPublication> {
        artifactId = artifactId.replace("shared", "compose-pref")
        
        pom {
            name.set("Compose Multiplatform Preferences")
            description.set("Lightweight Key-Value Storage library for Kotlin/Compose Multiplatform.")
            url.set("https://github.com/kvarun701/compose-multiplatform-preferences")
            
            licenses {
                license {
                    name.set("The Apache License, Version 2.0")
                    url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                }
            }
            developers {
                developer {
                    id.set("kvarun701")
                    name.set("Varun")
                    email.set("your-email@example.com")
                }
            }
            scm {
                connection.set("scm:git:git://github.com/kvarun701/compose-multiplatform-preferences.git")
                developerConnection.set("scm:git:ssh://github.com/kvarun701/compose-multiplatform-preferences.git")
                url.set("https://github.com/kvarun701/compose-multiplatform-preferences")
            }
        }
    }
    
    repositories {
        maven {
            name = "sonatype"
            url = uri("https://central.sonatype.com/api/v1/publisher/deployments")
            credentials {
                username = System.getenv("SONATYPE_USERNAME") ?: project.findProperty("sonatypeUsername")?.toString()
                password = System.getenv("SONATYPE_PASSWORD") ?: project.findProperty("sonatypePassword")?.toString()
            }
        }
    }
}

// Sign all publications
signing {
    val signingKey = System.getenv("SIGNING_KEY") ?: project.findProperty("signingKey")?.toString()
    val signingPassword = System.getenv("SIGNING_PASSWORD") ?: project.findProperty("signingPassword")?.toString()
    
    if (signingKey != null && signingPassword != null) {
        useInMemoryPgpKeys(signingKey, signingPassword)
        sign(publishing.publications)
    }
}
```

### 2. Configure Credentials in `local.properties` (or Global Gradle Properties)
Create/Update `~/.gradle/gradle.properties` or your local environment variables:

```properties
# Sonatype generated token credentials
sonatypeUsername=YOUR_TOKEN_USERNAME
sonatypePassword=YOUR_TOKEN_PASSWORD

# GPG Signing
signing.keyId=YOUR_8_CHAR_KEY_ID
signing.password=YOUR_GPG_PASSPHRASE
signing.secretKeyRingFile=/Users/varun/path/to/secring.gpg
```

---

## 🚀 Step 4: Publish your Library

You can publish either automatically to the Sonatype Portal or manually by uploading a Zip Bundle.

### Method A: Manual Bundle Upload (Safest for first-time setup)
Publish all artifacts locally to a local directory:
```bash
./gradlew publishAllPublicationsToMavenLocal
```
After successful execution, the build artifacts will be located under your `~/.m2/repository` directory.
1. Create a zip of your library directory (e.g., `com/github/kvarun701/compose-pref/1.0.0/`).
2. Go to [Sonatype Central Publishing](https://central.sonatype.com/publishing/deployments).
3. Click **Publish Components** and upload the zip file.
4. Sonatype will run automated checks (verifying POM completeness, signatures, and sources).
5. Once verified, click **Publish** to release it to Maven Central.

### Method B: Direct Publish from Gradle
Run the following task to push direct to the Sonatype Central Portal:
```bash
./gradlew publishAllPublicationsToSonatypeRepository
```
Once uploaded:
1. Go to [Sonatype Central Publishing](https://central.sonatype.com/publishing/deployments).
2. You will see a new deployment transition status (e.g., validating).
3. Click **Publish** once validations pass.
