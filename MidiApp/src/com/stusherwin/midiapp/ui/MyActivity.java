package com.stusherwin.midiapp.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import com.stusherwin.midiapp.core.Notifier;
import com.stusherwin.midiapp.core.midi.*;
import com.stusherwin.midiapp.ui.midiimpl.PureDataMidiDevice;
import com.stusherwin.midiapp.ui.midiimpl.PureDataMidiPlayer;

public class MyActivity extends Activity implements Notifier {
    private MidiInput midiInput;
    private MidiOutput midiOutput;
    private MidiToNotes midiToNotes;
    private NotesToMidi notesToMidi;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        midiInput = new MidiInput(new PureDataMidiDevice(getApplicationContext(), getFragmentManager(), this));
        midiOutput = new MidiOutput(new PureDataMidiPlayer(getApplicationContext()));
        midiToNotes = new MidiToNotes();
        notesToMidi = new NotesToMidi();

        midiInput.output().connectTo(midiToNotes.input());
        midiToNotes.output().connectTo(notesToMidi.input());
        notesToMidi.output().connectTo(midiOutput.input());

        Button connect = (Button) findViewById(R.id.connect);
        connect.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                midiInput.init();
                midiOutput.init();
            }
        });

        MidiEventViewer viewer = new MidiEventViewer(getApplicationContext());
        viewer.input().connectTo(notesToMidi.output());
        notesToMidi.output().connectTo(viewer.input());
        ViewGroup container = (ViewGroup)findViewById(R.id.layout);
        container.addView(viewer);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        midiInput.destroy();
        midiOutput.destroy();
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