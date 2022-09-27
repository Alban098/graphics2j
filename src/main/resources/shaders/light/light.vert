#version 430

layout (location = 0) in float vertexId;
layout (location = 1) in vec3 color;

layout(std430, binding=0) buffer transforms {
    mat4 matrices[];
};

out mat4 pass_transform;
out vec4 pass_color;

void main() {
    pass_color = vec4(color, 1.0);
    pass_transform = matrices[int(vertexId)];
    gl_Position = vec4(0, 0, 0, 1);
}