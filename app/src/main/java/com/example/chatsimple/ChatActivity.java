package com.example.chatsimple;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.*;
import com.example.chatsimple.adapter.MensajeAdapter;
import com.example.chatsimple.modelo.Mensaje;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;
import org.eclipse.paho.client.mqttv3.*;

import java.util.*;

public class ChatActivity extends AppCompatActivity {
    RecyclerView rv;
    EditText et;
    Button btn;
    List<Mensaje> mensajes = new ArrayList<>();
    MensajeAdapter ad;
    String chatId, receptorUid, miUid;
    MqttClient client;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_chat);

        // UI
        rv = findViewById(R.id.recyclerMensajes);
        rv.setLayoutManager(new LinearLayoutManager(this));
        ad = new MensajeAdapter(mensajes);
        rv.setAdapter(ad);

        et = findViewById(R.id.etMensaje);
        btn = findViewById(R.id.btnEnviar);

        // IDs de usuarios
        miUid = FirebaseAuth.getInstance().getUid();
        receptorUid = getIntent().getStringExtra("receptorUid");
        chatId = miUid.compareTo(receptorUid) < 0 ? miUid + "_" + receptorUid : receptorUid + "_" + miUid;

        // Listener Firestore (mensajes ordenados por timestamp)
        FirebaseFirestore.getInstance()
                .collection("chats").document(chatId).collection("mensajes")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((qs, e) -> {
                    mensajes.clear();
                    if (qs != null) {
                        for (DocumentSnapshot d : qs) {
                            Mensaje m = d.toObject(Mensaje.class);
                            if (m != null) mensajes.add(m);
                        }
                        ad.notifyDataSetChanged();
                    }
                });

        // Botón enviar
        btn.setOnClickListener(v -> {
            String texto = et.getText().toString().trim();
            if (!texto.isEmpty()) {
                Map<String, Object> m = new HashMap<>();
                m.put("remitenteUid", miUid);
                m.put("contenido", texto);
                m.put("timestamp", FieldValue.serverTimestamp());

                FirebaseFirestore.getInstance()
                        .collection("chats").document(chatId).collection("mensajes")
                        .add(m);

                try {
                    client.publish("chat/" + chatId, new MqttMessage(texto.getBytes()));
                } catch (MqttException ex) {
                    ex.printStackTrace();
                    Toast.makeText(ChatActivity.this, "Error al enviar mensaje MQTT", Toast.LENGTH_SHORT).show();
                }

                et.setText("");
            }
        });

        // Conexión MQTT con HiveMQ
        String brokerUrl = "LA URL DESDE HIVEMQ"; // la URL
        String clientId = UUID.randomUUID().toString();
        String mqttUser = "SU USUARIO";       //  usuario HiveMQ
        String mqttPassword = "INGRESAR SU CONTRASEÑA";

        try {
            client = new MqttClient(brokerUrl, clientId);

            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName(mqttUser);
            options.setPassword(mqttPassword.toCharArray());
            options.setCleanSession(true);
            options.setAutomaticReconnect(true);

            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    Toast.makeText(ChatActivity.this, "Conexión MQTT perdida", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) {
                    String texto = new String(message.getPayload());

                    // Guardar mensaje recibido en Firestore
                    Map<String, Object> m = new HashMap<>();
                    m.put("remitenteUid", receptorUid);
                    m.put("contenido", texto);
                    m.put("timestamp", FieldValue.serverTimestamp());

                    FirebaseFirestore.getInstance()
                            .collection("chats").document(chatId).collection("mensajes")
                            .add(m);

                    Toast.makeText(ChatActivity.this, "MQTT: " + texto, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {}
            });

            client.connect(options);
            client.subscribe("chat/" + chatId, 1);

        } catch (MqttException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al conectar con HiveMQ", Toast.LENGTH_SHORT).show();
        }
    }
}
