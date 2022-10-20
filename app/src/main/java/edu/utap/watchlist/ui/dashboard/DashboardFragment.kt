package edu.utap.watchlist.ui.dashboard

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import edu.utap.watchlist.R
import edu.utap.watchlist.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root



        binding.floatingActionButton.setOnClickListener {
            showDialog()
        }


        return root
    }

    //https://handyopinion.com/show-alert-dialog-with-an-input-field-edittext-in-android-kotlin/
    fun showDialog(){

        val input = EditText(binding.root.context)
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setHint("Enter List Name")
        input.inputType = InputType.TYPE_CLASS_TEXT
        input.setPadding(6)


        MaterialAlertDialogBuilder(requireActivity(), R.style.MaterialAlertDialog_rounded)
            .setTitle("New List")
            .setView(input)
            .setPositiveButton("Add", DialogInterface.OnClickListener { dialog, which ->
                // Here you get get input text from the Edittext
                var m_Text = input.text.toString()
            })
            .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which ->
                dialog.cancel() })
            .create().show()



//
//
//        val builder: AlertDialog.Builder = android.app.AlertDialog.Builder(binding.root.context, )
//        builder.setTitle("New List")
//
//
//
//        // Set up the input
//        val input = EditText(binding.root.context)
//        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
//        input.setHint("Enter List Name")
//        input.inputType = InputType.TYPE_CLASS_TEXT
//        builder.setView(input)
//
//        // Set up the buttons
//        builder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
//            // Here you get get input text from the Edittext
//            var m_Text = input.text.toString()
//        })
//        builder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which ->
//            dialog.cancel() })
//
//        builder.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}