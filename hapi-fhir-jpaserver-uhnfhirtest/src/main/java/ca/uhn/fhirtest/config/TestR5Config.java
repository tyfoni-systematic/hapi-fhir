package ca.uhn.fhirtest.config;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.jpa.api.config.JpaStorageSettings;
import ca.uhn.fhir.jpa.config.HapiJpaConfig;
import ca.uhn.fhir.jpa.config.r5.JpaR5Config;
import ca.uhn.fhir.jpa.config.util.HapiEntityManagerFactoryUtil;
import ca.uhn.fhir.jpa.model.config.SubscriptionSettings;
import ca.uhn.fhir.jpa.model.dialect.HapiFhirH2Dialect;
import ca.uhn.fhir.jpa.model.dialect.HapiFhirPostgres94Dialect;
import ca.uhn.fhir.jpa.search.DatabaseBackedPagingProvider;
import ca.uhn.fhir.jpa.util.CurrentThreadCaptureQueriesListener;
import ca.uhn.fhir.jpa.validation.ValidationSettings;
import ca.uhn.fhir.rest.server.interceptor.RequestValidatingInterceptor;
import ca.uhn.fhir.validation.IInstanceValidatorModule;
import ca.uhn.fhir.validation.ResultSeverityEnum;
import ca.uhn.fhirtest.interceptor.PublicSecurityInterceptor;
import jakarta.persistence.EntityManagerFactory;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;
import org.apache.commons.dbcp2.BasicDataSource;
import org.hl7.fhir.dstu2.model.Subscription;
import org.hl7.fhir.r5.utils.validation.constants.ReferenceValidationPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Properties;
import java.util.concurrent.TimeUnit;
import javax.sql.DataSource;

@Configuration
@Import({CommonConfig.class, JpaR5Config.class, HapiJpaConfig.class})
@EnableTransactionManagement()
public class TestR5Config extends BaseConfig {
	public static final String FHIR_DB_USERNAME = "fhir.db.username";
	public static final String FHIR_DB_PASSWORD = "fhir.db.password";
	public static final String FHIR_LUCENE_LOCATION_R5 = "fhir.lucene.location.r5";
	public static final Integer COUNT_SEARCH_RESULTS_UP_TO = 50000;
	private static final Logger ourLog = LoggerFactory.getLogger(TestR5Config.class);
	private String myDbUsername = System.getProperty(TestR5Config.FHIR_DB_USERNAME);
	private String myDbPassword = System.getProperty(TestR5Config.FHIR_DB_PASSWORD);
	private String myFhirLuceneLocation = System.getProperty(FHIR_LUCENE_LOCATION_R5);

	@Bean
	public SubscriptionSettings subscriptionSettings() {
		SubscriptionSettings retVal = new SubscriptionSettings();
		retVal.addSupportedSubscriptionType(Subscription.SubscriptionChannelType.RESTHOOK);
		retVal.setWebsocketContextPath("/websocketR5");
		return retVal;
	}

	@Bean
	public JpaStorageSettings storageSettings() {
		JpaStorageSettings retVal = new JpaStorageSettings();
		retVal.setAllowContainsSearches(true);
		retVal.setAllowMultipleDelete(true);
		retVal.setAllowInlineMatchUrlReferences(true);
		retVal.setAllowExternalReferences(true);
		retVal.getTreatBaseUrlsAsLocal().add("http://hapi.fhir.org/baseR5");
		retVal.getTreatBaseUrlsAsLocal().add("https://hapi.fhir.org/baseR5");
		retVal.getTreatBaseUrlsAsLocal().add("http://fhirtest.uhn.ca/baseR5");
		retVal.getTreatBaseUrlsAsLocal().add("https://fhirtest.uhn.ca/baseR5");
		retVal.setIndexMissingFields(JpaStorageSettings.IndexEnabledEnum.ENABLED);
		retVal.setCountSearchResultsUpTo(TestR5Config.COUNT_SEARCH_RESULTS_UP_TO);
		retVal.setFetchSizeDefaultMaximum(10000);
		retVal.setExpungeEnabled(true);
		retVal.setFilterParameterEnabled(true);
		retVal.setDefaultSearchParamsCanBeOverridden(false);
		retVal.setIndexOnContainedResources(true);
		retVal.setIndexIdentifierOfType(true);
		return retVal;
	}

	@Bean
	public ValidationSettings validationSettings() {
		ValidationSettings retVal = new ValidationSettings();
		retVal.setLocalReferenceValidationDefaultPolicy(ReferenceValidationPolicy.CHECK_VALID);
		return retVal;
	}

