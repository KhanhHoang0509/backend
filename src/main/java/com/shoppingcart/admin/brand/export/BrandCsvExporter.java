package com.shoppingcart.admin.brand.export;

import com.shoppingcart.admin.AbstractExporter;
import com.shoppingcart.admin.entity.Brand;
import com.shoppingcart.admin.entity.Category;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class BrandCsvExporter extends AbstractExporter {
	public void export(List<Brand> listBrands, HttpServletResponse response) throws IOException {
		super.setResponseHeader(response, "text/csv", ".csv", "brands_");
		ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);

		String[] csvHeader = {"ID", "Logos", "Name"};
		String[] filedMapping = {"id", "logos", "name"};
		csvWriter.writeHeader(csvHeader);

		for (Brand brand : listBrands) {
			csvWriter.write(brand, filedMapping);
		}
		csvWriter.close();
	}
}