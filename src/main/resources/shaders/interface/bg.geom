#version 430 core

struct Vertex {
    vec4 position;
    vec2 texCoords;
};

const Vertex[] VERTICES = {
    Vertex(vec4(-0.5, -0.5, 0.0, 1.0), vec2(0, 1)),
    Vertex(vec4( 0.5, -0.5, 0.0, 1.0), vec2(1, 1)),
    Vertex(vec4(-0.5,  0.5, 0.0, 1.0), vec2(0, 0)),
    Vertex(vec4( 0.5,  0.5, 0.0, 1.0), vec2(1, 0))
};

layout (points) in;
layout (triangle_strip, max_vertices = 4) out;

in mat4 pass_transform[];

out vec2 v_textureCoords;

void main() {
    mat4 mvpMatrix = mat4(pass_transform[0]);

    for (int i = 0; i < 4; i++) {
        v_textureCoords = VERTICES[i].texCoords;
        gl_Position =  mvpMatrix * VERTICES[i].position;
        EmitVertex();
    }
    EndPrimitive();
}
