package eu.seijindemon.salonikanews

data class ArticleInfo(
    var articleList: ArrayList<Article> = arrayListOf()
)

data class Article(
    var title: String,
    var category: String,
    var author: String,
    var admin: String,
    var date: String
)