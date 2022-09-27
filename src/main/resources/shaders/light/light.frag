#version 430

uniform sampler2D tex;
uniform int wireframe;
uniform vec4 wireframeColor;

in vec4 v_color;

out vec4 fragColor;

void main() {
    if (wireframe == 1) {
        fragColor = wireframeColor;
    } else {
        fragColor = v_color;
    }
}