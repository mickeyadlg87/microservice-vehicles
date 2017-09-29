package cl.tastets.life.microservices.metadata.dao.support;

/**
 *
 * @author gaston
 */
public class QueryVehicle {

        public static final String updateVehicleRslite = "UPDATE movil SET nombre =?,patente=?,vin=?,sub_tipo_movil_id=?,"
                        + "tipo_motor_id=?,empresa_id=?,atributos_extra=?, validado=?, fecha_baja=?, ingresado_por=?, "
                        + "fecha_validado=? {WHERE_CLAUSE} WHERE id=?";
	
	//Administarcion de Moviles
	public static final String queryCompanyMetadataPaginatedRslite = " SELECT m.id , m.nombre AS name,m.patente AS plateNumber,"
			+ " m.fecha_creacion AS createDate,"
			+ " m.fecha_validado AS validateDate,m.fecha_baja AS dischargeDate, "
			+ " m.validado AS validate,m.vin AS vin,"
			+ " m.ingresado_por AS inputBy,m.empresa_id AS companyId,m.sub_tipo_movil_id AS subVehicleTypeId,m.tipo_motor_id AS engineTypeId,"
			+ " m.atributos_extra AS extraFields, m.empresa_id_facturar AS facturationCustomerId, m.id_tipo_venta_movil AS unitTypeSale, "
			+ " tm.nombre AS engineTypeName, tmv.nombre AS subVehicleTypeName,"
			+ " e.nombre AS companyName ,e.id AS idCompany, tmov.nombre AS vehicleTypeName, g.imei AS _m, s.numero_telefono AS simCardPhone, "
                        + " tdg.nombre as deviceTypeName , g.tipo_dispositivo_gps_id AS deviceTypeId, "
			+ " ? AS total, ? AS realm "
			+ " FROM movil AS m , tipo_motor tm, sub_tipo_movil tmv, tipo_dispositivo_gps as tdg, empresa e, tipo_movil tmov,gps AS g, gps_movil AS gm "
			+ " LEFT JOIN sim_card_gps sg ON gm.gps_imei = sg.gps_imei LEFT JOIN sim_card s ON sg.sim_card_id = s.id "
			+ " WHERE "
			+ " e.id = ? "
			+ " AND gm.movil_id = m.id "
			+ " AND gm.gps_imei = g.imei "
                        + " AND tdg.id = g.tipo_dispositivo_gps_id "
			+ " AND gm.fecha_desinstalacion IS NULL "			
			+ " AND m.tipo_motor_id = tm.id "
			+ " AND m.sub_tipo_movil_id = tmv.id "
			+ " AND m.empresa_id = e.id "
			+ " AND tmov.id = tmv.tipo_movil_id "
			+ " AND m.fecha_baja IS NULL {WHERE_CLAUSE} ";

	public static final String queryCompanyMetadataPaginatedCountRslite = "SELECT count(m.id) "
		    + " FROM movil AS m , tipo_motor tm, sub_tipo_movil tmv, empresa e, tipo_movil tmov,gps AS g, gps_movil AS gm "
			+ " LEFT JOIN sim_card_gps sg ON gm.gps_imei = sg.gps_imei LEFT JOIN sim_card s ON sg.sim_card_id = s.id  "
			+ " WHERE "
			+ " e.id = ? "
			+ " AND gm.movil_id = m.id "
			+ " AND gm.gps_imei = g.imei AND sg.sim_card_id = s.id "
			+ " AND gm.fecha_desinstalacion IS NULL "			
			+ " AND m.tipo_motor_id = tm.id "
			+ " AND m.sub_tipo_movil_id = tmv.id "
			+ " AND m.empresa_id = e.id "
			+ " AND tmov.id = tmv.tipo_movil_id "
			+ " AND m.fecha_baja IS NULL {WHERE_CLAUSE} ";
        
