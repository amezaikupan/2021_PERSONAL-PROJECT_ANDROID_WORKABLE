package com.improver.workable.fragtaskprogres

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.activity.OnBackPressedCallback
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.improver.workable.R
import com.improver.workable.Utils.Utils
import com.improver.workable.data.database.TaskDatabase
import com.improver.workable.data.repository.TaskRepository
import com.improver.workable.databinding.FragmentProgressBinding
import com.improver.workable.fragactmain.adapter.TaskObject
import com.improver.workable.fragpostrun.adapter.PhotoAdapter
import com.improver.workable.fragpostrun.adapter.PhotoObject
import com.improver.workable.fragruntask.adapter.ChipObject
import com.improver.workable.fragtaskprogres.adapter.ProgressAdapter
import com.improver.workable.fragtaskprogres.adapter.ProgressObject
import com.improver.workable.fragtaskprogres.adapter.progressheader.ProgressHeaderAdapter
import com.improver.workable.fragtaskprogres.viewmodel.ProgressViewModel
import com.improver.workable.fragtaskprogres.viewmodel.ProgressViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import java.util.*


class ProgressFragment : Fragment() {

    val args: ProgressFragmentArgs by navArgs()

    private lateinit var progressAdapter: ProgressAdapter
    private lateinit var photoAdapter: PhotoAdapter
    private lateinit var progressHeaderAdapter: ProgressHeaderAdapter

    lateinit var binding: FragmentProgressBinding

    @SuppressLint("WrongConstant")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentProgressBinding.inflate(inflater, container, false)

        //get progress - task id
        val amountID = args.TaskID
        //----------------------------------------

        //set up view model ----------------------

        val application = requireNotNull(this.activity).application

        val taskDao = TaskDatabase.getProjectDatabase(application).taskDao()

        val progressDao = TaskDatabase.getProjectDatabase(application).progressDao()

        val chipDao = TaskDatabase.getProjectDatabase(application).chipDao()

        val photoDao = TaskDatabase.getProjectDatabase(application).photoDao()

        val repository = TaskRepository(taskDao, progressDao, chipDao, photoDao)

        val viewModelFactory = ProgressViewModelFactory(amountID, repository, application)

        val viewmodel: ProgressViewModel by viewModels { viewModelFactory }

        //----------------------------------------------

        //declare chip group

        progressAdapter = ProgressAdapter()
        photoAdapter = PhotoAdapter()
        progressHeaderAdapter = ProgressHeaderAdapter()


        progressAdapter.setPhotoListener(object : PhotoAdapter.OnPhotoEnlargeListener {
            override fun onPhotoEnlargeListener(photo: String) {
                binding.expandePhotoBackground.visibility = View.VISIBLE

                binding.expandedImage.setImageURI(photo.toUri())
            }
        })
        binding.progressRecView.layoutManager =
            LinearLayoutManager(context, LinearLayout.VERTICAL, false)

        //-------------------------------------------

