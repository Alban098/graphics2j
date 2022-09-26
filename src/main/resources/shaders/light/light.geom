#version 430 core

struct Vertex {
    vec2 position;
};

const Vertex[] VERTICES = {
    Vertex(vec2(-0.5, -0.5)),
    Vertex(vec2( 0.5, -0.5)),
    Vertex(vec2(-0.5,  0.5)),
    Vertex(vec2( 0.5,  0.5))
};

layout (points) in;
layout (triangle_strip, max_vertices = 4) out;

uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;

in float pass_scale[];
in float pass_rotation[];
in vec4 pass_color[];

out vec4 v_color;

void main() {
    mat2 transform = mat2(pass_scale[0], 0, 0, pass_scale[0]) * mat2(cos(pass_rotation[0]), -sin(pass_rotation[0]), sin(pass_rotation[0]), cos(pass_rotation[0]));
    mat4 projectionView = projectionMatrix * viewMatrix;

    v_color = pass_color[0];
    for (int i = 0; i < 4; i++) {
        gl_Position = projectionView * vec4(VERTICES[i].position * transform + gl_in[0].gl_Position.xy, 0, 1);
        EmitVertex();
    }
    EndPrimitive();
}