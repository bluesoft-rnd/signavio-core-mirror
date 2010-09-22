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

package de.hpi.bpmn2_0.model.artifacts;

import javax.xml.bind.annotation.XmlAccessType;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.oryxeditor.server.diagram.Shape;
import org.oryxeditor.server.diagram.StencilType;


import de.hpi.bpmn2_0.transformation.BPMN2DiagramConverterI;
import de.hpi.bpmn2_0.util.EscapingStringAdapter;


/**
 * <p>Java class for tTextAnnotation complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tTextAnnotation">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/bpmn20}tArtifact">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.omg.org/bpmn20}text" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tTextAnnotation", propOrder = {
    "text",
    "textFormat"
})
public class TextAnnotation
    extends Artifact
{

	@XmlElement
	@XmlJavaTypeAdapter(EscapingStringAdapter.class)
    protected String text;
    @XmlAttribute
    @XmlJavaTypeAdapter(EscapingStringAdapter.class)
	protected String textFormat;

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

		shape.setStencil(new StencilType("TextAnnotation"));
		
		if(this.getText() != null)
			shape.putProperty("text", this.getText());
		
		if(this.getTextFormat() != null)
			shape.putProperty("textformat", this.getTextFormat());
		
		//TODO set parent relation (or whatever there is), so the outgoing end of the edge is attached correctly to the task
		//converterForShapeCoordinateLookup.getBpmnShapeByID(this.)
        
        //shape.putProperty("", );
        
		return shape;
	}

    /* Getter & Setter */
    
    /**
     * Gets the value of the text property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getText() {
        return text;
    }

    public String getTextFormat() {
		return textFormat;
	}

	public void setTextFormat(String textFormat) {
		this.textFormat = textFormat;
	}

	/**
     * Sets the value of the text property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setText(String value) {
        this.text = value;
    }

}
