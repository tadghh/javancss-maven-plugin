package org.codehaus.mojo.javancss;

import org.dom4j.Node;
import org.junit.Before;
import org.junit.Test;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

/**
 * Test for NumericNodeComparator class.
 *
 * @author <a href="jeanlaurent@gmail.com">Jean-Laurent de Morlhon</a>
 */
public class NumericNodeComparatorTest {
    private static final String NODE_PROPERTY = "foobar";
    private static final Integer SMALL_VALUE = 10;
    private static final Integer BIG_VALUE = 42;

    private Node bigNodeMock;
    private Node smallNodeMock;
    private NumericNodeComparator nnc;

    @Before
    public void setUp() {
        nnc = new NumericNodeComparator(NODE_PROPERTY);
        bigNodeMock = createMock(Node.class);
        smallNodeMock = createMock(Node.class);
    }

    @Test
    public void testComparePositive() {
        expect(bigNodeMock.numberValueOf(NODE_PROPERTY)).andReturn(BIG_VALUE);
        expect(smallNodeMock.numberValueOf(NODE_PROPERTY)).andReturn(SMALL_VALUE);

        replay(bigNodeMock, smallNodeMock);

        assertTrue(nnc.compare(smallNodeMock, bigNodeMock) > 0);

        verify(bigNodeMock, smallNodeMock);
    }

    @Test
    public void testCompareNegative() {
        expect(bigNodeMock.numberValueOf(NODE_PROPERTY)).andReturn(SMALL_VALUE);
        expect(smallNodeMock.numberValueOf(NODE_PROPERTY)).andReturn(BIG_VALUE);

        replay(bigNodeMock, smallNodeMock);

        assertTrue(nnc.compare(smallNodeMock, bigNodeMock) < 0);

        verify(bigNodeMock, smallNodeMock);
    }

    @Test
    public void testCompareEqual() {
        expect(bigNodeMock.numberValueOf(NODE_PROPERTY)).andReturn(SMALL_VALUE);
        expect(smallNodeMock.numberValueOf(NODE_PROPERTY)).andReturn(SMALL_VALUE);

        replay(bigNodeMock, smallNodeMock);

        assertEquals(0, nnc.compare(smallNodeMock, bigNodeMock));

        verify(bigNodeMock, smallNodeMock);
    }

    @Test(expected = NullPointerException.class)
    public void testCompareWithBigNull() {
        expect(smallNodeMock.numberValueOf(NODE_PROPERTY)).andReturn(SMALL_VALUE);
        replay(smallNodeMock);

        nnc.compare(null, smallNodeMock);
    }

    @Test(expected = NullPointerException.class)
    public void testCompareWithSmallNull() {
        expect(bigNodeMock.numberValueOf(NODE_PROPERTY)).andReturn(BIG_VALUE);
        replay(bigNodeMock);

        nnc.compare(bigNodeMock, null);
    }
}
