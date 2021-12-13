package com.force5solutions.care
class MultiSelectTagLib {

    static namespace = "ui"

    def multiSelect = {attrs ->
        if (!attrs.containsKey('name'))
            throwTagError("Tag [multiSelect] is missing required attribute [name]")
        if (!attrs.containsKey('from'))
            throwTagError("Tag [multiSelect] is missing required attribute [from]")

        String name = attrs.remove('name')
        def remainingList = attrs.remove('from')
        def selectedList = attrs.remove('value')
        def onchange = attrs.remove('onchange') ?: ''
        def cssStyle = attrs.remove('style') ?: ''                                          
        def cssClass = attrs.remove('class') ?: ''

        Map noSelection = attrs.remove('noSelection')
        remainingList = remainingList - [selectedList].flatten()
        out << g.render('template': '/multiSelect/multiSelect', 'plugin': 'multiSelect',
                model: ['name': name,
                        'remainingList': remainingList,
                        'selectedList': selectedList,
                        'onchange': onchange,
                        'cssStyle': cssStyle,
                        'cssClass': cssClass,
                        'noSelection': noSelection,
                ])
    }

    def resources = {
        out<<"""<link rel="stylesheet" href="${resource(dir: 'js/multi.select', file: 'multi.select.css')}"/>"""
        out << "\n"
        out << """<script type="text/javascript" src="${resource(dir: 'js/multi.select', file: 'multi.select.js')}"></script>"""
    }
}
