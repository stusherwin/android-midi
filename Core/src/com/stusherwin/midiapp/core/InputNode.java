package com.stusherwin.midiapp.core;

import rx.Observable;

import java.util.ArrayList;
import java.util.List;

public class InputNode<T> extends Node<T> {
    private List<OutputNode> _connected = new ArrayList<OutputNode>();

    public void connectTo(OutputNode<T> output) {
        if( !_connected.contains(output)) {
            _connected.add(output);
            output.subject.subscribe(this.subject);
        }
    }

    public Observable<T> observable() {
        return this.subject;
    }
}