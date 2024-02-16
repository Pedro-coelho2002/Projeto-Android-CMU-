package pt.ipp.estg.peddypaper.ui.Models

data class Location(
    val type: String,
    val features: List<Feature>
)

data class Feature(
    val type: String,
    val properties: Properties,
)

data class Properties(
    val name: String?,
    val country: String,
    val country_code: String,
    val county: String,
    val city: String,
    val postcode: String,
    val district: String,
    val suburb: String,
    val street: String,
    val lon: Double,
    val lat: Double,
    val formatted: String,
    val address_line1: String,
    val address_line2: String,
    val categories: List<String>,
    val details: List<String>,
    val distance: Int,
    val place_id: String
)