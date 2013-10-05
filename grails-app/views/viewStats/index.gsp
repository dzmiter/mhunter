<html>
<head>
  <title>View stats</title>
  <meta name="layout" content="main"/>
  <r:require module="bootstrap"/>
</head>
<body>
    <table class="table table-hover">
        <thead>
        <th>Date</th>
        <th>User</th>
        <th>Report</th>
        </thead>

        <tbody>
            <g:each in="${reports}" var="item">
                <tr>
                    <td><g:formatDate format="HH:mm:ss dd/MM/yyyy" date="${item.date}"/></td>
                    <td>${item.user}</td>
                    <td>${item.report}</td>
                </tr>
            </g:each>
        </tbody>
    </table>
</body>
</html>