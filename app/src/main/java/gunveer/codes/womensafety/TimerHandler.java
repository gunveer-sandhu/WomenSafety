package gunveer.codes.womensafety;

import android.content.Context;
import android.os.CountDownTimer;
import android.widget.Toast;

public  class TimerHandler extends CountDownTimer {

    String timerLabeler;
    int position;
    Context context;

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    /**
     * @param millisInFuture    The number of millis in the future from the call
     *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
     *                          is called.
     * @param countDownInterval The interval along the way to receive
     *                          {@link #onTick(long)} callbacks.
     */
    public TimerHandler(long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
    }


    @Override
    public void onTick(long millisUntilFinished) {
        try {
            Toast.makeText(context ,"Ticking on Tick", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFinish() {
        try {
            Toast.makeText(context ,"Ticking", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(context, "Try catch", Toast.LENGTH_SHORT).show();
        }
    }


    public String getTimerLabeler() {
        return timerLabeler;
    }

    public void setTimerLabeler(String timerLabeler) {
        this.timerLabeler = timerLabeler;
    }

    public void extras(int position, Context context) {
        this.position = position;
        this.context = context;
    }
}
