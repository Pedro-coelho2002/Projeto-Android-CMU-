package pt.ipp.estg.peddypaper.ui.Room

data class User (
    var name: String = "",
    var email: String = "",
    var password: String = "",
    var numeroDeTelefone: String = "",
    var points: Int = 0,
) {
}