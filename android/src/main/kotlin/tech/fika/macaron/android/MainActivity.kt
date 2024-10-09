package tech.fika.macaron.android

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.arkivanov.essenty.lifecycle.essentyLifecycle
import tech.fika.macaron.android.foundation.contract
import tech.fika.macaron.android.foundation.handleEvents
import tech.fika.macaron.android.foundation.render
import tech.fika.macaron.android.sample.SampleAction
import tech.fika.macaron.android.sample.SampleEvent
import tech.fika.macaron.android.sample.SampleState
import tech.fika.macaron.android.sample.sampleStateMachine
import tech.fika.macaron.statemachine.components.store

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            val lifecycleOwner = LocalLifecycleOwner.current
            val store = remember {
                sampleStateMachine().store(lifecycle = lifecycleOwner.essentyLifecycle())
            }
            val contract = contract(store = store)
            contract.handleEvents { event ->
                when (event) {
                    SampleEvent.Complete -> Toast.makeText(context, "Complete", Toast.LENGTH_SHORT).show()
                    SampleEvent.Error -> Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
                }
            }

            val (state, dispatch) = contract
            Box(modifier = Modifier.fillMaxSize()) {
                state.render<SampleState.Stopped> {
                    Button(onClick = { dispatch(SampleAction.Start) }) {
                        Text("Start")
                    }
                }
                state.render<SampleState.Started> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        Text(
                            text = counter.toString()
                        )
                        Button(onClick = { dispatch(SampleAction.Pause) }) {
                            Text("Pause")
                        }
                        Button(onClick = { dispatch(SampleAction.Stop) }) {
                            Text("Stop")
                        }
                    }
                }
                state.render<SampleState.Paused> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        Text(text = counter.toString())
                        Button(onClick = { dispatch(SampleAction.Start) }) {
                            Text("Start")
                        }
                    }
                }
            }
        }
    }
}
