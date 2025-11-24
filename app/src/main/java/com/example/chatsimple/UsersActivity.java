package com.example.chatsimple;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.*;
import com.example.chatsimple.adapter.UserAdapter;
import com.example.chatsimple.modelo.Usuario;
import com.google.firebase.firestore.*;
import java.util.*;

public class UsersActivity extends AppCompatActivity {
    RecyclerView rv;
    List<Usuario> lista = new ArrayList<>();
    UserAdapter ad;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_users);

        rv = findViewById(R.id.recyclerUsers);
        rv.setLayoutManager(new LinearLayoutManager(this));
        ad = new UserAdapter(lista, u -> {
            Intent i = new Intent(this, ChatActivity.class);
            i.putExtra("receptorUid", u.getUid());
            startActivity(i);
        });
        rv.setAdapter(ad);

        FirebaseFirestore.getInstance().collection("usuarios")
                .addSnapshotListener((qs, e) -> {
                    lista.clear();
                    if (qs != null) {
                        for (DocumentSnapshot d : qs) lista.add(d.toObject(Usuario.class));
                        ad.notifyDataSetChanged();
                    }
                });
    }
}
