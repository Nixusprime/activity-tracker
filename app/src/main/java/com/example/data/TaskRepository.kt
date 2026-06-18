package com.example.data

import kotlinx.coroutines.flow.Flow

class TaskRepository(private val taskDao: TaskDao) {
    val allMissions: Flow<List<Mission>> = taskDao.getAllMissions()

    fun getTasksForMission(missionId: Int): Flow<List<Task>> {
        return taskDao.getTasksForMission(missionId)
    }

    suspend fun insertMission(mission: Mission): Long {
        return taskDao.insertMission(mission)
    }

    suspend fun updateMission(mission: Mission) {
        taskDao.updateMission(mission)
    }

    suspend fun deleteMission(mission: Mission) {
        taskDao.deleteMission(mission)
    }

    suspend fun insertTask(task: Task) {
        taskDao.insertTask(task)
    }

    suspend fun insertTasks(tasks: List<Task>) {
        taskDao.insertTasks(tasks)
    }

    suspend fun updateTask(task: Task) {
        taskDao.updateTask(task)
    }

    suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task)
    }

    suspend fun clearTasksForMission(missionId: Int) {
        taskDao.clearTasksForMission(missionId)
    }

    suspend fun clearAllMissions() {
        taskDao.clearAllMissions()
    }

    suspend fun resetTasksForMission(missionId: Int) {
        taskDao.resetTasksForMission(missionId)
    }
}
