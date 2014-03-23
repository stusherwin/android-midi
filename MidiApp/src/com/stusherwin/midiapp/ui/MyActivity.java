package com.stusherwin.midiapp.ui;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import com.stusherwin.midiapp.core.midi.MidiDevice;
import com.stusherwin.midiapp.core.midi.MidiEvent;
import com.stusherwin.midiapp.core.midi.NoteOff;
import com.stusherwin.midiapp.core.midi.NoteOn;
import com.stusherwin.midiapp.ui.midiimpl.AllAttachedUsbMidiDevices;
import com.stusherwin.midiapp.core.midi.MidiPlayer;
import com.stusherwin.midiapp.ui.midiimpl.PureDataMidiPlayer;
import rx.functions.Action1;


public class MyActivity extends Activity {
    private MidiDevice _midiDevice;
    private MidiPlayer _player;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        _player = new PureDataMidiPlayer(getApplicationContext());
        _player.init();

        _midiDevice = new AllAttachedUsbMidiDevices(getApplicationContext());
        _midiDevice.getNoteStream().subscribe(new Action1<MidiEvent>() {
            @Override
            public void call(MidiEvent evt) {
              if (evt instanceof NoteOn) {
                  _player.noteOn(((NoteOn) evt).getNote());
              } else if (evt instanceof NoteOff) {
                  _player.noteOff(((NoteOff) evt).getNote());
              }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
              Log.e("AARGH", throwable.getLocalizedMessage());
              //throw throwable;
              //Toast.makeText(MyActivity.this, throwable.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        _midiDevice.destroy();
        _player.destroy();
    }
}