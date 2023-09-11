package com.improver.workable.fragnewtask

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.improver.workable.Utils.Utils
import com.improver.workable.data.database.TaskDatabase
import com.improver.workable.data.entity.Task
import com.improver.workable.data.repository.TaskRepository
import com.improver.workable.fragnewtask.viewmodel.NewTaskViewModel
import com.improver.workable.fragnewtask.viewmodel.factory.NewTaskViewModelFactory
import com.improver.workable.R
import com.improver.workable.databinding.FragmentNewTaskBinding
import java.util.*


class NewTaskFragment : Fragment() {

    companion object {
        private val miniFont = 14

    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        var binding = FragmentNewTaskBinding.inflate(inflater, container, false)

        //set up view model ----------------------

        val application = requireNotNull(this.activity).application

        val taskDao = TaskDatabase.getProjectDatabase(application).taskDao()

        val progressDao = TaskDatabase.getProjectDatabase(application).progressDao()

        val chipDao = TaskDatabase.getProjectDatabase(application).chipDao()

        val photoDao = TaskDatabase.getProjectDatabase(application).photoDao()

        val repository = TaskRepository(taskDao, progressDao, chipDao, photoDao)

        val viewModelFactory = NewTaskViewModelFactory(repository, application)

        val viewmodel: NewTaskViewModel by viewModels { viewModelFactory }

        //----------------------------------------------


        var chipList = arrayListOf<String>()

        //add chip button onclick
        binding.chipsViewer.setOnClickListener {
            chipList.add(binding.chipSetEdittext.text.toString())
            binding.chipSetEdittext.text.clear()
            var adapter = ArrayAdapter(binding.chipsViewer.context,
                android.R.layout.simple_list_item_1,
                chipList)
            binding.chipList.adapter = adapter
        }
        //------------------------------


        //manage the next button -> flow to add new task.
        binding.nextButton.setOnClickListener {

            var task = Task()

            if (binding.newProjectBasicInfoAddCardview.isVisible && binding.tasksCardview.isVisible) {
                //see if the user has filled in atleast 1 chips + observer a change to new view + update new task.

                for (item in chipList) {
                    viewmodel.insertChip(item)
                }
                view?.findNavController()?.navigate(R.id.action_newTaskFragment_to_actMainFragment)


            } else if (binding.newProjectBasicInfoAddCardview.isVisible) {
                //see if the basic (3) info of task is updated then move to the next page - NEXT BUTTON PAGE 1
                var taskName = binding.newProjectNameEdittext.text.toString()
                var taskTo = binding.toNameEdittext.text.toString()
                var taskDeadline = binding.deadlineTextview.text.toString()

                if ((taskName.isEmpty() || taskTo.isEmpty() || taskDeadline.isEmpty())) {
                    Utils.emptyFieldSafeguard(binding.newProjectNameEdittext, "Task Name")
                    Utils.emptyFieldSafeguard(binding.toNameEdittext,
                        "Task to do (task destination)")
                    Utils.emptyTextFieldSafeguard(binding.deadlineTextview, "Task Deadline")


                } else {

                    task.task_name = taskName
                    task.task_to = taskTo
                    task.task_deadline =
                        Utils.makeMillis(taskDeadline)

                    //insert task
                    viewmodel.insertTask(task)
//                    viewmodel.setNoifDeadline(task.task_deadline, task.task_id, taskName)

                    binding.toTittleTextview.textSize = miniFont.toFloat()
                    binding.projectTittleTextview.textSize = miniFont.toFloat()
                    binding.deadlineTittleTextview.textSize = miniFont.toFloat()


                    binding.newProjectNameEdittext.visibility = View.GONE
                    binding.toNameEdittext.visibility = View.GONE
                    binding.pickDateChip.visibility = View.GONE

                    binding.projectNameTextview.visibility = View.VISIBLE
                    binding.toNameTextview.visibility = View.VISIBLE

                    binding.projectNameTextview.text = taskName
                    binding.toNameTextview.text = taskTo

                    binding.tasksCardview.visibility = View.VISIBLE



                }
            }


        }

        //Get date picker for deadline------------
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val dpd1 = DatePickerDialog(
            this.requireContext(),
            DatePickerDialog.OnDateSetListener { _, thisYear, monthOfYear, dayOfMonth ->
                var mont = monthOfYear + 1
                binding.deadlineTextview.text = "$dayOfMonth/$mont/$thisYear"
            },
            year,
            month,
            day
        )

        binding.pickDateChip.setOnClickListener {
            dpd1.show()
        }
        //----------------------------------------🍕



        return binding.root
    }



}