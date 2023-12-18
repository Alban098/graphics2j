#version 430 core

struct vertex {
    vec4 position;
};

const vertex[] VERTICES = {
vertex(vec4(-0.5, -0.5, 0.0, 1.0)),
vertex(vec4( 0.5, -0.5, 0.0, 1.0)),
vertex(vec4(-0.5,  0.5, 0.0, 1.0)),
vertex(vec4( 0.5,  0.5, 0.0, 1.0))
};

layout (points) in;
layout (triangle_strip, max_vertices = 4) out;

uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;

in mat4 pass_transform[];
in vec4 pass_color[];

out vec4 v_color;

void main() {
    mat4 mvpMatrix = mat4(projectionMatrix * viewMatrix * pass_transform[0]);

    for (int i = 0; i < 4; i++) {
        v_color = pass_color[0];
        gl_Position =  mvpMatrix * VERTICES[i].position;
        EmitVertex();
    }
    EndPrimitive();
}