package com.stusherwin.midiapp.core.modules

import rx.lang.scala.Observable
import rx.lang.scala.schedulers.NewThreadScheduler

class InputNode[T] extends Node[T] {
  def observable : Observable[T] = this.subject

  def connectTo(output : OutputNode[T]) : Unit = {
    output.subject
      .subscribeOn(NewThreadScheduler.apply())
      .subscribe(this.subject)
  }
}