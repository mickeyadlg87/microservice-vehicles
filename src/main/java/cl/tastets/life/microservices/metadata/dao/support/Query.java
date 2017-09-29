package cl.tastets.life.microservices.metadata.dao.support;

public class Query {

	public static final String queryDeviceEventIdByIdCompanyRS = "SELECT gps019_definicion_evento.gps019_id_evento_device_event AS deviceEventId"
			+ "  FROM gps021_programacion_equipo"
			+ " INNER JOIN gps019_definicion_evento ON gps021_programacion_equipo.gps019_id_definicion_evento = gps019_definicion_evento.gps019_id_definicion_evento"
			+ " INNER JOIN gps008_movil ON gps021_programacion_equipo.gps008_id_movil = gps008_movil.gps008_id_movil"
			+ " WHERE gps008_movil.gps001_id_empresa = ?"
			+ " AND gps008_movil.gps023_id_status = 1"
			+ " AND gps019_definicion_evento.gps019_id_evento_device_event IS NOT NULL"
			+ " GROUP BY gps021_programacion_equipo.gps019_id_definicion_evento"
			+ " ORDER BY gps019_definicion_evento.gps019_id_definicion_evento";

	public static final String queryGroupsMetadataByModemIdRslite = "SELECT f.id id_grupo, " //TODO Cambiar nombre de campos a standar camelCase
			+ "	f.nombre nombre_grupo, "
			+ "	e.rut rut_empresa, "
			+ "	e.nombre nombre_empresa "
			+ " FROM rsLite3.gps g, rsLite3.gps_movil gm, rsLite3.movil m, "
			+ "			rsLite3.movil_flota mf, rsLite3.flota f, rsLite3.empresa e"
			+ " WHERE g.imei = gm.gps_imei "
			+ "			and gm.movil_id = m.id"
			+ "			and m.id = mf.movil_id"
			+ "			and mf.flota_id = f.id"
			+ "			and m.empresa_id = e.id"
			+ "			and g.imei = ?";

	public static final String queryGroupsMetadataByModemIdRastreosat = "SELECT distinct"
			+ "			gps004_grupo_movil.gps004_id_grupo_movil id_grupo,"
			+ "			gps004_grupo_movil.gps004_nombre_grupo nombre_grupo,"
			+ "			gps001_empresa.gps001_rut rut_empresa,"
			+ "			gps001_empresa.gps001_nombre_empresa nombre_empresa"
			+ " FROM gps013_gps"
			+ "			INNER JOIN gps014_gps_movil ON gps013_gps.gps013_id_gps = gps014_gps_movil.gps013_id_gps"
			+ "			INNER JOIN gps005_moviles_grupo ON gps014_gps_movil.gps008_id_movil = gps005_moviles_grupo.gps008_id_movil"
			+ "			INNER JOIN gps004_grupo_movil ON gps005_moviles_grupo.gps004_id_grupo_movil = gps004_grupo_movil.gps004_id_grupo_movil"
			+ "			INNER JOIN gps001_empresa ON gps004_grupo_movil.gps001_id_empresa = gps001_empresa.gps001_id_empresa"
			+ "			INNER JOIN gps008_movil ON gps014_gps_movil.gps008_id_movil = gps008_movil.gps008_id_movil"
			+ " WHERE gps008_movil.gps023_id_status = 1"
			+ "			AND gps001_empresa.gps001_status = 1"
			+ "			AND gps013_gps.gps013_modemid = ?";

	public static final String queryCompanyMetadataRs = "SELECT gps001_empresa.gps001_nombre_empresa AS company,"
			+ " 		  gps001_empresa.gps001_id_empresa  AS    idCompany,"
			+ "         gps008_movil.gps008_id_movil        AS       idVehicle,"
			+ "         gps008_movil.gps008_patente         AS       plateNumber,"
			+ "         gps008_movil.gps008_nombre_movil_industria AS name,"
			+ "         gps008_movil.gps023_id_status       AS       status,"
			+ "         gps013_gps.gps013_modemid           AS       _m,"
			+ "         gps013_gps.gps013_imei              AS       imei,"
			+ "         gps013_gps.gps013_simcard           AS       simcard ," + "		  ? 							AS			 realm "
			+ "  FROM gps001_empresa "
			+ "         INNER JOIN gps008_movil ON gps001_empresa.gps001_id_empresa = gps008_movil.gps001_id_empresa"
			+ "         INNER JOIN gps014_gps_movil ON gps008_movil.gps008_id_movil = gps014_gps_movil.gps008_id_movil"
			+ "         INNER JOIN gps013_gps ON gps014_gps_movil.gps013_id_gps = gps013_gps.gps013_id_gps "
			+ "WHERE gps001_empresa.gps001_id_empresa = ? AND gps008_movil.gps023_id_status = 1";

