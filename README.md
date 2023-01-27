<h1 align="center">
  Engine2J
  <br>
</h1>

<h4 align="center">A small 2D Rendering Engine build with <a href="https://www.lwjgl.org/" target="_blank">LWJGL 3</a>.</h4>

<div align="center">

[![Github All Releases](https://img.shields.io/github/downloads/Alban098/engine2j/total.svg?logo=github)](https://github.com/Alban098/engine2j/releases)
[![CI](https://github.com/Alban098/engine2j/actions/workflows/ci.yml/badge.svg)](https://github.com/Alban098/engine2j/actions/workflows/ci.yml)<br>
[![CI](https://github.com/Alban098/engine2j/actions/workflows/release.yml/badge.svg)](https://github.com/Alban098/engine2j/actions/workflows/release.yml)<br>
[![CI](https://github.com/Alban098/engine2j/actions/workflows/javadoc.yml/badge.svg)](https://github.com/Alban098/engine2j/actions/workflows/javadoc.yml)<br>

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
- Component system for customizing Entity behavior
- Recursive UI system with Modal support
- SDF text rendering inside of UI (you can import custom SDF fonts)
- Customizable shaders for more freedom during development
- Customisable debugging interface using ImGUI
  - Renderer information :
    - drawcalls per frame per renderer
    - registered entities per renderer
    - detailed info about textures used by each renderer
    - details about all rendering datastructure per renderer (VAOs, VBOs, SSBOs, Uniforms, Shaders ...)
  - Timing information
    - FPS
    - TPS
    - frametime plot
  - Scene information
    - entities in the scene
    - detailed information about each entity in the scene
      - its components
      - its hierarchy

## How To Use

You can add `engine2j-core-x.x.x.jar` or `engine2j-example-x.x.x.-executable.jar` as a library and use it inside your projects !

A minimal application should look like this

```java
public class Main{
    public static void main(String[] args) {
        PropertyConfigurator.configure("./log4j.properties");
        new Engine("Demo", 1200, 600, new ExampleLogic(), new Engine.Options(false, 60, 120)).run();
    }
}
```

```java
public class ExampleLogic extends Logic {

    @Override
    protected void initFontManager() {
        FontManager.registerFont("Arial", "assets/fonts/");
    }

    @Override
    public void init() {
        Scene scene = getScene();

        Texture texture = ResourceLoader.loadTexture("assets/textures/texture.png");
        RenderableComponent renderable = new RenderableComponent(texture);
        TransformComponent transform = new TransformComponent();
        Entity entity = new Entity().addComponent(transform).addComponent(renderable);
        getScene().add(entity);

        UserInterface ui = new ControllableInterface(getWindow(), "Demo");
        scene.add(ui);
        scene.setVisibility(ui, true);
    }

    @Override
    protected void prepare(double elapsedTime) {}

    @Override
    protected void update(double elapsedTime) {
        getScene().update(elapsedTime);
    }

    @Override
    protected void end(double elapsedTime) {}
}
```

Fill free to take a look ath the [Example Module](./example) for a more complex example
The result of the `Example Module` is available in the [Release](https://github.com/Alban098/engine2j/releases) section as `engine2j-example-x.x.x-executable.jar` and can be run.
`engine2j-example-x.x.x-executable.jar` is runnable but can also be added as a library, as it contains the Core API along with the example for help and reference

You can also find the [Javadoc](https://alban098.github.io/engine2j/) of the latest release !

## Download

You can [download](https://github.com/Alban098/engine2j/releases) the latest version of Engine2J.

## Roadmap

- [x] Implement uniforms
- [x] Camera movement and zoom
- [x] Batch rendering
- [x] ImGui integration
- [x] Font rendering
- [x] GUI Rendering
- [x] Documentation
- [x] UserInterface debugger interface
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
