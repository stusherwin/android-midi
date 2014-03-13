package com.stusherwin.test.ui;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;
import com.stusherwin.test.core.midi.MidiDevice;
import com.stusherwin.test.core.midi.MidiDeviceInputListener;
import com.stusherwin.test.core.midi.Note;
import com.stusherwin.test.core.midi.SysExMessage;
import com.stusherwin.test.ui.midiimpl.AllAttachedUsbMidiDevices;

public class MyActivity extends Activity {
    private MidiDevice _midiDevice;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        _midiDevice = new AllAttachedUsbMidiDevices(getApplicationContext());
        _midiDevice.addInputListener(new MidiDeviceInputListener() {
            @Override
            public void onSysExMessage(SysExMessage sysExMessage) {
            }

            @Override
            public void onNoteOn(Note note) {
                
                //Toast.makeText(MyActivity.this, "" + note.note, Toast.LENGTH_SHORT ).show();
            }

            @Override
            public void onNoteOff(Note note) {
                //
            }
        } );
    }

    @Override
    public void onDestroy() {
        _midiDevice.dispose();
        super.onDestroy();
    }
}
