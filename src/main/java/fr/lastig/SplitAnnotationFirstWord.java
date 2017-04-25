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
  
  private final static String NOM_ANNOTATION      = "location";
  private final static String NOM_ATT_SOURCE      = "1-source";
  private final static String NOM_ATT_ENTITY_TYPE = "2-entityType";
  private final static String NOM_ATT_THEME       = "3-theme";
  private final static String NOM_ATT_VAGUE       = "41-vague";
  private final static String NOM_ATT_SENTIMENT   = "42-sentiment";

  
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
          
          if (type.equals(NOM_ANNOTATION) && ann.getFeatures().get(NOM_ATT_ENTITY_TYPE).equals("loc")) {
            
            String[] parts = txt.split(" ");
            if (parts.length > 1) {
              String finAnnotation = "";
              for (int i = 1; i < parts.length; i++) {
                finAnnotation = finAnnotation + " " + parts[i];
              }
              // System.out.println(txt + "      -->" + parts[0] + "    --" + finAnnotation + "--");
              
              // On crée une nouvelle annotation pour la première partie
              FeatureMap dfeat = Factory.newFeatureMap(); // ann.getFeatures();
              dfeat.put(NOM_ATT_SOURCE, ann.getFeatures().get(NOM_ATT_SOURCE));
              dfeat.put(NOM_ATT_ENTITY_TYPE, "dloc");
              dfeat.put(NOM_ATT_THEME, ann.getFeatures().get(NOM_ATT_THEME));
              dfeat.put(NOM_ATT_VAGUE, ann.getFeatures().get(NOM_ATT_VAGUE));
              dfeat.put(NOM_ATT_SENTIMENT, ann.getFeatures().get(NOM_ATT_SENTIMENT));
              newdoc.getAnnotations().add(start, start + parts[0].length(), NOM_ANNOTATION, dfeat);
              
              // On crée une nouvelle annotation pour la deuxième partie
              FeatureMap efeat = Factory.newFeatureMap(); // ann.getFeatures();
              efeat.put(NOM_ATT_SOURCE, ann.getFeatures().get(NOM_ATT_SOURCE));
              efeat.put(NOM_ATT_ENTITY_TYPE, "loc");
              efeat.put(NOM_ATT_THEME, ann.getFeatures().get(NOM_ATT_THEME));
              efeat.put(NOM_ATT_VAGUE, ann.getFeatures().get(NOM_ATT_VAGUE));
              efeat.put(NOM_ATT_SENTIMENT, ann.getFeatures().get(NOM_ATT_SENTIMENT));
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
