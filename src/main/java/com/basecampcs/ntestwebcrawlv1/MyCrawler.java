/**
 **************************************************************************
 *@author altrusticshade
 *Basecampcs Web Spider
 *
 * File Name: MyCrawler.java
 *
 * Description <Crawler class>
 */
package com.basecampcs.ntestwebcrawlv1;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import java.io.File;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import java.util.regex.Pattern;
import org.neo4j.io.fs.FileUtils;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Component;

@Component
@EnableAutoConfiguration

@SuppressWarnings("unchecked")
public class MyCrawler extends WebCrawler{
    
    //Extensions to filter out while crawling
    private final static Pattern FILTERS = Pattern.compile(".*(\\.(css?|css|js|gif|jpg"
                                       + "|png|mp3|mp4|zip|gz|txt|xml|json|))$");
    public static MyCrawler Instance;
    public static String userUrl;
    
    
    private static final ArrayList<String> list = new ArrayList<>();
    //private static File storageFolder;
    private static String storageFolder;
 
    private static List<String> fileDirs;
    private static ConcurrentHashMap<String,HtmlContent> fileMap;
    private static ConcurrentHashMap<String,String> urlLists;
    
    protected class HtmlContent{
    String url;
    String status;
    byte[] htmlContent;
    
    public void setUrl(String url){
        this.url = url;
    }
    
    public void setStatus(String status){
        this.status = status;
    }
    
    public void setContent(byte[] htmlContent){
        this.htmlContent = htmlContent;
    }    
        
}
  

    public static List<String> getFileList(){
        return fileDirs;
    }
    
    public static ConcurrentHashMap<String,HtmlContent> getHtmlContentAsMap(){
        return fileMap;
    }
    
    public static int getSize(){
        return list.size();   
    }
    
      /**
     * This method receives two parameters. The first parameter is the page
     * in which we have discovered this new url and the second parameter is
     * the new url. You should implement this function to specify whether
     * the given url should be crawled or not (based on your crawling logic).
     * In this example, we are instructing the crawler to ignore urls that
     * have css, js, git, ... extensions and to only accept urls that start
     * with "http://www.ics.uci.edu/". In this case, we didn't need the
     * referringPage parameter to make the decision.
     */
     @Override
     public boolean shouldVisit(Page referringPage, WebURL url) {
         String href = url.getURL().toLowerCase();
         //System.out.println("Url: " + url+"Domain: " +url.getDomain());
         return !FILTERS.matcher(href).matches()
                && href.contains(userUrl); //&& !url.getURL().toLowerCase().contains("logoff");
         
     }
     public static void configure(String storageFolderName, String url) throws IOException {
      FileUtils.deleteRecursively( new File(storageFolderName) );
        storageFolder = storageFolderName;
        userUrl = url;
        urlLists = Controller.crawlInstances.get(userUrl);
     fileMap = new ConcurrentHashMap<>();
        fileDirs = new ArrayList<>();
        

    }
      /**
      * This function is called when a page is fetched and ready
      * to be processed by your program.
      */
     @Override
     public void visit(Page page) {
         if(
        !page.getContentType().equalsIgnoreCase("text/html; charset=utf-8")){
             return;
         }
         //System.out.println("url: "+ page.getWebURL().getURL()+"tag: "+page.getWebURL().getTag()+page.getContentType());
         String url = page.getWebURL().getURL();
         if(url.endsWith("/")){
             url = url.subSequence(0, url.length()-1).toString();
         }
         // get a unique name for storing this image
        String extension = url.substring(url.lastIndexOf('.'));
        String hashedName = UUID.randomUUID() + extension;

       
        String filename = storageFolder + "/" + hashedName;
        
        fileDirs.add(filename);
        File file = new File(filename);
       
       
         String title = page.getWebURL().getAnchor();
         if(title == null){title = "Home";}
         urlLists.put(url, title);
         System.out.println(page.getWebURL().getAnchor());
        
        
 
        HtmlContent contentMap = new HtmlContent();
        byte[] pageContents = page.getContentData();
        contentMap.setContent(pageContents);
        
        String pageHtml = new String(pageContents);
        if(!pageHtml.contains("<form") && !pageHtml.contains("<button") ){
            contentMap.setStatus("static");
        }
        else{
        contentMap.setStatus("Dynamic");
        }
        
        contentMap.setUrl(url);
                
            fileMap.put(url, contentMap);
            WebCrawler.logger.info("Stored: {}", url);
        

         if (page.getParseData() instanceof HtmlParseData) {
             HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
             String text = htmlParseData.getText();
             String html = htmlParseData.getHtml();
             Set<WebURL> links = htmlParseData.getOutgoingUrls();
             
             System.out.println("Text length: " + text.length());
             System.out.println("Html length: " + html.length());
             System.out.println("Number of outgoing links: " + links.size());
             for(WebURL outLinks : links){
                 System.out.println(outLinks.getURL());
         }
         
    }
}
}
