package eu.seijindemon.salonikanews.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import eu.seijindemon.salonikanews.R
import kotlinx.android.synthetic.main.fragment_add_post.*
import kotlinx.android.synthetic.main.fragment_add_post.view.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import www.sanju.motiontoast.MotionToast
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.HashMap

class AddPostFragment : Fragment() {

    private var postReference :  DatabaseReference? = null
    private var userReference :  DatabaseReference? = null
    private var database: FirebaseDatabase? = null
    private var storageRef: StorageReference? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser

    private val RequestCode = 438
    private var imageUri: Uri? = null
    private lateinit var postId: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_add_post, container, false)

        setupFirebase()

        val categoryItems = listOf("Athletics","Politics","Competitions")
        val adapter = ArrayAdapter(requireContext(), R.layout.category_list_item, categoryItems)
        view.autoTextCategory.setAdapter(adapter)

        postId = System.currentTimeMillis().toString()

        view.uploadPostButton.setOnClickListener{
            uploadPost()
        }

        view.post_image.setOnClickListener{
            pickImage()
        }

        return view
    }

    // Firebase objects setup
    private fun setupFirebase()
    {
        database = FirebaseDatabase.getInstance()
        postReference = database?.reference!!.child("post")
        userReference = database?.reference!!.child("profile")
        storageRef = FirebaseStorage.getInstance().reference.child("Post Images")
        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!
    }
    // End Firebase objects setup

    // Upload Post
    @SuppressLint("SimpleDateFormat")
    private fun uploadPost()
    {
        val title = titlePost.text.toString()
        val description = descriptionPost.text.toString()
        val category = autoTextCategory.text.toString()
        val author = user.displayName!!
        val admin = "Admin"

        // Work and under 26 Api
        val dateFormat = SimpleDateFormat("HH:mm dd-MM-yyyy")
        val calendar = Calendar.getInstance().time
        val currentDate = dateFormat.format(calendar)
        val date = currentDate

        // Work only up 26 Api
        //val currentDate = LocalDateTime.now()
        //val formatter = DateTimeFormatter.ofPattern("HH:mm dd-MM-yyyy")
        //val date = currentDate.format(formatter)


        val currentPostDb = postReference?.child(postId)
        currentPostDb?.child("title")?.setValue(title)
        currentPostDb?.child("description")?.setValue(description)
        currentPostDb?.child("category")?.setValue(category)
        currentPostDb?.child("author")?.setValue(author)
        currentPostDb?.child("admin")?.setValue(admin)
        currentPostDb?.child("date")?.setValue(date)

        if(imageUri != null)
        {
            uploadImageToDatabase()
        }

        MotionToast.Companion.createColorToast(
                this.requireActivity(),
                "Successful",
                "Post Uploaded",
                MotionToast.Companion.TOAST_SUCCESS,
                MotionToast.Companion.GRAVITY_BOTTOM,
                MotionToast.Companion.LONG_DURATION,
                ResourcesCompat.getFont(this.requireContext(), R.font.helvetica_regular))
    }
    // End Upload Post

    // Post Image
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
//            uploadImageToDatabase()
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

                    val mapPostImg = HashMap<String, Any>()
                    mapPostImg["post_image"] = url
                    val currentUSerDb = postReference?.child((postId))
                    currentUSerDb?.updateChildren(mapPostImg)
                }
                prograssBar.dismiss()
            }
        }
    }
    // End Post Image

}


