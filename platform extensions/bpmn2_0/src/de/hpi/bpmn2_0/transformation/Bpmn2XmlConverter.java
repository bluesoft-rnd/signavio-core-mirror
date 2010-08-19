package de.hpi.bpmn2_0.transformation;

import java.io.File;
import java.io.StringWriter;
import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;

import de.hpi.bpmn2_0.ExportValidationEventCollector;
import de.hpi.bpmn2_0.model.Definitions;

public class Bpmn2XmlConverter {

	private Definitions bpmnDefinitions;
	private String bpmn20XsdPath;
	
	public Bpmn2XmlConverter(Definitions bpmnDefinitions, String bpmn20XsdPath) {
		this.bpmnDefinitions = bpmnDefinitions;
		this.bpmn20XsdPath = bpmn20XsdPath;
	}
	
	public StringWriter getXml() throws JAXBException, SAXException {
		/* Perform XML creation */
		JAXBContext context = JAXBContext.newInstance(Definitions.class);
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		
		NamespacePrefixMapper nsp = new BPMNPrefixMapper();
		marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", nsp);
		
		/* Set Schema validation properties */
		SchemaFactory sf = SchemaFactory
				.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);

		Schema schema = sf.newSchema(new File(bpmn20XsdPath));
		marshaller.setSchema(schema);
		
		ExportValidationEventCollector vec = new ExportValidationEventCollector();
		marshaller.setEventHandler(vec);
		
		StringWriter writer = new StringWriter();
		
		/* Marshal BPMN 2.0 XML */
		marshaller.marshal(bpmnDefinitions, writer);
		
		return writer;
	}
}
