/**
 * Copyright (c) 2010

 * Philipp Giese, Sven Wagner-Boysen, Robert Gurol
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

package de.hpi.bpmn2_0.transformation;

//import java.util.ArrayList;
//import java.util.Arrays;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.oryxeditor.server.diagram.Bounds;
//import org.oryxeditor.server.diagram.Diagram;
//import org.oryxeditor.server.diagram.Point;
//import org.oryxeditor.server.diagram.Shape;
//import org.oryxeditor.server.diagram.StencilSet;
//import org.oryxeditor.server.diagram.StencilType;
//
//import de.hpi.bpmn2_0.model.*;
//import de.hpi.bpmn2_0.model.Process;
//
//import de.hpi.bpmn2_0.model.bpmndi.*;
//import de.hpi.bpmn2_0.model.bpmndi.di.DiagramElement;
//import de.hpi.bpmn2_0.model.choreography.Choreography;
//import de.hpi.bpmn2_0.model.choreography.ChoreographyTask;
//import de.hpi.bpmn2_0.model.choreography.SubChoreography;
//import de.hpi.bpmn2_0.model.conversation.CallConversation;
//import de.hpi.bpmn2_0.model.conversation.ConversationLink;
//import de.hpi.bpmn2_0.model.conversation.ConversationNode;
//import de.hpi.bpmn2_0.model.conversation.SubConversation;
//import de.hpi.bpmn2_0.model.participant.Lane;
//import de.hpi.bpmn2_0.model.participant.LaneSet;
//import de.hpi.bpmn2_0.model.participant.Participant;
//import de.hpi.diagram.SignavioUUID;

/**
 * Converter that transforms a {@link Definition} to a {@link Diagram}.
 *  
 * @author Sven Wagner-Boysen, Robert Gurol
 *
 */
