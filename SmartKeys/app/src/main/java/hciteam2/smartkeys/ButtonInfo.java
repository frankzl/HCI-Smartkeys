package hciteam2.smartkeys;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by frankz on 26.11.16.
 */

public class ButtonInfo implements Parcelable, Serializable {
    private float x;
    private float y;

    private String val;
    private String name;

    public ButtonInfo(float x, float y, String symbol){
        this.x = x;
        this.y = y;
        this.val = symbol;
        if(symbol.length() >= 3){
            this.name = val.substring(3);
        }
    }

    public ButtonInfo(String encoded){
        String[]obj = encoded.split(";val:");
        if(obj.length < 4){
            throw new IllegalArgumentException();
        }
        this.x = Float.parseFloat(obj[0]);
        this.y = Float.parseFloat(obj[1]);
        this.val = obj[2];
        this.name = obj[3];
    }

    public String toString(){
        return new StringBuilder().append(x+";val:").append(y+";val:").append(val+";val:").append(name).toString();
    }

    public static final Parcelable.Creator CREATOR
            = new Parcelable.Creator() {
        public ButtonInfo createFromParcel(Parcel in) {
            return new ButtonInfo(in);
        }

        public ButtonInfo[] newArray(int size) {
            return new ButtonInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(this.x);
        dest.writeFloat(this.y);
        dest.writeString(this.val);
        dest.writeString(this.name);
    }

    private ButtonInfo(Parcel in){
        this.x = in.readFloat();
        this.y = in.readFloat();
        this.val = in.readString();
        this.name = in.readString();
    }

    public void setVal(String val){ this.val = val;}
    public void setName(String val){ this.name = val; }

    public String getName(){
        return name;
    }

    public float getX(){return this.x;}
    public void setX(float x){this.x = x;}
    public float getY(){return this.y;}
    public void setY(float y){this.y = y;}

    public String getVal(){
        return this.val;
    }


}
