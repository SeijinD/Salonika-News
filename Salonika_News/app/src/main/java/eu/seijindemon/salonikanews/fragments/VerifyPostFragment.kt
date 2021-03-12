package eu.seijindemon.salonikanews.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import eu.seijindemon.salonikanews.R
import eu.seijindemon.salonikanews.modelClasses.Post
import kotlinx.android.synthetic.main.card_verify_post.view.*
import kotlinx.android.synthetic.main.fragment_home.view.autoTextCategory
import kotlinx.android.synthetic.main.fragment_verify_post.view.*

class VerifyPostFragment : Fragment() {

    private var postReference :  DatabaseReference? = null
    private var database: FirebaseDatabase? = null
    private var storageRef: StorageReference? = null

    private lateinit var postRecyclerView: RecyclerView

    companion object{
        lateinit var openPost: Post
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_verify_post, container, false)

        setupFirebase()

        postRecyclerView = view.recycler_view
        postRecyclerView.layoutManager = LinearLayoutManager(context)

        val query = postReference?.limitToLast(3)

        val options = FirebaseRecyclerOptions.Builder<Post>()
            .setQuery(query!!, Post::class.java)
            .setLifecycleOwner(this)
            .build()

        val adapter = object : FirebaseRecyclerAdapter<Post, PostHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
                return PostHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.card_verify_post, parent, false))
            }

            override fun onBindViewHolder(holder: PostHolder, position: Int, model: Post) {
                holder.bind(model)
            }

        }
        postRecyclerView.adapter = adapter

        val categoryItems = listOf("All","Athletics","Politics","Competitions")
        val adapterCat = ArrayAdapter(requireContext(), R.layout.category_list_item, categoryItems)
        view.autoTextCategory.setAdapter(adapterCat)
        view.recycler_search_button.setOnClickListener{
            val categoryPost = view.autoTextCategory.text.toString()

            var queryCategory = postReference?.orderByChild("category")?.equalTo(categoryPost)?.limitToLast(3)
            if(categoryPost == "All")
            {
                queryCategory = postReference?.limitToLast(3)
            }

            val optionsCategory = FirebaseRecyclerOptions.Builder<Post>()
                .setQuery(queryCategory!!, Post::class.java)
                .setLifecycleOwner(this)
                .build()

            val adapterCategory = object : FirebaseRecyclerAdapter<Post, PostHolder>(optionsCategory) {
                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
                    return PostHolder(LayoutInflater.from(parent.context)
                        .inflate(R.layout.card_post, parent, false))
                }

                override fun onBindViewHolder(holder: PostHolder, position: Int, model: Post) {
                    holder.bind(model)
                }

            }
            postRecyclerView.adapter = adapterCategory
        }

        return view
    }

    class PostHolder(private val customerView: View, post: Post? = null) : RecyclerView.ViewHolder(customerView)
    {
        @SuppressLint("SetTextI18n")
        fun bind(post: Post){
            with(post){
                customerView.recycler_title.text = post.title
                customerView.recycler_description.text = post.description?.take(60) + "... Read More"
                Picasso.get().load(post.post_image).into(customerView.recycler_image)
                customerView.recycler_verify_button.setOnClickListener{verifyPost()}
                customerView.recycler_read_button.setOnClickListener{readMore(customerView,post)}
            }
        }

        private fun readMore(view: View, post: Post)
        {
            Navigation.findNavController(view).navigate(R.id.openPostFragment)
            openPost = post
        }

        private fun verifyPost()
        {

        }

    }

    // Firebase objects setup
    private fun setupFirebase()
    {
        database = FirebaseDatabase.getInstance()
        postReference = database?.reference!!.child("post")
        storageRef = FirebaseStorage.getInstance().reference.child("Post Images")
    }
    // End Firebase objects setup

}