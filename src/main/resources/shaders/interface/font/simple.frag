#version 430

uniform sampler2D tex;
uniform vec4 color;
uniform float fontWidth;
uniform float fontBlur;

in vec2 v_textureCoords;

layout (location = 0) out vec4 fragColor;

void main() {
    float dist = 1 - texture(tex, v_textureCoords).a;
    float alpha = 1 - smoothstep(fontWidth, fontWidth + fontBlur, dist);
    fragColor = vec4(color.rgb, alpha * color.a);
}
