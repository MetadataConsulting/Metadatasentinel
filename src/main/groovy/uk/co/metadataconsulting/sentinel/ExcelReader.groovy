package uk.co.metadataconsulting.sentinel

import groovy.transform.CompileStatic;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
 import org.apache.poi.ss.usermodel.*
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@CompileStatic
class ExcelReader {

    private static final Logger log = LoggerFactory.getLogger(DlrValidator.class)

    static void read(InputStream inputStream, int sheetNumber = 0, boolean skipFirstRow = false, Closure headerListClosure = null, Closure cls = null) throws IOException, InvalidFormatException {

        Workbook workbook = WorkbookFactory.create(inputStream)
        Sheet sheet = workbook.getSheetAt(sheetNumber)
        DataFormatter dataFormatter = new DataFormatter()
        Iterator<Row> rowIterator = sheet.rowIterator()Meta
        int max = 1
        int rowNumber = 0
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next()
            List<String> cellValues = cellValuesForRow(dataFormatter, row, max)
            if ( rowNumber == 0 && headerListClosure) {
                headerListClosure(cellValues)
            }
            if(rowNumber == 0){
                max = cellValues.size()
            }

            boolean processRow = !(rowNumber == 0 && skipFirstRow)
            try{
                boolean everyValueInLineIsEmpty = cellValues.every { String str -> !str }
                if ( everyValueInLineIsEmpty ) {
                    processRow = false
                }
                if ( processRow && cls ) {
                    cls(cellValues)
                }
            }catch(Exception ex){
                println ex.getMessage()
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
                }
            }
        }catch(Exception ex){
            println ex.getMessage()
        }

        data
    }

    static String getValue(Row row, Cell cell, List data) {
        def rowIndex = row.getRowNum()
        def colIndex = cell.getColumnIndex()
        def value = ""
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                value = cell.getRichStringCellValue().getString();
                break;
            case Cell.CELL_TYPE_NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    value = cell.getDateCellValue();
                } else {
                    value = cell.getNumericCellValue();
                }
                break;
            case Cell.CELL_TYPE_BOOLEAN:
                value = cell.getBooleanCellValue();
                break;
            case Cell.CELL_TYPE_FORMULA:
                value = cell.getCellFormula();
                break;
            default:
                value = ""
        }
        data[colIndex] = value
        data
    }

//    static List<String> cellValuesForRow(DataFormatter dataFormatter, Row row) {
//        List<String> cellValues = []
//        Iterator<Cell> cellIterator = row.cellIterator()
//        while (cellIterator.hasNext()) {
//            Cell cell = cellIterator.next()
//            String cellValue = dataFormatter.formatCellValue(cell)
//            cellValues << cellValue
//        }
//        cellValues
//    }
}