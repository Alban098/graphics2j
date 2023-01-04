#version 430 core

struct Vertex {
    vec4 position;
};

const Vertex[] VERTICES = {
    Vertex(vec4(-1.0, -1.0, 0.0, 1.0)),
    Vertex(vec4( 1.0, -1.0, 0.0, 1.0)),
    Vertex(vec4(-1.0,  1.0, 0.0, 1.0)),
    Vertex(vec4( 1.0,  1.0, 0.0, 1.0))
};

layout (points) in;
layout (triangle_strip, max_vertices = 4) out;

uniform vec2 viewport;

in vec2 pass_line_start[];
in vec2 pass_line_end[];
in int pass_id[];

out vec2 start;
out vec2 end;
flat out int pass_id_0;

void main() {
    start = pass_line_start[0];
    start.y = viewport.y - start.y;
    end = pass_line_end[0];
    end.y = viewport.y - end.y;
    for (int i = 0; i < 4; i++) {
        gl_Position = VERTICES[i].position;
        pass_id_0 = pass_id[0];
        EmitVertex();
    }
    EndPrimitive();
}
