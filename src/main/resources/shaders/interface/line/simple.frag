#version 430

uniform vec4 color;
uniform float lineWidth;

in vec2 start;
in vec2 end;

out vec4 fragColor;


void main() {
    vec2 coords = gl_FragCoord.xy;
    vec2 dir = end - start;
    float a = dir.y;
    float b = -dir.x;
    float c = -(a * start.x + b * start.y);
    float dist = abs(a*coords.x + b*coords.y + c)/(sqrt(a*a + b*b));

    vec2 startToPoint = coords - start;
    vec2 endToPoint = coords - end;
    if (dot(dir, startToPoint) < 0 || dot (dir, endToPoint) > 0) {
        dist = min(length(startToPoint), length(endToPoint));
    }

    fragColor.rgb = color.rgb;
    fragColor.a = 1 - smoothstep(lineWidth - 1, lineWidth + 1, dist);
}
