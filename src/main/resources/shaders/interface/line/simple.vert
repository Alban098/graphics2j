#version 430

layout (location = 0) in int vertexId;
layout (location = 1) in vec2 lineStart;
layout (location = 2) in vec2 lineEnd;
layout (location = 3) in int uiElementId;

out mat4 pass_transform;
out vec2 pass_line_start;
out vec2 pass_line_end;

void main() {
    pass_line_start = lineStart;
    pass_line_end = lineEnd;
    gl_Position = vec4(0, 0, 0, 1);
}