	public static final String queryAllRs = "SELECT gps001_empresa.gps001_nombre_empresa  AS     company,"
			+ " 		gps001_empresa.gps001_id_empresa  AS    idCompany,"
			+ "         gps008_movil.gps008_id_movil        AS       idVehicle,"
			+ "         gps008_movil.gps008_patente         AS       plate_number," //TODO Eliminar este campo cuando los demas actualicen
			+ "         gps008_movil.gps008_nombre_movil_industria AS name,"
			+ "         gps008_movil.gps023_id_status       AS       status,"
			+ "         gps013_gps.gps013_modemid           AS       _m,"
			+ "         gps013_gps.gps013_imei              AS       imei,"
			+ "         gps013_gps.gps013_simcard           AS       simcard ,"
			+ "		  ? AS realm "
			+ "  FROM gps001_empresa "
			+ "         INNER JOIN gps008_movil ON gps001_empresa.gps001_id_empresa = gps008_movil.gps001_id_empresa"
			+ "         INNER JOIN gps014_gps_movil ON gps008_movil.gps008_id_movil = gps014_gps_movil.gps008_id_movil"
			+ "         INNER JOIN gps013_gps ON gps014_gps_movil.gps013_id_gps = gps013_gps.gps013_id_gps "
			+ "WHERE gps008_movil.gps023_id_status = 1";

	public static final String queryAllByRealmWithFleetRastreosat = "SELECT g1.gps001_nombre_empresa AS company,"+
											"        g1.gps001_id_empresa  AS    idCompany, "+
											"        g1.gps001_rut  AS  rut_Company,"+
											"gps008_movil.gps008_id_movil        AS       vehicleId, "+
											"gps008_movil.gps008_patente  AS plate_number,"+
											"gps008_movil.gps008_patente  AS plateNumber,"+
											"gps008_movil.gps008_tipo_vehiculo  AS vehicleTypeName, "+
											"gps008_movil.gps008_nombre_movil_industria name,"+
											"'' AS vin ,"+
											"gps008_movil.gps023_id_status  AS status,"+
											"gps013_gps.gps013_modemid AS _m,"+
											"gps013_gps.gps013_imei  AS imei,"+
											"gps013_gps.gps013_simcard           AS       simcard , "+
											"'' AS validateDate,"+
											"'1' AS engineTypeId,"+
											"'Default' AS    subVehicleTypeName,"+
											"'null' AS dischargeDate,"+
											"'1' AS subVehicleTypeId,"+
											"'' AS createDate,"+
											"'null' AS extraFields,"+
											"CONCAT('[',GROUP_CONCAT(CONCAT('{\"id_grupo\":',g4.gps004_id_grupo_movil,',\"nombre_grupo\":\"',g4.gps004_descripcion,'\",\"rut_empresa\":\"', "+
											"g1.gps001_rut,'\",\"nombre_empresa\":\"',g1.gps001_nombre_empresa,'\"}')),']') as fleets, "+
											"? AS realm "+
											"FROM gps001_empresa g1 "+
											"INNER JOIN gps008_movil ON g1.gps001_id_empresa = gps008_movil.gps001_id_empresa "+
											"INNER JOIN gps014_gps_movil ON gps008_movil.gps008_id_movil = gps014_gps_movil.gps008_id_movil "+
											"INNER JOIN gps013_gps ON gps014_gps_movil.gps013_id_gps = gps013_gps.gps013_id_gps "+
											"INNER JOIN gps005_moviles_grupo g5 on g5.gps008_id_movil = gps008_movil.gps008_id_movil "+
											"INNER JOIN gps004_grupo_movil g4 on g5.gps004_id_grupo_movil=g4.gps004_id_grupo_movil "+
											"WHERE gps008_movil.gps023_id_status = 1 "+
											"group by company,idCompany,vehicleId,plate_number,name, status,_m,imei,simcard";

