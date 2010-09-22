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

import de.hpi.bpmn2_0.annotations.Property;
import de.hpi.bpmn2_0.annotations.StencilId;
import de.hpi.bpmn2_0.exceptions.BpmnConverterException;
import de.hpi.bpmn2_0.factory.AbstractShapeFactory;
import de.hpi.bpmn2_0.model.conversation.CallConversation;
import de.hpi.bpmn2_0.model.conversation.Conversation;
import de.hpi.bpmn2_0.model.conversation.ConversationNode;
import de.hpi.bpmn2_0.model.conversation.SubConversation;

/**
 * Factory that creates communication and conversation elements
 * 
 * @author Sven Wagner-Boysen
 * 
 */
@StencilId( { "Communication", "SubConversation" })
public class ConversationFactory extends AbstractShapeFactory {

	/*
	 * (non-Javadoc)
	 * 
	 * @seede.hpi.bpmn2_0.factory.AbstractBpmnFactory#createProcessElement(org.
	 * oryxeditor.server.diagram.Shape)
	 */
	// @Override
	protected ConversationNode createProcessElement(Shape shape)
			throws BpmnConverterException {
		try {
			ConversationNode node = (ConversationNode) this
					.invokeCreatorMethodAfterProperty(shape);
			return node;
		} catch (Exception e) {
			throw new BpmnConverterException(
					"Error while creating the process element of "
							+ shape.getStencilId(), e);
		}

	}

	/**
	 * Creates the process element for a call conversation.
	 * 
	 * @param shape
	 *            The resource shape
	 * @return The {@link CallConversation}
	 */
	@Property(name = "iscallconversation", value = "true")
	public CallConversation createCallConversation(Shape shape) {
		CallConversation callCon = new CallConversation();
		callCon.setId(shape.getResourceId());
		callCon.setName(shape.getProperty("name"));
		return callCon;
	}

	@Property(name = "iscallconversation", value = { "false", "" })
	public ConversationNode createNonCallConversation(Shape shape) {
		ConversationNode con = null;
		if (shape.getStencilId().equals("Communication")) {
			con = new Conversation();
		} else if (shape.getStencilId().equals("SubConversation")) {
			con = new SubConversation();
		}

		con.setId(shape.getResourceId());
		con.setName(shape.getProperty("name"));
		return con;
	}

	private List<String> getParticipantIds(Shape shape) {
		List<String> participantIds = new ArrayList<String>();

		/* Check outgoing conversation links */

		for (Shape connector : shape.getOutgoings()) {
			if (!connector.getStencilId().equals("ConversationLink"))
				continue;

			if (connector.getTarget() != null
					&& connector.getTarget().getStencilId().equals(
							"Participant"))
				participantIds.add(connector.getTarget().getResourceId());
		}

		/* Check incomming conversation links */
		for (Shape connector : shape.getIncomings()) {
			if (!connector.getStencilId().equals("ConversationLink"))
				continue;

			for (Shape part : connector.getIncomings()) {
				if (part.getStencilId().equals("Participant"))
					participantIds.add(part.getResourceId());
			}
		}

		return participantIds;
	}
}
