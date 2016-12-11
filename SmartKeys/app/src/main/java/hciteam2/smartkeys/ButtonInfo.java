package hciteam2.smartkeys;

/**
 * Created by frankz on 26.11.16.
 */

public class ButtonInfo {
    private float x;
    private float y;

    private String val;

    public ButtonInfo(float x, float y, String symbol){
        this.x = x;
        this.y = y;
        this.val = symbol;
    }

    public void setVal(String val){ this.val = val;}

    public float getX(){return this.x;}
    public float getY(){return this.y;}

    public String getVal(){
        return this.val;
    }
}
