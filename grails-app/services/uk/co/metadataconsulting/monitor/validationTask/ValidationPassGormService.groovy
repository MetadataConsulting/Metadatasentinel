package uk.co.metadataconsulting.monitor.validationTask

import grails.gorm.DetachedCriteria
import grails.gorm.transactions.ReadOnly
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import uk.co.metadataconsulting.monitor.RecordCollectionGormEntity

@Slf4j
@CompileStatic
class ValidationPassGormService {

    List<ValidationPass> findAllValidationPasses(Map args, ValidationTask validationTaskParam) {
        ValidationPass.where { validationTask == validationTaskParam }.list(args)
    }

    @ReadOnly
    Number count() {
        ValidationPass.count()
    }

    @ReadOnly
    List<ValidationPass> findAllByRecordCollection(RecordCollectionGormEntity recordCollection) {
        DetachedCriteria<ValidationPass> query = queryByRecordCollection(recordCollection)
        query.list()
    }

    private DetachedCriteria<ValidationPass> queryByRecordCollection(RecordCollectionGormEntity recordCollectionGormEntity) {
        ValidationPass.where {
            recordCollection == recordCollectionGormEntity
        }
    }
}