@Deprecated
public class BPMN2DiagramConverter {
//	
//	//private String rootDir;
//	
//	/* will be used by edges connecting two nodes, because the edge's coordinates are relative to 
//	 * their upperLeft boundary*/
//	
////	/**
////	 *  id > DiagramElement
////	 */
////	private Map<String, DiagramElement> diagramElementMap;
//	
//	/**
//	 *  id > BPMNShape
//	 */
//	private Map<String, BPMNShape> bpmnShapeMap;
//	
//	/**
//	 * id > BPMNEdge
//	 */
//	private Map<String, BPMNEdge> bpmnEdgeMap;
//	
//	/**
//	 * id > List of participant instance BPMNShape:s (for conversation)
//	 */
//	private Map<String, List<BPMNShape>> doublesMap;
//	
//	// BPMNShape's id (not, as before, of referenced bpmnelement) > BPMNShape 
//	//private Map<String, BPMNShape> choreographyBpmnShapeMap;
//	
//	//private Map<String, String> bpmnShapeIdsToBpmnElementIdsMap;
//	
//	/* will be used for generating the hierarchichal structure of the shapes*/
//	// BPMNShape / BPMNEdge 's Id > Lane or Participant (pool or lane)
//	private Map<String, FlowElement> laneMap;
//	//Shapes apart from pools that are to be painted directly on the canvas
//	//>> Unnecessary! Using the parent attribute for it
//	//List<String> globalShapes;
//	
//	//shapes that are already done
//	private Map<String, Shape> editorShapeMap;
//	
//	// as in {@link Diagram2BpmnConverter}
////	private static final List<String> edgeIds = Arrays.asList("SequenceFlow",
////			"Association_Undirected", "Association_Unidirectional",
////			"Association_Bidirectional", "MessageFlow", "ConversationLink");
////	
//	
//	//if a shape's bpmnElement is in one of these lists, the respective flags (isConversation / isChoreography) are set.
//	@SuppressWarnings("unchecked")
//	private static final List<?>  conversationClasses = Arrays.asList(ConversationNode.class, CallConversation.class, ConversationNode.class, SubConversation.class, ConversationLink.class);
//	@SuppressWarnings("unchecked")
//	private static final List<?>  choreographyClasses = Arrays.asList(ChoreographyTask.class, SubChoreography.class);
//	
//	//flags set during parsing, indicating the type of stencil set to be used later
//	private boolean isConversation;
//	private boolean isChoreography;
//	
//	// unnecessary
////	private boolean hasNoPool;
//	// root of tree of objects, JAXB-generated from the .bpmn xml file  
//	
//	//for canvas size of the diagram to be created
//	private double furthest_x;
//	private double furthest_y;
//	
//	//private Definitions definitions;
//	
//	/**
//	 * Constructor.  
//	 */
//	public BPMN2DiagramConverter(){
//		super();
//	}
//	
//	/**
//	 * Constructor. In the future, it shall be passed a directory, and convert the complete content. This has not yet been unlocked in the testing phase. 
//	 */
//	public BPMN2DiagramConverter(String rootDir) {
//		//this.rootDir = rootDir;
//		this();
//	}
//	
//	
//	/*
//	public BPMN2DiagramConverter(String path, String filename) throws Exception{
//		//using code from serialization test
//		File xml = new File(path + filename);
//		JAXBContext context = JAXBContext.newInstance(Definitions.class);
//		Unmarshaller unmarshaller = context.createUnmarshaller();
//		this.definitions = (Definitions) unmarshaller.unmarshal(xml);
//		
//		//BPMN2DiagramConverter converter = new BPMN2DiagramConverter("/oryx/");
//	}
//	*/
//	
//	/**
//	 * Init of local variables. Triggers the other initialization methods. 
//	 */
//	private void my_init(Definitions definitions){
//
//		//this.diagramElementMap = new HashMap<String, DiagramElement>();
//		this.bpmnShapeMap= new HashMap<String, BPMNShape>();
//		this.bpmnEdgeMap = new HashMap<String, BPMNEdge>();
//		this.laneMap = new HashMap<String, FlowElement>();  
//		this.editorShapeMap = new HashMap<String, Shape>();
//		//this.globalShapes = new ArrayList<String>();
//		this.doublesMap = new HashMap<String, List<BPMNShape>>(); 
//		//this.choreographyBpmnShapeMap = new HashMap<String, BPMNShape>();
//		//bpmnShapeIdsToBpmnElementIdsMap = new HashMap<String, String>();
//		
//		this.isChoreography = false;
//		this.isConversation = false;
////		this.hasNoPool = true;
//		
//		this.furthest_x = 0;
//		this.furthest_y = 0;
//		
//		initializeBpmnShapeLookup(definitions);
//		initializePoolStructure(definitions);
//	}
//	
//	/**
//	 * Initialization of BPMNShape lookup (i.e., forms the foundations of {@link BPMN2DiagramConverter#getBpmnEdgeByID(String)} and {@link BPMN2DiagramConverter#getBpmnShapeByID(String)}.
//	 * ASSUMPTION: keys unique within one XML document. (Exception to this is the instantiation of participants during choreographies; this has been cared for).
//	 */
//	private void initializeBpmnShapeLookup(Definitions definitions){
//
//		List<BPMNDiagram> bpmnDiags = definitions.getDiagram();
//		for(BPMNDiagram  bpmnDiagram : bpmnDiags){ 
//			List<DiagramElement> des = bpmnDiagram.getBPMNPlane().getDiagramElement();
//			
//			for(DiagramElement de : des){				
//				if(de instanceof BPMNShape){
//					if(this.bpmnShapeMap.containsKey(((BPMNShape) de).getBpmnElement().getId())){
//						//System.out.println("Key present: " + ((BPMNShape) de).getBpmnElement().getId());
//						//this may happen for choreographies, where one participant may reoccur in different roles
//						//Idea: make a list with pairs of the participant key and the BPMNShape
//						String key = ((BPMNShape) de).getBpmnElement().getId();
//						
//						if(!this.doublesMap.containsKey(key)){
//							this.doublesMap.put(key, new ArrayList<BPMNShape>());
//						}
//						
//						
//						List<BPMNShape> l = this.doublesMap.get(key);
//						l.add((BPMNShape) de);
//						if(!l.contains(this.bpmnShapeMap.get(key))){
//							l.add(this.bpmnShapeMap.get(key));
//						}
//						
//					}
//					
//					this.bpmnShapeMap.put(((BPMNShape) de).getBpmnElement().getId(), (BPMNShape)de);
//					//this.bpmnShapeIdsToBpmnElementIdsMap.put(de.getId(), ((BPMNShape) de).getBpmnElement().getId());
//					
//					if (choreographyClasses.contains(((BPMNShape) de).getBpmnElement().getClass())){
//						this.isChoreography = true;
//					}
//					
////					if( (((BPMNShape) de).getBpmnElement()) instanceof Participant){
////						this.hasNoPool = false;
////					}
//					
//					if (conversationClasses.contains(((BPMNShape) de).getBpmnElement().getClass())){
//						this.isConversation = true;
//					}
//					
//					de.hpi.bpmn2_0.model.bpmndi.dc.Bounds b = ((BPMNShape) de).getBounds();
//					if (b.getX() + b.getWidth() > this.furthest_x)
//						this.furthest_x = b.getX() + b.getWidth();
//					
//					if(b.getY() - b.getHeight() > this.furthest_y) 
//						this.furthest_y = b.getY() + b.getHeight();
//				}
//				if(de instanceof BPMNEdge){
//					/*
//					if(this.bpmnEdgeMap.containsKey(((BPMNEdge) de).getBpmnElement().getId()))
//						System.out.println("Key present: " + ((BPMNEdge) de).getBpmnElement().getId());
//					*/
//					
//					this.bpmnEdgeMap.put(((BPMNEdge) de).getBpmnElement().getId(), (BPMNEdge)de);
//					
//					/*
//					if (choreographyClasses.contains(((BPMNEdge) de).getBpmnElement().getClass())){
//						this.isChoreography = true;
//					}
//					*/
//					
//					if (conversationClasses.contains(((BPMNEdge) de).getBpmnElement().getClass())){
//						this.isConversation = true;
//					}
//					
//					//adjust total canvas size
//					for(de.hpi.bpmn2_0.model.bpmndi.dc.Point p : ((BPMNEdge) de).getWaypoint()){
//						if(p.getX() > this.furthest_x) this.furthest_x = p.getX();
//						if(p.getY() > this.furthest_y) this.furthest_y = p.getY();
//					}	
//				}
//				
//				//will not work... the map is to be removed
//				//this.diagramElementMap.put(de.getId(), de);				
//			}	
//		}
//		
//		//remove doubles from the normal lookup
//		for(String s : this.doublesMap.keySet()){
//			this.bpmnShapeMap.remove(s);
//		}
//		
//	}
//	
//	/**
//	 * Initialization of Pool and Lane elements, used for the correct transformation of hierarchichal BPMN processes.
//	 */
//	private void initializePoolStructure(Definitions definitions){
//
//		List<RootElement> rootElements = definitions.getRootElement();
//		
//		//goal: get list: process (participant) / lane for a given shape id
//		//Map<String, FlowElement> laneMap = new HashMap<String, FlowElement>(); //use this.laneMap instead!
//		
//		//first: get collaboration elements, and identify the pools (process objects, listed there as participants
//		
//		List<Process> processes = new ArrayList<Process>();
//		Map<String, Participant> participantMap = new HashMap<String, Participant>();
//		
//		for(RootElement re : rootElements){
//			
//			//in case of choreography and conversation, the inclusion relations should be given as subprocess relations, so it is not necessary to do extra stuff...?
//			if(re instanceof Choreography){
//				this.isChoreography = true;
//			}
//			else if(re instanceof Process){
//				
//				processes.add((Process) re);
//				
//			}
//			else if(re instanceof Collaboration){
//				//todo: can be a collaboration between processes (participants) in a collaboration diagram, or between participants in a conversation!
//				//in the latter case, there is no processRef
//				
////				this.hasNoPool = false;
//				
//				for(Participant p : ((Collaboration) re).getParticipant()){
//					if(p.getProcessRef() != null){
//						participantMap.put(p.getProcessRef().getId(), p);
//					}
//				}	
//			}
//			
//			
//		}
//		
//		// in case there are participants and multiple lanes per participant, there is a lane as explicit bpmn shape
//		
//		//if a lane is not contained in a participant (there can be lanes on a pure diagram canvas), a pool has to be created
//		Map<Process, List<Lane>> nonParticipantProcessesAndTheirLanes = new HashMap<Process, List<Lane>>(); 
//		for(Process p : processes){
//				//make a list of lanes to be looked 
//				
//				//list the nodes that are contained in the process
//				List<FlowElement> flowelements = p.getFlowElement();
//				
//				for(FlowElement fe : flowelements){
//					if(this.laneMap.get(fe.getId()) == null){
//						this.laneMap.put(fe.getId(), participantMap.get(p.getId()));
//					}
//				}
//				
//				//list the nodes that are contained in the process's sublanes (> overwrite the process entries if necessary)
//				List<LaneSet> ls = p.getLaneSet();
//				
//				 
//				for(LaneSet x : ls){
//					for(Lane l : x.getLanes()){
//						//check if this lane is contained in a participant! There can be lanes on a pure diagram canvas!
//						Participant parti = participantMap.get(p.getId());
//						this.laneMap.put(l.getId(), parti);
//
//						if(parti == null){
//							//remembering the lanes without process, saving the process, and later creating a pool with the bounds of the lanes, combined
//							//System.out.println("Lane without parent pool... Import may be problematic.");
//							
//							List<Lane> lanelist = nonParticipantProcessesAndTheirLanes.get(p);
//							if(lanelist == null){
//								lanelist = new ArrayList<Lane>();
//								nonParticipantProcessesAndTheirLanes.put(p, lanelist);
//							}
//							lanelist.add(l);
//							
//						}
//						
//						this.putEntries(this.laneMap, l);
//					}
//					
//				}
//				
//				/*
//				//make a list of lanes to be looked 
//				
//				//list the nodes that are contained in the process
//				List<FlowElement> flowelements = ((Process) re).getFlowElement();
//				
//				for(FlowElement fe : flowelements){
//					if(map.get(fe.getId()) == null){
//						map.put(fe.getId(), ((Process) re).);
//					}
//				}
//				
//				//list the nodes that are contained in the process's sublanes (> overwrite the process entries if necessary)
//				List<LaneSet> ls = ((Process) re).getLaneSet();
//				for(LaneSet x : ls){
//					this.putEntries(map, x);
//				}
//				*/
//			
//		}
//		
//		
//		
//		//create pool if there are pool-less lanes
//		if(!nonParticipantProcessesAndTheirLanes.isEmpty()){
//			for(Process  process : nonParticipantProcessesAndTheirLanes.keySet()){
//				List<Lane> l = nonParticipantProcessesAndTheirLanes.get(process);
//				
//				Shape shape = this.newShape(process.getId());
//				
//				shape.setBounds(getPoolBoundsForLaneList(l));
//				shape.setStencil(new StencilType("Pool"));
//				if(process.getName() != null)
//					shape.putProperty("name", process.getName());
//				
//				for(Lane lane : l){
//					Shape laneshape = this.getEditorShapeByID(lane.getId());
//					if(laneshape == null){
//						laneshape = this.newShape(lane.getId());
//					}
//					laneshape.setParent(shape);
//					shape.getChildShapes().add(laneshape);
//				}
//			}
//		}
//	}
//
//	/**
//	 * For a list of Lanes, returns the bounds for a containing pool. 
//	 */
//	private Bounds getPoolBoundsForLaneList(List<Lane> l) {
//		double maxX = 0, maxY = 0, minX = Double.MAX_VALUE, minY = Double.MAX_VALUE;
//
//		// iterate over the lanes' BPMNShapes, retrieve maximum bounds, subtract 30 for the upper left point's x coordinate
//		for(Lane lane : l){
//			de.hpi.bpmn2_0.model.bpmndi.dc.Bounds b = this.getBpmnShapeByID(lane.getId()).getBounds();
//			if(b.getX() < minX){minX = b.getX();}
//			if(b.getY() < minY){minY = b.getY();}
//			if(b.getX() + b.getWidth() > maxX){maxX = b.getX() + b.getWidth();}
//			if(b.getY() + b.getHeight() > maxY){maxY = b.getY() + b.getHeight();}
//		}
//		
////		//margin for caption
////		if(minX - 30 >= 0){
////			minX -= 30;
////		}else{
//			maxX += 30;
//			
////		}
//		
//		return new Bounds(new Point(maxX, maxY), new Point(minX, minY));
//	}
//	
//
//	/*
//	private void putEntries(Map<String, FlowElement> map, LaneSet ls) {
//		for(Lane l : ls.getLanes()){
//			putEntries(map, l);
//		}
//	}
//	*/
//	
//	/**
//	 * Helper method, putting entries into the laneMap that is passed as a parameter. Those serve the lookup of parent lanes for Shape objects.
//	 */
//	private void putEntries(Map<String, FlowElement> map, Lane l) {
//		for(FlowNode f : l.getFlowNodeRef())
//			map.put(f.getId(), l);
//		
//		for(Lane l2 : l.getChildLaneSet(true).getLanes()){
//			map.put(l2.getId(), l);
//			this.putEntries(map, l2);
//		}			
//	}
//	
//	/**
//	 * @param definitions Instance of de.hpi.bpmn2_0.model.Definitions that has been generated using JAXB functionality. It forms an intermediate result during the conversation process.
//	 * @return a org.oryxeditor.server.diagram.Diagram object, importable into the editor.
//	 * @throws Exception May throw an exception if the XML format is not (yet) supported (such as Processes and Collaborations in one diagram).
//	 */
//	public List<Diagram> generateDiagramFromBpmn20(Definitions definitions) throws Exception {
//		
//		/*
//		List<String> namespaces = definitions.getNamespaces();
//		
//		for(String ns : namespaces){
//			if(ns)
//		}
//		*/
//		
//		my_init(definitions);		
//		
//		List<Diagram> diagrams = new ArrayList<Diagram>();
//		List<BPMNDiagram> bpmndiags = definitions.getDiagram();
//		
//		for( BPMNDiagram bpmnDiagram : bpmndiags){
//			ArrayList<Shape> shapes = new ArrayList<Shape>();
//			Diagram diagram = new Diagram(bpmnDiagram.getId()); 
//			diagram.setStencil(new StencilType("BPMNDiagram"));
//			
//			//todo maybe write sth in this (It is a "flat" list of shapes, as opposed to hierarchichal child shapes...)
//			diagram.setShapes(new ArrayList<Shape>());
//			
//			Bounds bounds = new Bounds(new Point(this.furthest_x * 1.5, this.furthest_y * 1.5), new Point(0.0, 0.0));
//			diagram.setBounds(bounds);
//			
//			ArrayList<Shape> choreographyShapes = new ArrayList<Shape>();
//			
////			if(this.hasNoPool){
////				//all shapes are children of the diagram
////				for(DiagramElement de : bpmnDiagram.getBPMNPlane().getDiagramElement()){
////					Shape s = de.toShape(this);
////					if(s != null) {
////						shapes.add(s);
////						if(s.getStencilId().matches("Choreography(Task|Subprocess(Collapsed)?)")){
////							choreographyShapes.add(s);
////						}
////					}
////				}
////			}
////			else
//				//only top level pools are children of the diagram
//				for(DiagramElement de : bpmnDiagram.getBPMNPlane().getDiagramElement()){
//					Shape s = de.toShape(this);
//					
//					if(s != null){
//						if(s.getParent() == null){
//							shapes.add(s);
//						} 
//						else if(s.getParent().getStencilId()!= null && s.getParent().getStencilId().equals("Pool")){
//							if(!shapes.contains(s.getParent())){
//								shapes.add(s.getParent());
//							}
//						}
////						//[BPMN2.0] todo: workaround for collapsed, filled subprocess
////						else if(s.getParent().getProperty("isInCollapsedSubprocess") != null){
////							;
////						}
//							
//						
//						if(s.getStencilId().matches("Choreography(Task|Subprocess(Collapsed)?)")){
//							choreographyShapes.add(s);
//						}
//					}
//				}
//
//			
//			//for deferred adding of child shapes, see below
//			Map<Shape,List<Shape>> toBeAdded = new HashMap<Shape, List<Shape>>();
//			
//			//if it is a choreography subprocess or task, then create message shapes for its participants if necessary
//			for(Shape s : choreographyShapes){
//								
//				for(Shape childshape : s.getChildShapes()){
//					//if choreography participant: add a message and an undirected association in case the property "messagevisible" has been set					
//					if(childshape.getStencilId().equals("ChoreographyParticipant") && childshape.getProperty("messagevisible") != null && childshape.getProperty("messagevisible").equals("true")){
//						
//						//create message shape
//				    	
//				    	Shape message = new Shape(SignavioUUID.generate());
//				    	message.setStencil(new StencilType("Message"));
//				    	
//				    	if(childshape.getProperty("initiating") != null){
//				    		message.putProperty("initiating", childshape.getProperty("initiating"));
//				    	}
//				    			
//				    	ArrayList<Point> messagedockers = new ArrayList<Point>();
//				    	messagedockers.add(new Point(10.0, 15.0));
//				    	message.setDockers(messagedockers);
//				    	
//				    	
//				    	//create direct undirected association between childshape and message
//				    	Shape association = new Shape(SignavioUUID.generate());
//				    	association.setStencil(new StencilType("Association_Undirected"));
//				    	
//				    	Point childUpperLeft = childshape.getBounds().getUpperLeft();
//				    	Point childLowerRight = childshape.getBounds().getLowerRight();
//				    	
//				    	//distinguish between lines going up and going down (calculation has to be based on a heuristic... 
//				    	//if childshape's upper left corner is lower than half of the parent shape's Y coordinate, draw line down, otherwise up
//				    	
//				    	boolean messageAbove = false;
//				    	
//				    	if(childshape.getBounds().getUpperLeft().getY() < /*s.getBounds().getUpperLeft().getY() + */ 0.5 * s.getHeight()){
//				    		messageAbove = true;
//				    	}
//				    	
//				    	if(messageAbove){
//				    		Point associationPoint = new Point(childUpperLeft.getX() + 0.5 * childshape.getWidth(), childLowerRight.getY());
//					    	Point associationPoint2 = new Point(childUpperLeft.getX() + 0.5 * childshape.getWidth(), childLowerRight.getY() - 40);
//					    	association.setBounds(new Bounds(associationPoint, associationPoint2));
//				    	} else {
//					    	//set bounds, starting from (childshape.x + 1/2 its width, childshape's lowest y coordinate), and 40 in length
//					    	Point associationPoint = new Point(childUpperLeft.getX() + 0.5 * childshape.getWidth(), childLowerRight.getY() + childshape.getHeight());
//					    	Point associationPoint2 = new Point(childUpperLeft.getX() + 0.5 * childshape.getWidth(), childLowerRight.getY() + childshape.getHeight() + 40);
//					    	association.setBounds(new Bounds(associationPoint, associationPoint2));
//				    	}
//				    	
//				    	//set dockers relative to the respective shapes, so it's the childshape's lowest point relative to its upperLeft, and the message's center
//				    	//assuming the message is (X)30 x (Y)20 units in size
//				    	ArrayList<Point> dockers = new ArrayList<Point>();
//				    	
//				    	if(messageAbove){
//				    		dockers.add(new Point(0.5 * childshape.getWidth(), 0.0));
//				    	}else{
//				    		dockers.add(new Point(0.5 * childshape.getWidth(), childshape.getHeight()));
//				    	}
//				    	dockers.add(new Point(15.0, 10.0));
//				    	association.setDockers(dockers);
//				    					    	
//				    	
//				    	//set message's bounds, assuming that it is relative to the childshape, is 40 units lower (because of the association), and the message is (X)30 x (Y)20 units in size 
//				    	//Point messageUpperLeft = new Point(childUpperLeft.getX() + 0.5 * childshape.getWidth() - 15, childshape.getHeight() + 40 - 10);
//				    	
//				    	if(messageAbove){
//				    		Point messageUpperLeft = new Point(childUpperLeft.getX() + 0.5 * childshape.getWidth() - 15, childUpperLeft.getY() - 40 - 10);
//					    	message.setBounds(new Bounds(new Point(messageUpperLeft.getX() + 30, messageUpperLeft.getY() + 20), messageUpperLeft));
//				    	}else{
//					    	Point messageUpperLeft = new Point(childUpperLeft.getX() + 0.5 * childshape.getWidth() - 15, childUpperLeft.getY() + childshape.getHeight() + 40 - 10);
//					    	message.setBounds(new Bounds(new Point(messageUpperLeft.getX() + 30, messageUpperLeft.getY() + 20), messageUpperLeft));
//				    	}
//				    	
//				    	//set target and source stuff...
//				    	childshape.getOutgoings().add(association);
//				    	association.getIncomings().add(childshape);
//				    	association.getOutgoings().add(message);
//				    	
//				    	//[BPMN2.0] todo: do I have to set a target?
//				    	//association.setTarget(message);
//				    	message.getIncomings().add(association);	
//				    	
//				    	//add to canvas (deferred, because otherwise there would be a ConcurrentModiicationExcepiton)
//			    		
//				    	if(toBeAdded.get(childshape) == null)
//				    		toBeAdded.put(childshape, new ArrayList<Shape>());
//				    	
//				    	toBeAdded.get(childshape).add(message);
//			    		toBeAdded.get(childshape).add(association);
//				    	
//					}
//				}
//			}
//			
//			for(Shape x : toBeAdded.keySet()){
//				x.getChildShapes().addAll(toBeAdded.get(x));
//			}
//			
//			diagram.setChildShapes(shapes);
//
//			if(this.isChoreography()){
//				diagram.setStencilset(new StencilSet("/stencilsets/bpmn2.0chor/bpmn2.0chor.json", "http://b3mn.org/stencilset/bpmn2.0choreography#"));//"http://b3mn.org/stencilset/bpmn2.0#"));
//				ArrayList<String> argument = new ArrayList<String>();
//				argument.add("http://oryx-editor.org/stencilsets/extensions/bpmn2.0choreography#");
//				diagram.setSsextensions(argument);
//			}
//			else if(this.isConversation()){
//				diagram.setStencilset(new StencilSet("/stencilsets/bpmn2.0/bpmn2.0conversation.json", "http://b3mn.org/stencilset/bpmn2.0conversation#"));
//
//				/* is now included in the toShape-calls
//				//nasty workaround...
//				for(Shape s : diagram.getChildShapes()){
//					if(s.getStencilId().equals("Pool"))
//						s.setStencil(new StencilType("Participant"));
//				}
//				*/
//				if(this.isChoreography()){
//					throw new Exception("File not yet supported! Combinations of Choreographies, Conversations and Collaborations will be supported in the future!");
//				}
//			} else{
//				diagram.setStencilset(new StencilSet("/stencilsets/bpmn2.0/bpmn2.0.json", "http://b3mn.org/stencilset/bpmn2.0#"));
//			}
//				
//			diagrams.add(diagram);
//		}
//		
//		return diagrams;
//	}
//	
//	/**
//	 * Creates a new shape with a given ID, sets its bounds to (0, 0), (0, 0) and stores it in the local lookup structures. If it is created in another way, it may not reach the export's output.
//	 * @param id
//	 * @return
//	 */
//	public Shape newShape(String id){
//		Shape shape = new Shape(id);
//		shape.setBounds(new Bounds(new Point(0.0,0.0), new Point(0.0,0.0)));
//		this.putEditorShape(shape);
//		return shape;
//	}
//	
//	/**
//	 * Used to look up a parent shape (Lane, Participant (equals "Pool")).
//	 * @param shapeId
//	 * @return the parent Lane / Pool shape, or null if there is none. If the parent shape is null, the shape will be drawn on the diagram canvas later.
//	 */
//	public Shape getMyParentLaneOrParticipantShape(String shapeId){
//		if(this.laneMap.get(shapeId) == null){
//			//no parent > paint to pure canvas
//			return null;
//		}
//		else{
//			String laneOrParticipantId = this.laneMap.get(shapeId).getId();
//			Shape s =  this.getEditorShapeByID(laneOrParticipantId);
//			if(s == null){
//				//System.out.println("Participant found, but no Shape for it. Creating one...");
//				return this.newShape(laneOrParticipantId);
//			}
//			else 
//				return s;
//		}
//	}
//	
//	
////	/** 
////	 * Look up DiagramElement objects (non-semantic part of the .bpmn document) by id.
////	 */
////	public DiagramElement getDiagramElementByID(String id){
////		return this.diagramElementMap.get(id);
////	}
//	
//	/** 
//	 * Look up BPMNShape objects (non-semantic part of the .bpmn document) by id.
//	 */
//	public BPMNShape getBpmnShapeByID(String id){
//		return this.bpmnShapeMap.get(id);
//	}
//	
//	/*
//	public BPMNShape getChoreographyBpmnShapeByItsShapesID(String id){
//		return this.choreographyBpmnShapeMap.get(id);
//	}
//	*/
//	
////	public String getBpmnElementIdForBpmnShapesId(String id){
////		return this.bpmnShapeIdsToBpmnElementIdsMap.get(id);
////	}
//	
//	/** 
//	 * Look up BPMNEdge objects (non-semantic part of the .bpmn document) by id.
//	 */
//	public BPMNEdge getBpmnEdgeByID(String id){
//		return this.bpmnEdgeMap.get(id);
//	}
//	
//	/** 
//	 * Look up Shape objects (already created) by id.
//	 */
//	public Shape getEditorShapeByID(String id){
//		return this.editorShapeMap.get(id);
//	}
//	
//	/**
//	 * 
//	 * @param id
//	 * @return
//	 */
//	public List<BPMNShape> getDoublesForId(String id){
//		return this.doublesMap.get(id);
//	}
//	
//	/**
//	 * Place a org.oryxeditor.server.diagram.Shape object for lookup (lookup with getEditorShapeByID).
//	 * @param shape
//	 * @return
//	 */
//	private Shape putEditorShape(Shape shape){
//		return this.editorShapeMap.put(shape.getResourceId(), shape);
//	}
//		
////	public Boolean hasNoPool(){
////		return this.hasNoPool;
////	}
////	
//	
//	/**
//	 * Retrieve the isConversation flag that is set during conversion initialization; it indicates whether the imported file is a BPMN 2.0 conversation, and requires a certain stencilset. 
//	 */
//	public Boolean isConversation(){
//		return this.isConversation;
//	}
//	
//	/**
//	 * Set the isConversation flag that indicates whether the imported file is a BPMN 2.0 conversation, and requires a certain stencilset.
//	 */
//	public void setIsConversation(boolean value){
//		this.isConversation = value;
//	}
//	
//	/**
//	 * Retrieve the isChoreography flag that is set during conversion initialization; it indicates whether the imported file is a BPMN 2.0 choreography, and requires a certain stencilset.
//	 * @return
//	 */
//	public Boolean isChoreography(){
//		return this.isChoreography;
//	}
//	
//	/**
//	 * Set the isChoreography flag that indicates whether the imported file is a BPMN 2.0 choreography, and requires a certain stencilset.
//	 * @return
//	 */
//	public void setIsChoreography(boolean value){
//		this.isChoreography = value;
//	}
}
