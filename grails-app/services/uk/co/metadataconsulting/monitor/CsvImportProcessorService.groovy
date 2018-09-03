package uk.co.metadataconsulting.monitor

import grails.config.Config
import grails.core.support.GrailsConfigurationAware
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import uk.co.metadataconsulting.monitor.stringtransformer.AllCapsToLowerCamelCaseTransformer
import uk.co.metadataconsulting.monitor.stringtransformer.CamelCaseToLowerCameCaseTransformer
import uk.co.metadataconsulting.monitor.stringtransformer.DuplicatedDoubleQuotesToOneDoubleQuoteTransformer
import uk.co.metadataconsulting.monitor.stringtransformer.LeadingTrailingDoubleQuotesRemovalTransformer
import uk.co.metadataconsulting.monitor.stringtransformer.NotLettersOrDigitsRemovalTransformer
import uk.co.metadataconsulting.monitor.stringtransformer.ReservedWordsTransformer
import uk.co.metadataconsulting.monitor.stringtransformer.SnakeCaseToLowerCamelCaseTransformer
import uk.co.metadataconsulting.monitor.stringtransformer.StringTransformer

@Slf4j
@CompileStatic
class CsvImportProcessorService implements GrailsConfigurationAware, CsvImportProcessor {

    boolean nullIfValueBlank
    List<StringTransformer> headerTransformers = [
            new LeadingTrailingDoubleQuotesRemovalTransformer(),
            new DuplicatedDoubleQuotesToOneDoubleQuoteTransformer(),
            new SnakeCaseToLowerCamelCaseTransformer(),
            new AllCapsToLowerCamelCaseTransformer(),
            new CamelCaseToLowerCameCaseTransformer(),
            new ReservedWordsTransformer(),
            new NotLettersOrDigitsRemovalTransformer(),
    ] as List<StringTransformer>
    List<StringTransformer> valueTransformers = [
            new LeadingTrailingDoubleQuotesRemovalTransformer(),
            new DuplicatedDoubleQuotesToOneDoubleQuoteTransformer(),
    ] as List<StringTransformer>


    @Override
    void setConfiguration(Config co) {
        this.nullIfValueBlank = co.getProperty('loinic.csv.nullIfValueBlank', Boolean, true)
    }

    @Override
    int processInputStream(InputStream inputStream, Integer batchSize,   Closure headerListClosure, Closure cls) {
        int processed = 0
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))
        List<String> lines = []
        String line
        for ( int ln = 0; (line = br.readLine()) != null; ln++ ) {
            if ( ln == 0 ) {
                List<String> headerPropertyList = headerLineToPropertiesList(line)
                if ( headerListClosure != null ) {
                    headerListClosure(headerPropertyList)
                }
            } else {
                if ( line?.trim() ) {
                    lines << line
                }
                if ( lines.size() == batchSize ) {
                    log.debug('processing batch at line #{}', ln)
                    List<List<String>> valuesList = valuesListWithLines(lines)
                    cls(valuesList)
                    processed += lines.size()
                    lines.clear()
                }
            }
        }
        if ( !lines.isEmpty() ) {
            log.debug('processing last batch')
            List<List<String>> valuesList = valuesListWithLines(lines)
            cls(valuesList)
            processed += lines.size()
        }
        processed
    }

    List<List<String>> valuesListWithLines(List<String> lines) {
        lines.collect { String line ->
            lineValues(line)
        }
    }

    List<String> headerLineToPropertiesList(String headerLine) {
        processLineWithTransformers(headerLine, headerTransformers)
    }

    List<String> processLineWithTransformers(String line, List<StringTransformer> transformerList) {
        List<String> headers = line.split(/,(?=([^\"]*\"[^\"]*\")*[^\"]*$)/) as List<String>
        for ( StringTransformer transformer : transformerList ) {
            headers = headers.collect { transformer.transform(it) }
        }
        headers
    }

    Object valueAtPosition(List<String> values, int i) {
        if ( values.size() > i ) {
            Object value = values[i]
            if ( nullIfValueBlank && value == '') {
                return null
            }
            return value
        }
        null
    }

    List<String> lineValues(String line) {
        processLineWithTransformers(line, valueTransformers)
    }
}