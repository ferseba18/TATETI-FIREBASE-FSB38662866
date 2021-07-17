package com.njm.tictactoe.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.njm.tictactoe.R

class RegistroActivity : AppCompatActivity() {

    private lateinit var txtName: EditText
    private lateinit var txtEmail: EditText
    private lateinit var txtContraseña: EditText
    private lateinit var btnRegistrar: Button
    private lateinit var firebaseAuth: FirebaseAuth // para la autenticacion
    //dejar registro del usuario en el servicio de firestore, para tener la lista de usuarios para hacer ranking
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var progressBarRegistro: ProgressBar
    private lateinit var formRegistro: ScrollView
    private lateinit var name: String
    private lateinit var mail: String
    private lateinit var contraseña: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        txtName = findViewById(R.id.editTextNameRegistro)
        txtEmail = findViewById(R.id.editTextEmailRegistro)
        txtContraseña = findViewById(R.id.editTextPasswordRegistro)
        btnRegistrar = findViewById(R.id.buttonRegistrar)
        progressBarRegistro = findViewById(R.id.progressBarRegistro)
        formRegistro = findViewById(R.id.formRegistro)


        //instanciar servicio de firebaseAuth
        firebaseAuth = FirebaseAuth.getInstance()
        changeRegistroFormVisibility(true)//por defecto mostrar el formulario de registro
        events()


    }


    private fun events() {
        btnRegistrar.setOnClickListener{
           name = txtName.text.toString()
           mail  = txtEmail.text.toString()
           contraseña = txtContraseña.text.toString()

            if(name.isEmpty()){
                txtName.error = "El nombre es obligatorio"
            }else if(mail.isEmpty()){
                txtEmail.error = "El email es obligatorio"
            }else if(contraseña.isEmpty()){
                txtContraseña.error = "La contraseña es obligatoria"
            }else{
                //todo: realizar registro en firebase Auth
                createUser()
            }

        }
    }

    private fun createUser() {
        changeRegistroFormVisibility(false)//cuando se intente crear un usuario se oculta el formulario
        //invocar al servicio
        firebaseAuth!!.createUserWithEmailAndPassword(mail, contraseña)
            .addOnCompleteListener(this){ task ->
                if(task.isSuccessful){
                    val user: FirebaseUser? = firebaseAuth.currentUser //obtener info del usuario registrado
                    updateUI(user) //actualizacion de la interfaz de usuario
                } else{
                    Log.w("TAG", "ErrorRegistro: "+task.exception)
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
            changeRegistroFormVisibility(true)
            txtContraseña.error = "Credenciales incorrectas"
            txtContraseña.requestFocus()
        }

    }


    private fun changeRegistroFormVisibility(showForm: Boolean) {
        if (showForm){
            progressBarRegistro.visibility = View.GONE
            formRegistro.visibility = View.VISIBLE
        }else{
            progressBarRegistro.visibility = View.VISIBLE
            formRegistro.visibility = View.GONE
        }
    }

}
