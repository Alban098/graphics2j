<h1 align="center">
  Graphics2J
  <br>
</h1>

<h4 align="center">A small 2D Rendering Engine build with <a href="https://www.lwjgl.org/" target="_blank">LWJGL 3</a>.</h4>

<div align="center">

[![Github All Releases](https://img.shields.io/github/downloads/Alban098/graphics2j/total.svg?logo=github)](https://github.com/Alban098/graphics2j/releases)
[![CI](https://github.com/Alban098/graphics2j/actions/workflows/ci.yml/badge.svg)](https://github.com/Alban098/graphics2j/actions/workflows/ci.yml)<br>
[![CI](https://github.com/Alban098/graphics2j/actions/workflows/release.yml/badge.svg)](https://github.com/Alban098/graphics2j/actions/workflows/release.yml)<br>
[![CI](https://github.com/Alban098/graphics2j/actions/workflows/javadoc.yml/badge.svg)](https://github.com/Alban098/graphics2j/actions/workflows/javadoc.yml)<br>

</div>

<p align="center">
  <a href="#key-features">Key Features</a> •
  <a href="#how-to-use">How To Use</a> •
  <a href="#download">Download</a> •
  <a href="#roadmap">Roadmap</a> •
  <a href="#credits">Credits</a> •
  <a href="#license">License</a>
</p>

<p align="center">
  <img src="img/example.gif" />
</p>

## Key Features

- Modular entity system
- Recursive UI system with Modal support
- SDF text rendering inside of UI (you can import custom SDF fonts)
- Customizable shaders for more freedom during development

## How To Use

You can add `graphics2j-core-x.x.x.jar` or `graphics2j-example-x.x.x.-executable.jar` as a library and use it inside your projects !

A minimal application should look like this

```java
import org.alban098.graphics2j.common.Window;
import org.alban098.graphics2j.common.Cleanable;
import org.alban098.graphics2j.common.components.Camera;
import org.alban098.graphics2j.common.components.Transform;
import org.alban098.graphics2j.common.components.RenderElement;
import org.alban098.graphics2j.common.shaders.data.Texture;
import org.alban098.graphics2j.common.utils.ResourceLoader;
import org.alban098.graphics2j.entities.EntityRenderingManager;
import org.alban098.graphics2j.entities.Entity;
import org.alban098.graphics2j.fonts.FontManager;
import org.alban098.graphics2j.input.MouseState;
import org.alban098.graphics2j.interfaces.InterfaceRenderingManager;
import org.alban098.graphics2j.interfaces.windows.DecoratedUI;
import org.apache.log4j.PropertyConfigurator;
import org.joml.Vector2f;

import java.lang.ref.Cleaner;
import java.util.HashSet;
import java.util.Set;

public class Demo {

    private final Window window;
    private final MouseState mouseState;
    private final EntityRenderingManager entityManager;
    private final InterfaceRenderingManager interfaceManager;
    private final Camera camera;
    private final Set<DemoEntity> entities;

    public static void main(String[] args) {
        PropertyConfigurator.configure("./log4j.properties");
        new Demo();
    }

    public Demo() {
        window = new Window("Example", 1200, 600, false);
        mouseState = new MouseState();
        mouseState.linkCallbacks(window);
        entityManager = new EntityRenderingManager();
        interfaceManager = new InterfaceRenderingManager(window, mouseState);
        camera = new Camera(window, new Vector2f());
        entities = new HashSet<>();

        init();
        loop();
        cleanUp();
    }

    public void init() {
        FontManager.registerFont("Candara", "assets/fonts/");

        Texture texture = ResourceLoader.loadTexture("assets/textures/texture.png");

        RenderElement renderable = new RenderElement(texture);
        Transform transform = new Transform();

        DemoEntity entity = new DemoEntity(transform, renderable);
        entityManager.add(entity);
        entities.add(entity);

        UserInterface ui = new DecoratedUI(window, "Demo");
        interfaceManager.add(ui);
        interfaceManager.setVisibility(ui, true);
    }

    public void loop() {
        long interval = System.nanoTime();

        // While running
        while (!window.windowShouldClose()) {
            window.newFrame();

            // Calculate an update duration and get the elapsed time since last loop
            interval = System.nanoTime() - interval;

            // Handle user inputs
            mouseState.update();
            camera.update(window, mouseState);
            interfaceManager.processUserInput();

            update(interval / 1_000_000_000.0);

            // Render the frame
            entityManager.render(window, camera);
            interfaceManager.render();

            // Draw the frame
            window.endFrame();
        }
    }

    private void cleanUp() {
        window.cleanUp();
    }

    private void update(double elapsedTime) {
        interfaceManager.update(elapsedTime);
        entities.forEach(e -> e.update(elapsedTime));
    }

    public static final class DemoEntity implements Entity {
        private Transform transform;
        private RenderElement renderable;

        public TexturedEntity(Transform transform, RenderElement renderable) {
            this.transform = transform;
            this.renderable = renderable;
        }

        public void update(double elapsedTime) {
            transform.rotate((float) (2f * elapsedTime));
        }

        @Override
        public RenderElement getRenderable() {
            return renderable;
        }

        @Override
        public Transform getTransform() {
            return transform;
        }

        @Override
        public String getName() {
            return "Demo";
        }
    }
}
```

Fill free to take a look at the [Example Module](./example) for a more complex example.

The result of the `Example Module` is available in the [Release](https://github.com/Alban098/graphics2j/releases) section as `graphics2j-example-x.x.x-executable.jar` and can be run.

`engine2j-example-x.x.x-executable.jar` is runnable but can also be added as a library, as it contains the Core API along with the example for help and reference

You can also find the [Javadoc](https://alban098.github.io/graphics2j/) of the latest release !

## Download

You can [download](https://github.com/Alban098/graphics2j/releases) the latest version of Graphics2J.

## Roadmap

- [x] Implement uniforms
- [x] Camera movement and zoom
- [x] Batch rendering
- [x] ImGui integration
- [x] Font rendering
- [x] GUI Rendering
- [x] Documentation
- [x] Redo simple debug interface (ImGui)
- [ ] Dissociate rendering and engine logics to allow addition of an independent physics logic (WIP)
- [ ] Physics logic module
- [ ] Complete engine using the 2 independent modules (Rendering + Physics)
- [ ] Unit tests
- [ ] Particle system
- [ ] Lighting system
- [ ] Post-processing effect

## Credits

This software uses the following libraries:

- [LWJGL](https://github.com/LWJGL/lwjgl3)
- [ImGui Java](https://github.com/SpaiR/imgui-java)
- [JUnit 4](https://junit.org/junit4/)
- [SLF4J](https://github.com/qos-ch/slf4j)
- [Reload4J](https://github.com/qos-ch/reload4j)

## License

This project is licensed under the **[MIT license](http://opensource.org/licenses/mit-license.php)** (see [LICENCE.md](LICENSE.md))

---

> GitHub [@Alban098](https://github.com/Alban098)
