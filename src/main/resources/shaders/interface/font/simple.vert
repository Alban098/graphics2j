#version 430

layout (location = 0) in float vertexId;
layout (location = 1) in vec2 uvPos;
layout (location = 2) in vec2 uvSize;

layout(std430, binding=0) buffer transforms {
    mat4 matrices[];
};

out mat4 pass_transform;
out vec2 pass_uv_pos;
out vec2 pass_uv_size;

void main() {
    pass_transform = mat4(matrices[int(vertexId)]);
    pass_uv_pos = uvPos;
    pass_uv_size = uvSize;
    gl_Position = vec4(0, 0, 0, 1);
}
