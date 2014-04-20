package com.stusherwin.midiapp.core.modules

abstract class SourceModule[T] extends Module {
  val output = new OutputNode[T]
}
