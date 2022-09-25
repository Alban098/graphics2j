#version 430

uniform sampler2D tex;

in vec4 v_color;

out vec4 fragColor;

void main() {
    fragColor = v_color;
}