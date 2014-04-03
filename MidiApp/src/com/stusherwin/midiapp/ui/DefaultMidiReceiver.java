package com.stusherwin.midiapp.ui;

import com.noisepages.nettoyeur.midi.MidiReceiver;

/**
 * Created by Stu on 03/04/14.
 */
public class DefaultMidiReceiver implements MidiReceiver {
    @Override
    public void onNoteOff(int channel, int key, int velocity) {}

    @Override
    public void onNoteOn(int channel, int key, int velocity) {}

    @Override
    public void onPolyAftertouch(int channel, int key, int velocity) {}

    @Override
    public void onControlChange(int channel, int controller, int value) {}

    @Override
    public void onProgramChange(int channel, int program) {}

    @Override
    public void onAftertouch(int channel, int velocity) {}

    @Override
    public void onPitchBend(int channel, int value) {}

    @Override
    public void onRawByte(byte value) {}

    @Override
    public boolean beginBlock() {
        return false;
    }

    @Override
    public void endBlock() {}
}
