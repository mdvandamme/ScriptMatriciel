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
public class ChangementSchema {
	
	private static final String OLD_ELEMENT_NAME_NATURE = "1-nature";
	
	private static final String NEW_ELEMENT_NAME_SOURCE = "1-source";
	private static final String NEW_ELEMENT_NAME_NATURE = "2-entityType";

	/**
	 * 
	 * 
	 */
	public static void parseSchemaV1ToV2(String filename) {
		
		int cptLocation = 0;
		int cptNull = 0;
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
          
			Document doc = Factory.newDocument(new URL("File", "", "./corpus/matriciel/espagne/original/" + filename));
			System.out.println("Nombre total d'annotations pour le document en cours : " + doc.getAnnotations().size());
			
			Iterator<Annotation> it = doc.getAnnotations().iterator();
			while (it.hasNext()) {
			
			// Récupère les noms des villes pour les pays
			// for (int i = 0; i < doc.getAnnotations().size(); i++) {
				// Annotation a = doc.getAnnotations().get(i);
				Annotation ann = it.next();
				
				if (ann != null) {
					// System.out.println(a.getId() + " - " + gate.Utils.stringFor(doc, a) + " (type : " + a.getType() + ")");
					if (ann.getType().equals("location")) {
						cptLocation++;
						if (ann.getType().equals("location")) {
							FeatureMap feat = ann.getFeatures();
							
							String nature = feat.get("1-nature").toString();
							String geom = feat.get("2-geometry").toString();
							String type = feat.get("3-type").toString();
							String vague = feat.get("4-vague").toString();
							
							// On supprime 
							feat.remove("1-nature");
							feat.remove("2-geometry");
							feat.remove("3-type");
							feat.remove("4-vague");
							
							// On ajoute
							feat.put("1-source", "BDNymeWGS84");
							feat.put("2-entityType", nature);
							feat.put("3-theme", type);
							feat.put("41-vague", vague);
							feat.put("42-sentiment", "");
							feat.put("5-id", "");
							feat.put("51-category", "");
							feat.put("52-subCategory", "");
							feat.put("61-continent", "");
							feat.put("62-country", "");
							feat.put("63-departement", "");
							feat.put("64-x", "");
							feat.put("65-y", "");
							feat.put("66-geometry", geom);
							
							// System.out.println("    source = " + feat.get(NEW_ELEMENT_NAME_SOURCE));
							
							// 
						}
					} else if (ann.getType().equals("disfluence")) {
					
					} else {
						// System.out.println(a.getType());
					}
				} else {
					cptNull++;
				}
			}
		
		
			File outputFile = new File("./corpus/matriciel/espagne/test-" + filename); // based on doc name, sequential number, etc.
			DocumentStaxUtils.writeDocument(doc, outputFile);
		
			// gate.Utils.stringFor(document, annotation)
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("nb d'annotation location : " + cptLocation);
		System.out.println("nb d'annotation nulle : " + cptNull);
		

	}
	
	public static void main(String[] args) {
		// parseSchemaV1ToV2("MA01_B-v2.xml");
		parseSchemaV1ToV2("PP13_B.xml");
		
		
		
	}

}
