package com.stusherwin.midiapp.core;

import rx.subjects.PublishSubject;

public abstract class Node<T> {
    private PublishSubject<T> subject = PublishSubject.create();

    //Don't like exposing this :(
    public PublishSubject<T> subject() {
        return subject;
    }
}