package uk.co.metadataconsulting.sentinel

import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification
import spock.lang.Unroll

class MdxTagLibSpec extends Specification implements TagLibUnitTest<MdxTagLib> {

    def "mdx:link taglib generates correct anchor tag"() {
        given:
        final Long dataModelId = 3
        tagLib.metadataUrl = 'http://localhost:8080'
        RecordPortionMapping mapping = new RecordPortionMapping(header: 'NHS Number', gormUrl: 'gorm://org.modelcatalogue.core.DataElement:45')
        RecordPortionGormEntity recordPortion = new RecordPortionGormEntity(header: 'NHS Number')

        expect:
        "<a href='http://localhost:8080/#/3/dataElement/45'>NHS Number</a>" == tagLib.link(dataModelId: dataModelId, recordPortion: recordPortion, recordPortionMappingList: [mapping]).toString()
    }

    def "mdx:link taglib generates no link if gormUrl for mapping is null"() {
        given:
        tagLib.metadataUrl = 'http://localhost:8080'
        RecordPortionMapping mapping = new RecordPortionMapping(header: 'NHS Number', gormUrl: null)
        RecordPortionGormEntity recordPortion = new RecordPortionGormEntity(header: 'NHS Number')

        when:
        String result = tagLib.link(recordPortion: recordPortion, recordPortionMappingList: [mapping]).toString()

        then:
        "<span data-toggle='tooltip' title='NHS Number'>NHS Number</span>" == result
    }

    @Unroll
    def "for #gormUrl link is #link"(String gormUrl, String metadataUrl, Long dataModelId, String link) {
        given:
        tagLib.metadataUrl = metadataUrl

        expect:
        link == tagLib.linkFromGormUrl(dataModelId, gormUrl)

        where:
        gormUrl                                         | metadataUrl               | dataModelId | link
        'gorm://org.modelcatalogue.core.DataElement:45' | 'http://localhost:8080'   | 3           | 'http://localhost:8080/#/3/dataElement/45'
    }
}
