package com.stusherwin.midiapp.core.modules

import rx.lang.scala.Observable

abstract class ThruModule[TInput, TOutput] extends Module {
  val input = new InputNode[TInput]
  val output = new OutputNode[TOutput]

  transform(input.observable).subscribe(output.observer)

  def transform(input : Observable[TInput]) : Observable[TOutput]
}
