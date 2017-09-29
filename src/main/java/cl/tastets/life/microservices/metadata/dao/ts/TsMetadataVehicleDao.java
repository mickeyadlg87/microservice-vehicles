package cl.tastets.life.microservices.metadata.dao.ts;

import cl.tastets.life.commons.date.DateTools;
import cl.tastets.life.commons.services.DataSourceDao;
import cl.tastets.life.microservices.metadata.dao.support.QueryVehicle;
import cl.tastets.life.objects.BasicEntity;
import cl.tastets.life.objects.RealmEnum;
import cl.tastets.life.objects.Vehicle;
import cl.tastets.life.objects.utils.Paginated;
import cl.tastets.life.objects.utils.QueryFilter;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import java.text.SimpleDateFormat;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author gaston
 */
@Repository
public class TsMetadataVehicleDao extends TsAbstractDao {

    private static final Logger logger = Logger.getLogger("metadataVehicles");

    private Map<String, String> queries;

    @Autowired
    private DataSourceDao dao;

    @Value("${mongo.collection.name.lastState}")
    private String lastState;

    @PostConstruct
    public void initQueries() {
        queries = new HashMap<>();
        queries.put("updateVehicle" + RealmEnum.rslite, QueryVehicle.updateVehicleRslite);

        //Por flota
        queries.put("queryMetadataFleetVehicleByUser" + RealmEnum.rslite, QueryVehicle.queryMetadataFleetVehicleByUserRslite);
        queries.put("queryMetadataFleetVehicleByUserCount" + RealmEnum.rslite, QueryVehicle.queryMetadataFleetVehicleByUserCountRslite);

        //paginado by company
        queries.put("queryCompanyMetadataPaginated" + RealmEnum.rslite, QueryVehicle.queryCompanyMetadataPaginatedRslite);
        queries.put("queryCompanyMetadataPaginatedCount" + RealmEnum.rslite, QueryVehicle.queryCompanyMetadataPaginatedCountRslite);
        //paginado by company (moviles de baja)
        queries.put("queryCompanyUnsuscribeUnit" + RealmEnum.rslite, QueryVehicle.queryCompanyUnsuscribeUnitRslite);
        queries.put("queryCompanyUnsuscribeUnitCount" + RealmEnum.rslite, QueryVehicle.queryCompanyUnsuscribeUnitCountRslite);

        //Metadata de tipos
        queries.put("queryVehicleType" + RealmEnum.rslite, QueryVehicle.queryVehicleTypeRslite);
        queries.put("queryEngineType" + RealmEnum.rslite, QueryVehicle.queryEngineTypeRslite);
        queries.put("querySubVehicleType" + RealmEnum.rslite, QueryVehicle.querySubVehicleTypeRslite);

    }


