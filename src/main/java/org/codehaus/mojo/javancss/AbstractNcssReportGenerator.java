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

import java.text.MessageFormat;
import java.util.ResourceBundle;

import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.plugin.logging.Log;

/**
 * Base abstract class for NCSSReport classes.<br>
 * It holds essentially helper methods on top of the Sink Doxia object.
 *
 * @author <a href="mailto:jeanlaurent@NOSPAMgmail.com">Jean-Laurent de Morlhon</a>
 * @version $Id$
 */
public abstract class AbstractNcssReportGenerator {
    private final ResourceBundle bundle;
    private final Sink sink;
    private final Log log;

    /**
     * Creates the report generator
     * @param sink sink
     * @param bundle bundle
     * @param log log
     */
    protected AbstractNcssReportGenerator(Sink sink, ResourceBundle bundle, Log log) {
        this.bundle = bundle;
        this.sink = sink;
        this.log = log;
    }

    /**
     * Gets the log
     * @return log
     */
    public Log getLog() {
        return this.log;
    }

    /**
     * Gets the sink
     * @return sink
     */
    public Sink getSink() {
        return this.sink;
    }

    /**
     * Gets the resource bundle
     * @return resource bundle
     */
    public ResourceBundle getResourceBundle() {
        return this.bundle;
    }

    /**
     * List helper
     * @param text text
     */
    protected void codeItemListHelper(String text) {
        sink.listItem();
        sink.monospaced();
        sink.text(text);
        sink.monospaced_();
        sink.listItem_();
    }

    /**
     * List helper
     * @param text text
     */
    protected void paragraphHelper(String text) {
        sink.paragraph();
        sink.text(text);
        sink.paragraph_();
    }

    /**
     * List helper
     * @param text text
     */
    protected void subtitleHelper(String text) {
        sink.paragraph();
        sink.bold();
        sink.text(text);
        sink.bold_();
        sink.paragraph_();
    }

    /**
     * Code cell helper
     * @param text text
     */
    protected void codeCellHelper(String text) {
        sink.tableCell();
        sink.monospaced();
        sink.text(text);
        sink.monospaced_();
        sink.tableCell_();
    }

    /**
     * Header cell helper
     * @param text text
     */
    protected void headerCellHelper(String text) {
        sink.tableHeaderCell();
        sink.text(text);
        sink.tableHeaderCell_();
    }

    /**
     * Table cell helper
     * @param text text
     */
    protected void tableCellHelper(String text) {
        sink.tableCell();
        sink.text(text);
        sink.tableCell_();
    }

    /**
     * Creates a new table with grid enabled.
     */
    protected void startTable() {
        sink.table();
        sink.tableRows(null, true);
    }

    /**
     * Ends the current table.
     */
    protected void endTable() {
        sink.tableRows_();
        sink.table_();
    }

    /**
     * Creates a header row with multiple columns.
     * @param headers Array of header text keys
     */
    protected void createTableHeader(String... headers) {
        sink.tableRow();
        for (String header : headers) {
            sink.tableHeaderCell();
            sink.text(getString(header));
            sink.tableHeaderCell_();
        }
        sink.tableRow_();
    }

    /**
     * Starts a section
     * @param link anchor link to section
     * @param title title
     */
    protected void startSection(String link, String title) {
        sink.section1();
        sink.sectionTitle1();
        sink.text(bundle.getString(title));
        sink.sectionTitle1_();

        sink.anchor(bundle.getString(link));
        sink.text(bundle.getString(title));
        sink.anchor_();
    }

    /**
     * ends a section
     */
    protected void endSection() {
        sink.section1_();
    }

    /**
     * Gets the resource for the key
     * @param key the key
     * @return the resource for the key
     */
    protected String getString(String key) {
        return bundle.getString(key);
    }

    /**
     * Intro
     * @param withNavigationBar bar
     */
    protected void doIntro(boolean withNavigationBar) {
        sink.section1();
        sink.sectionTitle1();
        sink.text(getString("report.javancss.main.title"));
        sink.sectionTitle1_();

        if (withNavigationBar) {
            navigationBar();
        }

        sink.paragraph();
        String version = NcssExecuter.getJavaNCSSVersion();
        sink.text(MessageFormat.format(getString("report.javancss.main.text"), version));
        sink.lineBreak();
        sink.link("http://javancss.codehaus.org/");
        sink.text("JavaNCSS web site.");
        sink.link_();
        sink.paragraph_();
        sink.section1_();
    }

    /**
     * Navigation bar
     */
    protected void navigationBar() {
        sink.paragraph();
        String[] sections = {
            "report.javancss.package.link",
            "report.javancss.object.link",
            "report.javancss.function.link",
            "report.javancss.explanation.link"
        };

        for (int i = 0; i < sections.length; i++) {
            if (i > 0) {
                sink.text(" ] [ ");
            } else {
                sink.text("[ ");
            }
            sink.link("#" + getString(sections[i]));
            sink.text(getString(sections[i]));
            sink.link_();
        }
        sink.text(" ]");
        sink.paragraph_();
    }
}
