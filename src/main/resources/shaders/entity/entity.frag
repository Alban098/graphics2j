#version 430

uniform sampler2D tex;

in vec2 v_textureCoords;

out vec4 fragColor;

void main() {
    fragColor = texture(tex, v_textureCoords);
}