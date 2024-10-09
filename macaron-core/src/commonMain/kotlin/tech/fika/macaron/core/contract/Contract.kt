package tech.fika.macaron.core.contract

sealed interface Contract

interface Action : Contract

interface Event : Contract

interface State : Contract

