package com.gcr.selectordatos

import android.app.Instrumentation
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.gcr.selectordatos.databinding.ActivityMainBinding
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {

    private val REQUEST_IMAGE_CAPTURE = 1
    private val OPERATION_CHOOSE_PHOTO = 2
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    fun SelectImageOnClick(view: View) {
        Toast.makeText(this,"Selected image",Toast.LENGTH_SHORT).show()
        dispatchTakePictureIntent()
    }

    fun OpenGalleryOnClick(view: View) {
        openGallery()
        //requestPermission()
    }

    //Código que pide permisos en tiempo de ejecución
    //https://www.youtube.com/watch?v=EiQn3zVlPtQ
    private fun requestPermission(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            when{
                ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED ->{
                        openGallery()
                }
                else-> requestPermissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }else{
            openGallery()
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){
        isGranted->
        run {
            if (isGranted) {
                openGallery()
            } else {
                Toast.makeText(this, "", Toast.LENGTH_SHORT).show()
            }
        }
    }

    //Este metodo solo abre la galeria
    private fun openGallery(){
        val intent = Intent("android.intent.action.GET_CONTENT")
        intent.type = "image/*"
        startActivityForResult(intent, OPERATION_CHOOSE_PHOTO)
    }

    // abre la camara para tomar una foto
    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    //Recupera la foto y se muestra en la vista
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            REQUEST_IMAGE_CAPTURE->
                if (resultCode == RESULT_OK) {
                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    binding.imgSelected.visibility = View.VISIBLE
                    binding.imgSelected.setImageBitmap(imageBitmap)
                }
            OPERATION_CHOOSE_PHOTO->{
                if (resultCode == RESULT_OK) {
                    //val imageBitmap = data?.extras?.get("data") as Bitmap
                    val dataUri = data?.data
                    binding.imgSelected.visibility = View.VISIBLE
                    binding.imgSelected.setImageURI(dataUri)
                }
            }
        }

    }
}