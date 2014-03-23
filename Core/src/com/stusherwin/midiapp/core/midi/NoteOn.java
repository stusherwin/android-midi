package com.stusherwin.midiapp.core.midi;

public class NoteOn extends NoteMidiEvent {
    public NoteOn(int channel, int note, int velocity) {
        super(channel, note, velocity);
    }
}
