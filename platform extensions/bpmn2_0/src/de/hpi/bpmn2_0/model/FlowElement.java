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

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;

import org.oryxeditor.server.diagram.Bounds;
import org.oryxeditor.server.diagram.Point;
import org.oryxeditor.server.diagram.Shape;

import de.hpi.bpmn2_0.model.activity.SubProcess;
import de.hpi.bpmn2_0.model.bpmndi.BPMNShape;
import de.hpi.bpmn2_0.model.choreography.SubChoreography;
import de.hpi.bpmn2_0.model.connector.Edge;
import de.hpi.bpmn2_0.model.data_object.DataStoreReference;
import de.hpi.bpmn2_0.model.misc.Auditing;
import de.hpi.bpmn2_0.model.misc.Monitoring;
import de.hpi.bpmn2_0.model.participant.Lane;

import de.hpi.bpmn2_0.transformation.BPMN2DiagramConverterI;
import de.hpi.bpmn2_0.util.EscapingStringAdapter;

/**
 * <p>
 * Java class for tFlowElement complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="tFlowElement">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/bpmn20}tBaseElement">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.omg.org/bpmn20}auditing" minOccurs="0"/>
 *         &lt;element ref="{http://www.omg.org/bpmn20}monitoring" minOccurs="0"/>
 *         &lt;element name="categoryValue" type="{http://www.w3.org/2001/XMLSchema}QName" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "tFlowElement", propOrder = { "auditing", "monitoring",
		"categoryValue"
// "incoming",
// "outgoing"
// "process"
})
@XmlSeeAlso({
// SequenceFlow.class,
// DataObject.class,
// DataStore.class,
		Lane.class, FlowNode.class, DataStoreReference.class })
public abstract class FlowElement extends BaseElement {

	protected Auditing auditing;
	protected Monitoring monitoring;
	protected List<QName> categoryValue;

	@XmlAttribute
	@XmlJavaTypeAdapter(EscapingStringAdapter.class)
	protected String name;

	// @XmlIDREF
	// @XmlSchemaType(name = "IDREF")
	// @XmlElement(name = "incoming", type = Edge.class)
	@XmlTransient
	protected List<Edge> incoming;

	// @XmlIDREF
	// @XmlSchemaType(name = "IDREF")
	// @XmlElement(name = "outgoing", type = Edge.class)
	@XmlTransient
	protected List<Edge> outgoing;

	/* The process the element belongs to */
	// @XmlIDREF
	// @XmlAttribute
	// @XmlSchemaType(name = "IDREF")
	@XmlTransient
	protected Process process;

	@XmlTransient
	protected SubProcess subProcess;
	@XmlTransient
	protected SubChoreography subChoreography;

	/**
	 * Default constructor
	 */
	public FlowElement() {

	}

	/**
	 * Copy constructor
	 */
	public FlowElement(FlowElement flowEl) {
		super(flowEl);

		if (flowEl.getCategoryValue().size() > 0)
			this.getCategoryValue().addAll(flowEl.getCategoryValue());

		if (flowEl.getIncoming().size() > 0)
			this.getIncoming().addAll(flowEl.getIncoming());

		if (flowEl.getOutgoing().size() > 0)
			this.getOutgoing().addAll(flowEl.getOutgoing());

		this.setAuditing(flowEl.getAuditing());
		this.setMonitoring(flowEl.getMonitoring());

		this.setProcess(flowEl.getProcess());
		this.setName(flowEl.getName());
	}

	/**
	 * Another helper for the import. If the element is of fixed size, then it
	 * may have to be adjusted after import from other tools.
	 */
	public boolean isElementWithFixedSize() {
		return false;
	}

	/**
	 * Helper method for import of objects that are fixed-size in the editor,
	 * but not in external tools. The bounds are set so that the object is in
	 * the middle of the too-large / too-small original bounds.
	 */

	public static Bounds getMiddleBounds(double width, double height,
			Bounds bounds) {
		Bounds returnbounds = new Bounds(new Point(0.0, 0.0), new Point(0.0,
				0.0));

		// Differences of wished-for height / width and shape's current height /
		// width.
		// If these are positive, I have to shrink the bounds towards the middle
		// point, otherwise extend them. Yet, this is done automatically, as the
		// differcence may be negative :).
		
		double xdifference = bounds.getLowerRight().getX()
				- bounds.getUpperLeft().getX() - width;
		double ydifference = bounds.getLowerRight().getY()
				- bounds.getUpperLeft().getY() - height;

		returnbounds.getUpperLeft().setX(
				bounds.getUpperLeft().getX() + xdifference / 2);
		returnbounds.getLowerRight().setX(
				bounds.getLowerRight().getX() - xdifference / 2);

		returnbounds.getUpperLeft().setY(
				bounds.getUpperLeft().getY() + ydifference / 2);
		returnbounds.getLowerRight().setY(
				bounds.getLowerRight().getY() - ydifference / 2);

		return returnbounds;
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
	public Shape toShape(BPMN2DiagramConverterI converterForShapeCoordinateLookup) {
		Shape shape = super.toShape(converterForShapeCoordinateLookup);

		shape.putProperty("name",
				(this.getName() != null ? this.getName() : ""));

		for (Edge edge : this.getOutgoing()) {
			shape.getOutgoings().add(new Shape(edge.getId()));
		}

		// boolean debug = false;

		// do the containment stuff > child shape of a subprocess, or a lane

		if (this.getSubProcess() != null) {

			Shape parent;

			// [BPMN2.0] TODO CONVENTION: if the subprocess is collapsed,
			// currently ignore it
			BPMNShape bpmnShape = converterForShapeCoordinateLookup
					.getBpmnShapeByID(this.getSubProcess().getId());

			// CONVENTION default: expanded
			if (bpmnShape.isIsExpanded() != null && !bpmnShape.isIsExpanded()) {
				parent = new Shape("");
				parent.putProperty("isInCollapsedSubprocess", "true");
			} else {
				parent = converterForShapeCoordinateLookup
						.getEditorShapeByID(this.getSubProcess().getId());
			}
			parent.getChildShapes().add(shape);
			shape.setParent(parent);

			// if(debug)System.out.println("$ Added shape as child of subprocess: "
			// + this.toString());
		} else if (this.getSubChoreography() != null) {
			// analog to the above
			Shape parent;

			// [BPMN2.0] TODO CONVENTION: if the subprocess is collapsed,
			// currently ignore it
			BPMNShape bpmnShape = converterForShapeCoordinateLookup
					.getBpmnShapeByID(this.getSubChoreography().getId());

			// CONVENTION default: expanded
			if (bpmnShape.isIsExpanded() != null && !bpmnShape.isIsExpanded()) {
				parent = new Shape("");
				parent.putProperty("isInCollapsedSubprocess", "true");
			} else {
				parent = converterForShapeCoordinateLookup
						.getEditorShapeByID(this.getSubChoreography().getId());
			}
			parent.getChildShapes().add(shape);
			shape.setParent(parent);
		} else {

			// this stuff is not supported by the standard! I have to work
			// around it!
			/*
			 * if(this.getLane() != null) { //add as child Shape parent =
			 * converterForShapeCoordinateLookup
			 * .getEditorShapeByID(this.getLane().getId());
			 * parent.getChildShapes().add(shape);
			 * 
			 * } else{
			 * 
			 * if(this.getProcess()!= null){ if(this.getProcess().getPool() !=
			 * null){ Shape parent =
			 * converterForShapeCoordinateLookup.getEditorShapeByID
			 * (this.getProcess().getPool().getId());
			 * parent.getChildShapes().add(shape); System.out.println(
			 * "NO LANE NOR SUBPROCESS, but a process, and a pool in it..."); }
			 * else
			 * System.out.println("NO LANE NOR SUBPROCESS, but a process..."); }
			 * else System.out.println("NO LANE NOR SUBPROCESS!");
			 * 
			 * 
			 * }
			 */
			Shape parent = converterForShapeCoordinateLookup
					.getMyParentLaneOrParticipantShape(shape.getResourceId());
			if (parent != null) {
				parent.getChildShapes().add(shape);
				shape.setParent(parent);
				// if(debug)
				// System.out.println("## Added shape as child of a lane or participant: "
				// + this.toString());
			} else {
				// may already have a parent!
				// if(debug)System.out.println("=== Shape to be drawn on pure canvas: "
				// + this.toString());
			}
		}

		// highly unclear; CONVENTION use the first documentation string
		if (this.getAuditing() != null) {
			if (this.getAuditing().toExportString() != null)
				shape.putProperty("auditing", this.getAuditing()
						.toExportString());
		}

		if (this.getMonitoring() != null) {
			if (this.getMonitoring().toExportString() != null)
				shape.putProperty("monitoring", this.getMonitoring()
						.toExportString());
		}

		return shape;
	}

	public void afterUnmarshal(Unmarshaller u, Object parent) {
		if (parent != null && parent instanceof SubProcess) {
			this.subProcess = (SubProcess) parent;
		}

		if (parent != null && parent instanceof SubChoreography) {
			this.subChoreography = (SubChoreography) parent;
		}
	}

	/* Getter & Setter */

	/**
	 * Gets the value of the incoming property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the incoming property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getIncoming().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link QName }
	 * 
	 * 
	 */
	public List<Edge> getIncoming() {
		if (incoming == null) {
			incoming = new ArrayList<Edge>();
		}
		return this.incoming;
	}

	/**
	 * Gets the value of the outgoing property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the outgoing property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getOutgoing().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link QName }
	 * 
	 * 
	 */
	public List<Edge> getOutgoing() {
		if (outgoing == null) {
			outgoing = new ArrayList<Edge>();
		}
		return this.outgoing;
	}

	/**
	 * Gets the value of the auditing property.
	 * 
	 * @return possible object is {@link Auditing }
	 * 
	 */
	public Auditing getAuditing() {
		return auditing;
	}

	/**
	 * Sets the value of the auditing property.
	 * 
	 * @param value
	 *            allowed object is {@link Auditing }
	 * 
	 */
	public void setAuditing(Auditing value) {
		this.auditing = value;
	}

	/**
	 * Gets the value of the monitoring property.
	 * 
	 * @return possible object is {@link Monitoring }
	 * 
	 */
	public Monitoring getMonitoring() {
		return monitoring;
	}

	/**
	 * Sets the value of the monitoring property.
	 * 
	 * @param value
	 *            allowed object is {@link Monitoring }
	 * 
	 */
	public void setMonitoring(Monitoring value) {
		this.monitoring = value;
	}

	/**
	 * Gets the value of the categoryValue property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the categoryValue property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getCategoryValue().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link QName }
	 * 
	 * 
	 */
	public List<QName> getCategoryValue() {
		if (categoryValue == null) {
			categoryValue = new ArrayList<QName>();
		}
		return this.categoryValue;
	}

	/**
	 * Gets the value of the name property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the process
	 */
	public Process getProcess() {
		return process;
	}

	/**
	 * @param process
	 *            the process to set
	 */
	public void setProcess(Process process) {
		this.process = process;
	}

	/**
	 * Sets the value of the name property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setName(String value) {
		this.name = value;
	}

	public SubProcess getSubProcess() {
		return subProcess;
	}

	public void setSubProcess(SubProcess subProcess) {
		this.subProcess = subProcess;
	}

	public SubChoreography getSubChoreography() {
		return subChoreography;
	}

	public void setSubChoreography(SubChoreography subChoreography) {
		this.subChoreography = subChoreography;
	}
}
