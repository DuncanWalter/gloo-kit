package GlooKit.GlooShaders;

import org.lwjgl.opengl.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import static GlooKit.GlooAPI.GlooBatch.*;

public class Shaders {

    private final static List<BiConsumer<Integer, IntBuffer>> intBufferUniforms = constructIntBufferUniforms();
    private final static List<BiConsumer<Integer, FloatBuffer>> floatBufferUniforms = constructFloatBufferUniforms();

    public static int loadVertexShader(String filename) {
        return loadShader(filename, GL20.GL_VERTEX_SHADER);

    }
    public static int loadFragmentShader(String filename) {
        return loadShader(filename, GL20.GL_FRAGMENT_SHADER);

    }
    public static int loadGeometryShader(String filename) {
        return loadShader(filename, GL32.GL_GEOMETRY_SHADER);

    }

    // takes the file of the shader and the type of shader and returns an ID
    private static int loadShader(String filename, int type) {
        StringBuilder shaderSource = new StringBuilder();
        int shaderID;

        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line;
            while ((line = reader.readLine()) != null) {
                shaderSource.append(line).append("\n");
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("Unable to load in shader file");
            e.printStackTrace();
            System.exit(300);
        }

        shaderID = GL20.glCreateShader(type);
        GL20.glShaderSource(shaderID, shaderSource);
        GL20.glCompileShader(shaderID);

        int status = GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS);

        if (status == GL11.GL_FALSE) {
            System.out.println("Could not compile shader.");
            int logSize = GL20.glGetShaderi(shaderID, GL20.GL_INFO_LOG_LENGTH);
            String log = GL20.glGetShaderInfoLog(shaderID, logSize);
            System.out.println("ShaderError: " + filename + " : " + log);
            System.exit(300);
        }

        return shaderID;
    }


    public static int createShaderProgram(int vertexShaderID, int fragmentShaderID) {
        return createShaderProgram(vertexShaderID, fragmentShaderID, -1);
    }

    public static int createShaderProgram(int vertexShaderID, int fragmentShaderID, int geometryShaderID) {

        // create a shader program and attach both shaders
        int shaderProgramID = GL20.glCreateProgram();
        GL20.glAttachShader(shaderProgramID, vertexShaderID);
        GL20.glAttachShader(shaderProgramID, fragmentShaderID);

        if (geometryShaderID != -1) {
            GL20.glAttachShader(shaderProgramID, geometryShaderID);
        }

        // link and validate program
        GL20.glLinkProgram(shaderProgramID);
        GL20.glValidateProgram(shaderProgramID);

        // check to see if the shaders didn't compile properly
        int errorCheckValue = GL11.glGetError();
        if (errorCheckValue != GL11.GL_NO_ERROR) {
            System.out.println("ERROR - Could not create the shaders:" + GL11.glGetString(GL11.glGetError()));
            System.exit(300);
        }

        // return the shaderProgramID, and the 3 matrixLocations
        return shaderProgramID;
    }

    public static void sendUniformToGPU(int uniformType, int location, IntBuffer values) {
        if(intBufferUniforms.get(uniformType) != null) {
            (intBufferUniforms.get(uniformType)).accept(location, values);
        } else {
//            System.out.println("Unable to send uniform to the GPU.");
            (new Exception("Invalid uniformType for specified values")).printStackTrace();
            System.exit(300);
        }
    }

    public static void sendUniformToGPU(int uniformType, int location, FloatBuffer values) {
        if(floatBufferUniforms.get(uniformType) != null) {
            (floatBufferUniforms.get(uniformType)).accept(location, values);
        } else {
//            System.out.println("Unable to send uniform to the GPU.");
            (new Exception("Invalid uniformType for specified values")).printStackTrace();
            System.exit(300);
        }
    }

    private static List<BiConsumer<Integer, IntBuffer>> constructIntBufferUniforms() {
        List<BiConsumer<Integer, IntBuffer>> uniforms = new ArrayList<>(21);

        for(int i = 0; i < 21; i++) {
            uniforms.add(null);
        }

        uniforms.set(INT_VEC1, GL20::glUniform1iv);
        uniforms.set(INT_VEC2, GL20::glUniform2iv);
        uniforms.set(INT_VEC3, GL20::glUniform3iv);
        uniforms.set(INT_VEC4, GL20::glUniform4iv);
        uniforms.set(UINT_VEC1, GL30::glUniform1uiv);
        uniforms.set(UINT_VEC2, GL30::glUniform2uiv);
        uniforms.set(UINT_VEC3, GL30::glUniform3uiv);
        uniforms.set(UINT_VEC4, GL30::glUniform4uiv);

        return uniforms;
    }

    private static List<BiConsumer<Integer, FloatBuffer>> constructFloatBufferUniforms() {
        List<BiConsumer<Integer, FloatBuffer>> uniforms = new ArrayList<>(21);

        for(int i = 0; i < 21; i++) {
            uniforms.add(null);
        }

        uniforms.set(FLOAT_VEC1, GL20::glUniform1fv);
        uniforms.set(FLOAT_VEC2, GL20::glUniform2fv);
        uniforms.set(FLOAT_VEC3, GL20::glUniform3fv);
        uniforms.set(FLOAT_VEC4, GL20::glUniform4fv);
        uniforms.set(FLOAT_MAT2x2, (Integer location, FloatBuffer values) -> GL20.glUniformMatrix2fv  (location, false, values));
        uniforms.set(FLOAT_MAT2x3, (Integer location, FloatBuffer values) -> GL21.glUniformMatrix2x3fv(location, false, values));
        uniforms.set(FLOAT_MAT2x4, (Integer location, FloatBuffer values) -> GL21.glUniformMatrix2x4fv(location, false, values));
        uniforms.set(FLOAT_MAT3x2, (Integer location, FloatBuffer values) -> GL21.glUniformMatrix3x2fv(location, false, values));
        uniforms.set(FLOAT_MAT3x3, (Integer location, FloatBuffer values) -> GL20.glUniformMatrix3fv  (location, false, values));
        uniforms.set(FLOAT_MAT3x4, (Integer location, FloatBuffer values) -> GL21.glUniformMatrix3x4fv(location, false, values));
        uniforms.set(FLOAT_MAT4x2, (Integer location, FloatBuffer values) -> GL21.glUniformMatrix4x2fv(location, false, values));
        uniforms.set(FLOAT_MAT4x3, (Integer location, FloatBuffer values) -> GL21.glUniformMatrix4x3fv(location, false, values));
        uniforms.set(FLOAT_MAT4x4, (Integer location, FloatBuffer values) -> GL20.glUniformMatrix4fv  (location, false, values));

        return uniforms;
    }
}
