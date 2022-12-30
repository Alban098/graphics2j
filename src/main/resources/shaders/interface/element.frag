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

uniform vec2 dimension;
uniform float radius;

out vec4 fragColor;
in vec2 v_textureCoords;

void roundCorners() {
    if (radius > 0) {
        vec2 coords = v_textureCoords * dimension;
        float dist = -1;
        if (coords.x - radius < 0 && coords.y - radius < 0) {
            dist = length(vec2(radius, radius) - coords) - radius;
        }
        if (coords.x - radius < 0 && coords.y + radius > dimension.y) {
            dist = length(vec2(radius, dimension.y - radius) - coords) - radius;
        }
        if (coords.x + radius > dimension.x && coords.y - radius < 0) {
            dist = length(vec2(dimension.x - radius, radius) - coords) - radius;
        }
        if (coords.x + radius > dimension.x && coords.y + radius > dimension.y) {
            dist = length(vec2(dimension.x - radius, dimension.y - radius) - coords) - radius;
        }
        fragColor.w *= smoothstep(1, 0, dist / radius * radius);
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
    if (clicked) {
        fragColor.xyz = mix(fragColor.xyz, vec3(0), 0.25);
    } else if (focused) {
        fragColor.xyz = mix(fragColor.xyz, vec3(0.5), sin(timeMs * 5) / 8 + 0.2);
    } else if (hovered) {
        fragColor.xyz = mix(fragColor.xyz, vec3(0), 0.125);
    }

    roundCorners();
}
