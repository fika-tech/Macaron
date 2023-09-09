package tech.fika.macaron.core.contract

sealed interface Contract

/**
 * Models any form of user interaction
 */
interface Intent : Contract

/**
 * Result from a processor
 */
interface Action : Contract {
    interface Event : Action
    interface Message : Action
}

/**
 * Describe the state of the screen
 */
interface State : Contract

