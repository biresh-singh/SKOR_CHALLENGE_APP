package utils;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Movie;
import android.util.AttributeSet;
import android.view.View;

import com.root.skor.R;

import java.io.InputStream;

public class GIFView extends View {
    public Movie mMovie;
    public long movieStart;
    int width,height;

    public GIFView(Context context) {
        super(context);
        width= context.getResources().getDisplayMetrics().widthPixels;
         height= context.getResources().getDisplayMetrics().heightPixels;
        initializeView();
    }

    public GIFView(Context context, AttributeSet attrs) {
        super(context, attrs);
        width= context.getResources().getDisplayMetrics().widthPixels;
        height= context.getResources().getDisplayMetrics().heightPixels;
        initializeView();
    }

    public GIFView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        width= context.getResources().getDisplayMetrics().widthPixels;
        height= context.getResources().getDisplayMetrics().heightPixels;
        initializeView();
    }

    private void initializeView() {
//R.drawable.loader - our animated GIF
         if(width>720) {
             InputStream is = getResources().openRawResource(R.raw.man);
             mMovie = Movie.decodeStream(is);
         }
        else if(width<=720)
         {
             InputStream is = getResources().openRawResource(R.raw.mansmall_);
             mMovie = Movie.decodeStream(is);
         }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.TRANSPARENT);
        super.onDraw(canvas);
        long now = android.os.SystemClock.uptimeMillis();
        if (movieStart == 0) {
            movieStart = now;
        }
        if (mMovie != null) {
            int relTime = (int) ((now - movieStart) % mMovie.duration());
            mMovie.setTime(relTime);
            mMovie.draw(canvas, getWidth() - mMovie.width(), getHeight() - mMovie.height());
            this.invalidate();
        }
    }
    private int gifId;

    public void setGIFResource(int resId) {
        this.gifId = resId;
        initializeView();
    }

    public int getGIFResource() {
        return this.gifId;
    }
}
