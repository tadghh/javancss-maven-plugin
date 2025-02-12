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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.plugin.logging.Log;
import org.dom4j.Document;
import org.dom4j.Node;

/**
 * Generates the javaNcss maven report.
 *
 * @author <a href="jeanlaurentATgmail.com">Jean-Laurent de Morlhon</a>
 * @version $Id$
 */
public class NcssReportGenerator extends AbstractNcssReportGenerator {

    private String xrefLocation;

    private int lineThreshold;

    /**
     * build a new NcssReportGenerator.
     *
     * @param sink the sink which will be used for reporting.
     * @param bundle the correct RessourceBundle to be used for reporting.
     * @param log the log to output log with.
     * @param xrefLocation the location of the xref file.
     */
    public NcssReportGenerator(Sink sink, ResourceBundle bundle, Log log, String xrefLocation) {
        super(sink, bundle, log);
        this.xrefLocation = xrefLocation;
    }

    /**
     * Generates the JavaNcss reports.
     *
     * @param document the javaNcss raw report as an XML document.
     * @param lineThreshold the maximum number of lines to keep in major reports.
     */
    public void doReport(Document document, int lineThreshold) {
        this.lineThreshold = lineThreshold;
        // HEADER
        getSink().head();
        getSink().title();
        getSink().text(getString("report.javancss.title"));
        getSink().title_();
        getSink().head_();
        // BODY
        getSink().body();
        doIntro(true);
        // packages
        startSection("report.javancss.package.link", "report.javancss.package.title");
        doMainPackageAnalysis(document);
        doTotalPackageAnalysis(document);
        endSection();
        // Objects
        startSection("report.javancss.object.link", "report.javancss.object.title");
        doTopObjectNcss(document);
        doTopObjectFunctions(document);
        doObjectAverage(document);
        endSection();
        // Functions
        startSection("report.javancss.function.link", "report.javancss.function.title");
        doFunctionAnalysis(document);
        doFunctionAverage(document);
        endSection();
        // Explanation
        startSection("report.javancss.explanation.link", "report.javancss.explanation.title");
        doExplanation();
        endSection();
        getSink().body_();
        getSink().close();
    }

    private void doMainPackageAnalysis(Document document) {
        subtitleHelper(getString("report.javancss.package.text"));

        // Start table with proper grid
        getSink().table();
        getSink().tableRows(null, true); // Enable table grid

        // Header row
        getSink().tableRow();
        String[] headers = {
            "report.javancss.header.package",
            "report.javancss.header.classe",
            "report.javancss.header.function",
            "report.javancss.header.ncss",
            "report.javancss.header.javadoc",
            "report.javancss.header.javadoc_line",
            "report.javancss.header.single_comment",
            "report.javancss.header.multi_comment"
        };

        for (String header : headers) {
            getSink().tableHeaderCell();
            getSink().text(getString(header));
            getSink().tableHeaderCell_();
        }
        getSink().tableRow_();

        // Data rows
        List<Node> list = document.selectNodes("//javancss/packages/package");
        Collections.<Node>sort(list, new NumericNodeComparator("ncss"));
        for (Node node : list) {
            getSink().tableRow();

            // Each cell in proper cell tags
            getSink().tableCell();
            getSink().text(node.valueOf("name"));
            getSink().tableCell_();

            String[] fields = {
                "classes",
                "functions",
                "ncss",
                "javadocs",
                "javadoc_lines",
                "single_comment_lines",
                "multi_comment_lines"
            };

            for (String field : fields) {
                getSink().tableCell();
                getSink().text(node.valueOf(field));
                getSink().tableCell_();
            }

            getSink().tableRow_();
        }

        // End table
        getSink().tableRows_();
        getSink().table_();
    }

    private void doTotalPackageAnalysis(Document document) {
        getSink().table();
        getSink().tableRows(null, true);

        // Header row
        getSink().tableRow();
        String[] headers = {
            "report.javancss.header.classetotal",
            "report.javancss.header.functiontotal",
            "report.javancss.header.ncsstotal",
            "report.javancss.header.javadoc",
            "report.javancss.header.javadoc_line",
            "report.javancss.header.single_comment",
            "report.javancss.header.multi_comment"
        };

        for (String header : headers) {
            getSink().tableHeaderCell();
            getSink().text(getString(header));
            getSink().tableHeaderCell_();
        }
        getSink().tableRow_();

        // Data row
        Node node = document.selectSingleNode("//javancss/packages/total");
        getSink().tableRow();

        String[] fields = {
            "classes", "functions", "ncss", "javadocs", "javadoc_lines", "single_comment_lines", "multi_comment_lines"
        };

        for (String field : fields) {
            getSink().tableCell();
            getSink().text(node.valueOf(field));
            getSink().tableCell_();
        }

        getSink().tableRow_();
        getSink().tableRows_();
        getSink().table_();
    }

