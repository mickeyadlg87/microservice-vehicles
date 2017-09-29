package cl.tastets.life.microservices.metadata.dao.support;

/**
 *
 * @author gaston
 */
public class QueryFleet {

	//Find by id
	public static final String queryFindFleetByIdRslite = " SELECT f.id, f.nombre AS  name, "
			+ " f.fecha_creacion AS  createDate, f.empresa_id AS companyId, f.flota_default AS defaultFleet, "
			+ " f.compartida AS shared, f.generar_cartola AS generateReport, e.nombre AS companyName, f.velocidad_max AS maxSpeed,"
			+ " f.dia_inicio AS startDay, f.hora_inicio AS startHour, f.dia_fin AS endDay, f.hora_fin AS endHour,f.dias_inactividad AS inactiveDays ,? AS realm "
			+ " FROM flota AS f INNER JOIN  empresa AS e ON e.id = f.empresa_id WHERE f.id = ? ";

	public static final String queryFindUserFleetByIdRslite = " SELECT u.id,u.rut,u.userName,u.nombre AS name,u.apellido AS lastName,u.email,"
			+ " u.empresa_id AS compnayId,u.fecha_creacion AS createDate "
			+ " FROM usuario AS u INNER JOIN permisos_usuario_flota AS puf ON puf.usuario_id = u.id "
			+ " WHERE "
			+ " puf.flota_id = ? ";

	public static final String queryFindVehiclesFleetByIdRslite
			= "  SELECT m.id,m.nombre AS name,m.patente AS plateNumber,"
			+ " m.fecha_creacion AS  createDate,m.fecha_validado AS validationDate,m.fecha_baja AS dischargedDate,"
			+ " m.validado AS status,m.vin,"
			+ " m.ingresado_por AS inputBy,m.empresa_id AS  companyId,m.sub_tipo_movil_id AS subVehicleTypeId,m.tipo_motor_id AS engineTypeId,"
			+  "m.atributos_extra AS extraFields, g.imei AS _m "
			+ " FROM movil AS m INNER JOIN movil_flota mf ON mf.movil_id = m.id INNER JOIN gps_movil gm ON gm.movil_id= m.id INNER JOIN gps g ON gm.gps_imei = g.imei "
			+ " WHERE mf.flota_id = ? AND m.validado = 1 AND m.fecha_validado IS NOT NULL AND m.fecha_baja IS NULL";

	public static final String queryFindCompaniesFleetByIdRslite =
			"SELECT DISTINCT fm.empresa_id AS id,fm.flota_id AS fleetId,e.nombre AS name," +
					" e.direccion AS address,e.razon_social AS socialReason,e.rut, e.telefono_principal AS phone," +
					" e.telefono_secundario AS secondaryPhone,e.max_moviles AS maxVehicles,e.email, e.exceso_velocidad AS speedLimit," +
					" e.max_number_users AS maxNumberUsers, e.reseller_id AS resellerId,e.alias AS alias FROM flota_empresa_movil fm "
					+ " INNER JOIN empresa e on e.id = fm.empresa_id "
					+ " INNER JOIN flota f ON f.id = fm.flota_id "
					+" WHERE f.compartida is true AND f.id = ?";

	//Busqueda cdo el user es admin	
	public static final String queryFleetByUserAdminRslite = " SELECT f.id, f.nombre AS  name, "
			+ " f.fecha_creacion AS  createDate, f.empresa_id AS companyId, f.flota_default AS defaultFleet, "
			+ " f.compartida AS shared, f.generar_cartola AS generateReport, e.nombre AS companyName, f.velocidad_max AS maxSpeed,"
			+ " f.dia_inicio AS startDay, f.hora_inicio AS startHour, f.dia_fin AS endDate, f.hora_fin AS endHour ,f.dias_inactividad AS inactiveDays ,? AS total,? AS realm "
			+ " FROM flota f INNER JOIN empresa e ON e.id = f.empresa_id "
			+ " WHERE f.empresa_id = ? {WHERE_CLAUSE} ";

	public static final String queryFleetByUserAdminCountRslite = " SELECT count(f.id) FROM flota f "
			+ " INNER JOIN empresa e ON e.id = f.empresa_id "
			+ " WHERE f.empresa_id = ? {WHERE_CLAUSE} ";

	public static final String queryFleetByUserNotAdminRslite = " SELECT f.id, f.nombre AS  name, "
			+ " f.fecha_creacion AS  createDate, f.empresa_id AS companyId, f.flota_default AS defaultFleet, "
			+ " f.compartida AS shared, f.generar_cartola AS generateReport, e.nombre AS companyName, f.velocidad_max AS maxSpeed,"
			+ " f.dia_inicio AS startDay, f.hora_inicio AS startHour, f.dia_fin AS endDate, f.hora_fin AS endHour,f.dias_inactividad AS inactiveDays, ? AS total,? AS realm "
			+ " FROM flota AS f, empresa AS e, permisos_usuario_flota AS puf "
			+ " WHERE f.id = puf.flota_id AND e.id = f.empresa_id AND empresa_id = ? AND puf.usuario_id = ? {WHERE_CLAUSE} ";

	public static final String queryFleetByUserNotAdminCountRslite = "SELECT count(*) "
			+ " FROM flota AS f, empresa AS e, permisos_usuario_flota AS puf "
			+ " WHERE empresa_id = ? AND puf.usuario_id = ?  "
			+ " AND f.id = puf.flota_id "
			+ " AND e.id = f.empresa_id {WHERE_CLAUSE}";

