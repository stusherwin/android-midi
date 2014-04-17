package com.stusherwin.midiapp.ui;

import android.content.Context;
import android.graphics.*;
import android.view.TextureView;
import com.stusherwin.midiapp.core.midi.MidiEvent;
import com.stusherwin.midiapp.core.modules.InputNode;
import com.stusherwin.midiapp.core.Note;
import rx.Observable;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MidiEventViewer extends TextureView implements TextureView.SurfaceTextureListener {
    private InputNode<MidiEvent> input;
    private boolean ready;
    private Paint paint;

    public MidiEventViewer(Context context) {
        super(context);

        this.input = new InputNode<MidiEvent>();

        setSurfaceTextureListener(this);
        setOpaque(false);

        paint = new Paint();
        Typeface roboto = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Regular.ttf");
        paint.setTypeface(roboto);
        paint.setTextSize(200);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(Color.WHITE);

        this.input.observable()
                .observeOn(Schedulers.newThread())
                .subscribe(new Action1<MidiEvent>() {
                    @Override
                    public void call(MidiEvent midiEvent) {
                        if (!ready) {
                            return;
                        }

                        final Canvas canvas = lockCanvas(null);
                        try {
                            canvas.drawColor(0x00000000, PorterDuff.Mode.CLEAR);
                            int w = canvas.getWidth();
                            int h = canvas.getHeight();
                            String text = midiEvent.toString();
                            canvas.drawText(text, w/2f, h/2f, paint);
                        } finally {
                            unlockCanvasAndPost(canvas);
                        }
                    }
                });
    }

    public InputNode<MidiEvent> input() {
        return this.input;
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        ready = true;
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    }
}