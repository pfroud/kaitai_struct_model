/*
 * The MIT License
 *
 * Copyright 2020-2022 Mingun.
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
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import static java.util.Collections.enumeration;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import javax.swing.tree.TreeNode;
import ru.mingun.kaitai.struct.Span;

/**
 * Node, that represents single {@link KaitaiStruct} object. Each struct field
 * represented as child node.
 *
 * @author Mingun
 */
public class StructNode extends ChunkNode {
  private final KaitaiStruct value;
  /** Array of getters of fields for stored fields. */
  private final ArrayList<Method> fields;
  /** Array of getters of fields for parameters and instances. */
  private final ArrayList<Method> instances;
  /** Lazy populated list of child nodes. */
  private ArrayList<ChunkNode> children;

  private final Map<String, Integer> attrStart;
  private final Map<String, Integer> attrEnd;
  private final Map<String, ? extends List<Integer>> arrStart;
  private final Map<String, ? extends List<Integer>> arrEnd;

  /**
   * Constructor used to create node for representing root structure.
   *
   * @param name displayed name of the structure
   * @param value the root structure, represented by this node
   * @param parent parent node, for including node in hierarchy
   *
   * @throws ReflectiveOperationException If kaitai class was genereted without
   *         debug info (which includes position information)
   */
  public StructNode(String name, KaitaiStruct value, TreeNode parent) throws ReflectiveOperationException {
    this(name, value, parent, new Span(0, value._io().pos()), true);
  }
  StructNode(String name, KaitaiStruct value, TreeNode parent, Span span, boolean isSequential) throws ReflectiveOperationException {
    super(name, parent, span, isSequential);
    final Class<?> clazz = value.getClass();
    // getDeclaredMethods() doesn't guaranties any particular order, so sort fields
    // according order in the type
    final String[] names = (String[])clazz.getField("_seqFields").get(null);
    final List<String> order = Arrays.asList(names);

    this.instances = new ArrayList<>();
    this.fields = new ArrayList<>();
    for (final Method m : clazz.getDeclaredMethods()) {
      // Skip static methods, i.e. "fromFile"
      // Skip all internal methods, i.e. "_io", "_parent", "_root"
      if (Modifier.isStatic(m.getModifiers()) || m.getName().charAt(0) == '_') {
        continue;
      }
      if (order.contains(m.getName())) {
        fields.add(m);
      } else {
        // TODO: Distinguish between parameters and instances
        instances.add(m);
      }
    }

    fields.sort((Method m1, Method m2) -> {
      final int pos1 = order.indexOf(m1.getName());
      final int pos2 = order.indexOf(m2.getName());
      return pos1 - pos2;
    });

    this.value     = value;
    this.children  = null;
    this.attrStart = (Map<String, Integer>)clazz.getDeclaredField("_attrStart").get(value);
    this.attrEnd   = (Map<String, Integer>)clazz.getDeclaredField("_attrEnd").get(value);
    this.arrStart  = (Map<String, ? extends List<Integer>>)clazz.getDeclaredField("_arrStart").get(value);
    this.arrEnd    = (Map<String, ? extends List<Integer>>)clazz.getDeclaredField("_arrEnd").get(value);
  }

  @Override
  public KaitaiStruct getValue() { return value; }

  //<editor-fold defaultstate="collapsed" desc="TreeNode">
  @Override
  public TreeNode getChildAt(int childIndex) { return init().get(childIndex); }

  @Override
  public int getChildCount() { return fields.size() + instances.size(); }

  @Override
  public int getIndex(TreeNode node) {
    // If node is not initialized then node is not our child
    if (node == null || children == null) {
      return -1;
    }
    return init().indexOf(node);
  }

  @Override
  public boolean getAllowsChildren() { return true; }

  @Override
  public boolean isLeaf() { return false; }

  @Override
  public Enumeration<? extends TreeNode> children() { return enumeration(init()); }
  //</editor-fold>

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder(name);
    sb.append(" [").append(value.getClass().getSimpleName())
      .append("; fields = ").append(fields.size());
    if (span != null) {
      sb.append("; offset = ").append(span.getStart())
        .append("; size = ").append(span.size());
    }
    return sb.append(']').toString();
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
  private ChunkNode create(Method getter, boolean isSequential) throws ReflectiveOperationException {
    final Object field = getter.invoke(value);
    final String name  = getter.getName();
    // Optional field could be not presented in the maps if it missing in input
    // "value" instances doesn't present in the maps
    final Integer s = attrStart.get(name);
    final Integer e = attrEnd.get(name);
    final boolean isPresent = s != null && e != null;

    final Span span = isPresent ? new Span(s, e) : null;
    // isPresent filters out "value" instances with List content
    if (isPresent && List.class.isAssignableFrom(getter.getReturnType())) {
      final List<Integer> sa = arrStart.get(name);
      final List<Integer> ea = arrEnd.get(name);
      return new ListNode(name, (List<?>)field, this, span, isSequential, sa, ea);
    }
    return create(name, field, span, isSequential);
  }

  private ArrayList<ChunkNode> init() {
    if (children == null) {
      children = new ArrayList<>();
      try {
        for (final Method getter : fields) {
          children.add(create(getter, true));
        }
        for (final Method getter : instances) {
          children.add(create(getter, false));
        }
      } catch (ReflectiveOperationException ex) {
        throw new UnsupportedOperationException(ex);
      }
    }
    return children;
  }
}
