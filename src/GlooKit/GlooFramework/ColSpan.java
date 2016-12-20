package GlooKit.GlooFramework;

import java.util.ArrayList;
import java.util.List;

public class ColSpan extends KitBit{

    // TODO make this draw top to bottom (currently backwards)

    private List<KitBit> children = new ArrayList<>();

    public ColSpan(KitBit... children){
        this(R, B, "1/n+", "1/n+", children);

    }
    public ColSpan(int x, String w, KitBit... children){
        this(x, B, w, "1/n+", children);

    }
    public ColSpan(int x, int y, String w, String h, KitBit... children){
        super(x, y, w, h, children);
        //Collections.addAll(this.children, children);

    }
    public void drawFrame(float X, float Y, float W, float H, float Z){

        super.drawFrame(X, Y, W, H, Z);

        float spanPoints = calculateHeightSpanPoints(getChildren());
        float spanSpace = calculateHeightSpanSpace(getChildren());

        float offset = H;

        for(KitBit child : getChildren()){

            if(!child.isHidden()){

                float w = calculateW(child);
                float h = calculateH(child, spanPoints, spanSpace);
                float x = calculateX(child, w);

                if (child.isHSpaced()){
                    child.drawFrame(x, Y + offset - h - getSpacing(), w, h, Z);
                } else {
                    child.drawFrame(x, Y + offset - h, w, h, Z);
                }

                offset -= child.isHSpaced() ? h + getSpacing() : h - getSpacing();

            }
        }
    }
}
