package com.stusherwin.midiapp.core.midi;

public class NoteOn extends NoteMidiEvent {
    private int velocity;
    public NoteOn(int channel, int note, int velocity) {
        super(channel, note);

        this.velocity = velocity;
    }

    public int getVelocity() {
        return velocity;
    }

    @Override
    public String toString() {
        return "Note on: " + super.getNote();
    }
}
