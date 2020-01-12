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

import java.lang.reflect.Method;
import java.util.ArrayList;
import static java.util.Collections.enumeration;
import java.util.Enumeration;
import javax.swing.tree.TreeNode;

/**
 * Node-container for fields or instances. Each individualt field or instance value
 * represented as child node. Child nodes a lazy populated when node is expended.
 *
 * @author Mingun
 */
public class ChunksNode implements TreeNode {
  /** Name of container. */
  private final String name;
  private final StructNode parent;
  /** Array of getters of fields for stored struct. */
  private final ArrayList<Method> getters;
  /** Lazy populated list of child nodes. */
  private ArrayList<ChunkNode> children;

  ChunksNode(String name, ArrayList<Method> getters, StructNode parent) {
    this.name    = name;
    this.parent  = parent;
    this.getters = getters;
  }

  //<editor-fold defaultstate="collapsed" desc="TreeNode">
  @Override
  public StructNode getParent() { return parent; }

  @Override
  public ChunkNode getChildAt(int childIndex) { return init().get(childIndex); }

  @Override
  public int getChildCount() { return getters.size(); }

  @Override
  public int getIndex(TreeNode node) { return init().indexOf(node); }

  @Override
  public boolean getAllowsChildren() { return true; }

  @Override
  public boolean isLeaf() { return getters.isEmpty(); }

  @Override
  public Enumeration<? extends ChunkNode> children() { return enumeration(init()); }
  //</editor-fold>

  @Override
  public String toString() { return name + " (" + getters.size() + ")"; }

  private ArrayList<ChunkNode> init() {
    if (children == null) {
      children = new ArrayList<>(getters.size());
      for (final Method getter : getters) {
        try {
          children.add(parent.create(getter, this));
        } catch (ReflectiveOperationException ex) {
          throw new UnsupportedOperationException(ex);
        }
      }
    }
    return children;
  }
}
