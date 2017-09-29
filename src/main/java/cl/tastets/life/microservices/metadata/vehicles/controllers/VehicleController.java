package cl.tastets.life.microservices.metadata.vehicles.controllers;

import cl.tastets.life.microservices.metadata.dao.ts.TsMetadataDao;
import cl.tastets.life.microservices.metadata.dao.ts.TsMetadataVehicleDao;
import cl.tastets.life.objects.BasicEntity;
import cl.tastets.life.objects.Fleet;
import cl.tastets.life.objects.RequestListMids;
import cl.tastets.life.objects.Vehicle;
import cl.tastets.life.objects.utils.RequestData;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.async.DeferredResult;
import rx.Observable;
import javax.annotation.PostConstruct;
import java.io.Writer;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @author gaston
 */
@Component
public class VehicleController {

    private static final Logger logger = Logger.getLogger("metadataVehicles");

    @Value("${events.incidents}")
    private String incidents;

    @Autowired
    private TsMetadataDao metadataDao;

    @Autowired
    private TsMetadataVehicleDao dao;

    private List<String> listIncidents;
    
    private ObjectMapper mapper = new ObjectMapper();
    
    @PostConstruct
    public void incidentes() {
        listIncidents = Arrays.asList(incidents.split(","));
    }

    public void getMetadataFleetByMid(String realm, String mid, DeferredResult<List<BasicEntity>> deferredResult) {
        CompletableFuture.runAsync(() -> {
            final List<BasicEntity> result = Collections.synchronizedList(new ArrayList<>());
            try {
                result.addAll(metadataDao.getFleetMetadataByModemId(realm, mid, "queryFleetByModemId"));
            } catch (EmptyResultDataAccessException e) {
                logger.error("No se encontraron resultados getMetadataFleetByMid  para " + mid, e);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
            deferredResult.setResult(result);
        }).exceptionally((Throwable t) -> {
            logger.error("CompletableFutureError en getMetadataFleetByMid  para " + mid, t);
            deferredResult.setResult(Arrays.asList(new BasicEntity()));
            return null;
        });
    }

    public void getByMid(String realm, String mid, Optional<Boolean> lastState, DeferredResult<Vehicle> deferredResult) {
        CompletableFuture.runAsync(() -> {
            Observable<Vehicle> rt = Observable.defer(() -> {
                Vehicle v = new Vehicle();
                v.putAll(metadataDao.getMetadataByImei(realm, mid, "queryVehicleByImei"));
                return Observable.just(lastState(lastState, v));
            });
            rt.subscribe((Vehicle t) -> {
                deferredResult.setResult(t);
            }, (Throwable t) -> {
                logger.error("Error en getByMid " + t.getMessage());
                deferredResult.setResult(defaultVehicle());
            });
        }).exceptionally((Throwable t) -> {
            logger.error("CompletableFutureError en getByMid " + t.getMessage());
            deferredResult.setResult(defaultVehicle());
            return null;
        });
    }

    public void getById(String realm, Integer id, Optional<Boolean> lastState, DeferredResult<Vehicle> deferredResult) {
        CompletableFuture.runAsync(() -> {
            Observable<Vehicle> rt = Observable.defer(() -> {
                Vehicle v = new Vehicle();
                v.putAll(metadataDao.getMetadataById(id, realm, "queryVehicleById"));
                return Observable.just(lastState(lastState, v));
            });
            rt.subscribe((Vehicle t) -> {
                deferredResult.setResult(t);
            }, (Throwable t) -> {
                logger.error("Error en getById " + t.getMessage(), t);
                deferredResult.setResult(defaultVehicle());
            });
        }).exceptionally((Throwable t) -> {
            logger.error("CompletableFutureError en getById " + t.getMessage());
            deferredResult.setResult(defaultVehicle());
            return null;
        });
    }

    public void getByTag(String realm, String tag, Optional<Boolean> lastState, DeferredResult<Vehicle> deferredResult) {
        CompletableFuture.runAsync(() -> {
            Observable<Vehicle> rt = Observable.defer(() -> {
                Vehicle v = new Vehicle();
                v.putAll(metadataDao.getMetadataByTag(tag, realm, "queryVehicleByTag"));
                return Observable.just(lastState(lastState, v));
            });
            rt.subscribe((Vehicle t) -> {
                deferredResult.setResult(t);
            }, (Throwable t) -> {
                logger.error("Error en getByTag " + t.getMessage(), t);
                deferredResult.setResult(defaultVehicle());
            });
        }).exceptionally((Throwable t) -> {
            logger.error("CompletableFutureError en getByTag " + t.getMessage());
            deferredResult.setResult(defaultVehicle());
            return null;
        });
    }

    public void getMetadataByListMids(RequestListMids req, DeferredResult<List<Vehicle>> deferredResult) {
        CompletableFuture.runAsync(() -> {
            Observable<List<Vehicle>> rt = Observable.defer(() -> {
                final List<Vehicle> result = Collections.synchronizedList(new ArrayList<>());
                final String realm = req.getRealm();
                req.getMids().parallelStream().forEach((Map v) -> {
                    try {
                        String mid = (String) v.get("_m");
                        if (mid != null) {
                            Vehicle vehicle = metadataDao.getMetadataByImei(realm, mid, "queryVehicleByImei");
                            if (req.getLastState()) {
                                vehicle.put("lastState", metadataDao.lastState(mid));
                            }
                            result.add(vehicle);
                        }
                    } catch (EmptyResultDataAccessException e) {
                        logger.warn("No se encontraron resultados para " + realm + " - " + v.get("_m"));
                    } catch (IncorrectResultSizeDataAccessException e2) {
                        logger.warn("Mas de un valor para " + realm + " - " + v.get("_m"));
                    } catch (Exception e) {
                        throw new RuntimeException("Error al obtener imei by list mids", e);
                    }
                });
                return Observable.just(result);
            });
            rt.subscribe((List<Vehicle> t) -> {
                deferredResult.setResult(t);
            }, (Throwable t) -> {
                logger.warn("Error en getMetadataByListMids  " + t.getMessage(), t);
                deferredResult.setResult(defaultListVehicle());
            });
        }).exceptionally((Throwable t) -> {
            logger.error("CompletableFutureError en getMetadataByListMids " + t.getMessage());
            deferredResult.setResult(defaultListVehicle());
            return null;
        });
    }

    public void getMetadataByCompany(String realm, Integer companyId, Optional<Boolean> lastState, DeferredResult<List<Vehicle>> deferredResult) {
        CompletableFuture.runAsync(() -> {
            Observable<List<Vehicle>> rt = Observable.defer(() -> {
                final List<Vehicle> result = Collections.synchronizedList(new ArrayList<>());
                result.addAll(metadataDao.getCompanyMetadata(realm, companyId, "queryCompanyMetadata"));
                result.parallelStream().forEach((Map vehicle) -> {
                    vehicle.remove("lastState");
                });
                lastState(lastState, result);
                return Observable.just(result);
            });
            rt.subscribe((List<Vehicle> t) -> {
                deferredResult.setResult(t);
            }, (Throwable t) -> {
                logger.warn("Error en getMetadataByCompany  " + t.getMessage(), t);
                deferredResult.setResult(defaultListVehicle());
            });
        }).exceptionally((Throwable t) -> {
            logger.error("CompletableFutureError en getMetadataByCompany " + t.getMessage());
            deferredResult.setResult(defaultListVehicle());
            return null;
        });
    }

    public void getAllByCompany(String realm, Integer companyId, Optional<Boolean> lastState, RequestData request, Optional<Boolean> revertUnsuscribe, DeferredResult<List<Vehicle>> deferredResult) {
        CompletableFuture.runAsync(() -> {
            Observable<List<Vehicle>> rt = Observable.defer(() -> {
                final List<Vehicle> result = Collections.synchronizedList(new ArrayList<>());
                
                if (revertUnsuscribe.isPresent() && revertUnsuscribe.get()) {
                    result.addAll(dao.getCompanyMetadataPaginated(realm, companyId,
                            Optional.ofNullable(request.getPaginated()), Optional.ofNullable(request.getFilter()), "queryCompanyUnsuscribeUnit"));
                    lastState(lastState, result);
                } else {
                    result.addAll(dao.getCompanyMetadataPaginated(realm, companyId,
                            Optional.ofNullable(request.getPaginated()), Optional.ofNullable(request.getFilter()), "queryCompanyMetadataPaginated"));
                    lastState(lastState, result);
                }
                return Observable.just(result);
            });
            rt.subscribe((List<Vehicle> t) -> {
                deferredResult.setResult(t);
            }, (Throwable t) -> {
                logger.warn("Error en getAllByCompany  " + t.getMessage(), t);
                deferredResult.setResult(defaultListVehicle());
            });
        }).exceptionally((Throwable t) -> {
            logger.error("CompletableFutureError en getAllByCompany " + t.getMessage());
            deferredResult.setResult(defaultListVehicle());
            return null;
        });
    }

    public void getMetadataByFleet(String realm, Integer companyId, Integer fleetId, Optional<Boolean> lastState, DeferredResult<List<Vehicle>> deferredResult) {
        CompletableFuture.runAsync(() -> {
            Observable<List<Vehicle>> rt = Observable.defer(() -> {
                final List<Vehicle> result = Collections.synchronizedList(new ArrayList<>());
                result.addAll(metadataDao.getFleetMetadata(realm, companyId, fleetId, "queryFleetMetadata"));
                lastState(lastState, result);
                return Observable.just(result);
            });
            rt.subscribe((List<Vehicle> t) -> {
                deferredResult.setResult(t);
            }, (Throwable t) -> {
                logger.warn("Error en getMetadataByFleet  " + t.getMessage(), t);
                deferredResult.setResult(defaultListVehicle());
            });
        }).exceptionally((Throwable t) -> {
            logger.error("CompletableFutureError en getMetadataByFleet " + t.getMessage());
            deferredResult.setResult(defaultListVehicle());
            return null;
        });
    }

    public void getMetadataAllVehicles(Optional<String> realm, Optional<Boolean> onlyImei, Optional<Boolean> lastState,
                                       DeferredResult<List<Vehicle>> deferredResult, Optional<Boolean> withFleet, Optional<Boolean> forCert, Optional<RequestData> reqData) throws Exception {
    	
        CompletableFuture.runAsync(() -> {
            Observable<List<Vehicle>> rt = Observable.defer(() -> {
                final List<Vehicle> result = Collections.synchronizedList(new ArrayList<>());
                String query;
                if(withFleet.isPresent() && withFleet.get()){
                    query = "queryAllByRealmWithFleet";
                } else if (forCert.isPresent() && forCert.get()) {
                    query = "queryAllForCertificate";
                } else {
                    query = "queryAllByRealm";
                }
                if (realm.isPresent()) {
                    result.addAll(metadataDao.getByRealm(realm.get(), query, reqData.get()));
                } else {
                    //TODO parsear string json a map
                    TypeReference<List<Fleet>> typeRef = new TypeReference<List<Fleet>>() {};
                    List<Vehicle> reer = metadataDao.getAll(onlyImei.get(), query);
                    List<Vehicle> res = reer.parallelStream().map((Map<String,Object> vehicle) -> {
                        List<Fleet> list = new ArrayList<>();
                        Vehicle asdas = new Vehicle();
                        asdas.putAll(vehicle);
                        String fleets = asdas.getString("fleets");
                        if(fleets != null){
                            try{
                                // String encodedList = String.valueOf(JsonStringEncoder.getInstance().quoteAsString(fleets));
                                list = mapper.readValue(fleets,typeRef);
                            }catch (Exception e){
                                logger.error("Error al parsear JSON, para movil: "+ asdas, e);
                            }
                        }
                        Vehicle newVehicle = new Vehicle();
                        newVehicle.putAll(vehicle);
                        newVehicle.put("fleets", list);
                        return newVehicle;
                    }).collect(Collectors.toList());
                    result.addAll(res);
                    if (onlyImei.get()) {
                        result.parallelStream().forEach((Map v) -> {
                                    String mid = (String) v.get("_m");
                                    v.clear();
                                    v.put("_m", mid);
                                    v.put("incidents", listIncidents);
                                }
                        );
                    }
                }
                result.parallelStream().forEach((Map vehicle) -> {
                    vehicle.remove("lastState");
                });
                lastState(lastState, result);
                return Observable.just(result);
            });
            rt.subscribe((List<Vehicle> t) -> {
                deferredResult.setResult(t);
            }, (Throwable t) -> {
                logger.error("Error en getMetadataAllVehicles " + t.getMessage(), t);
                deferredResult.setResult(defaultListVehicle());
            });
        }).exceptionally((Throwable t) -> {
            logger.error("CompletableFutureError en getMetadataAllVehicles " + t.getMessage());
            deferredResult.setResult(defaultListVehicle());
            return null;
        });
    }

    public void getVehiclesByUser(String realm, Integer companyId, Optional<Boolean> lastState, RequestData request, DeferredResult<List<Vehicle>> deferredResult) {
        CompletableFuture.runAsync(() -> {
            Observable<List<Vehicle>> rt = Observable.defer(() -> {
                final List<Vehicle> result = Collections.synchronizedList(new ArrayList<>());
                result.addAll(metadataDao.getVehiclesByUser(companyId, realm, Optional.ofNullable(request.getFilter()),
                        Optional.ofNullable(request.getPaginated()), "queryVehicleByUser"));
                lastState(lastState, result);
                return Observable.just(result);
            });
            rt.subscribe((List<Vehicle> t) -> {
                deferredResult.setResult(t);
            }, (Throwable t) -> {
                logger.error("Error en getVehiclesByUser " + t.getMessage(), t);
                deferredResult.setResult(defaultListVehicle());
            });
        }).exceptionally((Throwable t) -> {
            logger.error("CompletableFutureError en getVehiclesByUser " + t.getMessage());
            deferredResult.setResult(defaultListVehicle());
            return null;
        });
    }

    public void getLastStateByMids(RequestListMids req, DeferredResult<BasicEntity> deferredResult) {
        CompletableFuture.runAsync(
                () -> {
                    deferredResult.setResult(metadataDao.lastStateByMids(req.getMids()));
                }).exceptionally((Throwable t) -> {
            logger.error("CompletableFutureError en getLastStateByMids " + t.getMessage());
            deferredResult.setResult(new BasicEntity());
            return null;
        });
    }

    public void getDeviceEventIdByCompanyId(String realm, Integer idCompany, DeferredResult<List<BasicEntity>> deferredResult) {
        CompletableFuture.runAsync(
                () -> {
                    try {
                        final List<BasicEntity> result = Collections.synchronizedList(new ArrayList<>());
                        result.addAll(metadataDao.getDeviceEventId(realm, idCompany, "queryDeviceEventIdByIdCompanyRS"));
                        deferredResult.setResult(result);
                    } catch (EmptyResultDataAccessException e) {
                        logger.error("No se encontraron resultados getDeviceEventIdByCompanyId para " + idCompany);
                    } catch (Exception e) {
                        throw new RuntimeException(e.getMessage(), e);
                    }
                }).exceptionally((Throwable t) -> {
            logger.error("CompletableFutureError en getDeviceEventIdByCompanyId " + t.getMessage());
            deferredResult.setResult(Arrays.asList(new BasicEntity()));
            return null;
        });
    }

    private void lastState(Optional<Boolean> lastState, List<Vehicle> vehicles) {
        vehicles.parallelStream().forEach((Map vehicle) -> {
            vehicle.remove("lastState");
        });
        lastState.ifPresent(lt -> {
            if (lt) {
                metadataDao.lastStateMetadata(vehicles);
            }
        });
    }

    private Vehicle lastState(Optional<Boolean> lastState, Vehicle vehicle) {
        vehicle.remove("lastState");
        lastState.ifPresent(lt -> {
            if (lt) {
                vehicle.put("lastState", metadataDao.lastState(vehicle.getString("_m")));
            }
        });
        return vehicle;
    }

    private Vehicle defaultVehicle() {
        return new Vehicle();
    }

    private List<Vehicle> defaultListVehicle() {
        List<Vehicle> list = new ArrayList<>();
        list.add(new Vehicle());
        return list;
    }

    public List<BasicEntity> getTypesMetadata(String realm, String type) {
        return dao.getTypesMetadata(realm,type);
    }
    
    public void getAll(String query, boolean imeiOnly, Writer out, String ... realms) {
    	metadataDao.getAll(query, out, listIncidents, imeiOnly, realms);
    }
    
}
