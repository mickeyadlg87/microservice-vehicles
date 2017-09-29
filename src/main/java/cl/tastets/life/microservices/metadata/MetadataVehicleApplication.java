package cl.tastets.life.microservices.metadata;

import static com.google.common.base.Predicates.or;
import static springfox.documentation.builders.PathSelectors.regex;

import java.util.Arrays;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoDataAutoConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.google.common.base.Predicate;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;

import cl.tastets.life.commons.services.DataSourceDao;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Clase de configuracion del contexto
 *
 * @author gaston
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class,
	DataSourceTransactionManagerAutoConfiguration.class,
	MongoRepositoriesAutoConfiguration.class,
	MongoAutoConfiguration.class,
	MongoDataAutoConfiguration.class
})
@EnableDiscoveryClient
@EnableCaching
@EnableCircuitBreaker
@EnableTransactionManagement
@EnableSwagger2
public class MetadataVehicleApplication {

	@Autowired
	protected Environment env;

	public static void main(String[] args) throws InterruptedException {
		SpringApplication.run(MetadataVehicleApplication.class, args).registerShutdownHook();
	}

	@Autowired
	private ApplicationContext context;


	/**
	 * Container de distintos dataSources
	 *
	 * @return Container de datasources
	 */
	@Bean
	public DataSourceDao dataSourceDao() {
		DataSourceDao metadata = new DataSourceDao();
		metadata.put("rslite", dsRSLite());
		metadata.put("rastreosat", dsRS());
		metadata.put("entel", dsEntel());
		metadata.put("mongo", dsMongo());
		return metadata;
	}

	/**
	 * DataSource de rsite
	 *
	 * @return Conexion a bd de rslite
	 */
	@Bean
	public DataSource dsRSLite() {
		BasicDataSource dsRSLite = new BasicDataSource();
		dsRSLite.setDriverClassName("com.mysql.jdbc.Driver");
		dsRSLite.setUrl(env.getProperty("lite.url"));
		dsRSLite.setUsername(env.getProperty("lite.user"));
		dsRSLite.setPassword(env.getProperty("lite.password"));
		dsRSLite.setMaxActive(env.getProperty("lite.max", Integer.class));
		dsRSLite.setValidationQuery("SELECT 1");
		dsRSLite.setTestOnBorrow(true);
		return dsRSLite;
	}

	/**
	 * DataSource de rs
	 *
	 * @return Conexion a bd de rs
	 */
	@Bean
	public DataSource dsRS() {
		BasicDataSource dsRastreosat = new BasicDataSource();
		dsRastreosat.setDriverClassName("com.mysql.jdbc.Driver");
		dsRastreosat.setUrl(env.getProperty("rastreosat.url"));
		dsRastreosat.setUsername(env.getProperty("rastreosat.user"));
		dsRastreosat.setPassword(env.getProperty("rastreosat.password"));
		dsRastreosat.setMaxActive(env.getProperty("rastreosat.max", Integer.class));
		dsRastreosat.setValidationQuery("SELECT 1");
		dsRastreosat.setTestOnBorrow(true);
		return dsRastreosat;
	}

	/**
	 * DataSource de entel
	 *
	 * @return Conexion a bd de entel
	 */
	@Bean
	public DataSource dsEntel() {
		BasicDataSource dsEntel = new BasicDataSource();
		dsEntel.setDriverClassName("com.mysql.jdbc.Driver");
		dsEntel.setUrl(env.getProperty("entel.url"));
		dsEntel.setUsername(env.getProperty("entel.user"));
		dsEntel.setPassword(env.getProperty("entel.password"));
		dsEntel.setMaxActive(env.getProperty("entel.max", Integer.class));
		dsEntel.setValidationQuery("SELECT 1");
		dsEntel.setTestOnBorrow(true);
		return dsEntel;
	}

	/**
	 * DataSource de mongo
	 *
	 * @return Conexion a bd de mongo
	 */
	@Bean
	public MongoDatabase dsMongo() {
		MongoClient mongoClient = new MongoClient(new MongoClientURI(env.getProperty("mongo.host")));
		MongoDatabase mongoDatabase = mongoClient.getDatabase(env.getProperty("mongo.database.name"));
		return mongoDatabase;
	}

	@Bean
	public CacheManager getEhCacheManager() {
		return new EhCacheCacheManager(getEhCacheFactory().getObject());
	}

	@Bean
	public EhCacheManagerFactoryBean getEhCacheFactory() {
		EhCacheManagerFactoryBean factoryBean = new EhCacheManagerFactoryBean();
		factoryBean.setConfigLocation(new ClassPathResource("ehcache.xml"));
		factoryBean.setShared(true);
		return factoryBean;
	}

	@Bean
	public PlatformTransactionManager getTxRslite() {
		return new DataSourceTransactionManager(dsRSLite());
	}

	@Bean
	public Docket apiGateway() {
		return new Docket(DocumentationType.SWAGGER_2)
				.groupName("metadata-vehicles")
				.apiInfo(apiInfo())
				.select()
				.paths(apiPaths())
				.build()
				.securitySchemes(Arrays.asList(new ApiKey("key", "api_key", "header")));
	}

	private Predicate<String> apiPaths() {
		return or(
				regex("/metadata.*")
		);
	}

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder()
				.title("MetadataVehicle Microservice")
				.description("Microservicio de MetadataVehicle")
				.contact("Redd")
				.licenseUrl("http://www.reddsystem.com")
				.build();
	}

	@Bean
	public WebMvcConfigurerAdapter adapter() {
		return new WebMvcConfigurerAdapter() {

			@Override
			public void addResourceHandlers(ResourceHandlerRegistry registry) {
				if (!registry.hasMappingForPattern("/webjars/**")) {
					registry.addResourceHandler("/webjars/**").addResourceLocations(
							"classpath:/META-INF/resources/webjars/");
				}

			}
		};
	}
}
