package GlooKit.GlooFramework;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RowSpan extends KitBit{

    private List<KitBit> children = new ArrayList<>();

    public RowSpan(KitBit... children){
        this(R, B, "1/n+", "1/n+", children);

    }
    public RowSpan(int y, String h, KitBit... children){
        this(R, y, "1/n+", h, children);

    }
    public RowSpan(int x, int y, String w, String h, KitBit... children){
        super(x, y, w, h, children);
        //Collections.addAll(this.children, children);
    }
    public void drawFrame(float X, float Y, float W, float H, float Z){
        super.drawFrame(X, Y, W, H, Z);

        // TODO hidden checks, clipping off-screen draws, fix n finding

        float spanPoints = calculateWidthSpanPoints(getChildren());
        float spanSpace = calculateWidthSpanSpace(getChildren());

        float offset = 0;

        for (KitBit child : getChildren()){

            if(!child.isHidden()){

                float w = calculateW(child, spanPoints, spanSpace);
                float h = calculateH(child);
                float y = calculateY(child, h);

                if (child.isWSpaced()){
                    child.drawFrame(X + offset + getSpacing(), y, w, h, Z);
                } else {
                    child.drawFrame(X + offset, y, w, h, Z);
                }

                offset += child.isWSpaced() ? w + getSpacing() : w - getSpacing();

            }
        }
    }
}
