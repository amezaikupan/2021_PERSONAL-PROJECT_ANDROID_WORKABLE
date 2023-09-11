package com.improver.workable.fragcamera

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.improver.workable.R
import com.improver.workable.databinding.FragmentCameraBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class CameraFragment : Fragment() {

    private var imageCapture: ImageCapture? = null

    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var binding: FragmentCameraBinding
    private var photoCounter: Int = 0

    val args: CameraFragmentArgs by navArgs()

    private var photoList = arrayListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        binding = FragmentCameraBinding.inflate(inflater, container, false)



        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissions(arrayOf<String>(android.Manifest.permission.CAMERA), REQUEST_CODE_PERMISSIONS)
        }
        //--------------------------------------

        var imageCounter = 0
        // Set up the listener for take photo button
        binding.cameraCaptureButton.setOnClickListener {
            imageCounter++
            takePhoto()
        }

        outputDirectory = getOutputDirectory()

        cameraExecutor = Executors.newSingleThreadExecutor()

        binding.progressPhotoFinishedButton.setOnClickListener {
            var action =
                CameraFragmentDirections.actionCameraFragmentToPostRunTaskFragment(args.TaskID,
                    args.TaskTimeLength,
                    args.TaskName,
                    photoList.toTypedArray(),
                    true,
                    args.ProgressNote,
                    args.TimeStamps
                )
            view?.findNavController()?.navigate(action)
        }

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    // Handle the back button event
                    var action =
                        CameraFragmentDirections.actionCameraFragmentToPostRunTaskFragment(args.TaskID,
                            args.TaskTimeLength,
                            args.TaskName,
                            photoList.toTypedArray(),
                            true,
                            args.ProgressNote,
                        args.TimeStamps)
                    view?.findNavController()?.navigate(action)

                }
            }

        requireActivity().getOnBackPressedDispatcher().addCallback(viewLifecycleOwner, callback)


        return binding.root
    }


    //get system permition
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()

            } else {
                Toast.makeText(context,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }
    //-------------------------

    //Response to granted permission code
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        activity?.let { it1 ->
            ContextCompat.checkSelfPermission(
                it1.baseContext, it)
        } == PackageManager.PERMISSION_GRANTED
    }
    //----------------------------------

    //start camera
    private fun startCamera() {
        val cameraProviderFuture = context?.let { ProcessCameraProvider.getInstance(it) }

        if (cameraProviderFuture != null) {
            cameraProviderFuture.addListener(Runnable {
                // Used to bind the lifecycle of cameras to the lifecycle owner
                val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

                binding.viewFinder.keepScreenOn = true

                // Preview
                val preview = Preview.Builder()
                    .setTargetResolution(Size(1280, 720))
                    .build()
                    .also {
                        it.setSurfaceProvider(binding.viewFinder.createSurfaceProvider())
                    }


                imageCapture = ImageCapture.Builder()
                    .build()

                // Select back camera as a default
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                try {
                    // Unbind use cases before rebinding
                    cameraProvider.unbindAll()

                    // Bind use cases to camera
                    cameraProvider.bindToLifecycle(
                        this, cameraSelector, preview, imageCapture)

                } catch (exc: Exception) {}

            }, ContextCompat.getMainExecutor(context))
        }
    }

    //-------------------------------

    //take photo
    private fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        //get task name for photo saving
        var taskName = args.TaskName

        //get photo counter

        taskName = taskName.replace("\\s".toRegex(), "")

        // Create time-stamped output file to hold the image
        val photoFile = File(
            outputDirectory,
            taskName + SimpleDateFormat(FILENAME_FORMAT, Locale.US
            ).format(System.currentTimeMillis()) + ".jpg")

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Toast.makeText(activity?.baseContext, "Photo capture failed", Toast.LENGTH_SHORT).show()

                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    photoCounter++
                    val savedUri = Uri.fromFile(photoFile)
                    val msg = "$photoCounter Photo(s) taken!"
                    photoList.add(savedUri.toString())
                    Toast.makeText(activity?.baseContext, msg, Toast.LENGTH_SHORT).show()
                }
            })


    }

    //----------------------------------


    // get output directory of file
    private fun getOutputDirectory(): File {
        val mediaDir =
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                activity?.externalMediaDirs?.firstOrNull()?.let {
                    File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
                }
            } else {
                TODO("VERSION.SDK_INT < LOLLIPOP")
            }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else requireActivity().filesDir
    }
//-----------------------------------


    //on destroy fragment view
    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
//--------------------------------------





    companion object {

        private const val TAG = "CameraFragment"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
}