package net.devetude.trace.usecase.history

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import net.devetude.trace.db.repository.HistoryRepository
import net.devetude.trace.entity.History
import net.devetude.trace.entity.HistoryWithCar
import net.devetude.trace.usecase.history.cases.InsertHistoryUseCase
import net.devetude.trace.usecase.history.cases.SelectPagedHistoriesWithCarUseCase

class HistoryUseCase(private val historyRepository: HistoryRepository) {
    suspend fun insert(history: History): Result<Unit> =
        InsertHistoryUseCase(historyRepository).run(history)

    fun selectPagedHistoriesWithCar(): LiveData<PagedList<HistoryWithCar>> =
        SelectPagedHistoriesWithCarUseCase(historyRepository).run()
}
