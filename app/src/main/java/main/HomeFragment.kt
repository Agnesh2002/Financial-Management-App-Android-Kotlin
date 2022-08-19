package main

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.financialassistant.databinding.FragmentHomeBinding
import kotlinx.coroutines.flow.collectLatest
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import utils.Common.toastShort
import java.util.*

class HomeFragment : Fragment(), EasyPermissions.PermissionCallbacks {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: HomeViewModel

    @SuppressLint("Range")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val task = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data = result.data?.data
                data?.let {
                    val cursor = activity?.contentResolver?.query(data, null, null, null, null)
                    cursor?.let {
                        if (it.moveToFirst()) {
                            val name = it.getString(it.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME))
                            if (Integer.parseInt(it.getString(it.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                                // Check if the contact has phone numbers
                                val id = it.getString(it.getColumnIndex(ContactsContract.Contacts._ID))
                                val phonesCursor = activity?.contentResolver?.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id, null, null)

                                val numbers = mutableSetOf<String>()
                                phonesCursor?.let {
                                    while (phonesCursor.moveToNext()) {
                                        val phoneNumber = phonesCursor.getString(phonesCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replace("-", "").replace(" ", "")
                                        numbers.add(phoneNumber)
                                    }
                                    //var contactNumber = numbers.toString()
                                    //contactNumber = contactNumber.replace("[","").replace("]","").trim()
                                    //val contactValue = "$name-$contactNumber"
                                    //toastShort(requireContext(), "$contactValue")

                                    binding.etPayee.setText(name)
                                    binding.etPayee.setSelection(binding.etPayee.length())
                                }

                                phonesCursor?.close()

                            } else {
                                toastShort(requireContext(), "$name - No numbers")
                            }
                        }

                        cursor.close()
                    }

                }
            }
        }

        binding = FragmentHomeBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        binding.lViewModel = viewModel
        binding.lifecycleOwner = this

        viewModel.loadData()

        binding.imgContacts.setOnClickListener {

            if (!hasContactPermission()) {
                requestContactPermission()
            }
            else
            {
                val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
                task.launch(intent)
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.stateFlow.collectLatest {
                when (it)
                {
                    1 -> { binding.etAmount.error = viewModel.errMsg }
                    2 -> { toastShort(requireContext(), viewModel.errMsg) }
                    3 -> { viewModel.makeExpense(binding.paymentModeSpinner.selectedItem.toString()) }
                }
            }
        }

        binding.tvSelectDate.setOnClickListener {
            DatePickerDialog(requireActivity(),
                viewModel.dateSetListener,
                viewModel.cal.get(Calendar.YEAR), viewModel.cal.get(Calendar.MONTH), viewModel.cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }


        return binding.root
    }

    private fun hasContactPermission() = EasyPermissions.hasPermissions(requireContext(), Manifest.permission.READ_CONTACTS)

    private fun requestContactPermission()
    {
        EasyPermissions.requestPermissions(this, "Contact permission is necessary to use this feature", 1, Manifest.permission.READ_CONTACTS)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        toastShort(requireContext(), "Permission granted")
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if(EasyPermissions.permissionPermanentlyDenied(this, perms.first()))
            AppSettingsDialog.Builder(requireActivity()).build().show()
        else
            requestContactPermission()
    }

}