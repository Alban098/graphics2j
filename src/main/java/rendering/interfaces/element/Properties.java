/*
 * Copyright (c) 2022-2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.interfaces.element;

import java.util.function.BiConsumer;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import rendering.Texture;

public class Properties {

  private Snapshot snapshot;
  private final BiConsumer<Snapshot, Snapshot> onChange;
  private float cornerRadius = 0;
  private float borderWidth = 0;
  private final Vector4f backgroundColor = new Vector4f();
  private final Vector3f borderColor = new Vector3f(0, 0, 0);
  private final Vector2f position = new Vector2f();
  private final Vector2f size = new Vector2f();
  private Texture backgroundTexture;
  private float fontSize = 16;
  private String fontFamily = "Candara";
  private final Vector4f fontColor = new Vector4f(1);
  private float fontWidth = 0.5f;
  private float fontBlur = 0.2f;

  public Properties(BiConsumer<Snapshot, Snapshot> onChange) {
    this.onChange = onChange;
    snapshot = new Snapshot(this);
  }

  public float getCornerRadius() {
    return cornerRadius;
  }

  public Properties setCornerRadius(float cornerRadius) {
    this.cornerRadius = cornerRadius;
    notifyObserver();
    return this;
  }

  public float getBorderWidth() {
    return borderWidth;
  }

  public Properties setBorderWidth(float borderWidth) {
    this.borderWidth = borderWidth;
    notifyObserver();
    return this;
  }

  public Vector4f getBackgroundColor() {
    return backgroundColor;
  }

  public Properties setBackgroundColor(Vector4f backgroundColor) {
    this.backgroundColor.set(backgroundColor);
    notifyObserver();
    return this;
  }

  public Properties setBackgroundColor(float r, float g, float b, float a) {
    this.backgroundColor.set(r, g, b, a);
    notifyObserver();
    return this;
  }

  public Vector3f getBorderColor() {
    return borderColor;
  }

  public Properties setBorderColor(Vector3f borderColor) {
    this.borderColor.set(borderColor);
    notifyObserver();
    return this;
  }

  public Properties setBorderColor(float r, float g, float b) {
    this.borderColor.set(r, g, b);
    notifyObserver();
    return this;
  }

  public Texture getBackgroundTexture() {
    return backgroundTexture;
  }

  public Properties setBackgroundTexture(Texture backgroundTexture) {
    this.backgroundTexture = backgroundTexture;
    notifyObserver();
    return this;
  }

  public Vector2f getPosition() {
    return position;
  }

  public Vector2f getSize() {
    return size;
  }

  public Properties setPosition(Vector2f position) {
    this.position.set(position);
    notifyObserver();
    return this;
  }

  public Properties setSize(Vector2f size) {
    this.size.set(size);
    notifyObserver();
    return this;
  }

  public Properties setPosition(float x, float y) {
    this.position.set(x, y);
    notifyObserver();
    return this;
  }

  public Properties setSize(float x, float y) {
    this.size.set(x, y);
    notifyObserver();
    return this;
  }

  private void notifyObserver() {
    Snapshot snapshot = new Snapshot(this);
    onChange.accept(this.snapshot, snapshot);
    this.snapshot = snapshot;
  }

  public Vector4f getFontColor() {
    return fontColor;
  }

  public Properties setFontColor(Vector4f fontColor) {
    this.fontColor.set(fontColor);
    notifyObserver();
    return this;
  }

  public Properties setFontColor(float r, float g, float b, float a) {
    this.fontColor.set(r, g, b, a);
    notifyObserver();
    return this;
  }

  public float getFontSize() {
    return fontSize;
  }

  public Properties setFontSize(float fontSize) {
    this.fontSize = fontSize;
    notifyObserver();
    return this;
  }

  public String getFontFamily() {
    return fontFamily;
  }

  public Properties setFontFamily(String fontFamily) {
    this.fontFamily = fontFamily;
    notifyObserver();
    return this;
  }

  public float getFontWidth() {
    return fontWidth;
  }

  public Properties setFontWidth(float fontWidth) {
    this.fontWidth = fontWidth;
    notifyObserver();
    return this;
  }

  public float getFontBlur() {
    return fontBlur;
  }

  public Properties setFontBlur(float fontBlur) {
    this.fontBlur = fontBlur;
    notifyObserver();
    return this;
  }

  public static class Snapshot {
    private final float cornerRadius;
    private final float borderWidth;
    private final Vector4f backgroundColor = new Vector4f();
    private final Vector3f borderColor = new Vector3f();
    private final Vector2f position = new Vector2f();
    private final Vector2f size = new Vector2f();
    private final Texture backgroundTexture;
    private final float fontSize;
    private final float fontWidth;
    private final float fontBlur;
    private final String fontFamily;
    private final Vector4f fontColor = new Vector4f();

    public Snapshot(Properties properties) {
      this.cornerRadius = properties.cornerRadius;
      this.borderWidth = properties.borderWidth;
      this.backgroundTexture = properties.backgroundTexture;
      this.backgroundColor.set(properties.backgroundColor);
      this.borderColor.set(properties.borderColor);
      this.position.set(properties.position);
      this.size.set(properties.size);
      this.fontSize = properties.fontSize;
      this.fontFamily = properties.fontFamily;
      this.fontBlur = properties.fontBlur;
      this.fontWidth = properties.fontWidth;
      this.fontColor.set(properties.fontColor);
    }

    public float getCornerRadius() {
      return cornerRadius;
    }

    public float getBorderWidth() {
      return borderWidth;
    }

    public Vector4f getBackgroundColor() {
      return backgroundColor;
    }

    public Vector3f getBorderColor() {
      return borderColor;
    }

    public Vector2f getPosition() {
      return position;
    }

    public Vector2f getSize() {
      return size;
    }

    public Texture getBackgroundTexture() {
      return backgroundTexture;
    }

    public float getFontSize() {
      return fontSize;
    }

    public String getFontFamily() {
      return fontFamily;
    }

    public Vector4f getFontColor() {
      return fontColor;
    }

    public float getFontWidth() {
      return fontWidth;
    }

    public float getFontBlur() {
      return fontBlur;
    }
  }
}