    private void doTopObjectNcss(Document document) {
        subtitleHelper(getString("report.javancss.top") + " " + lineThreshold + " "
                + getString("report.javancss.object.byncss"));
        List<Node> nodeList = document.selectNodes("//javancss/objects/object");
        Collections.<Node>sort(nodeList, new NumericNodeComparator("ncss"));
        doTopObjectGeneric(nodeList);
    }

    private void doTopObjectFunctions(Document document) {
        subtitleHelper(getString("report.javancss.top") + " " + lineThreshold + " "
                + getString("report.javancss.object.byfunction"));
        List<Node> nodeList = document.selectNodes("//javancss/objects/object");
        Collections.<Node>sort(nodeList, new NumericNodeComparator("functions"));
        doTopObjectGeneric(nodeList);
    }

    // generic method called by doTopObjectFunctions & doTopObjectNCss
    private void doTopObjectGeneric(List<Node> nodeList) {
        getSink().table();
        getSink().tableRows(null, true);

        // Header row
        getSink().tableRow();
        String[] headers = {
            "report.javancss.header.object",
            "report.javancss.header.ncss",
            "report.javancss.header.function",
            "report.javancss.header.classe",
            "report.javancss.header.javadoc"
        };

        for (String header : headers) {
            getSink().tableHeaderCell();
            getSink().text(getString(header));
            getSink().tableHeaderCell_();
        }
        getSink().tableRow_();

        // Data rows
        Iterator<Node> nodeIterator = nodeList.iterator();
        int i = 0;
        while (nodeIterator.hasNext() && (i++ < lineThreshold)) {
            Node node = nodeIterator.next();
            getSink().tableRow();

            // Object name cell with link
            getSink().tableCell();
            jxrLink(node.valueOf("name"));
            getSink().tableCell_();

            // Other cells
            String[] fields = {"ncss", "functions", "classes", "javadocs"};
            for (String field : fields) {
                getSink().tableCell();
                getSink().text(node.valueOf(field));
                getSink().tableCell_();
            }

            getSink().tableRow_();
        }

        getSink().tableRows_();
        getSink().table_();
    }

    private void doObjectAverage(Document document) {
        subtitleHelper(getString("report.javancss.averages"));
        getSink().table();
        getSink().tableRows(null, true);

        // Header row
        getSink().tableRow();
        String[] headers = {
            "report.javancss.header.ncssaverage",
            "report.javancss.header.programncss",
            "report.javancss.header.classeaverage",
            "report.javancss.header.functionaverage",
            "report.javancss.header.javadocaverage"
        };

        for (String header : headers) {
            getSink().tableHeaderCell();
            getSink().text(getString(header));
            getSink().tableHeaderCell_();
        }
        getSink().tableRow_();

        // Data row
        Node node = document.selectSingleNode("//javancss/objects/averages");
        String totalNcss = document.selectSingleNode("//javancss/objects/ncss").getText();
        getSink().tableRow();

        // Data cells
        getSink().tableCell();
        getSink().text(node.valueOf("ncss"));
        getSink().tableCell_();

        getSink().tableCell();
        getSink().text(totalNcss);
        getSink().tableCell_();

        getSink().tableCell();
        getSink().text(node.valueOf("classes"));
        getSink().tableCell_();

        getSink().tableCell();
        getSink().text(node.valueOf("functions"));
        getSink().tableCell_();

        getSink().tableCell();
        getSink().text(node.valueOf("javadocs"));
        getSink().tableCell_();

        getSink().tableRow_();
        getSink().tableRows_();
        getSink().table_();
    }

