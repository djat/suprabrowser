var insert_message_object = {
	insert_message : function (giver, moment, subject, message_id, resp, owner) {
		document.body.innerHTML = document.body.innerHTML+this.create_message_string(giver, moment, subject, message_id, resp, owner);
	},

	create_message_string : function(giver, moment, subject, message_id, resp, owner) {
		
		var message_string;
		var resp_style; 
		var giver_style;
		var time_style;
		if(owner==giver) {
			resp_style = "resp";
			giver_style = "send";
			time_style = "time";
		} else {
			resp_style = "resp_incom";
			giver_style = "send_incom";
			time_style = "time_incom";
		}
		message_string = "<div style=\"background-color:white\" id=\""+message_id+"\""+"ondblclick=\"scroller_object.on_dbl_click('"+message_id+"')\" onClick=\"scroller_object.on_mouse_click('"+message_id+"')\"><font class=\""+giver_style+"\">"+giver+":</font>"+"<font class=\""+time_style+"\">"+moment+"</font>"+(resp=="resp" ? "<font class=\""+resp_style+"\"><u>r</u></font>" : "")
+"<font class=\"subj\"> "+subject+"</font></div>";
		return message_string;
	}

}
