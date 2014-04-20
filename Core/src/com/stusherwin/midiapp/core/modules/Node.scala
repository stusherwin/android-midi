package com.stusherwin.midiapp.core.modules

import rx.lang.scala.Subject
import rx.subjects.PublishSubject
;

abstract class Node[T] {
    protected[modules] val subject : Subject[T] = Subject.apply[T]()
}