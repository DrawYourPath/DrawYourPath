package com.epfl.drawyourpath.preferences

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.authentication.Auth
import com.epfl.drawyourpath.authentication.createAuth

class ModifyPasswordFragment : Fragment(R.layout.fragment_modify_password) {

    private lateinit var auth: Auth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = createAuth(arguments ?: activity?.intent?.extras)

        view.findViewById<Button>(R.id.BT_Apply).setOnClickListener {
            onChangePasswordButtonPressed()
        }
    }

    /**
     * Called when the user presses on `APPLY` button.
     */
    private fun onChangePasswordButtonPressed() {
        if (!repeatPasswordMatches()) {
            showResult(getString(R.string.updatepassword_confirmation_not_matching))
            return
        }

        val user = auth.getUser()
        if (user == null) {
            showResult(getString(R.string.updatepassword_user_not_signed))
            return
        }

        user.updatePassword(getNewPassword())
            .thenApply {
                showResult(getString(R.string.updatepassword_success))
            }
            .exceptionally {
                showResult(it.localizedMessage ?: getString(R.string.updatepassword_unknown_error))
            }
    }

    /**
     * Checks that the password and its repetition match.
     */
    private fun repeatPasswordMatches(): Boolean {
        return requireView().findViewById<EditText>(R.id.ET_PasswordRepeat).text
            .toString() == getNewPassword()
    }

    /**
     * Gets the content of the new password input.
     */
    private fun getNewPassword(): String {
        return requireView().findViewById<EditText>(R.id.ET_Password).text.toString()
    }

    /**
     * Shows the result of password change, as a Toast.
     */
    private fun showResult(text: String) {
        Toast.makeText(activity, text, Toast.LENGTH_LONG).show()
    }
}
