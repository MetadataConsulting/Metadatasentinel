package uk.co.metadataconsulting.sentinel

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException
import org.apache.poi.ss.format.CellDateFormatter;
import org.apache.poi.ss.usermodel.*
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.text.SimpleDateFormat

@Slf4j
@CompileStatic
class ExcelReader {

    static void read(InputStream inputStream, int sheetNumber = 0, boolean skipFirstRow = false, Closure headerListClosure = null, Closure cls = null) throws IOException, InvalidFormatException {

        Workbook workbook = WorkbookFactory.create(inputStream)
        Sheet sheet = workbook.getSheetAt(sheetNumber)
        Iterator<Row> rowIterator = sheet.rowIterator()
        int max = 1
        int rowNumber = 0
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next()
            List<String> cellValues
            if ( rowNumber == 0 && headerListClosure) {
                cellValues = cellValuesForHeader(row)
                max = cellValues.size()
                headerListClosure(cellValues)
            } else {
                cellValues = cellValuesForRow(row, max)
            }

            boolean processRow = !(rowNumber == 0 && skipFirstRow)
            try{
                boolean everyValueInLineIsEmpty = cellValues.every { str -> !str }
                if ( everyValueInLineIsEmpty ) {
                    processRow = false
                }
                if ( processRow && cls ) {
                    cls(cellValues)
                }
            }catch(Exception ex){
                log.error(ex.getMessage())
            }
            rowNumber++
        }
        workbook.close()
    }

    static List<String> cellValuesForRow(Row row, int max) {


        List<String> data = []
        try{
            int lastColumn = (max == 1) ? row.getLastCellNum():max

            for (int cn = 0; cn < lastColumn; cn++) {
                Cell cell = row.getCell(cn, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                String cellValue = cellValue(cell)
                data << cellValue ?: ''
            }
        } catch(Exception ex){
            log.error(ex.getMessage())
        }

        data
    }

    static List<String> cellValuesForHeader(Row row) {
        List<String> cellValues = []
        Iterator<Cell> cellIterator = row.cellIterator()
        while (cellIterator.hasNext()) {
            Cell cell = cellIterator.next()
            String cellValue = cellValue(cell)
            if ( !cellValue.isEmpty() && cellValue!="" ) {
                cellValues << cellValue
            } else {
                break
            }
        }
        cellValues
    }

    static String cellValue(Cell cell) {
        def value = ""

        switch (cell.getCellTypeEnum()) {
            case CellType.STRING:
                value = cell.getRichStringCellValue().getString()
                break
            case CellType.NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    double val = cell.getNumericCellValue()
                    Date date = HSSFDateUtil.getJavaDate(val)

                    if ( cell.getCellStyle().getDataFormat() == (short) 14 ){
                        return new CellDateFormatter('dd/mm/yyyy').format(date)
                    }
                    else{
                        DataFormatter fmt = new DataFormatter()
                        return fmt.formatCellValue(cell)
                    }
                } else {
                    value = cell.getNumericCellValue()
                    if((value.mod(1)) == 0){
                        value = value.toInteger()
                    }
                }
                break
            case CellType.BOOLEAN:
                value = cell.getBooleanCellValue()
                break
            case CellType.FORMULA:
                switch(cell.getCachedFormulaResultTypeEnum()) {
                    case CellType.NUMERIC:
                        value = cell.getNumericCellValue()
                        break
                    case CellType.STRING:
                        value = cell.getRichStringCellValue()
                        break
                }
                break
            default:
                value = ""
        }
        value
    }
}