package com.stusherwin.midiapp.core;

import rx.Observable;

public abstract class ThruModule<TInput, TOutput> extends Module {
    private InputNode<TInput> input;
    private OutputNode<TOutput> output;

    public ThruModule() {
        this.input = new InputNode<TInput>();
        this.output = new OutputNode<TOutput>();

        transform(input.subject()).subscribe(output.subject());
    }

    public InputNode<TInput> input() {
        return this.input;
    }

    public OutputNode<TOutput> output() {
        return this.output;
    }

    protected abstract Observable<TOutput> transform(Observable<TInput> input);
}
