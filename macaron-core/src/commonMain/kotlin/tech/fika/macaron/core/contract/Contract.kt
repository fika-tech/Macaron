package tech.fika.macaron.core.contract

sealed interface Contract

/**
 * Models any form of user interaction
 */
interface Intent : Contract

/**
 * Defines logic that should be executed by a processor
 */
interface Action : Contract

/**
 * Result from a processor
 */
interface Result : Contract

/**
 * Describe the state of the screen
 */
interface State : Contract

/**
 * One-time effects for the client (i.e. navigation, snackbars, animation)
 */
interface Event : Contract

