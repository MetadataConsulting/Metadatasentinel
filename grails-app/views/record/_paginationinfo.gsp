<%@ page import="uk.co.metadataconsulting.sentinel.RecordCorrectnessDropdown" %>
<p>
    <g:if test="${correctness == uk.co.metadataconsulting.sentinel.RecordCorrectnessDropdown.ALL}">
        <span><g:message code="record.all.total" args="${[recordTotal]}" default="Total number of records: {0}."/> </span>
        <span><g:message code="record.total" args="${[invalidRecordTotal]}" default="Number of invalid records: {0}."/> </span>
    </g:if>
    <g:else>
        <span><g:message code="record.total" args="${[recordTotal, correctness]}" default="Total number of records: {0} for applied filtered {1}."/> </span>
        <span><g:message code="record.all.total" args="${[invalidRecordTotal, allRecordTotal]}" default="Globally {0} of {1} are invalid."/> </span>
    </g:else>
</p>