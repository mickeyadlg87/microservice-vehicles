package cl.tastets.life.microservices.metadata.dao.ts;

import static cl.tastets.life.objects.RealmEnum.rslite;

import java.io.Writer;
import java.net.URI;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import cl.tastets.life.commons.services.DataSourceDao;
import cl.tastets.life.microservices.metadata.dao.support.Query;
import cl.tastets.life.objects.BasicEntity;
import cl.tastets.life.objects.Fleet;
import cl.tastets.life.objects.Geotext;
import cl.tastets.life.objects.RealmEnum;
import cl.tastets.life.objects.ServicesEnum;
import cl.tastets.life.objects.Vehicle;
import cl.tastets.life.objects.utils.Paginated;
import cl.tastets.life.objects.utils.QueryFilter;
import cl.tastets.life.objects.utils.RequestData;

/**
 * Este bean contiene todos los metodos de consultas a las distintas bases de datos
 *
 * @author gaston
 */
@Repository
@SuppressWarnings({"unchecked", "rawtypes"})
public class TsMetadataDao extends TsAbstractDao {

    private static final Logger logger = Logger.getLogger("metadataVehicles");

    private Map<String, String> queries;

    @Autowired
    @LoadBalanced
    protected RestTemplate rest;

    @Autowired
    private DataSourceDao dao;

    @Value("${mongo.collection.name.lastState}")
    private String lastState;

    public TsMetadataDao() {
    }

    /**
     * Metodo que inicializa las queries, por convencion una query debe tener el nombre "nombre" + "realm" Ej:
     * entelgetAll
     */
    @PostConstruct
    public void initQueries() {
        queries = new HashMap<>();

        //Query CompanyMetadata
        queries.put("queryCompanyMetadata" + RealmEnum.rastreosat, Query.queryCompanyMetadataRs);
        queries.put("queryCompanyMetadata" + rslite, Query.queryCompanyMetadataRslite);
        queries.put("queryCompanyMetadata" + RealmEnum.entel, Query.queryCompanyMetadataRs);

        //Query queryFleetMetadata
        queries.put("queryFleetMetadata" + RealmEnum.entel, Query.queryFleetMetadataRs);
        queries.put("queryFleetMetadata" + RealmEnum.rastreosat, Query.queryFleetMetadataRs);
        queries.put("queryFleetMetadata" + rslite, Query.queryFleetMetadataRslite);

        //Query queryAllByRealm
        queries.put("queryAllByRealm" + RealmEnum.rastreosat, Query.queryAllRs);
        queries.put("queryAllByRealm" + RealmEnum.entel, Query.queryAllRs);
        queries.put("queryAllByRealm" + rslite, Query.queryAllRslite);
    
        //Query queryAllByRealm
        queries.put("queryAllByRealmWithFleet" + RealmEnum.rastreosat, Query.queryAllByRealmWithFleetRastreosat);
        queries.put("queryAllByRealmWithFleet" + RealmEnum.entel, Query.queryAllByRealmWithFleetRastreosat);
        queries.put("queryAllByRealmWithFleet" + rslite, Query.queryAllByRealmWithFleetRslite);
        

        //Query queryVehicleById
        queries.put("queryVehicleById" + RealmEnum.rastreosat, Query.queryVehicleByIdRastreosat);
        queries.put("queryVehicleById" + rslite, Query.queryVehicleByIdRslite);
        queries.put("queryVehicleById" + RealmEnum.entel, Query.queryVehicleByIdRastreosat);

        //Query vehicleByImei
        queries.put("queryVehicleByImei" + rslite, Query.queryVehicleByImeiRslite);
        queries.put("queryVehicleByImei" + RealmEnum.rastreosat, Query.queryVehicleByImeiRs);
        queries.put("queryVehicleByImei" + RealmEnum.entel, Query.queryVehicleByImeiRs);
        
        //Query queryVehicleByTag
        queries.put("queryVehicleByTag" + RealmEnum.rastreosat, Query.queryVehicleByTagRastreosat);
        queries.put("queryVehicleByTag" + rslite, Query.queryVehicleByTagRslite);
        queries.put("queryVehicleByTag" + RealmEnum.entel, Query.queryVehicleByTagRastreosat);

        //Query queryVehicleByUser
        queries.put("queryVehicleByUser" + RealmEnum.rastreosat, Query.queryVehicleByUserRastreosat);
        queries.put("queryVehicleByUser" + RealmEnum.entel, Query.queryVehicleByUserRastreosat);
        queries.put("queryVehicleByUserAdmin" + rslite, Query.queryVehicleByUserAdminRslite);
        queries.put("queryVehicleByUserNotAdmin" + rslite, Query.queryVehicleByUserNotAdminRslite);

        //Query queryFleetByModemId
        queries.put("queryFleetByModemId" + RealmEnum.rastreosat, Query.queryGroupsMetadataByModemIdRastreosat);
        queries.put("queryFleetByModemId" + RealmEnum.entel, Query.queryGroupsMetadataByModemIdRastreosat);
        queries.put("queryFleetByModemId" + rslite, Query.queryGroupsMetadataByModemIdRslite);

        //query queryDeviceEventIdByIdCompanyRS
        queries.put("queryDeviceEventIdByIdCompanyRS" + RealmEnum.rastreosat, Query.queryDeviceEventIdByIdCompanyRS);
        queries.put("queryDeviceEventIdByIdCompanyRS" + RealmEnum.entel, Query.queryDeviceEventIdByIdCompanyRS);
        
        // unidades para cerfificar
        queries.put("queryAllForCertificate" + RealmEnum.rslite, Query.queryUnitsForCertificateRsLite);

    }

