<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
    <head>
    </head>
    <body>
	<asset:javascript src="r-modules.js"/>

	<div id="analysisWidget">

	    <h2>
		Variable Selection
		<a target="_blank" href="${grailsApplication.config.org.transmartproject.helpUrls.markerSelection ?: "JavaScript:D2H_ShowHelp(1508,helpURL,'wndExternal',CTXT_DISPLAY_FULLHELP )"}">
		    <img src="${resource(dir: 'images/help', file: 'helpicon_white.jpg')}" alt="Help"/>
		</a>
	    </h2>

	    <div id="analysisForm">
		<fieldset class="inputFields">

		    %{--High dimensional input--}%
		    <div class="highDimContainer">
			<span>Select a High Dimensional Data node from the Data Set Explorer Tree and drag it into the box.<br />
			    Note: There must be two subsets in the Comparison panel. Normally, S1 would be considered
			    the reference group (for example, a control group), and S2, the comparison group (experimental group).</span>
			<div id='divIndependentVariable' class="queryGroupIncludeSmall highDimBox"></div>
			<div class="highDimBtns">
			    <button type="button" onclick="highDimensionalData.gather_high_dimensional_data('divIndependentVariable')">High Dimensional Data</button>
			    <input type="hidden" id="multipleSubsets" name="multipleSubsets" value="true" />
			    <button type="button" onclick="markerSelectionView.clear_high_dimensional_input('divIndependentVariable')">Clear</button>
			</div>
		    </div>

		    %{--Display independent variable--}%
		    <div id="displaydivIndependentVariable" class="independentVars"></div>

		    <label for="txtNumberOfMarkers">Number of Markers:</label>
		    <input type="text" id="txtNumberOfMarkers" value="50"/>

		</fieldset>

		<fieldset class="toolFields">
		    <div>
			<input type="checkbox" id="chkGroupBySubject" name="doGroupBySubject">
			<span>Group by subject (instead of node) for multiple nodes</span>
		    </div>
		    <div>
			<input type="checkbox" id="chkCalculateZscore" name="calculateZscore">
			<span>Calculate z-score on the fly</span>
		    </div>
		    <input type="button" value="Run" onClick="markerSelectionView.submit_job(this.form);" class="runAnalysisBtn">
		</fieldset>
	    </div>

	</div>
    </body>
</html>
