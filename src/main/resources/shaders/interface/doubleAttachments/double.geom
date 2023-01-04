#version 430 core

struct Vertex {
    vec4 position;
    vec2 texCoords;
};

const Vertex[] VERTICES = {
    Vertex(vec4(-0.5, -0.5, 0.0, 1.0), vec2(0, 0)),
    Vertex(vec4( 0.5, -0.5, 0.0, 1.0), vec2(1, 0)),
    Vertex(vec4(-0.5,  0.5, 0.0, 1.0), vec2(0, 1)),
    Vertex(vec4( 0.5,  0.5, 0.0, 1.0), vec2(1, 1))
};

layout (points) in;
layout (triangle_strip, max_vertices = 4) out;

in mat4 pass_transform[];
in int pass_id[];

out vec2 v_textureCoords;
flat out int pass_id_0;

void main() {
    mat4 mvpMatrix = mat4(pass_transform[0]);

    for (int i = 0; i < 4; i++) {
        v_textureCoords = VERTICES[i].texCoords;
        pass_id_0 = pass_id[0];
        gl_Position =  mvpMatrix * VERTICES[i].position;
        EmitVertex();
    }
    EndPrimitive();
}
