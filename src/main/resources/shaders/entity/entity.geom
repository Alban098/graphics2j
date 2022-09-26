#version 430 core

struct Vertex {
    vec2 position;
    vec2 texCoords;
};

const Vertex[] VERTICES = {
    Vertex(vec2(-0.5, -0.5), vec2(0, 1)),
    Vertex(vec2( 0.5, -0.5), vec2(1, 1)),
    Vertex(vec2(-0.5,  0.5), vec2(0, 0)),
    Vertex(vec2( 0.5,  0.5), vec2(1, 0))
};

layout (points) in;
layout (triangle_strip, max_vertices = 4) out;

uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;

in float pass_scale[];
in float pass_rotation[];

out vec2 v_textureCoords;

void main() {
    mat2 transform = mat2(pass_scale[0], 0, 0, pass_scale[0]) * mat2(cos(pass_rotation[0]), -sin(pass_rotation[0]), sin(pass_rotation[0]), cos(pass_rotation[0]));
    mat4 projectionView = projectionMatrix * viewMatrix;

    for (int i = 0; i < 4; i++) {
        v_textureCoords = VERTICES[i].texCoords;
        gl_Position = projectionView * vec4(VERTICES[i].position * transform + gl_in[0].gl_Position.xy, 0, 1);
        EmitVertex();
    }
    EndPrimitive();
}