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
import javax.json.*;
import java.io.StringReader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.eclipse.che.examples.IvonaCredentials;

import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.ivona.services.tts.IvonaSpeechCloudClient;
import com.ivona.services.tts.model.CreateSpeechRequest;
import com.ivona.services.tts.model.CreateSpeechResult;
import com.ivona.services.tts.model.Input;
import com.ivona.services.tts.model.Voice;

public class GreetingController implements Controller
{
   private final String USER_AGENT = "Mozilla/5.0";
   
   private static IvonaSpeechCloudClient speechCloud;
   
    private static void initIvona() {
        speechCloud = new IvonaSpeechCloudClient(new IvonaCredentials("3v8M2zbWQwuSFfGypJ2KJLWPLhwDBMIzASMMI50a", "GDNAINV33UU2ZPABUIIQ"));
        speechCloud.setEndpoint("https://tts.eu-west-1.ivonacloud.com");
    }

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
         
      String lastMessage = "something wrong with message parsing";
      try{
          lastMessage = getLastMessageText(jb.toString());
          initIvona();
          
          CreateSpeechRequest createSpeechRequest = new CreateSpeechRequest();
          Input input = new Input();
          Voice voice = new Voice();
    
          voice.setName("Salli");
          input.setData("This is a sample text to be synthesized.");
    
          createSpeechRequest.setInput(input);
          createSpeechRequest.setVoice(voice);
          InputStream in = null;
          FileOutputStream outputStream = null;
          CreateSpeechResult createSpeechResult = speechCloud.createSpeech(createSpeechRequest);

          System.out.println("\nSuccess sending request:");
          System.out.println(" content type:\t" + createSpeechResult.getContentType());
          System.out.println(" request id:\t" + createSpeechResult.getTtsRequestId());
          System.out.println(" request chars:\t" + createSpeechResult.getTtsRequestCharacters());
          System.out.println(" request units:\t" + createSpeechResult.getTtsRequestUnits());
                    
      } catch (Exception e){      }
      sendPost(lastMessage);


      ModelAndView view = new ModelAndView("hello_view");
      view.addObject("greeting", "result");
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

    private String getLastMessageText(String jb) throws Exception{
        JsonReader rdr = Json.createReader(new StringReader(jb));
        JsonObject obj = rdr.readObject();
        return obj.getJsonObject("message").getJsonString("text").getString();
    }
}
