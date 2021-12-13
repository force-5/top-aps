<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Select Date/Time</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLink(uri: '/')}">Home</a></span>
</div>
<div class="body">
    <h1>Select Future Date & Time</h1>

    <div class="list">
        <g:form action="triggerUptoDateTime" method='post'>
            <calendar:datePicker style="float:left" name="futureDate" id="futureDate"/>
            <br/>
            <br/>
            Hours:
            <select name="futureDate_hours">
                <g:each in="${0..23}" var="hour">
                    <option value="${hour}">
                        ${hour}
                    </option>
                </g:each>
            </select>
            <br/>
            Minutes:
            <select name="futureDate_minutes">
                <g:each in="${0..59}" var="min">
                    <option value="${min}">
                        ${min}
                    </option>
                </g:each>
            </select>
            <br/>
            <br/>
            <g:submitButton name="submit" value="OK"/>
        </g:form>
    </div>
</div>
</body>
</html>
