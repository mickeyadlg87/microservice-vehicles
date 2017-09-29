import cl.tastets.life.microservices.metadata.MetadataVehicleApplication;
import cl.tastets.life.objects.RealmEnum;
import cl.tastets.life.objects.Trailer;
import cl.tastets.life.objects.utils.RequestData;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by glucero on 08-07-16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = MetadataVehicleApplication.class)
@WebAppConfiguration
@Ignore
public class MetadataTrailerTest {
    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;
    ObjectMapper mapper = new ObjectMapper();

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    @Test
    public void testTrailerGetAll() throws Exception {
        try {
            RequestData req = RequestData.from();
            ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders.post("/metadata/vehicle/trailer/getAllByCompany")
                    .param("realm", RealmEnum.rslite.toString())
                    .param("companyId", "146")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsBytes(req))
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
            List<Trailer> lista = Arrays.asList(mapper.readValue(result.andReturn().getResponse().getContentAsByteArray(), Trailer[].class));
            Assert.assertTrue(lista.size() > 0 && lista.get(0).get("id") != null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCrud() throws Exception {
        try {
            Trailer trailer = new Trailer().put("realm", RealmEnum.rslite.toString())
                    .put("name", "trailerName").put("plateNumber", "AA-00-CC")
                    .put("trailerId", "0123456").put("companyId", 193);
            ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders.post("/metadata/vehicle/trailer/save")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsBytes(trailer))
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
            trailer = mapper.readValue(result.andReturn().getResponse().getContentAsByteArray(), Trailer.class);
            Assert.assertTrue(trailer.get("id") != null);
            trailer.put("name", "updateTrailerName");
            result = this.mockMvc.perform(MockMvcRequestBuilders.put("/metadata/vehicle/trailer/update")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsBytes(trailer))
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
            trailer = mapper.readValue(result.andReturn().getResponse().getContentAsByteArray(), Trailer.class);
            Assert.assertTrue(trailer.getString("name").equals("updateTrailerName"));
            result = this.mockMvc.perform(MockMvcRequestBuilders.delete("/metadata/vehicle/trailer/delete/rslite/" + trailer.get("id")))
                    .andExpect(status().isOk());
            trailer = mapper.readValue(result.andReturn().getResponse().getContentAsByteArray(), Trailer.class);
            Assert.assertTrue(trailer.getInteger("id") == -1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
