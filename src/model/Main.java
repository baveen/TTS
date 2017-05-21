package model;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.sampled.AudioInputStream;
import javax.swing.JFrame;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpException;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import marytts.LocalMaryInterface;
import marytts.MaryInterface;
import marytts.client.RemoteMaryInterface;
import marytts.datatypes.MaryData;
import marytts.datatypes.MaryDataType;
import marytts.datatypes.MaryXML;
import marytts.exceptions.MaryConfigurationException;
import marytts.exceptions.SynthesisException;
import marytts.modules.synthesis.Voice;
import marytts.util.data.audio.AudioPlayer;
import marytts.util.data.audio.MaryAudioUtils;


public class Main {
	
	private static MaryInterface	marytts;
	public void setupTTS(){
		try {
			//marytts = new LocalMaryInterface();
			marytts = new RemoteMaryInterface("localhost", 59125);
			//System.setProperty("mary.base","C:/MaryTTS/marytts/lib/voices");

		} catch (IOException ex) {
			Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
		}
	}


	public static void main(String[] args) throws SynthesisException {
	
		Main main = new Main();
		main.setupTTS();
		Voice.getAvailableVoices().stream().forEach(System.out::println);

		// Setting the Voice
		marytts.setVoice("dfki-prudence");

		
		String targetFileStr="";
		try {
			FileInputStream fisTargetFile = new FileInputStream(new File("story.txt"));

			targetFileStr = IOUtils.toString(fisTargetFile, "UTF-8");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// new code
		
		marytts.setInputType("TEXT");
		marytts.setOutputType("ACOUSTPARAMS");
		
		Document targetfeatures = marytts.generateXML(targetFileStr);
		
		List<CoreMap> splittedSentenceList = main.getSentenceList(targetFileStr);
		LinkedHashMap<String, String> sentenceMap = main.addSentencesToHashMap(splittedSentenceList);
		
		AcousticParser myParser = new AcousticParser(sentenceMap);
		
		myParser.parseAcousticFeatures(targetfeatures);
		try {
			main.printDocument(targetfeatures, System.out);
		} catch (Exception e) {
			// TODO: handle exception
		}

		
		marytts.setInputType("ACOUSTPARAMS");
		marytts.setOutputType("AUDIO");
		AudioInputStream audio = marytts.generateAudio(targetfeatures);
		try {
			MaryAudioUtils.writeWavFile(MaryAudioUtils.getSamplesAsDoubleArray(audio), "thisIsMyText.wav", audio.getFormat());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


//		ArrayList<Document> xmlFileList =	 main.generateMaryXMLForEachSentence();
//		
//		for(Document tgr: xmlFileList){
//			try {
//				main.printDocument(tgr, System.out);
//			} catch (Exception e) {
//				// TODO: handle exception
//			}
//		}
		
   
	}

	public  void printDocument(Document doc, OutputStream out) throws IOException, TransformerException {
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

		transformer.transform(new DOMSource(doc), 
				new StreamResult(new OutputStreamWriter(out, "UTF-8")));
	}	


public  void playAudio(Document doc){
	
	marytts.setInputType("RAWMARYXML");
	
	try  {
	
		AudioInputStream audio = marytts.generateAudio(doc);
		AudioPlayer ttsaudio = new AudioPlayer(audio);
		ttsaudio.start();
		ttsaudio.join();

	} catch (SynthesisException ex) {
		//			Logger.getLogger(getClass().getName()).log(Level.WARNING, "Error saying phrase.", ex);
	}catch(InterruptedException e){

	}
}

public List<CoreMap> getSentenceList(String sentenceString){
	
	Properties props = new Properties();
	props.setProperty("annotators", "tokenize, ssplit, pos, lemma, depparse, natlog, openie");
	StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

	// read some text in the text variable
	String text = sentenceString; // Add your text here!

	// create an empty Annotation just with the given text
	Annotation document = new Annotation(text);

	// run all Annotators on this text
	pipeline.annotate(document);

	// these are all the sentences in this document
	// a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
	List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
	
	for(CoreMap sentence: sentences) {
		System.out.println(sentence);
		
	}
	return sentences;
	}

public LinkedHashMap<String, String> addSentencesToHashMap(List<CoreMap> sentences){
	LinkedHashMap<String, String> sentenceHashMap = new LinkedHashMap<String, String>();
	for (int i = 0; i < sentences.size(); i++) {
		CoreMap sentence = sentences.get(i);
		
		switch(i){
		case 0:
			sentenceHashMap.put(String.valueOf(i)+"sad", sentence.toString());
			break;
		case 1:
			sentenceHashMap.put(String.valueOf(i)+"sad", sentence.toString());
			break;
		case 2:
			sentenceHashMap.put(String.valueOf(i)+"sad", sentence.toString());
			break;
		case 3:
			sentenceHashMap.put(String.valueOf(i)+"joy", sentence.toString());
			break;
		case 4:
			sentenceHashMap.put(String.valueOf(i)+"joy", sentence.toString());
			break;
		case 5:
			sentenceHashMap.put(String.valueOf(i), sentence.toString());
			break;
		case 6:
			sentenceHashMap.put(String.valueOf(i)+"joy", sentence.toString());
			break;
		case 7:
			sentenceHashMap.put(String.valueOf(i)+"fear", sentence.toString());
			break;
		case 8:
			sentenceHashMap.put(String.valueOf(i)+"anger", sentence.toString());
			break;
		case 9:
			sentenceHashMap.put(String.valueOf(i)+"fear", sentence.toString());
			break;
		case 10:
				sentenceHashMap.put(String.valueOf(i)+"fear", sentence.toString());
				break;
		default:
			break;
			
		}
	} 
	return sentenceHashMap;
}


public ArrayList<Document> generateMaryXMLForEachSentence(){
	
	StoryParser storyparser = new StoryParser();
	LinkedHashMap<String, String> EmosentenceHashMap = storyparser.getEmotionWithSentence();
	marytts.setInputType("TEXT");
	marytts.setOutputType("RAWMARYXML");
	ArrayList<Document> sentenceXMLFileArray = new ArrayList<Document>();
	Document targetfeatures = null;
	for (Map.Entry<String, String> entry : EmosentenceHashMap.entrySet()) {
	    String key = entry.getKey();
	    String value = entry.getValue();
	    Document document = MaryXML.newDocument();
		Element maryxml = document.getDocumentElement();
		maryxml.setAttribute("xml:lang", "en-GB");
		Element prosody = MaryXML.appendChildElement(maryxml, MaryXML.PROSODY);
		prosody.setTextContent(value);
		MaryData maryData = new MaryData(MaryDataType.PHONEMES, Locale.ENGLISH, false);
		maryData.setDocument(document);
	    try {
			targetfeatures = marytts.generateXML(value);
			sentenceXMLFileArray.add(targetfeatures);
		} catch (SynthesisException e) {
			
			e.printStackTrace();
		}
	}
	try {
		
	} catch (Exception e) {
		// TODO: handle exception
	}
	
	
	
	
	return sentenceXMLFileArray;
}
}
		



