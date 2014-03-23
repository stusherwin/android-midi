package com.stusherwin.midiapp.core.midi;

public class NoteOff extends NoteMidiEvent {
    public NoteOff(int channel, int note, int velocity) {
        super(channel, note, velocity);
    }
}
