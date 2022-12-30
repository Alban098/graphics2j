/*
 * Copyright (c) 2022, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package rendering.interfaces.element;

import java.util.function.BiConsumer;
import org.joml.Vector2f;
import org.joml.Vector4f;
import rendering.Texture;

public class Properties {

  private Snapshot snapshot;
  private final BiConsumer<Snapshot, Snapshot> onChange;
  private float cornerRadius;
  private float borderWidth;
  private final Vector4f backgroundColor = new Vector4f();
  private final Vector4f borderColor = new Vector4f();
  private final Vector2f position = new Vector2f();
  private final Vector2f size = new Vector2f();
  private Texture backgroundTexture;

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

  public Vector4f getBorderColor() {
    return borderColor;
  }

  public Properties setBorderColor(Vector4f borderColor) {
    this.borderColor.set(borderColor);
    notifyObserver();
    return this;
  }

  public Properties setBorderColor(float r, float g, float b, float a) {
    this.borderColor.set(r, g, b, a);
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

  public static class Snapshot {
    private final float cornerRadius;
    private final float borderWidth;
    private final Vector4f backgroundColor = new Vector4f();
    private final Vector4f borderColor = new Vector4f();
    private final Vector2f position = new Vector2f();
    private final Vector2f size = new Vector2f();
    private final Texture backgroundTexture;

    public Snapshot(Properties properties) {
      this.cornerRadius = properties.cornerRadius;
      this.borderWidth = properties.borderWidth;
      this.backgroundTexture = properties.backgroundTexture;
      this.backgroundColor.set(properties.backgroundColor);
      this.borderColor.set(properties.borderColor);
      this.position.set(properties.position);
      this.size.set(properties.size);
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

    public Vector4f getBorderColor() {
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
  }
}
