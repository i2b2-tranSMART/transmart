<!DOCTYPE html>
<html>
    <head>
	<meta name="layout" content="main"/>
	<title>Create Feedback</title>
	<asset:stylesheet href="feedback.css"/>
    </head>

    <body>
	<div id="header-div" class="header-div">
	    <g:render template='/layouts/commonheader' model="[app: 'feedback']"/>
	</div>
	<div class="nav">
	    <span class="menuButton"><g:link class="list" action="list">Feedback List</g:link></span>
	</div>

	<div class="body">
	    <h1>Create Feedback</h1>
	    <g:if test="${flash.message}">
		<div class="message">${flash.message}</div>
	    </g:if>
	    <g:hasErrors bean="${feedback}">
		<div class="errors">
		    <g:renderErrors bean="${feedback}" as="list"/>
		</div>
	    </g:hasErrors>
	    <g:form action='save'>
		<div class="dialog">
		    <table>
			<tbody>
			    <tr class="prop">
				<td valign="top" class="name">
				    <label for="searchUserId">User:</label>
				</td>
				<td valign="top" class="value ${hasErrors(bean: feedback, field: 'searchUserId', 'errors')}">
				    <input type="text" id="searchUserId" name="searchUserId"
					   value="${fieldValue(bean: feedback, field: 'searchUserId')}"/>
				</td>
			    </tr>
			    <tr class="prop">
				<td valign="top" class="name">
				    <label for="createDate">Created:</label>
				</td>
				<td valign="top" class="value ${hasErrors(bean: feedback, field: 'createDate', 'errors')}">
				    <g:datePicker name="createDate" value="${feedback?.createDate}" precision="day"/>
				</td>
			    </tr>
			    <tr class="prop">
				<td valign="top" class="name">
				    <label for="appVersion">Version:</label>
				</td>
				<td valign="top" class="value ${hasErrors(bean: feedback, field: 'appVersion', 'errors')}">
				    <input type="text" id="appVersion" name="appVersion"
					   value="${fieldValue(bean: feedback, field: 'appVersion')}"/>
				</td>
			    </tr>
			    <tr class="prop">
				<td valign="top" class="name">
				    <label for="feedbackText">Feedback:</label>
				</td>
				<td valign="top" class="value ${hasErrors(bean: feedback, field: 'feedbackText', 'errors')}">
				    <textarea id="feedbackText" name="feedbackText" rows="10"
					      cols="100">${fieldValue(bean: feedback, field: 'feedbackText')}</textarea>
				</td>
			    </tr>
			</tbody>
		    </table>
		</div>

		<div class="buttons">
		    <span class="button"><input class="save" type="submit" value="Create"/></span>
		</div>
	    </g:form>
	</div>
    </body>
</html>