	@Bean(name = "myPersistenceDataSourceR5")
	public DataSource dataSource() {
		ourLog.info("Starting R5 database with DB username: {}", myDbUsername);
		ourLog.info("Have system property username: {}", System.getProperty(FHIR_DB_USERNAME));

		BasicDataSource retVal = new BasicDataSource();
		if (CommonConfig.isLocalTestMode()) {
			retVal.setUrl("jdbc:h2:mem:fhirtest_r5");
		} else {
			retVal.setDriver(new org.postgresql.Driver());
			retVal.setUrl("jdbc:postgresql://localhost/fhirtest_r5");
		}
		retVal.setUsername(myDbUsername);
		retVal.setPassword(myDbPassword);
		applyCommonDatasourceParams(retVal);

		DataSource dataSource = ProxyDataSourceBuilder.create(retVal)
				//			.logQueryBySlf4j(SLF4JLogLevel.INFO, "SQL")
				.logSlowQueryBySlf4j(10000, TimeUnit.MILLISECONDS)
				.afterQuery(new CurrentThreadCaptureQueriesListener())
				.countQuery()
				.build();

		return dataSource;
	}

	static void applyCommonDatasourceParams(BasicDataSource retVal) {
		retVal.setDefaultQueryTimeout(20);
		retVal.setTestOnBorrow(true);
		retVal.setMaxTotal(50);
		retVal.setMaxWaitMillis(25000);
	}

	// TODO KHS there is code duplication between this and the other Test*Config classes in this directory
	@Bean
	public DatabaseBackedPagingProvider databaseBackedPagingProvider() {
		DatabaseBackedPagingProvider retVal = new DatabaseBackedPagingProvider();
		retVal.setDefaultPageSize(20);
		retVal.setMaximumPageSize(500);
		return retVal;
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(
			ConfigurableListableBeanFactory theConfigurableListableBeanFactory,
			FhirContext theFhirContext,
			JpaStorageSettings theStorageSettings) {
		LocalContainerEntityManagerFactoryBean retVal = HapiEntityManagerFactoryUtil.newEntityManagerFactory(
				theConfigurableListableBeanFactory, theFhirContext, theStorageSettings);
		retVal.setPersistenceUnitName("PU_HapiFhirJpaR5");
		retVal.setDataSource(dataSource());
		retVal.setJpaProperties(jpaProperties());
		return retVal;
	}

	private Properties jpaProperties() {
		Properties extraProperties = new Properties();
		if (CommonConfig.isLocalTestMode()) {
			extraProperties.put("hibernate.dialect", HapiFhirH2Dialect.class.getName());
		} else {
			extraProperties.put("hibernate.dialect", HapiFhirPostgres94Dialect.class.getName());
		}
		extraProperties.put("hibernate.format_sql", "false");
		extraProperties.put("hibernate.show_sql", "false");
		extraProperties.put("hibernate.hbm2ddl.auto", "none");
		extraProperties.put("hibernate.jdbc.batch_size", "20");
		extraProperties.put("hibernate.cache.use_query_cache", "false");
		extraProperties.put("hibernate.cache.use_second_level_cache", "false");
		extraProperties.put("hibernate.cache.use_structured_entries", "false");
		extraProperties.put("hibernate.cache.use_minimal_puts", "false");

		configureLuceneProperties(extraProperties, myFhirLuceneLocation);

		return extraProperties;
	}

	/**
	 * Bean which validates incoming requests
	 *
	 * @param theFhirInstanceValidator
	 */
	@Bean
	@Lazy
	public RequestValidatingInterceptor requestValidatingInterceptor(
			IInstanceValidatorModule theFhirInstanceValidator) {
		RequestValidatingInterceptor requestValidator = new RequestValidatingInterceptor();
		requestValidator.setFailOnSeverity(null);
		requestValidator.setAddResponseHeaderOnSeverity(null);
		requestValidator.setAddResponseOutcomeHeaderOnSeverity(ResultSeverityEnum.INFORMATION);
		requestValidator.addValidatorModule(theFhirInstanceValidator);
		requestValidator.setIgnoreValidatorExceptions(true);

		return requestValidator;
	}

	@Bean
	public PublicSecurityInterceptor securityInterceptor() {
		return new PublicSecurityInterceptor();
	}

	@Bean
	@Primary
	public JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
		JpaTransactionManager retVal = new JpaTransactionManager();
		retVal.setEntityManagerFactory(entityManagerFactory);
		return retVal;
	}

	/**
	 * This lets the "@Value" fields reference properties from the properties file
	 */
	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}
}
