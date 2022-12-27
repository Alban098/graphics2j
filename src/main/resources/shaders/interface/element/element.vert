#version 430

layout (location = 0) in float vertexId;

layout(std430, binding=0) buffer transforms {
    mat4 matrices[];
};

out mat4 pass_transform;

void main() {
    pass_transform = mat4(matrices[int(vertexId)]);
    gl_Position = vec4(0, 0, 0, 1);
}
