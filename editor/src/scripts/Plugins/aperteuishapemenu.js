if(!ORYX.Plugins) {
	ORYX.Plugins = new Object();
}
// Object which stores last edited object with our icon.
var lastEditedObjecyt;
var win;
var faccade;

/**
 * Sets data for component
 */
function editorSetStepData(retString){
    if (retString == null || retString == "") {
	    return;
	}
	  
    retString = Ext.decode(retString.replace(/&quot;/g,'"'));

	var taskNameOldValue = lastEditedObjecyt.properties['oryx-tasktype'];
	var taskNameNewValue = retString.taskName;
	var oldAC = lastEditedObjecyt.properties['oryx-aperte-conf'];
	var newAC = Ext.encode(retString.params);

    //check UNDO
	if (taskNameOldValue == taskNameNewValue) {
	    if (taskNameOldValue == "User") {
		  if (oldAC == newAC) {
		    win.close();
			return;
		  }
		} else {
			if (oldAC != null && oldAC != "") {
			   var aperteConfOldParams = Ext.decode(oldAC);
			   var aperteConfNewParams = retString.params;
			   
			   var oldParamsSize = 0;
			   var newParamsSize = 0;
			   for (var i in aperteConfOldParams) {
				 oldParamsSize++;
			   }
			   for (var i in aperteConfNewParams) {
				 newParamsSize++;
			   }
			   if (oldParamsSize == newParamsSize) {
				 var makeUndo = false;
				 for (var i in aperteConfOldParams) {
				   if (aperteConfOldParams[i] != aperteConfNewParams[i]) {
					 makeUndo = true;
				   }
				 }
				 if (!makeUndo) {
				   win.close();
				   return;
				 }
			   }
			}
		}
	}
	
	
    
	var commandClass = ORYX.Core.Command.extend({
			construct: function(){
				this.lastEditedObj 	  = lastEditedObjecyt;
				this.oldTaskType         = taskNameOldValue;
				this.newTaskType         = taskNameNewValue;
				this.oldAperteConf		 = oldAC;
				this.newAperteConf		 = newAC;
			},			
			execute: function(){
				this.lastEditedObj.properties['oryx-aperte-conf'] = this.newAperteConf;
				this.lastEditedObj.properties['oryx-tasktype'] = this.newTaskType;
			},
			rollback: function(){
				this.lastEditedObj.properties['oryx-aperte-conf'] = this.oldAperteConf;
				this.lastEditedObj.properties['oryx-tasktype'] = this.oldTaskType;
			}
		});
	
	// Instantiated the class
	var command = new commandClass();
		
	// Execute the command
	faccade.executeCommands([command]);
	
	win.close();
}

function editorSetActionData(retString) {
   if (retString == null || retString == "")
	  return;
	  
    retString = Ext.decode(retString.replace(/&quot;/g,'"'));
	
	var taskNameOldValue = lastEditedObjecyt.properties['oryx-button-type'];
	var taskNameNewValue = retString.buttonType;
	var oldAC = lastEditedObjecyt.properties['oryx-action-properties'];
	var newAC = Ext.encode(retString.items);

    var commandClass = ORYX.Core.Command.extend({
			construct: function(){
				this.lastEditedObj 	     = lastEditedObjecyt;
				this.oldTaskType         = taskNameOldValue;
				this.newTaskType         = taskNameNewValue;
				this.oldAperteConf		 = oldAC;
				this.newAperteConf		 = newAC;
			},			
			execute: function(){
				this.lastEditedObj.properties['oryx-action-properties'] = this.newAperteConf;
				this.lastEditedObj.properties['oryx-button-type'] = this.newTaskType;
			},
			rollback: function(){
				this.lastEditedObj.properties['oryx-action-properties'] = this.oldAperteConf;
				this.lastEditedObj.properties['oryx-button-type'] = this.oldTaskType;
			}
		});
	
	// Instantiated the class
	var command = new commandClass();
		
	// Execute the command
	faccade.executeCommands([command]);
	
	win.close();
}

