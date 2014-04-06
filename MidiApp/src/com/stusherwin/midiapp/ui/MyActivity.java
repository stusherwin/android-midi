package com.stusherwin.midiapp.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.stusherwin.midiapp.core.Notifier;
import com.stusherwin.midiapp.core.midi.*;
import com.stusherwin.midiapp.ui.midiimpl.PureDataMidiDevice;
import com.stusherwin.midiapp.ui.midiimpl.PureDataMidiPlayer;
import rx.functions.Action1;


public class MyActivity extends Activity implements Notifier {
    private MidiDevice midiDevice;
    private MidiPlayer midiPlayer;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        midiPlayer = new PureDataMidiPlayer(getApplicationContext());
        midiDevice = new PureDataMidiDevice(getApplicationContext(), getFragmentManager(), this);

        midiDevice.getNoteStream().subscribe(new Action1<MidiEvent>() {
            @Override
            public void call(MidiEvent midiEvent) {
                if(midiEvent instanceof NoteOn) {
                    NoteOn noteOn = (NoteOn)midiEvent;
                    midiPlayer.noteOn(noteOn.getNote(), noteOn.getVelocity());
                }
                if(midiEvent instanceof NoteOff) {
                    NoteOff noteOff = (NoteOff)midiEvent;
                    midiPlayer.noteOff(noteOff.getNote());
                }
            }
        });


        Button connect = (Button) findViewById(R.id.connect);
        connect.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                midiPlayer.init();
                midiDevice.init();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        midiDevice.destroy();
        midiPlayer.destroy();
    }

    private Toast toast = null;

    private void toast(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
            if (toast == null) {
                toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
            }
            toast.setText("MidiApp: " + msg);
            toast.show();
            }
        });
    }

    @Override
    public void Notify(String message) {
        toast( message );
    }
}