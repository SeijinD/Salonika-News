package eu.seijindemon.salonikanews.modelClasses

data class User(
    private var uid: String = "",
    private var username: String = "",
    private var firstName: String = "",
    private var lastName: String = "",
    private var profile: String = ""
)