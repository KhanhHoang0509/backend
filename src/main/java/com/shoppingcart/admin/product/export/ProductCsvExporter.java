package com.shoppingcart.admin.product.export;

import com.shoppingcart.admin.AbstractExporter;
import com.shoppingcart.admin.entity.Products;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class ProductCsvExporter extends AbstractExporter {

	public void export(List<Products> listProducts, HttpServletResponse response) throws IOException {
		super.setResponseHeader(response, "text/csv", ".csv", "products_");
		ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);

		String[] csvHeader = {"ID", "Alias", "Name", "Price"};
		String[] filedMapping = {"id", "alias", "name", "price"};
		csvWriter.writeHeader(csvHeader);

		for (Products products : listProducts) {
			csvWriter.write(products, filedMapping);
		}
		csvWriter.close();
	}
}
