package com.signavio.warehouse.model.business.modeltype;

import com.signavio.warehouse.model.business.ModelTypeRequiredNamespaces;
import com.signavio.warehouse.revision.business.RepresentationType;

import java.io.File;

@ModelTypeRequiredNamespaces(namespaces={"http://b3mn.org/stencilset/bpmn2.0#", "http://b3mn.org/stencilset/bpmn2.0conversation#", "http://b3mn.org/stencilset/bpmn2.0choreography#"})
public class BPMN2_0XMLModelType extends SignavioModelType {

	@Override
	public void storeRepresentationInfoToModelFile(RepresentationType type, byte[] content, String path) {
		super.storeRepresentationInfoToModelFile(type, content, path);
	}

	@Override
	public void storeRevisionToModelFile(String jsonRep, String svgRep,String path) {
		super.storeRevisionToModelFile(jsonRep, svgRep, path);
	}
	
	@Override
	public boolean acceptUsageForTypeName(String namespace) {
		for(String ns : this.getClass().getAnnotation(ModelTypeRequiredNamespaces.class).namespaces()) {
			if(ns.equals(namespace)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public File storeModel(String path, String id, String name, String description,
			String type, String jsonRep, String svgRep) {
		return super.storeModel(path, id, name, description, type, jsonRep, svgRep);
	}

	@Override
	public boolean renameFile(String parentPath, String oldName, String newName) {
		return super.renameFile(parentPath, oldName, newName);
	}
	
	@Override
	public void deleteFile(String parentPath, String name) {
		super.deleteFile(parentPath, name);
	}
}
