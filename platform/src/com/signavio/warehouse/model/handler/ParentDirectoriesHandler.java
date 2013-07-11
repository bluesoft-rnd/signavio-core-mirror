package com.signavio.warehouse.model.handler;

import com.signavio.platform.annotations.HandlerConfiguration;
import com.signavio.warehouse.directory.handler.AbstractParentDirectoriesHandler;

import javax.servlet.ServletContext;

@HandlerConfiguration(context=ModelHandler.class, uri="/parents", rel="parents")
public class ParentDirectoriesHandler extends AbstractParentDirectoriesHandler {

	public ParentDirectoriesHandler(ServletContext servletContext) {
		super(servletContext);
	}

}
