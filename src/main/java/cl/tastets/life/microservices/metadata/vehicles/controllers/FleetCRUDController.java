package cl.tastets.life.microservices.metadata.vehicles.controllers;

import cl.tastets.life.microservices.metadata.dao.ts.TsMetadataFleetDao;
import cl.tastets.life.objects.Fleet;
import cl.tastets.life.objects.RealmEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author gaston
 */
@Service
public class FleetCRUDController {

	@Autowired
	private TsMetadataFleetDao dao;

	public Fleet save(Fleet result, String realm) throws Exception {
		try {
			result = dao.save(result, realm);
			return result;
		} catch (Exception e) {
			throw new Exception("Error en save de flotas", e);
		}
	}

	public Fleet update(Fleet result, String realm) throws Exception {
		try {
			result = dao.update(result, realm);
			return result;
		} catch (Exception e) {
			throw new Exception("Error en update de flotas", e);
		}
	}

	public Fleet delete(Fleet result, String realm) throws Exception {
		try {
			result = dao.delete(result, realm);
			return result;
		} catch (Exception e) {
			throw new Exception("Error en delete de flotas", e);
		}
	}

}
