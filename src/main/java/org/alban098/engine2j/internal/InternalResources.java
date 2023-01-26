/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.engine2j.internal;

public class InternalResources {

    private InternalResources() {}

    /*
     *****************************************************
     *                     Textures                      *
     *****************************************************
     */

    /** The PNG file of the close button encoded as base64 */
    public static final String CLOSE_TEXTURE_BASE64 = "iVBORw0KGgoAAAANSUhEUgAAAEAAAABACAYAAACqaXHeAAAACXBIWXMAAAsTAAALEwEAmpwYAAAC50lEQVR4nO2bz2sTQRTHX/8gi4fMpB47a4uVVkSRBqFoe/MP8aJeFKFgwPqj6FXwj9Co5yo21IPgQdHLvF3IBjMysYlpu4nb3XnzJt0G3jGb7+ezk5mdSR7AoZcBmInn65cwks8xkm0diQQjaaax9rNbhmexqq1YNpj0wgVxVkfiA3dwMiFKvteqPpsNP1+/qJVE7pD0JTSq2tLRO6+qAP9PwnAkGICZkzzsx5VW8l1/Ttif8NgDMY2EZUAlt/mDcI0C8RTsMlHhEbALWsm4uiNAxhBCEM4C7gDcBdwBuAu4A3AXcAc4MQKS9VXzu9UycWOFNHBy60Z4ApL1VdPb+2LMj1+mt/OJTEL66KEx33+azp3b4QhINhpD+EFRSOjDDz7DoQRwDU8h4QC8YwlAAe9SQia8QwlQWMDaVdPbbU8UUFZC2tz87/WthDITI5SxRylh4p0fqfRxk2cEIKEEX/DobBl0KMEnPDp9EHIgwTc8un4ULiOBAx4p9gJFJHDBI9Vm6DgSuttbbPBIuRvMK4ETHqm3wy4kUMKjj/OAMhKo4dHXgUgRCT7g0eeJkJVgvn7LBd99+cJLJvQpIO9SN+45YaoFpMeA9y0BQoT3KQFChfclAdjhc0yMlBKAE94udcnaFfKTJa8C0gIbG04JwA3PLQFCgOeUAKHAc0mAkOA5JEBo8L4lQNE3dh7cywff3CwcLrl5zfTaeySCSwuIG8umt/ORLFjekdB9/crg4jmer0A8QYLL/fw4CWXh0cUkmCWB4jDjsAQX8OhqGRyVQHmSM5DgCh6d/jJ0/bLp3L9LBj8qGxfmnF0PqAOHXsAdgLuAOwB3AXcA7gLuANwFp3+XjyrcMKHEZ7BNhexBmEpHcuu0acrYtjkl3lTw7reGrbS2ibDfURlAMD8ltD4/d+Zg96iqLVVDgmUUFzL7h7Wqz9qOSv6QNKUj8fbInYeM9nnbUWmbCu0yMc3PCf3sfxmeWKas9vk/xmJZYL7LDzUAAAAASUVORK5CYII=";

    /*
     ******************************************************
     *                      Shaders                       *
     ******************************************************
     */

