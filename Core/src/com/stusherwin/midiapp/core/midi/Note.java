package com.stusherwin.midiapp.core.midi;

public class Note {
    public int channel;
    public int note;
    public int velocity;

    public Note(int channel, int note, int velocity) {
        this.channel = channel;
        this.note = note;
        this.velocity = velocity;
    }
}
