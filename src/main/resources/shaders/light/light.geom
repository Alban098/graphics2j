#version 430 core

struct Vertex {
    vec4 position;
};

const Vertex[] VERTICES = {
    Vertex(vec4(-0.5, -0.5, 0.0, 1.0)),
    Vertex(vec4( 0.5, -0.5, 0.0, 1.0)),
    Vertex(vec4(-0.5,  0.5, 0.0, 1.0)),
    Vertex(vec4( 0.5,  0.5, 0.0, 1.0))
};

layout (points) in;
layout (triangle_strip, max_vertices = 4) out;

uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;

in vec4 pass_color[];
in mat4 pass_transform[];

out vec4 v_color;

void main() {
    mat4 mvpMatrix = projectionMatrix * viewMatrix * pass_transform[0];
    v_color = pass_color[0];

    for (int i = 0; i < 4; i++) {
        gl_Position = mvpMatrix * VERTICES[i].position;
        EmitVertex();
    }
    EndPrimitive();
}