package net.devetude.trace.db.repository

import androidx.paging.DataSource
import net.devetude.trace.db.dao.HistoryDao
import net.devetude.trace.entity.History
import net.devetude.trace.entity.HistoryWithCar

class HistoryRepository(private val historyDao: HistoryDao) {
    suspend fun insert(history: History) = historyDao.insert(history)

    fun selectAllHistoriesWithCar(): DataSource.Factory<Int, HistoryWithCar> =
        historyDao.selectAllHistoriesWithCar()
}
