var unshade_object =
{
    image_id : null,
    node_id  : null,
    height   : null,

    unshade_init : function (node, height)
    {
        this.image_id  = node.id;

        this.height = height;

        this.node_id = node.parentNode.parentNode.id;

        unshade_object.unshade_window ();
    },

    unshade_window : function ()
    {

        node = document.getElementById (this.node_id);

        node.style.height = this.height + "px";

        var image = document.getElementById (this.image_id);

        image.src = "/icons/icon_shade_window.gif";

        image.setAttribute ("onclick", "shade_object.shade_init (this)");

        var resizer = document.getElementById ("rs" + this.image_id.substr (2, this.image_id.length - 2));

        resizer.style.display = "block";
    }
}