    private void doFunctionAnalysis(Document document) {
        subtitleHelper(getString("report.javancss.top") + " " + lineThreshold + " "
                + getString("report.javancss.function.byncss"));

        getSink().table();
        getSink().tableRows(null, true);

        // Header row
        getSink().tableRow();
        String[] headers = {
            "report.javancss.header.function",
            "report.javancss.header.ncss",
            "report.javancss.header.ccn",
            "report.javancss.header.javadoc"
        };

        for (String header : headers) {
            getSink().tableHeaderCell();
            getSink().text(getString(header));
            getSink().tableHeaderCell_();
        }
        getSink().tableRow_();

        // Data rows
        List<Node> list = document.selectNodes("//javancss/functions/function");
        Collections.<Node>sort(list, new NumericNodeComparator("ncss"));
        Iterator<Node> nodeIterator = list.iterator();
        int i = 0;
        while (nodeIterator.hasNext() && (i++ < lineThreshold)) {
            Node node = nodeIterator.next();
            getSink().tableRow();

            // Function name cell with link
            getSink().tableCell();
            jxrFunctionLink(node.valueOf("name"));
            getSink().tableCell_();

            // Other cells
            String[] fields = {"ncss", "ccn", "javadocs"};
            for (String field : fields) {
                getSink().tableCell();
                getSink().text(node.valueOf(field));
                getSink().tableCell_();
            }

            getSink().tableRow_();
        }

        getSink().tableRows_();
        getSink().table_();
    }

    private void doFunctionAverage(Document document) {
        subtitleHelper(getString("report.javancss.averages"));
        getSink().table();
        getSink().tableRows(null, true);

        // Header row
        getSink().tableRow();
        String[] headers = {
            "report.javancss.header.programncss",
            "report.javancss.header.ncssaverage",
            "report.javancss.header.ccnaverage",
            "report.javancss.header.javadocaverage"
        };

        for (String header : headers) {
            getSink().tableHeaderCell();
            getSink().text(getString(header));
            getSink().tableHeaderCell_();
        }
        getSink().tableRow_();

        // Data row
        Node node = document.selectSingleNode("//javancss/functions/function_averages");
        String totalNcss =
                document.selectSingleNode("//javancss/functions/ncss").getText();
        getSink().tableRow();

        getSink().tableCell();
        getSink().text(totalNcss);
        getSink().tableCell_();

        getSink().tableCell();
        getSink().text(node.valueOf("ncss"));
        getSink().tableCell_();

        getSink().tableCell();
        getSink().text(node.valueOf("ccn"));
        getSink().tableCell_();

        getSink().tableCell();
        getSink().text(node.valueOf("javadocs"));
        getSink().tableCell_();

        getSink().tableRow_();
        getSink().tableRows_();
        getSink().table_();
    }

