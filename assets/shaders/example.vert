#version 430

layout (location = 0) in int vertexId;
layout (location = 3) in vec4 color;

layout(std430, binding = 0) buffer transforms {
    mat4 matrices[];
};

out mat4 pass_transform;
out vec4 pass_color;

void main() {
    pass_color = vec4(color);
    pass_transform = mat4(matrices[vertexId]);
    gl_Position = vec4(0, 0, 0, 1);
}