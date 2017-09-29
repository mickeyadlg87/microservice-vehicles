package cl.tastets.life.microservices.metadata.dao.ts;

import cl.tastets.life.commons.services.DataSourceDao;
import cl.tastets.life.microservices.metadata.dao.support.QueryFleet;
import cl.tastets.life.objects.BasicEntity;
import cl.tastets.life.objects.Fleet;
import cl.tastets.life.objects.RealmEnum;
import cl.tastets.life.objects.utils.Paginated;
import cl.tastets.life.objects.utils.QueryFilter;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.sql.PreparedStatement;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author gaston
 */
@Repository
public class TsMetadataFleetDao extends TsAbstractDao {

    private static final Logger logger = Logger.getLogger("metadataVehicles");

    private Map<String, String> queries;

    @Value("${mongo.collection.name.lastState}")
    private String lastState;

    @Autowired
    private DataSourceDao dao;

    public TsMetadataFleetDao() {
    }

	@Override
    public DataSourceDao getDao() {
        return dao;
    }

    /**
     * Metodo que inicializa las queries, por convencion una query debe tener el nombre "nombre" + "realm" Ej:
     * entelgetAll
     */
    @PostConstruct
    public void initQueries() {
        queries = new HashMap<>();
        queries.put("queryFindFleetById" + RealmEnum.rslite, QueryFleet.queryFindFleetByIdRslite);
        queries.put("queryFindUserFleetById" + RealmEnum.rslite, QueryFleet.queryFindUserFleetByIdRslite);
        queries.put("queryFindVehiclesFleetById" + RealmEnum.rslite, QueryFleet.queryFindVehiclesFleetByIdRslite);
        queries.put("queryFindCompaniesFleetById" + RealmEnum.rslite, QueryFleet.queryFindCompaniesFleetByIdRslite);


        //Bsuqueda de los datos de las flotas
        queries.put("queryFleetByUserAdmin" + RealmEnum.rslite, QueryFleet.queryFleetByUserAdminRslite);
        queries.put("queryFleetByUserAdminCount" + RealmEnum.rslite, QueryFleet.queryFleetByUserAdminCountRslite);
        queries.put("queryFleetByUserNotAdmin" + RealmEnum.rslite, QueryFleet.queryFleetByUserNotAdminRslite);
        queries.put("queryFleetByUserNotAdminCount" + RealmEnum.rslite, QueryFleet.queryFleetByUserNotAdminCountRslite);

        queries.put("queryFleetByUser" + RealmEnum.entel, QueryFleet.queryFleetByUserEntel);
        queries.put("queryFleetByUserCount" + RealmEnum.entel, QueryFleet.queryFleetByUserCountEntel);
        queries.put("queryFleetByUser" + RealmEnum.rastreosat, QueryFleet.queryFleetByUserEntel);
        queries.put("queryFleetByUserAdminCount" + RealmEnum.rastreosat, QueryFleet.queryFleetByUserCountEntel);

        queries.put("queryFleetStatus" + RealmEnum.rslite, QueryFleet.queryFleetStatusRslite);
        queries.put("queryFleetStatus" + RealmEnum.rastreosat, QueryFleet.queryFleetStatusEntel);
        queries.put("queryFleetStatus" + RealmEnum.entel, QueryFleet.queryFleetStatusEntel);

        //Flota default
        queries.put("queryFindDefaultFleet" + RealmEnum.rslite, QueryFleet.queryFindDefaultFleetRslite);

        //CRUD
        queries.put("insert" + RealmEnum.rslite, QueryFleet.insertRslite);
        queries.put("update" + RealmEnum.rslite, QueryFleet.updateRslite);
        queries.put("delete" + RealmEnum.rslite, QueryFleet.deleteRslite);

        queries.put("insertFleetVehicles" + RealmEnum.rslite, QueryFleet.insertFleetVehiclesRslite);
        queries.put("insertFleetUsers" + RealmEnum.rslite, QueryFleet.insertFleetUsersRslite);
        queries.put("insertFleetSharedCompanies" + RealmEnum.rslite, QueryFleet.insertFleetSharedCompaniesRslite);

        queries.put("removeVehiclesFromFleet" + RealmEnum.rslite, QueryFleet.removeVehiclesFromFleetRslite);
        queries.put("removeUsersFromFleet" + RealmEnum.rslite, QueryFleet.removeUsersFromFleetRslite);
        queries.put("removeFleetSharedCompanies" + RealmEnum.rslite, QueryFleet.removeFleetSharedCompaniesRslite);

        queries.put("removeFleetReports" + RealmEnum.rslite, QueryFleet.removeFleetReportsRslite);
    }

