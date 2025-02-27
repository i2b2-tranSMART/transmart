<p><span style="color: red; ">${warningMsg}</span></p>
<table class="searchform">
    <tr>
	<td style='white-space: nowrap'>IGV Datasets in Subset 1:</td>
	<td>&nbsp;&nbsp;&nbsp;&nbsp;</td>
	<td>${snpDatasetNum_1}</td>
    </tr>
    <tr>
	<td style='white-space: nowrap'>IGV Datasets in Subset 2:</td>
	<td>&nbsp;&nbsp;&nbsp;&nbsp;</td>
	<td>${snpDatasetNum_2}</td>
    </tr>
    <tr>
	<td>&nbsp;</td>
	<td>&nbsp;&nbsp;&nbsp;&nbsp;</td>
	<td>&nbsp;</td>
    </tr>
    <tr>
	<td valign='top' style='white-space: nowrap'>Select Chromosomes:</td>
        <td>&nbsp;&nbsp;&nbsp;&nbsp;</td>
	<td><g:select name="igvChroms" from="${chroms}" value="${chromDefault}" multiple="multiple" size="5"/></td>
    </tr>
    <tr>
	<td valign='top' style='white-space: nowrap'>Selected Genes:</td>
        <td>&nbsp;&nbsp;&nbsp;&nbsp;</td>
	<td>
	    <input type="text" size="35" id="selectedGenesIgv" autocomplete="off"/>
            <div id="divPathwayIgv" style="width:100%; font:11px tahoma, arial, helvetica, sans-serif">
		<br>Add a Gene:<br>
		<input type="text" size="35" id="searchPathwayIgv" autocomplete="off"/>
		<input type="hidden" id="selectedGenesAndIdIgv"/>
            </div>
	</td>
    </tr>
    <tr>
	<td>&nbsp;</td>
	<td>&nbsp;&nbsp;&nbsp;&nbsp;</td>
	<td>&nbsp;</td>
    </tr>
    <tr>
	<td valign='top' style='white-space: nowrap'>Selected SNPs:</td>
        <td>&nbsp;&nbsp;&nbsp;&nbsp;</td>
	<td><input type="text" size="35" id="selectedSNPsIgv" autocomplete="off"/></td>
    </tr>
</table>

<script>
    showPathwaySearchBox('selectedGenesIgv', 'selectedGenesAndIdIgv', 'searchPathwayIgv', 'divPathwayIgv');
</script>
