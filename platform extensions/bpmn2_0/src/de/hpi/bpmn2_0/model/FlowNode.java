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

package de.hpi.bpmn2_0.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import de.hpi.bpmn2_0.model.activity.Activity;
import de.hpi.bpmn2_0.model.connector.Association;
import de.hpi.bpmn2_0.model.connector.AssociationDirection;
import de.hpi.bpmn2_0.model.connector.SequenceFlow;
import de.hpi.bpmn2_0.model.event.BoundaryEvent;
import de.hpi.bpmn2_0.model.event.CompensateEventDefinition;
import de.hpi.bpmn2_0.model.event.Event;
import de.hpi.bpmn2_0.model.gateway.Gateway;

/**
 * <p>
 * Java class for tFlowNode complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name=&quot;tFlowNode&quot;&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base=&quot;{http://www.omg.org/bpmn20}tFlowElement&quot;&gt;
 *       &lt;sequence&gt;
 *         &lt;element name=&quot;incoming&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}QName&quot; maxOccurs=&quot;unbounded&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;outgoing&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}QName&quot; maxOccurs=&quot;unbounded&quot; minOccurs=&quot;0&quot;/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tFlowNode")
@XmlSeeAlso( { Event.class,
// ChoreographyActivity.class,
		Gateway.class, Activity.class })
public abstract class FlowNode extends FlowElement {
	
	/**
	 * Default constructor
	 */
	public FlowNode() {
		
	}
	
	/**
	 * Copy constructor
	 * 
	 * @param flowNode
	 * 		The {@link FlowNode} to copy
	 */
	public FlowNode(FlowNode flowNode) {
		super(flowNode);
	}

	/**
	 * Convenience method to retrieve all incoming {@link SequenceFlow}
	 * 
	 * Changes to that list have no influence to the result other callers get.
	 * 
	 * @return The list of {@link SequenceFlow}
	 */
	public List<SequenceFlow> getIncomingSequenceFlows() {
		ArrayList<SequenceFlow> incomingSeq = new ArrayList<SequenceFlow>();

		for (FlowElement node : this.getIncoming()) {
			/* Determine if type of sequence flow */
			if (node instanceof SequenceFlow) {
				incomingSeq.add((SequenceFlow) node);
			}
		}

		return incomingSeq;
	}

	/**
	 * Convenience method to retrieve all outgoing {@link SequenceFlow}
	 * 
	 * Changes to that list have no influence to the result other callers get.
	 * 
	 * @return The list of {@link SequenceFlow}
	 */
	public List<SequenceFlow> getOutgoingSequenceFlows() {
		ArrayList<SequenceFlow> outgoingSeq = new ArrayList<SequenceFlow>();

		for (FlowElement node : this.getOutgoing()) {
			/* Determine if type of sequence flow */
			if (node instanceof SequenceFlow) {
				outgoingSeq.add((SequenceFlow) node);
			}
		}

		return outgoingSeq;
	}

	/**
	 * @return The incoming compensation Flow.
	 */
	public List<Association> getIncomingCompensationFlows() {
		ArrayList<Association> compensationFlows = new ArrayList<Association>();

		/* Find incomming compensation flow */
		for (FlowElement edge : this.getIncoming()) {
			if (edge instanceof Association
					&& ((Association) edge).getAssociationDirection().equals(
							AssociationDirection.ONE)
					&& ((Association) edge).getSourceRef() instanceof BoundaryEvent
					&& (((BoundaryEvent) ((Association) edge).getSourceRef())
							.getEventDefinition().size() == 1 && (((BoundaryEvent) ((Association) edge)
							.getSourceRef()).getEventDefinition().get(0) instanceof CompensateEventDefinition))) {
				compensationFlows.add((Association) edge);
			}
		}
		
		return compensationFlows;
	}
	
	/**
	 * @return The outcoming compensation Flow.
	 */
	public List<Association> getOutgoingCompensationFlows() {
		ArrayList<Association> compensationFlows = new ArrayList<Association>();

		/* Find outgoing compensation flow */
		for (FlowElement edge : this.getOutgoing()) {
			if (edge instanceof Association
					&& ((Association) edge).getAssociationDirection().equals(
							AssociationDirection.ONE)
					&& ((Association) edge).getSourceRef() instanceof BoundaryEvent
					&& (((BoundaryEvent) ((Association) edge).getSourceRef())
							.getEventDefinition().size() == 1 && (((BoundaryEvent) ((Association) edge)
							.getSourceRef()).getEventDefinition().get(0) instanceof CompensateEventDefinition))) {
				compensationFlows.add((Association) edge);
			}
		}
		
		return compensationFlows;
	}
}
