package com.example.ligasmartapp1

sealed class Routes (val route: String) {
    //Administrador
    object Login: Routes("login")
    object Torneo: Routes("torneo")
    object TorneoNavigation: Routes("torneonavigation/{torneoId}")

    object EditarPerfil: Routes("editarperfil")
//    object Notificaciones: Routes("notificaciones")
    object Home: Routes("home/{userId}")
    object TerminosCondiciones: Routes("terminoscondiciones")
    object SplashScreen: Routes("splashScreen")
    object AcercaDeApp: Routes("acercadeapp")

    // ejemplo
    object HomeCapitan: Routes("homecapitan")
    object AcercaDeAppCapitan: Routes("acercadeappcapitan")
    object JugadoresCapitan: Routes("jugadorescapitan")
    object CrearJugadorFormCapitan : Routes("crearjugadorformcapitan/{equipoId}")

    //    object CrearJugadorFormCapitan: Routes("crearjugadorformcapitan")
    object EditarPerfilCapitan: Routes("editarperfilcapitan")
    object EstadisiticaslCapitan: Routes("estadisticascapitan")
}