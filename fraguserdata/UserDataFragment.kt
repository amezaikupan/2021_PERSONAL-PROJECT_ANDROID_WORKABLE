package com.improver.workable.fraguserdata

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.improver.workable.R
import com.improver.workable.data.database.TaskDatabase
import com.improver.workable.data.repository.TaskRepository
import com.improver.workable.databinding.FragmentUserDataBinding
import com.improver.workable.fragactmain.adapter.TaskAdapter
import com.improver.workable.fragactmain.adapter.TaskObject
import com.improver.workable.fraguserdata.viewmodel.UserDataViewModel
import com.improver.workable.fraguserdata.viewmodel.UserDataViewModelFactory
import kotlinx.coroutines.launch
import java.io.File


class UserDataFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    private lateinit var taskAdapter: TaskAdapter

    @SuppressLint("WrongConstant")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        var binding = FragmentUserDataBinding.inflate(inflater, container, false)

        //set up view model ----------------------

        val application = requireNotNull(this.activity).application

        val taskDao = TaskDatabase.getProjectDatabase(application).taskDao()

        val progressDao = TaskDatabase.getProjectDatabase(application).progressDao()

        val chipDao = TaskDatabase.getProjectDatabase(application).chipDao()

        val photoDao = TaskDatabase.getProjectDatabase(application).photoDao()

        val repository = TaskRepository(taskDao, progressDao, chipDao, photoDao)

        val viewModelFactory = UserDataViewModelFactory(repository, application)

        val viewmodel: UserDataViewModel by viewModels { viewModelFactory }

        //----------------------------------------------

        taskAdapter = TaskAdapter(false)
        binding.taskRecyclerview.layoutManager = LinearLayoutManager(this.context,
            LinearLayout.VERTICAL,
            false)


        binding.myTasksCollection.setOnClickListener {
            binding.userInfoTop.visibility = View.GONE
            binding.taskAllList.visibility = View.VISIBLE
            var taskObjectList = arrayListOf<TaskObject>()

            viewmodel.allTask.observe(viewLifecycleOwner) {
                //Todo: This is th problem. It clear the list onlick but doensn't delete actual data and cause crashes. Redo pls
                taskObjectList.clear()
                if (!it.isEmpty()) {
                    for (item in it!!) {

                        taskObjectList.add(
                            TaskObject(
                                item.task_name,
                                item.task_to,
                                item.task_deadline,

                                item.task_id,
                                item.task_state
                            )
                        )

                        taskAdapter.setItem(taskObjectList)
                        binding.taskRecyclerview.adapter = taskAdapter
                        taskAdapter.setSetter(object : TaskAdapter.DisableViewSetter {
                            override fun disableView() {
                                binding.emptyNotify.visibility = View.VISIBLE
                                binding.taskRecyclerview.visibility = View.GONE
                            }

                            override fun deleteTask(
                                id: Long,
                                deleteResponse: TaskAdapter.DeleteResponse,
                            ) {
                                showDeleteTaskQ(viewmodel, id, deleteResponse)

                            }
                        })


                    }
                }
            }
        }

        binding.aboutTask.setOnClickListener {
            binding.userInfoTop.visibility = View.GONE
            binding.about.visibility = View.VISIBLE
        }

        binding.privacyPolicy.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://medium.com/@workable.project2021/workable-make-progress-happens-apps-privacy-policy-445e0ab85d14"))
            startActivity(browserIntent)
        }
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    // Handle the back button event
                    if (binding.userInfoTop.visibility == View.VISIBLE) {
                        view?.findNavController()
                            ?.navigate(R.id.action_userDataFragment_to_actMainFragment)
                    } else {
                        binding.userInfoTop.visibility = View.VISIBLE

                        if (binding.about.visibility == View.VISIBLE) {
                            binding.about.visibility = View.GONE
                        } else {
                            binding.taskAllList.visibility = View.GONE

                        }
                    }
                }
            }

        requireActivity().getOnBackPressedDispatcher().addCallback(viewLifecycleOwner, callback)

        return binding.root
    }

    fun showDeleteTaskQ(
        viewModel: UserDataViewModel,
        id: Long,
        deleteResponse: TaskAdapter.DeleteResponse,
    ) {

        context?.let {
            MaterialAlertDialogBuilder(it)
                .setTitle("Are sure you want to delete this task?")
                .setMessage("This task will be permanently deleted from your data. And, yeah, you probably know about it already. Do what you must. :)")
                .setPositiveButton("Yeah") { _, _ ->
                    viewLifecycleOwner.lifecycleScope.launchWhenCreated {
                        viewModel.deleteTask(id)
                        viewModel.deleteTaskChips(id)
                        viewLifecycleOwner.lifecycleScope.launch {
                            var progress = viewModel.getThisTaskProgress(id)
                            if (!progress.progress.isEmpty()) {
                                for (item in progress.progress) {
                                    var photo =
                                        viewModel.getThisProgressPhoto(item.progress_id)
                                    if (!photo.isEmpty()) {
                                        for (photoItem in photo) {
                                            val file = File(photoItem.photo_uri.toUri().getPath())
                                            file.delete()
                                            if (file.exists()) {
                                                file.getCanonicalFile().delete()
                                                if (file.exists()) {
                                                    activity?.applicationContext?.deleteFile(
                                                        file.name)
                                                }
                                            }
                                            viewModel.deletePhoto(photoItem)

                                        }
                                    }

                                }
                            }
                        }
                        viewModel.deleteTaskProgress(id)


                    }
                    deleteResponse.deleteResponse()

                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }
}