#version 430

layout (location = 0) in vec2 position;
layout (location = 1) in vec2 textureCoords;
layout (location = 2) in vec3 color;

out vec2 v_textureCoords;
out vec3 v_color;

void main() {

    v_color = color;
    v_textureCoords = textureCoords;
    gl_Position = vec4(position, 0, 1);
}