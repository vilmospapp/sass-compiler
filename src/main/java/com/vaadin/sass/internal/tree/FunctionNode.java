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

import java.util.ArrayList;
import java.util.Collections;

import com.vaadin.sass.internal.ScssStylesheet;
import com.vaadin.sass.internal.parser.LexicalUnitImpl;
import com.vaadin.sass.internal.parser.ParseException;
import com.vaadin.sass.internal.parser.SassListItem;

/**
 * Transient class representing a function call to a custom (user-defined)
 * function. This class is used to evaluate the function call and is discarded
 * after use. A FunctionNode does not have a parent in the stylesheet node tree.
 */
public class FunctionNode extends NodeWithVariableArguments {
    private static final long serialVersionUID = -5383104165955523923L;

    private FunctionDefNode def;

    public FunctionNode(FunctionDefNode def, LexicalUnitImpl invocation) {
        super(invocation.getFunctionName(), invocation.getParameterList());

        if (invocation.getParameterList().size() > 0) {
            updateSeparator(
                    invocation.getParameterList().get(
                            invocation.getParameterList().size() - 1),
                    hasVariableArguments());
        }

        this.def = def;
    }

    @Override
    public String toString() {
        // TODO arglist might not be very informative when printed out
        return "Function Node: {name: " + getName() + ", args: " + getArglist()
                + "}";
    }

    public SassListItem evaluate() {
        traverse();
        // already evaluated
        return getReturnNode(getChildren()).getExpr();
    }

    private ReturnNode getReturnNode(ArrayList<Node> defChildren) {
        if (defChildren.size() != 1
                || !(defChildren.get(0) instanceof ReturnNode)) {
            // TODO second parameter not very useful now
            throw new ParseException(
                    "Function "
                            + getName()
                            + " evaluation failed - did not result in one return statement",
                    this);
        }
        return (ReturnNode) defChildren.get(0);
    }

    @Override
    public void doTraverse() {
        // TODO should this be called?
        // replaceVariables(ScssStylesheet.getVariables());

        // copying is necessary as traversal modifies the parent of the
        // node
        FunctionDefNode defCopy = def.copy();
        defCopy.replacePossibleArguments(this);
        for (VariableNode param : defCopy.getArglist()) {
            ScssStylesheet.addVariable(param);
        }

        // only contains variable nodes, return nodes and control
        // structures
        for (Node node : new ArrayList<Node>(defCopy.getChildren())) {
            node.traverse();
        }

        ReturnNode returnNode = getReturnNode(defCopy.getChildren());
        // parameters are already in the scope
        SassListItem result = returnNode.evaluate(Collections
                .<VariableNode> emptyList());

        // now modify this node
        getChildren().clear();
        getChildren().add(new ReturnNode(result));
    }

}
