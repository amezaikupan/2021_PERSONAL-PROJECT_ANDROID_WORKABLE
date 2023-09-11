package com.improver.workable.fragactmain

import android.annotation.SuppressLint

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.improver.workable.Utils.Utils
import com.improver.workable.data.database.TaskDatabase
import com.improver.workable.data.repository.TaskRepository
import com.improver.workable.fragactmain.adapter.TaskAdapter
import com.improver.workable.fragactmain.adapter.TaskObject
import com.improver.workable.fragactmain.viewmodel.ActMainViewModel
import com.improver.workable.fragactmain.viewmodel.ActMainViewModelFactory
import com.improver.workable.R
import com.improver.workable.databinding.FragmentActMainBinding
import java.util.*


class ActMainFragment : Fragment() {

    lateinit var taskAdapter: TaskAdapter

    @SuppressLint("WrongConstant")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        var binding = FragmentActMainBinding.inflate(inflater, container, false)


        //set up view model ----------------------

        val application = requireNotNull(this.activity).application

        val taskDao = TaskDatabase.getProjectDatabase(application).taskDao()

        val progressDao = TaskDatabase.getProjectDatabase(application).progressDao()

        val chipDao = TaskDatabase.getProjectDatabase(application).chipDao()

        val photoDao = TaskDatabase.getProjectDatabase(application).photoDao()

        val repository = TaskRepository(taskDao, progressDao, chipDao, photoDao)

        val viewModelFactory = ActMainViewModelFactory(repository, application)

        val viewmodel: ActMainViewModel by viewModels { viewModelFactory }

        //----------------------------------------------


        //Set up adapter and recycler for recycler view and populate it ------------
        taskAdapter = TaskAdapter(true)
        taskAdapter.setSetter(object : TaskAdapter.DisableViewSetter {
            override fun disableView() {
                binding.emptyNotify.visibility = View.VISIBLE
                binding.taskView.visibility = View.GONE
            }
        })

        binding.taskView.layoutManager = LinearLayoutManager(context, LinearLayout.VERTICAL, false)


        //set up viewmodel to recyclerview + todo: see how to use observe
        viewmodel.allOnLiveTask.observe(viewLifecycleOwner) { list ->

            if (!list.isEmpty()) {

                var taskObjectList = arrayListOf<TaskObject>()
                var taskExpiredCounter = arrayListOf<String>()

                for (item in list!!) {

                    if (Utils.dateRangeDeadlineAlarmer(item.task_deadline) == -1 && item.task_state == true) {
                        viewmodel.updateState(item.task_id)
                        item.task_state = false
                        taskExpiredCounter.add(item.task_name)

                    }

                    taskObjectList.add(
                        TaskObject(
                            item.task_name,
                            item.task_to,
                            item.task_deadline,
                            item.task_id,
                            item.task_state
                        )
                    )

                }
                if (!taskExpiredCounter.isEmpty()) {
                    var tasksEmptyDisplay: String = ""
                    taskExpiredCounter.forEach { tasksEmptyDisplay += " $it," }


                    binding.emptyNotify.text =
                        "Look like your tasks$tasksEmptyDisplay of your tasks expired. \nTry harder next time!"
                    binding.emptyNotify.visibility = View.VISIBLE
                }
                Collections.reverse(taskObjectList)

                taskAdapter.setItem(taskObjectList)

                binding.taskView.adapter = taskAdapter
                // finished setting up adapter and populate with dummy data
                //---------------------------------------------------------

            } else {
                binding.taskView.visibility = View.GONE
                binding.emptyNotify.visibility = View.VISIBLE

            }
        }

        //Set onlick FAB to create new task --------------------------------
        binding.addFab.setOnClickListener {
            if (binding.taskView.childCount <=4){
                view?.findNavController()?.navigate(R.id.action_actMainFragment_to_newTaskFragment)
            }else{
                Toast.makeText(this.context,
                    "You're have exceeded your tasks limit. \nFocus on finish your current tasks!",
                    Toast.LENGTH_SHORT).show()            }

        }
        //------------------------------------------------------------------

        //set onclik to show user data
        binding.settingButton.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_actMainFragment_to_userDataFragment)
        }
        //----------------------------------------

        binding.goodday.text = Utils.goodDay()

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    activity?.finishAndRemoveTask()
                }
            }

        requireActivity().getOnBackPressedDispatcher().addCallback(viewLifecycleOwner, callback)


        return binding.root
    }




}