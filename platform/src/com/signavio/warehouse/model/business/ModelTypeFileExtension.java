package com.signavio.warehouse.model.business;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface ModelTypeFileExtension {
	public String fileExtension();
}
