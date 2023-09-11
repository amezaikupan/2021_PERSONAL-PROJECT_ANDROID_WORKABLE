package com.improver.workable.fragpostrun

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
import com.improver.workable.R
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.improver.workable.Utils.Utils
import com.improver.workable.data.database.TaskDatabase
import com.improver.workable.data.entity.Progress
import com.improver.workable.data.repository.TaskRepository
import com.improver.workable.fragpostrun.adapter.PhotoAdapter
import com.improver.workable.fragpostrun.adapter.PhotoObject
import com.improver.workable.fragpostrun.viewmodel.PostRunTaskViewModel
import com.improver.workable.fragpostrun.viewmodel.PostRunTaskViewModelFactory
import com.improver.workable.fragruntask.adapter.ChipAdapter
import com.improver.workable.fragruntask.adapter.ChipObject
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.improver.workable.databinding.FragmentPostRunTaskBinding
import kotlinx.coroutines.launch
import java.io.File
import java.util.*

class PostRunTaskFragment : Fragment() {

    private lateinit var binding: FragmentPostRunTaskBinding

    val args: PostRunTaskFragmentArgs by navArgs()

    private lateinit var chipAdapter: ChipAdapter

    private lateinit var photoAdapter: PhotoAdapter


    private var mInterstitialAd: InterstitialAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MobileAds.initialize(this.activity)


    }


    @SuppressLint("WrongConstant")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPostRunTaskBinding.inflate(inflater, container, false)

        val amountID = args.TaskID
        val amountPhoto = args.PhotoUriList
        val amoutTime = args.TaskTimeLength
        val amoutTaskName = args.TaskName
        val amountcheckstate = args.ChipsIsChecked
        val amountTimeStamp = args.TimeStamp
//set up view model ----------------------

        val application = requireNotNull(this.activity).application

        val taskDao = TaskDatabase.getProjectDatabase(application).taskDao()

        val progressDao = TaskDatabase.getProjectDatabase(application).progressDao()

        val chipDao = TaskDatabase.getProjectDatabase(application).chipDao()

        val photoDao = TaskDatabase.getProjectDatabase(application).photoDao()

        val repository = TaskRepository(taskDao, progressDao, chipDao, photoDao)

        val viewModelFactory = PostRunTaskViewModelFactory(amountID, repository, application)

        val viewmodel: PostRunTaskViewModel by viewModels { viewModelFactory }

        //----------------------------------------------

        loadAds()

        //borrow adapter from run task fragmenet to display chips
        chipAdapter = ChipAdapter()
        binding.chipsListRecyclerview.layoutManager =
            LinearLayoutManager(context, LinearLayout.VERTICAL, false)
        //----------------------------------------------------



//set state after camera fragemtn and record eddittext
        if (amountcheckstate == true) {
            binding.completeChipsConstrainlayout.visibility = View.GONE
            binding.progressRecordConstrainlayout.visibility = View.VISIBLE
        }
        if (args.ProgressNote != null) {
            binding.progressRecordEdittext.setText(args.ProgressNote)
        }
