package com.epfl.drawyourpath.userProfileCreation

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.ImageDecoder
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.database.MockDatabase
import com.epfl.drawyourpath.userProfile.cache.UserModelCached
import java.util.concurrent.CompletableFuture

class PhotoProfileInitFragment : Fragment(R.layout.fragment_photo_profile_init) {
    // for the test
    private val photoProfileTest: Bitmap = Bitmap.createBitmap(14, 14, Bitmap.Config.RGB_565)

    private var isTest: Boolean = false
    private var username: String = ""

    private var photoProfile: Bitmap? = null
    private val requestCodeFrag = 100

    private lateinit var imageView: ImageView
    private lateinit var errorText: TextView

    val userCached: UserModelCached by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // retrieve the isRunTestValue and userName from the PersonalInfoFragment
        val argsFromLastFrag: Bundle? = arguments
        if (argsFromLastFrag == null) {
            isTest = false
        } else {
            isTest = argsFromLastFrag.getBoolean(PROFILE_TEST_KEY)
            username = argsFromLastFrag.getString(PROFILE_USERNAME_KEY).toString()
        }

        // select the correct database in function of test scenario
        if (isTest) {
            userCached.setDatabase(MockDatabase())
        }

        imageView = view.findViewById(R.id.imagePhotoProfileInitFrag)
        errorText = view.findViewById(R.id.photoProfile_error_text)

        createSelectPhotoButton(view, isTest)

        createSkipButton(view)

        createValidateButton(view)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == requestCodeFrag) {
            val imageURI = data?.data
            if (imageURI != null) {
                val sourceURI =
                    ImageDecoder.createSource(requireActivity().contentResolver, imageURI)
                photoProfile = ImageDecoder.decodeBitmap(sourceURI)
                imageView.setImageBitmap(photoProfile)
                // remove the error, since a photo has been selected
                errorText.text = ""
            }
        }
    }

    /**
     * Helper function to show a message if the user forgot to select a photo and store the photo in the database if the photo is not null
     * @return a future to indicate if the photo was store in the database
     */
    private fun isPhotoSelected(): CompletableFuture<Boolean> {
        if (photoProfile == null) {
            errorText.text = "* You have forgotten to select a photo !"
            errorText.setTextColor(Color.RED)
            return CompletableFuture<Boolean>().thenApplyAsync { false }
        } else {
            return userCached.updateProfilePhoto(photoProfile!!)
                .thenApplyAsync { true }
        }
    }

    /**
     * Helper function to show the endProfileCreationFragment
     */
    private fun showEndProfileCreationFrag() {
        if (activity != null) {
            val fragManagement =
                requireActivity().supportFragmentManager.beginTransaction()
            val dataToEndProfileCreationFrag: Bundle = Bundle()
            // data to transmit to the UserGoalsInitFragment(username)
            dataToEndProfileCreationFrag.putString(PROFILE_USERNAME_KEY, username)
            val endProfileCreationFrag = EndProfileCreationFragment()
            endProfileCreationFrag.arguments = dataToEndProfileCreationFrag
            fragManagement.replace(
                R.id.userGoalInitFragment,
                endProfileCreationFrag,
            )
                .commit()
        } else {
            Log.e("DYP", "Failed to show end profile fragment: activity is null")
        }
    }

    /**
     * Helper function to create a photo picker when clicking on select a photo button
     * @param view where the button is displayed
     * @param isTest to know if we rae in a test scenario to evict the photo picker view in this case
     */
    private fun createSelectPhotoButton(view: View, isTest: Boolean) {
        val selectPhotoButton: Button = view.findViewById(R.id.selectPhotoInitPhotoFrag)
        selectPhotoButton.setOnClickListener {
            if (!isTest) {
                val photoPicker =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                @Suppress("DEPRECATION")
                startActivityForResult(photoPicker, requestCodeFrag)
            } else {
                // affect a simple image in test scenario
                photoProfile = photoProfileTest
            }
        }
    }

    /**
     * Helper function to create a skip button
     * @param view where the button is located
     */
    private fun createSkipButton(view: View) {
        // if we click on the skip button we simply pass the end profile creation fragment
        val skipButton: Button = view.findViewById(R.id.skipPhotoProfile_button_userProfileCreation)
        skipButton.setOnClickListener {
            showEndProfileCreationFrag()
        }
    }

    /**
     * Helper function to create a skip button
     * @param view where the button is located
     */
    private fun createValidateButton(view: View) {
        // if we click on the validate button we store the photo and ass the end profile creation fragment
        val validateButton: Button =
            view.findViewById(R.id.setPhotoProfile_button_userProfileCreation)
        validateButton.setOnClickListener {
            isPhotoSelected().thenApplyAsync {
                if (it) {
                    showEndProfileCreationFrag()
                } else {
                    Toast.makeText(context, "Failed to update photo.", Toast.LENGTH_SHORT).show()
                }
            }.exceptionally {
                Log.e("DYP", "Failed to set photo: ${it.message}")
            }
        }
    }
}
