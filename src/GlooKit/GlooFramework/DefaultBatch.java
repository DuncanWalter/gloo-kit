package GlooKit.GlooFramework;

import GlooKit.GlooAPI.*;
import org.lwjgl.opengl.*;

public class DefaultBatch extends GlooBatch {

    private int projMatrixLocation;

    // TODO target for simplification
    public DefaultBatch(GlooApplication app){
        super(app, null);
        describeShaders("src/GlooKit/GlooShaders/", "quadVertex.glsl", "quadFragment.glsl", null);
        projMatrixLocation = describeUniform("projectionMatrix");
    }

    public DefaultVertex createVertex(){
        return new DefaultVertex();

    }

    public void render(){
        // enable depth testing
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        // tell the shader to enable blending
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        // send the projection matrix
        assignUniform(FLOAT_MAT4x4, projMatrixLocation, app().getPanel().toFloatBuffer());
        // send the render request
        super.render(GL11.GL_TRIANGLES);
    }
}
