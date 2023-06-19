package com.housemanagement.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.loader.content.CursorLoader
import com.housemanagement.controllers.ComplaintsFragmentController
import com.housemanagement.databinding.FragmentComplaintsBinding


class ComplaintsFragment : Fragment() {
    private var mParam1: String? = null
    private var mParam2: String? = null
    lateinit var binding: FragmentComplaintsBinding
    private lateinit var complaintsFragmentController: ComplaintsFragmentController
    lateinit var selectedImage: Uri
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = requireArguments().getString(ARG_PARAM1)
            mParam2 = requireArguments().getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentComplaintsBinding.inflate(inflater, container, false)
        complaintsFragmentController = ComplaintsFragmentController(binding, this, viewLifecycleOwner)
        return binding.root
    }

    companion object {
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"
        fun newInstance(param1: String?, param2: String?): ComplaintsFragment {
            val fragment = ComplaintsFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity?)!!.supportActionBar?.title = "ЗАЯВКИ"
        binding.btnPhotoComplaint.setOnClickListener(View.OnClickListener {
            loadPhoto()
        })
    }

    private fun loadPhoto() {
        val PICK_IMAGE = 1

        val intent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == AppCompatActivity.RESULT_OK && data != null) {
            selectedImage = data.data!!
            binding.photoComplaint.visibility = View.VISIBLE
            binding.photoComplaint.setImageURI(selectedImage)
            complaintsFragmentController.getPhoto(getRealPathFromURI(selectedImage))
        }
    }

    private fun getRealPathFromURI(selectedImage: Uri): String {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val loader = CursorLoader(binding.root.context, selectedImage, proj, null, null, null)
        val cursor = loader.loadInBackground()
        val columnIndex = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor?.moveToFirst()
        val result = columnIndex?.let { cursor.getString(it) }
        if(result != null)
            return  result
        else return  ""
    }
}
