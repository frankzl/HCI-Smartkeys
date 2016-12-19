package hciteam2.smartkeys;

/**
 * Created by frankz on 22.11.16.
 */

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.security.Key;


public class ButtonItem extends Button{

    ButtonInfo information;

    private String val;
    private MainActivity widget;

    float center_offsetX;
    float center_offsetY;

    KeyPage page;

    public ButtonItem(float x, float y, String symbol, MainActivity widget, KeyPage page){
        super(widget);
        this.page = page;
        information = new ButtonInfo(x,y,symbol);
        this.val = symbol;
        this.widget = widget;

        Resources resources = widget.getResources();
        super.setBackgroundColor(Color.BLUE);

        this.setText(information.getName()+"");
        this.setHeight(Constants.key_default_height);
        this.setWidth(Constants.key_default_width);
        this.setX(x);
        this.setY(y);
    }

    public ButtonItem(ButtonInfo info, MainActivity widget, KeyPage page){
        super(widget);
        this.page = page;
        this.val = info.getVal();
        this.widget = widget;

        information = info;
        this.setText(info.getName()+"");
        this.setHeight(Constants.key_default_height);
        this.setWidth(Constants.key_default_width);
        this.setX(info.getX());
        this.setY(info.getY());
    }

    public void removeButton(){
        ViewGroup layout = (ViewGroup) this.getParent();

        if(layout != null){
            page.removeKey(this.getInformation());
            layout.removeView(this);
        }
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
    public boolean isDeleting(){ return widget.isDeleting();}

    public String getVal(){
        return information.getVal();
    }

    public void setX(float x){
        super.setX(x-Constants.key_default_width/2+Constants.key_offset_X);
        information.setX(x);
    }
    public void setY(float y){
        super.setY(y-Constants.key_default_height/2+Constants.key_offset_Y);
        information.setY(y);
    }

    public float getX(){ return super.getX()+Constants.key_default_width/2-Constants.key_offset_X; }
    public float getY(){ return super.getY()+Constants.key_default_height/2-Constants.key_offset_Y; }

    public ButtonInfo getInformation(){
        return information;
    }

}