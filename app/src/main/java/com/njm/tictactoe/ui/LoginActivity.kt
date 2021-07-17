package com.njm.tictactoe.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.example.myapplication.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.njm.tictactoe.ui.RegistroActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var etEmailLogin: EditText
    private lateinit var etPasswordLogin: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnRegistro: Button
    private lateinit var formLogin: ScrollView
    private lateinit var progressBarLogin: ProgressBar
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var email: String
    private lateinit var password: String
    private var tryLogin: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        loadViews()
        firebaseAuth = FirebaseAuth.getInstance()
        changeLoginFormVisibility(true)
        eventos()

    }

    private fun loadViews(){
        etEmailLogin = findViewById(R.id.editTextEmailRegistro)
        etPasswordLogin = findViewById(R.id.editTextPasswordRegistro)
        btnLogin = findViewById(R.id.buttonLogin)
        btnRegistro = findViewById(R.id.buttonRegistrar)
        formLogin = findViewById(R.id.formLogin)
        progressBarLogin = findViewById(R.id.progressBarLogin)
    }

    private fun eventos() {
        btnLogin.setOnClickListener {
            email = etEmailLogin.text.toString()
            password = etPasswordLogin.text.toString()


            if(email.isEmpty()){
                etEmailLogin.setError("El email es obligatorio")
            }else if(password.isEmpty()){
                etPasswordLogin.setError("La contraseña es obligatoria")
            }else{
                //todo: ralizar login
                changeLoginFormVisibility(false)
                loginUser()

            }
        }
        //registro
        btnRegistro.setOnClickListener { v ->
            val intent: Intent = Intent(this, RegistroActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loginUser() {
        firebaseAuth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener(this){task ->
                if(task.isSuccessful){
                    tryLogin = true
                    val user: FirebaseUser? = firebaseAuth.currentUser
                    updateUI(user)
                }else{
                    Log.w("TAG", "ErrorLogueo: "+task.exception)
                    Toast.makeText(this, "Error en el registro", Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {

        if(user != null){
            //almacenar la info del usuario en forestore, y pasar a la siguiente pantalla de la app: FindGameActivity
            val i = Intent(this, FindGameActivity::class.java)
            startActivity(i)
        }else{
            changeLoginFormVisibility(true)
            if (tryLogin){
                etPasswordLogin.error = "Credenciales incorrectas"
                etPasswordLogin.requestFocus()
            }
        }

    }

    private fun changeLoginFormVisibility(showForm: Boolean) {
        if (showForm){
            progressBarLogin.visibility = View.GONE
            formLogin.visibility = View.VISIBLE
        }else{
            progressBarLogin.visibility = View.VISIBLE
            formLogin.visibility = View.GONE
        }
    }

    override fun onStart() {
        super.onStart()

        //comprobar previamente si el usuario ya ha iniciado sesion en
        //este dispositivo, si es asi, entonces lo mando directo a FindGameActivity
        val currentUser: FirebaseUser? = firebaseAuth.currentUser
        updateUI(currentUser)
    }
}
