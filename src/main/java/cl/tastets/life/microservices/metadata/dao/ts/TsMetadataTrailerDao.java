package cl.tastets.life.microservices.metadata.dao.ts;

import cl.tastets.life.commons.services.DataSourceDao;
import cl.tastets.life.microservices.metadata.dao.support.QueryTrailer;
import cl.tastets.life.objects.BasicEntity;
import cl.tastets.life.objects.RealmEnum;
import cl.tastets.life.objects.Trailer;
import cl.tastets.life.objects.utils.Paginated;
import cl.tastets.life.objects.utils.QueryFilter;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.sql.PreparedStatement;
import java.util.*;

/**
 * Created by glucero on 08-07-16.
 */
@Repository
public class TsMetadataTrailerDao extends TsAbstractDao {

    private static final Logger logger = Logger.getLogger("metadataVehicles");

    private Map<String, String> queries;


    @Autowired
    private DataSourceDao dao;

    public TsMetadataTrailerDao() {
    }

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

        queries.put("queryTrailerMetadataPaginated" + RealmEnum.rslite, QueryTrailer.queryTrailerMetadataPaginatedRslite);
        queries.put("queryTrailerMetadataPaginatedCount" + RealmEnum.rslite, QueryTrailer.queryTrailerMetadataPaginatedCountRslite);
        //CRUD
        queries.put("insert" + RealmEnum.rslite, QueryTrailer.insertRslite);
        queries.put("update" + RealmEnum.rslite, QueryTrailer.updateRslite);
        queries.put("delete" + RealmEnum.rslite, QueryTrailer.deleteRslite);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Trailer save(Trailer trailer, String realm) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        getJdbcTemplate(realm).update((connection) -> {
            PreparedStatement ps = connection.prepareStatement(queries.get("insert" + realm), new String[]{"id"});
            ps.setObject(1, trailer.get("name"));
            ps.setObject(2, trailer.get("plateNumber"));
            ps.setObject(3, trailer.get("trailerId"));
            ps.setObject(4, trailer.get("companyId"));
            return ps;
        }, keyHolder);
        trailer.put("id", keyHolder.getKey().intValue());
        return trailer;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Trailer update(Trailer trailer, String realm) {
        int update = getJdbcTemplate(realm).update(queries.get("update" + realm),
                new Object[]{
                        trailer.get("name"), trailer.get("plateNumber"), trailer.get("trailerId"), trailer.get("companyId"), trailer.get("id")
                });
        return trailer;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Trailer delete(Trailer trailer, String realm) {
        int id = getJdbcTemplate(realm.toString()).update(queries.get("delete" + realm), new Object[]{trailer.get("id")});
        return trailer.put("id", -1);
    }

    public List<Trailer> getAllByCompany(String realm, Integer companyId, Optional<Paginated> paginated, Optional<QueryFilter> filter,String queryName) {
        List<Trailer> trailers = new ArrayList<>();
        int total = getTotal(realm, () -> filterQueryValues(queryName + "Count" + realm, "t", paginated, filter, false),
                () -> new Object[]{companyId});
        trailers.addAll(getListByCriteria(realm, () -> filterQueryValues(queryName + realm, "t", paginated, filter, true),
                () -> new Object[]{total, realm, companyId}));

        return trailers;
    }

    private String filterQueryValues(String query, String alias, Optional<Paginated> paginated, Optional<QueryFilter> filter, Boolean sortPaginated) {
        StringBuffer filteredQuery = new StringBuffer();
        StringBuffer queryReal = new StringBuffer(queries.get(query));
        filter.ifPresent((f) -> {
            f.getList("filter").parallelStream().forEach((Map m) -> {
                BasicEntity k = new BasicEntity();
                k.putAll(m);
                if (k.get("id") != null) {
                    filteredQuery.append(" {alias}.id = ").append(k.get("id")).append(" AND ");
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
                if (k.get("trailerId") != null) {
                    filteredQuery.append(" {alias}.trailer_id LIKE '%").append(k.get("trailerId")).append("%' AND ");
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
                    if (f.getString("sort").contains("name")) {
                        queryReal.append(f.getString("sort").replace("name", "nombre"));
                    }
                    if (f.getString("sort").contains("plateNumber")) {
                        queryReal.append(f.getString("sort").replace("plateNumber", "patente"));
                    }
                    if (f.getString("sort").contains("trailerId")) {
                        queryReal.append(f.getString("sort").replace("trailerId", "trailer_id"));
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
