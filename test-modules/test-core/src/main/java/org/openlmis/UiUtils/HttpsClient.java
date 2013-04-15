package org.openlmis.UiUtils;

//java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;

import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;

import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SchemeSocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.ClientParamsStack;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.DefaultedHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.entity.StringEntity;

public class HttpsClient {
    public HttpClient setupHttpClient(){
        try {

            HttpClient httpclient = new DefaultHttpClient();
            //Secure Protocol implementation.
            SSLContext ctx = SSLContext.getInstance("SSL");
            //Implementation of a trust manager for X509 certificates
            X509TrustManager tm = new X509TrustManager() {

                public void checkClientTrusted(X509Certificate[] xcs,
                                               String string) throws CertificateException {

                }

                public void checkServerTrusted(X509Certificate[] xcs,
                                               String string) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };
            ctx.init(null, new TrustManager[] { tm }, null);
            SSLSocketFactory ssf = new SSLSocketFactory(ctx);
            ClientConnectionManager ccm = httpclient.getConnectionManager();
            //register https protocol in httpclient's scheme registry
            SchemeRegistry sr = ccm.getSchemeRegistry();
            sr.register(new Scheme("https", 443, ssf));

            return httpclient;
        }
            catch (NoSuchAlgorithmException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        catch (Exception ex) {
            ex.printStackTrace();

        }
        return new DefaultHttpClient();
    }

    public String SendJSON(String fileName, String url, String commMethod) {
        HttpClient httpclient = new DefaultHttpClient();
        httpclient=setupHttpClient();
        String responseBody="";

        ResponseHandler responseHandler = new BasicResponseHandler();
        HttpPost httppost = new HttpPost(url);
        try {


            switch(commMethod)
            {
              case "GET":
                  HttpGet httpget = new HttpGet(url);
                  //HttpParams params = httpclient.getParams();
                  //params.setParameter("param1", "paramValue1");
                  //httpget.setParams(params);
                  responseBody = (String) httpclient.execute(httpget, responseHandler);
                  break;
                case "POST":
                    String json="";
                    File file = new File(fileName);
                    Scanner scanner = new Scanner(file);
                    while (scanner.hasNextLine()) {
                        json=json+scanner.nextLine();
                    }
                    scanner.close();
                    StringEntity input = new StringEntity(json);
                    input.setContentType("application/json");
                    httppost.setEntity(input);
                    responseBody = (String) httpclient.execute(httppost, responseHandler);
                    break;
                case "Non-JSON":
                    List nameValuePairs = new ArrayList(2);
                    nameValuePairs.add(new BasicNameValuePair("j_username","Admin123"));
                    nameValuePairs.add(new BasicNameValuePair("j_password", "Admin123"));
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    httpclient.execute(httppost, responseHandler);
                    break;

            }
            return responseBody;

            // Create a response handler

        }  catch (ClientProtocolException e) {
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

    public static void main(String args[]) {
        HttpsClient client = new HttpsClient();
        //client.SendJSON("/Users/Raman/open-lmis/test-modules/test-core/src/main/java/org/openlmis/UiUtils/DummyJSON.txt", "https://localhost:9091/facilities.json", "POST");
        //System.out.println(client.SendJSON("","https://openlmis:9091/","GET")) ;
        System.out.println(client.SendJSON("", "https://openlmis:9091/j_spring_security_check", "Non-JSON"));

        System.out.println(client.SendJSON("/Users/Raman/open-lmis/test-modules/test-core/src/main/java/org/openlmis/UiUtils/DummyJSON.txt", "https://openlmis:9091/facilities.json", "POST"));

        //       new ServiceUtils().createJson();


    }
}
