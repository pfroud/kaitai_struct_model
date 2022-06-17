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

import javax.swing.tree.TreeNode;

/**
 * Base node for all nodes in the tree, that represents objects with name and
 * value -- parameters, parsed field and instances.
 *
 * @author Mingun
 */
public abstract class ValueNode implements TreeNode {
  /** Field name or name of array element (index in brackets). */
  protected final String name;
  protected final TreeNode parent;

  ValueNode(String name, TreeNode parent) {
    this.name = name;
    this.parent = parent;
  }

  /**
   * Returns value object, returned by KaitaiStruct parser. Subclasses returns more
   * concrete classes
   *
   * @return Any object from underlaying Kaitai structure
   */
  public abstract Object getValue();

  /**
   * Returns the Java field name (camelCased name of the Kaitai Struct name, defined in the spec)
   * or a string {@code "[<array index>]"} for array elements.
   */
  public String getName() { return name; }

  //<editor-fold defaultstate="collapsed" desc="TreeNode">
  @Override
  public TreeNode getParent() { return parent; }
  //</editor-fold>

  protected static void toString(StringBuilder sb, Object value) {
    if (value instanceof byte[]) {
      for (final byte b : (byte[])value) {
        sb.append(String.format("%02x ", b));
      }
    } else {
      sb.append(value);
    }
  }
}
