
#version 330 core
layout (points) in;
layout (triangles) out;

void transform(vec3 position, float rotation){

    vec2 rotationMatrix = vec2(cos(rotation), sin(rotation));

    vec3 position_out = vec3(position[0] * rotationMatrix[0] - position[1] * rotationMatrix[1],
                             position[0] * rotationMatrix[1] + position[1] * rotationMatrix[0],
                             position[2]);




}

void main() {





    gl_Position = gl_in[0].gl_Position + vec4(-0.1, 0.0, 0.0, 0.0);
    EmitVertex();

    gl_Position = gl_in[0].gl_Position + vec4(0.1, 0.0, 0.0, 0.0);
    EmitVertex();

    EndPrimitive();
}

