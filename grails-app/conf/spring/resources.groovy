import uk.co.metadataconsulting.monitor.gorm.RecordCollectionGormListener
import uk.co.metadataconsulting.monitor.gorm.RecordGormListener
import uk.co.metadataconsulting.monitor.security.MdxAuthenticationProvider

beans = {
    mdxAuthenticationProvider(MdxAuthenticationProvider)
    recordCollectionGormListener(RecordCollectionGormListener) {
        springSecurityService = ref('springSecurityService')
    }
    recordGormListener(RecordGormListener){
        springSecurityService = ref('springSecurityService')
    }
}
