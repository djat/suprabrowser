var page_setup_object = 
{
    body          : null,

    input_text    : null,
    input_status  : null,
    output_text   : null,
    output_status : null,

    init : function ()
    {
        page_setup_object.add_to_page ();

        drag_object.initialize ();
    },
    
    changeInput : function ()
    {
        scroller_object.input_ready ();
    },
	
    add_to_page : function ()
    {
        this.body = document.getElementsByTagName ("body") [0];

        this.input_text = document.createElement ("input");
        this.input_text.setAttribute ("id", "input_text");
        this.input_text.setAttribute ("type", "hidden");
        this.input_text.setAttribute ("value", "yo");

        this.input_status = document.createElement ("input");
        this.input_status.setAttribute ("id", "input_status");
        this.input_status.setAttribute ("type", "hidden");

        this.output_text = document.createElement ("input");
        this.output_text.setAttribute ("id", "output_text");
        this.output_text.setAttribute ("type", "hidden");

        this.output_status = document.createElement ("input");
        this.output_status.setAttribute ("id", "output_status");
        this.output_status.setAttribute ("type", "hidden");

        this.body.appendChild (this.input_text);
        this.body.appendChild (this.input_status);
        this.body.appendChild (this.output_text);
        this.body.appendChild (this.output_status);
    }
}

window.onload = page_setup_object.init;

