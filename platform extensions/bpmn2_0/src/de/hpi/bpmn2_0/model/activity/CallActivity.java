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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.oryxeditor.server.diagram.Shape;
import org.oryxeditor.server.diagram.StencilType;

import de.hpi.bpmn2_0.model.CallableElement;

import de.hpi.bpmn2_0.transformation.BPMN2DiagramConverterI;


/**
 * <p>Java class for tCallActivity complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tCallActivity">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/bpmn20}tActivity">
 *       &lt;attribute name="calledElement" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tCallActivity")
public class CallActivity
    extends Activity
{

    @XmlAttribute
    @XmlIDREF
    protected CallableElement calledElement;

    /**
     * Gets the value of the calledElement property.
     * 
     * @return
     *     possible object is
     *     {@link CallableElement }
     *     
     */
    public CallableElement getCalledElement() {
        return calledElement;
    }

    /**
     * Sets the value of the calledElement property.
     * 
     * @param value
     *     allowed object is
     *     {@link CallableElement }
     *     
     */
    public void setCalledElement(CallableElement value) {
        this.calledElement = value;
    }
    
	/**
	 * 
	 * Basic method for the conversion of BPMN2.0 to the editor's internal format. 
	 * {@see BaseElement#toShape(BPMN2DiagramConverter)}
	 * @param converterForShapeCoordinateLookup an instance of {@link BPMN2DiagramConverter}, offering several lookup methods needed for the conversion.
	 * 
	 * @return Instance of org.oryxeditor.server.diagram.Shape, that will be used for the output. 
	 */
    public Shape toShape(BPMN2DiagramConverterI converterForShapeCoordinateLookup)  {    	
    	
    	Shape shape = super.toShape(converterForShapeCoordinateLookup);

		shape.putProperty("callacitivity", "true");
		
		if(this.getCalledElement() != null){
			//[BPMN2.0] TODO set all properties of called activity! 
			// > impossible, the called element is none of the "graphical" elements, which have all the attributes. 
			//    the info should be contained in "this"...
			//System.out.println("+ Call activity with calledELement!");
		}else{
			//System.out.println("- Call activity without calledELement!");	
		}
		
		if(this.getCalledElement() != null && this.getCalledElement() instanceof de.hpi.bpmn2_0.model.Process){
			if(converterForShapeCoordinateLookup.isChoreography()){
				shape.setStencil(new StencilType("ChoreographySubprocessCollapsed"));
			}else if(converterForShapeCoordinateLookup.isConversation()){
				shape.setStencil(new StencilType("SubConversation"));
			}else{
				shape.setStencil(new StencilType("CollapsedSubprocess"));
			}
		}else{
			if(converterForShapeCoordinateLookup.isChoreography()){
				shape.setStencil(new StencilType("ChoreographyTask"));
			}else if(converterForShapeCoordinateLookup.isConversation()){
				shape.setStencil(new StencilType("Communication"));
			}else{
				shape.setStencil(new StencilType("Task"));
			}
		}
		
		return shape;
	}

}
