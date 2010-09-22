/**
 * Copyright (c) 2010
 * Signavio, Sven Wagner-Boysen
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
package de.hpi.bpmn2_0.util;

import org.oryxeditor.server.diagram.Bounds;
import org.oryxeditor.server.diagram.Point;
import org.oryxeditor.server.diagram.Shape;

/**
 * @author Sven Wagner-Boysen
 *
 */
public class DiagramHelper {
	
	public static Bounds getAbsoluteBounds(Shape shape) {
		/* Handle invalid diagram and prevent from crashing the transformation */
		if(shape.getBounds() == null) {
			Bounds bounds = new Bounds(new Point(0.0, 0.0), new Point(0.0, 0.0));
			return bounds;
		}
		//clone bounds
		Bounds bounds = cloneBounds(shape.getBounds());
		
		Shape parent = shape.getParent();
		
		if(parent != null) {
			Bounds parentBounds = getAbsoluteBounds(parent);
			setBounds(bounds, parentBounds.getUpperLeft().getX().doubleValue(), parentBounds.getUpperLeft().getY().doubleValue());
		}
		
		return bounds;
	}
	
	private static Bounds cloneBounds(Bounds bounds) {
		double lrx = (bounds.getLowerRight().getX() != null ? bounds.getLowerRight().getX() : 0.0);
		double lry = (bounds.getLowerRight().getY() != null ? bounds.getLowerRight().getY() : 0.0);
		double ulx = (bounds.getUpperLeft().getX() != null ? bounds.getUpperLeft().getX() : 0.0);
		double uly = (bounds.getUpperLeft().getY() != null ? bounds.getUpperLeft().getY() : 0.0);
		
		return new Bounds(new Point(lrx, lry), new Point(ulx, uly));
	}
	
	private static void setBounds(Bounds bounds, double offsetX, double offsetY) {
		Point ul = bounds.getUpperLeft();
		ul.setX(ul.getX() + offsetX);
		ul.setY(ul.getY() + offsetY);
		Point lr = bounds.getLowerRight();
		lr.setX(lr.getX() + offsetX);
		lr.setY(lr.getY() + offsetY);
	}
	
}
