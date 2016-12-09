package hciteam2.smartkeys;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.RelativeLayout;

/**
 * Created by frankz on 27.11.16.
 */

public class KeysOnTouchListener implements OnTouchListener{

    TCPClient tcpClient;
    ButtonInfo info;
    float initialTouchX;
    float initialTouchY;
    float initialRawX;
    float initialRawY;

    public KeysOnTouchListener(TCPClient tcpClient, ButtonInfo info){
        super();
        this.tcpClient = tcpClient;
        this.info = info;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        System.out.println(event.getAction());
        ButtonItem element = (ButtonItem) v;

        if(element.isRearranging()){
            final int X = (int) event.getRawX();
            final int Y = (int) event.getRawY();
            if(event.getAction() == MotionEvent.ACTION_DOWN) {
                RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) v.getLayoutParams();
                initialTouchX = element.getX();
                initialTouchY = element.getY();
                initialRawX = event.getRawX();
                initialRawY = event.getRawY();
            }else if(event.getAction() == MotionEvent.ACTION_MOVE){
                element.setX(initialTouchX + event.getRawX() - initialRawX);
                element.setY(initialTouchY + event.getRawY() - initialRawY);
            }

            return true;
        }
        if(tcpClient == null){
            tcpClient = element.getTcpClient();
        }else{
            if(event.getAction() == MotionEvent.ACTION_DOWN){
                element.getTcpClient().sendMessage("1"+info.getVal()+"");

            }else if(event.getAction() == MotionEvent.ACTION_UP){
                element.getTcpClient().sendMessage("0"+info.getVal()+"");
            }
        }
        return true;
    }
}
