/**
f **************************************************************************
 * @author Julius Ahenkora
 * Basecampcs Web Spider
 *
 * File Name: Controller.java
 *
 * Description <Controller class, controls main functionality of spider,
 *                          and database interaction
 *                          Main Dependencies:
 *                                  https://github.com/yasserg/crawler4j >
 **************************************************************************
 */
package com.basecampcs.ntestwebcrawlv1;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.crawler.authentication.AuthInfo;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

import java.util.ArrayList;
import java.util.List;

import com.c05mic.generictree.Tree;
import com.basecampcs.ntestwebcrawlv1.controller.MasterController;
import edu.uci.ics.crawler4j.crawler.authentication.FormAuthInfo;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;

import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class Controller {

    public static ConcurrentHashMap<String, ConcurrentHashMap<String, String>> crawlInstances;
    static Integer totalUrls;
    private static final Logger logger = LoggerFactory.getLogger(Controller.class);
    private static final Pattern urlFilter = Pattern.compile("(?i)^(?:(?:https?|ftp)://)(?:\\S+(?::\\S*)?@)?(?:(?!(?:10|127)(?:\\.\\d{1,3}){3})(?!(?:169\\.254|192\\.168)(?:\\.\\d{1,3}){2})(?!172\\.(?:1[6-9]|2\\d|3[0-1])(?:\\.\\d{1,3}){2})(?:[1-9]\\d?|1\\d\\d|2[01]\\d|22[0-3])(?:\\.(?:1?\\d{1,2}|2[0-4]\\d|25[0-5])){2}(?:\\.(?:[1-9]\\d?|1\\d\\d|2[0-4]\\d|25[0-4]))|(?:(?:[a-z\\u00a1-\\uffff0-9]-*)*[a-z\\u00a1-\\uffff0-9]+)(?:\\.(?:[a-z\\u00a1-\\uffff0-9]-*)*[a-z\\u00a1-\\uffff0-9]+)*(?:\\.(?:[a-z\\u00a1-\\uffff]{2,}))\\.?)(?::\\d{2,5})?(?:[/?#]\\S*)?$");

    
// Data for authentication methods
    String userName = "";
    String password = "";
    static String DbUsername = "neo4j";
    static String DbPassword = "Basecamp";
    String testcase = "";
    String projectName = "";
    String urlLogin = "";
    String nodeID = "";
    String url = "";
    CrawlConfig config;
    GraphDatabaseConn urlToGraphDb;

    //Login Form Fields
    String fieldUsername = "";
    String fieldPassword = "";

    static {
        crawlInstances = new ConcurrentHashMap<>();
    }

    public Controller() {
    }

    /**
     * Controller main method Required Parameters: Url, username, Password
     * Optional Parameters: Array of seed urls?, Number of crawlers, crawler
     * storage folder, possibly extra config options?
     *
     */
    /* Print Current Beans in use?
    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {

            System.out.println("Let's inspect the beans provided by Spring Boot:");

            String[] beanNames = ctx.getBeanDefinitionNames();
            Arrays.sort(beanNames);
            for (String beanName : beanNames) {
                System.out.println(beanName);
            }

        };}*/
    @Async
    public void startCrawl(String[] args) throws Exception {

        /*
        Sample args for ide testing/ non rest-api
        
        args = new String[9];
        
        args[0] = "https://auth-demo.aerobaticapp.com/";
        args[1] = "aerobatic";
        args[2] = "aerobatic";
        args[3] = "neo4j";
        args[4] = "1a2W3B5c"; 
        args[5] = "0";
        args[6] = "LoginFormTest";
        args[7] = "";
        args[8] = "https://auth-demo.aerobaticapp.com/protected-standard/";*/
        if (args == null) {
            System.out.println("ERRROR!");
            return;
        }

        if (args.length == 1 && args[0].equals("help")) {
            System.out.println("How to use: Enter a website with format http://www(---).com\n"
                    + "Followed by a username ,password, Database username, Database Password\n"
                    + "Use case: webspooder <http://www.phptravels.net> <Username Password\n"
                    + "Type help for more info");
            System.out.println("Graph Database: \n" + "Node Properties: Id,ParentId,Site\n"
                    + "Relationships: childDir");

            return;

        } else if (args.length != 8) {
            System.out.println("           ;               ,           \n"
                    + "         ,;                 '.         \n"
                    + "        ;:                   :;         \n"
                    + "       ::                     ::       \n"
                    + "       ::                     ::       \n"
                    + "       ':                     :         \n"
                    + "        :.                    :         \n"
                    + "     ;' ::                   ::  '     \n"
                    + "    .'  ';                   ;'  '.     \n"
                    + "   ::    :;                 ;:    ::   \n"
                    + "   ;      :;.             ,;:     ::   \n"
                    + "   :;      :;:           ,;\"      ::   \n"
                    + "   ::.      ':;  ..,.;  ;:'     ,.;:   \n"
                    + "    \"'\"...   '::,::::: ;:   .;.;\"\"'     \n"
                    + "        '\"\"\"....;:::::;,;.;\"\"\"         \n"
                    + "    .:::.....'\"':::::::'\",...;::::;.   \n"
                    + "   ;:' '\"\"'\"\";.,;:::::;.'\"\"\"\"\"\"  ':;   \n"
                    + "  ::'         ;::;:::;::..         :;   \n"
                    + " ::         ,;:::::::::::;:..       :: \n"
                    + " ;'     ,;;:;::::::::::::::;\";..    ':. \n"
                    + "::     ;:\"  ::::::\"\"\"'::::::  \":     :: \n"
                    + " :.    ::   ::::::;  :::::::   :     ; \n"
                    + "  ;    ::   :::::::  :::::::   :    ;   \n"
                    + "   '   ::   ::::::....:::::'  ,:   '   \n"
                    + "    '  ::    :::::::::::::\"   ::       \n"
                    + "       ::     ':::::::::\"'    ::       \n"
                    + "       ':       \"\"\"\"\"\"\"'      ::       \n"
                    + "        ::                   ;:         \n"
                    + "        ':;                 ;:\"         \n"
                    + "-hrr-     ';              ,;'           \n"
                    + "            \"'           '\"             \n"
                    + "              '              ,·´¨'`·,'                   ,.,   '          ,-·-.          ,'´¨;    \n"
                    + "            :,   .:´\\                 ;´   '· .,         ';   ';\\      ,'´  ,':\\'  \n"
                    + "            ;   :\\:::\\              .´  .-,    ';\\        ;   ';:\\   .'   ,'´::'\\' \n"
                    + "           ;  ,':::\\·´'             /   /:\\:';   ;:'\\'      '\\   ';::;'´  ,'´::::;'  \n"
                    + ",.,      .'  ,'::::;''             ,'  ,'::::'\\';  ;::';        \\  '·:'  ,'´:::::;' '  \n"
                    + ";   '\\   ;  ,'::::;           ,.-·'  '·~^*'´¨,  ';::;         '·,   ,'::::::;'´    \n"
                    + " \\  ';',·'  ,'::::;            ':,  ,·:²*´¨¯'`;  ;::';          ,'  /::::::;'  '    \n"
                    + "  '\\    ,.'\\::::;''            ,'  / \\::::::::';  ;::';        ,´  ';\\::::;'  '      \n"
                    + "    \\¯\\::::\\:;' ‘           ,' ,'::::\\·²*'´¨¯':,'\\:;         \\`*ª'´\\\\::/‘         \n"
                    + "     '\\::\\;:·´'              \\`¨\\:::/          \\::\\'          '\\:::::\\';  '        \n"
                    + "       ¯       °            '\\::\\;'            '\\;'  '          `*ª'´‘            \n"
                    + "                               `¨'                               '               ");
            System.out.println("How to use: Enter a website with format http://www(---).com\n"
                    + "Followed by a username ,password, Database username, Database Password\n"
                    + "Use case: http://www.phptravels.net Username Password\n"
                    + "Type help for more info on program or database implmentation");

            throw new IllegalArgumentException("Invalid Arguments!");

        }

        url = args[0];

        Controller.crawlInstances.put(url, new ConcurrentHashMap<>());

        String crawlStorageFolder = "data/crawl/root";
        int numberOfCrawlers = 7;

        config = new CrawlConfig();
        config.setUserAgentString("BasecampCS: www.basecampcs.com");
        config.setCrawlStorageFolder(crawlStorageFolder);
        config.setIncludeHttpsPages(true);

        //Init fields
        userName = args[1];
        password = args[2];
        DbUsername = "neo4j";
        DbPassword = "Basecamp";
        testcase = args[3];
        projectName = args[6];
        urlLogin = args[4];
        nodeID = args[7];

        //Login Form Fields
        String fieldUsername = "ctrlView:fld.logon_user_name";
        String fieldPassword = "ctrlView_fld.logon_password";
        try {

            config.setConnectionTimeout(1000);

            config.setCookieStore(new BasicCookieStore());
            config.setCookiePolicy(CookieSpecs.STANDARD);
            config.setIncludeHttpsPages(true);
            config.setFollowRedirects(true);

            //Login to Website
            login();

            /*
         * Instantiate the controller for this crawl.
             */
            PageFetcher pageFetcher = new PageFetcher(config);
            RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
            RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
            CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

            //Test Cookie Store : Debugging
            System.out.println("CookieStore: \n" + config.getCookieStore());
            for (Cookie x : config.getCookieStore().getCookies()) {
                System.out.println(x.toString());
            }


            /*
         * For each crawl, you need to add some seed urls. These are the first
         * URLs that are fetched and then the crawler starts following links
         * which are found in these pages
             */
            controller.addSeed(url);
            //controller.addSeed("http://phptravels.org/clientarea.php?action=contacts");

            totalUrls = 0;
            MyCrawler.configure("HtmlContent", url);

            controller.start(MyCrawler.class, numberOfCrawlers);

            
            /*
        
        Debugging: Print Crawler Local Data
        
        System.out.println("Crawler Local Data");
        for(Object x : controller.getCrawlersLocalData()){
            System.out.println(x.toString());
            System.out.println(x);
            
        }*/
            ConcurrentHashMap<String, MyCrawler.HtmlContent> htmlContent = MyCrawler.getHtmlContentAsMap();

            //Debug: Print Urls
            System.out.println("Urls as Map");
            Object[] instanceUrls = crawlInstances.get(url).values().toArray();
            for (int i = 0; i < instanceUrls.length; i++) {
                System.out.println(instanceUrls[i]);
            }

            LoadDriver sqlConnection = new LoadDriver();

            //Sort crawler data for efficient data manipulation
            CrawlerSort startSort = new CrawlerSort(crawlInstances.get(url));
            //startSort.readUrlFromFile();
            startSort.sortList();
            totalUrls = startSort.size();

            sqlConnection.loadSQLDb(htmlContent, projectName, testcase);

            //Persist list containing site hierarchy into tree format
            Tree<String> siteMapTree = startSort.createTree();

            urlToGraphDb = new GraphDatabaseConn("bolt://ec2-52-91-130-19.compute-1.amazonaws.com:7687", DbUsername, DbPassword, testcase, projectName, crawlInstances.get(url));
            urlToGraphDb.createDb(siteMapTree.getRoot());

            //BFS of tree -Test-
            startSort.displayBFS(siteMapTree.getRoot());

        } catch (Exception e) {

            LoadDriver sqlConnection = new LoadDriver();
            sqlConnection.loadSQLDb(nodeID, 3, projectName);
            System.out.println("TCstatus 3\n" + e);

        } finally {
            crawlInstances.remove(url);
            System.out.println("Crawler Complete!");

//Close all write/read streams
            urlToGraphDb.close();
        }

    }

    /**
     * Login to Root url
     *
     * @throws IOException
     */
    public void login() throws IOException {

//To Do: Grab cookie store from url, login to page!
        AuthInfo siteLogin = new FormAuthInfo(userName, password, urlLogin,
                fieldUsername, fieldPassword);
        //AuthInfo authInfo1 = new BasicAuthInfo(userName, password, urlLogin);
        //nameUsername,namePassword);

        /**
         * Multiple auth
         */
        List<AuthInfo> authList = new ArrayList<>();
        authList.add(siteLogin);
        config.setAuthInfos(authList);

        // Could possibly use Jsoup for cookie requests and convert to apache.Cookie?
//This will get you the response.
        Response res = Jsoup
                .connect(urlLogin)
                .data("email", userName, "password", password)
                .method(Method.POST)
                .execute();

//This will get you cookies
        Map<String, String> loginCookies = res.cookies();
        System.out.println("JSOUP: \n" + loginCookies.toString());

//And this is the easiest way I've found to remain in session
        org.jsoup.nodes.Document doc = Jsoup.connect(url)
                .cookies(loginCookies)
                .get();

    }

    public static String reportProgress() {

        //Crawling in progress
        if (totalUrls == 0) {
            return "Crawling: " + MyCrawler.getSize();
        }
        return "Database Dump Progress: " + (GraphDatabaseConn.reportProgress() + LoadDriver.reportProgress()) / (totalUrls * 2);
    }

    public static boolean validation(String url){
        
        Matcher matcher = urlFilter.matcher(url);
        if(matcher.find()){
            return true;
        }
        
                return false;
                }
}

