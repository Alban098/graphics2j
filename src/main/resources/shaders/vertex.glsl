#version 430
layout (location = 0) in vec2 position;
layout (location = 1) in vec4 transform;

out vec2 v_textureCoords;

struct Transform {
    vec2 displacement;
    float scale;
    float rotation;
};

Transform toTransform(vec4 raw) {
    return Transform(vec2(raw.x, raw.y), raw.z, raw.w);
}

void main() {

    v_textureCoords = vec2(position.x + 0.5, 1.0 - (position.y + 0.5));
    Transform trans = toTransform(transform);
    mat2 scale = mat2(trans.scale, 0, 0, trans.scale);
    mat2 rotation = mat2(cos(trans.rotation), -sin(trans.rotation), sin(trans.rotation), cos(trans.rotation));

    gl_Position = vec4(position * scale * rotation + trans.displacement, 0, 1);
}