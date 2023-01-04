#version 430

layout(binding=0) uniform sampler2D tex;

uniform bool textured;
uniform bool clicked;
uniform bool hovered;
uniform bool focused;

uniform float timeMs;

uniform vec4 color;

uniform vec2 viewport;
uniform float radius;

uniform float borderWidth;
uniform vec3 borderColor;

in vec2 v_textureCoords;
flat in int pass_id_0;

layout (location = 0) out vec4 fragColor;
layout (location = 1) out vec4 id;

float getDistanceToCorner() {
    vec2 coords = v_textureCoords * viewport;
    if (coords.x - radius < 0 && coords.y - radius < 0) {
        return length(vec2(radius, radius) - coords) - radius;
    }
    if (coords.x - radius < 0 && coords.y + radius > viewport.y) {
        return length(vec2(radius, viewport.y - radius) - coords) - radius;
    }
    if (coords.x + radius > viewport.x && coords.y - radius < 0) {
        return length(vec2(viewport.x - radius, radius) - coords) - radius;
    }
    if (coords.x + radius > viewport.x && coords.y + radius > viewport.y) {
        return length(vec2(viewport.x - radius, viewport.y - radius) - coords) - radius;
    }
    return -1;
}

void roundCorners() {
    if (radius > 0) {
        float dist = getDistanceToCorner();
        fragColor.w *= smoothstep(1, 0, dist / radius * radius);
    }
}

void border() {
    vec2 coords = v_textureCoords * viewport;
    if (coords.x < borderWidth || coords.x > viewport.x - borderWidth) {
        fragColor = vec4(borderColor.rgb, 1);
    }
    if (coords.y < borderWidth || coords.y > viewport.y - borderWidth) {
        fragColor = vec4(borderColor.rgb, 1);
    }
    if (radius <= 0) {
        return;
    }
    float dist = getDistanceToCorner();
    if (dist > - borderWidth + 1) {
        fragColor = vec4(borderColor.rgb, 1);
    }
}

void main() {
    if (textured) {
        fragColor = texture(tex, v_textureCoords);
    } else {
        fragColor = color;
    }

    if (clicked) {
        fragColor.xyz = mix(fragColor.xyz, vec3(0), 0.25);
    } else if (focused) {
        fragColor.xyz = mix(fragColor.xyz, vec3(0.5), sin(timeMs * 5) / 8 + 0.2);
    } else if (hovered) {
        fragColor.xyz = mix(fragColor.xyz, vec3(0), 0.125);
    }
    id = vec4(
        (((pass_id_0) >> 16) & 255) / 255.0,
        (((pass_id_0) >> 8)  & 255) / 255.0,
        (((pass_id_0) >> 0)  & 255) / 255.0,
        1
    );
    border();
    roundCorners();

    if (fragColor.a < 0.5 || pass_id_0 == 0) {
        id = vec4(0);
    }
}
