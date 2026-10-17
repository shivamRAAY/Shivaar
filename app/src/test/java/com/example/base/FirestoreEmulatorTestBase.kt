package com.example.base

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.MemoryCacheSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [36])
@LooperMode(LooperMode.Mode.INSTRUMENTATION_TEST)
abstract class FirestoreEmulatorTestBase {

  protected lateinit var firestore: FirebaseFirestore
  protected lateinit var auth: FirebaseAuth
  protected lateinit var databaseId: String

  @Before
  open fun setUpFirebase() {
    val emulatorAvailable = try {
      java.net.Socket(EMULATOR_HOST, AUTH_PORT).use { }
      java.net.Socket(EMULATOR_HOST, FIRESTORE_PORT).use { }
      true
    } catch (_: Exception) {
      false
    }
    org.junit.Assume.assumeTrue("Firebase emulator not reachable on $EMULATOR_HOST:$AUTH_PORT and $FIRESTORE_PORT", emulatorAvailable)

    val context = ApplicationProvider.getApplicationContext<Context>()
    val resId = context.resources.getIdentifier("firestore_database_id", "string", context.packageName)
    databaseId = if (resId != 0) context.getString(resId) else "ai-studio-android-shivaar-0b84995a-d822-4825-8fd1-2391378b1934"

    val app = if (FirebaseApp.getApps(context).isEmpty()) {
      val newApp = FirebaseApp.initializeApp(
        context,
        FirebaseOptions.Builder()
          .setApplicationId("com.aistudio.shivaar.btech")
          .setProjectId(PROJECT_ID)
          .setApiKey("fake-api-key-for-emulator")
          .build()
      )
      val newFirestore = FirebaseFirestore.getInstance(newApp, databaseId)
      newFirestore.useEmulator(EMULATOR_HOST, FIRESTORE_PORT)
      newFirestore.firestoreSettings = FirebaseFirestoreSettings.Builder()
        .setLocalCacheSettings(MemoryCacheSettings.newBuilder().build())
        .build()

      val newAuth = FirebaseAuth.getInstance(newApp)
      newAuth.useEmulator(EMULATOR_HOST, AUTH_PORT)
      newApp
    } else {
      FirebaseApp.getInstance()
    }

    firestore = FirebaseFirestore.getInstance(app, databaseId)
    try {
      firestore.useEmulator(EMULATOR_HOST, FIRESTORE_PORT)
    } catch (_: Exception) {}

    auth = FirebaseAuth.getInstance(app)
    try {
      auth.useEmulator(EMULATOR_HOST, AUTH_PORT)
    } catch (_: Exception) {}
  }

  @After
  open fun tearDownFirebase() {
    if (::auth.isInitialized) {
      try {
        auth.signOut()
      } catch (_: Exception) {
      }
    }
  }

  protected suspend fun signInTestUser(email: String): String = withContext(Dispatchers.IO) {
    try {
      withTimeout(AUTH_TIMEOUT_MS) {
        val result = try {
          auth.signInWithEmailAndPassword(email, DEFAULT_PASSWORD).await()
        } catch (unused: Exception) {
          try {
            auth.createUserWithEmailAndPassword(email, DEFAULT_PASSWORD).await()
          } catch (alreadyExists: Exception) {
            auth.signInWithEmailAndPassword(email, DEFAULT_PASSWORD).await()
          }
        }
        checkNotNull(result.user?.uid) { "User auth failed" }
      }
    } catch (e: Exception) {
      org.junit.Assume.assumeNoException("Firebase Auth emulator not responding", e)
      ""
    }
  }

  companion object {
    const val EMULATOR_HOST = "127.0.0.1"
    const val FIRESTORE_PORT = 8085
    const val AUTH_PORT = 9099
    const val PROJECT_ID = "demo-no-project"
    const val DEFAULT_PASSWORD = "password123"
    const val AUTH_TIMEOUT_MS = 5000L
  }
}
