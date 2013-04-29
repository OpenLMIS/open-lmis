
package org.openlmis.UiUtils;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

public class SeleniumFileDownloadUtil {

    private WebDriver driver;
    private URI fileURI;
    private BasicCookieStore currentCookieStoreState;

    public SeleniumFileDownloadUtil(WebDriver driver) {
        this.driver = driver;
    }

    private BasicCookieStore getCurrentCookieStoreState(){
        Set<Cookie> currentDriverCookieSet = null;
        if(driver != null){
            currentDriverCookieSet = driver.manage().getCookies();
            currentCookieStoreState = new BasicCookieStore();
            for (Cookie seleniumCookie : currentDriverCookieSet) {
                BasicClientCookie cookieConfig = new BasicClientCookie(seleniumCookie.getName(), seleniumCookie.getValue());
                cookieConfig.setDomain(seleniumCookie.getDomain());
                cookieConfig.setSecure(seleniumCookie.isSecure());
                cookieConfig.setExpiryDate(seleniumCookie.getExpiry());
                cookieConfig.setPath(seleniumCookie.getPath());
                currentCookieStoreState.addCookie(cookieConfig);
            }

        }
        return  currentCookieStoreState;
    }
    public void setURI(String linkToFile) throws MalformedURLException, URISyntaxException {
        fileURI = new URI(linkToFile);
    }


    private HttpResponse getHTTPResponse() throws IOException, NullPointerException{

        if (fileURI == null) throw new NullPointerException("No file URI specified");

        HttpClient client = new DefaultHttpClient();
        BasicHttpContext localContext = new BasicHttpContext();

        //Clear down the local cookie store every time to make sure we don't have any left over cookies influencing the test
        localContext.setAttribute(ClientContext.COOKIE_STORE, null);

        //Mimic WebDriver cookie state
         localContext.setAttribute(ClientContext.COOKIE_STORE, getCurrentCookieStoreState());

        HttpRequestBase requestMethod = new HttpGet();
        requestMethod.setURI(fileURI);
        HttpParams httpRequestParameters = requestMethod.getParams();
        httpRequestParameters.setParameter(ClientPNames.HANDLE_REDIRECTS, Boolean.TRUE);
        requestMethod.setParams(httpRequestParameters);
        return client.execute(requestMethod, localContext);
    }

    public int getLinkHTTPStatus() throws Exception {
        HttpResponse fileToDownload = getHTTPResponse();
        int httpStatusCode;
        try {
            httpStatusCode = fileToDownload.getStatusLine().getStatusCode();
        } finally {
            fileToDownload.getEntity().getContent().close();
        }

        return httpStatusCode;
    }

    public File downloadFile(String filePrefix, String fileSuffix) throws Exception {
        if(filePrefix == null || filePrefix.isEmpty())
            filePrefix = "OpenLMIS_report_";

        File downloadedFile = File.createTempFile(filePrefix, fileSuffix);

        HttpResponse fileToDownload = getHTTPResponse();
        try {
            FileUtils.copyInputStreamToFile(fileToDownload.getEntity().getContent(), downloadedFile);
        } finally {
            fileToDownload.getEntity().getContent().close();
        }

        return downloadedFile;
    }
}
