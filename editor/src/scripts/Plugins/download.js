if (!ORYX.Plugins) 
    ORYX.Plugins = new Object();

ORYX.Plugins.Download = Clazz.extend({
	
    facade: undefined,
	
	construct: function(facade){
		this.facade = facade;
		this.facade.offer({
			'name': ORYX.I18N.Deploy.deploy,
            'functionality': this.download.bind(this),
            'group': ORYX.I18N.Deploy.group,
            'icon': ORYX.PATH + "images/download.png",
            'description': ORYX.I18N.Deploy.deployDesc,
			'index': 2,
			'minShape': 0,
			'maxShape': 0
		});
		
	},
	
    processDownload: function(modelInfo){

		if (!modelInfo)
		    return;
		var value = window.document.title || document.getElementsByTagName("title")[0].childNodes[0].nodeValue;
		
		if (value.startsWith("*")){
		   Ext.Msg.alert("TITLE", "Save your model before downloading.").setIcon(Ext.Msg.WARNING).getDialog().setWidth(260).center().syncSize();
		   delete this.downloading;
		   return;
		}
		
        var params = {
       		name: modelInfo.name,
			parent: modelInfo.parent
        };

		var failure = function(transport) {
						
			if(transport.status && transport.status === 401) {
				    Ext.Msg.alert(ORYX.I18N.Oryx.title, ORYX.I18N.Save.notAuthorized).setIcon(Ext.Msg.WARNING).getDialog().setWidth(260).center().syncSize();
			} else if(transport.status && transport.status === 403) {
				    Ext.Msg.alert(ORYX.I18N.Oryx.title, ORYX.I18N.Save.noRights).setIcon(Ext.Msg.WARNING).getDialog().setWidth(260).center().syncSize();
			} else if(transport.statusText === "transaction aborted") {
				    Ext.Msg.alert(ORYX.I18N.Oryx.title, ORYX.I18N.Save.transAborted).setIcon(Ext.Msg.WARNING).getDialog().setWidth(260).center().syncSize();
			} else if(transport.statusText === "communication failure") {
				    Ext.Msg.alert(ORYX.I18N.Oryx.title, ORYX.I18N.Save.comFailed).setIcon(Ext.Msg.WARNING).getDialog().setWidth(260).center().syncSize();
			} else {
					var msg = transport.responseText;
					if (msg != null && msg != "") {
					  msg = Ext.decode(msg);
					  msg = msg == null ? ORYX.I18N.Save.failed : msg.message;
					}
					// TODO Ext.Msg does not support new line, becuse it renders <span> HTML element, figure out how to display multiline messages
					Ext.Msg.alert(ORYX.I18N.Oryx.title, msg).setIcon(Ext.Msg.WARNING).getDialog().setWidth(260).center().syncSize();
			}
						
			delete this.downloading;
						
		}.bind(this);
				
		this.sendDownloadRequest('POST', APERTE_DOWNLOAD_URL, params);
		
    },
	
	
	
	sendDownloadRequest: function(method, url, params){

        try {
            Ext.destroy(Ext.get('downloadIframe'));
        }
        catch(e) {}
        url = url + "?name=" + escape(params.name) + "&parent-id=" + escape(params.parent);
        var iframe = Ext.getBody().createChild({
            tag: 'iframe',
            cls: 'x-hidden',
            id:'downloadIframe'
        });
        iframe.dom.src = url;
	},

	 download: function(){

            window.setTimeout((function(){
        		var meta = this.facade.getModelMetaData();
    	     	this.processDownload(meta);
    	    }).bind(this), 10);
            return true;
        }

});
