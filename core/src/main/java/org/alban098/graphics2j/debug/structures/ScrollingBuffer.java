/*
 * Copyright (c) 2023, @Author Alban098
 *
 * Code licensed under MIT license.
 */
package org.alban098.graphics2j.debug.structures;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

/**
 * A Buffer of fixed size acting kind like a queue discarding the oldest value when size is exceeded
 *
 * @param <T> the type of data to store
 */
public final class ScrollingBuffer<T> implements Iterable<Map.Entry<Number, T>> {

  /** An array holding all values of the Buffer */
  private final Object[] values;
  /** An Array holding the indices of the values in the Buffer */
  private final Number[] indices;
  /** The capacity of the Buffer */
  private final int capacity;
  /** At which position does the oldest value of the Buffer is in the array */
  private int offset = 0;

  /**
   * Creates a new {@link ScrollingBuffer} of set size
   *
   * @param capacity the capacity of the Buffer
   */
  public ScrollingBuffer(int capacity) {
    this.values = new Double[capacity];
    this.indices = new Long[capacity];
    this.capacity = capacity;
    Arrays.fill(values, 0d);
    Arrays.fill(indices, 0L);
  }

  /**
   * Adds a new values to the buffer, with set index
   *
   * @param index the index of the value to add
   * @param value the value to add
   */
  public void push(long index, T value) {
    values[offset] = value;
    indices[offset] = index;
    offset = (offset + 1) % capacity;
  }

  /**
   * Returns the array of values of the Buffer
   *
   * @return the array of values of the Buffer
   */
  public T[] getValues() {
    return (T[]) values;
  }

  /**
   * Returns the current offset of the Buffer
   *
   * @return the current offset of the Buffer
   */
  public int getOffset() {
    return offset;
  }

  /**
   * Returns the array of indices of the Buffer
   *
   * @return the array of indices of the Buffer
   */
  public Number[] getIndices() {
    return indices;
  }

  @Override
  public Iterator<Map.Entry<Number, T>> iterator() {
    return new Iterator<>() {

      private int currentIndex = offset;
      private boolean first = true;

      @Override
      public boolean hasNext() {
        return values[currentIndex] != null && (currentIndex != offset || first);
      }

      @Override
      public Map.Entry<Number, T> next() {
        Map.Entry<Number, T> entry =
            new AbstractMap.SimpleImmutableEntry<>(indices[currentIndex], (T) values[currentIndex]);
        currentIndex = (currentIndex + 1) % capacity;
        first = false;
        return entry;
      }
    };
  }
}
