package com.stusherwin.midiapp.core;

public abstract class SourceModule<T> extends Module {
    private OutputNode<T> output;

    public SourceModule() {
        this.output = new OutputNode<T>();
    }

    public OutputNode<T> output() {
        return this.output;
    }
}
