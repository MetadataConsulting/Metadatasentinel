package uk.co.metadataconsulting.monitor

import grails.testing.services.ServiceUnitTest
import org.modelcatalogue.core.scripting.ValidatingImpl
import spock.lang.Specification
import spock.lang.Unroll
import uk.co.metadataconsulting.monitor.modelcatalogue.ValidationRules

class ImportServiceSpec extends Specification implements ServiceUnitTest<ImportService> {

    def "validating rule is processed"() {
        given:
        String gormUrl = 'gorm://org.modelcatalogue.core.EnumeratedType:250'
        String value = 'yellow'
        String header = 'Color'
        List<String> values = ["RW6A06647291","yellow"]
        ValidationRules validationRules = new ValidationRules(validating: new ValidatingImpl(explicitRule: "x == null || x in ['red', 'blue']"))
        MappingMetadata metadata = new MappingMetadata()
        metadata.with {
            headersList = ['RadiologicalAccessionNumber', 'Color']
            gormUrls = ['gorm://org.modelcatalogue.core.DataElement:77', 'gorm://org.modelcatalogue.core.EnumeratedType:250']
            gormUrlsRules = [('gorm://org.modelcatalogue.core.EnumeratedType:250'): validationRules]
        }
        service.validatorService = Stub(ValidatorService) {
            validate(_, _, _) >> new ValidationResult(name: 'must be red and blue',
                    reason: 'failure',
                    numberOfRulesValidatedAgainst: 1,
                    status: ValidationStatus.INVALID)
        }

        when:
        RecordPortion portion = service.recordPortionFromValue(gormUrl, value, header, values, metadata)

        then:
        portion.numberOfRulesValidatedAgainst == 1
        portion.reason != null
        portion.status == ValidationStatus.INVALID
    }

    @Unroll
    def "headerAtIndex with values ( #headerLineList  #index ) does not throw exception"(List<String> headerLineList, int index) {
        when:
        MappingMetadata metadata = new MappingMetadata(headersList: headerLineList)
        service.headerAtIndex(metadata, index)

        then:
        noExceptionThrown()

        where:
        headerLineList | index
        null           | 0
    }
}
