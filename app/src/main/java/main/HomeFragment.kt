package main

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import com.example.financialassistant.databinding.FragmentHomeBinding
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import utils.Common.toastShort

class HomeFragment : Fragment(), EasyPermissions.PermissionCallbacks {

    private lateinit var binding: FragmentHomeBinding

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