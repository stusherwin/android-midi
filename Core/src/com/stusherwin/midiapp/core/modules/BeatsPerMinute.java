package com.stusherwin.midiapp.core.modules;

import com.stusherwin.midiapp.core.InitializationException;
import com.stusherwin.midiapp.core.Note;
import rx.Observable;

public class BeatsPerMinute extends ThruModule<Observable<Note>, Integer> {
    @Override
    protected Observable<Integer> transform(Observable<Observable<Note>> input) {
        //input.takeLast(4);
        return null;
    }

    @Override
    public void destroy() {

    }

    @Override
    public void init() throws InitializationException {

    }
}