    /**
     * Metodo que retorna los grupos a los que pertenece un movil con modemId determinado
     *
     * @param realm     Realm al que pertenece el movil
     * @param ModemId
     * @param queryName nombre de la query que se utilizara
     * @return lista de Mapa con los datos del grupo
     */

    public List<BasicEntity> getFleetMetadataByModemId(String realm, String ModemId, String queryName) {
        List<BasicEntity> groups = new ArrayList<>();
        groups.addAll(getListByCriteria(realm, () -> queries.get(queryName + realm), () -> new Object[]{ModemId}));
        return groups;
    }

    /**
     * Metodo que retorna los id de eventos que tiene programados una empresa
     *
     * @param realm     Realm al que pertenece el movil
     * @param companyId Id de la compañia
     * @return lista de Mapa con los id y nombre de los eventos programados en la compañia
     */

    public List<BasicEntity> getDeviceEventId(String realm, Integer companyId, String queryName) {
        List<BasicEntity> deviceEventsId = new ArrayList<>();
        deviceEventsId.addAll(getListByCriteria(realm, () -> queries.get(queryName + realm), () -> new Object[]{companyId}));

        return deviceEventsId;
    }

    /**
     * Metodo que retorna la metadata de un movil en particular
     *
     * @param realm     Realm al que pertenece el movil
     * @param mid       ModemId/imei del movil a consultar
     * @param queryName
     * @return Mapa con los datos del movil
     * @throws java.lang.Exception
     */

    @Cacheable(value = "vehicle")
    public Vehicle getMetadataByImei(String realm, String mid, String queryName) {
        Vehicle vehicle = new Vehicle();
        vehicle.putAll(getByCriteria(realm, () -> queries.get(queryName + realm), () -> new Object[]{realm, mid}));
        return vehicle;
    }

    /**
     * Metodo que busca moviles segun patente
     *
     * @param tag
     * @param realm
     * @param queryName
     * @return
     */

    @Cacheable(value = "tag")
    public Vehicle getMetadataByTag(String tag, String realm, String queryName) {
        Vehicle vehicle = new Vehicle();
        vehicle.putAll(getByCriteria(realm, () -> queries.get(queryName + realm), () -> new Object[]{tag}));
        return vehicle;
    }

    /**
     * Metodo privado qeu obtiene el lastState del mid
     *
     * @param vehs
     */

    public void lastStateMetadata(List<Vehicle> vehs) {
        MongoCollection<Document> collection = getMongoCollection().getCollection(lastState);
        vehs.parallelStream().filter((Map vehicle) -> {
            return vehicle.get("_m") != null;
        }).forEach((Map vehicle) -> {
            vehicle.put("lastState", lastStateMetadata((String) vehicle.get("_m"), collection));
        });
    }


    public Map<String, Object> lastState(String mid) {
        return lastStateMetadata(mid, getMongoCollection().getCollection(lastState));
    }


    public BasicEntity lastStateByMids(List<Vehicle> vehicles) {
        MongoCollection<Document> collection = getMongoCollection().getCollection(lastState);
        //Se juntan los _m para el filtro en lastState
        List<String> listMids = vehicles.parallelStream().filter((Map vehicle) -> {
            return vehicle.get("_m") != null;
        }).map(
                (Map v) -> (String) v.get("_m")
        ).collect(Collectors.toList());
        BasicEntity result = new BasicEntity();
        
        collection.find(Filters.in("_id", listMids)).forEach(new Consumer<Document>() {
            public void accept(Document i) {
                String mid = i.getString("_id");
                i.remove("_id");
                result.put(mid, i);
            }
        });
        return result;
    }

