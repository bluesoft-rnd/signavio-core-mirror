package de.hpi.bpmn2_0.transformation;

import java.util.List;

import org.oryxeditor.server.diagram.Diagram;
import org.oryxeditor.server.diagram.Shape;

import de.hpi.bpmn2_0.model.Definitions;
import de.hpi.bpmn2_0.model.bpmndi.BPMNEdge;
import de.hpi.bpmn2_0.model.bpmndi.BPMNShape;

public interface BPMN2DiagramConverterI {

	
	public abstract List<Diagram> generateDiagramFromBpmn20(
			Definitions definitions) throws Exception;

	
	public abstract Shape newShape(String id);

	
	public abstract Shape getMyParentLaneOrParticipantShape(String shapeId);

	
	public abstract BPMNShape getBpmnShapeByID(String id);

	
	public abstract BPMNEdge getBpmnEdgeByID(String id);

	
	public abstract Shape getEditorShapeByID(String id);

	
	public abstract List<BPMNShape> getDoublesForId(String id);

	
	public abstract Boolean isConversation();

	
	public abstract void setIsConversation(boolean value);

	
	public abstract Boolean isChoreography();

	
	public abstract void setIsChoreography(boolean value);

}