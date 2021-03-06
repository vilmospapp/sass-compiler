/*
 * Copyright 2000-2014 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.vaadin.sass.internal.tree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import com.vaadin.sass.internal.parser.SassListItem;
import com.vaadin.sass.internal.util.DeepCopy;

public abstract class Node implements Serializable {

    public static BuildStringStrategy PRINT_STRATEGY = new PrintStrategy();

    public static BuildStringStrategy TO_STRING_STRATEGY = new ToStringStrategy();

    private static final long serialVersionUID = 5914711715839294816L;

    protected ArrayList<Node> children;

    protected Node parentNode;

    public Node() {
        children = new ArrayList<Node>();
    }

    public void appendAll(Collection<Node> nodes) {
        if (nodes != null && !nodes.isEmpty()) {
            children.addAll(nodes);

            for (final Node n : nodes) {
                if (n.getParentNode() != null) {
                    n.getParentNode().removeChild(n);
                }
                n.setParentNode(this);
            }

        }
    }

    public void appendChildrenAfter(Collection<Node> childrenNodes, Node after) {
        if (childrenNodes != null && !childrenNodes.isEmpty()) {
            int index = children.indexOf(after);
            if (index != -1) {
                children.addAll(index, childrenNodes);
                for (final Node child : childrenNodes) {
                    if (child.getParentNode() != null) {
                        child.getParentNode().removeChild(child);
                    }
                    child.setParentNode(this);
                }
            } else {
                throw new NullPointerException("after-node was not found");
            }
        }
    }

    public void appendChild(Node node) {
        if (node != null) {
            children.add(node);
            if (node.getParentNode() != null) {
                node.getParentNode().removeChild(node);
            }
            node.setParentNode(this);
        }
    }

    public void appendChild(Node node, Node after) {
        if (node != null) {
            int index = children.indexOf(after);
            if (index != -1) {
                children.add(index + 1, node);
                if (node.getParentNode() != null) {
                    node.getParentNode().removeChild(node);
                }
                node.setParentNode(this);
            } else {
                throw new NullPointerException("after-node was not found");
            }
        }
    }

    public void removeChild(Node node) {
        if (node != null) {
            boolean removed = children.remove(node);
            if (removed) {
                node.setParentNode(null);
            }
        }
    }

    public ArrayList<Node> getChildren() {
        return children;
    }

    public void setChildren(ArrayList<Node> children) {
        this.children = children;
    }

    public boolean hasChildren() {
        return !children.isEmpty();
    }

    /**
     * Method for manipulating the data contained within the {@link Node}.
     * 
     * Traversing a node is allowed to modify the node, replace it with one or
     * more nodes at the same or later position in its parent and modify the
     * children of the node, but not modify or remove preceding nodes in its
     * parent.
     */
    public abstract void traverse();

    /**
     * Prints out the current state of the node tree. Will return SCSS before
     * compile and CSS after.
     * 
     * Result value could be null.
     * 
     * @return State as a string
     */
    public String printState() {
        return null;
    }

    public Node getParentNode() {
        return parentNode;
    }

    private void setParentNode(Node parentNode) {
        this.parentNode = parentNode;
    }

    public Node copy() {
        Node oldParent = parentNode;
        parentNode = null;
        Node copy = (Node) DeepCopy.copy(this);
        copy.parentNode = oldParent;
        parentNode = oldParent;
        return copy;
    }

    public static interface BuildStringStrategy {

        String build(Node node);

        String build(SassListItem expr);
    }

    public static class PrintStrategy implements BuildStringStrategy {

        @Override
        public String build(Node node) {
            return node.printState();
        }

        @Override
        public String build(SassListItem expr) {
            return expr.printState();
        }

    }

    public static class ToStringStrategy implements BuildStringStrategy {

        @Override
        public String build(Node node) {
            return node.toString();
        }

        @Override
        public String build(SassListItem expr) {
            return expr.toString();
        }

    }

}
