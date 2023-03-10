package com.github.drawyourpath.bootcamp.userProfileCreation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.github.drawyourpath.bootcamp.R
import com.github.drawyourpath.bootcamp.database.Database
import com.github.drawyourpath.bootcamp.database.FireDatabase
import com.github.drawyourpath.bootcamp.database.MockDataBase
import com.github.drawyourpath.bootcamp.userProfileCreation.personalInfoForm.PersonalInfoFragment

class UserNameTestAndSetFragment : Fragment() {

    private var isTest: Boolean =false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_user_name_test_and_set, container, false)


        //retrieve the value from the welcome activity to know if we are running testes
        val isRunTest: Bundle? = arguments
        if(isRunTest == null) {
            isTest = false
        }else{
            isTest = isRunTest.getBoolean("isRunningTestForDataBase")
        }

        //select the correct database in function of test scenario
        var database: Database? = null
        if(isTest){
            database = MockDataBase()
        }else{
            database = FireDatabase()
        }

        val testUserNameButton: Button = view.findViewById(R.id.testUserName_button_userProfileCreation)
        val inputUserName: EditText = view.findViewById(R.id.input_userName_text_UserProfileCreation)
        val showTestResult: TextView = view.findViewById(R.id.testUserName_text_userProfileCreation)



        testUserNameButton.setOnClickListener{
            database.isUserNameAvailable(inputUserName.text.toString(), showTestResult)
        }

        val setUserNameButton: Button = view.findViewById(R.id.setUserName_button_userProfileCreation)
        setUserNameButton.setOnClickListener{
            database.setUserName(inputUserName.text.toString(), showTestResult)
            val previousActivity = activity
            if(previousActivity!=null){
                val fragManagement = previousActivity.supportFragmentManager.beginTransaction()
                val dataTopPersoInfoFrag: Bundle = Bundle()
                //data to transmit to the PersonalInfoFragment(username + isTest)
                dataTopPersoInfoFrag.putBoolean("isRunningTestForDataBase", isTest)
                dataTopPersoInfoFrag.putString("userName", inputUserName.text.toString())
                val persoInfoFrag = PersonalInfoFragment()
                persoInfoFrag.arguments = dataTopPersoInfoFrag
                fragManagement.replace(R.id.userName_frame, persoInfoFrag).commit()
            }
        }
        return view
    }
}