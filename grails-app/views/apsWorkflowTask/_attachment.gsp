<br/><br/>
<div class="clearfix" style="padding-bottom: 10px;">
    <div id="boxbor_left">Attach Files:<br/>
        <input size="5" type="file" onchange="addSelectedFile(this);"/>
    </div>
    <div id="attachmentFileBox" class="affidavitList">
        <ul>
            <g:each in="${attachments}" var="attachment">
                <li>
                    <g:link controller="util" action="downloadFile" id="${attachment.id}" params ="[className: 'com.force5solutions.care.aps.ApsDataFile', fieldName: 'bytes']">
                        <img src="${createLinkTo(dir: 'images', file: 'magnifing.gif')}" alt="Magnify" border="0"/>
                    </g:link>
                    <img class="crossButton" src="${createLinkTo(dir: 'images', file: 'cross1.gif')}" alt="Delete" onclick='return removeThisFile(this);'/>
                    <span>${attachment.fileName}</span>
                    <g:hiddenField name="remainingAttachments" value="${attachment.id}"/>
                </li>
            </g:each>
        </ul>

    </div>
</div>
<script type="text/javascript">
    var counter1 = 0;

    function getFileNameOnly(fullName) {
        return (fullName.match(/[^\/\\]+$/));
    }

    function removeThisFile(imageTag) {
        jQuery(imageTag).parent('li').remove();
        return false;
    }

    function addSelectedFile(inputFile) {

        var clonedInputFile = jQuery(inputFile);
        var selectedFileName = getFileNameOnly(clonedInputFile.attr('value'));
        if (selectedFileName) {
            counter1++;
            var nameOfClonedInput = 'multiFiles' + counter1;
            clonedInputFile.attr('name', nameOfClonedInput);
            clonedInputFile.css('display', 'none');
            var ULElement = jQuery('.affidavitList ul');
            jQuery("<li></li>").appendTo(ULElement);
            var lastLIElement = jQuery('.affidavitList ul li:last');
            clonedInputFile.appendTo(lastLIElement);
            var crossFile = "${createLinkTo(dir:'images', file:'cross1.gif')}";
            jQuery('<img src="' + crossFile + '" alt="Delete" onclick="return removeThisFile(this);"/>').appendTo(lastLIElement);
            jQuery("<span>" + selectedFileName +"</span>").appendTo(lastLIElement);

            jQuery('<input  size="5"  type="file" onchange="addSelectedFile(this);"/>').appendTo('#boxbor_left');
            //Remove the li elements that dont have input of type file or hidden.    **** a bug in IE7
            jQuery('#attachmentFileBox li').not('li:has(input)').each(function() {
                jQuery(this).remove()
            });
            if(jQuery('#boxbor_left input:file').size()==2){
              jQuery('#boxbor_left input:file:last').remove();
            }
        }
    }

</script>
