package org.openlmis.rnr.service;

import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

@Component
public class PhantomWrapper {
    public void generatePDF(String url, String pdfPath, String sessionid) throws IOException, URISyntaxException, InterruptedException {
        URL pdfScriptUrl = this.getClass().getClassLoader().getResource("pdf/rasterize.js");
        String pdfScriptpath = Paths.get(pdfScriptUrl.toURI()).toFile().getAbsolutePath();
        ProcessBuilder phantomjs = new ProcessBuilder("phantomjs", pdfScriptpath, url, pdfPath, sessionid);
        phantomjs.redirectOutput(new File("/app/tomcat/open-lmis/logs/pdf.log"));
        phantomjs.start().waitFor();
    }
}
