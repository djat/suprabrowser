var debug_object = {

    clear_bug : function ()
    {
        var text_box = document.getElementById ("debug_text");
        text_box.value = "";
    },

    show_bug : function (bug_text)
    {
        var text_box = document.getElementById ("debug_text");
        text_box.value = bug_text;
    }
}
