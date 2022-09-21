#version 120

uniform sampler2D tex;

in vec2 v_textureCoords;

void main() {

    gl_FragColor = texture2D(tex, v_textureCoords);
}