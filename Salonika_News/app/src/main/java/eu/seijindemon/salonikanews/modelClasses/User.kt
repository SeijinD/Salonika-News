package eu.seijindemon.salonikanews.modelClasses

data class User(
    private var uid: String? = null,
    private var username: String? = null,
    private var firstName: String? = null,
    private var lastName: String? = null,
    private var profile: String? = null
)