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

import java.util.ArrayList;

import com.vaadin.sass.internal.parser.LexicalUnitImpl;
import com.vaadin.sass.internal.parser.ParseException;
import com.vaadin.sass.internal.parser.SassList;
import com.vaadin.sass.internal.parser.SassListItem;

public class ListJoinFunctionGenerator extends ListFunctionGenerator {

    @Override
    public String getFunctionName() {
        return "join";
    }

    @Override
    public SassListItem compute(LexicalUnitImpl function) {
        SassList params = function.getParameterList();
        if (params == null || params.size() < 2 || params.size() > 3) {
            throw new ParseException(
                    "The function join() must have two or three parameters. Actual parameters: "
                            + params);
        }
        SassListItem firstListAsItem = params.get(0);
        SassListItem secondListAsItem = params.get(1);

        SassList firstList = asList(firstListAsItem);
        SassList secondList = asList(secondListAsItem);
        ArrayList<SassListItem> newList = new ArrayList<SassListItem>();
        for (SassListItem firstItem : firstList) {
            newList.add(firstItem);
        }
        for (SassListItem secondItem : secondList) {
            newList.add(secondItem);
        }

        SassList.Separator sep = null; // this corresponds to "auto"
        if (params.size() == 3) { // get the specified list separator
            sep = getSeparator(params.get(2));
        }
        if (sep == null) { // determine the separator in "auto" mode
            sep = getAutoSeparator(firstList, secondList);
        }
        return new SassList(sep, newList);
    }

}
