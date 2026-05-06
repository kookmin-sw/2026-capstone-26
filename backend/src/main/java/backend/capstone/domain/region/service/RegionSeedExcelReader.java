package backend.capstone.domain.region.service;

import backend.capstone.domain.region.service.dto.RegionSeedRow;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RegionSeedExcelReader {

    private static final String SEED_PATH = "seed/region_seed.xlsx";
    private static final int LEGAL_DONG_CODE_INDEX = 0;
    private static final int SIDO_NAME_INDEX = 1;
    private static final int SIGUNGU_NAME_INDEX = 2;
    private static final int EUP_MYEON_DONG_NAME_INDEX = 3;
    private static final int DONG_RI_NAME_INDEX = 4;
    private static final int CREATED_DATE_INDEX = 5;
    private static final int DELETED_DATE_INDEX = 6;

    public List<RegionSeedRow> readAll() {
        ClassPathResource resource = new ClassPathResource(SEED_PATH);

        try (InputStream inputStream = resource.getInputStream();
            Workbook workbook = WorkbookFactory.create(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            validateHeader(sheet.getRow(0));

            List<RegionSeedRow> rows = new ArrayList<>();
            DataFormatter formatter = new DataFormatter();

            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null || isBlankRow(row, formatter)) {
                    continue;
                }

                rows.add(new RegionSeedRow(
                    getCellValue(row, LEGAL_DONG_CODE_INDEX, formatter),
                    getCellValue(row, SIDO_NAME_INDEX, formatter),
                    getCellValue(row, SIGUNGU_NAME_INDEX, formatter),
                    getCellValue(row, EUP_MYEON_DONG_NAME_INDEX, formatter),
                    getCellValue(row, DONG_RI_NAME_INDEX, formatter),
                    getCellValue(row, CREATED_DATE_INDEX, formatter),
                    getCellValue(row, DELETED_DATE_INDEX, formatter)
                ));
            }

            return rows;
        } catch (IOException e) {
            throw new IllegalStateException("region seed 엑셀 파일을 읽을 수 없습니다.", e);
        }
    }

    private void validateHeader(Row headerRow) {
        if (headerRow == null) {
            throw new IllegalStateException("region seed 엑셀 헤더가 비어 있습니다.");
        }

        DataFormatter formatter = new DataFormatter();
        String legalDongCodeHeader = getCellValue(headerRow, LEGAL_DONG_CODE_INDEX, formatter);
        String sidoNameHeader = getCellValue(headerRow, SIDO_NAME_INDEX, formatter);
        String sigunguNameHeader = getCellValue(headerRow, SIGUNGU_NAME_INDEX, formatter);
        String eupMyeonDongHeader = getCellValue(headerRow, EUP_MYEON_DONG_NAME_INDEX, formatter);
        String dongRiHeader = getCellValue(headerRow, DONG_RI_NAME_INDEX, formatter);

        if (!"법정동코드".equals(legalDongCodeHeader)
            || !"시도명".equals(sidoNameHeader)
            || !"시군구명".equals(sigunguNameHeader)
            || !"읍면동명".equals(eupMyeonDongHeader)
            || !"동리명".equals(dongRiHeader)) {
            throw new IllegalStateException("region seed 엑셀 헤더 형식이 예상과 다릅니다.");
        }
    }

    private boolean isBlankRow(Row row, DataFormatter formatter) {
        for (int index = LEGAL_DONG_CODE_INDEX; index <= DELETED_DATE_INDEX; index++) {
            if (getCellValue(row, index, formatter) != null) {
                return false;
            }
        }
        return true;
    }

    private String getCellValue(Row row, int cellIndex, DataFormatter formatter) {
        Cell cell = row.getCell(cellIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell == null) {
            return null;
        }

        String value = formatter.formatCellValue(cell);
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
