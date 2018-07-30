import uk.co.metadataconsulting.sentinel.gorm.RecordCollectionGormListener
import uk.co.metadataconsulting.sentinel.gorm.RecordGormListener
import uk.co.metadataconsulting.sentinel.security.MdxAuthenticationProvider

beans = {
    mdxAuthenticationProvider(MdxAuthenticationProvider)
    recordCollectionGormListener(RecordCollectionGormListener) {
        springSecurityService = ref('springSecurityService')
    }
    recordGormListener(RecordGormListener){
        springSecurityService = ref('springSecurityService')
    }
}
