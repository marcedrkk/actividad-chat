package com.example.chatsimple.adapter;
import android.view.*;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.chatsimple.R;
import com.example.chatsimple.modelo.Mensaje;
import com.google.firebase.auth.FirebaseAuth;
import java.util.List;

public class MensajeAdapter extends RecyclerView.Adapter<MensajeAdapter.VH> {
    private List<Mensaje> lista;
    private String miUid = FirebaseAuth.getInstance().getUid();

    public MensajeAdapter(List<Mensaje> l) { lista = l; }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvTexto;
        LinearLayout root;
        VH(View v) {
            super(v);
            tvTexto = v.findViewById(R.id.tvTextoMensaje);
            root = (LinearLayout) v;
        }
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        return new VH(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_mensaje, parent, false));
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        Mensaje m = lista.get(position);
        holder.tvTexto.setText(m.getTexto());

        if (m.getEmisor().equals(miUid)) {
            // Mensaje enviado → derecha
            holder.root.setGravity(Gravity.END);
            holder.tvTexto.setBackgroundResource(R.drawable.bg_mensaje_enviado);
        } else {
            // Mensaje recibido → izquierda
            holder.root.setGravity(Gravity.START);
            holder.tvTexto.setBackgroundResource(R.drawable.bg_mensaje_recibido);
        }
    }

    @Override
    public int getItemCount() { return lista.size(); }
}
