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

package de.hpi.bpmn2_0.model.conversation;

import java.util.ArrayList;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.oryxeditor.server.diagram.Shape;

import de.hpi.bpmn2_0.model.FlowNode;
import de.hpi.bpmn2_0.model.connector.MessageFlow;
import de.hpi.bpmn2_0.model.participant.Participant;

import de.hpi.bpmn2_0.transformation.BPMN2DiagramConverterI;

/**
 * <p>
 * Java class for tConversationNode complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="tConversationNode">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/bpmn20}tBaseElement">
 *       &lt;sequence>
 *         &lt;element name="participantRef" type="{http://www.w3.org/2001/XMLSchema}QName" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tConversationNode", propOrder = { "participantRef",
		"messageFlowRef", "correlationKey" })
@XmlSeeAlso({ CallConversation.class, SubConversation.class, Conversation.class })
public abstract class ConversationNode extends FlowNode implements
		ConversationElement {

	@XmlIDREF
	protected List<MessageFlow> messageFlowRef;
	protected List<CorrelationKey> correlationKey;

	@XmlIDREF
	protected List<Participant> participantRef;

	@XmlTransient
	public List<String> participantsIds;

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
    	return 33.5;
    }
    
    /**
     * For the fixed-size shape, return the fixed height.
     */
    public double getStandardHeight(){
    	return 29.0;
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
	 *         used for the output. Its bounds are set, as they are fixed in the
	 *         editor, but not necessarily in external ones.
	 */
	public Shape toShape(BPMN2DiagramConverterI converterForShapeCoordinateLookup) {
		Shape shape = super.toShape(converterForShapeCoordinateLookup);

		converterForShapeCoordinateLookup.setIsConversation(true);

		// also a fixed size!
		// width="33.5"
		// height="29"
		// > but, gets cut if there is a white rim, so:
		// y : 0.5 - 28.43 > 27.93
		// x : 0.578 - 32.828 > 32.250
		// >> looks better with the SUM of both...
		// Point upperLeft = shape.getBounds().getUpperLeft();

		// new Bounds(new Point(upperLeft.getX() + 33.5, upperLeft.getY() + 29),
		// upperLeft)
		shape.setBounds(getMiddleBounds(this.getStandardWidth(), this.getStandardHeight(), shape.getBounds()));

		return shape;
	}

	/**
	 * Gets the value of the participantRef property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the participantRef property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getParticipantRef().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link Participant }
	 * 
	 * 
	 */
	public List<Participant> getParticipantRef() {
		if (participantRef == null) {
			participantRef = new ArrayList<Participant>();
		}
		return this.participantRef;
	}
}
