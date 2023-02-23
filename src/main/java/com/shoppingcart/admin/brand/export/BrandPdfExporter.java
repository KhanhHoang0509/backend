package com.shoppingcart.admin.brand.export;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.shoppingcart.admin.AbstractExporter;
import com.shoppingcart.admin.entity.Brand;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class BrandPdfExporter extends AbstractExporter {
	public void export(List<Brand> listBrands, HttpServletResponse response) throws IOException {
		super.setResponseHeader(response, "application/pdf", ".pdf", "brands");

		Document document = new Document(PageSize.A4);
		PdfWriter.getInstance(document, response.getOutputStream());

		document.open();

		Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
		font.setSize(18);
		font.setColor(java.awt.Color.BLUE);

		Paragraph paragraph = new Paragraph("List of Brand", font);
		paragraph.setAlignment(Paragraph.ALIGN_CENTER);

		document.add(paragraph);

		PdfPTable table = new PdfPTable(5);
		table.setWidthPercentage(100f);
		table.setSpacingBefore(10);
		table.setWidths(new float[] {1.2f, 3.5f, 3.0f});

		writeTableHeader(table);
		writeTableData(table, listBrands);

		document.add(table);
		document.close();
	}

	private void writeTableData(PdfPTable table, List<Brand> listBrands) {
		for (Brand brand : listBrands) {
			table.addCell(String.valueOf(brand.getId()));
			table.addCell(brand.getLogos());
			table.addCell(brand.getName());

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
		cell.setPhrase(new Phrase("Logos", font));
		table.addCell(cell);
		cell.setPhrase(new Phrase("Name", font));
		table.addCell(cell);

	}
}
