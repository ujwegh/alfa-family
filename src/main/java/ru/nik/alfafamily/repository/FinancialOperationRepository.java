package ru.nik.alfafamily.repository;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import ru.nik.alfafamily.domain.FinancialOperation;

public interface FinancialOperationRepository extends MongoRepository<FinancialOperation, String> {

	List<FinancialOperation> findAllByCategory_Member_User_Id(String userId);

	int deleteAllByCategory_Member_Id(String id);
}
