package de.hpi.bpmn2_0.factory;

/**
 * Copyright (c) 2009
 * Philipp Giese, Sven Wagner-Boysen
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.oryxeditor.server.diagram.Shape;

import de.hpi.bpmn2_0.annotations.Property;
import de.hpi.bpmn2_0.annotations.StencilId;
import de.hpi.bpmn2_0.exceptions.BpmnConverterException;
import de.hpi.bpmn2_0.factory.configuration.Configuration;
import de.hpi.bpmn2_0.factory.edge.AssociationFactory;
import de.hpi.bpmn2_0.factory.edge.ConversationLinkFactory;
import de.hpi.bpmn2_0.factory.edge.MessageFlowFactory;
import de.hpi.bpmn2_0.factory.edge.SequenceFlowFactory;
import de.hpi.bpmn2_0.factory.node.ChoreographyActivityFactory;
import de.hpi.bpmn2_0.factory.node.ChoreographyParticipantFactory;
import de.hpi.bpmn2_0.factory.node.ConversationFactory;
import de.hpi.bpmn2_0.factory.node.ConversationParticipantFactory;
import de.hpi.bpmn2_0.factory.node.DataObjectFactory;
import de.hpi.bpmn2_0.factory.node.DataStoreFactory;
import de.hpi.bpmn2_0.factory.node.EndEventFactory;
import de.hpi.bpmn2_0.factory.node.GatewayFactory;
import de.hpi.bpmn2_0.factory.node.GroupFactory;
import de.hpi.bpmn2_0.factory.node.ITSystemFactory;
import de.hpi.bpmn2_0.factory.node.IntermediateCatchEventFactory;
import de.hpi.bpmn2_0.factory.node.IntermediateThrowEventFactory;
import de.hpi.bpmn2_0.factory.node.LaneFactory;
import de.hpi.bpmn2_0.factory.node.MessageFactory;
import de.hpi.bpmn2_0.factory.node.ParticipantFactory;
import de.hpi.bpmn2_0.factory.node.ProcessParticipantFactory;
import de.hpi.bpmn2_0.factory.node.StartEventFactory;
import de.hpi.bpmn2_0.factory.node.SubprocessFactory;
import de.hpi.bpmn2_0.factory.node.TaskFactory;
import de.hpi.bpmn2_0.factory.node.TextannotationFactory;
import de.hpi.bpmn2_0.model.BaseElement;
import de.hpi.bpmn2_0.model.Documentation;
import de.hpi.bpmn2_0.model.FlowElement;
import de.hpi.bpmn2_0.model.bpmndi.di.DiagramElement;
import de.hpi.bpmn2_0.model.extension.ExtensionElements;
import de.hpi.bpmn2_0.model.extension.signavio.SignavioLabel;
import de.hpi.bpmn2_0.model.extension.signavio.SignavioMetaData;
import de.hpi.bpmn2_0.model.misc.Auditing;
import de.hpi.bpmn2_0.model.misc.Monitoring;

/**
 * This is the abstract factory that offers methods to create a process element
 * and a related diagram element from a {@link Shape}.
 */
public abstract class AbstractBpmnFactory {

	private static List<Class<? extends AbstractBpmnFactory>> factoryClasses = new ArrayList<Class<? extends AbstractBpmnFactory>>();
	
	/**
	 * Manual initialization of factory classes list. Is there a pattern for automatic initialization
	 * except reading the jar file?
	 */
	static {
		
		/* Standard BPMN 2.0 */
		
		factoryClasses.add(AbstractActivityFactory.class);
		factoryClasses.add(SubprocessFactory.class);
		factoryClasses.add(TaskFactory.class);
		factoryClasses.add(AbstractEdgesFactory.class);
		factoryClasses.add(ConversationLinkFactory.class);
		factoryClasses.add(MessageFlowFactory.class);
		factoryClasses.add(SequenceFlowFactory.class);
		factoryClasses.add(AssociationFactory.class);
		factoryClasses.add(ChoreographyActivityFactory.class);
		factoryClasses.add(ChoreographyParticipantFactory.class);
		factoryClasses.add(ConversationFactory.class);
		factoryClasses.add(ConversationParticipantFactory.class);
		factoryClasses.add(DataObjectFactory.class);
		factoryClasses.add(DataStoreFactory.class);
		factoryClasses.add(EndEventFactory.class);
		factoryClasses.add(GatewayFactory.class);
		factoryClasses.add(GroupFactory.class);
		factoryClasses.add(IntermediateCatchEventFactory.class);
		factoryClasses.add(IntermediateThrowEventFactory.class);
		factoryClasses.add(ITSystemFactory.class);
		factoryClasses.add(LaneFactory.class);
		factoryClasses.add(MessageFactory.class);
		factoryClasses.add(ParticipantFactory.class);
		factoryClasses.add(ProcessParticipantFactory.class);
		factoryClasses.add(StartEventFactory.class);
		factoryClasses.add(TextannotationFactory.class);
	}
	
	public static List<Class<? extends AbstractBpmnFactory>> getFactoryClasses() {
		return new ArrayList<Class<? extends AbstractBpmnFactory>>(factoryClasses);
	}
	
	/**
	 * Creates a process element based on a {@link Shape}.
	 * 
	 * @param shape
	 *            The resource shape
	 * @return The constructed process element.
	 */
	protected abstract BaseElement createProcessElement(Shape shape)
			throws BpmnConverterException;

