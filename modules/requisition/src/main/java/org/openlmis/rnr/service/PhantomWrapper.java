package org.openlmis.rnr.service;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

@Component
public class PhantomWrapper {

    public void generatePDF(String url, String pdfPath, String sessionid, String programCode) throws IOException, URISyntaxException, InterruptedException {
        URL pdfScriptUrl = this.getClass().getClassLoader().getResource("pdf/" + programCode + "-pdf-renderer.js");
        String pdfScriptpath = Paths.get(pdfScriptUrl.toURI()).toFile().getAbsolutePath();
        ProcessBuilder phantomjs = new ProcessBuilder("phantomjs", "--ignore-ssl-errors=yes", pdfScriptpath, url, pdfPath, sessionid);
        phantomjs.start().waitFor();//if phantom takes too long, it'll timeout when tomcat says so
    }
}
