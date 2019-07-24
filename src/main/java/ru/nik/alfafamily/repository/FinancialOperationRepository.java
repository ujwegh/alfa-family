package ru.nik.alfafamily.repository;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import ru.nik.alfafamily.domain.Category;
import ru.nik.alfafamily.domain.FinancialOperation;

public interface FinancialOperationRepository extends MongoRepository<FinancialOperation, String> {

	List<FinancialOperation> findAllByCategoryIn(List<Category> categories);

	int deleteAllByCategoryIn(List<Category> categories);
}
