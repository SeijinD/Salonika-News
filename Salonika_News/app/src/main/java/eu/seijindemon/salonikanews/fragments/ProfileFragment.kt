package eu.seijindemon.salonikanews.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import eu.seijindemon.salonikanews.LoginActivity
import eu.seijindemon.salonikanews.MainActivity
import eu.seijindemon.salonikanews.R
import kotlinx.android.synthetic.main.activity_login.view.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import kotlinx.android.synthetic.main.navigation_header.*
import www.sanju.motiontoast.MotionToast


class ProfileFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    private var userReference :  DatabaseReference? = null
    private var database: FirebaseDatabase? = null
    private var storageRef: StorageReference? = null

    private val RequestCode = 438
    private var imageUri: Uri? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        userReference = database?.reference!!.child("profile")
        storageRef = FirebaseStorage.getInstance().reference.child("User Images")
        user = auth.currentUser!!
        val userRef = userReference?.child(user.uid)!!

        loadProfile(user, userRef)

        view.logoutButton.setOnClickListener{
            auth.signOut()
            val intent = Intent(context, LoginActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }

        view.deleteProfileButton.setOnClickListener{
            val mBuilder = AlertDialog.Builder(requireContext())
            mBuilder.setTitle("Are you Sure?")
            mBuilder.setMessage("If you delete your account, you will be a guest user.")
            mBuilder.setPositiveButton("Delete") { dialog, which ->
                user = auth.currentUser!!
                user.delete()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful)
                        {
                            userRef.removeValue()
                            MotionToast.Companion.createColorToast(
                                    this.requireActivity(),
                                    "Successful",
                                    "Profile Deleted",
                                    MotionToast.Companion.TOAST_SUCCESS,
                                    MotionToast.Companion.GRAVITY_BOTTOM,
                                    MotionToast.Companion.LONG_DURATION,
                                    ResourcesCompat.getFont(this.requireContext(), R.font.helvetica_regular))

                            val intent = Intent(activity, LoginActivity::class.java)
                            startActivity(intent)
                            activity?.finish()
                        }
                        else
                        {
                            MotionToast.Companion.createColorToast(
                                    this.requireActivity(),
                                    "UnSuccessful",
                                    "Profile Not Deleted",
                                    MotionToast.Companion.TOAST_ERROR,
                                    MotionToast.Companion.GRAVITY_BOTTOM,
                                    MotionToast.Companion.LONG_DURATION,
                                    ResourcesCompat.getFont(this.requireContext(), R.font.helvetica_regular))
                        }
                    }
            }
            mBuilder.show()
        }
        view.profile_image.setOnClickListener{
            pickImage()
        }
        return view
    }

    // Profile Image
    private fun pickImage()
    {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, RequestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RequestCode && resultCode == Activity.RESULT_OK && data!!.data != null)
        {
            imageUri = data.data
            MotionToast.Companion.createColorToast(
                    this.requireActivity(),
                    "Wait",
                    "Image Uploading...",
                    MotionToast.Companion.TOAST_INFO,
                    MotionToast.Companion.GRAVITY_BOTTOM,
                    MotionToast.Companion.LONG_DURATION,
                    ResourcesCompat.getFont(this.requireContext(), R.font.helvetica_regular))
            uploadImageToDatabase()
        }
    }

    private fun uploadImageToDatabase()
    {
        val prograssBar = ProgressDialog(context)
        prograssBar.setMessage("Image is uploading, please wait...")
        prograssBar.show()

        if(imageUri != null)
        {
            val fileRef = storageRef!!.child(System.currentTimeMillis().toString() + ".jpg")
            val uploadTask: StorageTask<*>
            uploadTask = fileRef.putFile(imageUri!!)

            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation fileRef.downloadUrl
            }).addOnCompleteListener{ task ->
                if(task.isSuccessful)
                {
                    val downloadUrl = task.result
                    val url = downloadUrl.toString()

                    val mapProfileImg = HashMap<String, Any>()
                    mapProfileImg["profile"] = url
                    val currentUSerDb = userReference?.child((auth.currentUser!!.uid))
                    currentUSerDb?.updateChildren(mapProfileImg)
                }
                prograssBar.dismiss()
            }
        }
    }
    // End Profile Image

    // Load Profile Information + Photo
    @SuppressLint("SetTextI18n")
    private fun loadProfile(user: FirebaseUser, userRef: DatabaseReference)
    {
//        val font = Typeface.createFromAsset(this.requireActivity().assets, "")
//        firstNameProfile.typeface = font
//        lastNameProfile.typeface = font
//        emailProfile.typeface = font
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (context != null) {
                    firstNameProfile.text = snapshot.child("firstname").value.toString()
                    lastNameProfile.text = snapshot.child("lastname").value.toString()
                    emailProfile.text = user.email
                    if (snapshot.hasChild("profile"))
                    {
                        Picasso.get().load(snapshot.child("profile").value.toString()).into(profile_image)
                    }
                    else
                    {
                        Picasso.get().load(R.drawable.default_profile).into(profile_image)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
    // End Load Profile Information + Photo

}