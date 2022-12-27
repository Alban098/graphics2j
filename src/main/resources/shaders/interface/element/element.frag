#version 430

uniform sampler2D tex;

uniform int wireframe;
uniform vec4 wireframeColor;

uniform bool textured;
uniform bool clicked;
uniform bool hovered;
uniform bool focused;

uniform float timeMs;

uniform vec4 color;
uniform vec4 clickTint;
uniform vec4 hoverTint;
uniform vec4 focusTint;

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
    if (clicked) {
        fragColor += clickTint;
    } if (hovered) {
        fragColor = mix(fragColor, hoverTint, sin(timeMs * 5) / 5 + 0.5);
    } if (focused) {
        fragColor = mix(fragColor, focusTint, sin(timeMs * 5) / 5 + 0.5);
    }
}