    public Fleet findFleetById(String realm, Integer id, Optional<Boolean> withMetadata, String query) {
        Fleet fleet = new Fleet();
        fleet.putAll(getByCriteria(realm, () -> queries.get(query + realm), () -> new Object[]{realm, id}));
        if (withMetadata.isPresent() && withMetadata.get()) {
            fleet.put("users", getListByCriteria(realm, () -> queries.get("queryFindUserFleetById" + realm), () -> new Object[]{fleet.get("id")}));
            fleet.put("vehicles", getListByCriteria(realm, () -> queries.get("queryFindVehiclesFleetById" + realm), () -> new Object[]{fleet.get("id")}));
        }
        if (fleet.getInteger("shared") == 1) {
            fleet.put("companies", getListByCriteria(realm, () -> queries.get("queryFindCompaniesFleetById" + realm), () -> new Object[]{fleet.get("id")}));
        }
        return fleet;
    }

    public List<Fleet> findFleetsByUser(String realm, Optional<QueryFilter> filter, Optional<Paginated> paginated, String query) {
        List<Fleet> result = new ArrayList<>();
        switch (realm) {
            case "rslite": {
                if (filter.isPresent()) {
                    if (filter.get().get("userProfile") != null && filter.get().getString("userProfile").equals("ADMIN")) {
                        int total = getTotal(realm, () -> filterQueryValues(query + "AdminCount" + realm, "f", paginated, filter, false),
                                () -> new Object[]{filter.get().getInteger("companyId")});
                        result.addAll(getListByCriteria(realm, () -> filterQueryValues(query + "Admin" + realm, "f", paginated, filter, true),
                                () -> new Object[]{total, realm, filter.get().getInteger("companyId")}));
                    } else if ((filter.get().get("userProfile") != null && filter.get().getString("userProfile").equals("NOT_ADMIN")) || (filter.get().get("byUser") != null && filter.get().getBoolean("byUser"))) {
                        int total = getTotal(realm, () -> filterQueryValues(query + "NotAdminCount" + realm, "f", paginated, filter, false),
                                () -> new Object[]{filter.get().getInteger("companyId"), filter.get().getInteger("userId")});
                        result.addAll(getListByCriteria(realm, () -> filterQueryValues(query + "NotAdmin" + realm, "f", paginated, filter, true),
                                () -> new Object[]{total, realm, filter.get().getInteger("companyId"), filter.get().getInteger("userId")}));
                    }
                }
                break;
            }
            default: {
                int total = getTotal(realm, () -> filterQueryValues(query + "Count" + realm, "f", paginated, filter, false),
                        () -> new Object[]{filter.get().getInteger("userId")});
                result.addAll(getListByCriteria(realm, () -> filterQueryValues(query + realm, "f", paginated, filter, true),
                        () -> new Object[]{total, realm, filter.get().getInteger("userId")}));
            }

        }
        return result;
    }

