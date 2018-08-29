package uk.co.metadataconsulting.sentinel.modelcatalogue

class MDXSearchResponseProjection {
    List<ElasticSearchCatalogueElementDocumentProjection> list

    static MDXSearchResponseProjection of(MDXSearchResponse mdxSearchResponse) {
        return new MDXSearchResponseProjection(list: mdxSearchResponse.list.collect{ElasticSearchCatalogueElementDocumentProjection.of(it)})
    }

}

class ElasticSearchCatalogueElementDocumentProjection {
    String name
    String gormUrl

    static ElasticSearchCatalogueElementDocumentProjection of(ElasticSearchCatalogueElementDocument elasticSearchCatalogueElementDocument) {
        return new ElasticSearchCatalogueElementDocumentProjection(
                name: elasticSearchCatalogueElementDocument.name,
                gormUrl: "gorm://${elasticSearchCatalogueElementDocument.elementType}:${elasticSearchCatalogueElementDocument.id}")

    }
}