ORYX.Plugins.AperteUiShapeMenuPlugin = ORYX.Plugins.ShapeMenuPlugin.extend({
    runAperteStepEditor: function() {
		var elements = this.currentShapes;
		if(elements.length != 1) return;
		lastEditedObjecyt = elements[0];
		faccade = this.facade;
        var props = lastEditedObjecyt.properties;
		var type =  props['oryx-tasktype'];
		var name = props['oryx-name'];
		var data =  props['oryx-aperte-conf'];
        this.openStepEditorWindow(name, type, data);
	},
	
	runAperteActionEditor: function() {
	    var elements = this.currentShapes;
		if(elements.length != 1) return;
		lastEditedObjecyt = elements[0];
		faccade = this.facade;
        var props = lastEditedObjecyt.properties;
		var type =  props['oryx-button-type'];
		var name = props['oryx-name'];
		var data =  props['oryx-action-properties'];
        this.openActionEditorWindow(name, type, data);
	},
	
    //Opens new popup window
    openStepEditorWindow: function (stepName, stepType, stepAperteConfig){
		var iframeName = "ifname";
		var props = lastEditedObjecyt.properties;

		var rurl = window.location.href;
		var base_editor_url = rurl.substr(0,rurl.indexOf('editor'));
		var back_url = base_editor_url+"aperte_post";
		
		win = new Ext.Window({
			width:900,
			height:500,
			autoScroll:false,
			html:'',
			modal:true,
			maximizable:true,
			cls:'x-window-body-report',
			title:'Aperte Step Editor'
		});
		win.on('close', function() {
			if(Ext.isIE) {
				win.body.dom.firstChild.src = "javascript:false";
			}
		}, win);
			
		var id = Ext.id();
		var frame = document.createElement('iframe');
 
		frame.id = id;
		frame.name = id;
		frame.frameBorder = '0';
		frame.width = '100%';
		frame.height = '100%';
		frame.src = '';//Ext.isIE ? Ext.SSL_SECURE_URL : "javascript:;";
 
		win.show();
		win.body.appendChild(frame);
 
		// Seems to be workaround for IE having name readonly.
		if(Ext.isIE) {
			document.frames[id].name = id;
		}
		
		var form = new Ext.FormPanel({
			url: APERTE_STEP_EDITOR_URL,
			renderTo:Ext.getBody(),
			standardSubmit:true,
			method:'POST',
			defaultType:'hidden',
			items:[	new Ext.form.TextField({
                        id:'stepName',
                        name:'stepName',
                        inputType:'text',
                        fieldLabel:'step_config',
                        value: stepName
                    }),
                    new Ext.form.TextField({
                        id:'stepType',
                        name:'stepType',
                        inputType:'text',
                        fieldLabel:'stepType',
                        value: stepType
                    }),
			        new Ext.form.TextField({
			            id:'stepConfig',
						name:'stepConfig',
						inputType:'text',
						fieldLabel:'step_config',
						value: stepAperteConfig
					}),
					new Ext.form.TextField({
					    id:'fre',
						name:'restartApplication',
						inputType:'text',
						fieldLabel:'restartApplication',
						value: "1"
					}),
					new Ext.form.TextField({
					    id:'cbe',
						name:'callbackUrl',
						inputType:'text',
						fieldLabel:'callbackUrl',
						value: back_url
					})
            ]
		});
 
		form.getForm().el.dom.action = form.url;
		form.getForm().el.dom.target = id;
			
		if(!Ext.isGecko) {
			var mask = new Ext.LoadMask(win.id, {msg:"Loading..."});
			mask.show();
		}
 
		Ext.EventManager.on(frame, 'load', function() {
			if(mask !== undefined) { mask.hide(); }
			form.destroy();
		});
 
		Ext.emptyFn.defer(200); // frame on ready?
		form.getForm().submit();
    },
	
	openActionEditorWindow: function (buttonName, buttonType, actionParameters){
		var iframeName = "ifname";
		var props = lastEditedObjecyt.properties;

		var rurl = window.location.href;
		var base_editor_url = rurl.substr(0,rurl.indexOf('editor'));
		var back_url = base_editor_url+"aperte_post";
		
		win = new Ext.Window({
			width:900,
			height:500,
			autoScroll:false,
			html:'',
			modal:true,
			maximizable:true,
			cls:'x-window-body-report',
			title:'Aperte Action Editor'
		});
		win.on('close', function() {
			if(Ext.isIE) {
				win.body.dom.firstChild.src = "javascript:false";
			}
		}, win);
			
		var id = Ext.id();
		var frame = document.createElement('iframe');
 
		frame.id = id;
		frame.name = id;
		frame.frameBorder = '0';
		frame.width = '100%';
		frame.height = '100%';
		frame.src = '';//Ext.isIE ? Ext.SSL_SECURE_URL : "javascript:;";
 
		win.show();
		win.body.appendChild(frame);
 
		// Seems to be workaround for IE having name readonly.
		if(Ext.isIE) {
			document.frames[id].name = id;
		}
		
		var form = new Ext.FormPanel({
			url: APERTE_ACTION_EDITOR_URL,
			renderTo:Ext.getBody(),
			standardSubmit:true,
			method:'POST',
			defaultType:'hidden',
			items:[	new Ext.form.TextField({
                        id:'buttonName',
                        name:'buttonName',
                        inputType:'text',
                        fieldLabel:'buttonName',
                        value: buttonName
                    }),
                    new Ext.form.TextField({
                        id:'buttonType',
                        name:'buttonType',
                        inputType:'text',
                        fieldLabel:'buttonType',
                        value: buttonType
                    }),
			        new Ext.form.TextField({
			            id:'actionParameters',
						name:'actionParameters',
						inputType:'text',
						fieldLabel:'actionParameters',
						value: actionParameters
					}),
					new Ext.form.TextField({
					    id:'fre',
						name:'restartApplication',
						inputType:'text',
						fieldLabel:'restartApplication',
						value: "1"
					}),
					new Ext.form.TextField({
					    id:'cbe',
						name:'callbackUrl',
						inputType:'text',
						fieldLabel:'callbackUrl',
						value: back_url
					})
            ]
		});
 
		form.getForm().el.dom.action = form.url;
		form.getForm().el.dom.target = id;
			
		if(!Ext.isGecko) {
			var mask = new Ext.LoadMask(win.id, {msg:"Loading..."});
			mask.show();
		}
 
		Ext.EventManager.on(frame, 'load', function() {
			if(mask !== undefined) { mask.hide(); }
			form.destroy();
		});
 
		Ext.emptyFn.defer(200); // frame on ready?
		form.getForm().submit();
    },


	createMorphMenu: function() {
		
		this.morphMenu = new Ext.menu.Menu({
			id: 'Oryx_morph_menu',
			items: []
		});
		
		this.morphMenu.on("mouseover", function() {
			this.morphMenuHovered = true;
		}, this);
		this.morphMenu.on("mouseout", function() {
			this.morphMenuHovered = false;
		}, this);
		
		
		// Create the button to show the morph menu
		var button = new ORYX.Plugins.ShapeMenuButton({
			hovercallback: 	(ORYX.CONFIG.ENABLE_MORPHMENU_BY_HOVER ? this.showMorphMenu.bind(this) : undefined), 
			resetcallback: 	(ORYX.CONFIG.ENABLE_MORPHMENU_BY_HOVER ? this.hideMorphMenu.bind(this) : undefined), 
			callback:		(ORYX.CONFIG.ENABLE_MORPHMENU_BY_HOVER ? undefined : this.toggleMorphMenu.bind(this)), 
			icon: 			ORYX.PATH + 'images/wrench_orange.png',
			align: 			ORYX.CONFIG.SHAPEMENU_BOTTOM,
			group:			0,
			msg:			ORYX.I18N.ShapeMenuPlugin.morphMsg
		});				
		
		// Create the button to show the morph menu
		var buttonAperteStepEditor = new ORYX.Plugins.ShapeMenuButton({
		    id:				"AperteStepEditorBtnId",
			callback:		this.runAperteStepEditor.bind(this), 
			icon: 			ORYX.PATH + 'images/aperte_small.png',
			align: 			ORYX.CONFIG.SHAPEMENU_BOTTOM,
			group:			0,
			msg:			"Aperte Step Editor"
		});				
		
		var buttonAperteActionEditor = new ORYX.Plugins.ShapeMenuButton({
		    id:				"AperteActionEditorBtnId",
			callback:		this.runAperteActionEditor.bind(this), 
			icon: 			ORYX.PATH + 'images/aperte_small.png',
			align: 			ORYX.CONFIG.SHAPEMENU_BOTTOM,
			group:			0,
			msg:			"Aperte Action Editor"
		});			

		
		this.shapeMenu.setNumberOfButtonsPerLevel(ORYX.CONFIG.SHAPEMENU_BOTTOM, 2)
		this.shapeMenu.addButton(button);
		this.shapeMenu.addButton(buttonAperteStepEditor);
		this.shapeMenu.addButton(buttonAperteActionEditor);
		
		this.morphMenu.getEl().appendTo(button.node);		
		this.morphButton = button;
		this.aperteStepEditorButton = buttonAperteStepEditor;
		this.aperteActionEditorButton = buttonAperteActionEditor;
		
	},
	showAperteStepEditorButton: function(elements){
		if(elements.length != 1) return;
		if(elements[0].properties['oryx-tasktype'] != null && elements[0].properties['oryx-tasktype'] != ""){
			this.aperteStepEditorButton.prepareToShow();
		}
		
	},
	
	showAperteActionEditorButton: function(elements){
		if(elements.length != 1) return;
		if(elements[0].properties['oryx-button-type'] != null &&
		   elements[0].properties['oryx-button-type'] != "" &&
		   elements[0].incoming.length >= 1 &&
		   elements[0].incoming[0] &&
		   elements[0].incoming[0].properties['oryx-gatewaytype'] &&
	       elements[0].incoming[0].properties['oryx-gatewaytype'].toUpperCase() == 'XOR' &&
		   elements[0].incoming[0].incoming[0] &&
		   elements[0].incoming[0].incoming[0].incoming[0] &&
		   elements[0].incoming[0].incoming[0].incoming[0].properties['oryx-tasktype'] &&
		   elements[0].incoming[0].incoming[0].incoming[0].properties['oryx-tasktype'].toUpperCase() == 'USER') {
		    this.aperteActionEditorButton.prepareToShow();
		 } else if (elements[0].properties['oryx-button-type'] != null &&
             	    elements[0].properties['oryx-button-type'] != "" &&
             	    elements[0].incoming.length >= 1 &&
             	    elements[0].incoming[0] &&
                    elements[0].incoming[0].properties['oryx-tasktype'] &&
                    elements[0].incoming[0].properties['oryx-tasktype'].toUpperCase() == 'USER' &&
                    elements[0].outgoing[0] &&
                    elements[0].outgoing[0].properties['oryx-gatewaytype'] != 'XOR'){
		    // display this editor button only when its source is a user task
			this.aperteActionEditorButton.prepareToShow();
		}
		
	},

	
	showShapeMenu: function( dontGenerateNew ) {
	
		if( !dontGenerateNew || this.resetElements ){
			
			window.clearTimeout(this.timer);
			this.timer = window.setTimeout(function(){
				
					// Close all Buttons
				this.shapeMenu.closeAllButtons();
		
				// Show the Morph Button
				this.showMorphButton(this.currentShapes);
				// Show the Morph Button
				this.showAperteStepEditorButton(this.currentShapes);
				this.showAperteActionEditorButton(this.currentShapes);
				
				// Show the Stencil Buttons
				this.showStencilButtons(this.currentShapes);	
				
				// Show the ShapeMenu
				this.shapeMenu.show(this.currentShapes);
				
				this.resetElements = false;
			}.bind(this), 300)
			
		} else {
			
			window.clearTimeout(this.timer);
			this.timer = null;
			
			// Show the ShapeMenu
			this.shapeMenu.show(this.currentShapes);
			
		}
	},
});