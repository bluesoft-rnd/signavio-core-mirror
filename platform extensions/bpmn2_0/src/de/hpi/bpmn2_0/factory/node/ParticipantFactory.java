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

package de.hpi.bpmn2_0.factory.node;

import java.util.ArrayList;
import java.util.List;

import org.oryxeditor.server.diagram.Shape;

import de.hpi.bpmn2_0.annotations.StencilId;
import de.hpi.bpmn2_0.exceptions.BpmnConverterException;
import de.hpi.bpmn2_0.factory.AbstractShapeFactory;
import de.hpi.bpmn2_0.model.bpmndi.BPMNShape;
import de.hpi.bpmn2_0.model.participant.Participant;

/**
 * Factory to create participants
 * 
 * @author Sven Wagner-Boysen
 *
 */
@StencilId("ChoreographyParticipant")
public class ParticipantFactory extends AbstractShapeFactory {

	/* (non-Javadoc)
	 * @see de.hpi.bpmn2_0.factory.AbstractBpmnFactory#createProcessElement(org.oryxeditor.server.diagram.Shape)
	 */
	// @Override
	protected Participant createProcessElement(Shape shape)
			throws BpmnConverterException {
		Participant p = new Participant();
		p._isChoreographyParticipant = true;
		this.setCommonAttributes(p, shape);
		p.setId(shape.getResourceId());
		p.setName(shape.getProperty("name"));
		
		/* Handle initiating property */
		String initiating = shape.getProperty("initiating");
		if(initiating != null)
			p.setInitiating(initiating.equalsIgnoreCase("true"));
		else 
			p.setInitiating(false);
		return p;
	}
	
	// @Override
	protected BPMNShape createDiagramElement(Shape shape) {
		BPMNShape bpmnShape = super.createDiagramElement(shape);
		bpmnShape.setIsMessageVisible(Boolean.valueOf(isMessageVisible(shape)));
		return bpmnShape;
	}
	
	/**
	 *  Checks whether the message of an participant is visible 
	 */
	private boolean isMessageVisible(Shape shape) {
		/* Navigate in both directions because of undirected association */
		List<Shape> connectedElements = new ArrayList<Shape>();
		connectedElements.addAll(shape.getOutgoings());
		connectedElements.addAll(shape.getIncomings());
		
		for(Shape conShape : connectedElements) {
			if(conShape.getStencilId().equals("Association_Undirected")) {
				List<Shape> shapeList = new ArrayList<Shape>();
				shapeList.addAll(conShape.getIncomings());
				shapeList.addAll(conShape.getOutgoings());
				
				for(Shape msgShape : shapeList) {
					if(msgShape.getStencilId().equals("Message"))
						return true;
				}
			}
		}
		
		return false;
	}
	

}
