package uk.co.metadataconsulting.sentinel

import groovy.util.logging.Slf4j
import org.springframework.context.MessageSource

@Slf4j
trait ValidateableErrorsMessage {

    String errorsMsg(def bean, MessageSource messageSource, Locale locale = Locale.getDefault()) {
        StringBuilder message = new StringBuilder("${bean.class.simpleName}: $bean")
        for (fieldErrors in bean.errors) {
            for (error in fieldErrors.allErrors) {
                message.append("\n\t").append(messageSource.getMessage(error, locale))
            }
        }
        message.toString()
    }
}