	public static final String queryAllByRealmWithFleetRslite = "SELECT e.nombre  company, "+
																	"e.id idCompany, "+
																	"e.rut  AS  rut_Company,"+
																	"m.id  AS vehicleId,"+
																	"m.patente AS plate_number,"+
																	"m.patente AS plateNumber, "+
																	"tmov.nombre AS vehicleTypeName, "+
																	"m.nombre  AS name,"+
																	"m.vin AS vin, "+
																	"m.validado AS status,"+
																	"gm.gps_imei AS _m,"+
																	"gm.gps_imei AS imei,"+
																	"m.atributos_extra AS extraFields,  "+
																	"s.numero_telefono  AS  simcard, "+
																	"m.tipo_motor_id AS engineTypeId , "+
																	"m.sub_tipo_movil_id AS subVehicleTypeId,"+
																	"tm.nombre AS engineTypeName, "+
																	"tmv.nombre AS subVehicleTypeName,"+
																	"(select  "+
																	"(CONCAT('[',GROUP_CONCAT(CONCAT('{\"id_grupo\":',f.id,',\"nombre_grupo\":\"',f.nombre,'\",\"rut_empresa\":\"',"+
																	"em.id,'\",\"nombre_empresa\":\"',em.nombre,'\"}')"+
																	")  ,']')) "+
																	"from movil_flota mf  "+
																	"    INNER JOIN flota f on mf.flota_id = f.id "+
																	"    INNER JOIN empresa em on em.id = f.empresa_id  "+
																	"    where mf.movil_id = m.id "+
																	") as fleets "+
																	", ? AS realm  "+
																	"FROM movil m  "+
																	"    INNER JOIN empresa e ON m.empresa_id = e.id  "+
																	"    INNER JOIN gps_movil gm ON m.id = gm.movil_id  "+
																	"    LEFT JOIN sim_card_gps sg ON gm.gps_imei = sg.gps_imei  "+
																	"    LEFT JOIN sim_card s ON sg.sim_card_id = s.id,  tipo_motor tm, sub_tipo_movil tmv,tipo_movil tmov  "+
																	"WHERE m.fecha_validado IS NOT NULL AND m.fecha_baja IS NULL "+
																	"AND m.sub_tipo_movil_id = tmv.id "+
																	"AND m.tipo_motor_id = tm.id "+
																	"AND tmov.id = tmv.tipo_movil_id "+
																	"group by company,idCompany,vehicleId,plate_number,name, status,_m,simcard";

	public static final String queryCompanyMetadataRslite = "SELECT e.nombre AS company, e.id AS idCompany,"
			+ " m.id AS vehicleId, m.patente AS plate_number, m.patente AS plateNumber, "
			+ " m.validado AS status, m.nombre AS name,m.vin AS vin, m.atributos_extra AS extraFields,"
			+ " gm.gps_imei AS _m, s.numero_telefono AS simcard,"
			+ " tm.nombre AS engineTypeName, tmv.nombre AS subVehicleTypeName,tmov.nombre AS vehicleTypeName, "
			+ " gp.tipo_dispositivo_gps_id as deviceTypeId, tdg.nombre as deviceTypeName, "
			+ " ? AS realm "
			+ " FROM movil m INNER JOIN empresa e ON m.empresa_id = e.id INNER JOIN gps_movil gm ON m.id = gm.movil_id "
			+ " LEFT JOIN sim_card_gps sg ON gm.gps_imei = sg.gps_imei LEFT JOIN sim_card s ON sg.sim_card_id = s.id ,"
			+ " tipo_motor tm, sub_tipo_movil tmv,tipo_movil tmov, gps gp, tipo_dispositivo_gps tdg "
			+ " WHERE e.id = ? AND m.fecha_validado IS NOT NULL AND m.fecha_baja IS NULL AND "
			+ " m.tipo_motor_id = tm.id AND m.sub_tipo_movil_id = tmv.id AND tmov.id = tmv.tipo_movil_id "
			+ " AND gp.imei = gm.gps_imei AND tdg.id = gp.tipo_dispositivo_gps_id ";

	public static final String queryAllRslite = "SELECT e.nombre  company, e.id idCompany,"
			+ " m.id  AS vehicleId, m.patente AS plate_number,m.patente AS plateNumber, m.atributos_extra AS extraFields, "
			+ " m.validado AS status,m.nombre  AS name,m.vin AS vin,"
			+ " gm.gps_imei AS _m, s.numero_telefono  AS  simcard,  ? AS realm "
			+ "FROM movil m " + "INNER JOIN empresa e ON m.empresa_id = e.id " + "INNER JOIN gps_movil gm ON m.id = gm.movil_id "
			+ "LEFT JOIN sim_card_gps sg ON gm.gps_imei = sg.gps_imei " + "LEFT JOIN sim_card s ON sg.sim_card_id = s.id "
			+ "WHERE m.fecha_validado IS NOT NULL AND m.fecha_baja IS NULL";