//-----------------------------------------

        //get up time length
        var date = Utils.makeDate(Calendar.getInstance().timeInMillis)


        binding.progressDateTextview.text = "$date - $amoutTime"
        //------------------------------

        //get data from viewmodel to
        viewLifecycleOwner.lifecycleScope.launch {
            val chips = viewmodel.getThisChipList()
            var chipObject = arrayListOf<ChipObject>()
            for (item in chips) {
                chipObject.add(ChipObject(item.chips, item.chip_check_state, item.chips_id))
            }
            chipAdapter.setItem(chipObject)
            binding.chipsListRecyclerview.adapter = chipAdapter
        }
        //-------------------------------------------

        var progress = Progress()
        //get next button to change view
        binding.setProgressButton.setOnClickListener {
            if (binding.completeChipsConstrainlayout.visibility == View.VISIBLE) {
                binding.completeChipsConstrainlayout.visibility = View.GONE
                binding.progressRecordConstrainlayout.visibility = View.VISIBLE
                var updateList = chipAdapter.stateList
                for (item in updateList) {
                    viewmodel.updateChips(item) 
                }

                viewmodel.insertProgress(progress)

            } else if (binding.progressRecordConstrainlayout.visibility == View.VISIBLE) {
                if (binding.takePhotoImagebutton.visibility == View.GONE){
                showInterstitial()
            }else {


                    if (amountPhoto != null) {
                        for (item in amountPhoto.toList()) {
                            viewmodel.insertPhoto(item)
                        }
                    }
                    val taskID = amountID.toLong()

                    var progress_note =
                        "[$amountTimeStamp - $amoutTime]  - " + binding.progressRecordEdittext.text.toString()
                    var progress_ticker = amountTimeStamp

                    viewmodel.updateProgress(taskID, progress_note, progress_ticker)


                    binding.noteTexted.visibility = View.VISIBLE
                    binding.noteTexted.text = progress_note
                    binding.progressRecordEdittext.visibility = View.GONE
                    binding.takePhotoImagebutton.visibility = View.GONE
                    binding.setProgressButton.text = "Progress :)"
                    binding.getVisualTittleTextview.text = "Photos: "

                }
            }
        }
        //-----------------------------

        //navigate to photo taking frag
        binding.takePhotoImagebutton.setOnClickListener {
            if (amountPhoto != null) {
                for (item in amountPhoto) {
                    val file = File(item.toUri().getPath())
                    file.delete()
                    if (file.exists()) {
                        file.getCanonicalFile().delete()
                        if (file.exists()) {
                            activity?.applicationContext?.deleteFile(
                                file.name)
                        }
                    }
                }
            }

            val action = PostRunTaskFragmentDirections.actionPostRunTaskFragmentToCameraFragment(
                amoutTaskName,
                amountID,
                amoutTime, true, binding.progressRecordEdittext.text.toString()
            , amountTimeStamp)
            view?.findNavController()?.navigate(action)

        }

        //-------------------------------------

        //update photo viewpager
        photoAdapter = PhotoAdapter()
        photoAdapter.setListener(object :PhotoAdapter.OnPhotoEnlargeListener{
            override fun onPhotoEnlargeListener(photo: String) {
                //Do nothing
            }
        })

        var photoList = args.PhotoUriList
        var photoObjectList = arrayListOf<PhotoObject>()
        if (photoList != null) {
            for (item in photoList.toList()) {
                photoObjectList.add(PhotoObject(item))
            }

            photoAdapter.setItem(photoObjectList)
            binding.imageHolderViewpager.layoutManager =
                LinearLayoutManager(context, LinearLayout.HORIZONTAL, false)

            PagerSnapHelper().attachToRecyclerView(binding.imageHolderViewpager)
            binding.imageHolderViewpager.adapter = photoAdapter

        }
        //---------------------------------------------

        //set reaction to change task state
        binding.finishTaskButton.setOnClickListener {
            showFinishTaskQ(viewmodel)
        }
        //-----------------------------------------

        //set finish task naviagation
        binding.finishTask.setOnClickListener {

            view?.findNavController()
                ?.navigate(R.id.action_postRunTaskFragment_to_actMainFragment)

        }
        //----------------------------------------


        //set custom back navigaiton
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    // Handle the back button event
                    if (binding.progressRecordConstrainlayout.visibility == View.VISIBLE) {
                        binding.completeChipsConstrainlayout.visibility = View.VISIBLE
                        binding.progressRecordConstrainlayout.visibility = View.GONE
                    }
                }
            }

        requireActivity().getOnBackPressedDispatcher().addCallback(viewLifecycleOwner, callback)



        return binding.root
    }

    fun showFinishTaskQ(viewModel: PostRunTaskViewModel) {

        context?.let {
            MaterialAlertDialogBuilder(it)
                .setTitle("Are you done?")
                .setPositiveButton("Yup") { _, _ ->
                    viewLifecycleOwner.lifecycleScope.launchWhenCreated {
                        viewModel.updateState()
                        viewModel.cancelNotification()
                    }
                    var resId = getResources().getIdentifier(R.raw.task_congrats.toString(),
                        "raw", activity?.packageName)
                    var mediaPlayer: MediaPlayer = MediaPlayer.create(this.context, resId)
                    mediaPlayer.start()

                    binding.setProgressButton.visibility = View.GONE
                    binding.finishTask.visibility = View.VISIBLE
                    binding.finishTaskButton.visibility = View.GONE
                    binding.finnishTaskSuggestTextview.text = "Task is completed. Yay!"
                    Utils.finishTaskConsgrats(binding.viewKonfetti)
                }
                .setNegativeButton("Nah") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }

    private fun loadAds(){

        var adRequest = AdRequest.Builder()
            .build()

        InterstitialAd.load(this.context,
            "ca-app-pub-9291564688146531/4639798338",
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    mInterstitialAd = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    mInterstitialAd = interstitialAd

                }

            }
        )        //---------------------------------------------------
    }

    private fun showInterstitial() {
        if (mInterstitialAd != null) {
            mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    // Don't forget to set the ad reference to null so you
                    // don't show the ad a second time.
                    mInterstitialAd = null
                    loadAds()
                    var action = PostRunTaskFragmentDirections.actionPostRunTaskFragmentToProgressFragment(args.TaskID)
                    view?.findNavController()
                        ?.navigate(action)

                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
                    // Don't forget to set the ad reference to null so you
                    // don't show the ad a second time.
                    mInterstitialAd = null

                }

                override fun onAdShowedFullScreenContent() {}
                    // Called when ad is dismissed.
            }
            mInterstitialAd?.show(this.activity)
        } else {
            var action = PostRunTaskFragmentDirections.actionPostRunTaskFragmentToProgressFragment(args.TaskID)
            view?.findNavController()
                ?.navigate(action)
        }
    }

}