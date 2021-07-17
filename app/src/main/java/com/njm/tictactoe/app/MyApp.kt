package com.njm.tictactoe.app

import android.app.Application
import com.google.firebase.FirebaseApp

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()

        //para inicializar el servicio de firebase en la app,
        //Aplicar en el manifest android:name=".app.MyApp"
        FirebaseApp.initializeApp(this)
    }
}