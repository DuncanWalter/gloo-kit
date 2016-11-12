// These get called to render the entire screen to a single quad. We can also use this to apply post-processing effects


#version 330 core
in vec2 TexCoords;
out vec4 color;

uniform sampler2D screenTexture;

void main()
{
    color = texture(screenTexture, TexCoords);
}