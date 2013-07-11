package com.signavio.warehouse.directory.handler;

import com.signavio.platform.annotations.HandlerConfiguration;

import javax.servlet.ServletContext;

@HandlerConfiguration(context=DirectoryHandler.class, uri="/parents", rel="parents")
public class ParentDirectoriesHandler extends AbstractParentDirectoriesHandler {

	public ParentDirectoriesHandler(ServletContext servletContext) {
		super(servletContext);
	}

}
