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

package de.hpi.bpmn2_0.model.participant;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

import org.oryxeditor.server.diagram.Point;
import org.oryxeditor.server.diagram.Shape;
import org.oryxeditor.server.diagram.StencilType;

import de.hpi.bpmn2_0.model.BaseElement;
import de.hpi.bpmn2_0.model.FlowNode;
import de.hpi.bpmn2_0.model.Process;
import de.hpi.bpmn2_0.model.bpmndi.BPMNShape;
import de.hpi.bpmn2_0.model.bpmndi.dc.Bounds;
import de.hpi.bpmn2_0.model.conversation.ConversationElement;

import de.hpi.bpmn2_0.transformation.BPMN2DiagramConverterI;
import de.hpi.diagram.SignavioUUID;


/**
 * <p>Java class for tParticipant complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tParticipant">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/bpmn20}tBaseElement">
 *       &lt;sequence>
 *         &lt;element name="interfaceRef" type="{http://www.w3.org/2001/XMLSchema}QName" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="endPointRef" type="{http://www.w3.org/2001/XMLSchema}QName" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.omg.org/bpmn20}participantMultiplicity" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="partnerRoleRef" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *       &lt;attribute name="partnerEntityRef" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *       &lt;attribute name="processRef" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tParticipant", propOrder = {
    "interfaceRef",
    "endPointRef",
    "participantMultiplicity"
})
public class Participant
    extends FlowNode implements ConversationElement
{

    @XmlElement
	protected List<QName> interfaceRef;
    @XmlElement
    protected List<QName> endPointRef;
    @XmlElement(type = ParticipantMultiplicity.class)
    protected ParticipantMultiplicity participantMultiplicity;
    
    @XmlAttribute
    @XmlIDREF
    protected Process processRef;
    
    @XmlAttribute
    protected QName partnerRoleRef;
    @XmlAttribute
    protected QName partnerEntityRef;
    
    @XmlTransient
    protected boolean isInitiating;
    
    @XmlTransient
    private LaneSet laneSet;
    
    @XmlTransient
	public String _processType;
	@XmlTransient
	public String _isClosed;
	@XmlTransient
	public String _isExecutable;
	@XmlTransient
	public boolean _isChoreographyParticipant = false;
    
    /*
     * Constructors
     */
	
	/**
	 * Default constructor
	 */
	public Participant() {
		super();
	}
	
	/**
	 * Copy constructor
	 * 
	 * @param p 
	 * 		template {@link Participant}
	 */
    public Participant(Participant p) {
		super(p);
		
		this.getInterfaceRef().addAll(p.getInterfaceRef());
		this.getEndPointRef().addAll(p.getEndPointRef());
		
		this.setParticipantMultiplicity(p.getParticipantMultiplicity());
		this.setProcessRef(p.getProcessRef());
		
		this.setPartnerRoleRef(p.getPartnerRoleRef());
		this.setPartnerEntityRef(p.getPartnerEntityRef());
		
		this.setInitiating(p.isInitiating());
		this.setLaneSet(p.getLaneSet());
		
		this._processType = p._processType;
		this._isClosed = p._isClosed;
		this._isExecutable = p._isExecutable;
		this._isChoreographyParticipant = p._isChoreographyParticipant;
	}
    
    /* Business logic methods */

	// @Override
    public void addChild(BaseElement child) {
    	if(child instanceof Lane) {
    		if(laneSet == null) {
    			laneSet = new LaneSet();
    			laneSet.setId(SignavioUUID.generate());
    		}
    		
    		getLaneSet().addChild(child);
    	}
    }
    
    /**
     * Helper method, in order to prevent repetition.
     * @param s a BPMNShape needed to set some properties of s.
     * @param shape the Shape related to s.
     */
    private void helper(BPMNShape s, Shape shape){

    	shape.setStencil(new StencilType("ChoreographyParticipant"));
    	
    	//BPMNShape: isMessageVisible
    	
		//no official property!
		if(s.isIsMessageVisible()){ //!= null && s.isIsMessageVisible()){
			shape.putProperty("messagevisible", "true");
		}else{
			shape.putProperty("messagevisible", "false");
		}
		
		/*
		if(this.isInitiating()){
			shape.putProperty("initiating", "true");
		}
		else{
			shape.putProperty("initiating", "false");
		}
		*/
		
		//BPMNShape: participantBandKind (bottom_non_initiating, middle_initiating, top_non_initiating, ...)
		if(s.getParticipantBandKind().isInitiating()){
			shape.putProperty("initiating", "true");
			
			//workaround for color issue
			shape.putProperty("color", "#ffffff");
			//this.setInitiating(true);
		}
		else{
			shape.putProperty("initiating", "false");
			shape.putProperty("color", "#acacac");
			//this.setInitiating(false);
		}
				
		//set initial bounds
		Bounds sb = s.getBounds();
		org.oryxeditor.server.diagram.Bounds shapeb = 
			new org.oryxeditor.server.diagram.Bounds(
					new Point(sb.getX() + sb.getWidth(), sb.getY() + sb.getHeight()), 
					new Point(sb.getX(), sb.getY())); 
		//shape.setBounds(shapeb);
		
		de.hpi.bpmn2_0.model.bpmndi.dc.Bounds parentbounds = s.getChoreographyActivityShape().getBounds();
		
		shape.setBounds(boundsAdjustment(shapeb, parentbounds));
		
		if(this.getParticipantMultiplicity() != null){
        	if(this.getParticipantMultiplicity().getMaximum() > 1)
        		shape.putProperty("multiple_instance", "true");
        	else
            	shape.putProperty("multiple_instance", "false");
        }
        else
        	shape.putProperty("multiple_instance", "false");
		
	}
    
    /**
     * {@see BPMNShape#boundsAdjustment(...)}
     * @return
     */
    private static org.oryxeditor.server.diagram.Bounds boundsAdjustment(org.oryxeditor.server.diagram.Bounds childBounds, de.hpi.bpmn2_0.model.bpmndi.dc.Bounds parentBounds){
		return new org.oryxeditor.server.diagram.Bounds(
				new Point(
						childBounds.getLowerRight().getX() - (parentBounds.getX()), 
						childBounds.getLowerRight().getY() - (parentBounds.getY())
						),
				new Point(
						childBounds.getUpperLeft().getX() - (parentBounds.getX()), 
						childBounds.getUpperLeft().getY() - (parentBounds.getY())
						)
				);
		
	}
    
    
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
		
		if(converterForShapeCoordinateLookup.isChoreography()){
			
			BPMNShape s = converterForShapeCoordinateLookup.getBpmnShapeByID(this.getId());
			
			if(s == null){
				//there is a doppelgänger of this participant instance for other choreography activities!
				//(inside different participant bands)
				List<BPMNShape> shapelist = converterForShapeCoordinateLookup.getDoublesForId(this.getId());

				//now, process all, delete them from the list and leave a dummy
				if(shapelist == null){
					//System.out.println("Warning: Import of choreography diagram might be endangered...");
				}
				else if(shapelist.size() == 0){
					//System.out.println("Already processed " + this.getId());
				}
				else{
					//System.out.println("Processing participant " + this.getId());
					//process all instances
					/*
					<bpmndi:BPMNShape bpmnElement="_6_P1275674668569" choreographyActivityShape="Trisotech.Visio__6_ChoreographyTask_CT2" isMessageVisible="true" participantBandKind="bottom_initiating" id="P1275674669275">
		                <dc:Bounds height="20.0" width="96.0" x="312.0" y="190.0"/>
		                <bpmndi:BPMNLabel/>
		            </bpmndi:BPMNShape>
					 */
					for(BPMNShape bpmnshape : shapelist){
						//has to be set and present
						BPMNShape x = bpmnshape.getChoreographyActivityShape();
						Shape parent = converterForShapeCoordinateLookup.getEditorShapeByID(
									(x.getBpmnElement().getId()));
						if(parent == null){
							parent = converterForShapeCoordinateLookup.newShape(
									(x.getBpmnElement().getId()));
						}
						
						//create a new Shape with a new id, set as child of the parent shape
						Shape child = converterForShapeCoordinateLookup.newShape(bpmnshape.getId());
						
						
						child.putProperty("name", this.getName());
						
						helper(bpmnshape, child);
						parent.getChildShapes().add(child);
						child.setParent(parent);
						
						// dummy to return >> no, do sth else ...??
						//see below: returning null
						//shape = child;
					}
					
					shapelist.clear();
				}
				
				return null;
			}
			else{
				
				
				Shape parent = converterForShapeCoordinateLookup.getEditorShapeByID(s.getChoreographyActivityShape().getBpmnElement().getId());
				if(parent == null){
					parent = converterForShapeCoordinateLookup.newShape(s.getChoreographyActivityShape().getBpmnElement().getId());
				}
				
				helper(s, shape);
				
				parent.getChildShapes().add(shape);
				shape.setParent(parent);
				
			}
			
		} else if(converterForShapeCoordinateLookup.isConversation()){
			shape.setStencil(new StencilType("Participant"));
			
		} else{

			//as indicated by Sven:
			if(this.getProcessRef() == null){
				shape.setStencil(new StencilType("CollapsedPool"));
			} else
				shape.setStencil(new StencilType("Pool"));
			
			/*
			if(this.getLaneSet() == null){
				System.out.println("Participant without lanes!");
				
				LaneSet laneset = new LaneSet();
				laneset.setProcess(this.getProcess());
				laneset.setLane(new Lane());
				
				
				this.setLaneSet(laneset);
				
			}
			*/
			
    	}
		if(this.getParticipantMultiplicity() != null){
        	if(this.getParticipantMultiplicity().getMaximum() != null && this.getParticipantMultiplicity().getMaximum() > 1)
        		shape.putProperty("multiinstance", "true");
        	else
            	shape.putProperty("multiinstance", "false");
        }
        else
        	shape.putProperty("multiinstance", "false");
        
		//whether it is extensible at runtime - setting a default true (not extensible) here
		shape.putProperty("isclosed", "true");
        
		return shape;
	}
    
    
    /* Getter & Setter */
    /**
     * Gets the value of the interfaceRef property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the interfaceRef property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getInterfaceRef().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link QName }
     * 
     * 
     */
    public List<QName> getInterfaceRef() {
        if (interfaceRef == null) {
            interfaceRef = new ArrayList<QName>();
        }
        return this.interfaceRef;
    }

    /**
	 * @return the processRef
	 */
	public Process getProcessRef() {
		return this.processRef;
	}

	/**
	 * @param processRef the processRef to set
	 */
	public void setProcessRef(Process processRef) {
		this.processRef = processRef;
	}

	/**
     * Gets the value of the endPointRef property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the endPointRef property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEndPointRef().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link QName }
     * 
     * 
     */
    public List<QName> getEndPointRef() {
        if (endPointRef == null) {
            endPointRef = new ArrayList<QName>();
        }
        return this.endPointRef;
    }

    /**
     * Gets the value of the participantMultiplicity property.
     * 
     * @return
     *     possible object is
     *     {@link ParticipantMultiplicity }
     *     
     */
    public ParticipantMultiplicity getParticipantMultiplicity() {
        return participantMultiplicity;
    }

    /**
     * Sets the value of the participantMultiplicity property.
     * 
     * @param value
     *     allowed object is
     *     {@link ParticipantMultiplicity }
     *     
     */
    public void setParticipantMultiplicity(ParticipantMultiplicity value) {
        this.participantMultiplicity = value;
    }

    /**
	 * @return the isInitiating
	 */
	public boolean isInitiating() {
		return isInitiating;
	}

	/**
	 * @param isInitiating the isInitiating to set
	 */
	public void setInitiating(boolean isInitiating) {
		this.isInitiating = isInitiating;
	}

    /**
     * Gets the value of the partnerRoleRef property.
     * 
     * @return
     *     possible object is
     *     {@link QName }
     *     
     */
    public QName getPartnerRoleRef() {
        return partnerRoleRef;
    }

    /**
     * Sets the value of the partnerRoleRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link QName }
     *     
     */
    public void setPartnerRoleRef(QName value) {
        this.partnerRoleRef = value;
    }

    /**
     * Gets the value of the partnerEntityRef property.
     * 
     * @return
     *     possible object is
     *     {@link QName }
     *     
     */
    public QName getPartnerEntityRef() {
        return partnerEntityRef;
    }

    /**
     * Sets the value of the partnerEntityRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link QName }
     *     
     */
    public void setPartnerEntityRef(QName value) {
        this.partnerEntityRef = value;
    }

	public LaneSet getLaneSet() {
		return laneSet;
	}

	public void setLaneSet(LaneSet laneSet) {
		this.laneSet = laneSet;
	}
}
