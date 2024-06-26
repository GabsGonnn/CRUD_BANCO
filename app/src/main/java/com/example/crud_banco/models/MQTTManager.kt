import android.content.Context
import android.util.Log
import com.example.crud_banco.models.MessageOptions.AWS_CLIENT_PASSWORD
import com.example.crud_banco.models.MessageOptions.AWS_CLIENT_USER_NAME
import com.example.crud_banco.models.MessageOptions.AWS_CONNECTION_CLEAN_SESSION
import com.example.crud_banco.models.MessageOptions.AWS_CONNECTION_KEEP_ALIVE_INTERVAL
import com.example.crud_banco.models.MessageOptions.AWS_CONNECTION_RECONNECT
import com.example.crud_banco.models.MessageOptions.AWS_CONNECTION_TIMEOUT
import com.example.crud_banco.models.MessageOptions.AWS_MQTT_HOST
import com.example.crud_banco.models.MessageOptions.QOS
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*

class MQTTManager(context: Context?) {

    companion object {
        const val TAG = "MqttClientHelper"
    }

    var mqttAndroidClient: MqttAndroidClient
    val serverUri = AWS_MQTT_HOST

    private val clientId: String = MqttClient.generateClientId()



    fun setCallback(callback: MqttCallbackExtended?) {
        mqttAndroidClient.setCallback(callback)
    }

    init {
        mqttAndroidClient = MqttAndroidClient(context, serverUri, clientId)
        mqttAndroidClient.setCallback(object : MqttCallbackExtended {
            override fun connectionLost(cause: Throwable?) {
                Log.d(TAG, "MQTT connection lost")
            }

            override fun messageArrived(topic: String?, message: MqttMessage?) {
                Log.w(TAG, message.toString())
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {
                Log.d("TAG", "message delivery complete")
            }

            override fun connectComplete(reconnect: Boolean, serverURI: String?) {
                serverURI?.let {
                    Log.w(TAG, it)
                }
            }
        })
        connect()
    }


    private fun connect() {
        Log.d(TAG, "connect: Trying to call connect function")
        val mqttConnectOptions = MqttConnectOptions()
        mqttConnectOptions.isAutomaticReconnect = AWS_CONNECTION_RECONNECT
        mqttConnectOptions.isCleanSession = AWS_CONNECTION_CLEAN_SESSION
        mqttConnectOptions.userName = AWS_CLIENT_USER_NAME
        mqttConnectOptions.password = AWS_CLIENT_PASSWORD.toCharArray()
        mqttConnectOptions.connectionTimeout = AWS_CONNECTION_TIMEOUT
        mqttConnectOptions.keepAliveInterval = AWS_CONNECTION_KEEP_ALIVE_INTERVAL

        try {
            Log.d(TAG, "connect: Inside the try block")

            mqttAndroidClient.connect(
                mqttConnectOptions, null, object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken?) {

                        Log.d(TAG, "onSuccess: Successfully connected to the broker")
                        val disconnectBufferOptions = DisconnectedBufferOptions()
                        disconnectBufferOptions.isBufferEnabled = true
                        disconnectBufferOptions.bufferSize = 100
                        disconnectBufferOptions.isPersistBuffer = false
                        disconnectBufferOptions.isDeleteOldestMessages = false
                        mqttAndroidClient.setBufferOpts(disconnectBufferOptions)
                    }

                    override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                        Log.w(
                            TAG,
                            "Failed to connect to: $serverUri; ${Log.getStackTraceString(exception)}"
                        )
                    }

                }
            )
        } catch (ex: MqttException) {
            ex.printStackTrace()
        }

    }

    fun subscribe(subscriptionTopic: String, qos: Int = QOS) {
        try {
            mqttAndroidClient.subscribe(subscriptionTopic, qos, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.w(TAG, "Subscribed to topic, $subscriptionTopic")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.w(TAG, "Subscription to topic $subscriptionTopic failed!")
                }
            })
        } catch (ex: MqttException) {
            System.err.println("Exception whilst subscribing to topic '$subscriptionTopic'")
            ex.printStackTrace()
        }
    }

    fun publish(topic: String, msg: String, qos: Int = 0) {
        try {
            val message = MqttMessage()
            message.payload = msg.toByteArray()
            mqttAndroidClient.publish(topic, message.payload, qos, false)
            Log.d(TAG, "Message published to topic `$topic`: $msg")
        } catch (e: MqttException) {
            Log.d(TAG, "Error publishing to $topic: " + e.message)
            e.printStackTrace()
        }
    }

    fun isConnected(): Boolean {
        return mqttAndroidClient.isConnected
    }

    fun disconnectClient() {
        mqttAndroidClient.disconnect()
    }

    fun connectClient() {
        connect()
    }

    fun destroy() {
        mqttAndroidClient.unregisterResources()
        mqttAndroidClient.disconnect()
    }

}