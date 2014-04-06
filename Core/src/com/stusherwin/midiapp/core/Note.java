package com.stusherwin.midiapp.core;

import rx.Observable;

public class Note {
    public int note;
    public int velocity;

    public Note(int note, int velocity) {
        this.note = note;
        this.velocity = velocity;
    }

    @Override
    public String toString() {
        return "Note: " + note;
    }
}

