package com.stusherwin.midiapp.core.modules;

import rx.subjects.PublishSubject;

public abstract class Node<T> {
    protected PublishSubject<T> subject = PublishSubject.create();
}