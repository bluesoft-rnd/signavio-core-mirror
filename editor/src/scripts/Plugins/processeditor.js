if (!ORYX.Plugins) 
    ORYX.Plugins = new Object();

var processWin;
	
function editorSetProcessData(newProcessConf) {
    if (newProcessConf == null || newProcessConf == "") {
	    return;
	}

    newProcessConf = newProcessConf.replace(/&quot;/g,'"');
	var oldProcessConf = faccade.getCanvas().properties["oryx-process-conf"];
	
	var commandClass = ORYX.Core.Command.extend({
			construct: function(){
				this.oldQC 	  = oldProcessConf;
				this.newQC    = newProcessConf;
			},			
			execute: function(){
				faccade.getCanvas().properties["oryx-process-conf"] = this.newQC;
			},
			rollback: function(){
				faccade.getCanvas().properties['oryx-process-conf'] = this.oldQC;
			}
		});
	
	// Instanciated the class
	var command = new commandClass();
		
	// Execute the command
	faccade.executeCommands([command]);
	
	processWin.close();
}
	
ORYX.Plugins.ProcessEditor = Clazz.extend({
	
    facade: undefined,
	
	construct: function(facade){
		this.facade = facade;
		
		this.facade.offer({
			'name': ORYX.I18N.ProcessEditor.processEditor,
			'functionality': this.runProcessEditor.bind(this),
			'group': ORYX.I18N.ProcessEditor.group,
			'icon': ORYX.PATH + "images/process_editor.png",
			'description': ORYX.I18N.ProcessEditor.desc,
			'index': 1,
			'minShape': 0,
			'maxShape': 0
		});
		
	},
	

    runProcessEditor: function(){
        var processConf = this.facade.getCanvas().properties["oryx-process-conf"];
		
		faccade = this.facade;
        this.editorOpenProcessWindow(processConf);
        
        return true;
    },
	
	editorOpenProcessWindow: function (processConf){
         
        // get callback url
		var rurl = window.location.href;
		var baseEditorUrl = rurl.substr(0, rurl.indexOf('editor'));
		var callbackUrl = baseEditorUrl + "aperte_post";

		processWin = new Ext.Window({
			width:800,
			height:600,
			autoScroll:false,
			html:'',
			modal:true,
			maximizable:true,
			cls:'x-window-body-report',
			title: 'Aperte Process Editor'
		});
		processWin.on('close', function() {
			if(Ext.isIE) {
				processWin.body.dom.firstChild.src = "javascript:false";
			}
		}, processWin);
			
		var id = Ext.id();
		var frame = document.createElement('iframe');
 
		frame.id = id;
		frame.name = id;
		frame.frameBorder = '0';
		frame.width = '100%';
		frame.height = '100%';
		frame.src = '';//Ext.isIE ? Ext.SSL_SECURE_URL : "javascript:;";
 
		processWin.show();
		processWin.body.appendChild(frame);
 
		// Seems to be workaround for IE having name readonly.
		if(Ext.isIE) {
			document.frames[id].name = id;
		}

		var form = new Ext.FormPanel({
			url: APERTE_PROCESS_EDITOR_URL,
			renderTo:Ext.getBody(),
			standardSubmit:true,
			method:'POST',
			defaultType:'hidden',
			items:[	new Ext.form.TextField({
			            id:'processConfig',
						name:'processConfig',
						fieldLabel:'processConfig',
						inputType:'text',
						value: processConf
					}),
					new Ext.form.TextField({
					    id:'processModelDirectory',
					    name:'processModelDirectory',
					    fieldLabel:'processModelDirectory',
					    inputType:'text',
					    value: null //?
					}),
					new Ext.form.TextField({
					    id:'callbackUrl',
						name:'callbackUrl',
						fieldLabel:'callbackUrl',
						inputType:'text',
						value: callbackUrl
					}),
					new Ext.form.TextField({
					    id:'restartApplication',
						name:'restartApplication',
						fieldLabel:'restartApplication',
						inputType:'text',
						value:'1'
					})
		    ]
		});
 
		form.getForm().el.dom.action = form.url;
		form.getForm().el.dom.target = id;
			
		if(!Ext.isGecko) {
			var mask = new Ext.LoadMask(processWin.id, {msg:"Loading..."});
			mask.show();
		}
 
		Ext.EventManager.on(frame, 'load', function() {
			if(mask !== undefined) { mask.hide(); }
			form.destroy();
		});
 
		Ext.emptyFn.defer(200); // frame on ready?
		form.getForm().submit();
    }
});
