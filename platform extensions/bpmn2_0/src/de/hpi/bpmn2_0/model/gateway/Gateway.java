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

package de.hpi.bpmn2_0.model.gateway;

import javax.xml.bind.annotation.XmlAccessType;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import org.oryxeditor.server.diagram.Shape;

import de.hpi.bpmn2_0.model.FlowNode;

import de.hpi.bpmn2_0.transformation.BPMN2DiagramConverterI;


/**
 * <p>Java class for tGateway complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tGateway">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/bpmn20}tFlowNode">
 *       &lt;attribute name="gatewayDirection" type="{http://www.omg.org/bpmn20}tGatewayDirection" default="unspecified" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tGateway")
@XmlSeeAlso({
    ComplexGateway.class,
    InclusiveGateway.class,
    EventBasedGateway.class,
    ParallelGateway.class,
    ExclusiveGateway.class
})
public class Gateway
    extends FlowNode
{

    @XmlAttribute
    protected GatewayDirection gatewayDirection;
    
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
    	return 40.0;
    }
    
    /**
     * For the fixed-size shape, return the fixed height.
     */
    public double getStandardHeight(){
    	return 40.0;
    }
    
    
    /**
	 * 
	 * Basic method for the conversion of BPMN2.0 to the editor's internal
	 * format. {@see BaseElement#toShape(BPMN2DiagramConverter)}
	 * 
	 * @param converterForShapeCoordinateLookup
	 *            an instance of {@link BPMN2DiagramConverter}, offering several
	 *            lookup methods needed for the conversion.
	 * 
	 * @return Instance of org.oryxeditor.server.diagram.Shape, that will be
	 *         used for the output. The name property is set, and a parent is
	 *         added (the flow element is added as a child as well).
	 */
    public Shape toShape(BPMN2DiagramConverterI converterForShapeLookup) {
    	Shape shape = super.toShape(converterForShapeLookup);
    	
    	//not relevant for the editor
    	//this.getGatewayDirection()
    	
    	shape.setBounds(getMiddleBounds(this.getStandardWidth(), this.getStandardHeight(), shape.getBounds()));
    	
   		return shape;    
    }
    
    
    
    /* Getter & Setter */
    
    /**
     * Gets the value of the gatewayDirection property.
     * 
     * @return
     *     possible object is
     *     {@link GatewayDirection }
     *     
     */
    public GatewayDirection getGatewayDirection() {
    	if (gatewayDirection == null) {
            return GatewayDirection.UNSPECIFIED;
        } else {
            return gatewayDirection;
        }
    }

    /**
     * Sets the value of the gatewayDirection property.
     * 
     * @param value
     *     allowed object is
     *     {@link GatewayDirection }
     *     
     */
    public void setGatewayDirection(GatewayDirection value) {
        this.gatewayDirection = value;
    }

}
