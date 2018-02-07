<html>
<head>
    <title><g:message code="recordCollection.csv.import" default="Import Csv"/></title>
    <meta name="layout" content="main" />
    <style type="text/css">
    form fieldset ol li { list-style-type: none; }
    </style>
</head>
<body>
<div id="content" role="main">
    <g:render template="/templates/flashmessage"/>
    <g:render template="/templates/flasherror"/>
    <g:uploadForm action="uploadCsv" controller="recordCollection">
        <fieldset>
            <ol>
                <li>
                    <label for="csvFile"><g:message code="recordCollection.csv.import.file" default="CSV File"/></label>
                    <input type="file" name="csvFile" />
                </li>
                <li>
                    <label for="batchSize"><g:message code="recordCollection.csv.import.batchSize" default="Batch Size"/></label>
                    <select name="batchSize">
                        <option value="10">10</option>
                        <option value="100">100</option>
                        <option value="200">200</option>
                        <option value="500">500</option>
                        <option value="1000">1000</option>
                    </select>
                </li>
                <li>
                    <input type="submit" class="btn btn-primary" value="${message(code: 'recordCollection.csv.import.submit', default: 'Submit')}"/>
                </li>
            </ol>
        </fieldset>
    </g:uploadForm>
</div><!-- /#content -->
</body>
</html>