#version 430

layout (location = 0) in vec2 position;
layout (location = 1) in float scale;
layout (location = 2) in float rotation;
layout (location = 3) in vec3 color;

out float pass_scale;
out float pass_rotation;
out vec4 pass_color;

void main() {
    pass_scale = scale;
    pass_rotation = rotation;
    pass_color = vec4(color, 1.0);
    gl_Position = vec4(position, 0, 1);
}