package net.devetude.trace.usecase.history.cases

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import androidx.paging.toLiveData
import net.devetude.trace.entity.HistoryWithCar
import net.devetude.trace.repository.HistoryRepository

internal class SelectPagedHistoriesWithCarUseCase(
    private val historyRepository: HistoryRepository
) {
    fun run(): LiveData<PagedList<HistoryWithCar>> =
        historyRepository.selectAllHistoriesWithCar().toLiveData(PAGE_SIZE)

    companion object {
        private const val PAGE_SIZE = 10
    }
}