    @Transactional(propagation = Propagation.REQUIRED)
    @CacheEvict(value = {"vehicle", "vehicles"}, allEntries = true)
    public Vehicle update(Vehicle vehicle, RealmEnum realm, String query) {
        
        StringBuilder unitUpdate = new StringBuilder();
        String principalQuery = queries.get(query + realm);
        
        ArrayList<Object> arrayObjectQuery = new ArrayList<Object>();
        arrayObjectQuery.add(vehicle.get("name"));
        arrayObjectQuery.add(vehicle.get("plateNumber"));
        arrayObjectQuery.add(vehicle.get("vin"));
        arrayObjectQuery.add(vehicle.get("subVehicleType"));
        arrayObjectQuery.add(vehicle.get("engineType"));
        arrayObjectQuery.add(vehicle.get("companyId"));
        arrayObjectQuery.add(vehicle.get("extraFields"));
        arrayObjectQuery.add(vehicle.getOrDefault("validate", 1));
        arrayObjectQuery.add(vehicle.get("downDate") == null ? null : new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(vehicle.getLong("downDate"))));
        arrayObjectQuery.add(vehicle.getOrDefault("requestBy", null));
        arrayObjectQuery.add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(vehicle.getOrDefault("validateDate", System.currentTimeMillis(), Long.class))));
        if (vehicle.get("facturationCustomerId")!= null){
            unitUpdate.append(",empresa_id_facturar=? ");
            arrayObjectQuery.add(vehicle.get("facturationCustomerId"));
        }
        if (vehicle.get("typeSaleId") != null) {
            unitUpdate.append(",id_tipo_venta_movil=? ");
            arrayObjectQuery.add(vehicle.get("typeSaleId"));
        }
        arrayObjectQuery.add(vehicle.get("id"));
        principalQuery = principalQuery.replace("{WHERE_CLAUSE}", unitUpdate);
        getJdbcTemplate(realm.toString()).update(principalQuery,arrayObjectQuery.toArray());
        
        return vehicle;
    }


    public List<Vehicle> getCompanyMetadataPaginated(String realm, Integer companyId, Optional<Paginated> paginated, Optional<QueryFilter> filter, String queryName) {
        List<Vehicle> vehs = new ArrayList<>();
        int total = getTotal(realm, () -> filterQueryValues(queryName + "Count" + realm, "m", paginated, filter, false),
                () -> new Object[]{companyId});
        vehs.addAll(getListByCriteria(realm, () -> filterQueryValues(queryName + realm, "m", paginated, filter, true),
                () -> new Object[]{total, realm, companyId}));
        return vehs;
    }


    public List<Vehicle> getVehicleFleetMetadataByUser(String realm, Integer fleetId, Optional<Paginated> paginated, Optional<QueryFilter> filter, String query) {
        List<Vehicle> vehs = new ArrayList<>();
        int total = getTotal(realm, () -> filterQueryValues(query + "Count" + realm, "m", paginated, filter, false),
                () -> new Object[]{fleetId});
        vehs.addAll(getListByCriteria(realm, () -> filterQueryValues(query + realm, "m", paginated, filter, true),
                () -> new Object[]{total, realm, fleetId}));
        return vehs;
    }


    @Cacheable(value = "vehicleTypes")
    public List<BasicEntity> getTypesMetadata(String realm, String type) {
        List<BasicEntity> result = new ArrayList<>();
        switch (type) {
            case "vehicleType": {
                result.addAll(getListByCriteria(realm, () -> queries.get("queryVehicleType" + realm), () -> new Object[]{}));
                break;
            }
            case "engineType": {
                result.addAll(getListByCriteria(realm, () -> queries.get("queryEngineType" + realm), () -> new Object[]{}));
                break;
            }
            case "subVehicleType": {
                result.addAll(getListByCriteria(realm, () -> queries.get("querySubVehicleType" + realm), () -> new Object[]{}));
                break;
            }
        }
        return result;
    }


    public List<Document> lastStateOrdered(List<Vehicle> vehicles, Optional<Paginated> paginated, Optional<QueryFilter> filter) {
        MongoCollection<Document> collection = getMongoCollection().getCollection(lastState);
        //Se juntan los _m para el filtro en lastState
        List<String> listMids = vehicles.parallelStream().filter((Map vehicle) -> {
            return vehicle.get("_m") != null;
        }).map(
                (Map v) -> (String) v.get("_m")
        ).collect(Collectors.toList());
        List<Document> result;

        //Si el filtro esta presente y tiene esos valores
        final List<Bson> bsonFilter = new ArrayList<>();
        bsonFilter.add(0, Filters.in("_id", listMids));
        if (filter.isPresent()) {
            filter.get().getList("filter").parallelStream().forEach((Map m) -> {
                BasicEntity k = new BasicEntity();
                k.putAll(m);
                if (k.get("geolocation") != null) {
                    bsonFilter.add(Filters.regex("lastPosition.geotext", k.getString("geolocation")));
                }
                if (k.get("activityDateStart") != null) {
                    bsonFilter.add(Filters.gte("lastDate", k.get("activityDateStart")));
                }
                if (k.get("activityDateEnd") != null) {
                    bsonFilter.add(Filters.lte("lastDate", k.get("activityDateEnd")));
                }
                if (k.get("eventIds") != null) {
                    bsonFilter.add(Filters.in("lastId", k.getIntegerList("eventIds")));
                }
                if (k.get("inactiveInterval") != null) {
                    bsonFilter.add(Filters.lt("_t", DateTools.substractDate(System.currentTimeMillis(), k.getLong("inactiveInterval"), ChronoUnit.MILLIS)));
                }
            });
//
//			if (!bsonFilter.isEmpty()) {
//				paginated = Optional.empty();
//			}
        }
        if (paginated.isPresent()) {
            int total = (int) collection.count(Filters.and(bsonFilter));
            result = collection.find(Filters.and(bsonFilter)).sort(new Document().append("lastDate", -1)).skip(paginated.get().getOffset()).limit(paginated.get().getLimit())
                    .into(new ArrayList<Document>());
            result.parallelStream().forEach(d -> {
                d.put("total", total);
            });
        } else {// if (!bsonFilter.isEmpty()) {
            result = collection.find(Filters.and(bsonFilter)).sort(new Document().append("lastDate", -1)).into(new ArrayList<Document>());
        }
        return result;
    }

    private String filterQueryValues(String query, String alias, Optional<Paginated> paginated, Optional<QueryFilter> filter, Boolean sortPaginated) {
        StringBuffer filteredQuery = new StringBuffer();
        StringBuffer queryReal = new StringBuffer(queries.get(query));
        filter.ifPresent((f) -> {
            f.getList("filter").parallelStream().forEach((Map m) -> {
                BasicEntity k = new BasicEntity();
                k.putAll(m);
                if (k.get("id") != null) {
                    String ids = k.getIntegerList("id").stream().map(i -> i.toString()).collect(Collectors.joining(","));
                    filteredQuery.append(" {alias}.id IN(").append(ids).append(") AND ");
                }
                if (k.get("companyId") != null) {
                    filteredQuery.append(" {alias}.empresa_id = ").append(k.get("companyId")).append(" AND ");
                }
                if (k.get("name") != null) {
                    filteredQuery.append(" {alias}.nombre LIKE '%").append(k.get("name")).append("%' AND ");
                }
                if (k.get("plateNumber") != null) {
                    filteredQuery.append(" {alias}.patente LIKE '%").append(k.get("plateNumber")).append("%' AND ");
                }
                if (k.get("vin") != null) {
                    filteredQuery.append(" {alias}.vin LIKE '%").append(k.get("vin")).append("%' AND ");
                }
                if (k.get("validate") != null) {
                    if (k.getBoolean("validate")) {
                        filteredQuery.append(" {alias}.validado = 1 AND {alias}.fecha_validado IS NOT NULL AND ");
                    } else {
                        filteredQuery.append(" {alias}.validado is NULL AND {alias}.fecha_validado IS NULL AND ");
                    }
                }
                if (k.get("fleetId") != null) {
                    filteredQuery.append(" {alias}.id IN (SELECT movil_id FROM movil_flota WHERE flota_id = ").append(k.get("fleetId")).append(") AND ");
                }
                if (k.get("imei") != null) {
                    filteredQuery.append(" {alias}.id IN(SELECT movil_id FROM gps_movil where gps_imei LIKE  '%").append(k.get("imei")).append("%') AND ");
                }
                if (k.get("ignoredIds") != null) {
                    String ids = k.getIntegerList("ignoredIds").stream().map(i -> i.toString()).collect(Collectors.joining(","));
                    filteredQuery.append(" {alias}.id NOT IN(").append(ids).append(") AND ");
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
                    queryReal.append(" ORDER BY {alias}.");
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
        return queryReal.toString().replace("{WHERE_CLAUSE}", filteredQuery.toString()).replace("{alias}", alias);
    }


    public DataSourceDao getDao() {
        return dao;
    }
}
