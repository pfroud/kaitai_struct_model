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
package ru.mingun.kaitai.struct;

/**
 *
 * @author Mingun
 */
public class Span {
  /** Position in a parsed stream where value begins. */
  private final long start;
  /** Position in a parsed stream where value ends (exclusive). */
  private final long end;

  /**
   * Creates a span that tree node occupies in a stream.
   *
   * @param start Byte offset from the begin of a root stream, where that object starts
   * @param end Byte offset from the begin of a root stream, where that object ends (exclusive)
   */
  public Span(long start, long end) {
    this.start = start;
    this.end = end;
  }

  /** Position in a root stream where value begins. */
  public long getStart() { return start; }
  /** Position in a root stream where value ends (exclusive). */
  public long getEnd() { return end; }
  /**
   * Returns occupied size in bytes in the stream.
   *
   * @return Size of this node in bytes
   */
  public long size() { return end - start; }
}
