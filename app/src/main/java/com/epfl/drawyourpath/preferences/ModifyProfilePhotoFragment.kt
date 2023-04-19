package com.epfl.drawyourpath.preferences

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.ImageDecoder
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.database.MockDataBase
import com.epfl.drawyourpath.userProfile.cache.UserModelCached

class ModifyProfilePhotoFragment : Fragment(R.layout.fragment_modify_profile_photo) {
    //for the test
    private val photoProfileTest: Bitmap = Bitmap.createBitmap(8, 5, Bitmap.Config.RGB_565)

    private var isTest: Boolean = false

    private val requestCodeFrag = 100

    private lateinit var photoPreview: ImageView

    private lateinit var errorText: TextView

    private lateinit var photoDescription: TextView

    private var photoInitiate: Boolean = false

    //new profile photo (=null if no photo selected)
    private var newProfilePhoto: Bitmap? = null

    private val user: UserModelCached by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        //retrieve the value from the welcome activity to know if we are running testes
        val isRunTest: Bundle? = arguments
        if (isRunTest != null) {
            isTest = isRunTest.getBoolean("isRunningTestForDataBase")
        }

        //select the correct database in function of test scenario
        if (isTest) {
            user.setDatabase(MockDataBase())
        }
        //retrieve the different elements of the UI
        photoDescription = view.findViewById(R.id.photo_description_modify_profile_photo)
        errorText = view.findViewById(R.id.error_modify_profile_photo)
        photoPreview = view.findViewById(R.id.photo_modify_profile_photo)
        val selectPhotoButton: Button = view.findViewById(R.id.select_photo_modify_profile_photo)
        val cancelButton: Button = view.findViewById(R.id.cancel_modify_profile_photo)
        val validateButton: Button = view.findViewById(R.id.validate_modify_profile_photo)

        if (!photoInitiate) {
            initPhotoPreview(photoPreview)
            photoInitiate = true
        }

        //create the photo picker
        createSelectPhotoButton(selectPhotoButton, isTest)

        //return back to preferences if click on cancel button without modifying the username
        cancelButton.setOnClickListener {
            returnBackToPreviousFrag()
        }

        //set the new profile photo(print an error if no new photo have been selected) and go back to the previous fragment
        validateButton.setOnClickListener {
            validateButtonAction(newProfilePhoto, errorText)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == requestCodeFrag) {
            val imageURI = data?.data
            if (imageURI != null) {
                val sourceURI =
                    ImageDecoder.createSource(requireActivity().contentResolver, imageURI)
                newProfilePhoto = ImageDecoder.decodeBitmap(sourceURI)
                photoPreview.setImageBitmap(newProfilePhoto)
            }
        }
    }

    /**
     * Helper function to init the photo preview with the actual photo profile and an icon photo if the user don't have one
     * @param photoPreview photo preview used to display the photo on the UI
     */
    private fun initPhotoPreview(photoPreview: ImageView) {
        user.getUser().observe(viewLifecycleOwner) {
            photoPreview.setImageBitmap(it.getProfilePhotoOrDefaultAsBitmap(resources))
        }
        photoPreview.tag = R.drawable.profile_placholderpng
    }

    /**
     * Helper function to create a photo picker when clicking on select a photo button
     * @param selectButton button used to lunch the photo picker
     * @param isTest to know if we are in a test scenario to evict the photo picker view in this case
     */
    private fun createSelectPhotoButton(selectButton: Button, isTest: Boolean) {
        selectButton.setOnClickListener {
            //remove the error, since a photo has been selected
            errorText.text = ""
            photoDescription.text = getString(R.string.new_profile_photo_selected)
            if (!isTest) {
                val photoPicker =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                startActivityForResult(photoPicker, requestCodeFrag)
            } else {
                //affect a simple image in test scenario
                newProfilePhoto = photoProfileTest
                photoPreview.setImageBitmap(photoProfileTest)
                photoPreview.tag = photoProfileTest.byteCount
            }
        }
    }

    /**
     * Helper function to set the new profile photo into the database and go back to the previous fragment
     * (display an error message if no new photo have been selected)
     * @param profilePhoto that we want to set into the database
     * @param errorMessage text view to display the potential error message to the user
     */
    private fun validateButtonAction(profilePhoto: Bitmap?, errorMessage: TextView) {
        if (profilePhoto == null) {
            errorMessage.text = getString(R.string.error_select_new_profile_photo)
            errorMessage.setTextColor(Color.RED)
        } else {
            user.updateProfilePhoto(profilePhoto).thenAcceptAsync { isPhotoSet ->
                if (isPhotoSet) {
                    returnBackToPreviousFrag()
                }
            }
        }
    }

    /**
     * Helper function to return back to the previous fragment
     */
    private fun returnBackToPreviousFrag() {
        requireActivity().supportFragmentManager.popBackStack()
    }
}