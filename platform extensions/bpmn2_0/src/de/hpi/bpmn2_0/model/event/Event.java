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

package de.hpi.bpmn2_0.model.event;

import java.util.ArrayList;


import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.oryxeditor.server.diagram.Shape;

import de.hpi.bpmn2_0.model.Expression;
import de.hpi.bpmn2_0.model.FlowNode;

import de.hpi.bpmn2_0.transformation.BPMN2DiagramConverterI;



/**
 * <p>Java class for tEvent complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tEvent">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/bpmn20}tFlowNode">
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tEvent")
@XmlSeeAlso({
    ThrowEvent.class,
    CatchEvent.class
})
public abstract class Event
    extends FlowNode
{
	@XmlElementRefs({
		@XmlElementRef(type = MessageEventDefinition.class),
		@XmlElementRef(type = TimerEventDefinition.class),
		@XmlElementRef(type = CancelEventDefinition.class),
		@XmlElementRef(type = CompensateEventDefinition.class),
		@XmlElementRef(type = ConditionalEventDefinition.class),
		@XmlElementRef(type = ErrorEventDefinition.class),
		@XmlElementRef(type = EscalationEventDefinition.class),
		@XmlElementRef(type = LinkEventDefinition.class),
		@XmlElementRef(type = SignalEventDefinition.class),
		@XmlElementRef(type = TerminateEventDefinition.class)
	})
	List<EventDefinition> eventDefinition;
	
	@XmlElement
	@XmlJavaTypeAdapter(CollapsedStringAdapter.class)
	String eventDefinitionRef;
	
	/* Constructors */
	
	public Event() {}
	
	public Event(Event endEvent) {
		super(endEvent);
		this.getEventDefinition().addAll(endEvent.getEventDefinition());
		this.setEventDefinitionRef(endEvent.getEventDefinitionRef());
	}
	
	/* Transformation Methods */

	/**
	 * Convenience method for putting shape properties.
	 * @param shape The editor shape whose properties shall be set.
	 * @param e An event definition that provides the information for the properties.
	 */
	public static void putShapeEscalationEventProperties(Shape shape, EventDefinition e){
		if(((EscalationEventDefinition) e).getEscalationRef() != null){
				if(((EscalationEventDefinition) e).getEscalationRef().getName() != null){
					shape.putProperty("escalationname", ((EscalationEventDefinition) e).getEscalationRef().getName());
				}
				if(((EscalationEventDefinition) e).getEscalationRef().getEscalationCode() != null){
					shape.putProperty("escalationcode", ((EscalationEventDefinition) e).getEscalationRef().getEscalationCode());
				}
			}
	}
	
	/**
	 * Convenience method for putting shape properties.
	 * @param shape The editor shape whose properties shall be set.
	 * @param e An event definition that provides the information for the properties.
	 */
	public static void putShapeErrorEventProperties(Shape shape, EventDefinition e){
		if(((ErrorEventDefinition) e).getError() != null){
			if(((ErrorEventDefinition) e).getError().getName() != null)
				shape.putProperty("errorname", ((ErrorEventDefinition) e).getError().getName());
			if(((ErrorEventDefinition) e).getError().getErrorCode() != null)
   				shape.putProperty("errorcode", ((ErrorEventDefinition) e).getError().getErrorCode());
		}
	}
	
	/**
	 * Convenience method for putting shape properties.
	 * @param shape The editor shape whose properties shall be set.
	 * @param e An event definition that provides the information for the properties.
	 */
	public static void putShapeSignalEventProperties(Shape shape, EventDefinition e){
		if(((SignalEventDefinition) e).getSignalRef() != null)
			if(((SignalEventDefinition) e).getSignalRef().getName() != null)
				shape.putProperty("signalname", ((SignalEventDefinition) e).getSignalRef().getName());
	}
	
	/**
	 * Convenience method for putting shape properties.
	 * @param shape The editor shape whose properties shall be set.
	 * @param e An event definition that provides the information for the properties.
	 */
	public static void putShapeTimerEventProperties(Shape shape, EventDefinition e){
		if(((TimerEventDefinition) e).getTimeCycle() != null){
			Expression timecycle = ((TimerEventDefinition) e).getTimeCycle();
			if(timecycle.toExportString() != null){
				shape.putProperty("timecycle", timecycle.toExportString());
			}
		}
		
		if(((TimerEventDefinition) e).getTimeDuration() != null){
			Expression timeduration = ((TimerEventDefinition) e).getTimeDuration();
			if(timeduration.toExportString() != null){
				shape.putProperty("timeduration", timeduration.toExportString());
			}
		}
		
		if(((TimerEventDefinition) e).getTimeDate() != null){
			Expression timedate = ((TimerEventDefinition) e).getTimeDate();
			if(timedate.toExportString() != null){
				shape.putProperty("timedate", timedate.toExportString());
			}
		}
	}
	
	/**
	 * Convenience method for putting shape properties.
	 * @param shape The editor shape whose properties shall be set.
	 * @param e An event definition that provides the information for the properties.
	 */
	public static void putShapeMessageEventProperties(Shape shape, EventDefinition e){
		if(((MessageEventDefinition) e).getOperationRef() != null){
			if(((MessageEventDefinition) e).getOperationRef().getName() != null)
				shape.putProperty("operationname", ((MessageEventDefinition) e).getOperationRef().getName());
		}
		
		if(((MessageEventDefinition) e).getMessageRef() != null){
			if(((MessageEventDefinition) e).getMessageRef().getName() != null)
				shape.putProperty("messagename", ((MessageEventDefinition) e).getMessageRef().getName());
		}
	}
	
	/**
	 * Convenience method for putting shape properties.
	 * @param shape The editor shape whose properties shall be set.
	 * @param e An event definition that provides the information for the properties.
	 */
	public static void putShapeConditionalEventProperties(Shape shape, EventDefinition e){
		if(((ConditionalEventDefinition) e).getCondition() != null){
			if(((ConditionalEventDefinition) e).getCondition().toExportString() != null)
				shape.putProperty("condition", ((ConditionalEventDefinition) e).getCondition().toExportString());
		}
	}
	
	/**
	 * Convenience method for putting shape properties.
	 * @param shape The editor shape whose properties shall be set.
	 * @param e An event definition that provides the information for the properties.
	 */
	public static void putShapeLinkEventProperties(Shape shape, EventDefinition e){
		//((LinkEventDefinition) e).getName();
		//There is no consensus on how to do this; we just set the name, which has already been done.
		//TODO: update if the standard changes...
	}
	
	/**
	 * Convenience method for putting shape properties.
	 * @param shape The editor shape whose properties shall be set.
	 * @param e An event definition that provides the information for the properties.
	 */
	public static void putShapeCompensateEventProperties(Shape shape, EventDefinition e){
		if(((CompensateEventDefinition)e).isWaitForCompletion() != null){
			shape.putProperty("waitforcompletion", ((CompensateEventDefinition)e).isWaitForCompletion().toString());
		}
		
		if(((CompensateEventDefinition)e).getActivityRef() != null){
			if(((CompensateEventDefinition)e).getActivityRef().getId() != null)
				shape.putProperty("activityref", ((CompensateEventDefinition)e).getActivityRef().getId());
		}		
	}
	
	/**
	 * Helper for the import, see {@link FlowElement#isElementWithFixedSize().
	 */
    // @Override
    public boolean isElementWithFixedSize() {
		return true;
	}
    
    /**
     * For the fixed-size shape, return the fixed width.
     */
    public double getStandardWidth(){
    	return 28.0;
    }
    
    /**
     * For the fixed-size shape, return the fixed height.
     */
    public double getStandardHeight(){
    	return 28.0;
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
    	
    	//has been moved to BPMNShape
    	
//    	if(!(this instanceof BoundaryEvent) && !(this instanceof IntermediateCatchEvent) && !(this instanceof IntermediateThrowEvent)){
//	    	Bounds thisBpmnShapeBounds = converterForShapeCoordinateLookup.getBpmnShapeByID(this.getId()).getBounds();
//	    	shape.getDockers().add(new Point(thisBpmnShapeBounds.getX() + 15, 
//	    			thisBpmnShapeBounds.getY() + 15));
//    	}
    	
    	shape.setBounds(getMiddleBounds(this.getStandardWidth(), this.getStandardHeight(), shape.getBounds()));
    	
    	return shape;
    }
	
	/**
	 * 
	 * @param type
	 * 		The {@link EventDefinition} type.
	 * @return
	 * 		The first occurrence of an {@link EventDefinition} where the type fits.
	 * 		Or null if no {@link EventDefinition} of this type exists.
	 */
	public EventDefinition getEventDefinitionOfType(Class<? extends EventDefinition> type) {
		for(EventDefinition evDef : this.getEventDefinition()) {
			if(evDef.getClass().equals(type))
				return evDef;
		}
		return null;
	}
	
	public boolean isSignalEvent() {
		return getEventDefinitionOfType(SignalEventDefinition.class) != null;
	}
	
	/* Getter & Setter */
	
	/**
     * Gets the value of the eventDefinition property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the eventDefinition property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEventDefinition().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CompensateEventDefinition }
     * {@link TMessageEventDefinition }
     * {@link ErrorEventDefinition }
     * {@link TTimerEventDefinition }
     * {@link EventDefinition }
     * {@link ConditionalEventDefinition }
     * {@link LinkEventDefinition }
     * {@link CancelEventDefinition }
     * {@link TEscalationEventDefinition }
     * {@link SignalEventDefinition }
     * {@link TTerminateEventDefinition }
     * 
	 * @return the eventDefinition
	 */
	public List<EventDefinition> getEventDefinition() {
		if(this.eventDefinition == null) {
			this.eventDefinition = new ArrayList<EventDefinition>();
		}
		return this.eventDefinition;
	}

	/**
	 * @return the eventDefinitionRef
	 */
	public String getEventDefinitionRef() {
		return eventDefinitionRef;
	}

	/**
	 * @param eventDefinitionRef the eventDefinitionRef to set
	 */
	public void setEventDefinitionRef(String eventDefinitionRef) {
		this.eventDefinitionRef = eventDefinitionRef;
	}

}
