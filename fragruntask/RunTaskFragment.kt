package com.improver.workable.fragruntask

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.improver.workable.Utils.Utils
import com.improver.workable.data.database.TaskDatabase
import com.improver.workable.data.repository.TaskRepository
import com.improver.workable.databinding.FragmentRunTaskBinding
import com.improver.workable.fragruntask.adapter.ChipAdapter
import com.improver.workable.fragruntask.adapter.ChipObject
import com.improver.workable.fragruntask.viewmodel.RunTaskViewModel
import com.improver.workable.fragruntask.viewmodel.RunTaskViewModelFactory
import com.google.android.material.chip.Chip
import kotlinx.coroutines.launch
import java.util.*

class RunTaskFragment : Fragment() {

    //get passed task name data from main recyclervier
    val args: RunTaskFragmentArgs by navArgs()
    private lateinit var adapter: ChipAdapter

    @SuppressLint("WrongConstant")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        var binding = FragmentRunTaskBinding.inflate(inflater, container, false)

        //find to set task
        var amount = args.TaskID
        //set up view model ----------------------

        val application = requireNotNull(this.activity).application

        val taskDao = TaskDatabase.getProjectDatabase(application).taskDao()

        val progressDao = TaskDatabase.getProjectDatabase(application).progressDao()

        val chipDao = TaskDatabase.getProjectDatabase(application).chipDao()

        val photoDao = TaskDatabase.getProjectDatabase(application).photoDao()

        val repository = TaskRepository(taskDao, progressDao, chipDao, photoDao)

        val viewModelFactory = RunTaskViewModelFactory(amount, repository, application)

        val viewmodel: RunTaskViewModel by viewModels { viewModelFactory }

        //----------------------------------------------

        //set backgroudn color
        binding.container.setBackgroundColor(ContextCompat.getColor(binding.container.context,
            Utils.setBackgroundColor(Utils.dateRangeDeadlineAlarmer(args.TaskDeadline))))
        //----------------------------------




        //set up adapter
        adapter = ChipAdapter()
        binding.chipsListRecyclerview.layoutManager =
            LinearLayoutManager(context, LinearLayout.VERTICAL, false)
        //---

        //set up this task data for view
        viewLifecycleOwner.lifecycleScope.launch {
            var task = viewmodel.getTaskByNAme()
            var chips = viewmodel.getThisChipList()

            binding.taskNameTextview.text = task.task_name
            binding.toNameTextview.text = task.task_to
            binding.deadline.text = Utils.makeDate(task.task_deadline)
            var chipObject = arrayListOf<ChipObject>()
            for (item in chips) {
                chipObject.add(ChipObject(item.chips, item.chip_check_state, item.chips_id))
            }
            adapter.setItem(chipObject)
            binding.chipsListRecyclerview.adapter = adapter
        }
        //-----------------------------------


        //manage start button on click --------------
        binding.startButton.setOnClickListener {

            //Get chooden time chip and task name for image saving
            var checkedChip = binding.timeSpanList.children
                .filter { (it as Chip).isChecked }
                .joinToString { (it as Chip).text }

            var taskName = binding.taskNameTextview.text.toString()
            //----------------------------------

            if (checkedChip.isEmpty()) {

                Toast.makeText(this.context,
                    "Please choose the time you'll work.",
                    Toast.LENGTH_SHORT).show()

            } else {

                if (binding.taskRunCardview.visibility == View.VISIBLE) {

                        var timeStamp = Utils.makeHour(Calendar.getInstance().timeInMillis)
                        viewmodel.timerStop()

                        var action =
                            RunTaskFragmentDirections.actionRunTaskFragmentToPostRunTaskFragment(
                                amount,
                                checkedChip,
                                taskName,
                                null,
                                false,
                                null,
                                timeStamp
                            )
                        view?.findNavController()?.navigate(action)

                } else if (binding.runTaskSetCardview.visibility == View.VISIBLE) {

                    binding.runTaskSetCardview.visibility = View.GONE
                    binding.taskRunCardview.visibility = View.VISIBLE
                    binding.chipsListCardview.visibility = View.VISIBLE
                    binding.startButton.text = "Record my progress"

                    var time = viewmodel.taskTimeSwitch(checkedChip)

                    viewmodel.timerString(binding.myTimer, time)

                }
            }


        }

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    // Handle the back button event
                    if (binding.taskRunCardview.visibility == View.GONE) {

                        var action =
                            RunTaskFragmentDirections.actionRunTaskFragmentToProgressFragment(args.TaskID)
                        view?.findNavController()?.navigate(action)


                    }
                }
            }

        requireActivity().getOnBackPressedDispatcher().addCallback(viewLifecycleOwner, callback)


        //------------------------------------------


        return binding.root
    }


}