        viewLifecycleOwner.lifecycleScope.launch {

            var progressList = arrayListOf<ProgressObject>()

            val allTaskProgress = viewmodel.getThisTaskProgress()
            val thisTask = allTaskProgress.task
            val thisProgressList = allTaskProgress.progress
            var progressIsEmpty = false

            //show chip list
            var chipList = viewmodel.getThisTaskChips()
            var chipString = arrayListOf<ChipObject>()

            chipList.forEach {
                chipString.add(ChipObject(it.chips, it.chip_check_state, it.chips_id))
            }
//---------------------------

//Implementing progress List
            if (thisProgressList.isEmpty()) {
                progressIsEmpty = true
            } else {
                progressIsEmpty = false
            }

            for (item in thisProgressList) {
                    var progressAndPhoto = viewmodel.getThisProgressPhoto(item.progress_id).photos
                    var photoList = arrayListOf<PhotoObject>()

                    for (photoItem in progressAndPhoto) {
                        photoList.add(PhotoObject(photoItem.photo_uri))
                    }
                    progressList.add(ProgressObject(item.progress_note,
                        Utils.makeDate(item.progress_date),
                        photoList))
            }

            Collections.reverse(progressList)
            progressAdapter.setItem(progressList)
//----------------------------------

//Set up header adapter
            var thisTaskObject = arrayListOf<TaskObject>(TaskObject(
                thisTask.task_name,
                thisTask.task_to,
                thisTask.task_deadline,
                thisTask.task_id,
                thisTask.task_state,
                progressIsEmpty
            ))

            progressHeaderAdapter.setItem(thisTaskObject)
            //-------------------

            val concatAdapter = ConcatAdapter(progressHeaderAdapter, progressAdapter)

            progressHeaderAdapter.setListener(object : ProgressHeaderAdapter.OnChipItemCick {

                override fun onChipDialog() {
                    if (thisTask.task_state == true) {
                        showChipList("Back to work!", chipString)
                    } else {
                        showChipList("Done", chipString)
                    }
                }

                override fun onFinishTask(inListener: ProgressHeaderAdapter.OnInResponse) {
                    showFinishTaskQ(inListener, viewmodel)
                }


            })
//----------------
            //Set concat adapter
            binding.progressRecView.adapter = concatAdapter


            //Set expanded photo to be invisible? Necesarry?
            binding.expandedImage.setOnClickListener {
                binding.expandePhotoBackground.visibility = View.GONE

            }

            //Set main navigator if task is finished
            binding.finishTask.setOnClickListener {
                view?.findNavController()
                    ?.navigate(R.id.action_progressFragment_to_actMainFragment)
            }
            //---------------------------------
            //set custom back navigaiton
            val callback: OnBackPressedCallback =
                object : OnBackPressedCallback(true /* enabled by default */) {
                    override fun handleOnBackPressed() {
                        // Handle the back button event
                        if (binding.expandePhotoBackground.visibility == View.VISIBLE) {
                            binding.expandePhotoBackground.visibility = View.GONE

                        }
                        else {
                            if (thisTask.task_state ==true) {
                                view?.findNavController()
                                    ?.navigate(R.id.action_progressFragment_to_actMainFragment)
                            }else{
                                view?.findNavController()
                                    ?.navigate(R.id.action_progressFragment_to_userDataFragment)
                            }
                        }

                    }
                }

            requireActivity().getOnBackPressedDispatcher().addCallback(viewLifecycleOwner, callback)
        }

        return binding.root
    }

    //navigate to run task fragment
    private fun showChipList(buttonMessage: String, chipList: List<ChipObject>) {
        var chipString = arrayListOf<String>()
        for (item in chipList) {
            if (item.chipState == true) {
                chipString.add("😁 - " + item.chipString)
            } else {
                chipString.add("❌ - " + item.chipString)
            }
        }
        context?.let {
            MaterialAlertDialogBuilder(it)
                .setTitle("My Chips")
                .setPositiveButton(buttonMessage) { dialog, _ ->
                    // Respond to positive button press
                    dialog.dismiss()
                }
                // Single-choice items (initialized with checked item)
                .setItems(chipString.toTypedArray()) { _, _ ->
                    // Respond to item chosen
                }

                .show()
        }
    }

    private fun showFinishTaskQ(listener: ProgressHeaderAdapter.OnInResponse, viewModel: ProgressViewModel){
        context?.let {
            MaterialAlertDialogBuilder(it)
                .setTitle("Are you done?")
                .setPositiveButton("Yup") { _, _ ->
                    viewLifecycleOwner.lifecycleScope.launchWhenCreated {
                        viewModel.updateState()
                        viewModel.cancelNotification()
                    }
                    Utils.finishTaskConsgrats(binding.viewKonfetti)
                    binding.finishTask.visibility = View.VISIBLE

                    listener.finishedTask()
                    var resId = getResources().getIdentifier(R.raw.task_congrats.toString(),
                        "raw", activity?.packageName)
                    var mediaPlayer: MediaPlayer = MediaPlayer.create(this.context, resId)
                    mediaPlayer.start()
                }
                .setNegativeButton("Nah") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }
}