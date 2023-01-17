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

in mat4 pass_transform[];
in vec2 pass_uv_pos[];
in vec2 pass_uv_size[];

out vec2 v_textureCoords;

void main() {
    mat4 mvpMatrix = mat4(pass_transform[0]);
    vec2[] uv = {
        vec2(pass_uv_pos[0].x, pass_uv_pos[0].y + pass_uv_size[0].y),
        vec2(pass_uv_pos[0].x + pass_uv_size[0].x, pass_uv_pos[0].y + pass_uv_size[0].y),
        vec2(pass_uv_pos[0].x, pass_uv_pos[0].y),
        vec2(pass_uv_pos[0].x + pass_uv_size[0].x, pass_uv_pos[0].y)
    };

    for (int i = 0; i < 4; i++) {
        v_textureCoords = uv[i];
        gl_Position =  mvpMatrix * VERTICES[i].position;
        EmitVertex();
    }
    EndPrimitive();
}
