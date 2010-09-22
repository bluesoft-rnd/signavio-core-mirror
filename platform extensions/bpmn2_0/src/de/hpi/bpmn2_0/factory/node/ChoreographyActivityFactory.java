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

import org.oryxeditor.server.diagram.Shape;

import de.hpi.bpmn2_0.annotations.StencilId;
import de.hpi.bpmn2_0.exceptions.BpmnConverterException;
import de.hpi.bpmn2_0.factory.AbstractShapeFactory;
import de.hpi.bpmn2_0.model.bpmndi.BPMNShape;
import de.hpi.bpmn2_0.model.choreography.ChoreographyActivity;
import de.hpi.bpmn2_0.model.choreography.ChoreographyLoopType;
import de.hpi.bpmn2_0.model.choreography.ChoreographyTask;
import de.hpi.bpmn2_0.model.choreography.SubChoreography;

/**
 * Factory that creates elements of a choreography diagram.
 * 
 * @author Sven Wagner-Boysen
 *
 */
@StencilId({
	"ChoreographyTask",
	"ChoreographySubprocessCollapsed",
	"ChoreographySubprocessExpanded"
})
public class ChoreographyActivityFactory extends AbstractShapeFactory {

	/* (non-Javadoc)
	 * @see de.hpi.bpmn2_0.factory.AbstractBpmnFactory#createProcessElement(org.oryxeditor.server.diagram.Shape)
	 */
	// @Override
	protected ChoreographyActivity createProcessElement(Shape shape)
			throws BpmnConverterException {
		try {
			ChoreographyActivity activity = (ChoreographyActivity) this.invokeCreatorMethod(shape);
			activity.setId(shape.getResourceId());
			activity.setName(shape.getProperty("name"));
			
			/* Loop type */
			String loopType = shape.getProperty("looptype");
			if(loopType != null) {
				if(loopType.equals("none")) {
					activity.setLoopType(ChoreographyLoopType.NONE);
				}
				else if(loopType.equals("standard")) {
					activity.setLoopType(ChoreographyLoopType.STANDARD);
				}
				else if(loopType.equals("parallel")) {
					activity.setLoopType(ChoreographyLoopType.MULTI_INSTANCE_PARALLEL);
				} 
				else if(loopType.equals("sequential")) {
					activity.setLoopType(ChoreographyLoopType.MULTI_INSTANCE_SEQUENTIAL);
				}
			}
			
			return activity;
		} catch (Exception e) {
			/* Wrap exceptions into specific BPMNConverterException */
			throw new BpmnConverterException(
					"Error while creating the process element of "
							+ shape.getStencilId(), e);
		}
	}
	
	/**
	 * Creator method for a choreography task.
	 * 
	 * @param shape
	 * 		The resource shape
	 * @return
	 * 		the {@link ChoreographyTask}
	 */
	@StencilId("ChoreographyTask")
	public ChoreographyTask createChoreographyTask(Shape shape) {
		return new ChoreographyTask();
	}
	
	/**
	 * Creator method for a collapsed choreography subprocess.
	 * 
	 * @param shape
	 * 		The resource shape
	 * @return
	 * 		the {@link SubChoreography}
	 */
	@StencilId({
		"ChoreographySubprocessCollapsed",
		"ChoreographySubprocessExpanded"
	})
	public SubChoreography createChoreographySubprocessCollapsed(Shape shape) {
		return new SubChoreography();
	}

	// @Override
	protected BPMNShape createDiagramElement(Shape shape) {
		BPMNShape diagramElement = super.createDiagramElement(shape);
		
		/* Expanded subprocess */
		if(shape.getStencilId().equals("ChoreographySubprocessExpanded")) {
			diagramElement.setIsExpanded(Boolean.TRUE);
		}
		/* Collapsed subprocess */
		else if(shape.getStencilId().equals("ChoreographySubprocessCollapsed")) {
			diagramElement.setIsExpanded(Boolean.FALSE);
		}
		
		return diagramElement;
	}
}
