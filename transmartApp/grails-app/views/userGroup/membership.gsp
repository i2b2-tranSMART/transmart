<!DOCTYPE html>
<html>
    <head>
	<meta name="layout" content="admin"/>
	<title>Group Membership</title>
	<style>
    p {
        width: 440px;
    }

    .ext-ie .x-form-text {
        position: static !important;
    }
	</style>
	<asset:javascript src="jquery-plugin.js"/>
    </head>

    <body>
	<div class="body">
	    <h1>Manage Group Membership</h1>
	    <g:if test="${flash.message}">
		<div class="message">${flash.message}</div>
	    </g:if>
	    <div id="divuser" style="width:100%; font:11px tahoma, arial, helvetica, sans-serif">
		please select a user then select groups<br/>
		<b>Search User</b><br/>
		    <input type="text" size="80" id="searchUsers" autocomplete="off"/>
	    </div>
	    <script>
        var pageInfo = { basePath: "${request.getContextPath()}" };
  
        createUserSearchBox2('${request.getContextPath()}/userGroup/ajaxGetUserSearchBoxData', 440);

        function searchgroup() {
            var pid = document.getElementById('currentprincipalid').value;
            if (pid == null || pid == '') {
                alert("Please select a user first");
                return false;
            }

        ${remoteFunction(action: 'searchGroupsWithoutUser',
	                 update: [success: 'groups', failure: ''],
	                 params: 'jQuery(\'#searchtext\').serialize()+\'&id=\'+pid')};
            return false;
        }
	    </script>
	    <table>
		<tr>
		    <td>&nbsp;</td>
		    <td>&nbsp;</td>
		    <td>
			<input name="searchtext" id="searchtext"/>
			<button class="" onclick="searchgroup();">Search Groups</button>
		    </td>
		</tr>
		<tr>
		    <td><b>Member of these groups</b></td>
		    <td>&nbsp;</td>
		    <td><b>Available groups</b></td>
		</tr>
		<tr id="groups">
		    <g:render template='addremoveg' model="[groupswithoutuser: groupswithoutuser]"/>
		</tr>
	    </table>
	</div>
	<input type="hidden" id="currentprincipalid"/>
    </body>
</html>
