package ru.nik.alfafamily.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import lombok.experimental.UtilityClass;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.web.multipart.MultipartFile;
import ru.nik.alfafamily.domain.Budget;
import ru.nik.alfafamily.domain.Category;
import ru.nik.alfafamily.domain.FinancialOperation;

@UtilityClass
public class Utilities {

	public List<FinancialOperation> parseCsv(MultipartFile file)
		throws IOException, ParseException {

		List<FinancialOperation> operations = new ArrayList<>();

		Reader in = new FileReader(convert(file), Charset.forName("windows-1251"));
		Iterable<CSVRecord> records = CSVFormat.TDF.withHeader().withSkipHeaderRecord(true)
			.parse(in);

		for (CSVRecord record : records) {

			Date date = new SimpleDateFormat("dd.MM.yyyy").parse(record.get(0));
			String type = record.get(1);
			Category category = new Category(record.get(2), null);
			Double sum = Double.valueOf(record.get(3).contains(",") ? record.get(3)
				.replace(".", "").replace(",", ".") : record.get(3));
			String currency = record.get(4);
			Long accountNumber = Double.valueOf(record.get(5).replace(",", ".")).longValue();
			String description = record.get(6);
			String comment = record.get(7);

			operations
				.add(new FinancialOperation(date, type, category, sum, currency, accountNumber,
					description, comment));
		}
		return operations;
	}

	private File convert(MultipartFile file) throws IOException {
		File convFile = null;
		if (file != null) {
			convFile = new File(file.getOriginalFilename());
			convFile.createNewFile();
			FileOutputStream fos = new FileOutputStream(convFile);
			fos.write(file.getBytes());
			fos.close();
		}
		return convFile;
	}

	public Budget countBudget(List<FinancialOperation> operations) {
		Budget budget = new Budget();
		Double income = 0.0;
		Double outcome = 0.0;

		for (FinancialOperation operation : operations) {
			if (operation.getType() != null && operation.getType().equals("доход")) {
				income += operation.getSum();
			} else if (operation.getType() != null && operation.getType().equals("расход")) {
				outcome += operation.getSum();
			}
		}
		return budget;
	}

	public List<FinancialOperation> getByDateBetween(List<FinancialOperation> operations, Date start, Date end) {
		List<FinancialOperation> result = new ArrayList<>();

		operations.forEach(operation -> {
			Date operationDate = operation.getDate();

			if ((operationDate.after(start) || operationDate.equals(start)) &
				(operationDate.before(end) || operationDate.equals(end))) {
				result.add(operation);
			}
		});
		result.sort(Comparator.comparing(FinancialOperation::getDate));
		return result;
	}
}
