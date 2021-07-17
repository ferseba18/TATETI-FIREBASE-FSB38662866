package ar.com.develup.tateti.actividades

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings

public class ActividadInicial : AppCompatActivity() {
    val database = Firebase.database
    val myRef = database.getReference("message")
    val RC_SING_IN = 12345
    private lateinit var auth: FirebaseAuth
    private lateinit var email: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actividad_inicial)

        email=findViewById(R.id.email)

        iniciarSesion().setOnClickListener { iniciarSesion() }
        registrate().setOnClickListener { registrate() }
        olvideMiContrasena().setOnClickListener { olvideMiContrasena() }



        if (usuarioEstaLogueado()) {
            // Si el usuario esta logueado, se redirige a la pantalla
            // de partidas
            verPartidas()

        }
        actualizarRemoteConfig()
        auth = FirebaseAuth.getInstance()

    }


    private fun usuarioEstaLogueado(): Boolean {

        // Validar que currentUser sea != null
        val user = FirebaseAuth.getInstance().currentUser
        var log = false
        if (user != null) {
            log = true
        }
        return log
    }


    private fun verPartidas() {
        val intent = Intent(this, ActividadPartidas::class.java)
        startActivity(intent)
    }

    private fun registrate() {
        val intent = Intent(this, ActividadRegistracion::class.java)
        startActivity(intent)
    }

    private fun actualizarRemoteConfig() {
        configurarDefaultsRemoteConfig()
        configurarOlvideMiContrasena()
    }

    private fun configurarDefaultsRemoteConfig() {

        // Configurar los valores por default para remote config,
        // ya sea por codigo o por XML
        val remoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 1
            fetchTimeoutInSeconds = 1000
        }
        remoteConfig.setConfigSettingsAsync(configSettings)

        val defaults = mapOf( "olvideMiContrasena" to true)
        Firebase.remoteConfig.setDefaultsAsync(defaults)
        Firebase.remoteConfig.setDefaultsAsync(R.xml.firebase_config_defaults)
    }

    private fun configurarOlvideMiContrasena() {

        // Obtener el valor de la configuracion para saber si mostrar
        // o no el boton de olvide mi contraseña

        Firebase.remoteConfig.fetchAndActivate()
            .addOnCompleteListener {
                val botonOlvideHabilitado = Firebase.remoteConfig.getBoolean("olvideMiContrasena")
                if (botonOlvideHabilitado) {
                    olvideMiContrasena.visibility = View.VISIBLE
                } else {
                    olvideMiContrasena.visibility = View.GONE
                }

            }
    }

    private fun olvideMiContrasena() {
        // Obtengo el mail
        val email = email.text.toString()

        // Si no completo el email, muestro mensaje de error
        if (email.isEmpty()) {
            Snackbar.make(rootView!!, "Completa el email", Snackbar.LENGTH_SHORT).show()
        } else {

            // Si completo el mail debo enviar un mail de reset
            // Para ello, utilizamos sendPasswordResetEmail con el email como parametro
            // Agregar el siguiente fragmento de codigo como CompleteListener, que notifica al usuario
            // el resultado de la operacion


            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Snackbar.make(rootView, "Email enviado", Snackbar.LENGTH_SHORT).show()
                              } else {
                                  Snackbar.make(rootView, "Error " + task.exception, Snackbar.LENGTH_SHORT).show()
                              }
                }

            //  .addOnCompleteListener { task ->
            //      if (task.isSuccessful) {
            //          Snackbar.make(rootView, "Email enviado", Snackbar.LENGTH_SHORT).show()
            //      } else {
            //          Snackbar.make(rootView, "Error " + task.exception, Snackbar.LENGTH_SHORT).show()
            //      }
            //  }
        }
    }

    private fun iniciarSesion() {
        val email = email.text.toString()
        val password = password.text.toString()





        // Choose authentication providers
        /*  val providers = arrayListOf(
              AuthUI.IdpConfig.EmailBuilder().build()
          )

          startActivityForResult(
              AuthUI.getInstance()
                  .createSignInIntentBuilder()
                  .setAvailableProviders(providers)
                  .build(),
              RC_SING_IN
          )*/

// Create and launch sign-in intent

        // IMPORTANTE: Eliminar  la siguiente linea cuando se implemente authentication
        // verPartidas()



        // hacer signInWithEmailAndPassword con los valores ingresados de email y password
        // Agregar en addOnCompleteListener el campo authenticationListener definido mas abajo
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser

                    bloqueVerif()
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                    if (task.exception is FirebaseAuthInvalidUserException) {
                        Snackbar.make(rootView!!, "El usuario no existe", Snackbar.LENGTH_SHORT)
                            .show()
                    } else if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        Snackbar.make(rootView!!, "Credenciales inválidas", Snackbar.LENGTH_SHORT)
                            .show()
                    }
                    updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {

    }

    companion object {
        private const val TAG = "EmailPassword"
    }

    fun bloqueVerif() {
        if (usuarioVerificoEmail()==true) {
            verPartidas()
        } else {
            desloguearse()
            Snackbar.make(rootView!!, "Verifica tu email para continuar", Snackbar.LENGTH_SHORT)
                .show()
        }

    }

    //    private val authenticationListener: OnCompleteListener<AuthResult?> = OnCompleteListener<AuthResult?> { task ->
    //        if (task.isSuccessful) {
    //            if (usuarioVerificoEmail()) {
    //                verPartidas()
    //            } else {
    //                desloguearse()
    //                Snackbar.make(rootView!!, "Verifica tu email para continuar", Snackbar.LENGTH_SHORT).show()
    //            }
    //        } else {
    //            if (task.exception is FirebaseAuthInvalidUserException) {
    //                Snackbar.make(rootView!!, "El usuario no existe", Snackbar.LENGTH_SHORT).show()
    //            } else if (task.exception is FirebaseAuthInvalidCredentialsException) {
    //                Snackbar.make(rootView!!, "Credenciales inválidas", Snackbar.LENGTH_SHORT).show()
    //            }
    //        }
    //    }

    private fun usuarioVerificoEmail(): Boolean {

        var bool = true
        // Preguntar al currentUser si verifico email
        val intent = intent
        val emailLink = intent.data.toString()


// Confirm the link is a sign-in with email link.
        if (auth.isSignInWithEmailLink(emailLink)) {
            // Retrieve this from wherever you stored it
            val email = email.text.toString()

            // The client SDK will parse the code from the link for you.
            auth.signInWithEmailLink(email, emailLink)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "Successfully signed in with email link!")
                        val result = task.result
                        //result.user

                        // You can access the new user via result.getUser()
                        // Additional user info profile *not* available via:
                        // result.getAdditionalUserInfo().getProfile() == null
                        // You can check if the user is new or existing:
                        // result.getAdditionalUserInfo().isNewUser()
                    } else {
                        Log.e(TAG, "Error signing in with email link", task.exception)
                        bool=false
                    }
                }
        }
        return bool
    }

    private fun desloguearse() {

        FirebaseAuth.getInstance()
            .signOut()
        // Hacer signOut de Firebase
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if (requestCode == RC_SING_IN) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {

                val user = FirebaseAuth.getInstance().currentUser
                startActivity(Intent(this, ActividadPartidas::class.java))

            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
                Toast.makeText(this, response!!.error!!.errorCode, Toast.LENGTH_LONG).show()
            }
        }
    }
}