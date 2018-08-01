package uk.co.metadataconsulting.sentinel

import groovy.transform.CompileStatic

@CompileStatic
interface RecordCollectionMetadata {
    String getDatasetName()
    String getAbout()
}
