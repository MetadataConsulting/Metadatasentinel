package uk.co.metadataconsulting.sentinel

import groovy.transform.CompileStatic
import uk.co.metadataconsulting.sentinel.modelcatalogue.MetadataDomainEntity
import uk.co.metadataconsulting.sentinel.modelcatalogue.ValidationRule

@CompileStatic
class RuleFetcherService {
    List<ValidationRule> fetchValidationRules(String catalogueElementStringRepresentation) {
        if ( ['PersonBirthDate','DiagnosticTestReqDate','DiagnosticTestReqRecDate','DiagnosticTestDate'].contains(catalogueElementStringRepresentation) ) {
            String rule = '''
package uk.co.metadataconsulting.sentinel;

global uk.co.metadataconsulting.sentinel.RecordValidation record;

rule "date as yyyy-MM-dd"
   when
      eval(java.util.regex.Pattern.compile("([12]\\\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\\\d|3[01]))").matcher(record.getInput()[0]).matches())
   then
      record.setOutput(true);
end    
'''
            return [new ValidationRule(rule: rule, elements: [new MetadataDomainEntity(id: 1, name: catalogueElementStringRepresentation, domain: catalogueElementStringRepresentation)])]
        }
        []
    }
}
