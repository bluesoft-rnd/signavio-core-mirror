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

package de.hpi.bpmn2_0.model.activity;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import org.oryxeditor.server.diagram.Shape;
import org.oryxeditor.server.diagram.StencilType;

import de.hpi.bpmn2_0.annotations.ChildElements;
import de.hpi.bpmn2_0.model.BaseElement;
import de.hpi.bpmn2_0.model.FlowElement;
import de.hpi.bpmn2_0.model.activity.type.BusinessRuleTask;
import de.hpi.bpmn2_0.model.activity.type.ManualTask;
import de.hpi.bpmn2_0.model.activity.type.ReceiveTask;
import de.hpi.bpmn2_0.model.activity.type.ScriptTask;
import de.hpi.bpmn2_0.model.activity.type.SendTask;
import de.hpi.bpmn2_0.model.activity.type.ServiceTask;
import de.hpi.bpmn2_0.model.activity.type.UserTask;
import de.hpi.bpmn2_0.model.artifacts.Artifact;
import de.hpi.bpmn2_0.model.artifacts.TextAnnotation;
import de.hpi.bpmn2_0.model.choreography.ChoreographyTask;
import de.hpi.bpmn2_0.model.connector.Association;
import de.hpi.bpmn2_0.model.data_object.DataObject;
import de.hpi.bpmn2_0.model.data_object.DataStore;
import de.hpi.bpmn2_0.model.event.CompensateEventDefinition;
import de.hpi.bpmn2_0.model.event.ConditionalEventDefinition;
import de.hpi.bpmn2_0.model.event.ErrorEventDefinition;
import de.hpi.bpmn2_0.model.event.EscalationEventDefinition;
import de.hpi.bpmn2_0.model.event.EventDefinition;
import de.hpi.bpmn2_0.model.event.MessageEventDefinition;
import de.hpi.bpmn2_0.model.event.SignalEventDefinition;
import de.hpi.bpmn2_0.model.event.StartEvent;
import de.hpi.bpmn2_0.model.event.TimerEventDefinition;
import de.hpi.bpmn2_0.model.gateway.ParallelGateway;

import de.hpi.bpmn2_0.transformation.BPMN2DiagramConverterI;

