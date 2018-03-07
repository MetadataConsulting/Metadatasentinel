package uk.co.metadataconsulting.sentinel

import groovy.transform.CompileStatic;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@CompileStatic
class ExcelReader {

    private static final Logger log = LoggerFactory.getLogger(DlrValidator.class)

    static void read(InputStream inputStream, int sheetNumber = 0, boolean skipFirstRow = false, Closure headerListClosure = null, Closure cls = null) throws IOException, InvalidFormatException {
        Workbook workbook = WorkbookFactory.create(inputStream)
        Sheet sheet = workbook.getSheetAt(sheetNumber)
        DataFormatter dataFormatter = new DataFormatter()
        Iterator<Row> rowIterator = sheet.rowIterator()
        int rowNumber = 0
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next()
            List<String> cellValues = cellValuesForRow(dataFormatter, row)
            if ( rowNumber == 0 && headerListClosure) {
                headerListClosure(cellValues)
            }

            boolean processRow = !(rowNumber == 0 && skipFirstRow)
            boolean everyValueInLineIsEmpty = cellValues.every { String str -> !str }
            if ( everyValueInLineIsEmpty ) {
                processRow = false
            }
            if ( processRow && cls ) {
                cls(cellValues)
            }



            rowNumber++
        }

        workbook.close()
    }

    static List<String> cellValuesForRow(DataFormatter dataFormatter, Row row) {
        List<String> cellValues = []
        Iterator<Cell> cellIterator = row.cellIterator()
        while (cellIterator.hasNext()) {
            Cell cell = cellIterator.next()
            String cellValue = dataFormatter.formatCellValue(cell)
            cellValues << cellValue
        }
        cellValues
    }
}