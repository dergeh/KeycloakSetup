package eu.ditas.tub

import com.fasterxml.jackson.databind.ObjectMapper
import eu.ditas.tub.model.BlueprintConfig
import eu.ditas.tub.model.KeyCloakModel
import eu.ditas.tub.model.UserModel
import io.javalin.Javalin
import org.jboss.resteasy.util.Base64
import org.junit.*

class APIControllerTest {

    class MockKeyCloak() : IKeycloakAdmin {
        override fun initizeRelam(config: BlueprintConfig?): Any {
            return object {}
        }

        override fun applyConfig(config: KeyCloakModel?) {

        }

    }
    private var mapper = ObjectMapper()
    private lateinit var app: Javalin
    private val url = "http://localhost:8000"


    @Before fun setUp() {

        app = APIController(8000,MockKeyCloak()).init()
    }

    @After fun tearDown() {
        app.stop()
    }

    @Test
    fun initBlueprint() {
        val blue = BlueprintConfig()
        blue.blueprintID = "0000"
        blue.clientId = "vdc_client"

        val response = khttp.post("$url/v1/init", data = mapper.writeValueAsString(blue))

        Assert.assertTrue("blueprint created!",response.statusCode == 201)
    }

    @Test
    fun applyConfiguration() {
        val blue = BlueprintConfig()
        blue.blueprintID = "f3a00b2"
        blue.clientId = "vdc_client"

        var response = khttp.post("$url/v1/init", data = mapper.writeValueAsString(blue))
        Assert.assertTrue("blueprint created!",response.statusCode == 201)

        val config = KeyCloakModel()
        config.roles.add("test")
        config.users.add(UserModel())

        val key = Base64.decode(khttp.get("$url/v1/keys").jsonObject.getString("key"))

        Assert.assertTrue("keys available",key != null)
        val mapper = ObjectMapper()
        val publicKey = Crypto.importKey(key)
        val data = Base64.encodeBytes(Crypto.encrypt(publicKey,mapper.writeValueAsString(config)))

        response = khttp.post("$url/v1/${blue.blueprintID}",data = mapper.writeValueAsString(data))
        Assert.assertTrue("blueprint created!",response.statusCode == 200)
    }

    @Test
    fun updateConfiguration() {
    }

    @Test
    fun getKeys() {
        print(khttp.get("$url/v1/keys").text)
    }
}