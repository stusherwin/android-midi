package com.stusherwin.midiapp.core.modules

abstract class SinkModule[T] extends Module {
  val input = new InputNode[T]
}
