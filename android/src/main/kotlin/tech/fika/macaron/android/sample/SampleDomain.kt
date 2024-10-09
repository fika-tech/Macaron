package tech.fika.macaron.android.sample

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.random.Random

object DI {
    val repository = Repository()
    val useCase2 = UseCase2()
    val useCase = UseCase(repository = repository)
}

class UseCase(
    private val repository: Repository
) {
    suspend operator fun invoke(): Flow<Unit> = repository.flow
}

class UseCase2 {
    suspend operator fun invoke(): Unit {
        delay(1000)
        if (Random.Default.nextInt() % 2 == 0) {
            throw IllegalStateException()
        }
    }
}

class Repository {
    val flow: Flow<Unit> = flow {
        while (true) {
            delay(1000)
            emit(Unit)
        }
    }
}
