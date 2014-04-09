package com.stusherwin.midiapp.core;

import rx.Observer;

public class OutputNode<T> extends Node<T> {
    public void connectTo(InputNode<T> input) {
        input.connectTo(this);
    }

    public Observer<T> observer() {
        return this.subject;
    }
}