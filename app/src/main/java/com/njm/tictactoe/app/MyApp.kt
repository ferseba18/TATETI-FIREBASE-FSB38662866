package com.njm.tictactoe.app

import android.app.Application
import com.google.firebase.FirebaseApp

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()

        //inicializar el servicio de firebase cuando inicia la app
        //para que se aplique esto, en el manifest lo declaramos: android:name=".app.MyApp"
        FirebaseApp.initializeApp(this)
    }
}