        public static final String queryUnitsForCertificateRsLite = "Select m.id, m.nombre as name, g.imei as _m, g.fecha_validado as validateDeviceDate, m.patente as plateNumber, m.fecha_ultima_actividad as lastActivityDate, m.fecha_creacion as creationDate, m.ultimo_geotexto as lastGeotext, "
                        + "m.odometro as odometer, m.horometro as hourmeter, g.tipo_dispositivo_gps_id as deviceTypeId, td.nombre as deviceTypeName, ? AS realm "
                        + "From movil m, gps_movil gm, gps g, tipo_dispositivo_gps td "
                        + "Where m.id = gm.movil_id "
                        + "and gm.gps_imei = g.imei "
                        + "and td.id = g.tipo_dispositivo_gps_id "
                        + "and g.fecha_validado is not null "
                        + "and m.fecha_baja is null "
                        + "and g.categoria = 1 {WHERE_CLAUSE}";

	public static final String queryFleetMetadataRs ="SELECT "
			+ "gps004_grupo_movil.gps001_id_empresa AS companyId, "
			+ "gps004_grupo_movil.gps004_id_grupo_movil AS fleetId, "
			+ "gps004_grupo_movil.gps004_nombre_grupo AS fleetName, "
			+ "gps008_movil.gps008_id_movil AS vehicleId,"
			+ "gps008_movil.gps008_patente AS plateNumber, "
			+ "gps008_movil.gps008_tipo_vehiculo AS vehicleTypeName, "
			+ "gps008_movil.gps008_nombre_movil_industria AS name, "
			+ "'' AS vin, "
			+ "gps013_gps.gps013_modemid _m, "
			+ "gps013_gps.gps013_imei imei, "
			+ "gps013_gps.gps013_simcard simcard, "
			+ "? AS realm "
			+ "FROM "
			+ "gps004_grupo_movil "
			+ "INNER JOIN gps005_moviles_grupo ON gps004_grupo_movil.gps004_id_grupo_movil = gps005_moviles_grupo.gps004_id_grupo_movil "
			+ "INNER JOIN gps008_movil ON gps005_moviles_grupo.gps008_id_movil = gps008_movil.gps008_id_movil "
			+ "INNER JOIN gps014_gps_movil ON gps008_movil.gps008_id_movil = gps014_gps_movil.gps008_id_movil "
			+ "INNER JOIN gps013_gps ON gps014_gps_movil.gps013_id_gps = gps013_gps.gps013_id_gps " + "WHERE "
			+ "gps004_grupo_movil.gps001_id_empresa = ? AND " + "gps004_grupo_movil.gps004_id_grupo_movil = ? AND "
			+ "gps004_grupo_movil.gps004_status = 1 AND " + "gps008_movil.gps023_id_status = 1 ";

	/**
	 * Moviles que veo union a los que me compartieron
	 */
	public static final String queryFleetMetadataRslite = "SELECT m.empresa_id AS companyId, "
			+ "       mf.flota_id AS fleetId, "
			+ "       f.nombre AS fleetName, "
			+ "       m.id AS vehicleId, "
			+ "       m.patente AS plateNumber, "
			+ "       m.nombre AS name, "
			+ "		m.vin AS vin, "
			+ "       g.gps_imei AS _m, "
			+ "       g.gps_imei AS imei,  "
			+ "       sm.numero_telefono AS simcard,m.fecha_creacion AS createDate,m.fecha_validado AS validateDate,m.fecha_baja AS dischargeDate "
			+ "        ,m.atributos_extra AS extraFields , ? AS realm, gp.tipo_dispositivo_gps_id as deviceTypeId, tdg.nombre as deviceTypeName  "
			+ "   FROM flota f "
			+ "          INNER JOIN movil_flota mf ON f.id = mf.flota_id "
			+ "          INNER JOIN movil m ON mf.movil_id = m.id "
			+ "          INNER JOIN gps_movil g ON g.movil_id = m.id  "
			+ "          INNER JOIN gps gp ON gp.imei = g.gps_imei  "
			+ "          INNER JOIN tipo_dispositivo_gps tdg ON tdg.id = gp.tipo_dispositivo_gps_id  "
			+ "           LEFT JOIN sim_card_gps scg ON scg.gps_imei = g.gps_imei  "
			+ "           LEFT JOIN sim_card sm ON sm.id = scg.sim_card_id  "
			+ "  WHERE f.empresa_id = ?  "
			+ "    AND mf.flota_id = ?  "
			+ "    AND m.validado = 1  "
			+ "    AND m.fecha_validado IS NOT NULL  "
			+ "    AND m.fecha_baja IS NULL  "
			+ "  UNION  "
			+ " SELECT m.empresa_id AS companyId, "
			+ "        fem.flota_id AS fleetId, "
			+ "        f.nombre AS fleetName, "
			+ "        m.id AS vehicleId, "
			+ "        m.patente AS plataNumber, "
			+ "        m.nombre AS name, "
			+ "		m.vin AS vin, "
			+ "        g.gps_imei AS _m, "
			+ "        g.gps_imei AS imei, "
			+ "        sm.numero_telefono AS simcard,m.fecha_creacion AS createDate,m.fecha_validado AS validateDate,m.fecha_baja AS dischargeDate "
			+ "        ,m.atributos_extra AS extraFields , ? AS realm, gp.tipo_dispositivo_gps_id as deviceTypeId, tdg.nombre as deviceTypeName  "
			+ "   FROM flota_empresa_movil fem "
			+ "          INNER JOIN movil m ON fem.movil_id = m.id  "
			+ "          INNER JOIN flota f ON f.id = fem.flota_id "
			+ "          INNER JOIN gps_movil g ON g.movil_id = m.id "
			+ "          INNER JOIN gps gp ON gp.imei = g.gps_imei  "
			+ "          INNER JOIN tipo_dispositivo_gps tdg ON tdg.id = gp.tipo_dispositivo_gps_id  "
			+ "           LEFT JOIN sim_card_gps scg ON scg.gps_imei = g.gps_imei  "
			+ "           LEFT JOIN sim_card sm ON sm.id = scg.sim_card_id  "
			+ "  WHERE fem.empresa_id= ?  "
			+ "    AND fem.flota_id = ?  "
			+ "    AND m.validado = 1  "
			+ "    AND m.fecha_validado IS NOT NULL  "
			+ "    AND m.fecha_baja IS NULL";

