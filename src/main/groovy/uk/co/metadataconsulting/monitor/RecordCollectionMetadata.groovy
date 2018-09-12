package uk.co.metadataconsulting.monitor

import groovy.transform.CompileStatic

@CompileStatic
interface RecordCollectionMetadata {
    String getDatasetName()
    String getAbout()
}
