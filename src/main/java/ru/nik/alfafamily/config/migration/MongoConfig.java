package ru.nik.alfafamily.config.migration;

import com.github.mongobee.Mongobee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class MongoConfig {

	@Autowired
	private MongoTemplate template;

	@Value ("${spring.data.mongodb.uri}")
	private String uri;

	@Bean
	public Mongobee mongobee(){
		Mongobee mongobee = new Mongobee(uri);
		mongobee.setChangeLogsScanPackage(DatabaseChangelog.class.getPackageName());
		// This next line fixes the missing constructor problem
		mongobee.setMongoTemplate(template);
		return mongobee;
	}

}
