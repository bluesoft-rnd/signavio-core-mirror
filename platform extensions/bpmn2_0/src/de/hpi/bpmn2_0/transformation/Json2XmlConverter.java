package de.hpi.bpmn2_0.transformation;

import java.io.StringWriter;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.json.JSONException;
import org.oryxeditor.server.diagram.Diagram;
import org.oryxeditor.server.diagram.DiagramBuilder;
import org.xml.sax.SAXException;

import de.hpi.bpmn2_0.exceptions.BpmnConverterException;

public class Json2XmlConverter {

	private String json;
	private String bpmn20XsdPath;
	
	public Json2XmlConverter(String json, String bpmn20XsdPath) {
		this.json = json;
		this.bpmn20XsdPath = bpmn20XsdPath;
	}
	
	public StringWriter getXml() throws JSONException, BpmnConverterException, JAXBException, SAXException, ParserConfigurationException, TransformerException {
		Diagram diagram = DiagramBuilder.parseJson(json);
		
		Diagram2XmlConverter converter = new Diagram2XmlConverter(diagram, bpmn20XsdPath);
		
		return converter.getXml();
	}
	
	public StringBuilder getValidationResults() throws JSONException, JAXBException, SAXException, BpmnConverterException {
		Diagram diagram = DiagramBuilder.parseJson(json);
		
		Diagram2XmlConverter converter = new Diagram2XmlConverter(diagram, bpmn20XsdPath);
		
		return converter.getValidationResults();
	}
}
