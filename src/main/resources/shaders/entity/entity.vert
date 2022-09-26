#version 430

layout (location = 0) in vec2 position;
layout (location = 1) in float scale;
layout (location = 2) in float rotation;

out float pass_scale;
out float pass_rotation;

void main() {
    pass_scale = scale;
    pass_rotation = rotation;
    gl_Position = vec4(position, 0, 1);
}