var richtext_object = {
	createRichTextElement : function () {
	var textFrames = document.getElementsByTagName("iframe");
	var textFrame = textFrames[0];
	var innerBody = textFrame.contentDocument.body.innerHTML;
        var elem = document.createElement("div");
	elem.setAttribute("id", "richtext");
	elem.setAttribute("value", innerBody);
	var body = document.getElementsByTagName("body")[0];
	body.appendChild(elem);
    },

    setTextToEditor : function (text) {
	var textFrames = document.getElementsByTagName("iframe");
	var textFrame = textFrames[0];
	var innerBody = textFrame.contentDocument.body;
	innerBody.innerHTML = text;
    },

    resizeTextEditor : function(height) {
	var textFrames = document.getElementsByTagName("iframe");
	var textFrame = textFrames[0];

	textFrame.style.display = "block";
	textFrame.style.height = height-52+"px";
    }

}