        public static final String queryCompanyUnsuscribeUnitRslite = "SELECT m.id , m.nombre AS name,m.patente AS plateNumber,"
			+ " m.fecha_creacion AS createDate,"
			+ " m.fecha_validado AS validateDate,m.fecha_baja AS dischargeDate, "
			+ " m.validado AS validate,m.vin AS vin,"
			+ " m.ingresado_por AS inputBy,m.empresa_id AS companyId,m.sub_tipo_movil_id AS subVehicleTypeId,m.tipo_motor_id AS engineTypeId,"
			+ " m.atributos_extra AS extraFields,"
			+ " tm.nombre AS engineTypeName, tmv.nombre AS subVehicleTypeName,"
			+ " e.nombre AS companyName ,e.id AS idCompany, tmov.nombre AS vehicleTypeName, g.imei AS _m, s.numero_telefono AS simCardPhone, "
                        + " tdg.nombre as deviceTypeName , g.tipo_dispositivo_gps_id AS deviceTypeId, "
			+ " ? AS total, ? AS realm "
			+ " FROM movil AS m , tipo_motor tm, sub_tipo_movil tmv, tipo_dispositivo_gps as tdg, empresa e, tipo_movil tmov,gps AS g, gps_movil AS gm "
			+ " LEFT JOIN sim_card_gps sg ON gm.gps_imei = sg.gps_imei LEFT JOIN sim_card s ON sg.sim_card_id = s.id "
			+ " WHERE "
			+ " e.id = ? "
			+ " AND gm.movil_id = m.id "
			+ " AND gm.gps_imei = g.imei "
                        + " AND tdg.id = g.tipo_dispositivo_gps_id "			
			+ " AND m.tipo_motor_id = tm.id "
			+ " AND m.sub_tipo_movil_id = tmv.id "
			+ " AND m.empresa_id = e.id "
			+ " AND tmov.id = tmv.tipo_movil_id "
			+ " AND m.fecha_baja IS NOT NULL {WHERE_CLAUSE} ";
        
        public static final String queryCompanyUnsuscribeUnitCountRslite = "Select  Count(m.id) "
                        + " FROM movil AS m , tipo_motor tm, sub_tipo_movil tmv, tipo_dispositivo_gps as tdg, empresa e, tipo_movil tmov,gps AS g, gps_movil AS gm "
                        + " LEFT JOIN sim_card_gps sg ON gm.gps_imei = sg.gps_imei "
                        + " LEFT JOIN sim_card s ON sg.sim_card_id = s.id "
                        + " WHERE e.id = ? "
                        + " AND gm.movil_id = m.id "
                        + " AND gm.gps_imei = g.imei "
                        + " AND tdg.id = g.tipo_dispositivo_gps_id "
                        + " AND m.tipo_motor_id = tm.id "
                        + " AND m.sub_tipo_movil_id = tmv.id "
                        + " AND m.empresa_id = e.id "
                        + " AND tmov.id = tmv.tipo_movil_id "
                        + " AND m.fecha_baja IS NOT NULL {WHERE_CLAUSE} ";
	
		 
	//ESTO esta  mal
	public static final String queryMetadataFleetVehicleByUserRslite = " SELECT "
			+ " m.id,m.vin,m.ultima_velocidad AS lastSpeed,m.odometro AS odometer,m.horometro AS hourmeter,m.patente AS plateNumber,"
			+ " m.nombre AS name,m.fecha_ultima_actividad AS lastActivityDate,m.ultima_latitud AS latitude,m.ultima_longitud AS longitude, "
			+ " m.ultimo_evento AS lastEventId,m.ultimo_rumbo AS course,m.ultimo_geotexto AS geotext,m.atributos_extra AS freeText,"
			+ " g.imei AS _m, ? AS total, ? AS realm "
			+ " FROM movil AS m "
			+ " INNER JOIN movil_flota AS mf ON mf.movil_id = m.id "
			+ " INNER JOIN gps_movil AS gm ON gm.movil_id =m.id INNER JOIN gps AS g ON g.imei = gm.gps_imei "
			+ " WHERE m.validado = 1 "
			+ " AND ultima_latitud is not null AND ultima_longitud is not null AND "
			+ " mf.flota_id = ? AND m.fecha_validado IS NOT NULL AND m.fecha_baja IS NULL {WHERE_CLAUSE} ";

	public static final String queryMetadataFleetVehicleByUserCountRslite = " SELECT count(m.id) "
			+ " FROM movil AS m INNER JOIN movil_flota AS mf ON mf.movil_id = m.id "
			+ " WHERE "
			+ " ultima_latitud is not null and ultima_longitud is not null AND "
			+ " mf.flota_id = ? AND "
			+ " m.validado = 1 AND "
			+ " m.fecha_validado IS NOT NULL AND "
			+ " m.fecha_baja IS NULL {WHERE_CLAUSE} ";

	public static final String queryVehicleTypeRslite =" SELECT id,nombre AS name FROM tipo_movil";
	public static final String queryEngineTypeRslite =" SELECT id, nombre AS name FROM tipo_motor ";
	public static final String querySubVehicleTypeRslite ="SELECT id, nombre AS name , tipo_movil_id AS vehicleTipeId FROM sub_tipo_movil ";
}
