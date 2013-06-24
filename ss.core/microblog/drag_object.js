var max_zindex = 100;


var drag_object =
{
    z              : 0,
    x              : 0,
    y              : 0,
    offsetx        : null,
    offsety        : null,
    targetobj      : null,
    high           : 0,
    wide           : 0,
    dragapproved   : 0,
    resizeapproved : 0,

    initialize : function () {

        document.onmousedown = this.drag;

        document.onmouseup = function () {
            this.dragapproved = 0;
            this.resizeapproved = 0;
        }
    },

    drag : function (e)
    {
        var evtobj = window.event ? window.event : e;

        this.targetobj = window.event ? event.srcElement : e.target;

        if (this.targetobj.className == "drag") {

            this.dragapproved = 1;

            drag_object.focus (this.targetobj.parentNode);

            if (isNaN (parseInt (this.targetobj.parentNode.style.left))) {
                this.targetobj.parentNode.style.left = this.targetobj.parentNode.offsetLeft + "px";
            }

            if (isNaN (parseInt (this.targetobj.parentNode.style.top))) {
                this.targetobj.parentNode.style.top = this.targetobj.parentNode.offsetTop + "px";
            }

            this.offsetx = parseInt (this.targetobj.parentNode.style.left);
            this.offsety = parseInt (this.targetobj.parentNode.style.top);

            this.x = evtobj.clientX;
            this.y = evtobj.clientY;

            if (evtobj.preventDefault) {
                evtobj.preventDefault ();
            }

            document.onmousemove = drag_object.moveit;

        } else if (this.targetobj.className == "resize") {

            this.resizeapproved = 1;

            drag_object.focus (this.targetobj.parentNode);

            if (isNaN (parseInt (this.targetobj.parentNode.style.left))) {
                this.targetobj.parentNode.style.left = this.targetobj.parentNode.offsetLeft + "px";
            }

            if (isNaN (parseInt (this.targetobj.parentNode.style.top))) {
                this.targetobj.parentNode.style.top = this.targetobj.parentNode.offsetTop + "px";
            }

            this.high = this.targetobj.parentNode.offsetHeight;
            this.wide = this.targetobj.parentNode.offsetWidth;

            this.offsetx = parseInt (this.targetobj.parentNode.style.left);
            this.offsety = parseInt (this.targetobj.parentNode.style.top);

            this.x = evtobj.clientX;
            this.y = evtobj.clientY;

            if (evtobj.preventDefault) {
                evtobj.preventDefault ();
            }

            document.onmousemove = drag_object.resize;
        }
    },

    moveit : function (e)
    {
        var evtobj = window.event ? window.event : e;

        if (this.dragapproved == 1) {
            this.targetobj.parentNode.style.left = this.offsetx + evtobj.clientX - this.x + "px";
            this.targetobj.parentNode.style.top = this.offsety + evtobj.clientY - this.y + "px";
            return false;
        }
    },

    resize : function (e)
    {
        var evtobj = window.event ? window.event : e;

        if (this.resizeapproved == 1) {

            if (this.high + evtobj.clientY - this.y < 49) {
                this.targetobj.parentNode.style.height = 50 + "px";
                return false;
            }
            if (this.wide + evtobj.clientX - this.x < 49) {
                this.targetobj.parentNode.style.width = 50 + "px";
                return false;
            }

            this.targetobj.parentNode.style.height = this.high + evtobj.clientY - this.y + "px";
            this.targetobj.parentNode.style.width  = this.wide + evtobj.clientX - this.x + "px";
            return false;
        }
    },

    focus : function (node)
    {
        max_zindex ++;

        node.style.zIndex = max_zindex;
    },

    close_window : function (node)
    {
        node.parentNode.parentNode.style.display = "none";
    }
}
