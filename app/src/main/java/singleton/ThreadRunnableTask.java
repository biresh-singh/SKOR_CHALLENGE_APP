package singleton;

import android.os.CountDownTimer;
import android.os.Handler;

/**
 * Created by Dihardja Software Solutions on 11/15/17.
 */

public abstract class ThreadRunnableTask {
    public Handler handler;
    public Runnable runnable;
    private CountDownTimer mCountDownTimer;
    private boolean isRun = false, isCalled = false;

    public abstract void executeRunnableTask();

    public abstract void onTickRunnable(String sec);

    public void executeThread(){
        handler.post(runnable);
    }

    public void executeDelayedThread(int miliSecond){
        isRun = true;
        handler.postDelayed(runnable, miliSecond);
        mCountDownTimer = null;
        mCountDownTimer = new CountDownTimer(miliSecond, 1000) {
            public void onFinish() {

            }
            public void onTick(long millisUntilFinished) {
                onTickRunnable(String.valueOf(millisUntilFinished / 1000));
            }
        }.start();

    }

    public ThreadRunnableTask(){
        handler = new Handler();
        runnable = new Runnable() {
            public void run() {
                isCalled = true;
                removeRunnableTask();
                executeRunnableTask();
            }
        };
    }

    /**
     * always call this on onPause and onDestroy method
     */
    public void removeRunnableTask() {
        isRun = false;
        isCalled = false;
        if(mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
        mCountDownTimer = null;
        handler.removeCallbacks(runnable);
    }

    public boolean isRun() {
        return isRun;
    }

    public void setRun(boolean run) {
        isRun = run;
    }
}
