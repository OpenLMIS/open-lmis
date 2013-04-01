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

    @Getter
    @Setter
    private String reportPath = "src/main/template";

    @Getter
    @Setter
    private String destinationPath = "src/main/resources/";

    public static void main(String[] args) {

        JasperReportCompiler jasperReportCompiler = new JasperReportCompiler();

        if(args != null && args.length > 0 ){
            jasperReportCompiler.setReportPath(args[0] == null || args[0].isEmpty()? jasperReportCompiler.getReportPath() : args[0] );
            jasperReportCompiler.setDestinationPath(args[1] == null || args[1].isEmpty()? jasperReportCompiler.getReportPath() : args[1] );
        }

        File dir = new File(jasperReportCompiler.getReportPath());

        if(dir == null) return;

        for(File f: dir.listFiles()){

            if ((f != null && f.isFile())){
                try {

                    String compiledJRName = f.getName();
                    if(compiledJRName.lastIndexOf(".") > 0 && compiledJRName.substring(compiledJRName.lastIndexOf(".")).equalsIgnoreCase(".jrxml")){

                        compiledJRName = compiledJRName.substring(0,compiledJRName.lastIndexOf("."));
                        File compiledJRFile = new File(jasperReportCompiler.getDestinationPath()+compiledJRName+".jasper");

                        JasperCompileManager.compileReportToFile(f.getAbsolutePath(), compiledJRFile.getAbsolutePath());
                    }
                } catch (JRException e) {
                    e.printStackTrace();
                }
            }
        }


    }

}