/**
 * <p>
 * Java class for tSubProcess complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="tSubProcess">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/bpmn20}tActivity">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.omg.org/bpmn20}flowElement" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.omg.org/bpmn20}artifact" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="triggeredByEvent" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tSubProcess", propOrder = { "flowElement", "artifact" })
@XmlSeeAlso( { AdHocSubProcess.class, Transaction.class })
public class SubProcess extends Activity {

	@XmlElementRef(type = FlowElement.class)
	protected List<FlowElement> flowElement;

	@XmlElementRef(type = Artifact.class)
	protected List<Artifact> artifact;

	@XmlAttribute
	protected Boolean triggeredByEvent;

	/**
	 * Adds the child to the list of {@link FlowElement} of the subprocess.
	 */
	public void addChild(BaseElement child) {
		/* Set sub process reference */
		if(child instanceof FlowElement) {
			this.setSubProcess(this);
		}
		
		/* Insert into appropriate list */
		if (child instanceof Artifact) {
			this.getArtifact().add((Artifact) child);
			((Artifact) child).setSubProcess(this);
		}

		else if (child instanceof FlowElement) {
			this.getFlowElement().add((FlowElement) child);
		}
	}

	/**
	 * Remove the child element from the sub process.
	 * 
	 * @param child
	 *            Child element to remove.
	 */
	public void removeChild(BaseElement child) {
		this.getArtifact().remove(child);

		this.getFlowElement().remove(child);
	}

	/**
	 * Retrieve all subprocesses and child subprocesses recursively.
	 * 
	 * @return A flat list of the contained subprocesses.
	 */
	public List<SubProcess> getSubprocessList() {
		List<SubProcess> subprocesses = new ArrayList<SubProcess>();

		for (FlowElement flowEle : getFlowElement()) {
			if (flowEle instanceof SubProcess) {
				subprocesses.add((SubProcess) flowEle);
				subprocesses.addAll(((SubProcess) flowEle).getSubprocessList());
			}
		}

		return subprocesses;
	}
	
	/**
	 * 
	 * Basic method for the conversion of BPMN2.0 to the editor's internal format. 
	 * {@see BaseElement#toShape(BPMN2DiagramConverter)}
	 * @param converterForShapeCoordinateLookup an instance of {@link BPMN2DiagramConverter}, offering several lookup methods needed for the conversion.
	 * 
	 * @return Instance of org.oryxeditor.server.diagram.Shape, that will be used for the output. 
	 */
	public Shape toShape(BPMN2DiagramConverterI converterForShapeCoordinateLookup) {
		Shape shape = super.toShape(converterForShapeCoordinateLookup);
		shape.putProperty("callacitivity", "false");
		shape.putProperty("activitytype", "Sub-Process");
		shape.putProperty("isclosed", "true");
		shape.putProperty("isadhoc", "false");
		shape.putProperty("isatransaction", "false");
		
		if(this.isTriggeredByEvent()){
			if(shape.getProperty("isExpanded") != null && shape.getProperty("isExpanded").equals("true")){
				shape.setStencil(new StencilType("EventSubprocess"));
			}else{
				shape.setStencil(new StencilType("CollapsedEventSubprocess"));
				
				List<StartEvent> startevents = new ArrayList<StartEvent>();
				for(FlowElement fe : this.getFlowElement())
					if(fe instanceof StartEvent){
						startevents.add(((StartEvent) fe));
						//CONVENTION: as the standard leaves me clueless of what will 
						//happen in case of multiple StartEvents in the collapsed SubProcess,
						//I just take the first one
						break;
					}
					
					if(startevents.size() == 1){
						StartEvent startevent = startevents.get(0);
						if(startevent.isIsInterrupting()){
							shape.putProperty("isinterrupting", "true");
						}
						else{
							shape.putProperty("isinterrupting", "false");
						}
						
						if(startevent.getEventDefinition().size() == 1){
							EventDefinition e = startevent.getEventDefinition().get(0);
							
							if (e instanceof CompensateEventDefinition){ shape.putProperty("startevent", "Compensation");
					   		} else if (e instanceof ConditionalEventDefinition){ shape.putProperty("startevent", "Conditional");
					   		} else if (e instanceof ErrorEventDefinition){ shape.putProperty("startevent", "Error");
					   		} else if (e instanceof EscalationEventDefinition){ shape.putProperty("startevent", "Escalation");
					   		} else if (e instanceof EscalationEventDefinition){ shape.putProperty("startevent", "Escalation");
					   		} else if (e instanceof MessageEventDefinition){ shape.putProperty("startevent", "Message");
					   		} else if (e instanceof SignalEventDefinition){ shape.putProperty("startevent", "Signal");
					   		} else if (e instanceof TimerEventDefinition){ shape.putProperty("startevent", "Timer");
					   		}	   		
						}
						else if(startevent.getEventDefinition().size() > 1){
							if(startevent.isParallelMultiple()){
								shape.putProperty("startevent", "MultipleParallel");
							}
							else{
								shape.putProperty("startevent", "Multiple");
							}
						}
						
						
					}
			
				
			}
					
		}
		else{
			if(shape.getProperty("isExpanded") != null && shape.getProperty("isExpanded").equals("true"))
				shape.setStencil(new StencilType("Subprocess"));
			else
				shape.setStencil(new StencilType("CollapsedSubprocess"));
		}
		
		return shape;
	}

	/* Getter & Setter */

	/**
	 * Gets the value of the flowElement property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the flowElement property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getFlowElement().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link JAXBElement }{@code <}{@link ManualTask }{@code >}
	 * {@link JAXBElement }{@code <}{@link TCallChoreographyActivity }{@code >}
	 * {@link JAXBElement }{@code <}{@link Transaction }{@code >}
	 * {@link JAXBElement }{@code <}{@link TEndEvent }{@code >}
	 * {@link JAXBElement }{@code <}{@link TIntermediateCatchEvent }{@code >}
	 * {@link JAXBElement }{@code <}{@link TFlowElement }{@code >}
	 * {@link JAXBElement }{@code <}{@link CallActivity }{@code >}
	 * {@link JAXBElement }{@code <}{@link TComplexGateway }{@code >}
	 * {@link JAXBElement }{@code <}{@link TBoundaryEvent }{@code >}
	 * {@link JAXBElement }{@code <}{@link TStartEvent }{@code >}
	 * {@link JAXBElement }{@code <}{@link TExclusiveGateway }{@code >}
	 * {@link JAXBElement }{@code <}{@link BusinessRuleTask }{@code >}
	 * {@link JAXBElement }{@code <}{@link ScriptTask }{@code >}
	 * {@link JAXBElement }{@code <}{@link TInclusiveGateway }{@code >}
	 * {@link JAXBElement }{@code <}{@link DataObject }{@code >}
	 * {@link JAXBElement }{@code <}{@link TEvent }{@code >} {@link JAXBElement }
	 * {@code <}{@link ServiceTask }{@code >} {@link JAXBElement }{@code <}
	 * {@link ChoreographyTask }{@code >} {@link JAXBElement }{@code <}
	 * {@link DataStore }{@code >} {@link JAXBElement }{@code <}{@link SubProcess }
	 * {@code >} {@link JAXBElement }{@code <}{@link TIntermediateThrowEvent }
	 * {@code >} {@link JAXBElement }{@code <}{@link UserTask }{@code >}
	 * {@link JAXBElement }{@code <}{@link TSequenceFlow }{@code >}
	 * {@link JAXBElement }{@code <}{@link TEventBasedGateway }{@code >}
	 * {@link JAXBElement }{@code <}{@link AdHocSubProcess }{@code >}
	 * {@link JAXBElement }{@code <}{@link SendTask }{@code >} {@link JAXBElement }
	 * {@code <}{@link ChoreographySubProcess }{@code >} {@link JAXBElement }
	 * {@code <}{@link ReceiveTask }{@code >} {@link JAXBElement }{@code <}
	 * {@link TImplicitThrowEvent }{@code >} {@link JAXBElement }{@code <}
	 * {@link ParallelGateway }{@code >} {@link JAXBElement }{@code <}
	 * {@link TTask }{@code >}
	 * 
	 * 
	 */
	@ChildElements
	public List<FlowElement> getFlowElement() {
		if (flowElement == null) {
			flowElement = new ArrayList<FlowElement>();
		}
		return this.flowElement;
	}

	/**
	 * Gets the value of the artifact property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the artifact property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getArtifact().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link JAXBElement }{@code <}{@link Artifact }{@code >} {@link JAXBElement }
	 * {@code <}{@link Association }{@code >} {@link JAXBElement }{@code <}
	 * {@link TGroup }{@code >} {@link JAXBElement }{@code <}
	 * {@link TextAnnotation }{@code >}
	 * 
	 * 
	 */
	@ChildElements
	public List<Artifact> getArtifact() {
		if (artifact == null) {
			artifact = new ArrayList<Artifact>();
		}
		return this.artifact;
	}

	/**
	 * Gets the value of the triggeredByEvent property.
	 * 
	 * @return possible object is {@link Boolean }
	 * 
	 */
	public boolean isTriggeredByEvent() {
		if (triggeredByEvent == null) {
			return false;
		} else {
			return triggeredByEvent;
		}
	}

	/**
	 * Sets the value of the triggeredByEvent property.
	 * 
	 * @param value
	 *            allowed object is {@link Boolean }
	 * 
	 */
	public void setTriggeredByEvent(Boolean value) {
		this.triggeredByEvent = value;
	}
}