    /** The vertex shader used to render {@link org.alban098.engine2j.objects.interfaces.element.Line}s */
    public static final String INTERFACE_LINE_VERTEX = """
            #version 430
            
            layout (location = 0) in int vertexId;
            layout (location = 1) in vec2 lineStart;
            layout (location = 2) in vec2 lineEnd;
            layout (location = 3) in int uiElementId;
            
            out mat4 pass_transform;
            out vec2 pass_line_start;
            out vec2 pass_line_end;
            
            void main() {
                pass_line_start = lineStart;
                pass_line_end = lineEnd;
                gl_Position = vec4(0, 0, 0, 1);
            }
            """;
    /** The geometry shader used to render {@link org.alban098.engine2j.objects.interfaces.element.Line}s */
    public static final String INTERFACE_LINE_GEOMETRY = """
            #version 430 core
            
            layout (points) in;
            layout (triangle_strip, max_vertices = 4) out;
            
            uniform vec2 viewport;
            
            in vec2 pass_line_start[];
            in vec2 pass_line_end[];
            
            out vec2 start;
            out vec2 end;
            
            struct vertex {
                vec4 position;
            };
            
            const vertex[] VERTICES = {
                vertex(vec4(-1.0, -1.0, 0.0, 1.0)),
                vertex(vec4( 1.0, -1.0, 0.0, 1.0)),
                vertex(vec4(-1.0,  1.0, 0.0, 1.0)),
                vertex(vec4( 1.0,  1.0, 0.0, 1.0))
            };
            
            void main() {
                start = pass_line_start[0];
                start.y = viewport.y - start.y;
                end = pass_line_end[0];
                end.y = viewport.y - end.y;
                for (int i = 0; i < 4; i++) {
                    gl_Position = VERTICES[i].position;
                    EmitVertex();
                }
                EndPrimitive();
            }
            """;
    /** The fragment shader used to render {@link org.alban098.engine2j.objects.interfaces.element.Line}s */
    public static final String INTERFACE_LINE_FRAGMENT = """
            #version 430
            
            uniform vec4 color;
            uniform float lineWidth;
            
            in vec2 start;
            in vec2 end;
            
            layout (location = 0) out vec4 fragColor;
            
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
                fragColor.a = 1 - smoothstep(lineWidth / 2 - 1, lineWidth / 2 + 1, dist);
            
            }
            """;
    /** The vertex shader used to render {@link org.alban098.engine2j.objects.interfaces.UserInterface}s and {@link org.alban098.engine2j.objects.interfaces.element.UIElement} */
    public static final String INTERFACE_SIMPLE_VERTEX = """
            #version 430
            
            layout (location = 0) in int vertexId;
            
            layout(std430, binding = 0) buffer transforms {
                mat4 matrices[];
            };
            
            out mat4 pass_transform;
            
            void main() {
                pass_transform = mat4(matrices[vertexId]);
                gl_Position = vec4(0, 0, 0, 1);
            }
            """;
    /** The geometry shader used to render {@link org.alban098.engine2j.objects.interfaces.UserInterface}s and {@link org.alban098.engine2j.objects.interfaces.element.UIElement} */
    public static final String INTERFACE_SIMPLE_GEOMETRY = """
            #version 430 core
            
            layout (points) in;
            layout (triangle_strip, max_vertices = 4) out;
            
            in mat4 pass_transform[];
            
            out vec2 v_textureCoords;
            
            struct vertex {
                vec4 position;
                vec2 texCoords;
            };
            
            const vertex[] VERTICES = {
                vertex(vec4(-0.5, -0.5, 0.0, 1.0), vec2(0, 0)),
                vertex(vec4( 0.5, -0.5, 0.0, 1.0), vec2(1, 0)),
                vertex(vec4(-0.5,  0.5, 0.0, 1.0), vec2(0, 1)),
                vertex(vec4( 0.5,  0.5, 0.0, 1.0), vec2(1, 1))
            };
            
            void main() {
                mat4 mvpMatrix = mat4(pass_transform[0]);
            
                for (int i = 0; i < 4; i++) {
                    v_textureCoords = VERTICES[i].texCoords;
                    gl_Position =  mvpMatrix * VERTICES[i].position;
                    EmitVertex();
                }
                EndPrimitive();
            }
            """;
    /** The fragment shader used to render {@link org.alban098.engine2j.objects.interfaces.UserInterface}s and backgrounds */
    public static final String INTERFACE_SIMPLE_FRAGMENT = """
            #version 430

            uniform sampler2D tex;
            uniform bool textured;
            uniform vec4 color;
            uniform vec2 viewport;
            uniform float radius;
            uniform float borderWidth;
            uniform vec3 borderColor;
            
            in vec2 v_textureCoords;
            
            out vec4 fragColor;
            
            float getDistanceToCorner() {
                vec2 coords = v_textureCoords * viewport;
                if (coords.x - radius < 0 && coords.y - radius < 0) {
                    return length(vec2(radius, radius) - coords) - radius;
                }
                if (coords.x - radius < 0 && coords.y + radius > viewport.y) {
                    return length(vec2(radius, viewport.y - radius) - coords) - radius;
                }
                if (coords.x + radius > viewport.x && coords.y - radius < 0) {
                    return length(vec2(viewport.x - radius, radius) - coords) - radius;
                }
                if (coords.x + radius > viewport.x && coords.y + radius > viewport.y) {
                    return length(vec2(viewport.x - radius, viewport.y - radius) - coords) - radius;
                }
                return -radius;
            }
            
            void roundCorners() {
                if (radius > 0) {
                    float dist = getDistanceToCorner();
                    fragColor.w *= smoothstep(1, 0, dist / radius * radius);
                }
            }
            
            void border() {
                vec2 coords = v_textureCoords * viewport;
                if (coords.x < borderWidth || coords.x > viewport.x - borderWidth) {
                    fragColor = vec4(borderColor.rgb, 1);
                }
                if (coords.y < borderWidth || coords.y > viewport.y - borderWidth) {
                    fragColor = vec4(borderColor.rgb, 1);
                }
                if (radius <= 0) {
                    return;
                }
                float dist = getDistanceToCorner();
                if (dist > - borderWidth + 1) {
                    fragColor = vec4(borderColor.rgb, 1);
                }
            }
            
            void main() {
                if (textured) {
                    fragColor = texture(tex, v_textureCoords);
                } else {
                    fragColor = color;
                }
                border();
                roundCorners();
            }
            """;
    /** The fragment shader used to render {@link org.alban098.engine2j.objects.interfaces.element.UIElement} */
    public static final String INTERFACE_ELEMENT_FRAGMENT = """
            #version 430

            uniform sampler2D tex;
            uniform bool textured;
            uniform bool clicked;
            uniform bool hovered;
            uniform float timeMs;
            uniform vec4 color;
            uniform vec2 viewport;
            uniform float radius;
            uniform float borderWidth;
            uniform vec3 borderColor;
            
            in vec2 v_textureCoords;
            
            out vec4 fragColor;
            
            float getDistanceToCorner() {
                vec2 coords = v_textureCoords * viewport;
                if (coords.x - radius < 0 && coords.y - radius < 0) {
                    return length(vec2(radius, radius) - coords) - radius;
                }
                if (coords.x - radius < 0 && coords.y + radius > viewport.y) {
                    return length(vec2(radius, viewport.y - radius) - coords) - radius;
                }
                if (coords.x + radius > viewport.x && coords.y - radius < 0) {
                    return length(vec2(viewport.x - radius, radius) - coords) - radius;
                }
                if (coords.x + radius > viewport.x && coords.y + radius > viewport.y) {
                    return length(vec2(viewport.x - radius, viewport.y - radius) - coords) - radius;
                }
                return -1;
            }
            
            void roundCorners() {
                if (radius > 0) {
                    float dist = getDistanceToCorner();
                    fragColor.w *= smoothstep(1, 0, dist / radius * radius);
                }
            }
            
            void border() {
                vec2 coords = v_textureCoords * viewport;
                if (coords.x < borderWidth || coords.x > viewport.x - borderWidth) {
                    fragColor = vec4(borderColor.rgb, 1);
                }
                if (coords.y < borderWidth || coords.y > viewport.y - borderWidth) {
                    fragColor = vec4(borderColor.rgb, 1);
                }
                if (radius <= 0) {
                    return;
                }
                float dist = getDistanceToCorner();
                if (dist > - borderWidth + 1) {
                    fragColor = vec4(borderColor.rgb, 1);
                }
            }
            
            void main() {
                if (textured) {
                    fragColor = texture(tex, v_textureCoords);
                } else {
                    fragColor = color;
                }
            
                if (clicked) {
                    fragColor.xyz = mix(fragColor.xyz, vec3(0), 0.25);
                } else if (hovered) {
                    fragColor.xyz = mix(fragColor.xyz, vec3(0), 0.125);
                }
                border();
                roundCorners();
            }
            """;
    /** The vertex shader used to render {@link org.alban098.engine2j.fonts.Font}s inside {@link org.alban098.engine2j.objects.interfaces.UserInterface} */
    public static final String INTERFACE_FONT_VERTEX = """
            #version 430

            layout (location = 0) in int vertexId;
            layout (location = 1) in vec2 uvPos;
            layout (location = 2) in vec2 uvSize;
            
            layout(std430, binding = 0) buffer transforms {
                mat4 matrices[];
            };
           
            out mat4 pass_transform;
            out vec2 pass_uv_pos;
            out vec2 pass_uv_size;
           
            void main() {
                pass_transform = mat4(matrices[vertexId]);
                pass_uv_pos = uvPos;
                pass_uv_size = uvSize;
                gl_Position = vec4(0, 0, 0, 1);
            }
            """;
    /** The geometry shader used to render {@link org.alban098.engine2j.fonts.Font}s inside {@link org.alban098.engine2j.objects.interfaces.UserInterface} */
    public static final String INTERFACE_FONT_GEOMETRY = """
            #version 430 core

            layout (points) in;
            layout (triangle_strip, max_vertices = 4) out;
           
            in mat4 pass_transform[];
            in vec2 pass_uv_pos[];
            in vec2 pass_uv_size[];
           
            out vec2 v_textureCoords;

            struct vertex {
                vec4 position;
            };
            
            const vertex[] VERTICES = {
                vertex(vec4(-0.5, -0.5, 0.0, 1.0)),
                vertex(vec4( 0.5, -0.5, 0.0, 1.0)),
                vertex(vec4(-0.5,  0.5, 0.0, 1.0)),
                vertex(vec4( 0.5,  0.5, 0.0, 1.0))
            };
           
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
            """;
    /** The fragment shader used to render {@link org.alban098.engine2j.fonts.Font}s inside {@link org.alban098.engine2j.objects.interfaces.UserInterface} */
    public static final String INTERFACE_FONT_FRAGMENT = """
            #version 430

            uniform sampler2D tex;
            uniform vec4 color;
            uniform float fontWidth;
            uniform float fontBlur;
           
            in vec2 v_textureCoords;
           
            layout (location = 0) out vec4 fragColor;
           
            void main() {
                float dist = 1 - texture(tex, v_textureCoords).a;
                float alpha = 1 - smoothstep(fontWidth, fontWidth + fontBlur, dist);
                fragColor = vec4(color.rgb, alpha * color.a);
            }
            """;
    /** The vertex shader used to render {@link org.alban098.engine2j.objects.entities.Entity} */
    public static final String ENTITY_VERTEX = """
            #version 430
                        
            layout (location = 0) in int vertexId;
                        
            layout(std430, binding = 0) buffer transforms {
                mat4 matrices[];
            };
                        
            out mat4 pass_transform;
                        
            void main() {
                pass_transform = mat4(matrices[vertexId]);
                gl_Position = vec4(0, 0, 0, 1);
            }         
            """;
    /** The geometry shader used to render {@link org.alban098.engine2j.objects.entities.Entity} */
    public static final String ENTITY_GEOMETRY = """
            #version 430 core
                        
            struct vertex {
                vec4 position;
                vec2 texCoords;
            };
                        
            const vertex[] VERTICES = {
                vertex(vec4(-0.5, -0.5, 0.0, 1.0), vec2(0, 1)),
                vertex(vec4( 0.5, -0.5, 0.0, 1.0), vec2(1, 1)),
                vertex(vec4(-0.5,  0.5, 0.0, 1.0), vec2(0, 0)),
                vertex(vec4( 0.5,  0.5, 0.0, 1.0), vec2(1, 0))
            };
                        
            layout (points) in;
            layout (triangle_strip, max_vertices = 4) out;
                        
            uniform mat4 viewMatrix;
            uniform mat4 projectionMatrix;
                        
            in mat4 pass_transform[];
                        
            out vec2 v_textureCoords;
                        
            void main() {
                mat4 mvpMatrix = mat4(projectionMatrix * viewMatrix * pass_transform[0]);
                        
                for (int i = 0; i < 4; i++) {
                    v_textureCoords = VERTICES[i].texCoords;
                    gl_Position =  mvpMatrix * VERTICES[i].position;
                    EmitVertex();
                }
                EndPrimitive();
            }
            """;
    /** The fragment shader used to render {@link org.alban098.engine2j.objects.entities.Entity} */
    public static final String ENTITY_FRAGMENT = """
            #version 430
                        
            uniform sampler2D tex;
                        
            in vec2 v_textureCoords;
                        
            out vec4 fragColor;
                        
            void main() {
                fragColor = texture(tex, v_textureCoords);
            }
            """;
}
