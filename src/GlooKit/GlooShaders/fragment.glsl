#version 330 core

uniform sampler2D texture_diffuse;

in vec4 pass_Color;
in vec2 pass_TextureCoord;

out vec4 out_Color;

void main() {
    out_Color = pass_Color;
    // add color with texture color
    out_Color += texture(texture_diffuse, pass_TextureCoord);



    // This discards fragments if they are basically entirely transparent
    if (out_Color.a < 0.1) {
        discard;
    }
}