	public static final String queryFleetByUserEntel = "SELECT DISTINCT empresa.gps001_nombre_empresa AS companyName, "
			+ " grupo.gps004_id_grupo_movil AS id,grupo.gps004_nombre_grupo AS  name,grupo.gps004_descripcion AS  description,"
			+ " ? AS total,? AS realm "
			+ " FROM gps004_grupo_movil grupo INNER JOIN gps006_usuario_gmovil_perfil mov_usuario "
			+ " ON grupo.gps004_id_grupo_movil = mov_usuario.gps004_id_grupo_movil  "
			+ " INNER JOIN gps001_empresa empresa on empresa.gps001_id_empresa = grupo.gps001_id_empresa "
			+ " INNER JOIN gps005_moviles_grupo mov_grupo on mov_grupo.gps004_id_grupo_movil = grupo.gps004_id_grupo_movil "
			+ " INNER JOIN gps008_movil movil on movil.gps008_id_movil = mov_grupo.gps008_id_movil and movil.gps023_id_status = 1 "
			+ " where mov_usuario.gps002_id_usuario = ? and grupo.gps004_status = 1 {WHERE_CLAUSE} ";

	public static final String queryFleetByUserCountEntel = "select count(DISTINCT(grupo.gps004_id_grupo_movil)) AS fleetCount "
			+ " FROM gps004_grupo_movil grupo INNER JOIN gps006_usuario_gmovil_perfil mov_usuario "
			+ " ON grupo.gps004_id_grupo_movil = mov_usuario.gps004_id_grupo_movil "
			+ " INNER JOIN gps001_empresa empresa on empresa.gps001_id_empresa = grupo.gps001_id_empresa "
			+ " INNER JOIN gps005_moviles_grupo mov_grupo on mov_grupo.gps004_id_grupo_movil = grupo.gps004_id_grupo_movil "
			+ " INNER JOIN gps008_movil movil on movil.gps008_id_movil = mov_grupo.gps008_id_movil and movil.gps023_id_status = 1 "
			+ " where mov_usuario.gps002_id_usuario = ? and grupo.gps004_status = 1 {WHERE_CLAUSE} ";

	//REEMPLAZAR POR LASTSTATE PARA TODA LA FLOTA
	public static final String queryFleetStatusRslite = "SELECT g.imei AS _m FROM movil AS m INNER JOIN movil_flota AS mf ON mf.movil_id = m.id "
			+ " INNER JOIN gps_movil gm ON gm.movil_id= m.id INNER JOIN gps g ON gm.gps_imei = g.imei WHERE mf.flota_id = ? AND "
			+ " m.fecha_validado IS NOT NULL AND  m.validado = 1 "
			+ " AND  m.fecha_validado IS NOT NULL AND m.fecha_baja IS NULL ";

	public static final String queryFleetStatusEntel = "SELECT gps.gps013_modemid AS _m "
			+ " FROM gps008_movil movil "
			+ " INNER JOIN gps014_gps_movil gps_movil on movil.gps008_id_movil = gps_movil.gps008_id_movil "
			+ " INNER JOIN gps013_gps gps on gps.gps013_id_gps = gps_movil.gps013_id_gps "
			//+ " LEFT OUTER JOIN gps016_ultima_posicion_movil posicion ON movil.gps008_id_movil = posicion.gps008_id_movil "
			+ " WHERE gps023_id_status = 1 AND movil.gps008_id_movil IN (SELECT gps008_id_movil from gps005_moviles_grupo "
			+ " WHERE gps004_id_grupo_movil = ? )";

	//CRUD y Flota default
	public static final String queryFindDefaultFleetRslite = "SELECT f.id FROM flota AS f INNER JOIN empresa AS e ON e.id = f.empresa_id "
			+ "WHERE flota_default = true AND empresa_id = ?";

	public static final String insertRslite = "INSERT INTO flota (nombre, fecha_creacion, empresa_id, flota_default, compartida, generar_cartola, velocidad_max, dia_inicio, hora_inicio, dia_fin, hora_fin, dias_inactividad) "
			+ " VALUES( ?, NOW(), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

	public static final String updateRslite = "UPDATE flota set nombre=?,compartida=?,generar_cartola=?,velocidad_max=?, dia_inicio=?, hora_inicio=?, dia_fin=?, hora_fin=?, dias_inactividad=? "
			+ "WHERE id = ?";

	public static final String deleteRslite = "DELETE FROM flota WHERE id = ?";


	public static final String insertFleetVehiclesRslite = "INSERT INTO movil_flota (flota_id, movil_id) VALUES  {VALUES} ON DUPLICATE KEY UPDATE flota_id = VALUES(flota_id)";

	public static final String insertFleetUsersRslite = "INSERT INTO permisos_usuario_flota (flota_id, usuario_id, permiso) VALUES {VALUES}";

	public static final String insertFleetSharedCompaniesRslite = " INSERT INTO flota_empresa_movil (flota_id, movil_id, empresa_id) "
			+ " SELECT flota_id, movil_id, ? FROM movil_flota WHERE flota_id = ? "
			+ " ON DUPLICATE KEY UPDATE flota_id = ?";

	public static final String removeFleetSharedCompaniesRslite = " DELETE FROM flota_empresa_movil WHERE flota_id = ? ";

	public static final String removeVehiclesFromFleetRslite = "DELETE FROM movil_flota WHERE flota_id = ?";

	public static final String removeUsersFromFleetRslite = "DELETE FROM permisos_usuario_flota WHERE flota_id = ? ";

	public static final String removeFleetReportsRslite = "DELETE FROM reporte WHERE flota_id = ?";
}