	public static final String queryVehicleByIdRslite = "SELECT m.id , m.nombre AS name, m.patente AS plate_number,"
			+ " m.patente AS plateNumber,"
			+ " m.fecha_creacion AS createDate,m.fecha_validado AS validateDate,m.fecha_baja AS dischargeDate, "
			+ " gm.gps_imei AS _m,m.validado AS status,"
			+ " m.atributos_extra AS extraFields,m.empresa_id AS companyId,"
			+ " m.vin AS vin,m.sub_tipo_movil_id AS subVehicleTypeId,"
			+ " m.tipo_motor_id AS engineTypeId ,"
			+ " tm.nombre AS engineTypeName, tmv.nombre AS subVehicleTypeName,tmov.nombre AS vehicleTypeName,s.numero_telefono AS  simcard "
			+ " FROM movil m INNER JOIN gps_movil gm ON m.id = gm.movil_id "
			+ " LEFT JOIN sim_card_gps sg ON gm.gps_imei = sg.gps_imei LEFT JOIN sim_card s ON sg.sim_card_id = s.id "
			+ " ,tipo_motor tm, sub_tipo_movil tmv,tipo_movil tmov "
			+ " WHERE m.id = ? "
			+ " AND m.fecha_validado is not null "
			+ " AND m.validado = 1 "
			+ " AND m.fecha_baja IS NULL "
			+ " AND m.tipo_motor_id = tm.id AND m.sub_tipo_movil_id = tmv.id AND tmov.id = tmv.tipo_movil_id";

	public static final String queryVehicleByIdRastreosat = "SELECT gps008_movil.gps008_id_movil AS id, "
			+ "gps008_movil.gps008_nombre_movil_industria AS name, "
			+ "gps008_movil.gps008_patente AS plate_number, "
			+ "0 AS horometro ,"
			+ "gps013_gps.gps013_imei,"
			+ "gps013_gps.gps013_modemid AS _m "
			+ "FROM gps008_movil "
			+ "INNER JOIN gps014_gps_movil ON gps008_movil.gps008_id_movil = gps014_gps_movil.gps008_id_movil "
			+ "INNER JOIN gps013_gps ON gps014_gps_movil.gps013_id_gps = gps013_gps.gps013_id_gps "
			+ "WHERE gps008_movil.gps008_id_movil = ? ";

	public static final String queryVehicleByTagRslite = "SELECT m.id AS vehicleId, m.nombre AS name, m.patente as plate_number,"
			+ " m.patente as plateNumber ,m.empresa_id as companyId, m.horometro AS hourmeter, gm.gps_imei AS _m , "
			+ " m.fecha_creacion AS createDate,m.fecha_validado AS validateDate,m.fecha_baja AS dischargeDate,"
			+ " m.validado AS status,m.vin AS vin,m.atributos_extra AS extraFields "
			+ " FROM movil m INNER JOIN gps_movil gm ON m.id = gm.movil_id  "
			+ " WHERE m.patente = ? "
			+ " AND m.fecha_validado is not null "
			+ " AND m.validado = 1 "
			+ " AND m.fecha_baja IS NULL";

