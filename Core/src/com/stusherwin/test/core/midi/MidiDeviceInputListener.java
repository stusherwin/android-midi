package com.stusherwin.test.core.midi;

public interface MidiDeviceInputListener {
    void onSysExMessage(SysExMessage sysExMessage);
    void onNoteOn(Note note);
    void onNoteOff(Note note);
}

