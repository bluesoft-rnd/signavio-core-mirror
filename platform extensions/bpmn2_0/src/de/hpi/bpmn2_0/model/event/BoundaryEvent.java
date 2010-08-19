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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

import de.hpi.bpmn2_0.model.activity.Activity;


/**
 * <p>Java class for tBoundaryEvent complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tBoundaryEvent">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/bpmn20}tCatchEvent">
 *       &lt;attribute name="cancelActivity" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
 *       &lt;attribute name="attachedToRef" use="required" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlRootElement(name = "boundaryEvent")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tBoundaryEvent")
public class BoundaryEvent
    extends IntermediateCatchEvent
{

    @XmlAttribute
    protected Boolean cancelActivity;
    @XmlAttribute(required = true)
    @XmlIDREF
    @XmlSchemaType(name = "IDREF")
    protected Activity attachedToRef;

    /**
     * Gets the value of the cancelActivity property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isCancelActivity() {
        if (cancelActivity == null) {
            return true;
        } else {
            return cancelActivity;
        }
    }

    /**
     * Sets the value of the cancelActivity property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setCancelActivity(Boolean value) {
        this.cancelActivity = value;
    }

    /**
     * Gets the value of the attachedToRef property.
     * 
     * @return
     *     possible object is
     *     {@link Activity }
     *     
     */
    public Activity getAttachedToRef() {
        return attachedToRef;
    }

    /**
     * Sets the value of the attachedToRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link Activity }
     *     
     */
    public void setAttachedToRef(Activity value) {
        this.attachedToRef = value;
    }

}
