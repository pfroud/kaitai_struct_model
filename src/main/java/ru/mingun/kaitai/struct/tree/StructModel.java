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
import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

/**
 * Model, that represents specified structure in tree with its fields.
 *
 * @author Mingun
 */
public class StructModel implements TreeModel {
  private final StructNode root;
  private final EventListenerList listeners = new EventListenerList();

  /**
   * Creates read-only model for specified structure with name {@code "<root>"}.
   *
   * @param value the root structure, represented by this node
   *
   * @throws ReflectiveOperationException If kaitai class was genereted without
   *         debug info (which includes position information)
   */
  public StructModel(KaitaiStruct value) throws ReflectiveOperationException {
    this("<root>", value);
  }

  /**
   * Creates read-only model for specified structure with specified name.
   *
   * @param name displayed name of the structure
   * @param value the root structure, represented by this node
   *
   * @throws ReflectiveOperationException If kaitai class was genereted without
   *         debug info (which includes position information)
   */
  public StructModel(String name, KaitaiStruct value) throws ReflectiveOperationException {
    this.root = new StructNode(name, value, null);
  }

  //<editor-fold defaultstate="collapsed" desc="TreeModel">
  @Override
  public StructNode getRoot() { return root; }

  @Override
  public TreeNode getChild(Object parent, int index) {
    if (parent instanceof TreeNode) {
      return ((TreeNode)parent).getChildAt(index);
    }
    return null;
  }

  @Override
  public int getChildCount(Object parent) {
    if (parent instanceof TreeNode) {
      return ((TreeNode)parent).getChildCount();
    }
    return 0;
  }

  @Override
  public boolean isLeaf(Object node) {
    if (node instanceof TreeNode) {
      return ((TreeNode)node).isLeaf();
    }
    return false;
  }

  @Override
  public void valueForPathChanged(TreePath path, Object newValue) {
    // immutable
  }

  @Override
  public int getIndexOfChild(Object parent, Object child) {
    if (parent instanceof TreeNode && child instanceof TreeNode) {
      final TreeNode nodeParent = (TreeNode)parent;
      for (int i = 0; i < nodeParent.getChildCount(); i++) {
        final TreeNode nodeChild = nodeParent.getChildAt(i);
        if (nodeChild == child) {
          return i;
        }
      }
    }
    return -1;
  }

  @Override
  public void addTreeModelListener(TreeModelListener l) {
    listeners.add(TreeModelListener.class, l);
  }

  @Override
  public void removeTreeModelListener(TreeModelListener l) {
    listeners.remove(TreeModelListener.class, l);
  }
  //</editor-fold>
}
