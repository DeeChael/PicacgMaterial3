package com.shicheeng.picacgmaterial3.fragment.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.shicheeng.picacgmaterial3.R
import com.shicheeng.picacgmaterial3.viewmodel.MainViewModel

class LoginDialogFragment : BottomSheetDialogFragment() {

    companion object {
        const val TAG = "TAG_LOGIN_DIALOG"
    }

    private lateinit var userNameInputLayout: TextInputLayout
    private lateinit var passWordInputLayout: TextInputLayout
    private lateinit var loginBtn: MaterialButton
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val viewRoot = inflater.inflate(R.layout.login_dialog, container, false)

        userNameInputLayout = viewRoot.findViewById(R.id.username_input_layout)
        passWordInputLayout = viewRoot.findViewById(R.id.password_input_layout)
        loginBtn = viewRoot.findViewById(R.id.login_btn)

        userNameInputLayout.helperText = getString(R.string.helper_text)
        return viewRoot
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loginBtn.setOnClickListener {
            val name = userNameInputLayout.editText!!.text.toString()
            val pw = passWordInputLayout.editText?.editableText!!.toString()
            viewModel.login(name, pw)
        }

        viewModel.dataTokenError.observe(viewLifecycleOwner) {
            userNameInputLayout.error = it
            passWordInputLayout.error = it
        }

        ViewCompat.setOnApplyWindowInsetsListener(view) { viewIn: View, windowInsetsCompat: WindowInsetsCompat ->

            val insets = windowInsetsCompat.getInsets(WindowInsetsCompat.Type.systemBars())
            viewIn.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                leftMargin = insets.left
                rightMargin = insets.right
            }

            val insetsIme = windowInsetsCompat.getInsets(WindowInsetsCompat.Type.ime())
            loginBtn.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                bottomMargin = insetsIme.bottom
            }

            WindowInsetsCompat.CONSUMED
        }
    }


}