<g:if test="${searchresult?.result?.groupByExp}"><div id='ptfilterresult'></g:if>
<g:else><div id='ptfilterresult_tea'></g:else>

<g:if test="${searchresult?.result == null || searchresult?.result?.analysisCount == 0}">
    <g:render template='/search/noResult'/>
</g:if>
<g:else>
    <g:set var="ear" value="${searchresult?.result?.expAnalysisResults[0]}"/>
    <g:set var="teaDisplay" value="${(ear?.bioMarkerCt > 1) as boolean}"/>

    <!-- results header -->
    <p style="font-weight:bold; font-size:11px;padding-left:5px;padding-bottom:5px; padding-top:5px;">
        <g:if test="${searchresult?.result?.groupByExp}">Study result:&nbsp;&nbsp;${searchresult?.result?.analysisCount}
            <g:if test="${searchresult?.result?.analysisCount > 1}">analyses</g:if>
            <g:else>analysis</g:else>
            &nbsp;from ${searchresult?.result?.expCount} experiment(s)
        </g:if>
        <g:else>
            Analysis result:&nbsp;&nbsp;${searchresult?.result?.analysisCount}
            <g:if test="${searchresult?.result?.analysisCount > 1}">analyses</g:if>
            <g:else>analysis</g:else>
            &nbsp;from ${searchresult?.result?.expCount} experiment(s)
            <g:if test="${teaDisplay}">&nbsp;[${ear.analysisCount - ear.inSignificantAnalCount} Significant TEA / ${ear.inSignificantAnalCount} Insignificant TEA]
                <br><span
			style="color:red;">Note, only significant TEA Analyses are displayed!</span>
            </g:if>
        </g:else>
    </p>

    <g:if test="${searchresult?.result?.groupByExp}">

        <!--  paging tabs -->
        <div id="expListDiv">
            <div class="paginateButtons">
                <g:remotePaginate update="ptfilterresult" total="${searchresult?.experimentCount}"
                                  controller="experimentAnalysis" action="datasourceResult"
                                  maxsteps="${grailsApplication.config.com.recomdata.search.paginate.maxsteps}"
                                  max="${grailsApplication.config.com.recomdata.search.paginate.max}"/>
            </div>

            <table width="100%" class="trborderbottom">
                <g:each in="${searchresult.result.expAnalysisResults}" status="ti" var="expAnalysisResult">
                    <tr>
                        <td width="100%" class="bottom">
                            <table width="100%">
                                <tr style="padding-bottom: 5px">
                                    <td style="padding: 5px 0px 10px 5px; margin-top: 5px;">
					<div id="TrialDet_${expAnalysisResult.experimentId}_anchor">
                                            <a onclick="javascript:if (divIsEmpty('${expAnalysisResult.experimentId}_detail')) {
							var ldiv = '${expAnalysisResult.experimentId}_detail_loading';
							${remoteFunction(action:'getAnalysis',controller:'experimentAnalysis', id:expAnalysisResult.experimentId, before:'toggleVisible(ldiv)',onComplete:'toggleVisible(ldiv)', update:expAnalysisResult.experimentId+'_detail')}
							};
							toggleDetail('${expAnalysisResult.experimentId}')">
					</div>
					<div id="${expAnalysisResult.experimentId}_fclose"
                                             style="visibility: hidden; display: none; width: 16px;">
                                            <img alt="" src="${resource(dir: 'images', file: 'folder-minus.gif')}" style="vertical-align: middle;"/>
					</div>

					<div id="${expAnalysisResult.experimentId}_fopen"
                                             style="display: inline; width: 16px;">
                                            <img alt="" src="${resource(dir: 'images', file: 'folder-plus.gif')}" style="vertical-align: middle;"/>
					</div>
                                        </a>
                                        <a onclick="showDialog('TrialDet_${expAnalysisResult.experimentId}', {
						    title: '${expAnalysisResult.experiment.accession}',
						    url: '${createLink(action:'expDetail', id:expAnalysisResult.experimentId)}'
						    });"
                                           onmouseover="delayedTask.delay(2000, showDialog, this, ['TrialDet_${expAnalysisResult.experimentId}', {
							title: '${expAnalysisResult.experimentId}',
							url: '${createLink(action:'expDetail', id:expAnalysisResult.experimentId)}'
							}]);"
                                           onmouseout="delayedTask.cancel();">
					    <img alt="" src="${resource(dir: 'images', file: 'view_detailed.png')}" style="vertical-align: top;"/>
					    <b><span style="color: #339933;">${expAnalysisResult.experiment.accession}:</span>
						&nbsp;&nbsp;${expAnalysisResult.experiment.title}
					    </b>
                                        </a>
                                        <br>
                                        &nbsp;&nbsp;&nbsp; - ${expAnalysisResult.analysisCount}
                                        <g:if test="${expAnalysisResult.analysisCount > 1}">analyses found</g:if>
                                        <g:else>analysis found</g:else>
                                        &nbsp;&nbsp;&nbsp;
                                        <g:if test="${!expAnalysisResult.experiment.files.isEmpty()}">
					    <g:set var="fcount" value="${0}"/>
					    <g:each in="${expAnalysisResult.experiment.files}" var="plan">
                                                <g:if test="${plan.content.type != 'Experiment Web Link'}">
						    <g:set var="fcount" value="${fcount++}"/>
						    <g:if test="${fcount > 1}">,</g:if>
						    <g:createFileLink content="${plan.content}" displayLabel="${plan.type}"/>
                                                </g:if>
					    </g:each>
                                        </g:if>
                                    </td>
                                </tr>
                                <tr>
                                    <td style="padding-left: 20px;">
                                        <g:waitIndicator
                                            divId="${expAnalysisResult.experimentId}_detail_loading"/>
                                        <div id="${expAnalysisResult.experimentId}_detail"
                                             class="gtb1"
                                             style="display: none;"></div>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                </g:each>
            </table>
        </div>

    </g:if>
    <g:else>
        <!--  paging tabs -->
        <div id="analysisListDiv">
            <div class="paginateButtons">
                <g:remotePaginate update="ptfilterresult_tea"
                                  controller="experimentAnalysis"
                                  action="pageTEAAnalysisView"
                                  total="${ear.analysisCount - ear.inSignificantAnalCount}"
                                  max="${grailsApplication.config.com.recomdata.search.paginate.max}"/>
            </div>

            <!-- display Analyses with or without TEA score -->
            <table style="background-color: #ffffff;" width="100%">
                <tbody>
                    <g:set var="counter" value="${1}"/>
                    <g:each in="${ear.pagedAnalysisList}" status="i" var="analysisResult">
			<g:if test="${!teaDisplay || analysisResult.bSignificantTEA}">
                            <g:set var="counter" value="${counter + 1}"/>
                            <g:render template='/trial/teaAnalysisSummary'
                                      model="[analysisResult: analysisResult, counter: counter, showTrial: true]"/>
			</g:if>
                    </g:each>
                </tbody>
            </table>
        </div>
    </g:else>

</g:else>
</div>
