package com.example.crud_banco.models

import android.util.Log
import com.hivemq.client.mqtt.MqttClient
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets.UTF_8

class MqttManager(
    private val host: String,
    private val username: String,
    private val password: String
) {
    private lateinit var client: Mqtt5AsyncClient
    private var messageCallback: ((String) -> Unit)? = null

    fun connect(onConnected: () -> Unit, onError: (Throwable) -> Unit) {
        // Criar um cliente MQTT assíncrono
        client = MqttClient.builder()
            .useMqttVersion5()
            .serverHost(host)
            .serverPort(8883)
            .sslWithDefaultConfig()
            .buildAsync()

        // Conectar-se ao HiveMQ Cloud usando coroutines
        CoroutineScope(Dispatchers.IO).launch {
            try {
                client.connectWith()
                    .simpleAuth()
                    .username(username)
                    .password(UTF_8.encode(password))
                    .applySimpleAuth()
                    .send()
                    .whenComplete { _, throwable ->
                        if (throwable != null) {
                            Log.e("MqttManager", "Erro ao conectar", throwable)
                            onError(throwable)
                        } else {
                            Log.d("MqttManager", "Conectado com sucesso")
                            onConnected()
                        }
                    }
            } catch (e: Exception) {
                Log.e("MqttManager", "Erro ao conectar ou comunicar com o servidor MQTT", e)
                onError(e)
            }
        }
    }

    fun disconnect() {
        if (::client.isInitialized && client.state.isConnected) {
            CoroutineScope(Dispatchers.IO).launch {
                client.disconnect()
                Log.d("MqttManager", "Desconectado com sucesso")
            }
        }
    }

    fun subscribe(topic: String, onSubscribed: () -> Unit, onError: (Throwable) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                client.subscribeWith()
                    .topicFilter(topic)
                    .callback { publish: Mqtt5Publish ->
                        val payloadOptional = publish.payload
                        if (payloadOptional.isPresent) {
                            val message = UTF_8.decode(payloadOptional.get()).toString()
                            messageCallback?.invoke(message) // Chama o callback com a mensagem recebida
                        }
                    }
                    .send()
                    .whenComplete { _, throwable ->
                        if (throwable != null) {
                            Log.e("MqttManager", "Erro ao inscrever no tópico $topic", throwable)
                            onError(throwable)
                        } else {
                            Log.d("MqttManager", "Inscrito no tópico $topic")
                            onSubscribed()
                        }
                    }
            } catch (e: Exception) {
                Log.e("MqttManager", "Erro ao inscrever no tópico", e)
                onError(e)
            }
        }
    }

    fun setMessageCallback(callback: (String) -> Unit) {
        this.messageCallback = callback
    }

    fun publish(topic: String, message: String, onSuccess: () -> Unit, onError: (Throwable) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            if (client.state.isConnected) {
                client.publishWith()
                    .topic(topic)
                    .payload(UTF_8.encode(message))
                    .send()
                    .whenComplete { _, throwable ->
                        if (throwable != null) {
                            Log.e("MqttManager", "Falha ao enviar mensagem", throwable)
                            onError(throwable)
                        } else {
                            Log.d("MqttManager", "Mensagem enviada com sucesso")
                            onSuccess()
                        }
                    }
            } else {
                Log.d("MqttManager", "Cliente não está conectado, não foi possível enviar mensagem")
                onError(Exception("Cliente não está conectado"))
            }
        }
    }
}
