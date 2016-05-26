package org.eclipse.che.examples;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

public class GreetingController implements Controller
{
   private final String USER_AGENT = "Mozilla/5.0";

   @Override
   public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception
   {
      StringBuffer jb = new StringBuffer();
      String line = null;
      try {
        BufferedReader reader = request.getReader();
        while ((line = reader.readLine()) != null)
          jb.append(line);
      } catch (Exception e) { /*report an error*/ }
  
      System.out.println("TESTTESTTESTTEST " + jb.toString());  
      
      
      sendPost(getLastMessageText());

      String userName = request.getParameter("user");
      String result = "";
      if (userName != null)
      {
        result = "Hello, " + userName + "!";
      }

      ModelAndView view = new ModelAndView("hello_view");
      view.addObject("greeting", result);
      return view;
   }
   

    // HTTP POST request
    private void sendPost(String prefixMessage) throws Exception {

        String url = "https://api.telegram.org/bot212564900:AAHoP3sQR4huAlIFegZh6h-EqFEJssmPCnM/sendmessage";
        URL obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        //add reuqest header
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

        String urlParameters = "chat_id=223196617&text=New%20Message!%20" + prefixMessage;


        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'POST' request to URL : " + url);
        System.out.println("Post parameters : " + urlParameters);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //print result
        System.out.println(response.toString());

    }

    private String getLastMessageText() {
        try {
        URL url = new URL("https://api.telegram.org/bot212564900:AAHoP3sQR4huAlIFegZh6h-EqFEJssmPCnM/getupdates");
        try (InputStream is = url.openStream();
             JsonReader rdr = Json.createReader(is)) {
            JsonObject obj = rdr.readObject();
            JsonArray results = obj.getJsonArray("result");

            return results.getJsonObject(results.size() - 1).getJsonObject("message").getJsonString("text").getString();
            }
        }
        catch (IOException ex)
        {
            //om-nom-nom
        }
    }
}
