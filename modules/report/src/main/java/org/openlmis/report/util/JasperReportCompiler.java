/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
