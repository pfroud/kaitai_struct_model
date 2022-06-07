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
import javax.swing.tree.TreeNode;
import ru.mingun.kaitai.struct.Span;

/**
 * Base node for all nodes in the tree, that represents parts of message in
 * KaitaiStruct format, that occupied some region in the file.
 *
 * @author Mingun
 */
public abstract class ChunkNode extends ValueNode {
  /** Space that this node occupies in a stream. */
  protected final Span span;

  /** {@code true} if this node came from {@code seq}, {@code false} if it came from {@code instances}. */
  protected final boolean isSequential;

  ChunkNode(String name, TreeNode parent, Span span, boolean isSequential) {
    super(name, parent);
    this.span = span;
    this.isSequential = isSequential;
  }

  /**
   * Space that this node occupies in a stream or {@code null} for missing
   * optional fields and calculated values ("value" instances).
   */
  public Span getSpan() { return span; }
  /**
   * {@code true} if this node came from {@code seq}, {@code false} if it came from {@code instances}.
   *
   * Note: because of technical limitations, parameters not distinguishable from instances, so
   * parameters represented as instances and this method will return {@code true} for parameter nodes.
   */
  public boolean isSequential() { return isSequential; }

  /**
   * Creates tree node for object.
   *
   * @param name Name of field, under which this field arrives
   * @param value Value of object
   * @param span Space that node is occupied in a stream
   * @param isSequential If {@code true}, field declared in the {@code seq} section of the type,
   *        otherwise it is declared in the {@code instances} section
   *
   * @return
   *
   * @throws ReflectiveOperationException If {@code value} is {@link KaitaiStruct}
   *         and it was compiled without debug info (which includes position information)
   */
  protected ChunkNode create(String name, Object value, Span span, boolean isSequential) throws ReflectiveOperationException {
    return value instanceof KaitaiStruct
      ? new StructNode(name, (KaitaiStruct)value, this, span, isSequential)
      : new SimpleNode(name, value, this, span, isSequential);
  }
}
