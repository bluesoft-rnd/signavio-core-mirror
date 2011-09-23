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

import org.oryxeditor.server.diagram.generic.GenericShape;

import de.hpi.bpmn2_0.annotations.StencilId;
import de.hpi.bpmn2_0.exceptions.BpmnConverterException;
import de.hpi.bpmn2_0.factory.AbstractShapeFactory;
import de.hpi.bpmn2_0.model.BaseElement;
import de.hpi.bpmn2_0.model.data_object.DataState;
import de.hpi.bpmn2_0.model.data_object.DataStore;
import de.hpi.bpmn2_0.model.data_object.DataStoreReference;
import de.hpi.diagram.SignavioUUID;

/**
 * Factory for DataStores 
 * 
 * @author Philipp Giese
 * @author Sven Wagner-Boysen
 *
 */
@StencilId("DataStore")
public class DataStoreFactory extends AbstractShapeFactory {

	/* (non-Javadoc)
	 * @see de.hpi.bpmn2_0.factory.AbstractBpmnFactory#createProcessElement(org.oryxeditor.server.diagram.Shape)
	 */
	// @Override
	protected BaseElement createProcessElement(GenericShape shape)
			throws BpmnConverterException {
		DataStoreReference dataStoreRef = new DataStoreReference();
		this.setCommonAttributes(dataStoreRef, shape);
		dataStoreRef.setDataStoreRef(new DataStore());
		this.setDataStoreRefAttributes(dataStoreRef, shape);
		
		return dataStoreRef;
	}
	
	/**
	 * Sets the attributes related to a data store element.
	 * 
	 * @param dataStoreRef
	 * 		The @link {@link DataStoreReference}.
	 * @param shape
	 * 		The data store {@link GenericShape}
	 */
	private void setDataStoreRefAttributes(DataStoreReference dataStoreRef, GenericShape shape) {
		DataStore dataStore = dataStoreRef.getDataStoreRef();
		String dataStateName = shape.getProperty("state");
		/* Set attributes of the global data store */
		if(dataStore != null) {
			dataStore.setId(SignavioUUID.generate());
			dataStore.setName(shape.getProperty("name"));
			if(shape.getProperty("capacity") != null && !(shape.getProperty("capacity").length() == 0))
				dataStore.setCapacity(Integer.valueOf(shape.getProperty("capacity")).intValue());
			
			/* Set isUnlimited attribute */
			String isUnlimited = shape.getProperty("isunlimited");
			if(isUnlimited != null && isUnlimited.equalsIgnoreCase("true")) 
				dataStore.setUnlimited(true);
			else
				dataStore.setUnlimited(false);
			
			/* Define DataState element */
			if(dataStateName != null && !(dataStateName.length() == 0)) {
				DataState dataState = new DataState(dataStateName);
				dataStore.setDataState(dataState);
			}
		}
		
		/* Set attributes of the data store reference */
		dataStoreRef.setName(shape.getProperty("name"));
		dataStoreRef.setId(shape.getResourceId());
		
		/* Define DataState element */
		if(dataStateName != null && !(dataStateName.length() == 0)) {
			DataState dataState = new DataState(dataStateName);
			dataStoreRef.setDataState(dataState);
		}
	}

}
