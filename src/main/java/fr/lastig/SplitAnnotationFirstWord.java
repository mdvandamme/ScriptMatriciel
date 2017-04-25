package fr.lastig;

import java.io.File;
import java.net.URL;
import java.util.Iterator;

import gate.Annotation;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.Gate;
import gate.corpora.DocumentStaxUtils;

/**
 * 
 * 
 *
 */
public class SplitAnnotationFirstWord {
  
  private final static String NOM_ANNOTATION = "location";
  private final static String NOM_ENTITY_TYPE = "2-entityType";

  
  public static void splitFile(String filename) {
    try {
      
      // Gate.init(); // prepare the library
      Gate.setNetConnected(false);
      Gate.setPluginsHome(new File("plugins/"));
      Gate.setSiteConfigFile(new File("gate.xml"));
      Gate.setUserConfigFile(new File("user-gate.xml"));
      if (Gate.getGateHome() == null) {
        Gate.init();
      } else {
        return;
      }
      
      Document doc = Factory.newDocument(new URL("File", "", "./corpus/original/" + filename));
      System.out.println("Nombre total d'annotations pour le document en cours : " + doc.getAnnotations().size());
      
      // Nouveau document
      FeatureMap params = Factory.newFeatureMap();
      params.put(Document.DOCUMENT_STRING_CONTENT_PARAMETER_NAME, doc.getContent().toString());
      Document newdoc = (Document) Factory.createResource("gate.corpora.DocumentImpl", params);
      
      Iterator<Annotation> it = doc.getAnnotations().iterator();
      System.out.println("");
      while (it.hasNext()) {
        
        Annotation ann = it.next();
        
        if (ann != null) {
          
          String txt = gate.Utils.stringFor(doc, ann);
          String type = ann.getType();
          Long start = ann.getStartNode().getOffset();
          Long end = ann.getEndNode().getOffset();
          
          if (type.equals(NOM_ANNOTATION) && ann.getFeatures().get(NOM_ENTITY_TYPE).equals("loc")) {
            
            String[] parts = txt.split(" ");
            if (parts.length > 1) {
              String finAnnotation = "";
              for (int i = 1; i < parts.length; i++) {
                finAnnotation = finAnnotation + " " + parts[i];
              }
              // System.out.println(txt + "      -->" + parts[0] + "    --" + finAnnotation + "--");
              
              // On crée une nouvelle annotation qui commence
              FeatureMap dfeat = ann.getFeatures();
              dfeat.remove(NOM_ENTITY_TYPE);
              dfeat.put(NOM_ENTITY_TYPE, "dloc");
              newdoc.getAnnotations().add(start, start + parts[0].length(), NOM_ANNOTATION, dfeat);
              
              // On crée une nouvelle annotation pour le premier mot
              FeatureMap efeat = ann.getFeatures();
              newdoc.getAnnotations().add(start + parts[0].length(), end, NOM_ANNOTATION, efeat);
              
            }
            // sinon rien à faire
          }
        }
      }
      
      File outputFile = new File("./corpus/final/" + filename); // based on doc name, sequential number, etc.
      DocumentStaxUtils.writeDocument(newdoc, outputFile);
      
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  
  public static void main(String[] args) {
    splitFile("EA02_C-sentimentPapier.xml");
  }

}
