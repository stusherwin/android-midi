package com.stusherwin.midiapp.core.midi;

import com.stusherwin.midiapp.core.Note;

public interface MidiDeviceInputListener {
    void onSysExMessage(com.stusherwin.midiapp.core.midi.SysExMessage sysExMessage);
    void onNoteOn(Note note);
    void onNoteOff(Note note);
}

