#version 430

layout (location = 0) in vec2 vertex;
layout (location = 2) in int transformIndex;
layout (location = 3) in vec3 color;

layout(std430, binding=0) buffer transforms {
    mat4 matrices[];
};

uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;

out vec4 pass_color;

void main() {
    pass_color = vec4(color, 1.0);
    mat4 transform = mat4(matrices[transformIndex]);
    mat4 mvpMatrix = mat4(projectionMatrix * viewMatrix * transform);
    gl_Position = mvpMatrix * vec4(vertex, 0, 1);
}