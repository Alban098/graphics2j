#version 430

uniform sampler2D tex;

uniform int wireframe;
uniform vec4 wireframeColor;

uniform bool textured;
uniform vec4 color;

out vec4 fragColor;
in vec2 v_textureCoords;


void main() {
    if (wireframe == 1) {
        fragColor = wireframeColor;
    } else {
        if (textured) {
            fragColor = texture(tex, v_textureCoords);
        } else {
            fragColor = color;
        }
    }
}
