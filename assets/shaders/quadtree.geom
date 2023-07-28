#version 430 core

layout (points) in;
layout (line_strip, max_vertices = 5) out;

uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;

in vec4 pass_color[];
in mat4 pass_transform[];

out vec4 v_color;

struct Vertex {
    vec4 position;
};

const Vertex[] VERTICES = {
    Vertex(vec4(-0.5, -0.5, 0.0, 1.0)),
    Vertex(vec4( 0.5, -0.5, 0.0, 1.0)),
    Vertex(vec4( 0.5,  0.5, 0.0, 1.0)),
    Vertex(vec4(-0.5,  0.5, 0.0, 1.0)),
    Vertex(vec4(-0.5, -0.5, 0.0, 1.0))
};

void main() {
    mat4 mvpMatrix = mat4(projectionMatrix * viewMatrix * pass_transform[0]);
    v_color = pass_color[0];

    for (int i = 0; i < 5; i++) {
        gl_Position = mvpMatrix * VERTICES[i].position;
        EmitVertex();
    }
    EndPrimitive();
}
