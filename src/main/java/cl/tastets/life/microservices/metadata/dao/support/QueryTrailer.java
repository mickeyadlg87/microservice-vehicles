package cl.tastets.life.microservices.metadata.dao.support;

/**
 * Created by glucero on 08-07-16.
 */
public class QueryTrailer {

    public static final String queryTrailerMetadataPaginatedRslite = "SELECT t.id,t.nombre AS name,t.patente AS plateNumber, t.trailer_id AS trailerId, t.empresa_id AS companyId, " +
            " ? AS total, ? AS realm FROM trailer t WHERE t.empresa_id = ? {WHERE_CLAUSE}";

    public static final String queryTrailerMetadataPaginatedCountRslite = "SELECT count(t.id) FROM trailer t WHERE t.empresa_id = ? {WHERE_CLAUSE}";

    public static final String insertRslite = "INSERT INTO trailer (nombre,patente,trailer_id, empresa_id) " +
            " VALUES(?,?,?,?)";

    public static final String updateRslite = "UPDATE trailer  SET nombre = ?, patente = ?, trailer_id = ?, empresa_id = ? " +
            " WHERE id = ?";
    public static final String deleteRslite = "DELETE FROM trailer WHERE id = ?";
}
