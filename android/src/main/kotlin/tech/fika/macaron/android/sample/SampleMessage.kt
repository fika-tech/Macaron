package tech.fika.macaron.android.sample

import tech.fika.macaron.core.tools.Message

sealed class SampleMessage : Message {
    data object Complete : SampleMessage()
}