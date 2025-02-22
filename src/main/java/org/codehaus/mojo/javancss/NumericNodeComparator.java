package org.codehaus.mojo.javancss;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.util.Comparator;

import org.dom4j.Node;

/**
 * Comparator for numeric xml node tag.
 *
 * @author <a href="jeanlaurentATgmail.com">Jean-Laurent de Morlhon</a>
 * @version $Id$
 */
public class NumericNodeComparator implements Comparator<Node> {
    /**
     * the tag property used by this comparator
     */
    private final String tagProperty;

    /**
     * The numeric node tag property to used by this comparator.
     *
     * @param property the tag property used by this comparator.
     */
    public NumericNodeComparator(String property) {
        this.tagProperty = property;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compare(Node node1, Node node2) {
        return node2.numberValueOf(tagProperty).intValue()
                - node1.numberValueOf(tagProperty).intValue();
    }
}
