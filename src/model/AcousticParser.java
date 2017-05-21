package model;


import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;

import marytts.datatypes.MaryData;
import marytts.datatypes.MaryDataType;
import marytts.util.dom.DomUtils;

public class AcousticParser {
	private LinkedHashMap<String, String> sentencemap;
	ArrayList<String> keys;
	public AcousticParser(LinkedHashMap<String, String> sentenceHashList) {
		this.sentencemap = sentenceHashList;
		keys = (new ArrayList<String>(sentencemap.keySet()));
	}
public void parseAcousticFeatures(Document doc){
	
	Document document = null;
	String xmlfile  = XmlToString(doc);
	System.out.println("\nKey:"+keys);
	try {
		 document = DomUtils.parseDocument(xmlfile);
		document.getDocumentElement().normalize();
		System.out.println("Root element :" 
	            + doc.getDocumentElement().getNodeName());
		 NodeList nList = doc.getElementsByTagName("s");
         System.out.println("----------------------------");
         
         
         for (int temp = 0; temp < nList.getLength(); temp++) {
        	    String keysWithStrings = keys.get(temp) ;
        	    String[] splittedKeysWithStrings = keysWithStrings.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
        	    String key = "";
        	    if(splittedKeysWithStrings.length>1){
        	    	key = splittedKeysWithStrings[1];
        	    }
        	    
            Node nNode = nList.item(temp);
            System.out.println("\nSentence:"+(temp+1)
               + nNode.getNodeName());
            System.out.println("\nKey: "+key);
            
            
            NodeList childNodes = nNode.getChildNodes();
            
            for (int i = 0; i < childNodes.getLength(); i++) {
				if(childNodes.item(i).getNodeName().equals("prosody")){
					
					if (nNode.getNodeType() == Node.ELEMENT_NODE) {
						Element prosody = (Element) childNodes.item(i);
		               
		                
						System.out.println("pitch: " 
		                   + prosody.getAttribute("pitch"));
		                System.out.println("range: " 
		                   + prosody
		                   .getAttribute("range"));
//		                prosody.setAttribute("pitch", "+2%");
//		                prosody.setAttribute("range", "-10%");
//		                prosody.setAttribute("rate", "+7%");
//		                prosody.setAttribute("contour", "(0%,+0st)(100%,-0st)");
		                setProsodyFeatures(prosody, key);
		             //   setProsodyFeatures(document, prosody);
		                
		            }
				}
			}
              
            
         }
	} catch (Exception e) {
		// TODO: handle exception
	} 
	
	MaryData maryData = new MaryData(MaryDataType.PHONEMES, Locale.ENGLISH, false);
	maryData.setDocument(document);
}

public static void setProsodyFeatures(Element prosody, String key){

    	switch(key){
    	case "sad":{
    		
    		prosody.setAttribute("pitch", "-4%");
            prosody.setAttribute("rate", "-30%");
            prosody.setAttribute("contour", "(0%,+0st)(100%,-0st)");
    	//    prosody.setAttribute("volume", "low");
    		
    	}
    	break;
    	case "fear":
    	{
    	prosody.setAttribute("pitch", "+6%");
      //  prosody.setAttribute("range", "-10%");
        prosody.setAttribute("rate", "+7%");
      //  prosody.setAttribute("contour", "(0%,+2st)(50%,+5st)(75%,+8st)(100%,+5st)");
      //  prosody.setAttribute("volume", "high");

    	}
    	break;
    	case "surprise":
    	{
        prosody.setAttribute("pitch", "+2%");
        //prosody.setAttribute("range", "-10%");
        prosody.setAttribute("rate", "+8%");
      //  prosody.setAttribute("contour", "(0%,+0st)(100%,-0st)");
     //   prosody.setAttribute("volume", "high");

    	}
    	break;
    	case "joy":
    	{
        prosody.setAttribute("pitch", "+2%");
     // prosody.setAttribute("range", "-10%");
        prosody.setAttribute("rate", "+7%");
        prosody.setAttribute("contour", "0%,+8st)(30%,+16st)(50%,+14st)(100%,+11st)");
      //  prosody.setAttribute("volume", "high");

    	}
    	break;
    	case "love":
    	{
        prosody.setAttribute("pitch", "+2%");
        prosody.setAttribute("range", "-10%");
        prosody.setAttribute("rate", "+7%");
       // prosody.setAttribute("contour", "(0%,+0st)(100%,-0st)");
   //     prosody.setAttribute("volume", "low");

    	}
    	break;
    	case "anger":
    	{
        prosody.setAttribute("pitch", "+30%");
        prosody.setAttribute("range", "+20%");
        prosody.setAttribute("rate", "+15%");
      //  prosody.setAttribute("contour", "(0%,-18st)(50%,-14st)(75%,-10st)(100%,-14st)");
     //   prosody.setAttribute("volume", "high");
        
    	}
    	break;
    	default:
    	{
        
    		prosody.setAttribute("range", "+2%");
    	}
    	
    	}
        	 

}

public static String XmlToString(Document doc) {
    try {
        StringWriter sw = new StringWriter();
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

        transformer.transform(new DOMSource(doc), new StreamResult(sw));
        return sw.toString();
    } catch (Exception ex) {
        throw new RuntimeException("Error converting to String", ex);
    }


}

}