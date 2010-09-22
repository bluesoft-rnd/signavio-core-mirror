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

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.oryxeditor.server.diagram.Shape;
import org.oryxeditor.server.diagram.StencilType;


import de.hpi.bpmn2_0.transformation.BPMN2DiagramConverterI;


/**
 * <p>Java class for tIntermediateCatchEvent complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tIntermediateCatchEvent">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/bpmn20}tCatchEvent">
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tIntermediateCatchEvent")
@XmlSeeAlso(BoundaryEvent.class)
public class IntermediateCatchEvent
    extends CatchEvent
{
	@XmlTransient
	protected String cancelActivity;
	
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

    	List<EventDefinition> eventdef = this.getEventDefinition();
    	
    	
    	if(eventdef.size() == 0){
	   		 shape.setStencil(new StencilType("StartNoneEvent"));
	   	}
	   	else if (eventdef.size() == 1){
	   		EventDefinition e = eventdef.get(0);
            
	   		if(e instanceof CancelEventDefinition){ shape.setStencil(new StencilType("IntermediateCancelEvent"));
	   		} else if (e instanceof CompensateEventDefinition){ 
	   			shape.setStencil(new StencilType("IntermediateCompensationEventCatching"));
	   			putShapeCompensateEventProperties(shape, e);
	   		} else if (e instanceof ConditionalEventDefinition){ 
	   			shape.setStencil(new StencilType("IntermediateConditionalEvent"));
	   			putShapeConditionalEventProperties(shape, e);
	   		} else if (e instanceof ErrorEventDefinition){ 
	   			shape.setStencil(new StencilType("IntermediateErrorEvent"));
	   			putShapeErrorEventProperties(shape, e);
	   		} else if (e instanceof EscalationEventDefinition){ 
	   			shape.setStencil(new StencilType("IntermediateEscalationEvent"));
	   			putShapeEscalationEventProperties(shape, e);
	   		} else if (e instanceof EscalationEventDefinition){ 
	   			shape.setStencil(new StencilType("IntermediateEscalationEvent"));
	   			putShapeEscalationEventProperties(shape, e);
	   		} else if (e instanceof LinkEventDefinition){ 
	   			shape.setStencil(new StencilType("IntermediateLinkEventCatching"));
	   			putShapeLinkEventProperties(shape, e);
	   		} else if (e instanceof MessageEventDefinition){ 
	   			shape.setStencil(new StencilType("IntermediateMessageEventCatching"));
	   			putShapeMessageEventProperties(shape, e);
	   		} else if (e instanceof SignalEventDefinition){ 
	   			shape.setStencil(new StencilType("IntermediateSignalEventCatching"));
	   			putShapeSignalEventProperties(shape, e);
	   		//} else if (e instanceof TerminateEventDefinition){ shape.setStencil(new StencilType(""));
	   		} else if (e instanceof TimerEventDefinition){ 
	   			shape.setStencil(new StencilType("IntermediateTimerEvent"));
	   			putShapeTimerEventProperties(shape, e);
	   		}
	   	}
	   	else{
	   		
	   	 	if(this.isParallelMultiple()){
   	 		  shape.setStencil(new StencilType("IntermediateParallelMultipleEventCatching"));
	   	 	}	   	 	  
	   	 	else{
   			  shape.setStencil(new StencilType("IntermediateMultipleEventCatching"));
	   		}
	   	 	
	   	 for(EventDefinition e : eventdef){
	   		if (e instanceof CompensateEventDefinition){ 
	   			putShapeCompensateEventProperties(shape, e);
	   		} else if (e instanceof ConditionalEventDefinition){ 
	   			putShapeConditionalEventProperties(shape, e);
	   		} else if (e instanceof ErrorEventDefinition){ 
	   			putShapeErrorEventProperties(shape, e);
	   		} else if (e instanceof EscalationEventDefinition){ 
	   			putShapeEscalationEventProperties(shape, e);
	   		} else if (e instanceof EscalationEventDefinition){ 
	   			putShapeEscalationEventProperties(shape, e);
	   		} else if (e instanceof LinkEventDefinition){ 
	   			putShapeLinkEventProperties(shape, e);
	   		} else if (e instanceof MessageEventDefinition){ 
	   			putShapeMessageEventProperties(shape, e);
	   		} else if (e instanceof SignalEventDefinition){ 
	   			putShapeSignalEventProperties(shape, e);
	   		} else if (e instanceof TimerEventDefinition){ 
	   			putShapeTimerEventProperties(shape, e);
	   		}
		  }
	   	}
    	return shape;
    }
    
	/**
	 * Avoid null values.
	 * 
	 * @return the cancelActivity
	 */
	public String getCancelActivity() {
		if(cancelActivity == null)
			return "";
		return cancelActivity;
	}

	/**
	 * @param cancelActivity the cancelActivity to set
	 */
	public void setCancelActivity(String cancelActivity) {
		this.cancelActivity = cancelActivity;
	}
	
	
	
	

}
