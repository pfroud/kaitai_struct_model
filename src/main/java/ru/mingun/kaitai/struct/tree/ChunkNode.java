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

import io.kaitai.struct.KaitaiStruct;
import javax.swing.tree.TreeNode;

/**
 * Base node for all nodes in the tree, that represents parts of message in
 * KaitaiStruct format, that occupied some region in the file.
 *
 * @author Mingun
 */
public abstract class ChunkNode extends ValueNode {
  /** Start offset of parent node in root stream (i.e. global offset). */
  protected final long offset;
  /** Position in parsed stream where this node begins. */
  protected final long start;
  /** Position in parsed stream where this node ends (exclusive). */
  protected final long end;

  ChunkNode(String name, TreeNode parent, long offset, long start, long end) {
    super(name, parent);
    this.offset = offset;
    this.start = start;
    this.end = end;
  }

  /** Position in root stream where parent of this node begins. */
  public long getParentStart() { return offset; }
  /** Position in root stream where this node begins. */
  public long getStart() { return offset + start; }
  /** Position position in root stream where this node ends (exclusive). */
  public long getEnd() { return offset + end; }
  /** Local position in parsed stream where this node begins. */
  public long getLocalStart() { return start; }
  /** Local position in parsed stream where this node ends (exclusive). */
  public long getLocalEnd() { return end; }
  /**
   * Returns occupied size in bytes in the stream.
   *
   * @return Size of this node in bytes
   */
  public long size() { return end - start; }

  /**
   * Creates tree node for object.
   *
   * @param name Name of field, under which this field arrives
   * @param value Value of object
   * @param offset Byte offset from begin of root stream of parent node
   * @param start Byte offset from begin of stream, where that object starts
   * @param end Byte offset from begin of stream, where that object ends (exclusive)
   * @return
   *
   * @throws ReflectiveOperationException If {@code value} is {@link KaitaiStruct}
   *         and it was compiled without debug info (which includes position information)
   */
  protected ChunkNode create(String name, Object value, Class<?> valueClass, long offset, long start, long end) throws ReflectiveOperationException {
    return value instanceof KaitaiStruct
      ? new StructNode(name, (KaitaiStruct)value, this, offset, start, end)
      : new SimpleNode(name, value, valueClass, this, offset, start, end);
  }
}
