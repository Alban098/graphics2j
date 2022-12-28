#version 430

uniform sampler2D tex;

uniform int wireframe;
uniform vec4 wireframeColor;

uniform bool textured;
uniform vec4 color;

uniform vec2 dimension;
uniform vec4 radius;

out vec4 fragColor;
in vec2 v_textureCoords;

void roundCorners() {
    vec2 coords = v_textureCoords * dimension;
    float dist = -1;
    if (radius.x != 0 && coords.x - radius.x < 0 && coords.y - radius.x < 0) {
        dist = length(vec2(radius.x, radius.x) - coords) - radius.x;
        fragColor.w *= smoothstep(1, 0, dist / radius.x * radius.x);
    }
    if (radius.y != 0 && coords.x - radius.y < 0 && coords.y + radius.y > dimension.y) {
        dist = length(vec2(radius.y, dimension.y - radius.y) - coords) - radius.y;
        fragColor.w *= smoothstep(1, 0, dist / radius.y * radius.y);
    }
    if (radius.w != 0 && coords.x + radius.w > dimension.x && coords.y - radius.w < 0) {
        dist = length(vec2(dimension.x - radius.w, radius.w) - coords) - radius.w;
        fragColor.w *= smoothstep(1, 0, dist / radius.w * radius.w);
    }
    if (radius.z != 0 && coords.x + radius.z > dimension.x && coords.y + radius.z > dimension.y) {
        dist = length(vec2(dimension.x - radius.z, dimension.y - radius.z) - coords) - radius.z;
        fragColor.w *= smoothstep(1, 0, dist / radius.z * radius.z);
    }
}

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
    roundCorners();
}
