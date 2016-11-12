#version 330 core


layout (location = 3) in vec2 position;
layout (location = 4) in vec2 texture;

out vec2 TexCoords;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;


void main()
{
    gl_Position = projectionMatrix * viewMatrix * modelMatrix * vec4(position, 0.0, 1.0);
    //gl_Position = vec4(position, 0.01, 1.0);
    TexCoords = texture;
}