	public static final String queryVehicleByTagRastreosat = "SELECT gps008_movil.gps008_id_movil as id, "
			+ "gps008_movil.gps008_nombre_movil_industria as name, "
			+ "gps008_movil.gps008_patente as plate_number, "
			+ "gps001_id_empresa as companyId, "
			+ "0 as horometro, "
			+ "gps013_gps.gps013_imei,"
			+ "gps013_gps.gps013_modemid AS _m "
			+ "FROM gps008_movil "
			+ "INNER JOIN gps014_gps_movil ON gps008_movil.gps008_id_movil = gps014_gps_movil.gps008_id_movil "
			+ "INNER JOIN gps013_gps ON gps014_gps_movil.gps013_id_gps = gps013_gps.gps013_id_gps "
			+ "WHERE gps008_movil.gps008_patente = ? ";

	public static final String queryVehicleByImeiRslite = "SELECT e.nombre AS company,e.id AS idCompany,e.rut AS rut_Company,"
			+ " m.id AS vehicleId, m.patente AS plate_number, m.patente AS plateNumber,"
			+ " m.fecha_creacion AS createDate,m.fecha_validado AS validateDate,m.fecha_baja AS dischargeDate,"
			+ " m.validado AS status,m.nombre AS name,m.vin AS vin,"
			+ " gm.gps_imei AS _m,s.numero_telefono AS  simcard,m.atributos_extra AS extraFields,"
			+ " m.tipo_motor_id AS engineTypeId , m.sub_tipo_movil_id AS subVehicleTypeId,tm.nombre AS engineTypeName, "
			+ " gp.tipo_dispositivo_gps_id as deviceTypeId, tdg.nombre as deviceTypeName, "
			+ " tmv.nombre AS subVehicleTypeName,tmov.nombre AS vehicleTypeName, ? AS realm "
			+ " FROM movil m INNER JOIN empresa e ON m.empresa_id = e.id INNER JOIN gps_movil gm ON m.id = gm.movil_id "
			+ " LEFT JOIN sim_card_gps sg ON gm.gps_imei = sg.gps_imei LEFT JOIN sim_card s ON sg.sim_card_id = s.id ,"
			+ " tipo_motor tm, sub_tipo_movil tmv,tipo_movil tmov, gps gp, tipo_dispositivo_gps tdg  "
			+ " WHERE gm.gps_imei = ? AND gm.fecha_desinstalacion IS null AND m.fecha_validado IS NOT NULL AND m.fecha_baja IS NULL "
			+ " AND m.sub_tipo_movil_id = tmv.id AND m.tipo_motor_id = tm.id AND tmov.id = tmv.tipo_movil_id"
			+ " AND gp.imei = gm.gps_imei AND tdg.id = gp.tipo_dispositivo_gps_id ";

	public static final String queryVehicleByImeiRs = "SELECT gps001_empresa.gps001_nombre_empresa AS company,"
			+ " gps001_empresa.gps001_id_empresa AS idCompany,"
			+ " gps001_empresa.gps001_rut  AS  rut_Company,"
			+ " gps008_movil.gps008_id_movil  AS vehicleId,"
			+ " gps008_movil.gps008_patente  AS plate_number,"
			+ " gps008_movil.gps008_patente  AS plateNumber,"
			+ " gps008_movil.gps008_tipo_vehiculo  AS vehicleTypeName, "
			+ " gps008_movil.gps008_nombre_movil_industria name,"
			+ " '' AS vin ,"
			+ " gps008_movil.gps023_id_status  AS status,"
			+ " gps013_gps.gps013_modemid AS _m,"
			+ " gps013_gps.gps013_imei  AS imei,"
			+ " gps013_gps.gps013_simcard  AS simcard, ? AS realm "
			+ "  FROM gps001_empresa "
			+ " INNER JOIN gps008_movil ON gps001_empresa.gps001_id_empresa = gps008_movil.gps001_id_empresa"
			+ " INNER JOIN gps014_gps_movil ON gps008_movil.gps008_id_movil = gps014_gps_movil.gps008_id_movil"
			+ " INNER JOIN gps013_gps ON gps014_gps_movil.gps013_id_gps = gps013_gps.gps013_id_gps "
			+ " WHERE gps008_movil.gps023_id_status = 1 AND gps013_gps.gps013_modemid = ?";