	/**
	 * Creates a diagram element based on a {@link Shape}.
	 * 
	 * @param shape
	 *            The resource shape
	 * @return The constructed diagram element.
	 */
	protected abstract DiagramElement createDiagramElement(Shape shape);

	/**
	 * Creates BPMNElement that contains DiagramElement and ProcessElement
	 * 
	 * @param shape
	 *            The resource shape.
	 * @return The constructed BPMN element.
	 */
	public abstract BPMNElement createBpmnElement(Shape shape,
			BPMNElement parent) throws BpmnConverterException;

	/**
	 * Sets attributes of a {@link BaseElement} that are common for all
	 * elements.
	 * 
	 * @param element
	 *            The BPMN 2.0 element
	 * @param shape
	 *            The resource shape
	 */
	protected void setCommonAttributes(BaseElement element, Shape shape) {
		element.setId(shape.getResourceId());
		
		/* Documentation */
		String documentation = shape.getProperty("documentation");
		if (documentation != null && !(documentation.length() == 0) && element.getDocumentation().size() == 0)
			element.getDocumentation().add(new Documentation(documentation));
		
		/* Common FlowElement attributes */
		if(element instanceof FlowElement) {
			
			/* Auditing */
			String auditing = shape.getProperty("auditing");
			if (auditing != null && !(auditing.length() == 0))
				((FlowElement) element).setAuditing(new Auditing(auditing));
			
			/* Monitoring */
			String monitoring = shape.getProperty("monitoring");
			if (monitoring != null && !(monitoring.length() == 0))
				((FlowElement) element).setMonitoring(new Monitoring(monitoring));
			
			/* Name */
			String name = shape.getProperty("name");
			if(name != null && !(name.length() == 0)) {
				((FlowElement) element).setName(name);
			}
		}
	}

	/**
	 * Sets common fields for the visual representation.
	 * 
	 * @param diaElement
	 *            The BPMN 2.0 diagram element
	 * @param shape
	 *            The resource shape
	 */
	protected void setVisualAttributes(DiagramElement diaElement, Shape shape) {
		diaElement.setId(shape.getResourceId() + "_gui");
	}

	protected BaseElement invokeCreatorMethod(Shape shape)
			throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, BpmnConverterException {

		/* Retrieve the method to create the process element */
		for (Method method : Arrays
				.asList(this.getClass().getMethods())) {
			StencilId stencilIdA = method.getAnnotation(StencilId.class);
			if (stencilIdA != null
					&& Arrays.asList(stencilIdA.value()).contains(
							shape.getStencilId())) {
				/* Create element with appropriate method */
				BaseElement createdElement = (BaseElement) method.invoke(this,
						shape);
				/* Invoke generalized method to set common element attributes */
				this.setCommonAttributes(createdElement, shape);
				
				return createdElement;
			}
		}

		throw new BpmnConverterException("Creator method for shape with id "
				+ shape.getStencilId() + " not found");
	}

	protected BaseElement invokeCreatorMethodAfterProperty(Shape shape)
			throws BpmnConverterException, IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {

		for (Method method : Arrays
				.asList(this.getClass().getMethods())) {
			Property property = method.getAnnotation(Property.class);

			if (property != null
					&& Arrays.asList(property.value()).contains(
							shape.getProperty(property.name()))) {
				
				/* Create element */
				BaseElement createdElement = (BaseElement) method.invoke(this,
						shape);
				/* Invoke generalized method to set common element attributes */
				this.setCommonAttributes(createdElement, shape);
				
				return createdElement;
			}
		}

		throw new BpmnConverterException("Creator method for shape with id "
				+ shape.getStencilId() + " not found");
	}
	
	
	public BPMNElement createBpmnElement(Shape shape, Configuration configuration) throws BpmnConverterException {
		BPMNElement bpmnElement = createBpmnElement(shape, new BPMNElement(null, null, null));
		bpmnElement.getNode()._diagramElement = bpmnElement.getShape();
		
		setCustomAttributes(shape, bpmnElement.getNode(), configuration.getMetaData());
		
		return bpmnElement;
	}
	
	private void setCustomAttributes(Shape shape, BaseElement node, Map<String, Set<String>> metaData) {
		if(shape == null || node == null || metaData == null) 
			return;
		
		Set<String> attributeNames = metaData.get(shape.getStencilId());
		if(attributeNames == null) {
			return;
		}
		
		ExtensionElements extElements = node.getOrCreateExtensionElements();
		
		Iterator<String> iterator = attributeNames.iterator();
		while(iterator.hasNext()) {
			String attributeKey = iterator.next();
			String attributeValue = shape.getProperty(attributeKey);
			
			SignavioMetaData sigMetaData = new SignavioMetaData(attributeKey, attributeValue);
			
			extElements.getAny().add(sigMetaData);
		}
	}
	
	protected void setLabelPositionInfo(Shape shape, BaseElement node) {
		if(shape == null || node == null || shape.getLabels().isEmpty()) {
			return;
		}
		
		ExtensionElements extElements = node.getOrCreateExtensionElements();
		
		for(Map<String, String> labelPosition : shape.getLabels()) {
			SignavioLabel label = new SignavioLabel(labelPosition);
			extElements.getAny().add(label);
		}
	}
	
}
