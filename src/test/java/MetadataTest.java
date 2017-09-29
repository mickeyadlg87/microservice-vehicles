
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import cl.tastets.life.microservices.metadata.MetadataVehicleApplication;
import cl.tastets.life.microservices.metadata.dao.ts.TsMetadataDao;
import cl.tastets.life.objects.BasicEntity;
import cl.tastets.life.objects.Fleet;
import cl.tastets.life.objects.RealmEnum;
import cl.tastets.life.objects.RequestListMids;
import cl.tastets.life.objects.Vehicle;
import cl.tastets.life.objects.utils.QueryFilter;
import cl.tastets.life.objects.utils.RequestData;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.springframework.http.MediaType;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 *
 * @author gaston
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = MetadataVehicleApplication.class)
@WebAppConfiguration
@Ignore
public class MetadataTest {

	@Autowired
	TsMetadataDao metadataDao;

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
	public void testFleetGetByMid() throws Exception {
		try {
			ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders.get("/metadata/vehicle/getFleetByMid")
					.param("realm", RealmEnum.rslite.toString())
					.param("mid", "353301059788922")
					.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk());
			List vehicles = (List) result.andReturn().getAsyncResult();
			Assert.assertTrue(!((String) ((Map) vehicles.get(0)).get("_m")).isEmpty());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testGetByMid() throws Exception {
		try {
			ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders.get("/metadata/vehicle/getByMid")
					.param("realm", RealmEnum.rslite.toString())
					.param("mid", "861074022159852")
					.param("lastState", "true")
					.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk());
			Vehicle vehicle = (Vehicle) result.andReturn().getAsyncResult();
			Assert.assertNotNull(vehicle.getString("_m"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testGetVehicleById() throws Exception {
		try {
			ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders.get("/metadata/vehicle/getVehicleById")
					.param("realm", RealmEnum.rslite.toString())
					.param("idVehicle", "10")
					.param("lastState", "true")
					.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk());
			Vehicle vehicle = (Vehicle) result.andReturn().getAsyncResult();
			Assert.assertNotNull(vehicle.getInteger("id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testGetVehicleByTag() throws Exception {
		try {
			ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders.get("/metadata/vehicle/getVehicleByTag")
					.param("realm", RealmEnum.rslite.toString())
					.param("tag", "GHYP-25")
					.param("lastState", "true")
					.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk());
			Vehicle vehicle = (Vehicle) result.andReturn().getAsyncResult();
			Assert.assertNotNull(vehicle.getString("plateNumber"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testListMids() throws Exception {
		try {
			List<Vehicle> list = new ArrayList<>();
			list.add(new Vehicle().put("_m", "861074022098480"));
			list.add(new Vehicle().put("_m", "861074022093812"));
			list.add(new Vehicle().put("_m", "861074022091667"));
			list.add(new Vehicle().put("_m", "861074022096625"));
			list.add(new Vehicle().put("_m", "861074022159852"));
			RequestListMids req = new RequestListMids("rslite", true, list);
			ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders.post("/metadata/vehicle/getByListMids").contentType(MediaType.APPLICATION_JSON)
					.content(mapper.writeValueAsBytes(req))
					.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk());
			List vehicles = (List) result.andReturn().getAsyncResult();
			Assert.assertTrue(vehicles.size() == 5);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testListMidsAsync() throws Exception {
		try {
			List<Vehicle> list = new ArrayList<>();
			list.add(new Vehicle().put("_m", "861074022098480"));
			list.add(new Vehicle().put("_m", "861074022093812"));
			list.add(new Vehicle().put("_m", "861074022091667"));
			list.add(new Vehicle().put("_m", "861074022096625"));
			list.add(new Vehicle().put("_m", "861074022159852"));
			RequestListMids req = new RequestListMids("rslite", true, list);
			ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders.post("/metadata/vehicle/getByListMidsAsync").contentType(MediaType.APPLICATION_JSON)
					.content(mapper.writeValueAsBytes(req))
					.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk());
			List vehicles = (List) result.andReturn().getAsyncResult();
			Assert.assertTrue(vehicles.size() == 5);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testMetadataByCompany() throws Exception {
		try {
			ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders.get("/metadata/vehicle/getByCompany")
					.param("realm", RealmEnum.rslite.toString())
					.param("companyId", "193")
					.param("lastState", "true")
					.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk());
			List vehicles = (List) result.andReturn().getAsyncResult();
			Assert.assertTrue(vehicles.size() > 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testMetadataAllByCompany() throws Exception {
		try {
			RequestData req = RequestData.from();
			QueryFilter filter = QueryFilter.from();
			filter.put("userProfile", "NOT_ADMIN");
			filter.put("userId", 2381);
			req.setFilter(filter);
			ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders.post("/metadata/vehicle/getAllByCompany")
					.contentType(MediaType.APPLICATION_JSON)
					.param("realm", RealmEnum.rslite.toString())
					.param("companyId", "193")
					.param("lastState", "true")
					.contentType(MediaType.APPLICATION_JSON)
					.content(mapper.writeValueAsBytes(req))
					.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk());
			List vehicles = (List) result.andReturn().getAsyncResult();
			Assert.assertTrue(!((String) ((Map) vehicles.get(0)).get("_m")).isEmpty());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testMetadataByFleet() throws Exception {
		try {
			ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders.get("/metadata/vehicle/getMetadataByFleet")
					.param("realm", RealmEnum.rslite.toString())
					.param("companyId", "1")
					.param("lastState", "true")
					.param("fleetId", "1")
					.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk());
			List vehicles = (List) result.andReturn().getAsyncResult();
			Assert.assertTrue(!((String) ((Map) vehicles.get(0)).get("name")).isEmpty());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testMetadataAllVehicles() throws Exception {
		try {
			ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders.get("/metadata/vehicle/getAll")
					.param("onlyImei", "true")
					.param("lastState", "true")
					.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk());
			List vehicles = (List) result.andReturn().getAsyncResult();
			Assert.assertTrue(!((String) ((Map) vehicles.get(0)).get("_m")).isEmpty());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testMetadataAllVehiclesByRealm() throws Exception {
		try {
			ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders.get("/metadata/vehicle/getByRealm")
					.param("realm", RealmEnum.rslite.toString())
					.param("lastState", "true")
					.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk());
			List vehicles = (List) result.andReturn().getAsyncResult();
			Assert.assertTrue(!((String) ((Map) vehicles.get(0)).get("name")).isEmpty());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void getVehiclesByUser() throws Exception {
		try {
			RequestData req = RequestData.from();
			QueryFilter filter = QueryFilter.from();
			filter.put("userProfile", "NOT_ADMIN");
			filter.put("userId", 2381);
			req.setFilter(filter);
			ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders.post("/metadata/vehicle/getVehiclesByUser")
					.param("realm", RealmEnum.rslite.toString())
					.param("lastState", "true")
					.param("companyId", "193")
					.contentType(MediaType.APPLICATION_JSON)
					.content(mapper.writeValueAsBytes(req))
					.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk());
			List vehicles = (List) result.andReturn().getAsyncResult();
			Assert.assertTrue(!((String) ((Map) vehicles.get(0)).get("name")).isEmpty());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testTypesMetadata() throws Exception {
		try {
			ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders.get("/metadata/vehicle/getTypesMetadata")
					.param("realm", RealmEnum.rslite.toString())
					.param("type", "vehicleType")
					.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk());
			List<BasicEntity> lista = Arrays.asList(mapper.readValue(result.andReturn().getResponse().getContentAsByteArray(), BasicEntity[].class));
			Assert.assertTrue(!((String) ((Map) lista.get(0)).get("name")).isEmpty());
			result = this.mockMvc.perform(MockMvcRequestBuilders.get("/metadata/vehicle/getTypesMetadata")
					.param("realm", RealmEnum.rslite.toString())
					.param("type", "engineType")
					.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk());
			lista = Arrays.asList(mapper.readValue(result.andReturn().getResponse().getContentAsByteArray(), BasicEntity[].class));
			Assert.assertTrue(!((String) ((Map) lista.get(0)).get("name")).isEmpty());
			result = this.mockMvc.perform(MockMvcRequestBuilders.get("/metadata/vehicle/getTypesMetadata")
					.param("realm", RealmEnum.rslite.toString())
					.param("type", "subVehicleType")
					.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk());
			lista = Arrays.asList(mapper.readValue(result.andReturn().getResponse().getContentAsByteArray(), BasicEntity[].class));
			Assert.assertTrue(!((String) ((Map) lista.get(0)).get("name")).isEmpty());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void getFleetsByCompany() throws Exception {
		try {
			RequestData req = RequestData.from();
			QueryFilter filter = QueryFilter.from();
			filter.put("userProfile", "ADMIN");
			req.setFilter(filter);
			ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders.post("/metadata/vehicle/fleet/getMetadataByCompany")
					.param("realm", RealmEnum.rslite.toString())
					.param("companyId", "144")
					.contentType(MediaType.APPLICATION_JSON)
					.content(mapper.writeValueAsBytes(req))
					.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk());
			List<Fleet> listaFlotas = Arrays.asList(mapper.readValue(result.andReturn().getResponse().getContentAsByteArray(), Fleet[].class));
			System.out.println("FLEET_BY_COMPANY = " + listaFlotas.get(0));
			Assert.assertTrue(listaFlotas.size() > 0 && !((String) listaFlotas.get(0).get("maxSpeed")).isEmpty());
		} catch (Exception e) {
			e.getMessage();
		}
	}

//	@Test
//	public void getByRealmTest() throws Exception {
//		Assert.assertNotNull(metadataDao.getByRealm(RealmEnum.rastreosat.name(), "queryAllByRealm"));
//		Assert.assertNotNull(metadataDao.getByRealm(RealmEnum.rslite.name(), "queryAllByRealm"));
//		Assert.assertNotNull(metadataDao.getByRealm(RealmEnum.entel.name(), "queryAllByRealm"));
//	}
//
//	@Test
//	public void getAll() throws Exception {
//		Assert.assertNotNull(metadataDao.getAll(true));
//	}
//
//	@Test
//	public void getVehicleMetadataTest() throws Exception {
//		Assert.assertNotNull(metadataDao.getVehicleMetadata(RealmEnum.rslite.name(), "861074022115003", "queryVehicleByImei"));
//		Assert.assertNotNull(metadataDao.getVehicleMetadata(RealmEnum.rastreosat.name(), "867844000194608", "queryVehicleByImei"));
//		Assert.assertNotNull(metadataDao.getVehicleMetadata(RealmEnum.entel.name(), "110F4BFC", "queryVehicleByImei"));
//	}
//
//	@Test
//	public void getCompanyMetadata() throws Exception {
//		Assert.assertNotNull(metadataDao.getCompanyMetadata(RealmEnum.rslite.name(), 45, "queryCompanyMetadata"));
//		Assert.assertNotNull(metadataDao.getCompanyMetadata(RealmEnum.rastreosat.name(), 2, "queryCompanyMetadata"));
//		Assert.assertNotNull(metadataDao.getCompanyMetadata(RealmEnum.entel.name(), 2, "queryCompanyMetadata"));
//	}
//
//	@Test
//	public void getFleetMetadataTest() throws Exception {
//		Assert.assertNotNull(metadataDao.getFleetMetadata(RealmEnum.rslite.name(), 1, 1, "queryFleetMetadata"));
//		Assert.assertNotNull(metadataDao.getFleetMetadata(RealmEnum.entel.name(), 2, 10002, "queryFleetMetadata"));
//		Assert.assertNotNull(metadataDao.getFleetMetadata(RealmEnum.rastreosat.name(), 2, 10002, "queryFleetMetadata"));
//	}
//
//	@Test
//	public void getVehicleByTagTest() throws Exception {
//		Assert.assertNotNull(metadataDao.getVehicleByTag("SL7745", RealmEnum.entel.name(), "queryVehicleByTag"));
//		Assert.assertNotNull(metadataDao.getVehicleByTag("GHYP-25", RealmEnum.rslite.name(), "queryVehicleByTag"));
//	}
//
//	@Test
//	public void getVehicleByIdTest() throws Exception {
//		Assert.assertNotNull(metadataDao.getVehicleById(1, RealmEnum.rslite.name(), "queryVehicleById"));
//		Assert.assertNotNull(metadataDao.getVehicleById(50856, RealmEnum.rastreosat.name(), "queryVehicleById"));
//
//	}
//
//	@Test
//	public void getVehicleByUser() throws Exception {
//		Assert.assertNotNull(metadataDao.getVehiclesByUser("eduardo", 2, RealmEnum.rastreosat.name(), "queryVehicleByUser"));
//	}
}
