package eu.seijindemon.salonikanews.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import eu.seijindemon.salonikanews.R
import kotlinx.android.synthetic.main.open_card_post.view.*

class OpenPostFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view =  inflater.inflate(R.layout.open_card_post, container, false)

        view.recycler_title_2.text = HomeFragment.openPost.title
        view.recycler_author.text = "Author: " + HomeFragment.openPost.author
        view.recycler_admin.text = "Admin: " + HomeFragment.openPost.admin
        view.recycler_description_2.text = HomeFragment.openPost.description
        Glide.with(view).load(HomeFragment.openPost.post_image).into(view.recycler_image_2)

        return view
    }

}