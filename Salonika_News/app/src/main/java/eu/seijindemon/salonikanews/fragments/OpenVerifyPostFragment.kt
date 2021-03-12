package eu.seijindemon.salonikanews.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import eu.seijindemon.salonikanews.R
import kotlinx.android.synthetic.main.open_verify_card_post.view.*

class OpenVerifyPostFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view =  inflater.inflate(R.layout.open_verify_card_post, container, false)

        view.recycler_title_2.text = VerifyPostFragment.openPostVerify.title
        view.recycler_description_2.text = VerifyPostFragment.openPostVerify.description
        Picasso.get().load(VerifyPostFragment.openPostVerify.post_image).into(view.recycler_image_2)

        return view
    }

}