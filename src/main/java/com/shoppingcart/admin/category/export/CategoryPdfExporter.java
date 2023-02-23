package com.shoppingcart.admin.category.export;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.shoppingcart.admin.AbstractExporter;
import com.shoppingcart.admin.entity.Category;


import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class CategoryPdfExporter extends AbstractExporter {
	public void export(List<Category> listCategories, HttpServletResponse response) throws IOException {
		super.setResponseHeader(response, "application/pdf", ".pdf", "categories");

		Document document = new Document(PageSize.A4);
		PdfWriter.getInstance(document, response.getOutputStream());

		document.open();

		Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
		font.setSize(18);
		font.setColor(java.awt.Color.BLUE);

		Paragraph paragraph = new Paragraph("List of Category", font);
		paragraph.setAlignment(Paragraph.ALIGN_CENTER);

		document.add(paragraph);

		PdfPTable table = new PdfPTable(5);
		table.setWidthPercentage(100f);
		table.setSpacingBefore(10);
		table.setWidths(new float[] {1.2f, 3.5f, 3.0f, 3.0f, 1.7f});

		writeTableHeader(table);
		writeTableData(table, listCategories);

		document.add(table);
		document.close();
	}

	private void writeTableData(PdfPTable table, List<Category> listCategories) {
		for (Category category : listCategories) {
			table.addCell(String.valueOf(category.getId()));
			table.addCell(category.getImage());
			table.addCell(category.getName());
			table.addCell(category.getAlias());
			table.addCell(String.valueOf(category.isEnabled()));
		}
	}

	private void writeTableHeader(PdfPTable table) {
		PdfPCell cell = new PdfPCell();
		cell.setBackgroundColor(java.awt.Color.BLUE);
		cell.setPadding(5);

		Font font = FontFactory.getFont(FontFactory.HELVETICA);
		font.setColor(java.awt.Color.WHITE);

		cell.setPhrase(new Phrase("ID", font));
		table.addCell(cell);
		cell.setPhrase(new Phrase("Image", font));
		table.addCell(cell);
		cell.setPhrase(new Phrase("Name", font));
		table.addCell(cell);
		cell.setPhrase(new Phrase("Alias", font));
		table.addCell(cell);
		cell.setPhrase(new Phrase("Enabled", font));
		table.addCell(cell);
	}
}
