<g:if test="${tagtype != 'ALL'}">
    Terms:<br/>
    <g:select class="searchform" name="tagterm" from="${tags}" multiple="multiple" size="5"/>
</g:if>
