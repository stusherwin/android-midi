package com.stusherwin.midiapp.core.midi;

public class NoteOff extends NoteMidiEvent {
    public NoteOff(int channel, int note) {
        super(channel, note);
    }

    @Override
    public String toString() {
        return "Note off: " + super.getNote();
    }
}
