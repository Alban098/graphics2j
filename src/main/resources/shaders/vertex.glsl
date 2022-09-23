#version 430

layout (location = 0) in vec2 position;
layout (location = 1) in vec2 textureCoords;

out vec2 v_textureCoords;

void main() {

    v_textureCoords = textureCoords;
    gl_Position = vec4(position, 0, 1);
}