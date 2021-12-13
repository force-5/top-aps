<%--
  Created by IntelliJ IDEA.
  User: kushal
  Date: 3/9/11
  Time: 11:45 AM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
  <head><title>List of Kick Off Jobs</title>
    <g:render template="/layouts/importCssAndJs"/>
      <meta name="layout" content="main"/>

  </head>
  <body>

  <h1>Jobs Details</h1>
  <table>
      <thead>
      <tr>
      <th>Job Name</th>
      <th>Trigger Name</th>
      <th>Cron Expression</th>
      <th>Trigger Now</th>
      </tr>
      </thead>
      <tbody>
      <g:each in="${jobs}" status="i" var="job">
          <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
              <td>${job.name}</td>
              <td>${job.triggerName}</td>
              <td>${job.cronExpression}</td>
              <td><input type="button" class="button" value="Trigger" style="cursor:pointer" onclick="window.location.href='${createLink(controller:'util',action:'triggerJob',params:[triggerName:job.triggerName,triggerGroup:job.triggerGroup])}'"></td>
          </tr>
      </g:each>
      </tbody>
  </table>

  </body>
</html>