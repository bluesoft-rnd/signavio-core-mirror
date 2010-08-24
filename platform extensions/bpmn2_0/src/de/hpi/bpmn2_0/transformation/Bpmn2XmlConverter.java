package de.hpi.bpmn2_0.transformation;

import java.io.File;
import java.io.StringWriter;
import java.util.Arrays;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.ValidationEvent;
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
//		final AnnotationReader<Type, Class, Field, Method> annotationReader = new AnnoxAnnotationReader();
//
//		final Map<String, Object> properties = new HashMap<String, Object>();
//
//		properties.put(JAXBRIContext.ANNOTATION_READER, annotationReader);
//
//		final JAXBContext context = JAXBContext.newInstance(
//			"de.hpi.bpmn2_0.model",
//			Thread.currentThread().getContextClassLoader(),
//			properties);
		
		/* Perform XML creation */
		JAXBContext context = JAXBContext.newInstance(Definitions.class);
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		
		NamespacePrefixMapper nsp = new BPMNPrefixMapper();
		marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", nsp);
		
		/* Set Schema validation properties */
//		SchemaFactory sf = SchemaFactory
//				.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
//
//		Schema schema = sf.newSchema(new File(bpmn20XsdPath));
//		marshaller.setSchema(schema);
//		
//		ExportValidationEventCollector vec = new ExportValidationEventCollector();
//		marshaller.setEventHandler(vec);
		
		StringWriter writer = new StringWriter();
		
		/* Marshal BPMN 2.0 XML */
		marshaller.marshal(bpmnDefinitions, writer);
		
		return writer;
	}
	
	public StringBuilder getValidationResults() throws JAXBException, SAXException {
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
		
		/* Retrieve validation results */
		ValidationEvent[] events = vec.getEvents();
		
		StringBuilder builder = new StringBuilder();
		builder.append("Validation Errors: \n\n");
		
		for(ValidationEvent event : Arrays.asList(events)) {
			
//			builder.append("Line: ");
//			builder.append(event.getLocator().getLineNumber());
//			builder.append(" Column: ");
//			builder.append(event.getLocator().getColumnNumber());
			
			builder.append("\nError: ");
			builder.append(event.getMessage());
			builder.append("\n\n\n");
		}
		
		return builder;
	}
}
