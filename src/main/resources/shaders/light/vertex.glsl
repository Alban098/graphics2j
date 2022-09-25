#version 430
layout (location = 0) in vec2 position;
layout (location = 1) in vec4 transform;
layout (location = 2) in vec3 color;

out vec4 v_color;

uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;

struct Transform {
    vec2 displacement;
    float scale;
    float rotation;
};

Transform toTransform(vec4 raw) {
    return Transform(vec2(raw.x, raw.y), raw.z, raw.w);
}

void main() {

    v_color = vec4(color, 1.0);
    Transform trans = toTransform(transform);
    mat2 scale = mat2(trans.scale, 0, 0, trans.scale);
    mat2 rotation = mat2(cos(trans.rotation), -sin(trans.rotation), sin(trans.rotation), cos(trans.rotation));

    gl_Position = projectionMatrix * viewMatrix * vec4(position * scale * rotation + trans.displacement, 0, 1);
}