package uk.co.metadataconsulting.sentinel.modelcatalogue

/**
 * Structure of JSON response to MDX call to ${METADATA_URL}/api/modelCatalogue/core/catalogueElement/search.
 * This is MDX's ListWithTotalAndType
 */
class MDXSearchResponse {
    String base
    String itemType
    Boolean success
    Long total
    Long offset
    Long page
    Long size
    List<ElasticSearchCatalogueElementDocument> list
    String previous
    String next
    String sort
    String order
}


/**
 * Structure of an ElasticSearch CatalogueElement Document.
 */
class ElasticSearchCatalogueElementDocument {
    String name
    String id
    String elementType
    String link
    String status
    Long versionNumber
    String latestVersionId
    String dataModel
    String classifiedName
    String modelCatalogueId
    String internalModelCatalogueId
    String description
    ExtJSON ext
    String dateCreated
    String versionCreated
    String lastUpdated
    Boolean minimal

}

class ExtJSON {
    String type
    List<Map<String, String>> values
}