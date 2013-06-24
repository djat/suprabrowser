var shade_object =
{
    image_id    : null,
    node_id     : null,
    timer       : null,
    orig_height : null,

    shade_init : function (node)
    {
        this.image_id = node.id;

        this.node_id = node.parentNode.parentNode.id;

        this.orig_height = document.getElementById (this.node_id).offsetHeight;

        shade_object.shade_window ();
    },

    shade_window : function ()
    {
        var resizer = document.getElementById ("rs" + this.image_id.substr (2, this.image_id.length - 2));

        resizer.style.display = "none";

        shade_object.shade ();

        image = document.getElementById (this.image_id);

        image.src = "/icons/icon_unshade_window.gif";

        image.setAttribute ("onclick", "unshade_object.unshade_init (this, '" + this.orig_height + "')");
    },

    shade : function ()
    {
        node = document.getElementById (this.node_id)

        node_height = parseInt (node.offsetHeight);

        if (node_height < 20) {
            node.style.height = "15px;";


            clearTimeout (this.timer);

            return;
        }

        node_height -= 13;

        node.style.height = node_height + "px";

        this.timer = setTimeout ("shade_object.shade ()", 1);
    }
}

