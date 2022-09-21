#version 120

attribute vec2 position;
attribute vec2 textureCoords;

varying out vec2 v_textureCoords;

void main() {

    v_textureCoords = textureCoords;
    gl_Position = vec4(position, 0, 1);
}