package eu.seijindemon.salonikanews

data class PostInfo(
    var postList: ArrayList<Post> = arrayListOf()
)

data class Post(
    var title: String,
    var category: String,
    var author: String,
    var admin: String,
    var date: String
)