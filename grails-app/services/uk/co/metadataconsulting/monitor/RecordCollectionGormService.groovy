package uk.co.metadataconsulting.monitor

import grails.gorm.DetachedCriteria
import grails.gorm.transactions.ReadOnly
import grails.gorm.transactions.Transactional
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.springframework.context.MessageSource
import org.springframework.transaction.annotation.Isolation
import uk.co.metadataconsulting.monitor.modelcatalogue.DataModel
import uk.co.metadataconsulting.monitor.modelcatalogue.GormUrlName

@Slf4j
@CompileStatic
class RecordCollectionGormService implements GormErrorsMessage {

    MessageSource messageSource

    @ReadOnly
    RecordCollectionGormEntity find(Serializable id) {
        RecordCollectionGormEntity.get(id)
    }

    @ReadOnly
    RecordCollectionGormEntity findByDatasetName(String datasetName) {
        queryByDatasetName(datasetName).get()
    }

    @ReadOnly
    List<RecordCollectionGormEntity> findAll(Map args) {
        RecordCollectionGormEntity.where {}.list(args)
    }

    @Transactional(isolation=Isolation.REPEATABLE_READ)
    void saveRecordCollectionMappingWithHeaders(RecordCollectionGormEntity recordCollection,
                                                List<String> headers,
                                                Map<String, List<GormUrlName>> suggestionsMap) {
        if ( headers ) {
            for ( String header : headers ) {
                RecordCollectionMappingEntryGormEntity recordCollectionMappingEntry = new RecordCollectionMappingEntryGormEntity(header: header)
                List<GormUrlName> suggestions = suggestionsMap[header]
                if (suggestions ) {
                    GormUrlName suggestion = suggestions.first()
                    recordCollectionMappingEntry.gormUrl = suggestion.gormUrl
                    recordCollectionMappingEntry.name = suggestion.name
                }
                recordCollection.addToMappings(recordCollectionMappingEntry)
            }
        }
        log.info("Mapping Entries added")
        if ( !recordCollection.save() ) {
            log.warn( 'error saving mappings {}', headers)
        }
        log.info("Record Collection Saved")
    }

    @Transactional(isolation=Isolation.REPEATABLE_READ)
    RecordCollectionGormEntity save(RecordCollectionMetadata recordCollectionMetadata) {
        RecordCollectionGormEntity recordCollection = new RecordCollectionGormEntity()
        recordCollection.with {
            datasetName = recordCollectionMetadata.datasetName
            about = recordCollectionMetadata.about
        }
        save(recordCollection)
    }

    @Transactional
    RecordCollectionGormEntity save(RecordCollectionGormEntity entity) {
        if ( !entity.save(validate:false) ) {
            log.warn '{}', errorsMsg(entity, messageSource)
        }
        entity
    }

    @Transactional
    RecordCollectionGormEntity update(Serializable id, RecordCollectionMetadata recordCollectionMetadata) {
        RecordCollectionGormEntity entity = RecordCollectionGormEntity.get(id)
        if (!entity) {
            return null
        }
        entity.with {
            datasetName = recordCollectionMetadata.datasetName
            about = recordCollectionMetadata.about
        }
        save(entity)
    }

    @ReadOnly
    Number count() {
        RecordCollectionGormEntity.count()
    }

    @Transactional
    void delete(Serializable id) {
        RecordCollectionGormEntity.get(id).delete()
    }

    @Transactional
    void deleteByDatasetName(String datasetName) {
        queryByDatasetName(datasetName).get()?.delete()
    }

    DetachedCriteria<RecordCollectionGormEntity> queryByDatasetName(String name) {
        RecordCollectionGormEntity.where {
            datasetName == name
        }
    }

    @ReadOnly
    List<RecordCollectionGormEntity> findAllInIds(Collection<Long> ids) {
        if ( !ids ) {
            return []
        }
        queryAllInIds(ids).list()
    }

    DetachedCriteria<RecordCollectionGormEntity> queryAllInIds(Collection<Long> ids) {
        RecordCollectionGormEntity.where { id in ids }
    }

    @Transactional
    RecordCollectionGormEntity associateWithDataModel(Long recordCollectionId, DataModel dataModel) {
        RecordCollectionGormEntity entity = find(recordCollectionId)
        associateWithDataModel(entity, dataModel)
    }

    @Transactional
    RecordCollectionGormEntity associateWithDataModel(RecordCollectionGormEntity entity, DataModel dataModel) {
        if (entity) {
            entity.with {
                dataModelId = dataModel.id
                dataModelName = dataModel.name
            }
            return save(entity)
        }
        null
    }

    @Transactional
    RecordCollectionGormEntity addHeadersList(RecordCollectionGormEntity entity, List<String> headersList) {
        if (entity) {
            entity.headersList = headersList
            return save(entity)
        }
        null
    }

    @Transactional
    RecordCollectionGormEntity updateFileMetadata(Long recordCollectionId,
                                                  UploadFileResult uploadFileResult) {
        RecordCollectionGormEntity entity = find(recordCollectionId)
        if (!entity) {
            return entity
        }
        entity.with {
            fileUrl = uploadFileResult.fileUrl
            fileKey = uploadFileResult.path
        }
        save(entity)
    }
}