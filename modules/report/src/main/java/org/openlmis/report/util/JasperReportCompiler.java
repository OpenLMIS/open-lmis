/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.report.util;

import lombok.Getter;
import lombok.Setter;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;

import java.io.File;

/**
 * Utility class used to compile Jasper Design file(.jrxml) to Jasper Report (.jasper)
 * The jasper file will be included to war file for later processing. This enable the app to process the compiled file
 * instead of compiling the jasper design while processing the report and hence improves app performance.
 */
public class JasperReportCompiler {

    private String reportPath = "src/main/template";

    private String destinationPath = "src/main/resources/";

    public String getReportPath() {
        return reportPath;
    }

    public void setReportPath(String reportPath) {
        this.reportPath = reportPath;
    }

    public String getDestinationPath() {
        return destinationPath;
    }

    public void setDestinationPath(String destinationPath) {
        this.destinationPath = destinationPath;
    }

    public static void main(String[] args) {
        JasperReportCompiler jasperReportCompiler = new JasperReportCompiler();

        if(args != null && args.length > 0 ){
            jasperReportCompiler.setReportPath(args[0] == null || args[0].isEmpty()? jasperReportCompiler.getReportPath() : args[0] );
            jasperReportCompiler.setDestinationPath(args[1] == null || args[1].isEmpty()? jasperReportCompiler.getReportPath() : args[1] );
        }

        File dir = new File(jasperReportCompiler.getReportPath());

        if(dir == null) return;

        for(File file : dir.listFiles()){

            if ((file != null && file.isFile())){
             try {

                    String compiledJRName = file.getName();
                    if(compiledJRName.lastIndexOf(".") > 0 && compiledJRName.substring(compiledJRName.lastIndexOf(".")).equalsIgnoreCase(".jrxml")){

                        compiledJRName = compiledJRName.substring(0,compiledJRName.lastIndexOf("."));
                        File compiledJRFile = new File(jasperReportCompiler.getDestinationPath()+compiledJRName+".jasper");
                        compiledJRFile.delete();
                        JasperCompileManager.compileReportToFile(file.getAbsolutePath(), compiledJRFile.getAbsolutePath());
                    }
                } catch (JRException e) {
                    e.printStackTrace();
                }
            }
        }


    }

}
