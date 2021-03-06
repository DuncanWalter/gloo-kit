#version 330 core

uniform sampler2D texture_diffuse;

in vec4 pass_Color;
in vec2 pass_TextureCoord;

out vec4 out_Color;

void main() {
    out_Color = pass_Color;
    // multiply color with texture color
    out_Color *= texture(texture_diffuse, pass_TextureCoord);
    // this requires the quad to have a texture


    // This discards fragments if they are basically entirely transparent
    if (out_Color.a < 0.02) {
        discard;
    }
}
