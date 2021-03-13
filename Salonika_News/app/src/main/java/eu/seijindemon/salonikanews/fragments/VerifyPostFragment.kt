package eu.seijindemon.salonikanews.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import eu.seijindemon.salonikanews.R
import eu.seijindemon.salonikanews.modelClasses.Post
import kotlinx.android.synthetic.main.card_verify_post.view.*
import kotlinx.android.synthetic.main.fragment_verify_post.*
import kotlinx.android.synthetic.main.fragment_verify_post.view.*
import www.sanju.motiontoast.MotionToast


class VerifyPostFragment : Fragment() {

    private var database: FirebaseDatabase? = null
    private var storageRef: StorageReference? = null

    private lateinit var postRecyclerView: RecyclerView

    companion object{
        lateinit var openPostVerify: Post
        private var postReference :  DatabaseReference? = null
        private var verifiedPostReference :  DatabaseReference? = null

        private lateinit var user: FirebaseUser
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onResume() {
        super.onResume()
        val categoryItems = listOf("All", "Athletics", "Politics", "Competitions")
        val adapterCat = ArrayAdapter(requireContext(), R.layout.category_list_item, categoryItems)
        autoTextCategory.setAdapter(adapterCat)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_verify_post, container, false)

        setupFirebase()

        postRecyclerView = view.recycler_view
        postRecyclerView.layoutManager = LinearLayoutManager(context)

        val categoryItems = listOf("All", "Athletics", "Politics", "Competitions")
        val adapterCat = ArrayAdapter(requireContext(), R.layout.category_list_item, categoryItems)
        view.autoTextCategory.setAdapter(adapterCat)

        val query = postReference?.limitToLast(3)

        val options = FirebaseRecyclerOptions.Builder<Post>()
            .setQuery(query!!, Post::class.java)
            .setLifecycleOwner(this)
            .build()

        val adapter = object : FirebaseRecyclerAdapter<Post, PostHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
                return PostHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.card_verify_post, parent, false)
                )
            }

            override fun onBindViewHolder(holder: PostHolder, position: Int, model: Post) {
                holder.bind(model)
            }

        }
        postRecyclerView.adapter = adapter

        view.recycler_search_button.setOnClickListener{
            val categoryPost = view.autoTextCategory.text.toString()

            var queryCategory = postReference?.orderByChild("category")?.equalTo(categoryPost)?.limitToLast(
                3
            )
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
                    return PostHolder(
                        LayoutInflater.from(parent.context)
                            .inflate(R.layout.card_verify_post, parent, false)
                    )
                }

                override fun onBindViewHolder(holder: PostHolder, position: Int, model: Post) {
                    holder.bind(model)
                }

            }
            postRecyclerView.adapter = adapterCategory
        }

        return view
    }

    class PostHolder(private val customerView: View, post: Post? = null) : RecyclerView.ViewHolder(
        customerView
    )
    {
        @SuppressLint("SetTextI18n")
        fun bind(post: Post){
            with(post){
                customerView.recycler_title.text = post.title
                customerView.recycler_description.text = post.description?.take(60) + "... Read More"
                Picasso.get().load(post.post_image).into(customerView.recycler_image)
                customerView.recycler_verify_button.setOnClickListener{verifyPost(
                    customerView,
                    post
                )}
                customerView.recycler_read_button.setOnClickListener{readMore(customerView, post)}
            }
        }

        private fun readMore(view: View, post: Post)
        {
            Navigation.findNavController(view).navigate(R.id.openVerifyPostFragment)
            openPostVerify = post
        }

        private fun verifyPost(view: View, post: Post)
        {
            val postId: String = System.currentTimeMillis().toString()
            val currentPostDb = VerifyPostFragment.verifiedPostReference?.child(postId)
            currentPostDb?.child("title")?.setValue(post.title)
            currentPostDb?.child("description")?.setValue(post.description)
            currentPostDb?.child("category")?.setValue(post.category)
            currentPostDb?.child("author")?.setValue(post.author)
            currentPostDb?.child("admin")?.setValue(user.displayName)
            currentPostDb?.child("date")?.setValue(post.date)
            currentPostDb?.child("post_image")?.setValue(post.post_image)

            MotionToast.Companion.createColorToast(
                view.context as Activity,
                "Successful",
                "Post Verified",
                MotionToast.Companion.TOAST_SUCCESS,
                MotionToast.Companion.GRAVITY_BOTTOM,
                MotionToast.Companion.LONG_DURATION,
                ResourcesCompat.getFont(view.context as Activity, R.font.helvetica_regular)
            )

            val query: Query? = postReference?.orderByChild("title")?.equalTo(post.title)

            query?.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (snap in snapshot.children)
                    {
                        snap.ref.removeValue()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        }
    }

    // Firebase objects setup
    private fun setupFirebase()
    {
        database = FirebaseDatabase.getInstance()
        verifiedPostReference = database?.reference!!.child("verified_post")
        postReference = database?.reference!!.child("post")
        storageRef = FirebaseStorage.getInstance().reference.child("Post Images")

        user = FirebaseAuth.getInstance().currentUser!!
    }
    // End Firebase objects setup

}