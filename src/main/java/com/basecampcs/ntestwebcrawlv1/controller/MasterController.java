/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.basecampcs.ntestwebcrawlv1.controller;
import com.basecampcs.ntestwebcrawlv1.Controller;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
/**
 *
 * @author altrusticshade
 * To Do: Create Functionality to update existing values in table for TC0
 * Additional Arg's : Login form fields.
 * 
 * To Do: Fix http status returned at beginging of transaction for the invalid param case
 *        Use Environment Variables for easy updates
 */

@RestController
public class MasterController {
    Controller inst;
    public static ArrayList<String> arr = new ArrayList<>();
     
   
@RequestMapping(
    value = "/Crawl", 
    method = RequestMethod.POST)
public ResponseEntity<?> process(@RequestBody Map<String, Object> payload, final HttpServletRequest request) throws Exception {
    try{
        
        
        
    Executors.newScheduledThreadPool(1).schedule( () -> CrawlHelper(payload,request),
                                                   10, TimeUnit.SECONDS
                                                   );
                                                 if(Controller.validation(payload.get("url").toString()))
                                                    return new ResponseEntity<>(HttpStatus.ACCEPTED);
                                               
                                                 return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
    }
    catch(Exception e){
        System.out.println(e);
        return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
    }
}

@RequestMapping(
    value = "/CrawlTest", 
    method = RequestMethod.POST)
public ResponseEntity<?> CrawlHelper(@RequestBody Map<String, Object> payload, final HttpServletRequest request) throws Exception {
 
   
                                   
        
        try{
         inst = new Controller();
        String[] args = {payload.get("url").toString(),
                         payload.get("username").toString(),
                         payload.get("password").toString(),
                         payload.get("testcase").toString(),
                         payload.get("loginUrl").toString(),
                         payload.get("projectId").toString(),
                         payload.get("projectName").toString(), 
                         payload.get("nodeID").toString()};
        
        arr.add((String) payload.get("url"));
        System.out.println("User Input: ");
        for(String x : args){
            System.out.println(x);
        }
        inst.startCrawl(args);
        return new ResponseEntity<>("Request Accepeted", HttpStatus.ACCEPTED); //202
        }
        catch(IllegalArgumentException | NullPointerException err){
            return new ResponseEntity<>(err, HttpStatus.NOT_ACCEPTABLE); // Http 406 
        }
        // Http 406

        catch(Exception err){
                return new ResponseEntity<>(err, HttpStatus.INTERNAL_SERVER_ERROR); //404
                }
       // return;
    }
    
    @RequestMapping(value = "/progress",method = RequestMethod.GET) 
    public String progress(){
        if(inst == null){return Integer.toString(0);}
      //  while(inst.reportProgress()!= 1){
     //   return ;   
     //   }
        
        return inst.reportProgress();
                
    }
    
    //Container health status
    
      @RequestMapping(value = "/health",method = RequestMethod.GET)
    public ResponseEntity<?> health(){
       
        
         return new ResponseEntity<>("Healthy", HttpStatus.OK); //200
                
    }
    /**
    @RequestMapping(value = "/add",method = RequestMethod.GET)
    public ResponseEntity<?> add(){
       
        
         return new ResponseEntity<>("addedfield", HttpStatus.OK); //200
                
    }
    **/
}

