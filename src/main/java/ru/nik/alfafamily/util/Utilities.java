package ru.nik.alfafamily.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import lombok.experimental.UtilityClass;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import ru.nik.alfafamily.domain.Budget;
import ru.nik.alfafamily.domain.Category;
import ru.nik.alfafamily.domain.FinancialOperation;

@UtilityClass
public class Utilities {

	public List<FinancialOperation> parseCsv(MultipartFile file)
		throws IOException, ParseException {

		List<FinancialOperation> operations = new ArrayList<>();

		Reader in = new FileReader(convertToFile(file), Charset.forName("windows-1251"));
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

	public File convertToFile(MultipartFile file) throws IOException {
		File tempFile = File.createTempFile("tmp", ".tmp");
		try (FileOutputStream fos = new FileOutputStream(tempFile)) {
			fos.write(file.getBytes());
		}
		return tempFile;
	}

	public MultipartFile convertToMultipartFile(File file) throws IOException {
		String name = file.getName();
		byte[] content = Files.readAllBytes(file.toPath());
		return new MockMultipartFile(name, content);
	}


	public Budget countBudget(List<FinancialOperation> operations) {
		Budget budget = new Budget();
		Double income = 0.0;
		Double outcome = 0.0;

		for (FinancialOperation operation : operations) {
			if (operation.getType() != null && operation.getType().toLowerCase().equals("доход")) {
				income += operation.getSum();
			} else if (operation.getType() != null && operation.getType().toLowerCase()
				.equals("расход")) {
				outcome += operation.getSum();
			}
		}
		Date start = new Date();
		Date end = new Date();
		if (operations.size() > 0) {
			start =  operations.get(0).getDate();
			end =operations.get(operations.size() - 1).getDate();
		}
		budget.setIncome(income);
		budget.setOutcome(outcome);
		budget.setStartDate(start);
		budget.setEndDate(end);
		return budget;
	}

	public List<FinancialOperation> getByDateBetween(List<FinancialOperation> operations, Date start, Date end) {
		List<FinancialOperation> result = new ArrayList<>();

		Date endd = end;

		Calendar c = Calendar.getInstance();
		c.setTime(endd);
		c.add(Calendar.DATE, 1);
		endd = c.getTime();

		for (FinancialOperation operation : operations) {
			Date operationDate = operation.getDate();
			if (operationDate.compareTo(start) >= 0 && operationDate.compareTo(simplifyDate(endd)) <= 0) {
				result.add(operation);
			}
		}
		result.sort(Comparator.comparing(FinancialOperation::getDate));
		return result;
	}

	public Date removeTime(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		try {
			date = sdf.parse(sdf.format(date));
			return date;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	private Date simplifyDate(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");

		String dat = format.format(date);

		try {
			return format.parse(dat);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}
}
