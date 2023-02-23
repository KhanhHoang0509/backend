package com.shoppingcart.admin.user.export;

import com.shoppingcart.admin.AbstractExporter;
import com.shoppingcart.admin.entity.User;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class UserCsvExporter extends AbstractExporter {
	public void export(List<User> listUsers, HttpServletResponse response) throws IOException {
		super.setResponseHeader(response, "text/csv", ".csv", "users_");
		ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);

		String[] csvHeader = {"User ID", "Email", "First Name", "Last Name", "Roles", "Enabled"};
		String[] filedMapping = {"id", "email", "firstName", "lastName", "roles", "enabled"};
		csvWriter.writeHeader(csvHeader);

		for (User user : listUsers) {
			csvWriter.write(user, filedMapping);
		}
		csvWriter.close();
	}
}
