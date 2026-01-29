package com.colors.ecommerce.backend.application;

import com.colors.ecommerce.backend.infrastucture.adapter.ICategoryCrudRepository;
import com.colors.ecommerce.backend.infrastucture.adapter.IProductCrudRepository;
import com.colors.ecommerce.backend.infrastucture.adapter.IProductVariantCrudRepository;
import com.colors.ecommerce.backend.infrastucture.entity.CategoryEntity;
import com.colors.ecommerce.backend.infrastucture.entity.ProductEntity;
import com.colors.ecommerce.backend.infrastucture.entity.ProductVariantEntity;
import com.colors.ecommerce.backend.infrastucture.rest.dto.ExcelImportResult;
import com.colors.ecommerce.backend.infrastucture.rest.dto.ExcelImportResult.ImportError;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Service
public class ExcelImportService {
    private final IProductCrudRepository productCrudRepository;
    private final IProductVariantCrudRepository productVariantCrudRepository;
    private final ICategoryCrudRepository categoryCrudRepository;

    public ExcelImportService(IProductCrudRepository productCrudRepository,
                              IProductVariantCrudRepository productVariantCrudRepository,
                              ICategoryCrudRepository categoryCrudRepository) {
        this.productCrudRepository = productCrudRepository;
        this.productVariantCrudRepository = productVariantCrudRepository;
        this.categoryCrudRepository = categoryCrudRepository;
    }

    public ExcelImportResult importFile(MultipartFile file) {
        ExcelImportResult result = new ExcelImportResult();
        if (file == null || file.isEmpty()) {
            result.getErrors().add(new ImportError(0, "Archivo vacío"));
            return result;
        }

        try (InputStream inputStream = file.getInputStream();
             XSSFWorkbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            int rowIndex = 0;
            for (Row row : sheet) {
                rowIndex++;
                if (rowIndex == 1) {
                    continue;
                }
                if (isRowEmpty(row)) {
                    continue;
                }
                try {
                    String productName = getString(row, 0);
                    String categoryName = getString(row, 1);
                    String description = getString(row, 2);
                    BigDecimal basePrice = getBigDecimal(row, 3);
                    String imagesRaw = getString(row, 4);
                    String sku = getString(row, 5);
                    String size = getString(row, 6);
                    String color = getString(row, 7);
                    BigDecimal gsm = getBigDecimal(row, 8);
                    String material = getString(row, 9);
                    String usage = getString(row, 10);
                    Integer stock = getInteger(row, 11);
                    Boolean active = getBoolean(row, 12);

                    if (sku == null || sku.isBlank()) {
                        result.getErrors().add(new ImportError(rowIndex, "SKU obligatorio"));
                        continue;
                    }
                    if (size == null || size.isBlank()) {
                        result.getErrors().add(new ImportError(rowIndex, "Talle obligatorio"));
                        continue;
                    }
                    if (stock != null && stock < 0) {
                        result.getErrors().add(new ImportError(rowIndex, "Stock negativo"));
                        continue;
                    }

                    ProductVariantEntity existingVariant = productVariantCrudRepository.findBySku(sku);
                    if (existingVariant != null) {
                        existingVariant.setSize(size);
                        existingVariant.setColor(color);
                        existingVariant.setGsm(gsm);
                        existingVariant.setMaterial(material);
                        existingVariant.setUsage(usage);
                        existingVariant.setStockCurrent(stock == null ? 0 : stock);
                        existingVariant.setActive(active == null || active);
                        productVariantCrudRepository.save(existingVariant);
                        result.setUpdated(result.getUpdated() + 1);
                        continue;
                    }

                    ProductEntity product = findOrCreateProduct(productName, description, basePrice, imagesRaw, categoryName);

                    ProductVariantEntity variant = new ProductVariantEntity();
                    variant.setProductEntity(product);
                    variant.setSku(sku);
                    variant.setSize(size);
                    variant.setColor(color);
                    variant.setGsm(gsm);
                    variant.setMaterial(material);
                    variant.setUsage(usage);
                    variant.setStockCurrent(stock == null ? 0 : stock);
                    variant.setStockMinimum(0);
                    variant.setActive(active == null || active);
                    productVariantCrudRepository.save(variant);
                    result.setCreated(result.getCreated() + 1);
                } catch (Exception ex) {
                    result.getErrors().add(new ImportError(rowIndex, ex.getMessage()));
                }
            }
        } catch (Exception e) {
            result.getErrors().add(new ImportError(0, "Error leyendo Excel: " + e.getMessage()));
        }
        return result;
    }

