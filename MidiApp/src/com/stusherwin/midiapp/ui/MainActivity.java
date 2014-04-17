package com.stusherwin.midiapp.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import com.stusherwin.midiapp.core.Notifier;
import com.stusherwin.midiapp.core.midi.modules.*;
import com.stusherwin.midiapp.ui.midiimpl.PureDataMidiDevice;
import com.stusherwin.midiapp.ui.midiimpl.PureDataMidiPlayer;

public class MainActivity extends Activity implements Notifier {
    private MidiInput midiInput;
    private MidiOutput midiOutput;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        midiInput = new MidiInput(new PureDataMidiDevice(getApplicationContext(), getFragmentManager(), this));
        midiOutput = new MidiOutput(new PureDataMidiPlayer(getApplicationContext()));
        MidiToNotes midiToNotes = new MidiToNotes();
        NotesToMidi notesToMidi = new NotesToMidi();
        NotesToMidi2 notesToMidi2 = new NotesToMidi2();
        MidiToNotes2 midiToNotes2 = new MidiToNotes2();

//        midiInput.output().connectTo(midiToNotes.input());
//        midiToNotes.output().connectTo(notesToMidi.input());
//        notesToMidi.output().connectTo(midiOutput.input());
        midiInput.output().connectTo(midiToNotes2.input());
        midiToNotes2.output().connectTo(notesToMidi2.input());
        notesToMidi2.output().connectTo(midiOutput.input());

        Button connect = (Button) findViewById(R.id.connect);
        connect.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                midiInput.init();
                midiOutput.init();
            }
        });
        ViewGroup container = (ViewGroup)findViewById(R.id.layout);

        MidiEventViewer viewer = new MidiEventViewer(getApplicationContext());
        viewer.input().connectTo(notesToMidi2.output());
        container.addView(viewer);

//        NoteViewer noteViewer = new NoteViewer(getApplicationContext());
//        noteViewer.input().connectTo(midiToNotes2.output());
//        container.addView(noteViewer);
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