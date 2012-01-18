/**
 * Copyright (c) 2009, Signavio GmbH
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
package com.signavio.platform.security.business;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.signavio.platform.tenant.business.FsTenant;

public abstract class FsSecureBusinessObject {

	protected static final String emptyString = "";
	
	@SuppressWarnings("unchecked")
	protected static final Set emptySet = new HashSet();
	@SuppressWarnings("unchecked")
	protected static final List emptyList = new ArrayList();
	
	private boolean privilegeInheritanceBlocked = false;
	private boolean deleted = false;
	
	public FsSecureBusinessObject(){
		// empty - for now
	}
	
	public abstract String getId();

	
	public FsTenant getTenant(){
		return FsTenant.getSingleton();
	}
	
	public FsAccessToken getAccessToken(){
		return FsAccessToken.getDummy();
	}
	

	public void	setDeleted(boolean bool){
		deleted = bool;
	}

	public boolean isDeleted(){
		return deleted;
	}
	
	public void	setPrivilegeInheritanceBlocked(boolean bool){
		privilegeInheritanceBlocked = bool;
	}
	
	public boolean isPrivilegeInheritanceBlocked(){
		return privilegeInheritanceBlocked;
	}
	
	@SuppressWarnings("unchecked")
	public Set<String> getGainedPrivileges(FsSecureBusinessObject object) { 
		// ISSUE: Would full set be better?
		return emptySet; 
	}

	// ISSUE: Uncomment these methods in order to get to know needed implementations..
	public <T extends FsSecureBusinessObject> Set<T> getChildren(Class<T> type){
		throw new UnsupportedOperationException("Not supported by this sub-type of SecureBusinessObject");
	}
	public void addChild(FsSecureBusinessObject Child){
		throw new UnsupportedOperationException("Not supported by this sub-type of SecureBusinessObject");
	}
	public <T extends FsSecureBusinessObject> Set<T> getParents(Class<T> businessObjectClass) {
		throw new UnsupportedOperationException("Not supported by this sub-type of SecureBusinessObject");
	}
	public <T extends FsSecureBusinessObject> Set<T> removeChild(T Child){
		throw new UnsupportedOperationException("Not supported by this sub-type of SecureBusinessObject");
	}
	
}
