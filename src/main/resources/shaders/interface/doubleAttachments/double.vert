#version 430

layout (location = 0) in int vertexId;
layout (location = 3) in int uiElementId;

layout(std430, binding=0) buffer transforms {
    mat4 matrices[];
};

out mat4 pass_transform;
out int pass_id;

void main() {
    pass_transform = mat4(matrices[vertexId]);
    pass_id = uiElementId;
    gl_Position = vec4(0, 0, 0, 1);
}
