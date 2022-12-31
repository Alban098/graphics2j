/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.interfaces.element;

import java.util.ArrayList;
import java.util.Collection;
import org.joml.Vector2f;
import rendering.MouseInput;
import rendering.entities.component.RenderableComponent;
import rendering.entities.component.TransformComponent;
import rendering.fonts.AtlasCharacter;
import rendering.fonts.Font;
import rendering.fonts.FontManager;
import rendering.interfaces.UIElement;
import rendering.renderers.Renderable;
import rendering.shaders.ShaderAttributes;

public class TextLabel extends UIElement {

  private String text;

  private Collection<RenderableCharacter> renderableCharacters = new ArrayList<>();

  public TextLabel(String text) {
    setText(text);
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
    createRenderableCharacters();
  }

  public void createRenderableCharacters() {
    if (getContainer() != null || getParent() != null) {
      renderableCharacters.forEach(RenderableCharacter::cleanup);
      renderableCharacters.clear();

      Font font = FontManager.getFont(getProperties().getFontFamily());
      float fontSize = getProperties().getFontSize();

      Vector2f position = new Vector2f(getProperties().getPosition());
      Vector2f size = new Vector2f(1, 1);
      Vector2f offset = new Vector2f();
      Vector2f viewport;
      if (getParent() != null) {
        viewport = getParent().getProperties().getSize();
      } else {
        viewport = getContainer().getProperties().getSize();
      }

      for (char c : text.toCharArray()) {
        AtlasCharacter ac = font.get(c);
        RenderableCharacter character = new RenderableCharacter();
        character.renderableComponent.setAttributeValue(
            ShaderAttributes.TEXT_TEXTURE_POS, ac.getPosition());
        character.renderableComponent.setAttributeValue(
            ShaderAttributes.TEXT_TEXTURE_SIZE, ac.getSize());
        size.set(ac.getSize()).mul(fontSize * font.getFontFactor());
        offset.set(ac.getOffset()).mul(fontSize * font.getFontFactor());
        position.add(offset);

        character.updateTransform(position, size, viewport);

        position.sub(offset);
        position
            .add(ac.getAdvance() * fontSize * font.getFontFactor(), 0)
            .sub(font.getPadding()[0] * 2 * fontSize, 0);
        renderableCharacters.add(character);
      }
    }
  }

  @Override
  protected void onPropertyChange(
      Properties.Snapshot oldProperties, Properties.Snapshot newProperties) {}

  @Override
  public void update(double elapsedTime) {}

  @Override
  public boolean input(MouseInput input) {
    return false;
  }

  public Collection<RenderableCharacter> getCharacters() {
    return renderableCharacters;
  }

  private static final class RenderableCharacter implements Renderable {
    private final RenderableComponent renderableComponent;
    private final TransformComponent transformComponent;

    public RenderableCharacter() {
      renderableComponent = new RenderableComponent();
      transformComponent = new TransformComponent();
    }

    @Override
    public RenderableComponent getRenderable() {
      return renderableComponent;
    }

    @Override
    public TransformComponent getTransform() {
      return transformComponent;
    }

    public void updateTransform(Vector2f position, Vector2f size, Vector2f viewport) {
      float width = 2f * size.x / viewport.x;
      float height = 2f * size.y / viewport.y;
      transformComponent.setScale(width, height);
      transformComponent.setDisplacement(
          2f * position.x / viewport.x - 1 + width / 2f,
          2f * -position.y / viewport.y + 1 - height / 2f);
      transformComponent.update(null);
    }

    public void cleanup() {
      renderableComponent.cleanUp();
      transformComponent.cleanUp();
    }
  }
}
