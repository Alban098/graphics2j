/*
 * Copyright (c) 2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.physics2j;

import java.util.*;
import org.alban098.common.Transform;
import org.alban098.graphics2j.common.Renderable;
import org.alban098.graphics2j.common.RenderableComponent;
import org.alban098.graphics2j.common.components.RenderElement;
import org.alban098.graphics2j.common.shaders.data.model.Models;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class QuadTree<T extends Physical> implements Iterable<T> {

  private static final int DEFAULT_NODE_CAPACITY = 8;
  private static final float MERGE_THRESHOLD = 0.8f;

  public Collection<Node<T>> getAllLeafs() {
    Collection<Node<T>> nodes = new ArrayList<>(size);
    root.accumulateLeafs(nodes);
    return nodes;
  }

  static class BoundingBox {
    private Vector2f centerPoint;
    private Vector2f size;

    public BoundingBox(Vector2f size, Vector2f centerPoint) {
      this.centerPoint = centerPoint;
      this.size = size;
    }
  }

  public static class Node<T extends Physical> implements Renderable {

    private static final RenderableComponent DEFAULT =
        new RenderableComponent(new RenderElement(new Vector4f(1f, 0f, 0f, 1f), Models.QUAD), "node");
    private final Transform transform;

    @Override
    public RenderableComponent getRenderableComponent() {
      return DEFAULT;
    }

    @Override
    public Transform getTransform() {
      return transform;
    }

    public void accumulateLeafs(Collection<Node<T>> nodes) {
      if (children == null || children.isEmpty()) {
        nodes.add(this);
      }
      if (children != null) {
        children.forEach(child -> child.accumulateLeafs(nodes));
      }
    }

    enum Region {
      TOP_LEFT,
      TOP_RIGHT,
      BOTTOM_LEFT,
      BOTTOM_RIGHT
    }

    private final int capacity;
    private final Set<T> elements;
    private ArrayList<Node<T>> children;

    private final BoundingBox boundingBox;

    public Node(int capacity, Vector2f size, Vector2f center) {
      this.capacity = capacity;
      this.elements = new HashSet<>();
      this.boundingBox = new BoundingBox(size, center);
      this.transform = new Transform();
      this.transform.setDisplacement(center);
      this.transform.setScale(size);
    }

    public boolean add(T element) {
      if (children != null && !children.isEmpty()) {
        return getRegion(element).add(element);
      }
      if (elements.size() == capacity) {
        split();
        elements.clear();
        return getRegion(element).add(element);
      }
      elements.add(element);
      return true;
    }

    private boolean remove(T element) {
      if (children == null || children.isEmpty()) {
        return this.elements.remove(element);
      }
      if (getRegion(element).remove(element)) {
        boolean hasChildren = false;
        int count = 0;
        for (Node<T> child : children) {
          hasChildren |= child.hasChildren();
          count += child.elements.size();
        }
        if (!hasChildren && count < capacity * MERGE_THRESHOLD) {
          merge();
        }
        return true;
      }
      return false;
    }

    public boolean contains(T element) {
      if (children == null || children.isEmpty()) {
        return elements.contains(element);
      }
      return getRegion(element).contains(element);
    }

    public void clear() {
      if (children == null || children.isEmpty()) {
        elements.clear();
      } else {
        for (Node<T> child : children) {
          child.clear();
        }
        this.children.clear();
      }
    }

    public boolean hasElements() {
      if (children == null || children.isEmpty()) {
        return !elements.isEmpty();
      }
      for (Node<T> child : children) {
        if (child.hasElements()) {
          return true;
        }
      }
      return false;
    }

    private void split() {
      // Array will not trigger reallocation because size will never exceed 4, and reallocation is
      // only trigger when size exceed allocated size
      children = new ArrayList<>(4);

      Vector2f size = new Vector2f(this.boundingBox.size).div(2);
      Vector2f offset = new Vector2f(size).div(2);

      children.add(
          Region.TOP_LEFT.ordinal(),
          new Node<>(
              capacity, size, new Vector2f(boundingBox.centerPoint).add(-offset.x, offset.y)));
      children.add(
          Region.TOP_RIGHT.ordinal(),
          new Node<>(
              capacity, size, new Vector2f(boundingBox.centerPoint).add(offset.x, offset.y)));
      children.add(
          Region.BOTTOM_LEFT.ordinal(),
          new Node<>(
              capacity, size, new Vector2f(boundingBox.centerPoint).add(-offset.x, -offset.y)));
      children.add(
          Region.BOTTOM_RIGHT.ordinal(),
          new Node<>(
              capacity, size, new Vector2f(boundingBox.centerPoint).add(offset.x, -offset.y)));

      for (T element : elements) {
        getRegion(element).add(element);
      }
    }

    private void merge() {
      for (Node<T> child : children) {
        elements.addAll(child.elements);
        child.clear();
      }
      this.children.clear();
    }

    private Node<T> getRegion(T element) {
      Vector2f displacement = element.getTransform().getDisplacement();
      if (displacement.x > boundingBox.centerPoint.x) {
        if (displacement.y > boundingBox.centerPoint.y) {
          return children.get(Region.TOP_RIGHT.ordinal());
        } else {
          return children.get(Region.BOTTOM_RIGHT.ordinal());
        }
      } else {
        if (displacement.y > boundingBox.centerPoint.y) {
          return children.get(Region.TOP_LEFT.ordinal());
        } else {
          return children.get(Region.BOTTOM_LEFT.ordinal());
        }
      }
    }

    private boolean hasChildren() {
      return children != null && !children.isEmpty();
    }
  }

  private final Node<T> root;
  private int size = 0;

  public QuadTree() {
    this(DEFAULT_NODE_CAPACITY);
  }

  public QuadTree(int nodeCapacity) {
    this(nodeCapacity, new Vector2f(Float.MAX_VALUE));
  }

  public QuadTree(Vector2f size) {
    this(DEFAULT_NODE_CAPACITY, size);
  }

  public QuadTree(int nodeCapacity, Vector2f size) {
    this(nodeCapacity, size, new Vector2f(0));
  }

  public QuadTree(Vector2f size, Vector2f position) {
    this(DEFAULT_NODE_CAPACITY, size, position);
  }

  public QuadTree(int nodeCapacity, Vector2f size, Vector2f position) {
    root = new Node<>(nodeCapacity, size, position);
  }

  public int size() {
    return size;
  }

  public boolean isEmpty() {
    return size <= 0;
  }

  public boolean contains(T o) {
    return root.contains(o);
  }

  @Override
  public Iterator<T> iterator() {
    return new QuadTreeIterator(this);
  }

  public boolean add(T t) {
    if (root.add(t)) {
      size++;
      return true;
    }
    return false;
  }

  public boolean remove(T o) {
    if (root.remove(o)) {
      size--;
      return true;
    }
    return false;
  }

  public boolean containsAll(Collection<T> c) {
    for (T elem : c) {
      if (!root.contains(elem)) {
        return false;
      }
    }
    return true;
  }

  public boolean addAll(Collection<T> c) {
    boolean added = true;
    for (T elem : c) {
      added &= add(elem);
    }
    return added;
  }

  public boolean removeAll(Collection<T> c) {
    boolean removed = true;
    for (T elem : c) {
      removed &= remove(elem);
    }
    return removed;
  }

  public void clear() {
    this.root.clear();
    size = 0;
  }

  private class QuadTreeIterator implements Iterator<T> {

    private final Stack<Node<T>> stack = new Stack<>();
    private Iterator<T> currentNodeIterator;

    public QuadTreeIterator(QuadTree<T> quadTree) {
      stack.push(quadTree.root);
      getNextNode();
    }

    private boolean getNextNode() {
      if (stack.isEmpty()) {
        return false;
      }
      Node<T> currentNode = stack.pop();
      currentNodeIterator = currentNode.elements.iterator();
      if (currentNode.children != null) {
        currentNode.children.forEach(stack::push);
      }
      return true;
    }

    @Override
    public boolean hasNext() {
      if (currentNodeIterator.hasNext()) {
        return true;
      }
      if (stack.isEmpty()) {
        return false;
      }
      return stack.peek().hasElements();
    }

    @Override
    public T next() {
      if (currentNodeIterator.hasNext()) {
        return currentNodeIterator.next();
      }
      if (getNextNode()) {
        return next();
      }
      return null;
    }
  }
}
