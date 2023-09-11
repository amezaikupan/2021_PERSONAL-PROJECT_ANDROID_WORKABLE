package com.improver.workable.data.repository

import com.improver.workable.data.dao.ChipDao
import com.improver.workable.data.dao.PhototDao
import com.improver.workable.data.dao.ProgressDao
import com.improver.workable.data.dao.TaskDao
import com.improver.workable.data.entity.Chips
import com.improver.workable.data.entity.Photo
import com.improver.workable.data.entity.Progress
import com.improver.workable.data.entity.Task
import com.improver.workable.data.relationship.ProgressWithPhoto
import com.improver.workable.data.relationship.TaskWithProgress
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.Callable


class TaskRepository(
    private val taskDao: TaskDao,
    private val progressDao: ProgressDao,
    private val chipDao: ChipDao,
    private val phototDao: PhototDao,
) {


    //uiScope
    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    val allTask: Flow<List<Task>> = taskDao.getAllLiveTask()
    val allOnLiveTask: Flow<List<Task>> = taskDao.getAllOnLiveTask()




    //insert new task----------------------------

     fun InsertTask(task: Task): Single<Long>{
        return Single.fromCallable (Callable <Long>{
            taskDao.insertTask(task)
        })

    }
    //-----------------------------------------🍕🍕🍕🍕🍕

    //Get most recent task list
    private suspend fun gettaskid(): Long {
        return withContext(Dispatchers.IO) {
            taskDao.getRecentTaskID()!!
        }
    }
    //---------------------------------------------🍕🍕🍕🍕🍕🍕



    //update task state ---------------------------
    fun UpdateState(id: Long) {
        uiScope.launch {
            updatestate(id)
        }
    }

    private suspend fun updatestate(id: Long) {
        withContext(Dispatchers.IO) {
            taskDao.updateState(id)
        }
    }
    //-------------------------------------------

    //set chips to task parent id
    fun InsertChips(chipstring: String) {
        uiScope.launch {
            insertchip(chipstring)
        }
    }

    private suspend fun insertchip(chipstring: String) {

        withContext(Dispatchers.IO) {
            var chip = Chips()
            chip.this_chip_task_id = gettaskid()
            chip.chips = chipstring
            chipDao.insertChip(chip)
        }
    }
    //-------------------------------------------


    //Get all chips
    suspend fun getAllChips(): List<Chips> {
        return withContext(Dispatchers.IO) {
            var chips = chipDao.getAllChips()
            chips
        }
    }
    //----------------------------------------------

    //get all task chips
    suspend fun getAllTaskChips(id: Long): List<Chips> {
        return withContext(Dispatchers.IO) {
            var chipsList = chipDao.getTaskAllLChips(id)
            chipsList
        }
    }
    //-------------------------------------------

    //get task by task name
    suspend fun getTaskByID(taskID: Long): Task {
        return withContext(Dispatchers.IO) {
            var task = taskDao.getTaskById(taskID)
            task
        }
    }
    //---------------------------------------------


    //insert new progress
    fun InsertProgress(progress: Progress) {
        uiScope.launch {
            insertprogress(progress)
        }
    }

    private suspend fun insertprogress(progress: Progress) {
        withContext(Dispatchers.IO) {
            progressDao.insertProgress(progress)
        }
    }
    //--------------------------

    //insert new photo
    fun InsertPhoto(uri: String) {
        uiScope.launch {
            insertphoto(uri)
        }
    }

    private suspend fun insertphoto(uri: String) {
        withContext(Dispatchers.IO) {
            var photo = Photo()
            photo.this_photo_progress_id = getMostRecentProgressId()
            photo.photo_uri = uri
            phototDao.insertPhoto(photo)
        }
    }
    //--------------------------------------------

    //Get photo by progress's id
    suspend fun getPhotoByProgressID(progressID: Long): List<Photo> {
        return withContext(Dispatchers.IO) {
            var photo = phototDao.getProgressPhoto(progressID)
            photo
        }
    }
    //---------------------------------

    //get all progress
    suspend fun getAllProgress(): List<Progress> {
        return withContext(Dispatchers.IO) {
            var progress = progressDao.getAllProgress()
            progress
        }
    }
    //---------------------------------

    //get all photoes
    suspend fun getAllPhoto(): List<Photo> {
        return withContext(Dispatchers.IO) {
            var photo = phototDao.getAllPhoto()
            photo
        }
    }
    //-------------------------------------------

    //Update Chip entity with list/id position
    fun UpdateChip(ids: Long) {
        uiScope.launch {
            updatechips(ids)
        }
    }

    private suspend fun updatechips(ids: Long) {
        withContext(Dispatchers.IO) {
            chipDao.updateChipState(ids)
        }
    }
    //-------------------------------------------

    //get progress from this task
    suspend fun getProgressByTask(taskID: Long): List<Progress> {
        return withContext(Dispatchers.IO) {
            var progress = progressDao.getThisTaskProgress(taskID)
            progress
        }
    }
    //----------------------------------

    //get most recent progress
    suspend fun getMostRecentProgressId(): Long {
        return withContext(Dispatchers.IO){
            var id = progressDao.getRecentMadeProgressID()
            id
        }
    }
    //-------------------------------

    //get all taskWithProgress by id
    suspend fun getTaskProgressByID(taskID:Long): TaskWithProgress {
        return withContext(Dispatchers.IO){
            var taskWithProgress = taskDao.getTaskWithProgress(taskID)
            taskWithProgress
        }
    }
    //-------------------------------

    //get all taskWithProgress by id
    suspend fun getProgressPhotoByID (progresID:Long): ProgressWithPhoto {
        return withContext(Dispatchers.IO){
            var progressWithPhoto = progressDao.getProgressWithPhoto(progresID)
            progressWithPhoto
        }
    }
    //-------------------------------

    //delete Task by id
    fun DeleteTask(ids: Long) {
        uiScope.launch {
            deletetask(ids)
        }
    }

    private suspend fun deletetask(ids: Long) {
        withContext(Dispatchers.IO) {
            taskDao.deleteTask(ids)
        }
    }
    //------------------------------------

    //delete Task by id
    fun DeleteTaskProgress(ids: Long) {
        uiScope.launch {
            deletetaskprogress(ids)
        }
    }

    private suspend fun deletetaskprogress(ids: Long) {
        withContext(Dispatchers.IO) {
            progressDao.deleteTaskProgress(ids)
        }
    }
    //------------------------------------

    //delete Task by id
    fun DeleteTaskChip(ids: Long) {
        uiScope.launch {
            deletetaskchip(ids)
        }
    }

    private suspend fun deletetaskchip(ids: Long) {
        withContext(Dispatchers.IO) {
            chipDao.deleteTaskChip(ids)
        }
    }
    //------------------------------------

    //update task bid ---------------------------
    fun UpdateProgress(thisTaskID: Long, progressNote: String, progressTicker: String) {
        uiScope.launch {
            updateprogrss(thisTaskID, progressNote, progressTicker)
        }
    }

    private suspend fun updateprogrss(thisTaskID: Long, progressNote: String, progressTicker: String) {
        withContext(Dispatchers.IO) {
            progressDao.updateProgress(thisTaskID, progressNote, progressTicker, getMostRecentProgressId())
        }
    }
    //-------------------------------------------

    //delete Task by id
    fun deleteProgress() {
        uiScope.launch {
            deleteprogress()
        }
    }

    private suspend fun deleteprogress() {
        withContext(Dispatchers.IO) {
            val toDelete = getMostRecentProgressId()
            progressDao.deleteProgress(toDelete)
        }
    }
    //------------------------------------

    //delete Photo by id
    fun DeletePhoto(photo: Photo) {
        uiScope.launch {
            deletephoto(photo)
        }
    }

    private suspend fun deletephoto(photo: Photo) {
        withContext(Dispatchers.IO) {
            phototDao.deletePhoto(photo)
        }
    }
    //------------------------------------

    //Update Chip entity with list/id position
    fun UpdateChipFalse(ids: Long) {
        uiScope.launch {
            updatechipsfalse(ids)
        }
    }

    private suspend fun updatechipsfalse(ids: Long) {
        withContext(Dispatchers.IO) {
            chipDao.updateChipStateFalse(ids)
        }
    }
    //-------------------------------------------

}
