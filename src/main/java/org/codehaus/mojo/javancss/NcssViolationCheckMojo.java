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

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

/**
 * Check the build if for any Method with a ccn greater than a limit in the source code. Fails the build if told so.
 *
 * @author <a href="jeanlaurentATgmail.com">Jean-Laurent de Morlhon</a>
 * @version $Id$
 */
@Mojo(name = "check", defaultPhase = LifecyclePhase.VERIFY)
@Execute(goal = "report")
public class NcssViolationCheckMojo extends AbstractMojo {
    /**
     * Specifies the location of the source files to be used.
     */
    @Parameter(defaultValue = "${project.build.sourceDirectory}", readonly = true, required = true)
    private File sourceDirectory;

    /**
     * Specifies the directory where the XML report will be generated.
     */
    // FIXME : same variable, same value in NCSSReportMojo...
    @Parameter(defaultValue = "${project.build.directory}", readonly = true, required = true)
    private File xmlOutputDirectory;

    /**
     * Whether to fail the build if the validation check fails.
     */
    @Parameter(defaultValue = "true")
    private boolean failOnViolation;

    /**
     * Name of the file holding the xml file generated by JavaNCSS
     */
    // FIXME : same variable, same value in NCSSReportMojo...
    @Parameter(defaultValue = "javancss-raw-report.xml")
    private String tempFileName;

    /**
     * CCN Limit, any code with a ccn greater than this number will generate a violation
     */
    @Parameter(defaultValue = "10")
    private int ccnLimit;

    /**
     * ncss Limit, any code with a ncss greater than this number will generate a violation
     */
    @Parameter(defaultValue = "100")
    private int ncssLimit;

    /**
     * Skip entire check.
     *
     * @since 2.1
     */
    // FIXME : same variable, same value in NCSSReportMojo...
    @Parameter(property = "ncss.skip", defaultValue = "false")
    private boolean skip;

    /**
     * Executes the report
     * @throws MojoExecutionException bad
     * @throws MojoFailureException bad
     */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (skip || (sourceDirectory == null) || !sourceDirectory.exists()) {
            return;
        }
        Set<String> ccnViolation = new HashSet<>();
        Set<String> ncssViolation = new HashSet<>();
        List<Node> methodList = loadDocument().selectNodes("//javancss/functions/function");
        // Count ccn & ncss violations
        for (Node node : methodList) {
            // count ccn violation
            int ccn = Integer.parseInt(node.valueOf("ccn"));
            if (ccn > ccnLimit) {
                ccnViolation.add(node.valueOf("name"));
            }
            // count ncss violation
            int ncss = Integer.parseInt(node.valueOf("ncss"));
            if (ncss > ncssLimit) {
                ncssViolation.add(node.valueOf("name"));
            }
        }
        // crappy....
        reportViolation("ccn", ccnViolation, ccnLimit);
        reportViolation("ncss", ncssViolation, ncssLimit);
    }

    private Document loadDocument() throws MojoFailureException {
        // FIXME: Building of File is strangely equivalent to method buildOutputFileName of NcssReportGenerator class...
        File ncssXmlFile = new File(xmlOutputDirectory, tempFileName);
        try {
            return new SAXReader().read(ncssXmlFile);
        } catch (DocumentException de) {
            throw new MojoFailureException("Can't read javancss xml output file : " + ncssXmlFile);
        }
    }

    private void reportViolation(String statName, Set<String> violationSet, int limit) throws MojoFailureException {
        getLog().debug(statName + " Violation = " + violationSet.size());
        if (!violationSet.isEmpty()) {
            String violationString =
                    "Your code has " + violationSet.size() + " method(s) with a " + statName + " greater than " + limit;
            getLog().warn(violationString);
            for (String violation : violationSet) {
                getLog().warn("    " + violation);
            }
            if (failOnViolation) {
                throw new MojoFailureException(violationString);
            }
        }
    }
}
