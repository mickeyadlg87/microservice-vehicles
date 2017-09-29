package cl.tastets.life.microservices.metadata.vehicles.services;

import cl.tastets.life.microservices.metadata.vehicles.controllers.TrailerController;
import cl.tastets.life.objects.RealmEnum;
import cl.tastets.life.objects.Trailer;
import cl.tastets.life.objects.utils.RequestData;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * Created by glucero on 08-07-16.
 */
@RestController
@RequestMapping(value = "/metadata/vehicle/trailer", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(value = "Metadata Trailer service")
public class TrailerMetadataService {

    @Autowired
    TrailerController controller;

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public Trailer save(@RequestBody Trailer trailer) {
        try {
            return controller.save(
                    trailer, RealmEnum.valueOf(trailer.getString("realm")).toString());
        } catch (Exception e) {
            throw new RuntimeException("Error al crear trailer", e);
        }
    }

    /**
     * Metodo que actualiza un trailer
     *
     * @param trailer Trailer modificada
     * @return trailer actualizado
     * @throws Exception
     */
    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    public Trailer update(@RequestBody Trailer trailer) {
        try {
            return controller.update(
                    trailer, RealmEnum.valueOf(trailer.getString("realm")).toString());
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar trailer ", e);
        }
    }

    /**
     * Metodo que elimina un trailer
     *
     * @param realm Realm al que pertenece el trailer
     * @param id    Id del trailer a eliminar
     * @return Flota eliminada
     * @throws Exception
     */
    @RequestMapping(value = "/delete/{realm}/{id}", method = RequestMethod.DELETE)
    public Trailer delete(@PathVariable("realm") String realm, @PathVariable("id") Integer id) {
        try {
            return controller.delete(new Trailer().put("id", id), RealmEnum.valueOf(realm).toString());
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar trailer ", e);
        }
    }

    @RequestMapping(value = "/getAllByCompany", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @HystrixCommand(fallbackMethod = "defaultAllByCompany")
    public List<Trailer> getAllByCompany(@RequestParam(value = "realm") String realm, @RequestParam(value = "companyId") Integer companyId,
                                 @RequestBody RequestData requestData) {
        try {
            return controller.getAllByCompany(realm, companyId, requestData);
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar metadata por empresa", e);
        }
    }

    public List<Trailer> defaultAllByCompany(String realm, Integer companyId,RequestData requestData) {
        return Arrays.asList(new Trailer());
    }
}

