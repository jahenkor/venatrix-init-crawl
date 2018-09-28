/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.basecampcs.ntestwebcrawlv1;

/**
 *
 * @author juliusahenkora
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Component;

// Notice, do not import com.mysql.jdbc.*
// or you will have problems!
@Component
@EnableAutoConfiguration

public class LoadDriver {
   // JDBC driver name and database URL
   static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
   static final String DB_URL = "jdbc:mysql://venatrixio.cvo0fimkibyb.us-east-1.rds.amazonaws.com:3306/db?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&useSSL=false";
   static double urlsInDB = 0.0;
   
   //  Database credentials
   static final String USER = "RDS-Othmane";
   static final String PASS = "Pa$$word";
   
   /**
    * Initialize sql connection to dump all urls to db
    * @param testCase
    * @param tcStatus
    * @param projectName 
    */
  public void loadSQLDb(String testCase, int tcStatus, String projectName) {
   urlsInDB = 0;
   Connection conn = null;
   Statement stmt = null;
   try{
      //Register JDBC driver
      Class.forName("com.mysql.cj.jdbc.Driver");

      
      System.out.println("Connecting to database...");
      conn = DriverManager.getConnection(DB_URL,USER,PASS);

      System.out.println("Creating statement...");
      stmt = conn.createStatement();
      PreparedStatement statement;
      String loadDB;
          loadDB = "USE db";
      stmt.executeUpdate(loadDB);
      
      statement = conn.prepareStatement("UPDATE tcs SET tcrunstatus_id = ?  WHERE id = ?");
      statement.setInt(1,4);
      statement.setString(2, testCase);
      
      statement.executeUpdate();
      
      statement = conn.prepareStatement("UPDATE tcs SET tcstatuss_id = ?  WHERE id = ?");
      statement.setInt(1,5);
      statement.setString(2, testCase);
     
      statement.executeUpdate();
      

      
      System.out.println("Database created successfully...");
      //STEP 5: Extract data from result set
      
      
      stmt.close();
      conn.close();
   }catch(SQLException se){
      //Handle errors for JDBC
      se.printStackTrace();
   }catch(Exception e){
      //Handle errors for Class.forName
      e.printStackTrace();
   }finally{
      //finally block used to close resources
      try{
         if(stmt!=null)
            stmt.close();
      }catch(SQLException se2){
      }// nothing we can do
      try{
         if(conn!=null)
            conn.close();
      }catch(SQLException se){
         se.printStackTrace();
      }//end finally try
   }//end try
   System.out.println("Goodbye!");
}
  public void loadProgressDb(String progress, int tcStatus, String projectName) {
   urlsInDB = 0;
   Connection conn = null;
   Statement stmt = null;
   try{
      //Register JDBC driver
      Class.forName("com.mysql.cj.jdbc.Driver");

      
      System.out.println("Connecting to database...");
      conn = DriverManager.getConnection(DB_URL,USER,PASS);

      System.out.println("Creating statement...");
      stmt = conn.createStatement();
      PreparedStatement statement;
      String loadDB;
          loadDB = "USE db";
      stmt.executeUpdate(loadDB);
      
      statement = conn.prepareStatement("UPDATE tcs SET tcrunstatus_id = ?  WHERE id = ?");
      statement.setInt(1,4);
      statement.setString(2, progress);
      
      statement.executeUpdate();
      
      statement = conn.prepareStatement("UPDATE tcs SET tcstatuss_id = ?  WHERE id = ?");
      statement.setInt(1,5);
      statement.setString(2, progress);
     
      statement.executeUpdate();
      

      
      System.out.println("Database created successfully...");
      //STEP 5: Extract data from result set
      
      
      stmt.close();
      conn.close();
   }catch(SQLException se){
      //Handle errors for JDBC
      se.printStackTrace();
   }catch(Exception e){
      //Handle errors for Class.forName
      e.printStackTrace();
   }finally{
      //finally block used to close resources
      try{
         if(stmt!=null)
            stmt.close();
      }catch(SQLException se2){
      }// nothing we can do
      try{
         if(conn!=null)
            conn.close();
      }catch(SQLException se){
         se.printStackTrace();
      }//end finally try
   }//end try
   System.out.println("Goodbye!");
  }
  
   public void loadSQLDb(ConcurrentHashMap<String,MyCrawler.HtmlContent> siteMap, String projectName,String tcID) {
   urlsInDB = 0;
   Connection conn = null;
   Statement stmt = null;
   try{
      //STEP 2: Register JDBC driver
      //Class.forName("com.mysql.cj.jdbc.Driver");

      //STEP 3: Open a connection
      System.out.println("Connecting to database...");
      conn = DriverManager.getConnection(DB_URL,USER,PASS);

      //STEP 4: Execute a query
      System.out.println("Creating statement...");
      stmt = conn.createStatement();
      
      String createDbQuery;
      createDbQuery = "CREATE DATABASE IF NOT EXISTS CrawlDB";
      stmt.executeUpdate(createDbQuery);
      
      String loadDB;
      loadDB = "USE CrawlDB";
      stmt.executeUpdate(loadDB);
      
      
      String deleteDbQuery;
      //deleteDbQuery = "DELETE FROM Html WHERE EXISTS pname=" +projectName;
      PreparedStatement statement = conn.prepareStatement("DELETE FROM Html WHERE pname = ?");
      statement.setString(1,projectName);
      statement.executeUpdate();
      
      System.out.println("Creating Tables....");
      String createTbls = "CREATE TABLE IF NOT EXISTS Html" +
                   " (pname VARCHAR(255) not NULL, "
              + "url VARCHAR(255) not NULL, " +
                   "html LONGTEXT, "+
                   "status VARCHAR(255), "
              + "PRIMARY KEY (url))"; 

      stmt.executeUpdate(createTbls);
      
     
      System.out.println("Inserting Sitemap into DB");
      //for(Enumeration<String> url = siteMap.keys(); url.hasMoreElements();){
     
      System.out.println("Urls in DB");
      for(String url: siteMap.keySet()){
           statement = conn.prepareStatement("INSERT INTO Html"+" VALUES(?,?,?,?)");
          
          statement.setString(1,projectName);
          
          String s = url;
          //System.out.println(s);
          statement.setString(2,url);
          
          //Grab html content from sitemap
          String s1 = new String(siteMap.get(url).htmlContent);
          statement.setString(3,s1);
          
          String s2 = siteMap.get(url).status;
          statement.setString(4, s2);
        
          
          
      statement.executeUpdate();
      urlsInDB++;
      
      System.out.println(url);
      }
      
      System.out.println("Database created successfully...");
      //STEP 5: Extract data from result set
      
      
      loadDB = "USE db";
      stmt.executeUpdate(loadDB);
      
       statement = conn.prepareStatement("UPDATE tcs SET tcrunstatus_id = ?  WHERE id = ?");
      statement.setInt(1,4);
      statement.setString(2, tcID);
      statement.executeUpdate();
      
      statement = conn.prepareStatement("UPDATE tcs SET tcstatuss_id = ?  WHERE id = ?");
      statement.setInt(1,5);
      statement.setString(2, tcID);
      statement.executeUpdate();
      
      statement = conn.prepareStatement("UPDATE tcs SET errorDescription = ?  WHERE id = ?");
      statement.setString(1,"All Test Steps Passed!");
      statement.setString(2, tcID);
      statement.executeUpdate();
      System.out.println("TCstatus 2");
      
      stmt.close();
      conn.close();
   }catch(SQLException se){
      //Handle errors for JDBC
      se.printStackTrace();
   }catch(Exception e){
      //Handle errors for Class.forName
      e.printStackTrace();
   }finally{
      //finally block used to close resources
      try{
         if(stmt!=null)
            stmt.close();
      }catch(SQLException se2){
      }// nothing we can do
      try{
         if(conn!=null)
            conn.close();
      }catch(SQLException se){
         se.printStackTrace();
      }//end finally try
   }//end try
   System.out.println("Goodbye!");
}
   public static double reportProgress(){
       return urlsInDB;
   }
}
