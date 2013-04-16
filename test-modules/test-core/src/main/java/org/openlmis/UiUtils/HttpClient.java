package org.openlmis.UiUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;

import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.BasicHttpContext;

import static org.apache.http.protocol.HTTP.UTF_8;

public class HttpClient {
    private DefaultHttpClient httpclient;
    private BasicHttpContext httpContext;


    public String SendJSON(String fileName, String url, String commMethod) {

        HttpPost httppost = new HttpPost(url);
        httppost.setHeader(new BasicHeader("X-Requested-With", "XMLHttpRequest"));
        httppost.setHeader(new BasicHeader("Content-Type","application/json;charset=UTF-8"));
        HttpResponse response;
        HttpEntity entity;
        String responseBody = null;
        try {


            switch (commMethod) {
                case "GET":
                    HttpGet httpget = new HttpGet(url);
                    response = httpclient.execute(httpget, httpContext);
                    int statusCode = response.getStatusLine().getStatusCode();
                    entity = response.getEntity();
                    BufferedReader rd1 = new BufferedReader(new InputStreamReader(entity.getContent()));
                    responseBody = rd1.readLine();
                    entity.getContent().close();
                    break;
                case "POST":
                    String json = "";
                    File file = new File(fileName);
                    Scanner scanner = new Scanner(file);
                    while (scanner.hasNextLine()) {
                        json = json + scanner.nextLine();
                    }
                    scanner.close();
                    httppost.setEntity(new StringEntity(json, UTF_8));

                    response = httpclient.execute(httppost, httpContext);
                    entity = response.getEntity();
                    BufferedReader rd = new BufferedReader(new InputStreamReader(entity.getContent()));
                    responseBody = rd.readLine();
                    break;

            }
            return responseBody;

            // Create a response handler

        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();

        }
        return "";
    }

    private void createContext() {
        this.httpclient = new DefaultHttpClient();
        String responseBody = "";
        final HttpParams params = new BasicHttpParams();
        CookieStore cookieStore = new BasicCookieStore();
        httpContext = new BasicHttpContext();
        httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
    }

    private void login(String loginUrl, String userName, String password) throws IOException {
        HttpPost httppost = new HttpPost(loginUrl);
        httppost.setHeader(new BasicHeader("X-Requested-With", "XMLHttpRequest"));

        List nameValuePairs = new ArrayList(2);
        nameValuePairs.add(new BasicNameValuePair("j_username", userName));
        nameValuePairs.add(new BasicNameValuePair("j_password", password));
        httppost.setHeader(new BasicHeader("Content-Type", "application/x-www-form-urlencoded"));
        httppost.setHeader(new BasicHeader("Connection", "keep-alive"));
        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, UTF_8));
        HttpResponse response = httpclient.execute(httppost, httpContext);

        System.out.println(response.getStatusLine().toString());
        response.getEntity().getContent().close();


    }
/*
    public static void main(String args[]) throws IOException {
        HttpClient client = new HttpClient();
        client.createContext();
        System.out.println(client.SendJSON("", "http://localhost:9091/", "GET"));
        client.login("http://localhost:9091/j_spring_security_check","Admin123","Admin123");
        System.out.println(client.SendJSON("/Users/Raman/open-lmis/test-modules/test-core/src/main/java/org/openlmis/UiUtils/DummyJSON.txt", "http://localhost:9091/facilities.json", "POST"));

        //       new ServiceUtils().createJson();


    }*/
}
