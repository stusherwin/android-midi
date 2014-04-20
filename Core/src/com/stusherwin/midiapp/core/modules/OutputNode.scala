package com.stusherwin.midiapp.core.modules

import rx.lang.scala.Observer

class OutputNode[T] extends Node[T] {
  val observer : Observer[T] = this.subject

  def connectTo(input : InputNode[T]) : Unit = {
    input.connectTo(this)
  }
}