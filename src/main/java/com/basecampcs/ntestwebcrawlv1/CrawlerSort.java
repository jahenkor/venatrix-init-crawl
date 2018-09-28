/**
 * @author juliusahenkora
 *
 * File Name: CrawlerSort.java
 *
 * Description <Crawler -  Crawler data manipulation functions>
 */
package com.basecampcs.ntestwebcrawlv1;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import com.c05mic.generictree.Node;
import com.c05mic.generictree.Tree;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;


public class CrawlerSort extends Controller {
    
    //protected static ConcurrentHashMap<String,String> list;
        protected  ArrayList<String> preSortList;

    protected  List<String> addedList;
    //Collection<String> list2 = new Collection<>();
    
    public CrawlerSort(ConcurrentHashMap<String,String> inputList){
       
        preSortList = new ArrayList<>();
        for(String x : inputList.keySet()){
            preSortList.add(x);
        }
        
        //System.out.println(java.util.Arrays.asList(inputList.values()));
    }
    
    /**
     * Reads urls from file
     * @throws IOException 
     */
    /**
     * Change this, use static list from MyCrawler
     * @throws IOException 
     */
    
    /*
    public void readUrlFromFile() throws IOException{
        list = Files.readAllLines(Paths.get("output1.txt"), StandardCharsets.UTF_8);
        ConcurrentSkipListSet uniques = new ConcurrentSkipListSet(list);
        list.clear();
        list.addAll(uniques);
    
    }*/
    
    public Integer size(){
        return preSortList.size();
    }
    
    /**
     * Sorts list of urls found from file
     * @throws FileNotFoundException - File not found / Issues opening file
     * @throws IOException - If an I/O Error occurs reading from the file 
     */
    public void sortList() throws FileNotFoundException, IOException{
        
        //readUrlFromFile();
        preSortList.sort(new Comparator(){
             @Override
             public int compare(Object o1, Object o2) {
                 String o3 = (String)o1;
                 String o4 = (String)o2;
                 
                 //Just laziness, try to remove these and replace below
                 String s1 = o3;
                 String s2 = o4;
                  int comparison = 0;
                  int c1, c2;
                  for(int i = 0; i < s1.length() && i < s2.length(); i++) {
                    c1 = (int) s1.toLowerCase().charAt(i);   
                    c2 = (int) s2.toLowerCase().charAt(i);   
        comparison = c1 - c2;   

        if(comparison != 0)    
            return comparison;
    }
    if(s1.length() > s2.length())    
        return 1;
    else if (s1.length() < s2.length())
        return -1;
    else
        return 0;
}

                 
                 
             });

        
    }
    

   
/**
 * Create tree from list of urls
 *@return Site map as tree hierarchy
 */
    public Tree<String> createTree() {
        
        if(preSortList.size() == 0) {
            System.out.println("No urls in graph");
            return null;
        }
    Node<String> root = new Node(preSortList.remove(0));
    Tree<String> graphTree = new Tree<>(root);
    List<Node<String>> addedList = new ArrayList<>();
    
    for(String url : preSortList){
        Node<String> child = new Node(url);
        root.addChild(child);
        addedList.add(child);
    }
    if(preSortList.size() < 2){
        return graphTree;
    }
    addNodesRecursive(root,addedList,1);
        return graphTree;
}
    
   

/**
 * Recursive function, add's child nodes to respective parent and builds tree
 * @param node - node url
 * @param addedList List of child urls of root
 * @param i - counter
 */
private static void addNodesRecursive(Node<String> node, List<Node<String>> addedList,int i) {
    
    //Base case
    if(i != addedList.size()){

        //Possibly fix this?
        if(i == 1){
            node = addedList.get(i);
        }

        /*
         *Naive implementation - O(n^2)
         *Search through list of all urls for child node containing parent.
         */
            Node<String> child = addedList.get(i);
            for(Node<String> url2 : addedList) {
                
            if(url2.getData().contains(node.getData()) && !url2.getData().equalsIgnoreCase(node.getData())){
               // System.out.println(url2.getData() +  "is contained in: " +
               // node.getData());
            node.addChild(url2);

            //Remove duplicates in tree
            node.getParent().removeChild(url2);
            }
            }
            
            //Recursive case
            addNodesRecursive(child,addedList,i+1);
    }  
}

/**
 * Display Breadth First Traversal of n-ary tree
 * @param root - Tree root node
 */
public void displayBFS(Node<String> root) {
    int curlevel = 1;
    int nextlevel = 0;

    LinkedList<Node<String>> queue = new LinkedList<Node<String>>();
    queue.add(root);

    while(!queue.isEmpty()) { 
        Node<String> node = queue.remove(0);

        if (curlevel == 0) {
            //System.out.println();
            curlevel = nextlevel;
            nextlevel = 0;
        }

        for(Node<String> n : node.getChildren()) {
            queue.addLast(n);
            nextlevel++;
        }

        curlevel--;
       // System.out.println(node.getData() + " ");
    } 
}
}

