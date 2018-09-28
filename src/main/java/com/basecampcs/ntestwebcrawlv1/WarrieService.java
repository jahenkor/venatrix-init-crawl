/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.basecampcs.ntestwebcrawlv1;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.Map;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONObject;

/**
 *
 * @author juliusahenkora
 */

 class ParameterStringBuilder {
    public static String getParamsString(Map<String, String> params) 
      throws UnsupportedEncodingException{
        StringBuilder result = new StringBuilder();
 
        for (Map.Entry<String, String> entry : params.entrySet()) {
          result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
          result.append("=");
          result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
          result.append("&");
        }
 
        String resultString = result.toString();
        return resultString.length() > 0
          ? resultString.substring(0, resultString.length() - 1)
          : resultString;
    }
}

/**
 * Grabs html login fields
 * @author juliusahenkora
 */
public class WarrieService {
    
    
    
    private static final String USER_AGENT = "Mozilla/5.0";
    
    
    public static void main(String[] args) throws MalformedURLException, IOException{
    
        JSONObject json = new JSONObject();
json.put("login_page", "https://en.wikipedia.org/w/index.php?title=Special:UserLogin&returnto=Main+Page");    

CloseableHttpClient httpClient = HttpClientBuilder.create().build();

try {
    HttpPost request = new HttpPost("http://ec2-52-91-130-19.compute-1.amazonaws.com/loginform/");
    StringEntity params = new StringEntity(json.toString());
    request.addHeader("content-type", "application/json");
    request.setEntity(params);
    httpClient.execute(request);
// handle response here...
} catch (Exception ex) {
    // handle exception here
} finally {
    httpClient.close();
}
}
    }
    

