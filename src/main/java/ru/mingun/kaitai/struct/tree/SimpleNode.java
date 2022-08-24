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

import ru.mingun.kaitai.struct.Span;

import javax.swing.tree.TreeNode;
import java.lang.reflect.Field;
import java.util.Enumeration;

import static java.util.Collections.emptyEnumeration;

/**
 * Node, that represents any simple object (such as {@code byte[]}, {@link Integer} or {@link String}. Doesn't have
 * child nodes.
 *
 * @author Mingun
 */
public class SimpleNode extends ChunkNode {
    /** Parsed value of non-constructed type. */
    private final Object value;

    /** Static type of {@code value}, to identify the type when value is null. */
    private final Class<?> valueClass;

    SimpleNode(String name, Object value, Class<?> valueClass, ChunkNode parent, Span span, boolean isSequential) {
        super(name, parent, span, isSequential);
        this.value = value;
        this.valueClass = valueClass;
    }

    @Override
    public Object getValue() {
        return value;
    }

    /** Static type of {@code value}, to identify the type when value is {@code null}. */
    public Class<?> getValueClass() {
        return valueClass;
    }

    //<editor-fold defaultstate="collapsed" desc="TreeNode">
    @Override
    public ChunkNode getChildAt(int childIndex) {
        throw new IndexOutOfBoundsException("SimpleNode has no child nodes (childIndex = " + childIndex + ")");
    }

    @Override
    public int getChildCount() {
        return 0;
    }

    @Override
    public int getIndex(TreeNode node) {
        return -1;
    }

    @Override
    public boolean getAllowsChildren() {
        return false;
    }

    @Override
    public boolean isLeaf() {
        return true;
    }

    @Override
    public Enumeration<? extends ChunkNode> children() {
        return emptyEnumeration();
    }
    //</editor-fold>

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("<html>").append(name).append(" = ");

        if (value == null) {
            sb.append("null");

        } else if (value instanceof Enum) {
            sb.append("<b>").append(value).append("</b>");

            try {
                final Field idField = value.getClass().getDeclaredField("id");
                idField.setAccessible(true);
                final Object get = idField.get(value);
                final long castLong = (long) get;
                sb.append("<font color=").append(NodeStyle.COLOR_ENUM_DESCRIPTION).append(">(0x")
                        .append(Long.toHexString(castLong))
                        .append(" = ")
                        .append(castLong)
                        .append(")</font>");
            } catch (ReflectiveOperationException ex) {
                ex.printStackTrace();
            }

        } else if (value instanceof String || value instanceof Float || value instanceof Double || value instanceof Boolean) {
            sb.append("<b>").append(value).append("</b>");

        } else if(value instanceof Byte){
            final byte b = (byte) value;
            sb.append("<b>0x").append(Integer.toHexString(b))
                    .append("<font color=").append(NodeStyle.COLOR_INTEGER_VALUE).append("> = ")
                    .append(b).append("</font>").append("</b>");

        } else if(value instanceof Short){
            final short s = (short) value;
            sb.append("<b>0x").append(Integer.toHexString(s))
                    .append("<font color=").append(NodeStyle.COLOR_INTEGER_VALUE).append("> = ")
                    .append(s).append("</font>").append("</b>");

        } else if(value instanceof Integer){
            final int i = (int) value;
            sb.append("<b>0x").append(Integer.toHexString(i))
                    .append("<font color=").append(NodeStyle.COLOR_INTEGER_VALUE).append("> = ")
                    .append(i).append("</font>").append("</b>");

        } else if(value instanceof Long){
            final long l = (long) value;
            sb.append("<b>0x").append(Long.toHexString(l))
                    .append("<font color=").append(NodeStyle.COLOR_INTEGER_VALUE).append("> = ")
                    .append(l).append("</font>").append("</b>");
        } else{
            System.out.println("Don't know what to do with "+value.getClass() +": "+value);
        }
        return sb.toString();
    }
}
