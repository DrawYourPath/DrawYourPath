package com.epfl.drawyourpath.preferences

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.ImageDecoder
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.database.Database
import com.epfl.drawyourpath.database.FireDatabase
import com.epfl.drawyourpath.database.MockDataBase
import java.util.concurrent.CompletableFuture

class ModifyProfilePhotoFragment : Fragment(R.layout.fragment_modify_profile_photo) {
    //for the test
    private val photoProfileTest : Bitmap = Bitmap.createBitmap(14,14, Bitmap.Config.RGB_565)

    private var isTest: Boolean = false

    private val requestCodeFrag = 100

    private lateinit var photoPreview: ImageView

    private lateinit var errorText: TextView

    private var photoInitiate: Boolean = false

    //new profile photo (=null if no photo selected)
    private var newProfilePhoto: Bitmap? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        var database: Database = FireDatabase()

        //retrieve the value from the welcome activity to know if we are running testes
        val isRunTest: Bundle? = arguments
        if (isRunTest == null) {
            isTest = false
        } else {
            isTest = isRunTest.getBoolean("isRunningTestForDataBase")
        }

        //select the correct database in function of test scenario
        if (isTest) {
            database = MockDataBase()
        }
        //retrieve the different elements of the UI
        val photoDescription: TextView = view.findViewById(R.id.photo_description_modify_profile_photo)
        errorText = view.findViewById(R.id.error_modify_profile_photo)
        photoPreview = view.findViewById(R.id.photo_modify_profile_photo)
        val selectPhotoButton: Button = view.findViewById(R.id.select_photo_modify_profile_photo)
        val cancelButton: Button = view.findViewById(R.id.cancel_modify_profile_photo)
        val validateButton: Button = view.findViewById(R.id.validate_modify_profile_photo)

        if(!photoInitiate){
            initPhotoPreview(photoPreview, database)
            photoInitiate=true
        }

        //create the photo picker
        createSelectPhotoButton(selectPhotoButton, isTest)

        actualizePhotoDescription(photoDescription)

        //return back to preferences if click on cancel button without modifying the username
        cancelButton.setOnClickListener{
            returnBackToPreviousFrag()
        }

        //set the new profile photo(print an error if no new photo have been selected) and go back to the previous fragment
        validateButton.setOnClickListener{
            validateButtonAction(newProfilePhoto, database, errorText)
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
                //remove the error, since a photo has been selected
                errorText.text = ""
            }
        }
    }

    /**
     * Helper function to set the description text of the photo in function if a new photo has been selected
     * @param textView where the description will be displayed
     */
    private fun actualizePhotoDescription(textView: TextView){
        if(newProfilePhoto != null){
            textView.text = getString(R.string.new_profile_photo_selected)
        }
    }

    /**
     * Helper function to init the photo preview with the actual photo profile and an icon photo if the user don't have one
     * @param photoPreview photo preview used to display the photo on the UI
     * @param database used to retrieve the profile photo
     */
    private fun initPhotoPreview(photoPreview: ImageView, database: Database){
        database.getLoggedUserAccount().thenAccept { user->
            val profilePhoto = user.getProfilePhoto()
            //we let the default image if the user don't have a profile photo
            if(profilePhoto != null){
                photoPreview.setImageBitmap(profilePhoto)
            }
        }
    }

    /**
     * Helper function to create a photo picker when clicking on select a photo button
     * @param selectButton button used to lunch the photo picker
     * @param isTest to know if we are in a test scenario to evict the photo picker view in this case
     */
    private fun createSelectPhotoButton(selectButton: Button, isTest: Boolean) {
        selectButton.setOnClickListener {
            if(!isTest){
                val photoPicker =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                startActivityForResult(photoPicker, requestCodeFrag)
            }else{
                //affect a simple image in test scenario
                newProfilePhoto = photoProfileTest
            }
        }
    }

    /**
     * Helper function to set the new profile photo into the database and go back to the previous fragment(display an error message if no new photo have been selected)
     * @param profilePhoto that we want to set into the database
     * @param database used to store the user information
     * @param errorMessage text view to display the potential error message to the user
     */
    private fun validateButtonAction(profilePhoto: Bitmap?, database: Database, errorMessage: TextView){
        if (profilePhoto == null) {
            errorMessage.text = getString(R.string.error_select_new_profile_photo)
            errorMessage.setTextColor(Color.RED)
        } else {
            database.setProfilePhoto(profilePhoto).thenAccept {isPhotoSet->
                if (isPhotoSet) {
                    returnBackToPreviousFrag()
                }
            }
        }
    }

    /**
     * Helper function to return back to the previous fragment
     */
    private fun returnBackToPreviousFrag(){
        activity?.onBackPressed()
    }
}