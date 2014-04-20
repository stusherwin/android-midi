package com.stusherwin.midiapp.ui
import android.app.Activity
import com.stusherwin.midiapp.core.Notifier
import com.stusherwin.midiapp.core.midi.modules._
import com.stusherwin.midiapp.ui.midiimpl.{PureDataMidiPlayer, PureDataMidiDevice}
import android.widget.{TextView, Toast, Button}
import android.view.{View, ViewGroup}
import android.os.Bundle
import com.stusherwin.midiapp.core.midi.NoteOn

class MainActivity extends Activity with Notifier {
  private var midiInput: MidiInput = null
  private var midiOutput: MidiOutput = null

  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.main)

    midiInput = new MidiInput(new PureDataMidiDevice(getApplicationContext, getFragmentManager, this))
    midiOutput = new MidiOutput(new PureDataMidiPlayer(getApplicationContext))
    val notesToMidi = new NotesToMidi2
    val midiToNotes = new MidiToNotes2
    midiInput.output.connectTo(midiToNotes.input)
    midiToNotes.output.connectTo(notesToMidi.input)
    notesToMidi.output.connectTo(midiOutput.input)

    val connect = findViewById(R.id.connect).asInstanceOf[Button]
    connect.setOnClickListener(new View.OnClickListener {
      def onClick(v: View) {
        midiInput.init()
        midiOutput.init()
      }
    })

    val container = findViewById(R.id.layout).asInstanceOf[ViewGroup]

    val viewer = new MidiEventViewer(getApplicationContext)
    viewer.input.connectTo(notesToMidi.output)
    container.addView(viewer)
  }

  override def onDestroy() : Unit = {
    super.onDestroy()
    midiInput.destroy()
    midiOutput.destroy()
  }

  private var toast: Toast = null

  private def toast(msg: String) {
    runOnUiThread(new Runnable {
      def run() : Unit = {
        if (toast == null) {
          toast = Toast.makeText(getApplicationContext, "", Toast.LENGTH_SHORT)
        }
        toast.setText("MidiApp: " + msg)
        toast.show()
      }
    })
  }

  def Notify(message: String) {
    toast(message)
  }
}