package cl.tastets.life.microservices.metadata.vehicles.controllers;

import cl.tastets.life.microservices.metadata.dao.ts.TsMetadataTrailerDao;
import cl.tastets.life.objects.Trailer;
import cl.tastets.life.objects.utils.RequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Created by glucero on 08-07-16.
 */
@Service
public class TrailerController {
    @Autowired
    private TsMetadataTrailerDao dao;

    public Trailer save(Trailer result, String realm) {
        try {
            result = dao.save(result, realm);
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Error en save de trailer", e);
        }
    }

    public Trailer update(Trailer result, String realm) {
        try {
            result = dao.update(result, realm);
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Error en update de trailer", e);
        }
    }

    public Trailer delete(Trailer result, String realm) {
        try {
            result = dao.delete(result, realm);
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Error en delete de trailer", e);
        }
    }

    public List<Trailer> getAllByCompany(String realm, Integer companyId, RequestData requestData) {
        try {
            return dao.getAllByCompany(realm, companyId, Optional.ofNullable(requestData.getPaginated()), Optional.ofNullable(requestData.getFilter()),"queryTrailerMetadataPaginated");
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar los trailers por empresa " + companyId, e);
        }
    }
}
