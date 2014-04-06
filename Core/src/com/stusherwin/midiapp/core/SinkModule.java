package com.stusherwin.midiapp.core;

public abstract class SinkModule<T> extends Module {
    private InputNode<T> input;

    public SinkModule() {
        this.input = new InputNode<T>();
    }

    public InputNode<T> input() {
        return this.input;
    }
}
