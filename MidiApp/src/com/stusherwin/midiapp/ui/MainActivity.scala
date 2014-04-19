package com.stusherwin.midiapp.ui
import android.app.Activity
import com.stusherwin.midiapp.core.Notifier
import com.stusherwin.midiapp.core.midi.modules._
import com.stusherwin.midiapp.ui.midiimpl.{PureDataMidiPlayer, PureDataMidiDevice}
import android.widget.{TextView, Toast, Button}
import android.view.{View, ViewGroup}
import android.os.Bundle

class MainActivity extends Activity with Notifier  {
  private var midiInput: MidiInput = null
  private var midiOutput: MidiOutput = null

  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.main)
    midiInput = new MidiInput(new PureDataMidiDevice(getApplicationContext, getFragmentManager, this))
    midiOutput = new MidiOutput(new PureDataMidiPlayer(getApplicationContext))
    val notesToMidi2: NotesToMidi2 = new NotesToMidi2
    val midiToNotes2: MidiToNotes2 = new MidiToNotes2
    midiInput.output.connectTo(midiToNotes2.input)
    midiToNotes2.output.connectTo(notesToMidi2.input)
    notesToMidi2.output.connectTo(midiOutput.input)
    val connect: Button = findViewById(R.id.connect).asInstanceOf[Button]
    connect.setOnClickListener(new View.OnClickListener {
      def onClick(v: View) {
        midiInput.init
        midiOutput.init
      }
    })
    val container: ViewGroup = findViewById(R.id.layout).asInstanceOf[ViewGroup]

    val viewer: MidiEventViewer = new MidiEventViewer(getApplicationContext)
    viewer.input.connectTo(notesToMidi2.output)
    container.addView(viewer)
  }

  override def onDestroy {
    super.onDestroy
    midiInput.destroy
    midiOutput.destroy
  }

  private var toast: Toast = null

  private def toast(msg: String) {
    runOnUiThread(new Runnable {
      def run {
        if (toast == null) {
          toast = Toast.makeText(getApplicationContext, "", Toast.LENGTH_SHORT)
        }
        toast.setText("MidiApp: " + msg)
        toast.show
      }
    })
  }

  def Notify(message: String) {
    toast(message)
  }
}