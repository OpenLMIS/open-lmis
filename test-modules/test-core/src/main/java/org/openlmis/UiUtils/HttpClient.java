package org.openlmis.UiUtils;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.*;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import static org.apache.http.client.protocol.ClientContext.AUTH_CACHE;
import static org.apache.http.protocol.HTTP.UTF_8;

public class HttpClient {

  public static final String GET = "GET";
  public static final String POST = "POST";
  public static final String PUT = "PUT";

  public static final String HOST = "localhost";
  public static final int PORT = 9091;
  public static final String PROTOCOL = "http";

  private DefaultHttpClient httpClient;
  private BasicHttpContext httpContext;

  public ResponseEntity SendJSON(String json, String url, String commMethod, String username, String password) {
//    if (StringUtils.isEmpty(username) && StringUtils.isEmpty(password)) return null;

    HttpHost targetHost = new HttpHost(HOST, PORT, PROTOCOL);
    AuthScope localhost = new AuthScope(HOST, PORT);

    UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password);
    httpClient.getCredentialsProvider().setCredentials(localhost, credentials);

    AuthCache authCache = new BasicAuthCache();
    authCache.put(targetHost, new BasicScheme());

    httpContext.setAttribute(AUTH_CACHE, authCache);

    try {
      return handleRequest(commMethod, json, url);
    } catch (ClientProtocolException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }


  private ResponseEntity handleRequest(String commMethod, String json, String url) throws IOException {
    HttpResponse response;
    HttpEntity entity;
    BufferedReader rd;
    String sCurrentLine;
    String sCompleteResponse="";

    HttpRequestBase httpRequest = getHttpRequest(commMethod, url);

    prepareRequestHeaderAndEntity(commMethod, json, httpRequest);

    response = httpClient.execute(httpRequest, httpContext);
    entity = response.getEntity();

    rd = new BufferedReader(new InputStreamReader(entity.getContent()));

    ResponseEntity responseEntity = new ResponseEntity();
    responseEntity.setStatus(response.getStatusLine().getStatusCode());

      while ((sCurrentLine = rd.readLine()) != null) {
          sCompleteResponse= sCompleteResponse + (sCurrentLine);
      }
    responseEntity.setResponse(sCompleteResponse);

    EntityUtils.consume(entity);

    return responseEntity;
  }

  private HttpRequestBase getHttpRequest(String commMethod, String url) {
    switch (commMethod) {
      case GET:
        return new HttpGet(url);
      case POST:
        return new HttpPost(url);
      case PUT:
        return new HttpPut(url);
    }
    return null;
  }

  private void prepareRequestHeaderAndEntity(String commMethod, String json, HttpRequestBase httpRequest)
    throws UnsupportedEncodingException {

    if (commMethod.equals(GET)) return;

    httpRequest.setHeader(new BasicHeader("Content-Type", "application/json;charset=UTF-8"));
    ((HttpEntityEnclosingRequestBase) httpRequest).setEntity(new StringEntity(json, UTF_8));
  }

  public void createContext() {
    this.httpClient = new DefaultHttpClient();
    CookieStore cookieStore = new BasicCookieStore();
    httpContext = new BasicHttpContext();
    httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
  }
}
