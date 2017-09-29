package cl.tastets.life.microservices.metadata.vehicles.services;

import cl.tastets.life.microservices.metadata.vehicles.controllers.FleetCRUDController;
import cl.tastets.life.microservices.metadata.vehicles.controllers.FleetController;
import cl.tastets.life.objects.Fleet;
import cl.tastets.life.objects.RealmEnum;
import cl.tastets.life.objects.utils.RequestData;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import io.swagger.annotations.Api;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller asociado a todos los metodos que se invocan desde los distintos visualizadores
 *
 * @author gaston
 */
@RestController
@RequestMapping(value = "/metadata/vehicle/fleet", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(value = "Metadata Fleet service")
public class FleetMetadataService {

	@Autowired
	private FleetCRUDController crudController;

	@Autowired
	private FleetController fleetController;

	/**
	 * Metodo que persiste una flota
	 *
	 * @param fleet Flota a guardar
	 * @return Flota guardada
	 * @throws Exception
	 */
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public Fleet save(@RequestBody Fleet fleet) throws Exception {
		try {
			return crudController.save(
					fleet, RealmEnum.valueOf(fleet.getString("realm")).toString());
		} catch (Exception e) {
			throw new Exception("Error al crear flota", e);
		}
	}

	/**
	 * Metodo que actualiza una flota
	 *
	 * @param fleet Flota modificada
	 * @return flota actualizada
	 * @throws Exception
	 */
	@RequestMapping(value = "/update", method = RequestMethod.PUT)
	public Fleet update(@RequestBody Fleet fleet) throws Exception {
		try {
			return crudController.update(
					fleet, RealmEnum.valueOf(fleet.getString("realm")).toString());
		} catch (Exception e) {
			throw new Exception("Error al crear flota", e);
		}
	}

	/**
	 * Metodo que elimina una flota
	 *
	 * @param realm Realm al que pertenece la flota
	 * @param id Id de la flota a eliminar
	 * @return Flota eliminada
	 * @throws Exception
	 */
	@RequestMapping(value = "/delete/{realm}/{id}", method = RequestMethod.DELETE)
	public Fleet delete(@PathVariable("realm") String realm, @PathVariable("id") Integer id) throws Exception {
		try {
			return crudController.delete(new Fleet().put("id", id), RealmEnum.valueOf(realm).toString());
		} catch (Exception e) {
			throw new Exception("Error al eliminar flota ", e);
		}
	}

	/**
	 * Metodo que retorna los datos de la flota
	 *
	 * @param realm Realm de la flota a buscar
	 * @param fleetId Id de la flota a buscar
	 * @return Flota que tenga ese id en el realm indicado
	 */
	@RequestMapping(value = "/getMetadataFleetById", method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@HystrixCommand(fallbackMethod = "defaultMetadataFleetById")
	public Fleet getFleetById(@RequestParam(value = "realm") String realm, @RequestParam(value = "fleetId") Integer fleetId,
			@RequestParam(value = "withMetadata", defaultValue = "false", required = false) Boolean withMetadata) throws Exception {
		try {
			return fleetController.getFleetById(realm, fleetId, Optional.ofNullable(withMetadata));
		} catch (Exception e) {
			throw new Exception(e.getMessage(), e);
		}

	}

	/**
	 * Metodo que retorna las flotas que tiene el usuario asignadas
	 *
	 * @param realm Realm de las flotas
	 * @param requestData Contiene los filtros y el paginado
	 * @return Listado de flotas del usuario
	 */
	@RequestMapping(value = "/getMetadataByUser", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@HystrixCommand(fallbackMethod = "defaultMetadataByUser")
	public List<Fleet> getMetadataByUser(@RequestParam(value = "realm") String realm,
			@RequestBody RequestData requestData) throws Exception {
		try {
			return fleetController.getMetadataByUser(realm, requestData);
		} catch (Exception e) {
			throw new Exception(e.getMessage(), e);
		}
	}

	/**
	 * Metodo que retorno la sumatoria de los estados de los moviles, agrupados por cada estado Inactivos - Activos
	 * >5km, Activos e Inactivos > 24hs
	 *
	 * @param realm Realm de la flota a buscar
	 * @param id Id de la flota a buscar
	 * @return Objecto flota que contiene las sumatorias de los distintos estados
	 */
	@RequestMapping(value = "/getMetadataFleetStatus", method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@HystrixCommand(fallbackMethod = "defaultMetadataFleetStatus")
	public Fleet getFleetStatus(@RequestParam(value = "realm") String realm, @RequestParam(value = "id") Integer id)
			throws Exception {
		try {
			return fleetController.getFleetStatus(realm, id);
		} catch (Exception e) {
			throw new Exception(e.getMessage(), e);
		}
	}

	/**
	 * FALLBACK METHODS
	 */
	public Fleet defaultMetadataFleetById(String realm, Integer fleetId, Boolean withMetadata) {
		return new Fleet();
	}

	public List<Fleet> defaultMetadataByUser(String realm, RequestData requestData) {
		return defaultList();
	}

	public Fleet defaultMetadataFleetStatus(String realm, Integer id) {
		return new Fleet();
	}

	private List<Fleet> defaultList() {
		return Arrays.asList(new Fleet());
	}
}
