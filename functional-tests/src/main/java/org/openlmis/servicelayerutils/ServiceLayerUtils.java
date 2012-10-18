package org.openlmis.servicelayerutils;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;


import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import net.sf.json.*;

public class ServiceLayerUtils {
    /*
      * Java function to post JSON
      */

    /*
   When the message is processed successfully,
   the HTTP response has a 200 status and the body
   contains more information about the status of the message (including possible errors).
   When the request is rejected, the HTTP response contains a non-200 status code (such as 400, 401, or 503).
    */

    /* For example :
    String responseJson=
        postJSON("{\"account\":\"dummy\",\"password\":\"pass\"}","http://www.f-list.net/json/getApiTicket.php");
        System.out.println(responseJson);
     */

    @SuppressWarnings("finally")
    public static String postJSON(String json, String endPoint) {
        String output, outputFinal = "";
        try {
            URL url = new URL(endPoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            String input = json;
            OutputStream os = conn.getOutputStream();
            os.write(input.getBytes());
            os.flush();
            System.out.println(conn.getResponseMessage());

            //HttpURLConnection.HTTP_CREATED
            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));
            System.out.println("Output .... \n");
            while ((output = br.readLine()) != null) {
                outputFinal = outputFinal + output;
            }
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

    /*
    * Java function to get JSON
    */


    @SuppressWarnings("finally")
    public static String getJSON(String endPoint) {
        String output, outputFinal = "";
        try {

            URL url = new URL(endPoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            System.out.println(conn.getResponseMessage());
            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            System.out.println("Output  .... \n");
            while ((output = br.readLine()) != null) {
                outputFinal = outputFinal + output;
            }
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

    /*
    * Dummy java function to create a Json
    */

    public static void createJson() {
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

    /*
      * Java function to read file content
      */
    @SuppressWarnings("finally")
    public static String readFile(String filePath) {
        String strLine, strLineFinal = "";
        try {
            FileInputStream fstream = new FileInputStream(filePath);
            // Get the object of DataInputStream
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            // Read File Line By Line
            while ((strLine = br.readLine()) != null) {
                strLineFinal = strLineFinal + strLine;
            }
            // Close the input stream
            in.close();
        } catch (Exception e) {// Catch exception if any
            System.err.println("Error: " + e.getMessage());
        } finally {
            return strLineFinal;
        }
    }

    /*
    * Dummy java function to read Json String
    */

    public static void readJson(String filePath) {
        try {
            String jsonString = readFile(filePath);
            System.out.println(jsonString);
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
        /*
       readJson(System.getProperty("user.dir")+"/modules/functional-tests/src/main/java/com/openlmis/servicelayerutils/sample-json.txt");
        */

        /*
        String responseJson=
        postJSON("{\"account\":\"dummy\",\"password\":\"pass\"}","http://www.f-list.net/json/getApiTicket.php");
        System.out.println(responseJson);
         */

        /*
         String responseJson=
           getJSON("http://www.f-list.net/json/getApiTicket.php");
        System.out.println(responseJson);
         */

        /*
      createJson();
        */

    }

}
