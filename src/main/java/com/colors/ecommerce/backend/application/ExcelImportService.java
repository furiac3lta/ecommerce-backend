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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class ExcelImportService {
    private final IProductCrudRepository productCrudRepository;
    private final IProductVariantCrudRepository productVariantCrudRepository;
    private final ICategoryCrudRepository categoryCrudRepository;
    private final ProductCodeService productCodeService;

    public ExcelImportService(IProductCrudRepository productCrudRepository,
                              IProductVariantCrudRepository productVariantCrudRepository,
                              ICategoryCrudRepository categoryCrudRepository,
                              ProductCodeService productCodeService) {
        this.productCrudRepository = productCrudRepository;
        this.productVariantCrudRepository = productVariantCrudRepository;
        this.categoryCrudRepository = categoryCrudRepository;
        this.productCodeService = productCodeService;
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
            Map<String, Integer> columnMap = buildColumnMap(sheet.getRow(0));
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
                    String productName = getString(row, resolveIndex(columnMap, "productname", 0));
                    String productCode = getString(row, resolveIndex(columnMap, "code", -1));
                    String categoryName = getString(row, resolveIndex(columnMap, "category", 1));
                    String description = getString(row, resolveIndex(columnMap, "description", 2));
                    BigDecimal basePrice = getBigDecimal(row, resolveIndex(columnMap, "baseprice", 3));
                    String imagesRaw = getString(row, resolveIndex(columnMap, "images", 4));
                    String sku = getString(row, resolveIndex(columnMap, "sku", 5));
                    String size = getString(row, resolveIndex(columnMap, "size", 6));
                    String color = getString(row, resolveIndex(columnMap, "color", 7));
                    BigDecimal gsm = getBigDecimal(row, resolveIndex(columnMap, "gsm", 8));
                    String material = getString(row, resolveIndex(columnMap, "material", 9));
                    String usage = getString(row, resolveIndex(columnMap, "use", 10));
                    Integer stock = getInteger(row, resolveIndex(columnMap, "stock", 11));
                    Boolean active = getBoolean(row, resolveIndex(columnMap, "active", 12));

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

                    ProductEntity product = findOrCreateProduct(productName, productCode, description, basePrice, imagesRaw, categoryName);
                    ProductVariantEntity existingVariant = productVariantCrudRepository.findBySku(sku);
                    if (existingVariant != null) {
                        existingVariant.setProductEntity(product);
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

    private ProductEntity findOrCreateProduct(String name, String code, String description, BigDecimal basePrice, String imagesRaw, String categoryName) {
        String brand = "LION'S BRAND";
        ProductEntity product = productCrudRepository.findByNameIgnoreCaseAndBrand(name, brand)
                .orElseGet(() -> productCrudRepository.findByNameIgnoreCase(name).orElse(null));

        if (product == null) {
            product = new ProductEntity();
            product.setName(name);
            product.setBrand(brand);
        }

        String normalizedCode = productCodeService.normalize(code);
        if (normalizedCode != null) {
            product.setCode(normalizedCode);
        } else {
            String existingCode = productCodeService.normalize(product.getCode());
            if (existingCode != null) {
                product.setCode(existingCode);
            } else {
                product.setCode(productCodeService.nextCode());
            }
        }

        if (product.getPriceOverride() == null) {
            product.setPriceOverride(false);
        }
        product.setSellOnline(true);

        if (description != null && !description.isBlank()) {
            product.setDescription(description);
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
            if (category.getPrice() == null && basePrice != null) {
                category.setPrice(basePrice);
                category = categoryCrudRepository.save(category);
            }
            product.setCategoryEntity(category);
            if (category.getPrice() != null) {
                product.setPrice(category.getPrice());
            }
        } else if (basePrice != null) {
            product.setPrice(basePrice);
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
        for (int i = 0; i <= 13; i++) {
            Cell cell = row.getCell(i);
            if (cell != null && cell.getCellType() != org.apache.poi.ss.usermodel.CellType.BLANK) {
                return false;
            }
        }
        return true;
    }

    private Map<String, Integer> buildColumnMap(Row headerRow) {
        Map<String, Integer> columnMap = new HashMap<>();
        if (headerRow == null) {
            return columnMap;
        }
        for (Cell cell : headerRow) {
            String value = getString(headerRow, cell.getColumnIndex());
            if (value == null || value.isBlank()) {
                continue;
            }
            columnMap.put(value.trim().toLowerCase(Locale.ROOT), cell.getColumnIndex());
        }
        return columnMap;
    }

    private int resolveIndex(Map<String, Integer> columnMap, String key, int fallback) {
        return columnMap.getOrDefault(key, fallback);
    }

    private String getString(Row row, int index) {
        if (index < 0) {
            return null;
        }
        Cell cell = row.getCell(index);
        if (cell == null) {
            return null;
        }
        cell.setCellType(org.apache.poi.ss.usermodel.CellType.STRING);
        return cell.getStringCellValue().trim();
    }

    private BigDecimal getBigDecimal(Row row, int index) {
        if (index < 0) {
            return null;
        }
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
        if (index < 0) {
            return null;
        }
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
        if (index < 0) {
            return null;
        }
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
