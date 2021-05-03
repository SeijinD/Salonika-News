package eu.seijindemon.salonikanews.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import eu.seijindemon.salonikanews.R
import kotlinx.android.synthetic.main.email_form.*
import kotlinx.android.synthetic.main.email_form.view.*
import www.sanju.motiontoast.MotionToast
import java.lang.Exception

class EmailFormFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.email_form, container, false)

        view.send_button.setOnClickListener{
            val subject = subject_form.text.toString().trim()
            val message = message_form.text.toString().trim()
            val email = "georgekara2010@yahoo.gr"

            sendEmail(email, subject, message)
        }

        return view
    }

    private fun sendEmail(email: String, subject: String, message: String) {

        val mIntent = Intent(Intent.ACTION_SEND)

        mIntent.data = Uri.parse("mailto:")
        mIntent.type = "text/plain"

        mIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
        mIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
        mIntent.putExtra(Intent.EXTRA_TEXT, message)

        try {
            startActivity(Intent.createChooser(mIntent, "Choose Email Client:"))
        } catch (e: Exception) {
            MotionToast.Companion.createColorToast(
                requireActivity(),
                "Error",
                "Error Message: ${e.message}",
                MotionToast.Companion.TOAST_ERROR,
                MotionToast.Companion.GRAVITY_BOTTOM,
                MotionToast.Companion.LONG_DURATION,
                ResourcesCompat.getFont(requireContext(), R.font.helvetica_regular))
        }
    }

}