    private void doExplanation() {
        subtitleHelper(getString("report.javancss.explanation.ncss.title"));
        paragraphHelper(getString("report.javancss.explanation.ncss.paragraph1"));
        paragraphHelper(getString("report.javancss.explanation.ncss.paragraph2"));

        // Main explanation table
        getSink().table();
        getSink().tableRows(null, true);

        // Header row
        getSink().tableRow();
        getSink().tableHeaderCell();
        getSink().text("");
        getSink().tableHeaderCell_();
        getSink().tableHeaderCell();
        getSink().text(getString("report.javancss.explanation.ncss.table.examples"));
        getSink().tableHeaderCell_();
        getSink().tableRow_();

        // Package row
        getSink().tableRow();
        getSink().tableCell();
        getSink().text(getString("report.javancss.explanation.ncss.table.package"));
        getSink().tableCell_();

        getSink().tableCell();
        getSink().monospaced();
        getSink().text("package java.lang;");
        getSink().monospaced_();
        getSink().tableCell_();
        getSink().tableRow_();

        // Import row
        getSink().tableRow();
        getSink().tableCell();
        getSink().text(getString("report.javancss.explanation.ncss.table.import"));
        getSink().tableCell_();

        getSink().tableCell();
        getSink().monospaced();
        getSink().text("import java.awt.*;");
        getSink().monospaced_();
        getSink().tableCell_();
        getSink().tableRow_();

        // Class row
        getSink().tableRow();
        getSink().tableCell();
        getSink().text(getString("report.javancss.explanation.ncss.table.class"));
        getSink().tableCell_();

        getSink().tableCell();
        getSink().list();
        codeItemListHelper("public class Foo {");
        codeItemListHelper("public class Foo extends Bla {");
        getSink().list_();
        getSink().tableCell_();
        getSink().tableRow_();

        // Interface row
        getSink().tableRow();
        getSink().tableCell();
        getSink().text(getString("report.javancss.explanation.ncss.table.interface"));
        getSink().tableCell_();

        getSink().tableCell();
        getSink().monospaced();
        getSink().text("public interface Able ; {");
        getSink().monospaced_();
        getSink().tableCell_();
        getSink().tableRow_();

        // Field row
        getSink().tableRow();
        getSink().tableCell();
        getSink().text(getString("report.javancss.explanation.ncss.table.field"));
        getSink().tableCell_();

        getSink().tableCell();
        getSink().list();
        codeItemListHelper("int a;");
        codeItemListHelper("int a, b, c = 5, d = 6;");
        getSink().list_();
        getSink().tableCell_();
        getSink().tableRow_();

        // Method row
        getSink().tableRow();
        getSink().tableCell();
        getSink().text(getString("report.javancss.explanation.ncss.table.method"));
        getSink().tableCell_();

        getSink().tableCell();
        getSink().list();
        codeItemListHelper("public void cry();");
        codeItemListHelper("public void gib() throws DeadException {");
        getSink().list_();
        getSink().tableCell_();
        getSink().tableRow_();

        // Constructor declaration row
        getSink().tableRow();
        getSink().tableCell();
        getSink().text(getString("report.javancss.explanation.ncss.table.constructorD"));
        getSink().tableCell_();

        getSink().tableCell();
        getSink().monospaced();
        getSink().text("public Foo() {");
        getSink().monospaced_();
        getSink().tableCell_();
        getSink().tableRow_();

        // Constructor invocation row
        getSink().tableRow();
        getSink().tableCell();
        getSink().text(getString("report.javancss.explanation.ncss.table.constructorI"));
        getSink().tableCell_();

        getSink().tableCell();
        getSink().list();
        codeItemListHelper("this();");
        codeItemListHelper("super();");
        getSink().list_();
        getSink().tableCell_();
        getSink().tableRow_();

        // Statement row
        getSink().tableRow();
        getSink().tableCell();
        getSink().text(getString("report.javancss.explanation.ncss.table.statement"));
        getSink().tableCell_();

        getSink().tableCell();
        getSink().list();
        codeItemListHelper("i = 0;");
        codeItemListHelper("if (ok)");
        codeItemListHelper("if (exit) {");
        codeItemListHelper("if (3 == 4);");
        codeItemListHelper("if (4 == 4) { ;");
        codeItemListHelper("} else {");
        getSink().list_();
        getSink().tableCell_();
        getSink().tableRow_();

        // Label row
        getSink().tableRow();
        getSink().tableCell();
        getSink().text(getString("report.javancss.explanation.ncss.table.label"));
        getSink().tableCell_();

        getSink().tableCell();
        getSink().monospaced();
        getSink().text("fine :");
        getSink().monospaced_();
        getSink().tableCell_();
        getSink().tableRow_();

        getSink().tableRows_();
        getSink().table_();

        paragraphHelper(getString("report.javancss.explanation.ncss.paragraph3"));

        // CCN Explanation
        subtitleHelper(getString("report.javancss.explanation.ccn.title"));
        paragraphHelper(getString("report.javancss.explanation.ccn.paragraph1"));
        paragraphHelper(getString("report.javancss.explanation.ccn.paragraph2"));
        getSink().list();
        codeItemListHelper("if");
        codeItemListHelper("for");
        codeItemListHelper("while");
        codeItemListHelper("case");
        codeItemListHelper("catch");
        getSink().list_();
        paragraphHelper(getString("report.javancss.explanation.ccn.paragraph3"));
        getSink().list();
        codeItemListHelper("if");
        codeItemListHelper("for");
        getSink().list_();
        paragraphHelper(getString("report.javancss.explanation.ccn.paragraph4"));
        paragraphHelper(getString("report.javancss.explanation.ccn.paragraph5"));
    }

    // sink helper to start a section
    protected void startSection(String link, String title) {
        super.startSection(link, title);
        navigationBar();
    }

    protected void jxrLink(String clazz) {
        if (xrefLocation != null) {
            getSink().link(xrefLocation + "/" + clazz.replace('.', '/') + ".html");
        }
        getSink().text(clazz);
        if (xrefLocation != null) {
            getSink().link_();
        }
    }

    protected void jxrFunctionLink(String clazz) {
        int indexDot = -1;
        if (xrefLocation != null) {
            indexDot = clazz.lastIndexOf('.');
            if (indexDot != -1) {
                getSink().link(xrefLocation + "/" + clazz.substring(0, indexDot).replace('.', '/') + ".html");
            }
        }
        getSink().text(clazz);
        if (xrefLocation != null && indexDot != -1) {
            getSink().link_();
        }
    }
}
