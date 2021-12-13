<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>TOP Human Task</title>
    <style type="text/css">
    .scrollTable {
        width: 700px;
    }
    </style>
</head>
<body>
<br/>
<div id="wrapper">
    <h1>TOP Human Task</h1>
    <br/>
    <div style="margin:0 50px;font-size:14px;">
        <h3>Comments</h3><br/>
        <div>
            <span>
                Hello <care:fullName slid="${session.loggedUser}"/>,
                <br/>
                <br/>
                This request NOW stands cancelled.
                <br/>
                <br/>
                This task was marked cancelled by: <b>${task?.actorSlid}</b> at <b><g:formatDate date="${task?.lastUpdated}" format="MM/dd/yyyy, hh:mm a"/></b>
                <br/>
                <br/>
                <br/>
            </span>
        </div>
        <g:form action="changeTaskStatus">
            <g:hiddenField name="id" value="${task?.id}"/>
            <div style="text-align:center;">
                <input class="buttonWidth170px" type="submit" value="Don't Show this To Me Again" />
            </div>
        </g:form>
    </div>
</div>
</body>
</html>
