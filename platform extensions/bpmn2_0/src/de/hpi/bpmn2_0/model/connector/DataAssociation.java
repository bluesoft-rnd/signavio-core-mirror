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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.oryxeditor.server.diagram.Shape;
import org.oryxeditor.server.diagram.StencilType;

import de.hpi.bpmn2_0.model.FlowElement;
import de.hpi.bpmn2_0.model.FormalExpression;
import de.hpi.bpmn2_0.model.misc.Assignment;

import de.hpi.bpmn2_0.transformation.BPMN2DiagramConverterI;

/**
 * <p>
 * Java class for tDataAssociation complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="tDataAssociation">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/bpmn20}tBaseElement">
 *       &lt;sequence>
 *         &lt;element name="transformation" type="{http://www.omg.org/bpmn20}tFormalExpression" minOccurs="0"/>
 *         &lt;element ref="{http://www.omg.org/bpmn20}assignment" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tDataAssociation", propOrder = { "transformation",
		"assignment", "sourceRefList", "targetRefList" })
@XmlSeeAlso({ DataInputAssociation.class, DataOutputAssociation.class })
public class DataAssociation extends Edge {
	@XmlIDREF
	@XmlSchemaType(name = "IDREF")
	@XmlElement(type = FlowElement.class, name = "sourceRef")
	public List<FlowElement> sourceRefList;

	@XmlIDREF
	@XmlSchemaType(name = "IDREF")
	@XmlElement(type = FlowElement.class, name = "targetRef")
	public List<FlowElement> targetRefList;

	@XmlTransient
	protected FlowElement parent;

	@XmlElement
	protected FormalExpression transformation;
	@XmlElement
	protected List<Assignment> assignment;

	public void afterUnmarshal(Unmarshaller u, Object parent) {
		if (parent != null && parent instanceof FlowElement) {
			this.parent = (FlowElement) parent;
		}
	}

	/**
	 * The {@link Marshaller} invokes this method right before marshaling to
	 * XML. It secures that sourceRefList and targetRefList only contains a
	 * maximum of one element.
	 * 
	 * @param marshaller
	 *            The marshaling context
	 */
	public void beforeMarshal(Marshaller marshaller) {
		/*
		 * Check sourceRef
		 */
		if (sourceRefList != null && sourceRefList.size() > 1) {
			FlowElement firstEle = sourceRefList.get(0);
			sourceRefList = new ArrayList<FlowElement>();
			sourceRefList.add(firstEle);
		}

		/*
		 * Check targetRef
		 */
		if (targetRefList != null && targetRefList.size() > 1) {
			FlowElement firstEle = targetRefList.get(0);
			targetRefList = new ArrayList<FlowElement>();
			targetRefList.add(firstEle);
		}
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
	 *         used for the output.
	 */
	public Shape toShape(BPMN2DiagramConverterI converterForShapeCoordinateLookup) {
		Shape shape = super.toShape(converterForShapeCoordinateLookup);

		shape.setStencil(new StencilType("Association_Undirected"));

		String sourceObject = this.getSourceRef().getId();
		String targetObject = this.getTargetRef().getId();

		// add to source as outgoing
		Shape s = converterForShapeCoordinateLookup
				.getEditorShapeByID(sourceObject);
		if (s == null) {
			s = converterForShapeCoordinateLookup.newShape(sourceObject);
		}
		s.addOutgoing(new Shape(this.getId()));

		// ..or, as ingoing :)
		Shape s2 = converterForShapeCoordinateLookup
				.getEditorShapeByID(targetObject);
		if (s2 == null) {
			s2 = converterForShapeCoordinateLookup.newShape(targetObject);
		}
		s2.addIncoming(new Shape(this.getId()));

		// shape.putProperty("", );

		/*
		 * "properties" : { "assignments" : { "totalCount" : 3, "items" : [ {
		 * "to" : "10", "from" : "", "language" : "en" }, { "to" : "", "from" :
		 * "5", "language" : "de" }, { "to" : "1", "from" : "2", "language" :
		 * "fr" } ] }, "transformation" : "oaeioaieoai", "text" : "einname\n"
		 * },...
		 */
		
		if(this.getAssignment().size() != 0){
			shape.putProperty("assignments", this.getAssignmentString());
			System.out.println("assignments : " + shape.getProperty("assignments"));
		}
		
		if(this.getTransformation() != null)
			shape.putProperty("transformation", this.getTransformation().toExportString());
		
		//DataAssociation does not have a name property, it seems. Importing this as the "text" property
		if(this.getName() != null)
			shape.putProperty("text", this.getName());
		
		return shape;
	}

	private String getAssignmentString() {
		
		int count = this.getAssignment().size();
//		if (count == 0)
//			return "";
		String answerstring = "{ \"totalCount\" : "
				+ Integer.toString(count) + ", \"items\" : [";
		for (Assignment a : this.getAssignment()) {
			answerstring += "{ \"to\" : \""
					+ (a.getTo() == null ? "" : a.getTo())
					+ "\", \"from\" : \""
					+ (a.getFrom() == null ? "" : a.getFrom())
					+ "\", \"language\" : \""
					+ (a.getLanguage() == null ? "" : a.getLanguage())
					+ "\"},";
		}
		answerstring = answerstring.substring(0, answerstring.length() - 1);
		answerstring += "]}";
	
		return answerstring;
	}

	/* Getter & Setter */

	/**
	 * Gets the value of the transformation property.
	 * 
	 * @return possible object is {@link TFormalExpression }
	 * 
	 */
	public FormalExpression getTransformation() {
		return transformation;
	}

	/**
	 * Sets the value of the transformation property.
	 * 
	 * @param value
	 *            allowed object is {@link FormalExpression }
	 * 
	 */
	public void setTransformation(FormalExpression value) {
		this.transformation = value;
	}

	/**
	 * Gets the value of the assignment property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the assignment property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getAssignment().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link TAssignment }
	 * 
	 * 
	 */
	public List<Assignment> getAssignment() {
		if (assignment == null) {
			assignment = new ArrayList<Assignment>();
		}
		return this.assignment;
	}

	public FlowElement getSourceRef() {
		if (sourceRefList != null && sourceRefList.size() > 0) {
			return sourceRefList.get(0);
		}
		return null;
	}

	public void setSourceRef(FlowElement sourceRef) {
		if (sourceRefList == null) {
			sourceRefList = new ArrayList<FlowElement>();
		}
		sourceRefList.add(0, sourceRef);
	}

	public FlowElement getTargetRef() {
		if (targetRefList != null && targetRefList.size() > 0) {
			return targetRefList.get(0);
		}
		return null;
	}

	public void setTargetRef(FlowElement targetRef) {
		if (targetRefList == null) {
			targetRefList = new ArrayList<FlowElement>();
		}
		targetRefList.add(0, targetRef);
	}
}
