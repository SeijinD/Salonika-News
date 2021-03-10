package eu.seijindemon.salonikanews.modelClasses

data class PostInfo(
    var postList: ArrayList<Post> = arrayListOf()
)

data class Post(
        var title: String? = null,
        var description: String? = null,
        var category: String? = null,
        var author: String? = null,
        var admin: String? = null,
        var date: String? = null,
        var post_image: String? = null
)