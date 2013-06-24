var blog_object =
{
    highlight : function ()
    {
        var nodes = document.evaluate ("//div[@class='highlight']", document, null, 6, null);
        var node_length = document.evaluate ("//div[@class='highlight']", document,null, 6, null).snapshotLength;

        for (var i = 0; i < node_length; i ++) {
            nodes.snapshotItem (i).style.backgroundColor = "#000080";
            nodes.snapshotItem (i).style.color = "white";
            nodes.snapshotItem (i).style.cursor = "pointer"; 
        }
    },

    unhighlight : function ()
    {
        var nodes = document.evaluate ("//div[@class='highlight']", document, null, 6, null);

        var node_length = document.evaluate ("//div[@class='highlight']", document, null, 6, null).snapshotLength;

        for (var i = 0; i < node_length; i ++) {
            nodes.snapshotItem (i).style.backgroundColor = "";
            nodes.snapshotItem (i).style.color  = "";
            nodes.snapshotItem (i).style.cursor = "";
        }
    },

   setXMLUpdate : function (xml)
    {
	var input = document.getElementById("xml_to_add");
	input.setAttribute("value",xml);

    }
}
