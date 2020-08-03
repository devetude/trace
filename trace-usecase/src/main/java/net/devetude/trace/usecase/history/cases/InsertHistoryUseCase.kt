package net.devetude.trace.usecase.history.cases

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.devetude.trace.entity.History
import net.devetude.trace.repository.HistoryRepository

internal class InsertHistoryUseCase(private val historyRepository: HistoryRepository) {
    suspend fun run(history: History): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching { historyRepository.insert(history) }
    }
}
