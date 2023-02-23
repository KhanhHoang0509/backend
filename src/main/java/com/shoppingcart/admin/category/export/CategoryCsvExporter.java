package com.shoppingcart.admin.category.export;

import com.shoppingcart.admin.AbstractExporter;
import com.shoppingcart.admin.entity.Category;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class CategoryCsvExporter extends AbstractExporter {
	public void export(List<Category> listUsers, HttpServletResponse response) throws IOException {
		super.setResponseHeader(response, "text/csv", ".csv", "categories_");
		ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);

		String[] csvHeader = {"ID", "Image", "Name", "Alias", "Enabled"};
		String[] filedMapping = {"id", "image", "name", "alias", "enabled"};
		csvWriter.writeHeader(csvHeader);

		for (Category category : listUsers) {
			csvWriter.write(category, filedMapping);
		}
		csvWriter.close();
	}
}

