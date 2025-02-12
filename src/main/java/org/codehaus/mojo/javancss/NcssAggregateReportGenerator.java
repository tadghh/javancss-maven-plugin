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

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.plugin.logging.Log;
import org.dom4j.Document;
import org.dom4j.Node;

/**
 * Generates the JavaNCSS aggregate report.
 *
 * @author <a href="mperham AT gmail.com">Mike Perham</a>
 * @version $Id$
 */
public class NcssAggregateReportGenerator extends AbstractNcssReportGenerator {

    /**
     * @param sink the sink that will be used for reporting.
     * @param bundle the correct RessourceBundle to be used for reporting.
     * @param log the logger to output log with.
     */
    public NcssAggregateReportGenerator(Sink sink, ResourceBundle bundle, Log log) {
        super(sink, bundle, log);
    }

    /**
     * Generates the JavaNCSS report.
     *
     * @param locale the Locale used for this report.
     * @param moduleReports the javancss raw reports to aggregate, List of ModuleReport.
     * @param lineThreshold the maximum number of lines to keep in major reports.
     */
    public void doReport(Locale locale, List<ModuleReport> moduleReports, int lineThreshold) {
        // HEADER
        getSink().head();
        getSink().title();
        getSink().text(getString("report.javancss.title"));
        getSink().title_();
        getSink().head_();

        // BODY
        getSink().body();
        doIntro(false);

        // packages
        startSection("report.javancss.module.link", "report.javancss.module.title");
        doModuleAnalysis(moduleReports);
        endSection();

        getSink().body_();
        getSink().close();
    }

    private void doModuleAnalysis(List<ModuleReport> reports) {
        startTable();

        // Header row
        String[] headers = {
            "report.javancss.header.module",
            "report.javancss.header.packages",
            "report.javancss.header.classetotal",
            "report.javancss.header.functiontotal",
            "report.javancss.header.ncsstotal",
            "report.javancss.header.javadoc",
            "report.javancss.header.javadoc_line",
            "report.javancss.header.single_comment",
            "report.javancss.header.multi_comment"
        };
        createTableHeader(headers);

        // Initialize counters
        int packages = 0, classes = 0, methods = 0, ncss = 0;
        int javadocs = 0, jdlines = 0, single = 0, multi = 0;

        // Data rows
        for (ModuleReport report : reports) {
            Document document = report.getJavancssDocument();
            getSink().tableRow();

            getLog().debug("Aggregating " + report.getModule().getArtifactId());
            tableCellHelper(report.getModule().getArtifactId());

            // Package count
            int packageSize =
                    document.selectNodes("//javancss/packages/package").size();
            packages += packageSize;
            tableCellHelper(String.valueOf(packageSize));

            // Get totals node
            Node node = document.selectSingleNode("//javancss/packages/total");

            // Process each metric
            String[] metrics = {
                "classes",
                "functions",
                "ncss",
                "javadocs",
                "javadoc_lines",
                "single_comment_lines",
                "multi_comment_lines"
            };
            int[] sums = {classes, methods, ncss, javadocs, jdlines, single, multi};

            for (int i = 0; i < metrics.length; i++) {
                String value = node.valueOf(metrics[i]);
                tableCellHelper(value);
                sums[i] += Integer.parseInt(value);
            }

            getSink().tableRow_();
        }

        // Totals row
        getSink().tableRow();
        tableCellHelper(getString("report.javancss.header.totals"));
        String[] totals = {
            String.valueOf(packages),
            String.valueOf(classes),
            String.valueOf(methods),
            String.valueOf(ncss),
            String.valueOf(javadocs),
            String.valueOf(jdlines),
            String.valueOf(single),
            String.valueOf(multi)
        };

        for (String total : totals) {
            tableCellHelper(total);
        }
        getSink().tableRow_();

        endTable();
    }
}
