package uk.co.metadataconsulting.monitor.modelcatalogue

/**
 * Projection of MDXSearchResponse for use in mapping
 */
class MDXSearchResponseProjection {
    List<ElasticSearchCatalogueElementDocumentProjection> list

    static MDXSearchResponseProjection of(MDXSearchResponse mdxSearchResponse) {
        return new MDXSearchResponseProjection(list: mdxSearchResponse.list.collect{ElasticSearchCatalogueElementDocumentProjection.of(it)})
    }

}

class ElasticSearchCatalogueElementDocumentProjection {
    String name
    String gormUrl

    String description

    String combinedGormUrlName

    static ElasticSearchCatalogueElementDocumentProjection of(ElasticSearchCatalogueElementDocument elasticSearchCatalogueElementDocument) {
        String gormUrl = "gorm://${elasticSearchCatalogueElementDocument.elementType}:${elasticSearchCatalogueElementDocument.id}"
        String name = elasticSearchCatalogueElementDocument.name
        return new ElasticSearchCatalogueElementDocumentProjection(
                name: name,
                gormUrl: gormUrl,
                combinedGormUrlName: gormUrl + GormUrlName.gormUrlNameSeparator + name,

                description: elasticSearchCatalogueElementDocument.description)

    }

}