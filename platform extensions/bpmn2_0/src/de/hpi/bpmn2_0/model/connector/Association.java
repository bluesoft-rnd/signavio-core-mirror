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

package de.hpi.bpmn2_0.model.connector;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.oryxeditor.server.diagram.Shape;
import org.oryxeditor.server.diagram.StencilType;


import de.hpi.bpmn2_0.transformation.BPMN2DiagramConverterI;


/**
 * <p>Java class for tAssociation complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tAssociation">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/bpmn20}tArtifact">
 *       &lt;attribute name="sourceRef" use="required" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *       &lt;attribute name="targetRef" use="required" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *       &lt;attribute name="associationDirection" type="{http://www.omg.org/bpmn20}tAssociationDirection" default="none" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tAssociation")
public class Association
    extends Edge
{
    @XmlAttribute
    protected AssociationDirection associationDirection;
    
    @XmlTransient
    public boolean _containedInProcess;

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

		if(this.getAssociationDirection().equals(AssociationDirection.BOTH)){
			shape.setStencil(new StencilType("Association_Bidirectional"));
		}else if(this.getAssociationDirection().equals(AssociationDirection.ONE)){
			shape.setStencil(new StencilType("Association_Unidirectional"));
		}else { //if(this.getAssociationDirection().equals(AssociationDirection.NONE)){
			shape.setStencil(new StencilType("Association_Undirected"));
		}
		
		String sourceObject = this.sourceRef.getId();
		
		//add to source as outgoing
		Shape s = converterForShapeCoordinateLookup.getEditorShapeByID(sourceObject);
		if(s == null){
			s = converterForShapeCoordinateLookup.newShape(sourceObject);
		}
		s.addOutgoing(new Shape(this.getId()));
		
        //shape.putProperty("", );
        
		return shape;
	}

    
    /**
     * Gets the value of the associationDirection property.
     * 
     * @return
     *     possible object is
     *     {@link AssociationDirection }
     *     
     */
    public AssociationDirection getAssociationDirection() {
        if (associationDirection == null) {
            return AssociationDirection.NONE;
        } else {
            return associationDirection;
        }
    }

    /**
     * Sets the value of the associationDirection property.
     * 
     * @param value
     *     allowed object is
     *     {@link AssociationDirection }
     *     
     */
    public void setAssociationDirection(AssociationDirection value) {
        this.associationDirection = value;
    }

}
