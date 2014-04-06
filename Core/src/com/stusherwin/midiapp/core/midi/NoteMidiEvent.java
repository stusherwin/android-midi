package com.stusherwin.midiapp.core.midi;

public abstract class NoteMidiEvent extends MidiEvent {
    private int channel;
    private int note;

    protected NoteMidiEvent(int channel, int note) {
        this.channel = channel;
        this.note = note;
    }

    public int getChannel() {
        return channel;
    }

    public int getNote() {
        return note;
    }
}
