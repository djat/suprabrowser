/********************************

    Scrolling/Expanding List
    Version 1.0
    last revision: 04.26.2004
    steve@slayeroffice.com

    http://slayeroffice.com/code/scroll_menu/

    Please notify me of any improvments you make on this
    code so that I can update the version hosted on
    slayeroffice

    please leave this notice intact.

********************************/

var interval = null;

var items = 1;

var d = document;
var expander;             // object reference for the plus/minus div
var lContainer;             // object reference for the UL

var currentTop = 0;            // the current Y position of the UL
var zInterval = null;        // animation thread interval
var direction;            // direction we're scrolling the UL. 0==up, 1==down
var startTop = 0;            // the starting top of the UL

var scrollRate = 1;            // initial rate of scrolling
var scrollTick = 0;            // keeps track of long we've scrolled so we can slow it down accordingly

var listExpand = true;        // boolean to tell us if we're expanding or contracting the list
var listHeight = 150;            // the current height of the UL
var isExpanded = false;        // boolean to denote if the list is expanded or not. initiliazed to true as it will be set to false as soon as the expand control is clicked.

var MAX_SCROLL_TICK = 1;        // the maximum value of scrollTick before it's reset
var LI_PADDING = 2;        // the LI's padding value. used to compensate for overall dimensions
var LI_HEIGHT = 22;        // the height of the LI
var MIN_LIST_HEIGHT = 150;        // contracted height of the list
var REBOUND = 0;        // the value of scrollRate when we stop scrolling
var FAST_EXPAND = 5;        // the initial rate of list expansion
var SLOW_EXPAND = 1;        // the end rate of expansion
var SPEED_TRANSITION = 20;    // when this value + the MAX or MIN list height is reached, we set scrollRate to its slower rate
var INLINE_EXPANDED = 0;

function show_comment (n)
{
    var insert = document.getElementsByTagName ("html") [0];
    var div = d.createElement ("div");
    div.setAttribute ("class", "comment_display");
    div.appendChild (document.createTextNode (unescape (n)));
    insert.appendChild (div);
}

function init ()
{
    if (!d.getElementById) {
        return; // bail out if this is an older browser
    }

    up = d.getElementById ("upArrow");
    down = d.getElementById ("downArrow");

    // apply onclick behaviors to the up arrow, down arrow and expansion control elements
    down.onmouseover = function ()
    {
        interval = setInterval ("scrollObjects (0)", 100);
    }

    up.onmouseover = function () 
    {
        interval = setInterval ("scrollObjects (1)", 100);
    }

    down.onmouseout = function ()
    {
        clearInterval (interval);
    }

    up.onmouseout = function ()
    {
        clearInterval (interval);
    }

    expander = d.getElementById ("changeSize");

    expander.onclick = function ()
    {
        if (!isExpanded) {
            isExpanded = true;
        }

        changeListSize ();
    }

    lContainer = d.getElementById ("listContainer");

    var xml_comment_unique = document.implementation.createDocument ("", "", null);
    xml_comment_unique.async = false;
    xml_comment_unique.load ("comments.xml");

    var comments = xml_comment_unique.getElementsByTagName ("comment");

    for (var i = 0; i < comments.length; i ++) {

        var div = d.createElement ("div");

        div.setAttribute ("id", "div_" + i);
        div.setAttribute ("class", "subject");

        var subdiv = d.createElement ("div");
        subdiv.setAttribute ("id", "subdiv_" + i);

        var content = comments [i].getElementsByTagName ("content") [0].firstChild.data;

        var read_a = document.createElement ("a");
        read_a.setAttribute ("href", "#");
        read_a.setAttribute ("onclick", "show_comment ('" + escape (content) + "');");
        read_a.appendChild (d.createTextNode ("[read entire comment]"));

        subdiv.appendChild (read_a);

        subdiv.style.display = "none";

        var ex_div = d.createElement ("div");

        ex_div.setAttribute ("class", "expansion");
        ex_div.setAttribute ("id", "ex_div_" + i);
        ex_div.style.display = "none";

        div.setAttribute ("onmouseover", "div_mouseover (" + i + ");");
        div.setAttribute ("onmouseout", "div_mouseout (" + i + ");");

        div.appendChild (d.createTextNode (comments [i].getElementsByTagName ("subject") [0].firstChild.data.substring (0, 40)));
        div.appendChild (subdiv);
        
        ex_div.appendChild (d.createTextNode (content.substring (0, 100)));

        if (content.length > 100) {
            ex_div.appendChild (d.createTextNode ("..."));
        }

        lContainer.appendChild (div);
        lContainer.appendChild (ex_div);

        items ++;
    }

    d.getElementById ("nContainer").style.height = MIN_LIST_HEIGHT + "px";
}

function div_mouseover (id)
{
    var div = d.getElementById ("ex_div_" + id);
    div.style.display = "block";

    var subdiv = d.getElementById ("subdiv_" + id);
    subdiv.style.display = "block";
}

function div_mouseout (id)
{
    var div = document.getElementById ("ex_div_" + id);
    div.style.display = "none";

    var subdiv = d.getElementById ("subdiv_" + id);
    subdiv.style.display = "none";
}

function scrollObjects (dir)
{
    if ((!dir && currentTop <= (-1 * items * LI_HEIGHT)) || (dir && currentTop >= 0 )) {
        return; // dont scroll up if we're at the top or down if at the bottom of the list
    }

    direction = dir;
    animate ();
//    zInterval = setInterval ("animate ()", 100);
}

function animate ()
{
    // increment or decrement currentTop based on direction

    if (!direction) {
        currentTop -= LI_HEIGHT;
    } else {
        currentTop += LI_HEIGHT;
    }

    scrollTick ++;

    lContainer.style.top = currentTop + "px";
}

function changeListSize ()
{
    listExpand = listExpand ? false : true;
    clearInterval (zInterval);
    zInterval = setInterval ("expandList ()", 20);
}

function expandList ()
{
return;
}
