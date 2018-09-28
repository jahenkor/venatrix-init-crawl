/**
 **************************************************************************
 *
 * @author Julius Ahenkora
 * Basecampcs Web Spider
 *
 * File Name: GraphDatabaseConn.java
 *
 * Description <Graph Database class, connects to and persists tree into
 *                                  neo4j database instance>
 *
 */
package com.basecampcs.ntestwebcrawlv1;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.io.fs.FileUtils;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import java.util.UUID;
import com.google.gson.*;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

/**
 * Random String Generator
 */
@Component

 class randomStringGenerator {
    

    public  String generateString() {
        String uuid = UUID.randomUUID().toString();
        return  "uuid"+uuid;
    }
}

/**
 * Graph Database Connection class
 */
public class GraphDatabaseConn implements AutoCloseable
{
    private static final File databaseDirectory = new File( "target/neo4j-hello" );

    public String greeting = "";
    public String relationships = "";
    String testCase = "";
    static double urlsInDB = 0.0;
    String projectName = "";
    Integer projectId;

    GraphDatabaseService graphDb;
    HashMap<String,String> allNodes;
    private final Driver driver;
    Node firstNode;
    Node secondNode;
    Relationship relationship;
    String testCaseID;
    ConcurrentHashMap<String,String> urls;
    

    /**
     * Constructor
     *@param uri Host connection
     *@param user Username for database connection
     *@param password Password for database connection
     * @param testCase Test Case ID
     * @param projectName
     */
    public GraphDatabaseConn(String uri, String user, String password, String testCase, String projectName,ConcurrentHashMap<String,String> urls){
                urlsInDB = 0;
                System.out.println("Starting Graph Databaes...."); 
                driver = GraphDatabase.driver( uri, AuthTokens.basic( user, password ) );
                allNodes = new HashMap<>();
                this.testCase = "TC0"; 
                this.testCaseID = testCase;
                this.urls = urls;
                this.projectName = projectName;
                 //this.projectId = projectId;
    }
   
    private static enum RelTypes implements RelationshipType
    {
        KNOWS, CHILDDIR
    }
    

    
     /*
      * Close database connection
      */
    @Override
    public void close() throws Exception
    {
        driver.close();
    }
        
    public void removeNodes() {
        
         try ( Session session = driver.session() )
        {        
            System.out.println("Remove");
            System.out.println("MATCH (n:"+projectName+"_"+testCase+") DETACH DELETE n");
            session.run("MATCH (n:"+projectName+"_"+testCase+") DETACH DELETE n");
            
        }
    }
    
    /**
    *Create Database
    *@param root Root node of tree
    */
    void createDb(com.c05mic.generictree.Node<String> root) throws IOException
    {
        //Remove existing nodes from database
        removeNodes();
        
        randomStringGenerator uniq = new randomStringGenerator();
        
        FileUtils.deleteRecursively( databaseDirectory );

         int curlevel = 1;
    int nextlevel = 0;

    LinkedList<com.c05mic.generictree.Node<String>> queue = new LinkedList<com.c05mic.generictree.Node<String>>();
    queue.add(root);
    greeting += "CREATE";

    //Persist tree into database via breadth first traversal
    while(!queue.isEmpty()) { 
        com.c05mic.generictree.Node<String> node = queue.remove(0);
        
         
         String parent = uniq.generateString();
         parent = parent.replace("-", "");
         if(!allNodes.containsKey(node.getData())){
         allNodes.put(node.getData(), parent);
            
         
            greeting += "(" +parent+":"+projectName+"_"+testCase +" {url:(\"" +
                   node.getData()+"\"),"+" nodeParent_id:(\"" + 
                    "null"+"\"),"+
                   " name:(\""+node.getData()+"\"),"+
                    " title:(\""+urls.get(node.getData())+"\")," +
                     "description:(\""+node.getData()+"\")," +
                    " node_id:(\""+parent+"\"),"+" tcid"+":(\""+
                    testCaseID+"\")}),";
            urlsInDB += 1;
         }
        
         
        
        if (curlevel == 0) {
            System.out.println();
            curlevel = nextlevel;
            nextlevel = 0;
        }

        for(com.c05mic.generictree.Node<String> n : node.getChildren()) {
           String child = uniq.generateString();
            child = child.replace("-", "");
      // if(n.getData().equals(node.getData())){continue;}
            if(!allNodes.containsKey(n.getData())){
                allNodes.put(n.getData(), child);
           greeting += "(" +child+":"+projectName+"_"+testCase +" {url:(\"" +
                   n.getData()+"\"),"+" nodeParent_id:(\"" +
                   allNodes.get(n.getParent().getData())+"\"),"+
                   " node_id:(\""+child+"\"),"+
                   " title:(\""+urls.get(n.getData())+"\"),"+
                   " name:(\""+n.getData()+"\"),"+
                   " description:(\""+n.getData()+"\"),"+
                   " tcid"+":(\""+testCaseID+"\")}),";
           urlsInDB += 1;
           
           
            }
     
            
           relationships += "("+allNodes.get(node.getData())+")-[:parentOf]->"+"("+allNodes.get(n.getData())+"),";
           
            queue.addLast(n);
            nextlevel++;
        }

        curlevel--;
        //System.out.println(node.getData() + " ");
    }
    
if(relationships.length() > 1){    
    relationships = relationships.subSequence(0, relationships.length()-1).toString();
}
System.out.println("Url size: "+ urls.size());
            if(urls.size()==1){greeting = greeting.subSequence(0, greeting.length()-1).toString();}
//System.out.println(greeting+relationships);

       try ( Session session = driver.session() )
        {
            Gson gson = new Gson();
            //System.out.println(greeting+relationships);
            System.out.println(greeting+relationships);
            session.run(greeting+"\n"+relationships) ;
            StatementResult result = session.run("MATCH (n:Url) RETURN n");
            

        }
      
        }

            
        

    void shutDown()
    {
        
        System.out.println( "Shutting down database ..." );
        
        graphDb.shutdown();
       
    }

    
    private static void registerShutdownHook( final GraphDatabaseService graphDb )
    {
        // Registers a shutdown hook for the Neo4j instance so that it
        // shuts down nicely when the VM exits (even if you "Ctrl-C" the
        // running application).
        Runtime.getRuntime().addShutdownHook( new Thread()
        {
            @Override
            public void run()
            {
                graphDb.shutdown();
            }
        } );
    }
    
    public static double reportProgress(){
        return urlsInDB;
    }
}

