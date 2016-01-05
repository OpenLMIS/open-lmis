package org.openlmis.rnr.service;

import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

@Component
public class PhantomWrapper {
    public void generatePDF(String url, String sessionid, String pdfPath) throws IOException, URISyntaxException, InterruptedException {
        URL resource = this.getClass().getClassLoader().getResource("rasterize.js");
        File file = Paths.get(resource.toURI()).toFile();

        ProcessBuilder pb = new ProcessBuilder("phantomjs", file.getAbsolutePath(), url, pdfPath, sessionid);
        pb.start().waitFor();
    }
}
