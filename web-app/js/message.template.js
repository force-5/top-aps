

function initializeTinyMCE(tagsMap) {
    tinymce.create('tinymce.plugins.ExamplePlugin', {
            createControl: function(n, cm) {
                if (n == 'myTagList') {
                    var mlb = cm.createListBox('myTagList', {
                        title : 'Select Tag',
                        onselect : function(selectedText) {
                            // Get method needs name of textarea as parameter and not id of textarea
                             tinyMCE.get('bodyTemplate').execCommand('mceInsertContent', false, selectedText);
                        }
                    });
                    // Add some values to the list box
                    jQuery.each(tagsMap,function(key,value){
                        mlb.add(key,value);
                    });
                    return mlb;
                }
                return null;
            }
        });


    tinymce.PluginManager.add('example', tinymce.plugins.ExamplePlugin);

    tinyMCE.init({
        plugins : 'example,preview', // - tells TinyMCE to skip the loading of the plugin
        mode : "textareas",
        theme : "advanced",
        editor_selector: 'mceEditor2',
        theme_advanced_buttons1 : "bold,italic,underline,strikethrough,|,sub,sup,|,numlist,bullist,|,outdent,indent,|,justifyleft,justifycenter,justifyright,justifyfull",
        theme_advanced_buttons2 : "myTagList",
        theme_advanced_buttons3 : "",
        theme_advanced_toolbar_location : "top",
        theme_advanced_toolbar_align : "left",
        theme_advanced_statusbar_location : "none"
    });
}

function initializeTinyMCEReadOnly() {
    tinyMCE.init({
        mode : "textareas",
        editor_selector: 'textarea1',
        theme:"advanced",
        readonly:true
    });
}