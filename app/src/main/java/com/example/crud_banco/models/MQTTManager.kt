import org.eclipse.paho.client.mqttv3.*
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import java.io.File
import java.io.FileInputStream
import java.security.KeyStore
import java.security.cert.Certificate
import java.security.cert.CertificateFactory
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory

class MQTTManager {
    private val brokerUrl = "ssl://a9wbaiz2m81xn-ats.iot.us-east-2.amazonaws.com:8883"
    private val clientId = "AndroidClient"
    private val topic = "esp32/sub"
    private val certificateFilePath = "C:\\Users\\gabri\\AndroidStudioProjects\\CRUD_BANCO\\app\\src\\main\\java\\com\\example\\crud_banco\\models\\certificate.crt"
    private val privateKeyFilePath = "C:\\Users\\gabri\\AndroidStudioProjects\\CRUD_BANCO\\app\\src\\main\\java\\com\\example\\crud_banco\\models\\private_key.pem"

    private val mqttClient = MqttClient(brokerUrl, clientId, MemoryPersistence())

    init {
        connect()
    }

    fun connect() {
        val options = MqttConnectOptions()
        options.socketFactory = createSSLSocketFactory()
        options.isAutomaticReconnect = true
        options.isCleanSession = false

        mqttClient.connect(options)
    }

    fun publish(message: String) {
        if (mqttClient.isConnected) {
            val mqttMessage = MqttMessage()
            mqttMessage.payload = message.toByteArray()
            mqttClient.publish(topic, mqttMessage)
        }
    }

    private fun createSSLSocketFactory(): javax.net.ssl.SSLSocketFactory {
        val certificate = loadCertificate(certificateFilePath)
        val privateKey = readFileAsString(privateKeyFilePath).toCharArray()

        val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
        keyStore.load(null, null)
        keyStore.setCertificateEntry("certificate", certificate)

        val keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())
        keyManagerFactory.init(keyStore, privateKey)

        val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        trustManagerFactory.init(keyStore)

        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(keyManagerFactory.keyManagers, trustManagerFactory.trustManagers, null)

        return sslContext.socketFactory
    }

    private fun loadCertificate(filePath: String): Certificate {
        val certificateStream = FileInputStream(filePath)
        val certificateFactory = CertificateFactory.getInstance("X.509")
        return certificateFactory.generateCertificate(certificateStream)
    }

    private fun readFileAsString(filePath: String): String {
        val file = File(filePath)
        return file.readText()
    }

}
