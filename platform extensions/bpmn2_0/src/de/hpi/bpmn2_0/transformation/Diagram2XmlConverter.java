package de.hpi.bpmn2_0.transformation;

import java.io.StringWriter;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.oryxeditor.server.diagram.Diagram;
import org.xml.sax.SAXException;

import de.hpi.bpmn2_0.exceptions.BpmnConverterException;
import de.hpi.bpmn2_0.factory.AbstractBpmnFactory;
import de.hpi.bpmn2_0.model.Definitions;

public class Diagram2XmlConverter {

	private Diagram diagram;
	String bpmn20XsdPath;
	
	public Diagram2XmlConverter(Diagram diagram, String bpmn20XsdPath) {
		this.diagram = diagram;
		this.bpmn20XsdPath = bpmn20XsdPath;
	}
	
	public StringWriter getXml() throws BpmnConverterException, JAXBException, SAXException, ParserConfigurationException, TransformerException {
		
		/* Build up BPMN 2.0 model */
		Diagram2BpmnConverter converter = new Diagram2BpmnConverter(diagram, AbstractBpmnFactory.getFactoryClasses());
		Definitions bpmnDefinitions = converter.getDefinitionsFromDiagram();
		
		/* Get BPMN 2.0 XML */
		Bpmn2XmlConverter xmlConverter = new Bpmn2XmlConverter(bpmnDefinitions, bpmn20XsdPath);
		return xmlConverter.getXml();
	}
	
	public StringBuilder getValidationResults() throws JAXBException, SAXException, BpmnConverterException {
		/* Build up BPMN 2.0 model */
		Diagram2BpmnConverter converter = new Diagram2BpmnConverter(diagram, AbstractBpmnFactory.getFactoryClasses());
		Definitions bpmnDefinitions = converter.getDefinitionsFromDiagram();
		
		/* Get BPMN 2.0 XML */
		Bpmn2XmlConverter xmlConverter = new Bpmn2XmlConverter(bpmnDefinitions, bpmn20XsdPath);
		return xmlConverter.getValidationResults();
	}
}
