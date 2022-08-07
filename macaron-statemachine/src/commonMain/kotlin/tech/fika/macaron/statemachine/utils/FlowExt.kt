package tech.fika.macaron.statemachine.utils

import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flowOf
import tech.fika.macaron.core.contract.Event
import tech.fika.macaron.core.contract.Result
import tech.fika.macaron.statemachine.components.StateMachine

suspend fun <R : Result, E : Event> FlowCollector<StateMachine.SideEffect<R, E>>.result(result: R) = emit(StateMachine.SideEffect.ResultNode(result))

suspend fun <R : Result, E : Event> FlowCollector<StateMachine.SideEffect<R, E>>.event(event: E) = emit(StateMachine.SideEffect.EventNode(event))

fun <R : Result, E : Event> resultOf(result: R) = flowOf(StateMachine.SideEffect.ResultNode<R, E>(result))

fun <R : Result, E : Event> eventOf(event: E) = flowOf(StateMachine.SideEffect.EventNode<R, E>(event))


