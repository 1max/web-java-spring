package org.eclipse.che.examples;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;

public class GreetingController implements Controller
{

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
}
