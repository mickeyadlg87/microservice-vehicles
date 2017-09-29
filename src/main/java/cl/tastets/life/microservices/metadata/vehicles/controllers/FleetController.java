package cl.tastets.life.microservices.metadata.vehicles.controllers;

import cl.tastets.life.microservices.metadata.dao.ts.TsMetadataFleetDao;
import cl.tastets.life.objects.Fleet;
import cl.tastets.life.objects.utils.RequestData;
import java.util.List;
import java.util.Optional;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author gaston
 */
@Service
public class FleetController {

	private static final Logger logger = Logger.getLogger("metadataVehicles");
	
	@Autowired
	private TsMetadataFleetDao dao;

	public Fleet getFleetById(String realm, Integer fleetId, Optional<Boolean> withMetadata) throws Exception {
		try {
			return dao.findFleetById(realm, fleetId, withMetadata, "queryFindFleetById");
		} catch (Exception e) {
			logger.error("Error en getFleetById " + e.getMessage(), e);
			throw e;
		}
	}

	public List<Fleet> getMetadataByUser(String realm, RequestData requestData) throws Exception {
		try {
			return dao.findFleetsByUser(realm, Optional.ofNullable(requestData.getFilter()), Optional.ofNullable(requestData.getPaginated()), "queryFleetByUser");
		} catch (Exception e) {
			logger.error("Error en getMetadataByUser " + e.getMessage(), e);
			throw e;
		}
	}

	public Fleet getFleetStatus(String realm, Integer id) throws Exception {
		try {
			return dao.fleetStatus(realm, id, "queryFleetStatus");
		} catch (Exception e) {
			logger.error("Error en getFleetStatus " + e.getMessage(), e);
			throw e;
		}
	}
}
