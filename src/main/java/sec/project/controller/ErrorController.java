package sec.project.controller;

import org.springframework.stereotype.Controller; 
import org.springframework.web.bind.annotation.RequestMapping; 
 
@Controller 
public class ErrorController implements org.springframework.boot.autoconfigure.web.ErrorController { 
 
  @Override 
  public String getErrorPath() { 
    return "/error"; 
  } 
 
  @RequestMapping("/error") 
  public String error() { 
    return "error";
  } 
 
}
