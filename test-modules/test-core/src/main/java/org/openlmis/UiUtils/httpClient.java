package org.openlmis.UiUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.apache.commons.io.IOUtils;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Scanner;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;

public class httpClient {
    HttpURLConnection conn ;

    @SuppressWarnings("finally")
    public String SendJSON(String fileName, String destinationUrl, String commMethod) {
        String output, outputFinal = "";

        try {

            // configure the SSLContext with a TrustManager
            SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(new KeyManager[0], new TrustManager[] {new DefaultTrustManager()}, new SecureRandom());
            SSLContext.setDefault(ctx);



            String json = "";
            URL url = new URL(destinationUrl);
            conn = (HttpURLConnection) url.openConnection();

            conn.setDoOutput(true);
            conn.setRequestMethod(commMethod);
            conn.setRequestProperty("Accept", "application/json, text/plain, */*");
            conn.setRequestProperty("Connection", "keep-alive");
            conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");


            File file = new File(fileName);
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                json=json+scanner.nextLine();
            }
            scanner.close();


            OutputStream os = conn.getOutputStream();
            os.write(json.getBytes());
            os.flush();

            if (conn.getResponseCode() != 200 && conn.getResponseCode() != 401) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }


            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            while ((output = br.readLine()) != null) {
                outputFinal = outputFinal + output;
            }

            br.close();
            conn.disconnect();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
             e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {
            return outputFinal;


        }
    }



    @SuppressWarnings("finally")
    public String getJSON(String endPoint) {
        String output, outputFinal = "";
        try {

            URL url = new URL(endPoint);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));


            while ((output = br.readLine()) != null) {
                outputFinal = outputFinal + output;
            }

            br.close();
            conn.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {
            return outputFinal;
        }
    }

    public void createJson() {
        JSONObject obj = new JSONObject();
        LinkedHashMap lhmObj = new LinkedHashMap();
        LinkedList llObj = new LinkedList();
        lhmObj.put("DAR", "Lock");
        llObj.add("Accept");
        llObj.add("Reject");
        lhmObj.put("New Req", llObj);
        obj.put("SCC", "Lock");
        obj.put("Dummy", lhmObj);
        obj.put("Requisition", llObj);
    }




    public void readJson(String jsonString) {
        try {
            InputStream isObj = new ByteArrayInputStream(jsonString.getBytes("UTF-8"));

            String jsonTxt = IOUtils.toString(isObj);

            JSONObject json = (JSONObject) JSONSerializer.toJSON(jsonTxt);
            String foo = json.getString("foo");
            double coolness = json.getDouble("coolness");
            int altitude = json.getInt("altitude");
            JSONObject pilot = json.getJSONObject("pilot");
            String firstName = pilot.getString("firstName");
            String lastName = pilot.getString("lastName");

            JSONObject dummy = json.getJSONObject("Dummy");
            String dar = dummy.getString("DAR");

            String scc = dummy.getString("SCC");

            JSONArray newreq = dummy.getJSONArray("New Req");
            Iterator<String> iterator = newreq.iterator();
            System.out.print("New Req : ");
            while (iterator.hasNext()) {
                System.out.print(iterator.next());
                System.out.print(" ");
            }



        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static {
        //for localhost testing only
        javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(
                new javax.net.ssl.HostnameVerifier(){

                    public boolean verify(String hostname,
                                          javax.net.ssl.SSLSession sslSession) {
                        if (hostname.equals("localhost:9091")) {
                            return true;
                        }
                        return false;
                    }
                });
    }

    public static void main(String args[]) {
       httpClient client = new httpClient();
        client.SendJSON("/Users/Raman/open-lmis/test-modules/test-core/src/main/java/org/openlmis/UiUtils/DummyJSON.txt", "https://localhost:9091/facilities.json", "POST");


 //       new ServiceUtils().createJson();


    }

    private static class DefaultTrustManager implements X509TrustManager {

        @Override
        public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}

        @Override
        public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }

}