    /**
     * Metodo que busca en la coleccion mongo el laststate segun el _m especificado
     *
     * @param mid        ModemId/imei a consultar
     * @param collection Collection mongo
     * @return Mapa con los valores de laststate
     */
    private Map<String, Object> lastStateMetadata(String mid, MongoCollection<Document> collection) {
        Map<String, Object> result = new HashMap<>();
        try {
            Document document = collection.find(Filters.eq("_id", mid)).first();
            if (document != null && document.containsKey("_id") && document.containsKey("lastPosition")) {
                Document doc = (Document) document.get("lastPosition");
                result.putAll(doc);
                result.put("_m", document.get("_id"));
                result.put("latitude", doc.get("lat"));
                result.put("longitude", doc.get("lng"));
                result.put("_t", doc.get("date"));
                result.put("speed", doc.get("speed"));
                result.put("odometer", doc.get("odo"));
            }
        } catch (Exception e) {
            logger.error("Error al obtener lastState", e);
        }
        return result;
    }

    /**
     * Obtiene el listado de moviles de una empresa
     *
     * @param realm     Realm al que pertenece la empresa
     * @param companyId Id de la empresa
     * @return Listado de moviles
     */

    @Cacheable(value = "vehicles")
    public List<Vehicle> getCompanyMetadata(String realm, Integer companyId, String queryName) {
        List<Vehicle> vehs = new ArrayList<>();
        vehs.addAll(getListByCriteria(realm, () -> queries.get(queryName + realm), () -> new Object[]{realm, companyId}));
        return vehs;
    }

    /**
     * Obtiene el listado de moviles por flota de una empresa
     *
     * @param realm     Realm al que pertenece la flota
     * @param companyId Id de la empresa
     * @param fleetId   Id de la flota
     * @return Listado de moviles
     */

    @Cacheable(value = "fleets")
    public List<Vehicle> getFleetMetadata(String realm, Integer companyId, Integer fleetId, String queryName) {
        List<Vehicle> vehs = new ArrayList<>();
        vehs.addAll(getListByCriteria(realm, () -> queries.get(queryName + realm), () -> {
            if (realm.equals(rslite.name())) {
                return new Object[]{realm, companyId,
                        fleetId, realm, companyId, fleetId};
            } else {
                return new Object[]{realm, companyId,
                        fleetId};
            }
        }));
        return vehs;
    }

    /**
     * Obtiene todos los moviles
     *
     * @return Listado de todos los moviles
     */

//	@Cacheable(value = "vehicles")
    public List<Vehicle> getAll(Boolean onlyImei, String query) {
        List<Vehicle> vehs = new ArrayList<>();
        CompletableFuture<Collection> rslite = CompletableFuture.supplyAsync(() -> this.getByRealm(RealmEnum.rastreosat.name(), query, null));
        CompletableFuture<Collection> rastreosat = CompletableFuture.supplyAsync(() -> this.getByRealm(RealmEnum.rslite.name(), query, null));
        CompletableFuture<Collection> entel = CompletableFuture.supplyAsync(() -> this.getByRealm(RealmEnum.entel.name(), query, null));
        vehs.addAll(rastreosat.join());
        vehs.addAll(rslite.join());
        vehs.addAll(entel.join());
        return vehs;
    }

    /**
     * Obtiene los moviles de una plataforma
     *
     * @param realm Realm indicado
     * @param queryName
     * @param reqData
     * @return Todos los moviles de la plataforma
     */
//    @Cacheable(value = "vehicles", key = "{ #realm, #queryName }")
    public List<Vehicle> getByRealm(String realm, String queryName, RequestData reqData) {
        List<Vehicle> vehs = new ArrayList<>();
        if (reqData == null) {
            vehs.addAll(getListByCriteria(realm, () -> queries.get(queryName + realm), () -> new Object[]{realm}));
        } else {
            vehs.addAll(getListByCriteria(realm,
                    () -> filterQueryValues(queryName + realm, Optional.ofNullable(reqData.getPaginated()), Optional.ofNullable(reqData.getFilter()), Boolean.TRUE),
                    () -> new Object[]{realm}));
        }        
        return vehs;
    }

    /**
     * Metodo que busca moviles segun su id (base de datos)
     *
     * @param vehicleId
     * @param realm
     * @param queryName
     * @return Vehiculo que tiene ese id
     */

