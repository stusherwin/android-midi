package com.stusherwin.midiapp.ui

import android.content.Context
import android.graphics._
import android.view.TextureView
import com.stusherwin.midiapp.core.midi.MidiEvent
import com.stusherwin.midiapp.core.modules.InputNode
import rx.lang.scala.schedulers.NewThreadScheduler

class MidiEventViewer(context : Context) extends TextureView(context) with TextureView.SurfaceTextureListener {
  val input = new InputNode[MidiEvent]
  var ready = false
  setSurfaceTextureListener(this)
  setOpaque(false)

  val paint = new Paint()
  val roboto = Typeface.createFromAsset(context.getAssets, "fonts/Roboto-Regular.ttf")
  paint.setTypeface(roboto)
  paint.setTextSize(200)
  paint.setTextAlign(Paint.Align.CENTER)
  paint.setColor(Color.WHITE)

  input.observable
    .observeOn(NewThreadScheduler.apply())
    .subscribe((midiEvent : MidiEvent) => {
      if (ready) {
        val canvas = lockCanvas(null)
        try {
            canvas.drawColor(0x00000000, PorterDuff.Mode.CLEAR)
            val w = canvas.getWidth
            val h = canvas.getHeight
            val text = midiEvent.toString
            canvas.drawText(text, w/2f, h/2f, paint)
        } finally {
            unlockCanvasAndPost(canvas)
        }
      }
    })

  override def onSurfaceTextureAvailable(surface : SurfaceTexture, width : Int, height : Int) : Unit = {
    ready = true
  }

  override def onSurfaceTextureSizeChanged(surface : SurfaceTexture, width : Int, height : Int) : Unit = {}
  override def onSurfaceTextureDestroyed(surface : SurfaceTexture) : Boolean = true
  override def onSurfaceTextureUpdated(surface : SurfaceTexture) : Unit = {}
}