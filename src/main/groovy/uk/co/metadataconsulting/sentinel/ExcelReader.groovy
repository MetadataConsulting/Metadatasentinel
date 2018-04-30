package uk.co.metadataconsulting.sentinel

import groovy.transform.CompileStatic;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
 import org.apache.poi.ss.usermodel.*
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.text.SimpleDateFormat

@CompileStatic
class ExcelReader {

    private static final Logger log = LoggerFactory.getLogger(DlrValidator.class)

    static void read(InputStream inputStream, int sheetNumber = 0, boolean skipFirstRow = false, Closure headerListClosure = null, Closure cls = null) throws IOException, InvalidFormatException {

        Workbook workbook = WorkbookFactory.create(inputStream)
        Sheet sheet = workbook.getSheetAt(sheetNumber)
        DataFormatter dataFormatter = new DataFormatter()
        Iterator<Row> rowIterator = sheet.rowIterator()
        int max = 1
        int rowNumber = 0
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next()
            List<String> cellValues
            if ( rowNumber == 0 && headerListClosure) {
                cellValues = cellValuesForHeader(dataFormatter, row)
                max = cellValues.size()
                headerListClosure(cellValues)
            }else{
                cellValues = cellValuesForRow(dataFormatter, row, max)
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

    static List<String> cellValuesForRow(DataFormatter dataFormatter, Row row, int max) {


        List<String> data = []
        try{
            int lastColumn = (max == 1) ? row.getLastCellNum():max

            for (int cn = 0; cn < lastColumn; cn++) {
                Cell cell = row.getCell(cn, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK  )
                String cellValue = dataFormatter.formatCellValue(cell)
                data << cellValue
                if (cell != null) {
                    getValue(row, cell, data)
                } else {
                    //log
                    log.warn("null value in row")
                }
            }
        }catch(Exception ex){
            log.error(ex.getMessage())
        }

        data
    }

    static List<String> cellValuesForHeader(DataFormatter dataFormatter, Row row) {
        List<String> cellValues = []
        Iterator<Cell> cellIterator = row.cellIterator()
        while (cellIterator.hasNext()) {
            Cell cell = cellIterator.next()
            String cellValue = dataFormatter.formatCellValue(cell)
            if(!cellValue.isEmpty() && cellValue!="") {
                cellValues << cellValue;
            }else{
                break;
            }
        }
        cellValues
    }

    static String getValue(Row row, Cell cell, List data) {
        def rowIndex = row.getRowNum()
        def colIndex = cell.getColumnIndex()
        def value = ""
        DataFormatter df = new DataFormatter();
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                value = cell.getRichStringCellValue().getString();
                break;
            case Cell.CELL_TYPE_NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    value = df.formatCellValue(cell);
                } else {
                    value = cell.getNumericCellValue();
                    if((value.mod(1)) == 0){
                        value = value.toInteger()
                    }
                }
                break;
            case Cell.CELL_TYPE_BOOLEAN:
                value = cell.getBooleanCellValue();
                break;
            case Cell.CELL_TYPE_FORMULA:
                switch(cell.getCachedFormulaResultType()) {
                    case Cell.CELL_TYPE_NUMERIC:
                       value = cell.getNumericCellValue()
                        break;
                    case Cell.CELL_TYPE_STRING:
                        value = cell.getRichStringCellValue()
                        break;
                }
                break;
            default:
                value = ""
        }
        data[colIndex] = value
        data
    }


}