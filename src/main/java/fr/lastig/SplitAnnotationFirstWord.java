package fr.lastig;

import java.net.URL;

import gate.Document;
import gate.Factory;
import gate.Gate;

/**
 * 
 * 
 *
 */
public class SplitAnnotationFirstWord {

  
  public static void splitFile(String filename) {
    try {
      Gate.init(); // prepare the library
      Document doc = Factory.newDocument(new URL("File", "", "./corpus/matriciel/espagne/original/" + filename));
      System.out.println("Nombre total d'annotations pour le document en cours : " + doc.getAnnotations().size());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  
  public static void main(String[] args) {
    splitFile("PP13_B.xml");
  }

}
