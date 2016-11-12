package GlooKit.GlooFramework.Components;

import GlooKit.GlooFramework.Input;

public interface Drawable {

    void draw(float X, float Y, float W, float H, float Z);

    void calcFrame(double delta, Input input);

    void stepFrame(double delta);

}
