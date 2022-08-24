/*
 * The MIT License
 *
 * Copyright 2020-2021 Mingun.
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

import static java.util.Collections.emptyEnumeration;
import java.util.Enumeration;
import javax.swing.tree.TreeNode;

/**
 * Node, that represents any simple object (such as {@code byte[]}, {@link Integer}
 * or {@link String}. Doesn't have child nodes.
 *
 * @author Mingun
 */
public class ParamNode extends ValueNode {
  /** Value of the parameter. */
  private final Object value;

  ParamNode(String name, Object value, StructNode parent) {
    super(name, parent);
    this.value = value;
  }

  @Override
  public Object getValue() { return value; }

  //<editor-fold defaultstate="collapsed" desc="TreeNode">
  @Override
  public StructNode getParent() { return (StructNode)parent; }

  @Override
  public TreeNode getChildAt(int childIndex) {
    throw new IndexOutOfBoundsException("ParamNode has no child nodes (childIndex = "+childIndex+")");
  }

  @Override
  public int getChildCount() { return 0; }

  @Override
  public int getIndex(TreeNode node) { return -1; }

  @Override
  public boolean getAllowsChildren() { return false; }

  @Override
  public boolean isLeaf() { return true; }

  @Override
  public Enumeration<? extends TreeNode> children() { return emptyEnumeration(); }
  //</editor-fold>

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder(name);
    appendValue(sb.append(" = "), value);
    return sb.toString();
  }
}