    public Vehicle getMetadataById(Integer vehicleId, String realm, String queryName) {
        Vehicle v = new Vehicle();
        v.putAll(getByCriteria(realm, () -> queries.get(queryName + realm), () -> new Object[]{vehicleId}));
        return v;
    }


    public List<Vehicle> getVehiclesByUser(Integer companyId, String realm, Optional<QueryFilter> filter, Optional<Paginated> paginated, String queryName) {
        final List<Vehicle> vehs = Collections.synchronizedList(new ArrayList<>());
        switch (realm) {
            case "rslite": {
                if (filter.isPresent()) {
                    if (filter.get().get("userProfile") != null && filter.get().getString("userProfile").equals("ADMIN")) {
                        logger.info( "Querying ... " + queryName + "Admin" + realm + " " + queries.get(queryName + "Admin" + realm ) );
                        vehs.addAll(getListByCriteria(realm, () -> queries.get(queryName + "Admin" + realm),
                                () -> new Object[]{realm, companyId, realm, companyId}));
                    } else if (filter.get().get("userProfile") != null && filter.get().getString("userProfile").equals("NOT_ADMIN")) {
                        logger.info( "Querying ... " + queryName + "NotAdmin" + realm + " " + queries.get(queryName + "NotAdmin" + realm ) );
                        vehs.addAll(getListByCriteria(realm, () -> queries.get(queryName + "NotAdmin" + realm),
                                () -> new Object[]{realm, filter.get().getInteger("userId")}));
                    }
                }
                break;
            }
            default: {
                vehs.addAll(getListByCriteria(realm, () -> queries.get(queryName + realm), () -> new Object[]{filter.get().getInteger("userId"), companyId}));
            }
        }
        return vehs;
    }

    // Comandos hystrix

    /**
     * Metodo que busca el geotexto para las coordenadas indicadas
     *
     * @param vehicle
     * @return
     */
    @HystrixCommand(fallbackMethod = "defaultGeotext")
    public Geotext getGeotext(Vehicle vehicle) {
        URI uri = UriComponentsBuilder.fromHttpUrl(ServicesEnum.GEOTEXT + "/geotext/geotextfind?longitude=" + (Double) ((Map) vehicle.get("lastState")).get("longitude") + "&latitude="
                + (Double) ((Map) vehicle.get("lastState")).get("latitude")).build().encode().toUri();
        return rest.getForObject(uri, Geotext.class);
    }

    public Geotext defaultGeotext(Vehicle vehicle) {
        Geotext be = new Geotext();
        be.put("geotext", "S/I");
        return be;
    }
    
    private String filterQueryValues(String query, Optional<Paginated> paginated, Optional<QueryFilter> filter, Boolean sortPaginated) {
        StringBuilder filteredQuery = new StringBuilder();
        StringBuilder queryReal = new StringBuilder(queries.get(query));
        filter.ifPresent((f) -> {
            f.getList("filter").parallelStream().forEach((Map m) -> {
                BasicEntity k = new BasicEntity();
                k.putAll(m);
                if (k.get("id") != null) {
                    String ids = k.getIntegerList("id").stream().map(i -> i.toString()).collect(Collectors.joining(","));
                    filteredQuery.append(" m.id IN(").append(ids).append(") AND ");
                }
                if (k.get("companyId") != null) {
                    filteredQuery.append(" m.empresa_id = ").append(k.get("companyId")).append(" AND ");
                }
                if (k.get("name") != null) {
                    filteredQuery.append(" m.nombre LIKE '%").append(k.get("name")).append("%' AND ");
                }
                if (k.get("plateNumber") != null) {
                    filteredQuery.append(" m.patente LIKE '%").append(k.get("plateNumber")).append("%' AND ");
                }
                if (k.get("vin") != null) {
                    filteredQuery.append(" m.vin LIKE '%").append(k.get("vin")).append("%' AND ");
                }
                if (k.get("validate") != null) {
                    if (k.getBoolean("validate")) {
                        filteredQuery.append(" m.validado = 1 AND m.fecha_validado IS NOT NULL AND ");
                    } else {
                        filteredQuery.append(" m.validado is NULL AND m.fecha_validado IS NULL AND ");
                    }
                }
                if (k.get("fleetId") != null) {
                    filteredQuery.append(" m.id IN (SELECT movil_id FROM movil_flota WHERE flota_id = ").append(k.get("fleetId")).append(") AND ");
                }
                if (k.get("imei") != null) {
                    filteredQuery.append(" m.id IN(SELECT movil_id FROM gps_movil where gps_imei LIKE  '%").append(k.get("imei")).append("%') AND ");
                }
            });
            //Le saco el ultimo AND al string de los filtros y le agrego el and al comienzo
            if (filteredQuery.toString().endsWith(" AND ")) {
                filteredQuery.replace(filteredQuery.lastIndexOf(" AND "), filteredQuery.length() - 1, " ");
                filteredQuery.insert(0, " AND ");
            }
            //Le concateno al queryReal al final el SORT
            if (sortPaginated) {
                if (f.get("sort") != null) {
                    queryReal.append(" ORDER BY m.");
                    if (f.getString("sort").contains("lastActivityDate")) {
                        queryReal.append(f.getString("sort").replace("lastActivityDate", "fecha_ultima_actividad")).append(" DESC");
                    }
                    if (f.getString("sort").contains("name")) {
                        queryReal.append(f.getString("sort").replace("name", "nombre"));
                    }

                }
            }
        });
        //Le concateno al queryReal al final el despues del SORT el paginado
        if (sortPaginated) {
            paginated.ifPresent(p -> {
                queryReal.append(" LIMIT ").append(p.getLimit()).append(" OFFSET ").append(p.getOffset());
            });
        }
        //En este punto el queryREal esta con sort y limit, y tengo que reemplazar donde aparezce WHERE_CLASE por el filterdQuery
        return queryReal.toString().replace("{WHERE_CLAUSE}", filteredQuery.toString());
    }


