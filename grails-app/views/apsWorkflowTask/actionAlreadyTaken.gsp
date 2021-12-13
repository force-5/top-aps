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
        <div>
            <span>
                Hello <care:fullName slid="${session?.loggedUser}"/>,
                <br/>
                <br/>
                This request is now <b>${task.status}</b>. Action was taken by <b>${task?.actorSlid}</b> at <b>${task.lastUpdated?.myDateTimeFormat()}</b>.
                <br/>
                <br/>
            </span>
        </div>
        <g:form action="changeTaskStatus">
            <g:hiddenField name="id" value="${task?.id}"/>
            <div style="text-align:center;">
                <input class="buttonWidth170px" type="submit" value="Don't Show This Message Again" />
            </div>
        </g:form>
    </div>
</div>
</body>
</html>
