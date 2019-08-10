package ru.nik.alfafamily.repository;

import java.util.Date;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import ru.nik.alfafamily.domain.Category;
import ru.nik.alfafamily.domain.FinancialOperation;

public interface FinancialOperationRepository extends MongoRepository<FinancialOperation, String> {

	List<FinancialOperation> findAllByCategoryInOrderByDateDesc(List<Category> categories);

	int deleteAllByCategoryIn(List<Category> categories);

	List<FinancialOperation> findAllByCategory_IdInAndDateBetweenOrderByDateDesc(List<String> categories, Date start, Date end);

	boolean existsById(String operationId);
}