    public Fleet fleetStatus(String realm, Integer id, String query) {
        Fleet fleet = new Fleet().put("id", id);
        List<Fleet> result = new ArrayList<>();
        result.addAll(getListByCriteria(realm, () -> queries.get(query + realm), () -> new Object[]{id}));
        MongoCollection<Document> collection = getMongoCollection().getCollection(lastState);
        List<String> listMids = result.parallelStream().filter((Map vehicle) -> {
            return vehicle.get("_m") != null;
        }).map(
                (Map v) -> (String) v.get("_m")
        ).collect(Collectors.toList());
        List<Document> fleets = collection.find(Filters.in("_id", listMids)).into(new ArrayList<Document>());

        final Map<String, Long> status = new HashMap<>();
        status.put("INACTIVE24", 0L);
        status.put("INACTIVE", 0L);
        status.put("ACTIVE", 0L);
        status.put("ACTIVE5KM", 0L);
        fleets.stream().forEach((Map v) -> {
            long diff = (System.currentTimeMillis() - ((Date) v.get("lastDate")).getTime()) / 1000L;
            if (diff > 86400) {
                status.put("INACTIVE24", status.get("INACTIVE24") + 1);
            } else if (diff > 28800 && diff <= 86400) {
                status.put("INACTIVE", status.get("INACTIVE") + 1);
            } else if (diff < 28800) {
                status.put("ACTIVE", status.get("ACTIVE") + 1);
            } else if (v.get("lastPosition") != null && ((Document) v.get("lastPosition")).get("speed") != null
                    && diff <= 900 && ((Double) ((Document) v.get("lastPosition")).get("speed")) >= 5) {
                status.put("ACTIVE5KM", status.get("ACTIVE5KM"));
            }
        });

        fleet.put("status", status);
        return fleet;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Fleet save(Fleet fleet, String realm) {
        //Persisto la flota sola
        KeyHolder keyHolder = new GeneratedKeyHolder();
        getJdbcTemplate(realm).update((connection) -> {
            PreparedStatement ps = connection.prepareStatement(queries.get("insert" + realm), new String[]{"id"});
            ps.setObject(1, fleet.get("name"));
            ps.setObject(2, fleet.get("companyId"));
            ps.setObject(3, fleet.get("defaultGroup"));
            //compartida
            ps.setObject(4, (fleet.get("companies") != null && !fleet.getList("companies").isEmpty()));
            ps.setObject(5, fleet.get("generateReport") != null ? fleet.get("generateReport") : false);
            ps.setObject(6, fleet.get("maxSpeed"));
            ps.setObject(7, fleet.get("startDay"));
            ps.setObject(8, fleet.get("startHour"));
            ps.setObject(9, fleet.get("endDay"));
            ps.setObject(10, fleet.get("endHour"));
            ps.setObject(11, fleet.getOrDefault("inactivityDays", 2, Integer.class));
            return ps;
        }, keyHolder);
        fleet.put("id", keyHolder.getKey().intValue());

        //si tiene moviles asociados se guardan la tabla movil_flota
        if (fleet.get("vehicles") != null && !fleet.getList("vehicles").isEmpty()) {
            String updateFleetVehicle = queries.get("insertFleetVehicles" + realm);
            StringBuffer values = new StringBuffer();
            fleet.getList("vehicles").stream().forEach((vehicle) -> {
                values.append("(").append(fleet.get("id")).append(",").append(vehicle.get("id")).append("),");
            });
            updateFleetVehicle = updateFleetVehicle.replace("{VALUES}", values.substring(0, values.length() - 1));
            getJdbcTemplate(realm).update(updateFleetVehicle);
        }

        //Si tiene usuarios asociados se guardan los datos en la tabla permisos_usuario_flota
        if (fleet.get("users") != null && !fleet.getList("users").isEmpty()) {
            String updateFleetUser = queries.get("insertFleetUsers" + realm);
            StringBuffer values = new StringBuffer();
            fleet.getList("users").stream().forEach((user) -> {
                values.append("(").append(fleet.get("id")).append(",").append(user.get("id")).append(",").append(1).append("),");
            });
            updateFleetUser = updateFleetUser.replace("{VALUES}", values.substring(0, values.length() - 1));
            getJdbcTemplate(realm).update(updateFleetUser);
        }

        //Si tiene empresas a las que se le comparte esta flota
        if (fleet.get("companies") != null && !fleet.getList("companies").isEmpty()) {

            //por cada empresa se busca la flota default
            fleet.getList("companies").stream().forEach((company) -> {
                //Se comparte la flota con esta empresa
                getJdbcTemplate(realm).update(queries.get("insertFleetSharedCompanies" + realm), new Object[]{
                        company.get("id"), fleet.get("id"), fleet.get("id")
                });

                //Se agregan todos los moviles de esta flota en la flota default de esta empresa
                Fleet defaultFleet = new Fleet();

                defaultFleet.putAll(getByCriteria(realm, () -> queries.get("queryFindDefaultFleet" + realm), () -> new Object[]{company.get("id")}));
                //Si la nueva flota tiene moviles se agregan a la flota default de esta empresa
                if (fleet.get("vehicles") != null && !fleet.getList("vehicles").isEmpty()) {
                    String updateFleetVehicle = queries.get("insertFleetVehicles" + realm);
                    StringBuffer values = new StringBuffer();
                    fleet.getList("vehicles").stream().forEach((vehicle) -> {
                        values.append("(").append(defaultFleet.get("id")).append(",").append(vehicle.get("id")).append("),");
                    });
                    updateFleetVehicle = updateFleetVehicle.replace("{VALUES}", values.substring(0, values.length() - 1));
                    getJdbcTemplate(realm).update(updateFleetVehicle);
                }
            });
        }
        return fleet;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Fleet update(Fleet fleet, String realm) {
        getJdbcTemplate(realm).update(queries.get("update" + realm),
                new Object[]{
                        fleet.get("name"), (fleet.get("companies") != null && !fleet.getList("companies").isEmpty()), fleet.getOrDefault("generateReport", false, Boolean.class)
                        , fleet.get("maxSpeed"), fleet.get("startDay"), fleet.get("startHour"), fleet.get("endDay"), fleet.get("endHour"), fleet.get("inactiveDays"), fleet.get("id")
                });

        //si tiene moviles asociados se guardan la tabla movil_flota
        if (fleet.get("vehicles") != null && !fleet.getList("vehicles").isEmpty()) {

            // no elimina moviles dado que se esta certificando la unidad
            if (fleet.get("unitCertification") == null) {
                //Elimina los moviles compartidos de la base , para agregar los que estan actualizados
                getJdbcTemplate(realm).update(queries.get("removeVehiclesFromFleet" + realm), new Object[]{fleet.get("id")});
            }


            String updateFleetVehicle = queries.get("insertFleetVehicles" + realm);
            StringBuffer values = new StringBuffer();
            fleet.getList("vehicles").stream().forEach((vehicle) -> {
                values.append("(").append(fleet.get("id")).append(",").append(vehicle.get("id")).append("),");
            });
            updateFleetVehicle = updateFleetVehicle.replace("{VALUES}", values.substring(0, values.length() - 1));
            getJdbcTemplate(realm).update(updateFleetVehicle);
        }

        //Si tiene usuarios asociados se guardan los datos en la tabla permisos_usuario_flota
        if (fleet.get("users") != null && !fleet.getList("users").isEmpty()) {

            //Elimina los usuarios de la base , para agregar los que estan actualizados
            getJdbcTemplate(realm).update(queries.get("removeUsersFromFleet" + realm), new Object[]{fleet.get("id")});

            String updateFleetUser = queries.get("insertFleetUsers" + realm);
            StringBuffer values = new StringBuffer();
            fleet.getList("users").stream().forEach((user) -> {
                values.append("(").append(fleet.get("id")).append(",").append(user.get("id")).append(",").append(1).append("),");
            });
            updateFleetUser = updateFleetUser.replace("{VALUES}", values.substring(0, values.length() - 1));
            getJdbcTemplate(realm).update(updateFleetUser);
        }

        //Si tiene empresas a las que se le comparte esta flota
        if (fleet.get("companies") != null && !fleet.getList("companies").isEmpty()) {

            //Descomparte todas las empresas asociadas a la flota
            getJdbcTemplate(realm).update(queries.get("removeFleetSharedCompanies" + realm), new Object[]{fleet.get("id")});

            //por cada empresa se busca la flota default
            fleet.getList("companies").stream().forEach((company) -> {
                //Se comparte la flota con esta empresa
                getJdbcTemplate(realm).update(queries.get("insertFleetSharedCompanies" + realm), new Object[]{
                        company.get("id"), fleet.get("id"), fleet.get("id")
                });
                //Se agregan todos los moviles de esta flota en la flota default de esta empresa
                Fleet defaultFleet = new Fleet();
                defaultFleet.putAll(getByCriteria(realm, () -> queries.get("queryFindDefaultFleet" + realm), () -> new Object[]{company.get("id")}));

                //Si la nueva flota tiene moviles se agregan a la flota default de esta empresa
                if (fleet.get("vehicles") != null && !fleet.getList("vehicles").isEmpty()) {
                    String updateFleetVehicle = queries.get("insertFleetVehicles" + realm);
                    StringBuffer values = new StringBuffer();
                    fleet.getList("vehicles").stream().forEach((vehicle) -> {
                        values.append("(").append(defaultFleet.get("id")).append(",").append(vehicle.get("id")).append("),");
                    });
                    updateFleetVehicle = updateFleetVehicle.replace("{VALUES}", values.substring(0, values.length() - 1));
                    getJdbcTemplate(realm).update(updateFleetVehicle);
                }
            });
        }

        return fleet;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Fleet delete(Fleet fleet, String realm) {
        //Elimina los moviles compartidos de la base , para agregar los que estan actualizados
        getJdbcTemplate(realm.toString()).update(queries.get("removeVehiclesFromFleet" + realm), new Object[]{fleet.get("id")});

        //Elimina los usuarios de la base , para agregar los que estan actualizados
        getJdbcTemplate(realm.toString()).update(queries.get("removeUsersFromFleet" + realm), new Object[]{fleet.get("id")});

        //Elimina los reportes de la flota
        getJdbcTemplate(realm.toString()).update(queries.get("removeFleetReports" + realm), new Object[]{fleet.get("id")});

        //Se descomparte la flota de todas las empresas
        getJdbcTemplate(realm.toString()).update(queries.get("removeFleetSharedCompanies" + realm), new Object[]{fleet.get("id")});

        int id = getJdbcTemplate(realm.toString()).update(queries.get("delete" + realm), new Object[]{fleet.get("id")});
        return fleet.put("id", -1);
    }

    /**
     * Este metodo se usa para generar el query con filtro, paginacion y ordenamineto
     *
     * @param query         Query inicial
     * @param alias         Alias de la tabla a utilizar para los filtros
     * @param paginated     Paginacion, es opcional, si esta presente se agrega
     * @param filter        Filtros , opcional , si esta se evalua cada valor del mapa y se van armando la query
     * @param sortPaginated True si se tiene que ordenar, en cuyo caso el sort esta como una propiedad de QueryFilter
     * @return Query completo listo para ejecutarse
     */
    private String filterQueryValues(String query, String alias, Optional<Paginated> paginated, Optional<QueryFilter> filter, Boolean sortPaginated) {
        StringBuffer filteredQuery = new StringBuffer();
        StringBuffer queryReal = new StringBuffer(queries.get(query));
        filter.ifPresent((f) -> {
            f.getList("filter").parallelStream().forEach((Map m) -> {
                        BasicEntity k = new BasicEntity();
                        k.putAll(m);
                        if (k.get("id") != null) {
                            filteredQuery.append(" {alias}.id =").append(k.get("id")).append(" AND ");
                        }
                        if (k.get("name") != null) {
                            filteredQuery.append(" {alias}.nombre LIKE '%").append(k.get("name")).append("%' AND ");
                        }
                        if (k.get("companyId") != null) {
                            filteredQuery.append(" {alias}.empresa_id =").append(k.get("companyId")).append(" AND ");
                        }
                        if (k.get("generateReport") != null) {
                            filteredQuery.append(" {alias}.generar_cartola = ").append(k.get("generateReport")).append(" AND ");
                        }
                    }
            );
            //Le saco el ultimo AND al string de los filtros y le agrego el and al comienzo
            if (filteredQuery.toString()
                    .endsWith(" AND ")) {
                filteredQuery.replace(filteredQuery.lastIndexOf(" AND "), filteredQuery.length() - 1, " ");
                filteredQuery.insert(0, " AND ");
            }
            //Le concateno al queryReal al final el SORT
            if (sortPaginated) {
                if (f.get("sort") != null) {
                    queryReal.append(" ORDER BY {alias}.");
                    if (f.getString("sort").contains("name")) {
                        queryReal.append(f.getString("sort").replace("name", "nombre"));
                    }
                    if (f.getString("sort").contains("createDate")) {
                        queryReal.append(f.getString("sort").replace("createDate", "fecha_creacion"));
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

}
