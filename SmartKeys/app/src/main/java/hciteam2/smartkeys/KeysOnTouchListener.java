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

    public KeysOnTouchListener(TCPClient tcpClient, ButtonInfo info){
        super();
        this.tcpClient = tcpClient;
        this.info = info;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        System.out.println(event.getAction());
        ButtonItem element = (ButtonItem) v;
        if(element.isEditing()){
            if(event.getAction() == MotionEvent.ACTION_DOWN) {
                RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) v.getLayoutParams();
                initialTouchX = event.getX()-element.getX();
                initialTouchY = event.getY()-element.getY();
            }else if(event.getAction() == MotionEvent.ACTION_MOVE){
                element.setX(event.getX() - initialTouchX);
                element.setY(event.getY() - initialTouchY);
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