    public DataSourceDao getDao() {
        return dao;
    }
    
    
    public void getAll(String query, final Writer out, List<String> listIncidents, boolean imeiOnly, String ... realms)  {
    	
    	try {
	    	AtomicBoolean first = new AtomicBoolean(true);
	    	out.write('[');
	    	for (String realm : realms) {
		    		VechicleRowCallbackHandler sad = new VechicleRowCallbackHandler(out, imeiOnly, first, listIncidents);
		    		JdbcTemplate template = getJdbcTemplate(realm);
		    		template.query(new StreamingStatementCreator(queries.get(query + realm), realm), sad);
	    	}
	    	out.write(']');
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
    }
    
    static class VechicleRowCallbackHandler implements RowCallbackHandler {
    	static ColumnMapRowMapper cmrm = new ColumnMapRowMapper();
		static ObjectMapper mapper = new ObjectMapper();
		static TypeReference<List<Fleet>> typeRef = new TypeReference<List<Fleet>>() {};
		
    	final Writer out;
    	final boolean imeiOnly;
    	final AtomicBoolean first;
    	final List<String> listIncidents;
    	
    	public VechicleRowCallbackHandler(Writer out, boolean imeiOnly, AtomicBoolean first, List<String> listIncidents) {
    		this.out = out;
    		this.imeiOnly = imeiOnly;
    		this.first = first;
    		this.listIncidents = listIncidents;
    	}
    	
		@Override
		public void processRow(ResultSet rs) throws SQLException {
			Vehicle asdas = new Vehicle();
			if (imeiOnly) {
				// TODO escupir solo el imei
				asdas.put("_m", rs.getString("_m"));
				asdas.put("incidents", listIncidents);
				
			} else {
				// sacar data completa
				Map<String, Object> row = cmrm.mapRow(rs, 0);
				
				// complementar flotas
                asdas.putAll(row);
                String fleets = asdas.getString("fleets");
                if(fleets != null){
    				List<Fleet> list = new ArrayList<>();
                    try{
                        // String encodedList = String.valueOf(JsonStringEncoder.getInstance().quoteAsString(fleets));
                        list = mapper.readValue(fleets.replaceAll("\t", ""),typeRef);
                        asdas.put("fleets", list);
                    }catch (Exception e){
                        logger.error("Error al parsear JSON, para movil: "+ asdas, e);
                    }
                }
			}
			try {
				if (!first.getAndSet(false)) {
					out.write(",");
				}
				out.write(mapper.writeValueAsString(asdas));
			} catch (Exception e) {
				logger.error("Error al imprimir JSON, para movil: "+ asdas, e);
			}
		}
    }
    
    // pruebas streaming
    class StreamingStatementCreator implements PreparedStatementCreator {
        private final String sql;
        private final String realm;

        public StreamingStatementCreator(String sql, String realm) {
            this.sql = sql;
            this.realm = realm;
        }

        @Override
        public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
            final PreparedStatement statement = connection.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            statement.setFetchSize(Integer.MIN_VALUE);
            statement.setString(1, realm);
            return statement;
        }
    }

}
