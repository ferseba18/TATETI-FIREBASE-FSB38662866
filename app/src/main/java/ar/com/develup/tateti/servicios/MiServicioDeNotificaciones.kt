package ar.com.develup.tateti.servicios

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MiServicioDeNotificaciones  : FirebaseMessagingService() {
    // Esta función se llama cada vez que Firebase cambia el token
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Guardar el token, enviarlo a nuestro servidor, etc
    }

    // Esta función se llama cada vez que recibimos un mensaje con datos personalizados
    // o bien nuestra app esta en foreground (visible)
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        // Manejar el mensaje recibido
    }

}