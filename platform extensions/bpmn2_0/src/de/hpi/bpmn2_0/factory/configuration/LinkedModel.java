/**
 * 
 */
package de.hpi.bpmn2_0.factory.configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONException;
import org.oryxeditor.server.diagram.Diagram;
import org.oryxeditor.server.diagram.DiagramBuilder;

/**
 * A LinkedModel contains the JSON representation of the diagram, custom 
 * defined attributes and models linked to this model.
 * 
 * 
 * @author Sven Wagner-Boysen
 *
 */
public class LinkedModel {
	/*
	 * Attributes
	 */
	
	private Map<String, LinkedModel> linkedModels;
	private Map<String, Set<String>> metaData;
	private Diagram diagram;
	private String id;
	
	
	
	@SuppressWarnings("unchecked")
	public LinkedModel(Map<String, Object> modelMetaData) {
		if(modelMetaData.get("modelJSON") != null && modelMetaData.get("modelJSON") instanceof String) {
			Diagram diagram = null;
			try {
				diagram = DiagramBuilder.parseJson((String) modelMetaData.get("modelJSON"));
			} catch (JSONException e) {
				diagram = new Diagram("");
			}
			
			setDiagram(diagram);
		}
		
		if(modelMetaData.get("metaData") != null && modelMetaData.get("metaData") instanceof Map<?, ?>) {
			getMetaData().putAll((Map<? extends String, ? extends Set<String>>) modelMetaData.get("metaData"));
		}
		
		if(modelMetaData.get("linkedModels") != null && modelMetaData.get("linkedModels") instanceof List<?>) {
			extractLinkedModels((List<Map<String, Object>>) modelMetaData.get("linkedModels"));
		}
		
		if(modelMetaData.get("id") != null && modelMetaData.get("id") instanceof String) {
			setId((String) modelMetaData.get("id"));
		}
	}
	
	private void extractLinkedModels(List<Map<String, Object>> linkedModelsList) {
		for(Map<String, Object> linkedModelMap : linkedModelsList) {
			LinkedModel model = new LinkedModel(linkedModelMap);
			getLinkedModels().put(model.getId(), model);
		}
	}


	/* Getter & Setter */
	
	public Map<String, LinkedModel> getLinkedModels() {
		if(linkedModels == null) {
			linkedModels = new HashMap<String, LinkedModel>();
		}
		return linkedModels;
	}
	
	
	
	public Map<String, Set<String>> getMetaData() {
		if(metaData == null) {
			metaData = new HashMap<String, Set<String>>();
		}
		return metaData;
	}

	public void setDiagram(Diagram diagram) {
		this.diagram = diagram;
	}

	public Diagram getDiagram() {
		return diagram;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}
}
