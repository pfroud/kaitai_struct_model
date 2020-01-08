/*
 * The MIT License
 *
 * Copyright 2020 Mingun.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package ru.mingun.kaitai.struct.tree;

import io.kaitai.struct.KaitaiStruct;
import java.util.Enumeration;
import javax.swing.tree.TreeNode;

/**
 * Base node for all nodes in the tree, that represents parts of message in
 * KaitaiStruct format.
 *
 * @author Mingun
 */
public abstract class BaseNode implements TreeNode {
  /** Field name or name of array element (index in brackets). */
  protected final String name;
  protected final BaseNode parent;
  /** Position in parsed stream where this node begins. */
  private final int start;
  /** Position in parsed stream where this node ends (exclusive). */
  private final int end;

  BaseNode(String name, BaseNode parent, int start, int end) {
    this.name = name;
    this.parent = parent;
    this.start = start;
    this.end = end;
  }

  //<editor-fold defaultstate="collapsed" desc="TreeNode">
  @Override
  public BaseNode getParent() { return parent; }
  @Override
  public abstract BaseNode getChildAt(int childIndex);
  @Override
  public abstract Enumeration<? extends BaseNode> children();
  //</editor-fold>

  /**
   * Returns value object, returned by KaitaiStruct parser. Subclasses returns more
   * concrete classes
   *
   * @return Any object from underlaying Kaitai structure
   */
  public abstract Object getValue();
  /** Position in parsed stream where this node begins. */
  public int getStart() { return start; }
  /** Position in parsed stream where this node ends (exclusive). */
  public int getEnd() { return end; }
  /**
   * Returns occupied size in bytes in the stream.
   *
   * @return Size of this node in bytes
   */
  public int size() { return end - start; }

  /**
   * Creates tree node for object.
   *
   * @param name Name of field, under which this field arrives
   * @param value Value of object
   * @param start Byte offset from begin of stream, where that object starts
   * @param end Byte offset from begin of stream, where that object ends (exclusive)
   * @return
   *
   * @throws ReflectiveOperationException If {@code value} is {@link KaitaiStruct}
   *         and it was compiled without debug info (which includes position information)
   */
  protected BaseNode create(String name, Object value, int start, int end) throws ReflectiveOperationException {
    return value instanceof KaitaiStruct
      ? new StructNode(name, (KaitaiStruct)value, this, start, end)
      : new SimpleNode(name, value, this, start, end);
  }
}
