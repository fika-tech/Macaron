package tech.fika.macaron.android.sample

import tech.fika.macaron.core.tools.DefaultJobManager
import tech.fika.macaron.core.tools.DefaultMessageManager
import tech.fika.macaron.core.tools.JobManager
import tech.fika.macaron.core.tools.MessageManager
import tech.fika.macaron.logging.LoggingInterceptor
import tech.fika.macaron.statemachine.components.stateMachine

fun sampleStateMachine(
    useCase: UseCase = DI.useCase,
    useCase2: UseCase2 = DI.useCase2,
    jobManager: JobManager = DefaultJobManager(),
    messageManager: MessageManager = DefaultMessageManager(),
) = stateMachine<SampleAction, SampleEvent, SampleState> {
    initialState {
        SampleState.Stopped
    }

    interceptors {
        add(LoggingInterceptor())
    }

    lifecycle {
        onStart {
            if (state is SampleState.Stopped) {
                dispatch(SampleAction.Start)
            }
        }
        onResume {
            if (state is SampleState.Paused) {
                dispatch(SampleAction.Start)
            }
        }
        onPause {
            if (state is SampleState.Started) {
                dispatch(SampleAction.Pause)
            }
        }
        onDestroy {
            jobManager.cancelAll()
            messageManager.dispose()
        }
    }

    state<SampleState.Stopped> {
        process<SampleAction.Start> {
            transition {
                SampleState.Started(counter = 5)
            }
        }
    }

    state<SampleState.Started> {
        onEnter {
            dispatch(SampleAction.Start)
        }

        onRepeat {
            if (state.counter == 0) {
                dispatch(SampleAction.Complete)
            }
        }

        onExit {
            jobManager.cancel(key = "Timer")
        }

        process<SampleAction.Start> {
            jobManager.launch(key = "Timer") {
                useCase().collect { dispatch(SampleAction.Decrement) }
            }
            empty()
        }

        process<SampleAction.Pause> {
            transition {
                SampleState.Paused(counter = counter)
            }
        }

        process<SampleAction.Stop> {
            transition {
                SampleState.Stopped
            }
        }

        process<SampleAction.Decrement> {
            transition {
                if (counter > 0) {
                    copy(counter = counter - 1)
                } else {
                    this
                }
            }
        }

        process<SampleAction.Complete> {
            jobManager.launch("Random") {
                runCatching {
                    useCase2.invoke()
                }.onSuccess {
                    send(SampleEvent.Complete)
                    dispatch(SampleAction.Stop)
                }.onFailure {
                    send(SampleEvent.Error)
                    dispatch(SampleAction.Pause)
                }
            }
            empty()
        }
    }

    state<SampleState.Paused> {
        process<SampleAction.Start> {
            transition {
                SampleState.Started(counter = counter)
            }
        }
    }
}
