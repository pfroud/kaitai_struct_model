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
import java.lang.reflect.Method;
import java.util.ArrayList;
import static java.util.Collections.enumeration;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import javax.swing.tree.TreeNode;

/**
 * Node, that represents single {@link KaitaiStruct} object. Each struct field
 * represented as child node.
 *
 * @author Mingun
 */
public class StructNode extends BaseNode {
  private final KaitaiStruct value;
  /** Array of getters of fields for storen struct. */
  private final Method[] getters;
  /** Lazy populated list of child nodes. */
  private ArrayList<BaseNode> children;

  private final Map<String, Integer> attrStart;
  private final Map<String, Integer> attrEnd;
  private final Map<String, ? extends List<Integer>> arrStart;
  private final Map<String, ? extends List<Integer>> arrEnd;

  StructNode(String name, KaitaiStruct value, BaseNode parent, int start, int end) throws ReflectiveOperationException {
    super(name, parent, start, end);
    final Class<?> clazz = value.getClass();
    final String[] names = (String[])clazz.getField("_seqFields").get(null);

    this.value = value;
    this.getters = new Method[names.length];
    for (int i = 0; i < names.length; ++i) {
      this.getters[i] = clazz.getMethod(names[i]);
    }

    this.attrStart = (Map<String, Integer>)clazz.getDeclaredField("_attrStart").get(value);
    this.attrEnd   = (Map<String, Integer>)clazz.getDeclaredField("_attrEnd").get(value);
    this.arrStart  = (Map<String, ? extends List<Integer>>)clazz.getDeclaredField("_arrStart").get(value);
    this.arrEnd    = (Map<String, ? extends List<Integer>>)clazz.getDeclaredField("_arrEnd").get(value);
  }

  @Override
  public KaitaiStruct getValue() { return value; }

  //<editor-fold defaultstate="collapsed" desc="TreeNode">
  @Override
  public BaseNode getChildAt(int childIndex) { return init().get(childIndex); }

  @Override
  public int getChildCount() { return getters.length; }

  @Override
  public int getIndex(TreeNode node) { return init().indexOf(node); }

  @Override
  public boolean getAllowsChildren() { return true; }

  @Override
  public boolean isLeaf() { return false; }

  @Override
  public Enumeration<? extends BaseNode> children() { return enumeration(init()); }
  //</editor-fold>

  @Override
  public String toString() {
    return name + " [" + value.getClass().getSimpleName()
      + ", fields = " + getters.length
      + ", size = " + size()
      + "]";
  }

  private ArrayList<BaseNode> init() {
    if (children == null) {
      children = new ArrayList<>(getters.length);
      for (final Method getter : getters) {
        try {
          children.add(create(getter));
        } catch (ReflectiveOperationException ex) {
          throw new UnsupportedOperationException(ex);
        }
      }
    }
    return children;
  }

  /**
   * Creates tree node for the specified struct field
   *
   * @param getter Method, that used to get data from structure
   * @return New tree node object, that represents value in the tree
   *
   * @throws ReflectiveOperationException If kaitai class was genereted without
   *         debug info (which includes position information)
   */
  private BaseNode create(Method getter) throws ReflectiveOperationException {
    final Object field = getter.invoke(value);
    final String name  = getter.getName();
    final int s = attrStart.get(name);
    final int e = attrEnd.get(name);
    if (List.class.isAssignableFrom(getter.getReturnType())) {
      final List<Integer> sa = arrStart.get(name);
      final List<Integer> se = arrEnd.get(name);
      return new ListNode(name, (List<?>)field, parent, s, e, sa, se);
    }
    return create(name, field, s, e);
  }
}
