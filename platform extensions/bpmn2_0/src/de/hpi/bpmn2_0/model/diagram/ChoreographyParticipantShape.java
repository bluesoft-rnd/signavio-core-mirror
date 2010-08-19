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
package de.hpi.bpmn2_0.model.diagram;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

import de.hpi.bpmn2_0.model.FlowElement;
import de.hpi.bpmn2_0.model.participant.Participant;

/**
 * Class representing the visual appearance of participants of an choreography
 * activity.
 * 
 * @author Sven Wagner-Boysen
 *
 */
@XmlRootElement(name = "participantShape")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {
		
})
public class ChoreographyParticipantShape extends BpmnNode {
	@XmlAttribute
	@XmlIDREF
	@XmlSchemaType(name = "IDREF")
	private Participant participant;

	/* Getter & Setter */
	
	/**
	 * @return the participant
	 */
	public Participant getParticipant() {
		return participant;
	}

	/**
	 * @param participant the participant to set
	 */
	public void setParticipant(Participant participant) {
		this.participant = participant;
	}

	@Override
	protected FlowElement getFlowElement() {
		return this.getParticipant();
	}
}
