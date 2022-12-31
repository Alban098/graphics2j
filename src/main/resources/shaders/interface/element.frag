#version 430

uniform sampler2D tex;

uniform bool textured;
uniform bool clicked;
uniform bool hovered;
uniform bool focused;

uniform float timeMs;

uniform vec4 color;

uniform vec2 dimension;
uniform float radius;

uniform float borderWidth;
uniform vec3 borderColor;

out vec4 fragColor;
in vec2 v_textureCoords;


float getDistanceToCorner() {
    vec2 coords = v_textureCoords * dimension;
    if (coords.x - radius < 0 && coords.y - radius < 0) {
        return length(vec2(radius, radius) - coords) - radius;
    }
    if (coords.x - radius < 0 && coords.y + radius > dimension.y) {
        return length(vec2(radius, dimension.y - radius) - coords) - radius;
    }
    if (coords.x + radius > dimension.x && coords.y - radius < 0) {
        return length(vec2(dimension.x - radius, radius) - coords) - radius;
    }
    if (coords.x + radius > dimension.x && coords.y + radius > dimension.y) {
        return length(vec2(dimension.x - radius, dimension.y - radius) - coords) - radius;
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
    vec2 coords = v_textureCoords * dimension;
    if (coords.x < borderWidth || coords.x > dimension.x - borderWidth) {
        fragColor = vec4(borderColor.rgb, 1);
    }
    if (coords.y < borderWidth || coords.y > dimension.y - borderWidth) {
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
    border();
    roundCorners();
}
