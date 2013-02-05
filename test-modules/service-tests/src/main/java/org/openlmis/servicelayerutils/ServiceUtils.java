package org.openlmis.servicelayerutils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;

public class ServiceUtils {
    HttpURLConnection conn ;



    @SuppressWarnings("finally")
    public String postNONJSON(String json, String endPoint) {
        String output, outputFinal = "";

        try {

            java.net.CookieHandler.setDefault(new java.net.CookieManager(null, java.net.CookiePolicy.ACCEPT_ALL));
            URL url = new URL(endPoint);
            conn = (HttpURLConnection) url.openConnection();

            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Accept", "application/json, text/plain, */*");
            conn.setRequestProperty("Connection", "keep-alive");

            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");



            String input = json;

            OutputStream os = conn.getOutputStream();
            os.write(input.getBytes());
            os.flush();

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

    @SuppressWarnings("finally")
    public String postJSON(String json, String endPoint) {
        String output, outputFinal = "";

        try {

            URL url = new URL(endPoint);
            conn = (HttpURLConnection) url.openConnection();

            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Accept", "application/json, text/plain, */*");
            conn.setRequestProperty("Connection", "keep-alive");
            conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");



            String input = json;

            OutputStream os = conn.getOutputStream();
            os.write(input.getBytes());
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
           // e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {
            return outputFinal;


        }
    }

    public String putJSON(String json, String endPoint) {
        String output, outputFinal = "";

        try {

            URL url = new URL(endPoint);
            conn = (HttpURLConnection) url.openConnection();

            conn.setDoOutput(true);
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Accept", "application/json, text/plain, */*");
            conn.setRequestProperty("Connection", "keep-alive");
            conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");



            String input = json;

            OutputStream os = conn.getOutputStream();
            os.write(input.getBytes());
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
            // e.printStackTrace();
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
        System.out.println(obj);
    }


    public String getFacilityFieldJSON(String jsonString, String field) {
        String id=null;
        try {
            InputStream isObj = new ByteArrayInputStream(jsonString.getBytes("UTF-8"));

            String jsonTxt = IOUtils.toString(isObj);

            JSONObject json = (JSONObject) JSONSerializer.toJSON(jsonTxt);
            JSONObject facility = json.getJSONObject("facility");
             id = facility.getString(field);


        } catch (IOException e) {
            e.printStackTrace();
        }
        finally
        {
            return id;
        }
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
            System.out.println("");
            System.out.println("FOO : " + foo);
            System.out.println("Coolness: " + coolness);
            System.out.println("Altitude: " + altitude);
            System.out.println("Pilot: firstName : " + firstName);
            System.out.println("Pilot: lastName : " + lastName);
            System.out.println("DAR : " + dar);
            System.out.println("SCC : " + scc);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {
//       new ServiceUtils().readJson("{\"foo\":\"bar\",\n" +
//               " \"coolness\":2.0,\n" +
//               " \"altitude\":39000,\n" +
//               " \"pilot\":{\"firstName\":\"Manjyot\",\"lastName\":\"Singh\"},\n" +
//               " \"mission\":\"apollo 11\",\n" +
//               " \"Dummy\":{\"DAR\":\"Lock\",\"SCC\":\"Locked\",\"New Req\":[\"Accept\",\"Reject\"]}}");


//        new ServiceUtils().createJson();


    }

}
