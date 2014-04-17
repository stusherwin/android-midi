package com.stusherwin.midiapp.ui;

import android.content.Context;
import android.graphics.*;
import android.view.TextureView;
import com.stusherwin.midiapp.core.Note;
import com.stusherwin.midiapp.core.midi.NoteWithDuration;
import com.stusherwin.midiapp.core.modules.InputNode;
import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class NoteViewer extends TextureView implements TextureView.SurfaceTextureListener {
    private InputNode<NoteWithDuration> input;
    private boolean ready;
    private Paint paint;

    public NoteViewer(Context context) {
        super(context);

        this.input = new InputNode<NoteWithDuration>();

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
                .subscribe(new Action1<NoteWithDuration>() {
                    @Override
                    public void call(final NoteWithDuration note) {
                        if (!ready) {
                            return;
                        }

                        drawText(note.toString() + " on");

                        note.getDuration().subscribe(new Action1<Object>() {
                            @Override
                            public void call(Object o) {

                            }
                        }, new Action1<Throwable>() {
                             @Override
                             public void call(Throwable throwable) {
                                 drawText(throwable.getMessage());
                             }
                         }, new Action0() {
                             @Override
                             public void call() {
                                 drawText(note.toString() + " off");
                             }
                         });
                    }
                });
    }

    private void drawText(String text) {
        Canvas canvas = lockCanvas(null);
        try {
            canvas.drawColor(0x00000000, PorterDuff.Mode.CLEAR);
            int w = canvas.getWidth();
            int h = canvas.getHeight();
            canvas.drawText(text, w/2f, h/2f, paint);
        } finally {
            unlockCanvasAndPost(canvas);
        }
    }

    public InputNode<NoteWithDuration> input() {
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