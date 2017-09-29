package cl.tastets.life.microservices.metadata.vehicles.controllers;

import cl.tastets.life.microservices.metadata.dao.ts.TsMetadataVehicleDao;
import cl.tastets.life.objects.RealmEnum;
import cl.tastets.life.objects.Vehicle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Controller utilizada para el crud de moviles
 *
 * @author gaston
 */
@Service
public class VehicleCRUDController {

	@Autowired
	private TsMetadataVehicleDao dao;

	public Vehicle update(RealmEnum realm, Vehicle vehicle) throws Exception {
		vehicle = dao.update(vehicle, realm, "updateVehicle");
		return vehicle;
	}
}