    private ProductEntity findOrCreateProduct(String name, String description, BigDecimal basePrice, String imagesRaw, String categoryName) {
        String brand = "LION'S BRAND";
        ProductEntity product = productCrudRepository.findByNameIgnoreCaseAndBrand(name, brand)
                .orElseGet(() -> productCrudRepository.findByNameIgnoreCase(name).orElse(null));

        if (product == null) {
            product = new ProductEntity();
            product.setName(name);
            product.setBrand(brand);
        }

        if (description != null && !description.isBlank()) {
            product.setDescription(description);
        }
        if (basePrice != null) {
            product.setPrice(basePrice);
        }
        if (imagesRaw != null && !imagesRaw.isBlank()) {
            List<String> images = parseImages(imagesRaw);
            product.setImages(images);
            if (!images.isEmpty()) {
                product.setUrlImage(images.get(0));
            }
        }
        product.setActive(true);

        if (categoryName != null && !categoryName.isBlank()) {
            CategoryEntity category = categoryCrudRepository.findByNameIgnoreCase(categoryName)
                    .orElseGet(() -> {
                        CategoryEntity newCategory = new CategoryEntity();
                        newCategory.setName(categoryName);
                        return categoryCrudRepository.save(newCategory);
                    });
            product.setCategoryEntity(category);
        }

        return productCrudRepository.save(product);
    }

    private List<String> parseImages(String raw) {
        String[] parts = raw.split(",");
        List<String> images = new ArrayList<>();
        for (String part : parts) {
            String trimmed = part.trim();
            if (!trimmed.isBlank()) {
                images.add(trimmed);
            }
        }
        return images;
    }

    private boolean isRowEmpty(Row row) {
        for (int i = 0; i <= 12; i++) {
            Cell cell = row.getCell(i);
            if (cell != null && cell.getCellType() != org.apache.poi.ss.usermodel.CellType.BLANK) {
                return false;
            }
        }
        return true;
    }

    private String getString(Row row, int index) {
        Cell cell = row.getCell(index);
        if (cell == null) {
            return null;
        }
        cell.setCellType(org.apache.poi.ss.usermodel.CellType.STRING);
        return cell.getStringCellValue().trim();
    }

    private BigDecimal getBigDecimal(Row row, int index) {
        Cell cell = row.getCell(index);
        if (cell == null) {
            return null;
        }
        if (cell.getCellType() == org.apache.poi.ss.usermodel.CellType.NUMERIC) {
            return BigDecimal.valueOf(cell.getNumericCellValue());
        }
        String value = cell.toString().trim();
        if (value.isBlank()) {
            return null;
        }
        return new BigDecimal(value.replace(",", "."));
    }

    private Integer getInteger(Row row, int index) {
        Cell cell = row.getCell(index);
        if (cell == null) {
            return null;
        }
        if (cell.getCellType() == org.apache.poi.ss.usermodel.CellType.NUMERIC) {
            return (int) cell.getNumericCellValue();
        }
        String value = cell.toString().trim();
        if (value.isBlank()) {
            return null;
        }
        return Integer.parseInt(value);
    }

    private Boolean getBoolean(Row row, int index) {
        Cell cell = row.getCell(index);
        if (cell == null) {
            return null;
        }
        String value = cell.toString().trim().toLowerCase(Locale.ROOT);
        if (value.isBlank()) {
            return null;
        }
        return Arrays.asList("true", "1", "si", "sí", "yes").contains(value);
    }
}
