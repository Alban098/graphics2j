#version 430

uniform sampler2D tex;
uniform vec4 color;
uniform float fontWidth;
uniform float fontBlur;

in vec2 v_textureCoords;
flat in int pass_id_0;

layout (location = 0) out vec4 fragColor;
layout (location = 1) out vec4 id;

void main() {
    float dist = 1 - texture(tex, v_textureCoords).a;
    float alpha = 1 - smoothstep(fontWidth, fontWidth + fontBlur, dist);
    fragColor = vec4(color.rgb, alpha * color.a);
    id = vec4(
        (((pass_id_0) >> 16) & 255) / 255.0,
        (((pass_id_0) >> 8)  & 255) / 255.0,
        (((pass_id_0) >> 0)  & 255) / 255.0,
        1
    );
}
