package com.stusherwin.midiapp.core.midi;

public abstract class NoteMidiEvent extends MidiEvent {
    private int channel;
    private int note;
    private int velocity;

    protected NoteMidiEvent(int channel, int note, int velocity) {
        this.channel = channel;
        this.note = note;
        this.velocity = velocity;
    }

    public int getChannel() {
        return channel;
    }

    public int getNote() {
        return note;
    }

    public int getVelocity() {
        return velocity;
    }
}
