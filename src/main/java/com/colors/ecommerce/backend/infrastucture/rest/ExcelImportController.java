package com.colors.ecommerce.backend.infrastucture.rest;

import com.colors.ecommerce.backend.application.ExcelImportService;
import com.colors.ecommerce.backend.infrastucture.rest.dto.ExcelImportResult;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;

@RestController
@RequestMapping("/api/v1/admin/import")
@CrossOrigin(origins = {"http://localhost:4200", "https://www.lcosmeticadigital.com.ar", "https://ecommerce-angular-production.up.railway.app"})
public class ExcelImportController {
    private final ExcelImportService excelImportService;

    public ExcelImportController(ExcelImportService excelImportService) {
        this.excelImportService = excelImportService;
    }

    @PostMapping(value = "/excel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ExcelImportResult> importExcel(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(excelImportService.importFile(file));
    }

    @GetMapping("/template")
    public ResponseEntity<ByteArrayResource> downloadTemplate() throws Exception {
        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("variants");
        Row header = sheet.createRow(0);
        String[] columns = new String[]{
                "productName", "category", "description", "basePrice", "images",
                "sku", "size", "color", "gsm", "material", "use", "stock", "active"
        };
        for (int i = 0; i < columns.length; i++) {
            header.createCell(i).setCellValue(columns[i]);
        }
        Row sample = sheet.createRow(1);
        sample.createCell(0).setCellValue("Kimono Classic");
        sample.createCell(1).setCellValue("kimono");
        sample.createCell(2).setCellValue("Pearl weave 450 GSM");
        sample.createCell(3).setCellValue(120000);
        sample.createCell(4).setCellValue("https://...");
        sample.createCell(5).setCellValue("LB-CL-A2-BLK");
        sample.createCell(6).setCellValue("A2");
        sample.createCell(7).setCellValue("negro");
        sample.createCell(8).setCellValue(450);
        sample.createCell(9).setCellValue("Pearl Weave");
        sample.createCell(10).setCellValue("competencia");
        sample.createCell(11).setCellValue(10);
        sample.createCell(12).setCellValue(true);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        ByteArrayResource resource = new ByteArrayResource(out.toByteArray());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=template.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .contentLength(resource.contentLength())
                .body(resource);
    }
}
