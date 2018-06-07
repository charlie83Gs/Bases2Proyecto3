/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Logica;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.ServerAddress;
import com.mongodb.MongoCredential;
import com.mongodb.MongoClientOptions;
import org.w3c.dom.Document;


/**
 *
 * @author charli83
 */
public class FileLoaderThread implements Runnable{
    private ArrayList<File> files;
    private String collectionName;
    //si no existe esta base de datos se debe crear
    //basta con correr la instruccion: use proyecto3
    //en la consola de mongo
    private final String dbName = "proyecto3";
    public FileLoaderThread(File pDirectory,String pCollectionName) {
        files = new ArrayList<>();
        collectionName = pCollectionName;
        for(File fileEntry: pDirectory.listFiles()){
             if(fileEntry.isFile()){
                 files.add(fileEntry);
                 System.out.println(fileEntry.getPath().toString());
             }
             }
    }
    
    
    @Override
    public void run() {
        MongoClient mongoClient = new MongoClient();
        DB dbProyecto3 = mongoClient.getDB(dbName);
        DBCollection createdCollection = dbProyecto3.getCollection(collectionName);
        
        for(File file : files){
            try {
                //lectura del xml
                DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
                        .newInstance();
                DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                //carga el archivo en un documento
                Document document = documentBuilder.parse(file);
                //busco todos los elementos con el tag REUTERS
                NodeList articles = document.getElementsByTagName("REUTERS");
            
                int totalArticles = articles.getLength();
                for(int articleNumber = 0; articleNumber < totalArticles; articleNumber++){
                    Element article = (Element)articles.item(articleNumber);
                    Node date = article.getElementsByTagName("DATE").item(0);
                    Node topics = article.getElementsByTagName("TOPICS").item(0);
                    Node places = article.getElementsByTagName("PLACES").item(0);
                    Node people = article.getElementsByTagName("PEOPLE").item(0);
                    Node orgs = article.getElementsByTagName("ORGS").item(0);
                    Node exchanges = article.getElementsByTagName("EXCHANGES").item(0);
                    //Node text = article.getElementsByTagName("TEXT").item(0);
                    Node title = article.getElementsByTagName("TITLE").item(0);
                    Node author = article.getElementsByTagName("AUTHOR").item(0);
                    Node dateline = article.getElementsByTagName("DATELINE").item(0);
                    Node body = article.getElementsByTagName("BODY").item(0);
                    
                    String newId = article.getAttributes().getNamedItem("NEWID").getNodeValue();
                    
                    ArrayList<String> allTopics = getAllNodeElements(topics);
                    ArrayList<String> allPlaces = getAllNodeElements(places);
                    ArrayList<String> allPeople = getAllNodeElements(people);
                    ArrayList<String> allExchanges = getAllNodeElements(exchanges);
                    ArrayList<String> allOrgs = getAllNodeElements(orgs);
                    
                    
                    //mongodb part
                    BasicDBObject newArticle = new BasicDBObject();
                    newArticle.append("NEWID", Integer.valueOf(newId));
                    newArticle.append("DATE", date.getTextContent());
                    newArticle.append("TOPICS", allTopics);
                    newArticle.append("PLACES", allPlaces);
                    newArticle.append("PEOPLE", allPeople);
                    newArticle.append("ORGS", allOrgs);
                    newArticle.append("EXCHANGES", allExchanges);
                    //cargamos los elementos que podrian no existir
                    if(title != null){
                    newArticle.append("TITLE", title.getTextContent());
                    }
                    if(author != null){
                        newArticle.append("AUTHOR", author.getTextContent());
                    }
                    if(dateline != null){
                        newArticle.append("DATELINE", dateline.getTextContent());
                    }
                    if(body != null){
                        newArticle.append("BODY", body.getTextContent());
                    }
                    //se inserta el nuevo documento en la colecion : )
                    createdCollection.insert(newArticle);
                    System.out.println(newArticle.toString());
                }
            
            } catch (SAXException ex) {
                Logger.getLogger(FileLoaderThread.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(FileLoaderThread.class.getName()).log(Level.SEVERE, null, ex);
            } catch(ParserConfigurationException ex){
                Logger.getLogger(FileLoaderThread.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        
    }
    
public static ArrayList<String> getAllNodeElements(Node target){
    ArrayList<String> elements = new ArrayList<>();
    Node firstElement = target.getFirstChild();
    while(firstElement != null){
        //System.out.println(firstElement.getNodeName());
        //System.out.println(firstElement.getTextContent());
        elements.add(firstElement.getTextContent());
        firstElement = firstElement.getNextSibling();
    }
    return elements;
}
    
}