	public static final String queryVehicleByUserAdminRslite =
			"SELECT"
			+ " e2.nombre AS company,"
			+ " e2.id AS idCompany,"
			+ " e2.rut AS rut_Company,"
			+ " m2.id AS vehicleId,"
			+ " m2.vin AS vin,"
			+ " m2.patente AS plateNumber,"
			+ " m2.patente AS plate_number,"
			+ " m2.nombre AS name,"
			+ " m2.tipo_motor_id AS engineTypeId,"
			+ " m2.sub_tipo_movil_id AS subVehicleTypeId,"
			+ " m2.fecha_creacion AS createDate,"
			+ " m2.fecha_validado AS validateDate,"
			+ " m2.fecha_baja AS dischargeDate,"
			+ " m2.validado AS status,"
			+ " m2.atributos_extra AS extraFields,"
			+ " g2.imei AS _m,"
			+ " g2.tipo_dispositivo_gps_id AS deviceTypeId,"
			+ " s2.numero_telefono AS simcard,"
			+ " tm2.nombre AS engineTypeName,"
			+ " tdg2.nombre AS deviceTypeName,"
			+ " tmv2.nombre AS subVehicleTypeName,"
			+ " 'sin informacion' AS vehicleTypeName,"
			+ " ? AS realm"
			+ " FROM movil m2"
			+ " INNER JOIN gps_movil AS gm2 ON gm2.movil_id = m2.id"
			+ " INNER JOIN gps AS g2 ON g2.imei = gm2.gps_imei"
			+ " INNER JOIN empresa e2 ON e2.id = m2.empresa_id"
			+ " INNER JOIN sim_card_gps AS sg2 ON sg2.gps_imei = gm2.gps_imei"
			+ " INNER JOIN sim_card AS s2 ON s2.id = sg2.sim_card_id"
			+ " INNER JOIN tipo_motor AS tm2 ON tm2.id = m2.tipo_motor_id"
			+ " INNER JOIN tipo_dispositivo_gps AS tdg2 ON tdg2.id = g2.tipo_dispositivo_gps_id"
			+ " INNER JOIN sub_tipo_movil AS tmv2 ON tmv2.id = m2.sub_tipo_movil_id"
			+ " WHERE  m2.validado = 1"
			+ " AND m2.ultima_latitud IS NOT NULL"
			+ " AND m2.ultima_longitud IS NOT NULL"
			+ " AND m2.fecha_validado IS NOT NULL"
			+ " AND m2.fecha_baja IS NULL"
			+ " AND m2.empresa_id = ?"
			+ " UNION"
			+ " SELECT"
			+ " e.nombre AS company,"
			+ " e.id AS idCompany,"
			+ " e.rut AS rut_Company,"
			+ " m.id AS vehicleId,"
			+ " m.vin AS vin,"
			+ " m.patente AS plateNumber,"
			+ " m.patente AS plate_number,"
			+ " m.nombre AS name,"
			+ " m.tipo_motor_id AS engineTypeId,"
			+ " m.sub_tipo_movil_id AS subVehicleTypeId,"
			+ " m.fecha_creacion AS createDate,"
			+ " m.fecha_validado AS validateDate,"
			+ " m.fecha_baja AS dischargeDate,"
			+ " m.validado AS status,"
			+ " m.atributos_extra AS extraFields,"
			+ " g.imei AS _m,"
			+ " g.tipo_dispositivo_gps_id AS deviceTypeId,"
			+ " s.numero_telefono AS simcard,"
			+ " tm.nombre AS engineTypeName,"
			+ " tdg.nombre AS deviceTypeName,"
			+ " tmv.nombre AS subVehicleTypeName,"
			+ " 'sin informacion' AS vehicleTypeName,"
			+ " ? AS realm"
			+ " FROM movil m"
			+ " INNER JOIN gps_movil AS gm ON gm.movil_id = m.id"
			+ " INNER JOIN gps AS g ON g.imei = gm.gps_imei"
			+ " INNER JOIN empresa e ON e.id = m.empresa_id"
			+ " INNER JOIN sim_card_gps AS sg ON sg.gps_imei = gm.gps_imei"
			+ " INNER JOIN sim_card AS s ON s.id = sg.sim_card_id"
			+ " INNER JOIN tipo_motor AS tm ON tm.id = m.tipo_motor_id"
			+ " INNER JOIN tipo_dispositivo_gps AS tdg ON tdg.id = g.tipo_dispositivo_gps_id"
			+ " INNER JOIN sub_tipo_movil AS tmv ON tmv.id = m.sub_tipo_movil_id"
			+ " WHERE  m.validado = 1"
			+ " AND m.ultima_latitud IS NOT NULL"
			+ " AND m.ultima_longitud IS NOT NULL"
			+ " AND m.fecha_validado IS NOT NULL"
			+ " AND m.fecha_baja IS NULL"
			+ " AND m.id IN ("
			+ " SELECT DISTINCT movil_id"
			+ " FROM flota_empresa_movil fem, flota f"
			+ " WHERE f.id = fem.flota_id"
			+ " AND fem.empresa_id = ?"
			+ "  )";

