package cl.tastets.life.microservices.metadata.dao.ts;

import cl.tastets.life.commons.services.DataSourceDao;
import com.mongodb.client.MongoDatabase;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Clase abstracta que contiene metodos comunes a la ejecucion de las queries
 *
 * @author gaston
 */
public abstract class TsAbstractDao {

	private final Map<String, JdbcTemplate> jdbcTemplates = new HashMap<>();

	protected Integer getTotal(String realm, Supplier<String> query, Supplier<Object[]> parameters) {
		return getJdbcTemplate(realm).queryForObject(query.get(), parameters.get(), Integer.class);
	}

	protected Map<String, Object> getByCriteria(String realm, Supplier<String> query, Supplier<Object[]> parameters) {
		return getJdbcTemplate(realm).queryForMap(query.get(), parameters.get());
	}

	protected Collection getListByCriteria(String realm, Supplier<String> query, Supplier<Object[]> parameters) {
		return getJdbcTemplate(realm).queryForList(query.get(), parameters.get());
	}

	/**
	 * Metodo que obtiene los jdbc templates por realm evitar la creación de este objecto en cada query que se realiza
	 *
	 * @param realm Realm asociado
	 * @return Jdbc template con el datasource correspondiente
	 */
	protected JdbcTemplate getJdbcTemplate(String realm) {
		if (!jdbcTemplates.containsKey(realm)) {
			synchronized (jdbcTemplates) {
				jdbcTemplates.put(realm, new JdbcTemplate((BasicDataSource)getDao().getSQLDataSource(realm)));
			}
		}
		return jdbcTemplates.get(realm);
	}

	public abstract DataSourceDao getDao();
	
	public MongoDatabase getMongoCollection() {
		return (MongoDatabase)getDao().getNoSQLCollection("mongo");
	}


}
