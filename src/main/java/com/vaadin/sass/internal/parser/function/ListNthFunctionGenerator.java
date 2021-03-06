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
package com.vaadin.sass.internal.parser.function;

import com.vaadin.sass.internal.parser.LexicalUnitImpl;
import com.vaadin.sass.internal.parser.ParseException;
import com.vaadin.sass.internal.parser.SassList;
import com.vaadin.sass.internal.parser.SassListItem;

public class ListNthFunctionGenerator implements SCSSFunctionGenerator {

    @Override
    public String getFunctionName() {
        return "nth";
    }

    @Override
    public SassListItem compute(LexicalUnitImpl function) {
        SassList params = function.getParameterList();
        if (params == null || params.size() != 2) {
            throw new ParseException(
                    "The function nth requires exactly two parameters. Actual parameters: "
                            + params);
        }
        SassListItem listAsItem = params.get(0);
        if (!(listAsItem instanceof SassList)) {
            throw new ParseException(
                    "The first parameter of nth() must be a list. Actual value: "
                            + listAsItem);
        }
        SassListItem nAsItem = params.get(1);
        if (!(nAsItem instanceof LexicalUnitImpl)
                || ((LexicalUnitImpl) nAsItem).getLexicalUnitType() != LexicalUnitImpl.SAC_INTEGER) {
            throw new ParseException(
                    "The second parameter of nth() must be an integer. Actual value: "
                            + nAsItem);
        }
        SassList list = (SassList) listAsItem;
        int n = ((LexicalUnitImpl) nAsItem).getIntegerValue();
        if (n < 1 || n > list.size()) {
            throw new ParseException(
                    "Index out of range for the function nth(). List: " + list
                            + ", index: " + n);
        }
        return list.get(n - 1);
    }
}
