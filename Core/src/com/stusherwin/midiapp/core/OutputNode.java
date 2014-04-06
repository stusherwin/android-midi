package com.stusherwin.midiapp.core;

public class OutputNode<T> extends Node<T> {
    public void connectTo(InputNode<T> input) {
        this.subject().subscribe(input.subject());
    }
}