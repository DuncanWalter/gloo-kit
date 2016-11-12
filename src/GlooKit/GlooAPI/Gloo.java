package GlooKit.GlooAPI;

//import java.awt.*;


public class Gloo {

    /**
     *
     * This class is totally dead. It does, however, contain many of the functions that have
     * to be called for Open GL. As such, it remains here as a ghost for the time being.
     *
     * ... Its Spirit Passes On ....
     * */



//    // TODO this needs to be broken into about 5 pieces
//
//    /** This is the lowest-level graphics engine. It is designed to be accessed through the higher-level GlooKit.
//     *
//     * The Gloo works directly with GlooAPI (well, LWJGL) to draw things to the screen.
//     *
//     * Any questions about the function or general scope of this should be sent to Eli (E3LS).
//     * He might know what is going on.
//     * */
//
//
//    public static ArrayList<DrawingObject> DrawingObjects = new ArrayList<>(); // each of these must be initialized to empty
//    public static ArrayList<Quad> DrawingQuads = new ArrayList<>();
//
//
//    protected static Vector3f cameraPos; // TODO addBatch function to move cameraPos. Maybe take our Vector class (+ zoom) and just redo the viewMatrix?
//    protected static Matrix4f projectionMatrix;
//    protected static Matrix4f viewMatrix;
//
//    protected static Matrix4f screenCoordinateProjectionMatrix; // this one is for use with object we "draw directly on the screen"
//
//    protected static FloatBuffer matrix44buffer; // FloatBuffer used to pass a 4x4 matrix on to the GPU
//
//
//    public static GlooFontFamily Font;
//    protected static GlooFontFamily TimesNewRoman;
//    protected static GlooFontFamily Arial;
//
//    protected static float ppi;
//
//    /* Default VAO stuff*/
//
//    protected static int VAOID;
//    protected static int verticesVBOID;
//    protected static int indicesVBOID;
//
//    protected static int shaderProgramID; // these will probably all be made into arrays eventually
//    protected static int vertexShaderID;
//    protected static int fragmentShaderID;
//
//    protected static int projectionMatrixLocation; // These are attached to the shader program (used for uniforms)
//    protected static int viewMatrixLocation;
//    protected static int modelMatrixLocation;
//
//    protected static int quadShaderProgramID; // these are specifically for drawing quads
//    protected static int quadVertexShaderID;
//    protected static int quadFragmentShaderID;
//
//    protected static int quadProjectionMatrixLocation;
//
//    protected static int textShaderProgramID; // these are specifically for drawing text on the fly (and aren't used right now)
//    protected static int textVertexShaderID;
//    protected static int textFragmentShaderID;
//
//    protected static int textProjectionMatrixLocation; // These are attached to the shader program (used for uniforms)
//    protected static int textViewMatrixLocation;
//    protected static int textModelMatrixLocation;
//    protected static int textColorLocation;
//
//    protected static int[] textureIDs;
//    protected static int textureSelector = 0; // indexer for the textureIDs
//
//
//    // render buffer stuff
//    protected static int renderBufferID;
//    protected static int frameBufferID;
//    protected static int textureBufferID;
//
//    protected static ByteBuffer textureByteBuffer;
//
//    /* Text pre-renderer VAO stuff */
//
//    protected static int textPrerendererVAOID;
//    protected static int textPrerendererVerticesVBOID;
//
//    protected static int textPrerendererShaderProgramID; // these are specifically for pre-rendering text
//    protected static int textPrerendererVertexShaderID;
//    protected static int textPrerendererFragmentShaderID;
//
//    protected static int textPrerendererColorLocation; // uniforms
//
//    // render buffer stuff
//    protected static int textPrerendererFrameBufferID;
//
//
//    /* Post-processor VAO stuff */
//
//    protected static int postProcessorVAOID;
//    protected static int postProcessorVBOID;
//
//    protected static int postProcessorShaderProgramID; // this set are for drawing the entire screen to a single quad (which allows us to do post-processing effects)
//    protected static int postProcessorVertexShaderID;
//    protected static int postProcessorFragmentShaderID;
//
//    protected static FloatBuffer postProcessorVerticesFloatBuffer;
//
//
//    public static void setupAll (boolean fullscreen, boolean diagnostic) {
//        setupOpenGL(fullscreen, diagnostic); // setup GlooAPI and the window itself
//        setupBufferObjects(); // setup the various buffer objects
//        setupShaders(); // setup the shaders
//        setupTextures(); // setup the textures and fonts
//        setupMatrices(); // setup the transformation matrices
//    }
//
//    public static void setupOpenGL (boolean fullscreen, boolean diagnostic) {
//
//        /* Create the display */
//        try {
//
////            PixelFormat pixelFormat = new PixelFormat(0, 8, 0, 4); // anti-aliasing
//
////            ContextAttribs contextAttributes = new ContextAttribs(3, 2).withForwardCompatible(true).withProfileCore(true); // select version 3.2 core with forward compatibility
//
//            // Set an arbitrary default display pointSize
////            Display.setDisplayMode(new DisplayMode(1500, 850, 32, 60));
//
//            // At this point, the screen is setup, so we can figure out the dpi using the Java Toolkit
//            ppi = Toolkit.getDefaultToolkit().getScreenResolution(); // returns the pixels per inch of the screen
//            System.out.println("Pixels per inch: " + ppi);
//
//            // Flavor!!!
////            Display.setTitle("Hex - Humor: E3LS eXcluded");
//
//            // Set fullscreen according to the boolean above
////            Display.setFullscreen(fullscreen);
//            // Set VSync
//
//            // Actually create the display with these settings
//
//
//        } catch (Exception e) {
//            System.out.println("Unable to create Display");
//            e.printStackTrace();
//            System.exit(-1);
//        }
//
//        /* Gloo actual GlooAPI stuff*/
//
//        // Map the internal Gloo coordinate system to the entire screen
//        GL11.glViewport(0, 0, Display.getDisplayMode().width(), Display.getDisplayMode().height());
//
//    }
//
//    public static void setupBufferObjects() {
//        /* Setup of Gloo VAO's, VBO's, renderBuffers, frameBuffers, etc. */
//
//
//
//        cameraPos = new Vector3f(0, 0, -1); // TODO fix this
//
//
//        /* ------------------- SETUP DEFAULT RENDERER (for first rendering pass) -------------------------------------*/
//
//        // Create and bind our default VAO
//        VAOID = GL30.glGenVertexArrays();
//        GL30.glBindVertexArray(VAOID);
//
//        // Create and bind the default VAO's VBO
//        verticesVBOID = GL15.glGenBuffers();
//        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, verticesVBOID);
//
//        // Setup the VBO with the corresponding attribute pointers
//        // attribute list #, pointSize of element, type of element, isNormalized?, stride (skipping stuff), run spot
//        GL20.glVertexAttribPointer(0, 4, GL11.GL_FLOAT, false, Vertex.sizeInBytes, Vertex.positionByteOffset); // position attribute pointer
//        GL20.glVertexAttribPointer(1, 4, GL11.GL_FLOAT, false, Vertex.sizeInBytes, Vertex.colorByteOffset); // color attribute pointer
//        GL20.glVertexAttribPointer(2, 4, GL11.GL_FLOAT, false, Vertex.sizeInBytes, Vertex.textureByteOffset); // texture attribute pointer
//        GL20.glVertexAttribPointer(3, 4, GL11.GL_FLOAT, false, 4 * 4, 0); // text position
//        GL20.glVertexAttribPointer(4, 4, GL11.GL_FLOAT, false, 4 * 4, 2 * 4); // text texture
//
//        // unbind the VBO
//        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
//
//        // deselect the VAO
//        GL30.glBindVertexArray(0);
//
//        // Create and bind a VBO for the indices (this isn't attached to the VAO (for some reason))
//        indicesVBOID = GL15.glGenBuffers();
//
//        /* ------------------ END SETUP DEFAULT RENDERER -------------------------------------------------------------*/
//
//
//        /* ------------------ SETUP FRAMEBUFFER AND RENDERBUFFER FOR DEFAULT RENDERER --------------------------------*/
//
//        // Setup a new frameBufferObject
//        frameBufferID = GL30.glGenFramebuffers();
//        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, frameBufferID);
//
//        // grab the current display mode to set the renderBuffer and textureBuffer pointSize
//        DisplayMode screen = Display.getDisplayMode();
//
//        // Setup the textureBuffer ( this is a texture that holds the colors of the entire screen )
//        textureBufferID = GL11.glGenTextures();
//        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureBufferID);
//
//        // allocate the memory for the texture, but don't fill it
//        textureByteBuffer = BufferUtils.createByteBuffer(3*screen.width()*screen.height()); // RGB * width * height (This appears to not be necessary, but I'm keeping it)
//        // target, level, internalformat, width, height, border, format, type, data pointer
//        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, screen.width(), screen.height(), 0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, textureByteBuffer);
//        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
//        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
//        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0); // unbind texture
//
//        // attach the texture to the frameBuffer
//        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, textureBufferID, 0);
//
//        // Setup a renderBufferObject (this is used to draw the entire screen, which then gets copied into a single quad
//        renderBufferID = GL30.glGenRenderbuffers();
//
//        // bind the renderBuffer to allocate memory for it
//        GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, renderBufferID);
//        GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL30.GL_DEPTH24_STENCIL8, screen.width(), screen.height()); // target, internal format, screenwidth, screenheight
//        GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, 0); // unbind render buffer
//
//        // attach the renderBuffer to the frameBuffer
//        GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_STENCIL_ATTACHMENT, GL30.GL_RENDERBUFFER, renderBufferID);
//
//        // check to make sure the renderBuffer is complete
//        if (GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) != GL30.GL_FRAMEBUFFER_COMPLETE) {
//            System.out.println("Framebuffer (renderBuffer) is not complete!");
//            System.exit(-1);
//        }
//
//        // unbind the frameBuffer
//        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
//
//        /* ----------------- END SETUP FRAMEBUFFER AND RENDERBUFFER FOR DEFAULT RENDERER -----------------------------*/
//
//
//        /* ----------------- SETUP TEXTPRERENDERER -------------------------------------------------------------------*/
//
//        // Create and bind our VAO
//        textPrerendererVAOID = GL30.glGenVertexArrays();
//        GL30.glBindVertexArray(textPrerendererVAOID);
//
//        // Create and bind the VAO's VBO
//        textPrerendererVerticesVBOID = GL15.glGenBuffers();
//        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, textPrerendererVerticesVBOID);
//
//        GL20.glVertexAttribPointer(0, 4, GL11.GL_FLOAT, false, 4 * 4, 0); // text position
//        GL20.glVertexAttribPointer(1, 4, GL11.GL_FLOAT, false, 4 * 4, 2 * 4); // text texture
//
//        // unbind the VBO
//        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
//
//        // deselect the VAO
//        GL30.glBindVertexArray(0);
//
//        // no creation of an indices VBO
//
//        /* ----------------- END SETUP TEXTPRERENDERER ---------------------------------------------------------------*/
//
//
//        /* ----------------- SETUP FRAMEBUFFER FOR TEXTPRERENDERER ----------------------------------*/
//
//        // Setup a new frameBufferObject
//        textPrerendererFrameBufferID = GL30.glGenFramebuffers();
//        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, textPrerendererFrameBufferID);
//
//        // unbind the frameBuffer
//        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
//
//        // This is all we can do here. The rest of the setup has to be done when we prerender
//
//        /* ----------------- END SETUP FRAMEBUFFER FOR TEXTPRERENDERER ------------------------------*/
//
//
//        /* ----------------- SETUP POST-PROCESSOR RENDERER (for second rendering pass) -------------------------------*/
//
//        // Setup the post-processor VAO
//        postProcessorVAOID = GL30.glGenVertexArrays(); // create pp vao
//        GL30.glBindVertexArray(postProcessorVAOID); // bind pp vao
//
//        // the post-processor only draws a single quad to the screen, so we are actually going to just use a single VBO (no index array)
//        float[] postProcessorQuad = new float[] {
//            -1.0f,  1.0f, 0.0f, 0.0f, // x, y, s, t
//            -1.0f, -1.0f, 0.0f, 1.0f,
//             1.0f, -1.0f, 1.0f, 1.0f, // this is the first triangle
//             1.0f, -1.0f, 1.0f, 1.0f,
//             1.0f,  1.0f, 1.0f, 0.0f,
//            -1.0f,  1.0f, 0.0f, 0.0f  // this is the second triangle
//        };
//
//        postProcessorVerticesFloatBuffer = BufferUtils.createFloatBuffer(postProcessorQuad.length); // create a float buffer
//        postProcessorVerticesFloatBuffer.put(postProcessorQuad); // put the vertices in there
//        postProcessorVerticesFloatBuffer.flip(); // flip buffer
//
//        // create and fill VBO
//        postProcessorVBOID = GL15.glGenBuffers();
//        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, postProcessorVBOID); // bind to the array buffer
//        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, postProcessorVerticesFloatBuffer, GL15.GL_STREAM_DRAW); // buffer to bind to, data to bind, type of data
//
//        // create array attributes
//        // attribute list #, pointSize of element, type of element, isNormalized?, stride (skipping stuff), run spot
//        GL20.glVertexAttribPointer(0,4, GL11.GL_FLOAT, false, 4*4, 0); // position attribute pointer
//        GL20.glVertexAttribPointer(1, 4, GL11.GL_FLOAT, false, 4*4, 2*4); // color attribute pointer
//        // These are hard-coded numbers; A float is 4 bytes and there are 4 floats per point.
//        // The offset on the color is the pointSize in bytes of the position, 2 floats * 4 bytes/float
//
//        // unbind VBO and VAO
//        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
//        GL30.glBindVertexArray(0);
//
//        /* ------------------- END SETUP POST-PROCESSOR RENDERER -----------------------------------------------------*/
//
//    }
//
//    public static void setupShaders() {
//
//
//        /* --------------------- SETUP DEFAULT SHADER PROGRAM (for first rendering pass) -----------------------------*/
//
//        // load the vertex shader
//        vertexShaderID = GlooShaders.loadShader("src/GlooKit/GlooShaders/vertex.glsl", GL20.GL_VERTEX_SHADER);
//        // load the fragment shader
//        fragmentShaderID = GlooShaders.loadShader("src/GlooKit/GlooShaders/fragment.glsl", GL20.GL_FRAGMENT_SHADER);
//
//        shaderProgramID = GlooShaders.createShaderProgram(vertexShaderID, fragmentShaderID);
//
//        // setup uniform locations
//        projectionMatrixLocation = GL20.glGetUniformLocation(shaderProgramID, "projectionMatrix");
//        viewMatrixLocation = GL20.glGetUniformLocation(shaderProgramID, "viewMatrix");
//        modelMatrixLocation = GL20.glGetUniformLocation(shaderProgramID, "modelMatrix");
//
//
//        /* --------------------- END SETUP DEFAULT SHADER PROGRAM ----------------------------------------------------*/
//
//
//        /* --------------------- SETUP QUAD SHADER PROGRAM -----------------------------------------------------------*/
//
//        // load the vertex shader
//        quadVertexShaderID = GlooShaders.loadShader("src/GlooKit/GlooShaders/quadVertex.glsl", GL20.GL_VERTEX_SHADER);
//        // load the fragment shader
//        quadFragmentShaderID = GlooShaders.loadShader("src/GlooKit/GlooShaders/quadFragment.glsl", GL20.GL_FRAGMENT_SHADER);
//
//        quadShaderProgramID = GlooShaders.createShaderProgram(quadVertexShaderID, quadFragmentShaderID);
//
//        // we still need to pass the projection matrix in (it fixes the directions of the +y and +z axes)
//        quadProjectionMatrixLocation = GL20.glGetUniformLocation(quadShaderProgramID, "projectionMatrix");
//
//        /* --------------------- END SETUP QUAD SHADER PROGRAM -------------------------------------------------------*/
//
//
//        /* --------------------- SETUP TEXT PRERENDERER SHADER PROGRAM -----------------------------------------------*/
//
//        // load the vertex and fragment shaders
//        textPrerendererVertexShaderID = GlooShaders.loadShader("src/GlooKit/GlooShaders/textPrerendererVertex.glsl", GL20.GL_VERTEX_SHADER);
//        textPrerendererFragmentShaderID = GlooShaders.loadShader("src/GlooKit/GlooShaders/textPrerendererFragment.glsl", GL20.GL_FRAGMENT_SHADER);
//
//        textPrerendererShaderProgramID = GlooShaders.createShaderProgram(textPrerendererVertexShaderID, textPrerendererFragmentShaderID);
//
//        // setup uniform locations
//        textPrerendererColorLocation = GL20.glGetUniformLocation(textPrerendererShaderProgramID, "textColor");
//
//        /* --------------------- END SETUP TEXT PRERENDERER SHADER PROGRAM -------------------------------------------*/
//
//
//        /* --------------------- SETUP TEXT SHADER PROGRAM -----------------------------------------------------------*/
//
//        // TODO consider deleting this program (we don't have much need to render text on-the-fly)
//
//        // Setup the text shader program
//        textVertexShaderID = GlooShaders.loadShader("src/GlooKit/GlooShaders/textVertex.glsl", GL20.GL_VERTEX_SHADER);
//        textFragmentShaderID = GlooShaders.loadShader("src/GlooKit/GlooShaders/textFragment.glsl", GL20.GL_FRAGMENT_SHADER);
//
//        textShaderProgramID = GlooShaders.createShaderProgram(textVertexShaderID, textFragmentShaderID);
//
//        // setup uniform locations
//        textProjectionMatrixLocation = GL20.glGetUniformLocation(textShaderProgramID, "projectionMatrix");
//        textViewMatrixLocation = GL20.glGetUniformLocation(textShaderProgramID, "viewMatrix");
//        textModelMatrixLocation = GL20.glGetUniformLocation(textShaderProgramID, "modelMatrix");
//        textColorLocation = GL20.glGetUniformLocation(textShaderProgramID, "textColor");
//
//        /* --------------------- END SETUP TEXT SHADER PROGRAM -------------------------------------------------------*/
//
//
//        /* --------------------- SETUP POST-PROCESSOR SHADER PROGRAM -------------------------------------------------*/
//
//        // Setup the post-processor shader program
//        postProcessorVertexShaderID = GlooShaders.loadShader("src/GlooKit/GlooShaders/postProcessorVertex.glsl", GL20.GL_VERTEX_SHADER);
//        postProcessorFragmentShaderID = GlooShaders.loadShader("src/GlooKit/GlooShaders/postProcessorFragment.glsl", GL20.GL_FRAGMENT_SHADER);
//
//        postProcessorShaderProgramID = GlooShaders.createShaderProgram(postProcessorVertexShaderID, postProcessorFragmentShaderID);
//
//        // we don't actually care about the uniform locations because there aren't any
//
//        /* --------------------- END SETUP POST-PROCESSOR SHADER PROGRAM ---------------------------------------------*/
//
//        int err = GL11.glGetError();
//        if (err != GL11.GL_NO_ERROR) {
//            System.out.println("Error in shaders: " + err);
//        }
//
//    }
//
//    public static void setupTextures() {
//        /* This only loads textures that can be pre-loaded. There is also a special texture loaded above for the render buffer*/
//
//        // load the images into the game
//        textureIDs = new int[3];
//        textureIDs[0] = FileLoader.loadPNGTexture("assets/sprite-sheet.png", GL13.GL_TEXTURE0);
//        textureIDs[1] = FileLoader.loadPNGTexture("assets/Dragon-Gold-Portrait.png", GL13.GL_TEXTURE0);
//        textureIDs[2] = FileLoader.loadPNGTexture("assets/BG.png", GL13.GL_TEXTURE0);
//
//        // Also, we load the font textures here (And setup the entire font)
//
//        Font = FileLoader.loadFontFromFile("assets/fonts/basic_sans_serif_7.ttf", 48);
////        TimesNewRoman = FileLoader.loadDefaultFont("Times New Roman", 48);
////        Arial = FileLoader.loadDefaultFont("Arial", 48);
//
//        /*
//        * TODO
//        * TODO Look for ways to speed up the process of loading fonts... it's pretty darn slow
//        * TODO
//        * */
//
//
//        int err = GL11.glGetError();
//        if (err != GL11.GL_NO_ERROR) {
//            System.out.println("Error in textures: " + err);
//        }
//
//    }
//
//    public static void setupMatrices() {
//
//
//        /* Top and Side View
//        * __________________    <_____ z far
//        *  \        |        /             |
//        *   \  fov/2|       /              |
//        *    \  (φ) |      /               |
//        *     \     |     /                |
//        *      \____|____/     <____ z near|
//        *       \XXX|XXX/               |  |
//        *        \XX|XX/                |  |
//        *         \X|X/                 |  |
//        *          \|/          ________|__|
//        *
//        * This is the frustrum of the camera. ( Its actually a rectangular pyramid with the top cut off )
//        *
//        * Our matrix for the projection of the camera is:
//        *
//        *   cot(φ)/a    0      0       0
//        *       0     cot(φ)   0       0
//        *       0       0   -zp/zm  -(2*zfar*znear)/zm
//        *       0       0     -1       0
//        *
//        * Where a   = aspect ratio (Width/Height of screen)
//        *       fov = Field of View (φ)
//        *       zm  = zfar - znear
//        *       zp  = zfar + znear
//        * */
//
//        /*
//        *  We have three matrices that help us transform an object to the correct location on the screen
//        *
//        *  The projection matrix transforms the view of the camera in the shape of the frustrum
//        *  The view matrix defines the position and location of the camera
//        *  Each model matrix defines the position, location, and scale of a model
//        *
//        *  For a given model: (do this in the shader)
//        *
//        *  gl_position = projectionMatrix * viewMatrix * modelMatrix * in_Position (the vertices defined in the VBO)
//        * */
//
//        // Gloo projection matrix
//        projectionMatrix = new OVRMatrix4f("asd");
//        projectionMatrix.setZero(); // set the projectionMatrix to the zero matrix
//        float FOV = (float) (Math.PI / 180) * 60; // 60° to radians
//        float aspectRatio = (float) Display.getDisplayMode().width() / Display.getDisplayMode().height(); // (WIDTH/HEIGHT)
//        float nearPlane = 0.1f;
//        float farPlane = 1.1f;
//
//        // See above for math
//        projectionMatrix.m00 = (float) (1f/(Math.tan(FOV / 2f))) / aspectRatio;
//        projectionMatrix.m11 = (float) (1f/Math.tan(FOV / 2f));
//        projectionMatrix.m22 = -(farPlane + nearPlane)/(farPlane - nearPlane);
//        projectionMatrix.m23 = -1;
//        projectionMatrix.m32 = -(2f * farPlane * nearPlane)/(farPlane - nearPlane);
//
//        /*
//        * We also have a special matrix that defines the transformation from "screen coords" to normalized device coordinates ("GL coords")
//        *
//        * Essentially, we do a rescaling so that our x,y,z end up from 0.0 to 2.0
//        *
//        * Then we do a translation so that the center of the screen becomes 0,0,0
//        *
//        * */
//
//        screenCoordinateProjectionMatrix = new Matrix4f();
//
//        Matrix4f screenScaleMatrix = new Matrix4f();
//        screenScaleMatrix.m00 = 2f/Display.width(); // scale by the screen width
//        screenScaleMatrix.m11 = 2f/Display.height(); // flip the y and scale by the screen height
//        screenScaleMatrix.m22 = 1f;
//
//        Matrix4f screenTranslateMatrix = new Matrix4f();
//        screenTranslateMatrix.m30 = -1f; // translate 1.0 to the left
//        screenTranslateMatrix.m31 = -1f; // translate 1.0 down
//        // z should already be fine
//
//        screenCoordinateProjectionMatrix = Matrix4f.mul(screenTranslateMatrix, screenScaleMatrix, screenCoordinateProjectionMatrix);
//
//
//
//        // Gloo view matrix
//        viewMatrix = new Matrix4f();
//
//        // Gloo model matrix
//        //modelMatrix = new Matrix4f(); // TODO move this to each object
//
//        // Gloo a float buffer to hold a matrix
//        matrix44buffer = BufferUtils.createFloatBuffer(16);
//
//    }
//
//
//
////    public static void createDrawingObjects() {
////        // This is a temporary function that simply holds the objects for us that we draw in the GlooAPI test
////
////        /*TODO This section should get cut at some point */
////        Quad[] quads = new Quad[1];
////        for (int i = 0; i < quads.length; i++) {
////
////            float x = (float) (0);
////            float y = (float) (0);
////            float sizeX = (Display.width() / (i+1f) );
////            float sizeY = (float) (Display.height());
////
////            quads[i] = new Quad(x, y, sizeX, sizeY);
////            /*
////            for (int j = 0; j < quads[i].verticesCount; j++) {
////                float x = (float) (Math.random() * 2.0 - 1.0);
////                float y = (float) (Math.random() * 2.0 - 1.0);
////                float z = (float) 0;
////                quads[i].vertices[j].setXYZ(x, y, z);
////            }*/
////
////            float r = (float) (Math.PI* Math.random()*2.0);
////            int s = (int) Math.floor(Math.random()*4.999);
////            quads[i].setTexture(textureIDs[0], s, 0);
//////            quads[i].setZ( (float) i / 30f);
//////            quads[i].setModelScale((i+1)/6f, (i+1)/6f, 1);
//////            quads[i].setModelPos(x,y,-((float) (i))/10f); // each of the smaller ones is in front of the larger //TODO check and make sure z is in the correct direction
//////            quads[i].setModelAngle(0f,0f,1f,r); // rotate by r around z-axis
////            DrawingQuads.addBatch(quads[i]);
////        }
////        float x = (float) (Display.width() / 4);
////        float y = (float) (Display.height() / 4);
////        float sizeX = (Display.width()  / 6f);
////        float sizeY = (Display.height() / 4f);
////        float[] color = new float[] {0.0f, 0.0f, 1.0f};
////        TextQuad tq = new TextQuad(x,y,sizeX,sizeY);
////        tq.initText(Font, "Test of finality\nI sure hope this works\nHow many lines?\nUp to 4?\nHow 'bout 5?\nMore?\n7\n8\n9\n10\n11\n12\n13\n14\n15\n16\n17\n18\n19\n20\n21\n22\n23\n24\n25\n26\n27\n28\n29\n30", 12, color);
//////        for (int i = 0; i < tq.verticesCount; i++) {
//////            tq.vertices[i].setZ(0.5f);
//////        }
////        DrawingQuads.addBatch(tq);
////        /* End of Section to cut*/
////    }
//
//    // TODO maybe move these to someplace where they'd make more sense
//    public static float pointsToPixels(float points) {
//        // points to inch (/72)
//        // inch to pixel (*ppi)
//        return points/72f*(ppi);
//    }
//    public static float pixelsToPoints(float pixels) {
//        // pixels to inch (/ppi)
//        // inch to points (*72)
//        return pixels/ppi*72f;
//    }
//
//
//    public static int preRenderText(GlooFontFamily font, String string, float width, float height, float textHeight, float[] rgb) {
//
//        /* Setup a new texture to attach to the framebuffer */
//        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, textPrerendererFrameBufferID);
//
//        // Setup the textureBuffer ( this is a texture that will hold the final image and gets returned)
//        int finalTexture = GL11.glGenTextures();
//        GL11.glBindTexture(GL11.GL_TEXTURE_2D, finalTexture);
//
//        // allocate the memory for the texture, but don't fill it
//        ByteBuffer textureByteBuffer2 = BufferUtils.createByteBuffer( (int) (4*width*height) ); // RGBA * width * height (This appears to not be necessary, but I'm keeping it)
//        // target, level, internalformat, width, height, border, format, type, data pointer
//        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, (int) width, (int) height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, textureByteBuffer2);
//        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
//        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
//        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0); // unbind texture
//
//        // attach the texture to the frameBuffer
//        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, finalTexture, 0);
//
//        // check to make sure the renderBuffer is complete
//        int status = GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER);
//        if (status != GL30.GL_FRAMEBUFFER_COMPLETE) {
//            System.out.println("Framebuffer for text prerenderer is not complete!");
//            (new Exception()).printStackTrace();
//            System.exit(-1);
//        }
//
//        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
//        /* End setup new texture */
//
//        /* Actually do the rendering */
//
//        // first, we bind the framebuffer and do the clearing of stuff
//        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, textPrerendererFrameBufferID);
//
//        GL11.glClearColor(0.8f, 0.6f, 0.4f, 0f);
//        // clear the color buffer (draw the background over the screen)
//        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
//        // enable depth testing
//        GL11.glEnable(GL11.GL_DEPTH_TEST);
//
//        // then, we bind the appropriate shader program
//        GL20.glUseProgram(textPrerendererShaderProgramID);
//        // And the VAO
//        GL30.glBindVertexArray(textPrerendererVAOID);
//        GL20.glEnableVertexAttribArray(0); // enable the text position attrib pointer
//        GL20.glEnableVertexAttribArray(1); // enable the text texture position attrib pointer
//
//        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, textPrerendererVerticesVBOID);
//
//        // next, we set the color of the text
//        GL20.glUniform3f(textPrerendererColorLocation, rgb[0], rgb[1], rgb[2]);
//
//        // so, width and height will give us the pointSize of the entire textbox in pixels...
//        // We want text to always be the same number of pixels tall. This is textHeight
//
//        // 1.0f / font.getSize2D() gets us a texture that is half the pointSize of the screen per letter
//        // desired pixels tall * 2 * scale / Display.height
//        //float scale = 1.0f / font.getSize2D();
//        //float scale = textHeight * 2f / font.font.getSize2D() / Display.height();
//        //float scale = pointsToPixels(textHeight);
//
//        // I'm gonna leave that ^ there because we may need it in the future (in case I got something wrong)
//
//        float scale = pointsToPixels(textHeight) / font.font.getSize2D() / Display.height(); // I think this is right...
//
//        // We assume the run of the text is located in the upper-left of the textbox
//        float x = -1f + font.characters.get( string.charAt(0) ).pointSize.y / 4 * scale;
//        float y = -1f - font.characters.get( string.charAt(0) ).pointSize.y / 2 * scale;
//
//        int err = GL11.glGetError();
//        if (err != GL11.GL_NO_ERROR) {
//            System.out.println("Error in prerender text setup: " + err);
//        }
//
//        //System.out.println(string.length());
//
//        for (int i = 0; i < string.length(); i++) {
//
//            GlooFontFamily.FontCharacter ch = font.characters.get( string.charAt(i) );
//
//            if (ch == null) {
//                // if this is not an available character, then use the '\0' character
//                ch = font.characters.get('\0');
//            }
//
//            float xpos = x + ch.bearing.x * scale;
//            float ypos = y + (ch.pointSize.y - ch.bearing.y) * scale;
//
//            float charWidth = ch.pointSize.x * scale;
//            float charHeight = ch.pointSize.y * scale;
//
//            if (string.charAt(i) == '\n') {
//                // if this is the newline character, then drop down one line and skip this character
//                x = -1f + font.characters.get( string.charAt(0) ).pointSize.y / 4 * scale;
//                y += charHeight * 3f/2f;
//                continue;
//            }
//
//            float[] vertices = new float[] {
//                    xpos, ypos + charHeight,  0.0f, 1.0f, // x, y, s, t
//                    xpos, ypos, 0.0f, 0.0f,
//                    xpos + charWidth, ypos, 1.0f, 0.0f,
//                    xpos + charWidth, ypos, 1.0f, 0.0f,
//                    xpos + charWidth, ypos + charHeight, 1.0f, 1.0f,
//                    xpos, ypos + charHeight, 0.0f, 1.0f
//            };
//
//            FloatBuffer verticesFloatBuffer = BufferUtils.createFloatBuffer(vertices.length);
//            verticesFloatBuffer.put(vertices);
//            verticesFloatBuffer.flip();
//
//            err = GL11.glGetError();
//            if (err != GL11.GL_NO_ERROR) {
//                System.out.println("Error in prerender text vertex[] setup: " + err);
//            }
//
//
//            // update VBO
//            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesFloatBuffer, GL15.GL_STREAM_DRAW);
//
//            // bind character textures
//            GL11.glBindTexture(GL11.GL_TEXTURE_2D, ch.textureID);
//
//            err = GL11.glGetError();
//            if (err != GL11.GL_NO_ERROR) {
//                System.out.println("Error in prerender text texture setup: " + err);
//            }
//
//            //GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
//
//            // Draw triangles
//            GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 6);
//
//            //GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
//
//            err = GL11.glGetError();
//            if (err != GL11.GL_NO_ERROR) {
//                System.out.println("Error in prerender text drawing: " + err);
//            }
//
//            // advance
//            x += ch.advance * scale;
//
//        }
//
//        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
//
//        GL20.glDisableVertexAttribArray(0); // disable text position
//        GL20.glDisableVertexAttribArray(1); // disable text texture position
//        GL30.glBindVertexArray(0); // unbind vertex array
//        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0); // unbind texture
//
//        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0); // unbind the framebuffer
//
//        return finalTexture;
//    }
//
//
//
//    // TODO figure out if we even want to keep this at all
//    public static void updateCycle() {
//
//
//
//        // -------- Updating matrices
//
//        // Reset view and model
//        viewMatrix = new Matrix4f();
//        //modelMatrix = new Matrix4f();
//
//        // Translate camera
//        Matrix4f.translate(cameraPos, viewMatrix, viewMatrix);
//
//        /*
//        // Scale, translate and rotate model
//        Matrix4f.scale(modelScale, modelMatrix, modelMatrix); // scale the model first
//        Matrix4f.translate(modelPos, modelMatrix, modelMatrix); // then translate
//        Matrix4f.rotate(modelAngle.z, new Vector3f(0,0,1), modelMatrix, modelMatrix); // rotate along the z-axis
//        Matrix4f.rotate(modelAngle.y, new Vector3f(0,1,0), modelMatrix, modelMatrix); // along y-axis
//        Matrix4f.rotate(modelAngle.x, new Vector3f(1,0,0), modelMatrix, modelMatrix); // along x-axis
//        */
//
//        // Upload matrices to the shader program
//        GL20.glUseProgram(shaderProgramID);
//
//        projectionMatrix.store(matrix44buffer); // projectionMatrix
//        matrix44buffer.flip();
//        GL20.glUniformMatrix4(projectionMatrixLocation, false, matrix44buffer);
//
//        viewMatrix.store(matrix44buffer); // viewMatrix
//        matrix44buffer.flip();
//        GL20.glUniformMatrix4(viewMatrixLocation, false, matrix44buffer);
//
//        /*
//        modelMatrix.store(matrix44buffer); // modelMatrix
//        matrix44buffer.flip();
//        GL20.glUniformMatrix4(modelMatrixLocation, false, matrix44buffer);
//        */
//
//        GL20.glUseProgram(0);
//    }
//
//    public static void renderCycle() {
//        /* Rendering */
//
//        // The new rendering style is to render the entire screen to a frameBuffer (with a renderBuffer attached)
//        // Afterward, we render that frameBuffer to a single quad which spans the whole screen
//
//        // TODO deal with blending on drawing textquads
//
//        GL11.glClearColor(0.4f, 0.6f, 0.9f, 0f);
//        // clear the color buffer (draw the background over the screen)
//        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT);
//        // enable depth testing
//        GL11.glEnable(GL11.GL_DEPTH_TEST);
//        // Bind the default shader
//        GL20.glUseProgram(quadShaderProgramID);
//        // Bind the texture
//        GL13.glActiveTexture(GL13.GL_TEXTURE0);
//        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureIDs[textureSelector]);
//        // tell the shader to enable blending
//        GL11.glEnable(GL11.GL_BLEND);
//        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
//        // send the projection matrix
//        screenCoordinateProjectionMatrix.store(matrix44buffer); // projectionMatrix
//        matrix44buffer.flip();
//        GL20.glUniformMatrix4(quadProjectionMatrixLocation, false, matrix44buffer);
//        // bind to the default VAO and enable the standard VertexAttrib pointers
//        GL30.glBindVertexArray(VAOID);
//        GL20.glEnableVertexAttribArray(0); // select the vertex attributes array from earlier for position
//        GL20.glEnableVertexAttribArray(1); // and the one for color
//        GL20.glEnableVertexAttribArray(2); // and the one for texture
//        // Bind to the vertices VBO
//        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, verticesVBOID);
//        // Bind to the indices VBO
//        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesVBOID);
//
//        List<Vertex> bufferVertexes = new ArrayList<>();
//        List<Short> bufferIndices = new ArrayList<>();
//
////        for(int i = DrawingObject.drawQueue.pointSize(); i > 0; i--){
////            DrawingObject o = DrawingObject.drawQueue.get(i-1);
////            if(!o.vertices.get(0).alphaFlag /*o is opaque*/){
////                for(Short j : o.indices){
////                    bufferIndices.addBatch((short)(bufferVertexes.pointSize() + j));
////                }
////                bufferVertexes.addAll(o.vertices);
////            }
////            DrawingObject.drawQueue.set(i-1, null);
////        }
////        for(DrawingObject o : DrawingObject.drawQueue){
////            if(o != null){
////                for(Short j : o.indices){
////                    bufferIndices.addBatch((short)(bufferVertexes.pointSize() + j));
////                }
////                bufferVertexes.addAll(o.vertices);
////            }
////        }
////
////        DrawingObject.drawQueue.clear();
//        // first, we need to stream the information for each object to the gpu
//        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(bufferVertexes.pointSize() * Vertex.sizeInBytes);
//        for(Vertex v: bufferVertexes){
//            vertexBuffer.put(v.getElements()); // put each of the elements of each vertex of the object in the buffer
//            if(v.burryFlag){v.burry();}
//        }
//        vertexBuffer.flip();
//        ShortBuffer indexBuffer = BufferUtils.createShortBuffer(bufferIndices.pointSize());
//        for(short i: bufferIndices){
//            indexBuffer.put(i); // put each of the elements of each vertex of the object in the buffer
//        }
//        indexBuffer.flip();
//
//
//        // send the object vertices to the GPU
//        GL15.glBufferData(GL15.GL_ARRAY_BUFFER,        vertexBuffer, GL15.GL_STREAM_DRAW); // buffer to bind to, data to bind, type of data
//        // send the object indices to the GPU
//        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL15.GL_STREAM_DRAW);
//        // Finally, we draw:
////        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
//        // Draw triangles, number of indices, type of indicesArray, where to run
//        GL11.glDrawElements(GL11.GL_TRIANGLES, bufferIndices.pointSize(), GL11.GL_UNSIGNED_SHORT, 0);
////        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
//
//        int err = GL11.glGetError();
//        if (err != GL11.GL_NO_ERROR) {
//            System.out.println("Error rendering non-quads: " + err);
//        }
//
//        // switch the buffers so that things actually get drawn on the screen
//        Display.update(); // this actually does double-buffering for us...
//
//
//    }
//
//    public static void cleanup() {
//
//        /* ------------------------- DELETE TEXTURES -----------------------------------------------------------------*/
//
//        // Delete textures in the handy-dandy array
//        for (int i = 0; i < textureIDs.length; i++) {
//            GL11.glDeleteTextures(textureIDs[i]);
//        }
//
//        // Delete textures in any loaded fonts
//        // TODO implement this
//        Font.delete();
//
//        // ---- Delete additional textures -----
//
//        // Delete render buffer texture
//        GL11.glDeleteTextures(textureBufferID);
//
//        // Delete all TextQuad
//        // TODO implement this
//
//        /* ------------------------- END DELETE TEXTURES -------------------------------------------------------------*/
//
//
//        /* ------------------------- DELETE SHADERS ------------------------------------------------------------------*/
//
//        // Delete default shader
//        GL20.glUseProgram(0); // unbind the program
//        GL20.glDetachShader(shaderProgramID, vertexShaderID); // detach the shaders
//        GL20.glDetachShader(shaderProgramID, fragmentShaderID);
//
//        GL20.glDeleteShader(vertexShaderID); // delete the shaders
//        GL20.glDeleteShader(fragmentShaderID);
//        GL20.glDeleteProgram(shaderProgramID); // delete the program
//
//        // Delete text prerenderer shader
//        GL20.glDetachShader(textPrerendererVertexShaderID, textPrerendererShaderProgramID);
//        GL20.glDetachShader(textPrerendererFragmentShaderID, textPrerendererShaderProgramID);
//
//        GL20.glDeleteShader(textPrerendererVertexShaderID);
//        GL20.glDeleteShader(textPrerendererFragmentShaderID);
//        GL20.glDeleteProgram(textPrerendererShaderProgramID);
//
//        // Delete on-the-fly text renderer shader
//        GL20.glDetachShader(textVertexShaderID, textShaderProgramID);
//        GL20.glDetachShader(textFragmentShaderID, textShaderProgramID);
//
//        GL20.glDeleteShader(textVertexShaderID);
//        GL20.glDeleteShader(textFragmentShaderID);
//        GL20.glDeleteProgram(textShaderProgramID);
//
//        // Delete post-processor shader
//        GL20.glDetachShader(postProcessorShaderProgramID, postProcessorVertexShaderID);
//        GL20.glDetachShader(postProcessorShaderProgramID, postProcessorFragmentShaderID);
//
//        GL20.glDeleteShader(postProcessorVertexShaderID);
//        GL20.glDeleteShader(postProcessorFragmentShaderID);
//        GL20.glDeleteProgram(postProcessorShaderProgramID);
//
//        /* ------------------------ END DELETE SHADERS ---------------------------------------------------------------*/
//
//
//        /* ------------------------ DELETE BUFFER OBJECTS ------------------------------------------------------------*/
//
//        // ----- Delete the default VAO -----
//        GL30.glBindVertexArray(VAOID); // select the default VAO
//
//        // Disable the VAO's attributes
//        GL20.glDisableVertexAttribArray(0); // position
//        GL20.glDisableVertexAttribArray(1); // color
//        GL20.glDisableVertexAttribArray(2); // texture
//
//        // Delete the vertex VBO
//        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
//        GL15.glDeleteBuffers(verticesVBOID);
//
//        // Delete the index VBO
//        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
//        GL15.glDeleteBuffers(indicesVBOID);
//
//        // Delete the default VAO
//        GL30.glBindVertexArray(0);
//        GL30.glDeleteVertexArrays(VAOID);
//
//        // ----- Delete the render buffer, frame buffer -----
//        GL15.glDeleteBuffers(frameBufferID);
//        GL15.glDeleteBuffers(renderBufferID);
//
//        // ----- Delete the post-processor VAO -----
//        GL30.glBindVertexArray(postProcessorVAOID);
//
//        // Disable the VAO's attributes
//        GL20.glDisableVertexAttribArray(0); // position
//        GL20.glDisableVertexAttribArray(1); // texture
//
//        // Delete the vertex VBO
//        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
//        GL15.glDeleteBuffers(postProcessorVBOID);
//
//        /* ------------------------ END DELETE BUFFER OBJECTS --------------------------------------------------------*/
//
//        // exits the program when finished
//        Worker.destroy();
//        Display.destroy();
//
//    }

}
