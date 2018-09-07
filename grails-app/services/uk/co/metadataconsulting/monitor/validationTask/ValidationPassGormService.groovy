package uk.co.metadataconsulting.monitor.validationTask

import grails.gorm.transactions.ReadOnly
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import uk.co.metadataconsulting.monitor.validationTask.ValidationPass
import uk.co.metadataconsulting.monitor.validationTask.ValidationTask



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
}