	public static final String queryVehicleByUserNotAdminRslite =

			"SELECT"
			+ " e.nombre AS company,"
			+ " e.id AS idCompany,"
			+ " e.rut AS rut_Company,"
			+ " m.id AS vehicleId,"
			+ " m.vin AS vin,"
			+ " m.patente AS plateNumber,"
			+ " m.patente AS plate_number,"
			+ " m.nombre AS name,"
			+ " m.tipo_motor_id AS engineTypeId,"
			+ " m.sub_tipo_movil_id AS subVehicleTypeId,"
			+ " m.fecha_creacion AS createDate,"
			+ " m.fecha_validado AS validateDate,"
			+ " m.fecha_baja AS dischargeDate,"
			+ " m.validado AS status,"
			+ " m.atributos_extra AS extraFields,"
			+ " m.tipo_motor_id AS engineTypeId,"
			+ " m.sub_tipo_movil_id AS subVehicleTypeId,"
			+ " g.imei AS _m,"
			+ " g.tipo_dispositivo_gps_id AS deviceTypeId,"
			+ " s.numero_telefono AS simcard,"
			+ " tm.nombre AS engineTypeName,"
			+ " tdg.nombre AS deviceTypeName,"
			+ " tmv.nombre AS subVehicleTypeName,"
			+ " 'sin informacion' AS vehicleTypeName," // temporal ?
			+ " ? AS realm"
			+ " FROM movil m"
			+ " INNER JOIN gps_movil AS gm ON gm.movil_id = m.id"
			+ " INNER JOIN gps AS g ON g.imei = gm.gps_imei"
			+ " INNER JOIN empresa e ON e.id = m.empresa_id"
			+ " INNER JOIN sim_card_gps AS sg ON sg.gps_imei = gm.gps_imei"
			+ " INNER JOIN sim_card AS s ON s.id = sg.sim_card_id"
			+ " INNER JOIN tipo_motor AS tm ON tm.id = m.tipo_motor_id"
			+ " INNER JOIN tipo_dispositivo_gps AS tdg ON tdg.id = g.tipo_dispositivo_gps_id"
			+ " INNER JOIN sub_tipo_movil AS tmv ON tmv.id = m.sub_tipo_movil_id"
			+ " WHERE"
			+ " m.validado = 1"
			+ " AND m.ultima_latitud IS NOT NULL"
			+ " AND m.ultima_longitud IS NOT NULL"
			+ " AND m.fecha_validado IS NOT NULL"
            + " AND m.fecha_baja IS NULL"
			+ " AND m.id IN ("
			+ " SELECT DISTINCT movil_id"
			+ " FROM movil_flota mf, flota f"
			+ " WHERE"
			+ " f.id = mf.flota_id"
			+ " AND mf.flota_id IN ("
			+ "	SELECT f.id FROM flota AS f, empresa AS e, permisos_usuario_flota AS puf WHERE"
			+ "	puf.usuario_id = ? AND f.id = puf.flota_id AND e.id = f.empresa_id)"
			+ " )";

	public static final String queryVehicleByUserRastreosat = "SELECT DISTINCT " +
			" gps002_usuario.gps002_id_usuario, " +
			" gps005_moviles_grupo.gps008_id_movil AS id, " +
			" gps008_movil.gps008_nombre_movil_industria as name, " +
			" gps008_movil.gps008_patente AS plateNumber, " +
			" gps008_movil.gps008_tipo_vehiculo AS vehicleTypeName, " +
			" gps013_gps.gps013_modemid  AS  _m " +
			" FROM gps002_usuario " +
			" INNER JOIN gps006_usuario_gmovil_perfil ON gps002_usuario.gps002_id_usuario = gps006_usuario_gmovil_perfil.gps002_id_usuario " +
			" INNER JOIN gps005_moviles_grupo ON gps006_usuario_gmovil_perfil.gps004_id_grupo_movil = gps005_moviles_grupo.gps004_id_grupo_movil " +
			" INNER JOIN gps008_movil ON gps005_moviles_grupo.gps008_id_movil = gps008_movil.gps008_id_movil " +
			" INNER JOIN gps014_gps_movil ON gps014_gps_movil.gps008_id_movil = gps008_movil.gps008_id_movil " +
			" INNER JOIN gps013_gps ON gps013_gps.gps013_id_gps = gps014_gps_movil.gps013_id_gps " +
			" WHERE gps002_usuario.gps002_id_usuario = ? " +
			" AND gps002_usuario.gps001_id_empresa = ? " +
			" AND gps008_movil.gps023_id_status = 1 " +
			" ORDER BY gps005_moviles_grupo.gps008_id_movil";

}
