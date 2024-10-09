package tech.fika.macaron.statemachine.builders

import tech.fika.macaron.core.contract.Contract
import kotlin.reflect.KClass

class Matcher<T : Contract, out R : T> private constructor(private val kClass: KClass<R>) {
    private val predicates = mutableListOf<(T) -> Boolean>({ kClass.isInstance(it) })
    fun matches(value: T) = predicates.all { it(value) }

    companion object {
        fun <T : Contract, R : T> any(kClass: KClass<R>): Matcher<T, R> = Matcher(kClass)
        inline fun <T : Contract, reified R : T> any(): Matcher<T, R> = any(R::class)
    }
}