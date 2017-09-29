package cl.tastets.life.microservices.metadata.vehicles.services;

import java.io.Writer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import cl.tastets.life.microservices.metadata.vehicles.controllers.VehicleCRUDController;
import cl.tastets.life.microservices.metadata.vehicles.controllers.VehicleController;
import cl.tastets.life.objects.BasicEntity;
import cl.tastets.life.objects.RealmEnum;
import cl.tastets.life.objects.RequestListMids;
import cl.tastets.life.objects.Vehicle;
import cl.tastets.life.objects.utils.RequestData;
import io.swagger.annotations.Api;

/**
 * Clase que expone los servicios web de metadata de moviles. Todos los servicios retornan Json, y los parametros son
 * encapsulados en actions que seran ejecutadas en el Core
 *
 * @author gaston
 */
@RestController
@RequestMapping(value = "/metadata", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(value = "Metadata Vehicle service")
public class VehicleMetadataService {

    @Value("${hystrix.command.default.execution.isolation.thread.timeoutInMilliseconds}")
    private int deferredTimeout;

    @Autowired
    private VehicleController vehicleController;

    @Autowired
    private VehicleCRUDController crudController;

    /**
     * Metodo que actualiza un movil
     *
     * @param vehicle Movil que se va a actualizar
     * @return Movil actualizado
     * @throws Exception
     */
    @RequestMapping(value = "/vehicle/update", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Vehicle update(@RequestBody Vehicle vehicle) throws Exception {
        try {
            return crudController.update(RealmEnum.valueOf(vehicle.getString("realm")), vehicle);
        } catch (Exception e) {
            throw new Exception("Error al actualizar el movil", e);
        }
    }

    /**
     * Servicio web que consulta los eventos programados a un empresa en particular
     *
     * @param realm     Realm al que pertenece el movil
     * @param idCompany
     * @return Movil con la informacion
     */
    @RequestMapping(value = "/vehicle/getEventsByCompany", method = RequestMethod.GET, consumes = MediaType.ALL_VALUE)
    @HystrixCommand(fallbackMethod = "defaulEventByCompany")
    public DeferredResult<List<BasicEntity>> getDeviceEventIdByCompanyId(@RequestParam(value = "realm") String realm, @RequestParam(value = "idCompany") Integer idCompany)
            throws Exception {
        try {
            final DeferredResult<List<BasicEntity>> deferredResult = new DeferredResult<>();
            vehicleController.getDeviceEventIdByCompanyId(realm, idCompany, deferredResult);
            return deferredResult;
        } catch (Exception e) {
            throw new Exception("Error al buscar metadata del movil", e);
        }
    }

    /**
     * Servicio web que consulta la metadata de todas las flotas a la que pertenece un modemId
     *
     * @param realm Realm al que pertenece el movil
     * @param mid   ModemId/imei del movil
     * @return Movil con la informacion
     */
    @RequestMapping(value = "/vehicle/getFleetByMid", method = RequestMethod.GET, consumes = MediaType.ALL_VALUE)
    @HystrixCommand(fallbackMethod = "defaulGroupByMid")
    public DeferredResult<List<BasicEntity>> getMetadataFleetByMid(@RequestParam(value = "realm") String realm,
                                                                   @RequestParam(value = "mid") String mid) throws Exception {
        try {
            final DeferredResult<List<BasicEntity>> deferredResult = new DeferredResult<>();
            vehicleController.getMetadataFleetByMid(RealmEnum.valueOf(realm).toString(), mid, deferredResult);
            return deferredResult;
        } catch (Exception e) {
            throw new Exception("Error al buscar metadata del movil", e);
        }
    }

    /**
     * Servicio web que consulta la metadata de un movil
     *
     * @param realm     Realm al que pertenece el movil
     * @param mid       ModemId/imei del movil
     * @param lastState Si se desea obtener la informacion de lastState
     * @return Movil con la informacion
     */
    @RequestMapping(value = "/vehicle/getByMid", method = RequestMethod.GET, consumes = MediaType.ALL_VALUE)
    @HystrixCommand(fallbackMethod = "defaultVehicle")
    public DeferredResult<Vehicle> getMetadataByMid(@RequestParam(value = "realm") String realm,
                                                    @RequestParam(value = "mid") String mid,
                                                    @RequestParam(value = "lastState", required = true) Boolean lastState) throws Exception {
        try {
            final DeferredResult<Vehicle> deferredResult = new DeferredResult<>(deferredTimeout);
            vehicleController.getByMid(realm, mid, Optional.ofNullable(lastState), deferredResult);
            return deferredResult;
        } catch (Exception e) {
            throw new Exception("Error al buscar metadata del movil", e);
        }
    }

    /**
     * Servicio que dado un id de movil, retorna la informacion del mismo
     *
     * @param idVehicle Id del movil a buscar
     * @param realm     REalm al que pertenece el movil
     * @param lastState
     * @return Vehiculo correspondiente al id y realm
     */
    @RequestMapping(value = "/vehicle/getVehicleById", method = RequestMethod.GET, consumes = MediaType.ALL_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @HystrixCommand(fallbackMethod = "defaultVehicleById")
    public DeferredResult<Vehicle> getVehicleById(@RequestParam(value = "idVehicle") Integer idVehicle, @RequestParam(value = "realm") String realm,
                                                  @RequestParam(value = "lastState", required = true) Boolean lastState) throws Exception {
        try {
            final DeferredResult<Vehicle> deferredResult = new DeferredResult<>(deferredTimeout);
            vehicleController.getById(realm, idVehicle, Optional.ofNullable(lastState), deferredResult);
            return deferredResult;
        } catch (Exception e) {
            throw new Exception("Error al buscar metadata por id", e);
        }
    }

    /**
     * Servicio que dada una patente retorna la inforamcion del movil
     *
     * @param tag       TAg del movil a buscar
     * @param realm     REalm al que pertenece el mundo
     * @param lastState
     * @return Vehiculo correspondiente al tag y realm
     */
    @RequestMapping(value = "/vehicle/getVehicleByTag", method = RequestMethod.GET, consumes = MediaType.ALL_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @HystrixCommand(fallbackMethod = "defaultVehicleByTag")
    public DeferredResult<Vehicle> getVehicleByTag(@RequestParam(value = "tag") String tag, @RequestParam(value = "realm") String realm,
                                                   @RequestParam(value = "lastState", required = true) Boolean lastState) throws Exception {
        try {
            final DeferredResult<Vehicle> deferredResult = new DeferredResult<>(deferredTimeout);
            vehicleController.getByTag(realm, tag, Optional.ofNullable(lastState), deferredResult);
            return deferredResult;
        } catch (Exception e) {
            throw new Exception("Error al buscar metadata por id", e);
        }
    }

    /**
     * Este servicio dada una lista de mids retorna la metadata de cada movil
     *
     * @param req REquest con la lista de mids
     * @return Listado de metadata para los mids indicados
     * @throws Exception
     */
    @RequestMapping(value = "/vehicle/getByListMids", method = RequestMethod.POST, consumes = MediaType.ALL_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @HystrixCommand(fallbackMethod = "defaultMetadataByListMids")
    public DeferredResult<List<Vehicle>> getMetadataByListMids(@RequestBody RequestListMids req) throws Exception {
        try {
            final DeferredResult<List<Vehicle>> deferredResult = new DeferredResult<>(deferredTimeout);
            vehicleController.getMetadataByListMids(req, deferredResult);
            return deferredResult;
        } catch (Exception e) {
            throw new Exception("Error al buscar metadata del listado de moviles", e);
        }
    }

    /**
     * Este servicio dada una lista de mids retorna la metadata de cada movil en forma async
     *
     * @param req REquest con la lista de mids
     * @return Listado de metadata para los mids indicados
     * @throws Exception
     */
    @RequestMapping(value = "/vehicle/getByListMidsAsync", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @HystrixCommand(fallbackMethod = "defaultMetadataByListMids")
    @Deprecated
    public DeferredResult<List<Vehicle>> getMetadataByListMidsAsync(@RequestBody RequestListMids req) throws Exception {
        try {
            final DeferredResult<List<Vehicle>> deferredResult = new DeferredResult<>(deferredTimeout);
            vehicleController.getMetadataByListMids(req, deferredResult);
            return deferredResult;
        } catch (Exception e) {
            throw new Exception("Error al buscar metadata del listado de moviles", e);
        }
    }

    /**
     * Metodo que retorna la lista de moviles para una empresa determinada
     *
     * @param realm     Realm al que pertenece la empresa
     * @param companyId Id de la empresa
     * @param lastState
     * @return Listado de moviles de la empresa
     */
    @RequestMapping(value = "/vehicle/getByCompany", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @HystrixCommand(fallbackMethod = "defaultVehiclesByCompany")
    public DeferredResult<List<Vehicle>> getMetadataByCompanyGET(@RequestParam(value = "realm") String realm, @RequestParam(value = "companyId") Integer companyId,
                                                              @RequestParam(value = "lastState", required = false) Boolean lastState) throws Exception {
    	return getMetadataByCompany(realm, companyId, lastState);
    }

    /**
     * Metodo que retorna la lista de moviles para una empresa determinada
     *
     * @param realm     Realm al que pertenece la empresa
     * @param companyId Id de la empresa
     * @param lastState
     * @return Listado de moviles de la empresa
     */
    @RequestMapping(value = "/vehicle/getByCompany", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @HystrixCommand(fallbackMethod = "defaultVehiclesByCompany")
    public DeferredResult<List<Vehicle>> getMetadataByCompanyPOST(@RequestParam(value = "realm") String realm, @RequestParam(value = "companyId") Integer companyId,
                                                              @RequestParam(value = "lastState", required = false) Boolean lastState) throws Exception {
    	return getMetadataByCompany(realm, companyId, lastState);
    }
    
    /**
     * Metodo que retorna la lista de moviles para una empresa determinada
     *
     * @param realm     Realm al que pertenece la empresa
     * @param companyId Id de la empresa
     * @param lastState
     * @return Listado de moviles de la empresa
     */
    public DeferredResult<List<Vehicle>> getMetadataByCompany(String realm, Integer companyId, Boolean lastState) throws Exception {
        try {
            final DeferredResult<List<Vehicle>> deferredResult = new DeferredResult<>(deferredTimeout);
            vehicleController.getMetadataByCompany(realm, companyId, Optional.ofNullable(lastState), deferredResult);
            return deferredResult;
        } catch (Exception e) {
            throw new Exception("Error al buscar metadata por empresa", e);
        }
    }


    /**
     * Metodo que retorna la lista de moviles para una empresa determinada independientemente del perfil del usuario
     *
     * @param realm       Realm al que pertenece la empresa
     * @param companyId   Id de la empresa
     * @param lastState   Si se desea lastState de cada movil
     * @param requestData RequestData contiene los filtros y paginacion
     * @param revertUnsuscribe Si desea buscar los moviles dados de baja de la empresa en particular
     * @return Listado de moviles de la empresa
     */
    @RequestMapping(value = "/vehicle/getAllByCompany", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @HystrixCommand(fallbackMethod = "defaultVehiclesAllByCompany")
    public DeferredResult<List<Vehicle>> ByCompany(@RequestParam(value = "realm") String realm, @RequestParam(value = "companyId") Integer companyId,
                                                   @RequestParam(value = "lastState", defaultValue = "false") Boolean lastState, @RequestParam(value = "revertUnsuscribe", defaultValue = "false") Boolean revertUnsuscribe, 
                                                   @RequestBody RequestData requestData) throws Exception {
        try {
            final DeferredResult<List<Vehicle>> deferredResult = new DeferredResult<>(deferredTimeout);
            vehicleController.getAllByCompany(realm, companyId, Optional.ofNullable(lastState), requestData, Optional.ofNullable(revertUnsuscribe), deferredResult);
            return deferredResult;
        } catch (Exception e) {
            throw new Exception("Error al buscar metadata por empresa", e);
        }
    }

    /**
     * Metodo que retorna la lsta de moviles de una flota de una empresa en particular
     *
     * @param realm     Realm al que pertene la fltoa
     * @param companyId Id de la empresa a la cual pertenece la flota
     * @param fleetId   id de la flota a consultar
     * @param lastState
     * @return Listado de moviles de la flota
     */
    @RequestMapping(value = "/vehicle/getMetadataByFleet", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @HystrixCommand(fallbackMethod = "defaultVehiclesByFleet")
    public DeferredResult<List<Vehicle>> getMetadataByFleetGET(@RequestParam(value = "realm") String realm, @RequestParam(value = "companyId") Integer companyId,
                                                            @RequestParam(value = "fleetId") Integer fleetId,
                                                            @RequestParam(value = "lastState", required = false) Boolean lastState) throws Exception {
        return getMetadataByFleet(realm, companyId, fleetId, lastState);
    }

    /**
     * Metodo que retorna la lsta de moviles de una flota de una empresa en particular
     *
     * @param realm     Realm al que pertene la fltoa
     * @param companyId Id de la empresa a la cual pertenece la flota
     * @param fleetId   id de la flota a consultar
     * @param lastState
     * @return Listado de moviles de la flota
     */
    @RequestMapping(value = "/vehicle/getMetadataByFleet", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @HystrixCommand(fallbackMethod = "defaultVehiclesByFleet")
    public DeferredResult<List<Vehicle>> getMetadataByFleetPOST(@RequestParam(value = "realm") String realm, @RequestParam(value = "companyId") Integer companyId,
                                                            @RequestParam(value = "fleetId") Integer fleetId,
                                                            @RequestParam(value = "lastState", required = false) Boolean lastState) throws Exception {
        return getMetadataByFleet(realm, companyId, fleetId, lastState);
    }

    /**
     * Metodo que retorna la lsta de moviles de una flota de una empresa en particular
     *
     * @param realm     Realm al que pertene la fltoa
     * @param companyId Id de la empresa a la cual pertenece la flota
     * @param fleetId   id de la flota a consultar
     * @param lastState
     * @return Listado de moviles de la flota
     */
    public DeferredResult<List<Vehicle>> getMetadataByFleet(String realm, Integer companyId, Integer fleetId, Boolean lastState) throws Exception {
        try {
            final DeferredResult<List<Vehicle>> deferredResult = new DeferredResult<>(deferredTimeout);
            vehicleController.getMetadataByFleet(realm, companyId, fleetId, Optional.ofNullable(lastState), deferredResult);
            return deferredResult;
        } catch (Exception e) {
            throw new Exception("Error al buscar metadata por flota", e);
        }
    }

    /**
     * Este metodo retorna todos los moviles
     *
     * @param onlyImei
     * @param lastState
     * @return Listado con todos los moviles de todas las plataformas
     */
    @RequestMapping(value = "/vehicle/getAll", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @HystrixCommand(fallbackMethod = "defaultVehiclesAll")
    public DeferredResult<List<Vehicle>> getMetadataAllVehicles(@RequestParam(value = "onlyImei") Boolean onlyImei,
                                                                @RequestParam(value = "lastState", required = false) Boolean lastState,
                                                                @RequestParam(value = "whitFleet", required = false, defaultValue = "false") Boolean withFleet
                                                                ) throws Exception {
    	try {
            final DeferredResult<List<Vehicle>> deferredResult = new DeferredResult<>(deferredTimeout);
            vehicleController.getMetadataAllVehicles(Optional.empty(), Optional.ofNullable(onlyImei), Optional.ofNullable(lastState),
                    deferredResult, Optional.ofNullable(withFleet), Optional.empty(), Optional.empty());
            return deferredResult;
        } catch (Exception e) {
            throw new Exception("Error al buscar metadata de todos los moviles", e);
        }
    }
    
    /**
     * Este metodo retorna todos los moviles
     *
     * @param onlyImei
     * @param lastState
     * @return Listado con todos los moviles de todas las plataformas
     */
    @RequestMapping(value = "/vehicle/getAllStream", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @HystrixCommand(fallbackMethod = "defaultVehiclesAllStream")
    public void //DeferredResult<List<Vehicle>> 
    getMetadataAllVehiclesStream(@RequestParam(value = "onlyImei") Boolean onlyImei,
                                                                @RequestParam(value = "lastState", required = false) Boolean lastState,
                                                                @RequestParam(value = "whitFleet", required = false, defaultValue = "false") Boolean withFleet,
                                                                Writer out
                                                                ) throws Exception {
        try {
        	String query = null;
        	if (onlyImei) {
        		query = "queryAllByRealm";
        	} else {
                if(withFleet){
                    query = "queryAllByRealmWithFleet";
                } else {
                    query = "queryAllByRealm";
                }
        	}
            vehicleController.getAll(query, onlyImei, out, RealmEnum.rastreosat.name(), RealmEnum.entel.name(), RealmEnum.rslite.name());
        	
        } catch (Exception e) {
            throw new Exception("Error al buscar metadata de todos los moviles", e);
        }
    }
    

    /**
     * Metdodo que retorna el listado de moviles por plataforma
     *
     * @param realm     Plataforma a consultar
     * @param lastState
     * @param withFleet
     * @param forCertificate
     * @param requestData
     * @return Lsitado de moviles de la plataforma
     * @throws java.lang.Exception
     */
    @RequestMapping(value = "/vehicle/getByRealm", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @HystrixCommand(fallbackMethod = "defaultVehiclesByRealm")
    public DeferredResult<List<Vehicle>> getMetadataAllVehiclesByRealm(@RequestParam(value = "realm") String realm,
                    @RequestParam(value = "lastState", required = false) Boolean lastState,
                    @RequestParam(value = "withFleet", required = false, defaultValue = "false") Boolean withFleet,
                    @RequestParam(value = "forCertificate", required = false) Boolean forCertificate,
                    @RequestBody RequestData requestData) throws Exception {
        try {
            final DeferredResult<List<Vehicle>> deferredResult = new DeferredResult<>(deferredTimeout);
            vehicleController.getMetadataAllVehicles(
                    Optional.ofNullable(RealmEnum.valueOf(realm).toString()), Optional.empty(), Optional.ofNullable(lastState),
                    deferredResult, Optional.ofNullable(withFleet), Optional.ofNullable(forCertificate), Optional.ofNullable(requestData));
            return deferredResult;
        } catch (Exception e) {
            throw new Exception("Error al buscar metadata por realm", e);
        }
    }

    @RequestMapping(value = "/vehicle/getVehiclesByUser", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @HystrixCommand(fallbackMethod = "defaultMetadataCompanyByUser")
    public DeferredResult<List<Vehicle>> getVehiclesByUser(@RequestParam(value = "realm") String realm, @RequestParam(value = "companyId") Integer companyId,
                                                           @RequestParam(value = "lastState") Boolean lastState, @RequestBody RequestData requestData) throws Exception {
        try {
            final DeferredResult<List<Vehicle>> deferredResult = new DeferredResult<>(deferredTimeout);
            vehicleController.getVehiclesByUser(realm, companyId, Optional.ofNullable(lastState), requestData, deferredResult);
            return deferredResult;
        } catch (Exception e) {
            throw new Exception("Error al buscar metadata de moviles segun el usuario", e);
        }
    }

    /**
     * Este servicio dada una lista de mids retorna la metadata de cada movil
     *
     * @param req REquest con la lista de mids
     * @return Listado de metadata para los mids indicados
     * @throws Exception
     */
    @RequestMapping(value = "/vehicle/getLastStateByMids", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @HystrixCommand(fallbackMethod = "defaultLastStateByMids")
    public DeferredResult<BasicEntity> getLastStateByMids(@RequestBody RequestListMids req) {
        try {
            final DeferredResult<BasicEntity> deferredResult = new DeferredResult<>(deferredTimeout);
            vehicleController.getLastStateByMids(req, deferredResult);
            return deferredResult;
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar metadata del listado de moviles", e);
        }
    }

    @RequestMapping(value = "/vehicle/getTypesMetadata", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<BasicEntity> getTypesMetadata(@RequestParam(value = "realm") String realm,
                                              @RequestParam(value = "type") String type) {
        try {
           return  vehicleController.getTypesMetadata(RealmEnum.valueOf(realm).toString(), type);
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar metadata de tipos ", e);
        }
    }

    /**
     * Metodos fallback
     *
     * @param realm
     * @param mid
     * @param lastState
     * @return
     */
    public Vehicle defaultVehicle(String realm, String mid, Boolean lastState) {
        return new Vehicle();
    }

    public Vehicle defaultVehicleById(Integer idVehicle, String realm, Boolean lastState) {
        return new Vehicle();
    }

    public Vehicle defaultVehicleByTag(String tag, String realm, Boolean lastState) {
        return new Vehicle();
    }

    public List<Vehicle> defaultVehiclesByUser(String user, Integer companyId, String realm, Boolean lastState) {
        return defaultList();
    }

    public List<Vehicle> defaultVehiclesByCompany(String realm, Integer companyId, Boolean lastState) {
        return defaultList();
    }

    public List<Vehicle> defaultVehiclesByFleet(String realm, Integer companyId, Integer fleetId, Boolean lastState) {
        return defaultList();
    }

    public List<Vehicle> defaultVehiclesAll(Boolean onlyImei, Boolean lastState, Boolean whitFleet) {
        return defaultList();
    }

    public void defaultVehiclesAllStream(Boolean onlyImei, Boolean lastState, Boolean whitFleet, Writer out) {
        try {
        	out.write("[]");
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }

    public List<Vehicle> defaultVehiclesByRealm(String realm, Boolean lastState, Boolean whitFleet, Boolean forCert, RequestData requestData) {
        return defaultList();
    }

    public List<Vehicle> defaultMetadataByListMids(RequestListMids req) {
        return defaultList();
    }

    public List<Vehicle> defaultLastStateByMids(RequestListMids req) {
        return defaultList();
    }

    public List<BasicEntity> defaulGroupByMid(String realm, String mid) {
        return defaulGroupList();
    }

    public List<BasicEntity> defaulEventByCompany(String realm, Integer idCompany) {
        return defaulGroupList();
    }

    private List<Vehicle> defaultList() {
        List<Vehicle> list = new ArrayList<>();
        list.add(new Vehicle());
        return list;
    }

    private List<BasicEntity> defaulGroupList() {
        List<BasicEntity> list = new ArrayList<>();
        list.add(new BasicEntity());
        return list;
    }

    public List<Vehicle> defaultVehiclesAllByCompany(String realm, Integer companyId, Boolean lastState, Boolean revertUnsuscribe, RequestData requestData) {
        return defaultList();
    }

    public List<Vehicle> defaultMetadataCompanyByUser(String realm, Integer companyId, Boolean lastState, RequestData requestData) {
        return defaultList();
    }

}
