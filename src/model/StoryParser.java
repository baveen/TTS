package model;

import java.io.File;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


import marytts.util.dom.DomUtils;

public class StoryParser {

//	private Document outputXML;
//public StoryParser(Document outputXML ) {
//	this.outputXML = outputXML;
//}

public LinkedHashMap<String, String> getEmotionWithSentence(){
	LinkedHashMap<String, String> sentenceHashMap = new LinkedHashMap<String, String>();
	File xmlFile = new File("C:/Mary/New folder/TTS/EmotionOut.xml");
	
	Document document = null;
	//String xmlfile  = XmlToString(doc);
	
	try {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		 document = DomUtils.parseDocument(xmlFile);
		document.getDocumentElement().normalize();
		System.out.println("Root element :" 
	            + document.getDocumentElement().getNodeName());
		 NodeList nList = document.getElementsByTagName("sentence");
         System.out.println("----------------------------"); 
         for (int temp = 0; temp < nList.getLength(); temp++) {
        	 
            Node nNode = nList.item(temp);
            Element eElement = (Element) nNode;
//            System.out.println("\nSentence:"+(temp+1)
//               + nNode.getNodeName());
            String key = temp+"_"+eElement.getAttribute("emotion");
            String sentence = eElement.getTextContent();
//            System.out.println("\n"+sentence);
            sentenceHashMap.put(key, sentence);
         }
	} catch (Exception e) {
		// TODO: handle exception
	} 
	return sentenceHashMap;
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
