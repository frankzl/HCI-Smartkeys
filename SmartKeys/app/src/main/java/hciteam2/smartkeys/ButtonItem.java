package hciteam2.smartkeys;

/**
 * Created by frankz on 22.11.16.
 */

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.Button;


public class ButtonItem extends Button{

    ButtonInfo information;

    private String val;
    private MainActivity widget;

    float center_offsetX;
    float center_offsetY;


    public ButtonItem(float x, float y, String symbol, MainActivity widget){
        super(widget);
        information = new ButtonInfo(x,y,symbol);
        this.val = symbol;
        this.widget = widget;

        this.setText(information.getName()+"");
        this.setHeight(Constants.key_default_height);
        this.setWidth(Constants.key_default_width);
        this.setX(x-Constants.key_default_width/2+Constants.key_offset_X);
        this.setY(y-Constants.key_default_height/2+Constants.key_offset_Y);
    }

    public ButtonItem(ButtonInfo info, Activity widget){
        super(widget);
        information = info;
        this.setText(info.getName()+"");
        this.setHeight(Constants.key_default_height);
        this.setWidth(Constants.key_default_width);
        this.setX(info.getX()-Constants.key_default_width/2+Constants.key_offset_X);
        this.setY(info.getY()-Constants.key_default_height/2+Constants.key_offset_Y);
    }

    public void setText(){
        super.setText(information.getName()+"");
    }

    public void setCenter_offset(){
        this.center_offsetX = this.getWidth()/2;
        this.center_offsetY = this.getHeight()/2;
    }

    public float getCenter_offsetX(){
        return center_offsetX;
    }

    public float getCenter_offsetY(){
        return center_offsetY;
    }

    public TCPClient getTcpClient(){
        return widget.getTcpClient();
    }

    public boolean isEditing(){ return widget.isEditing(); }
    public boolean isRearranging() { return widget.isRearranging(); }
    public boolean isScaling() {return widget.isScaling();}

    public String getVal(){
        return information.getVal();
    }

}