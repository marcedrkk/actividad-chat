package com.example.chatsimple.adapter;
import android.view.*; import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.chatsimple.R;
import com.example.chatsimple.modelo.Usuario;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.VH> {
    public interface OnClick { void click(Usuario u); }
    List<Usuario> lista; OnClick listener;

    public UserAdapter(List<Usuario> l, OnClick o) { lista = l; listener = o; }

    static class VH extends RecyclerView.ViewHolder {
        TextView tv;
        VH(View v) { super(v); tv = v.findViewById(R.id.tvNombreUsuario); }
    }

    @Override public VH onCreateViewHolder(ViewGroup p, int v) {
        return new VH(LayoutInflater.from(p.getContext()).inflate(R.layout.item_user, p, false));
    }
    @Override public void onBindViewHolder(VH h, int i) {
        Usuario u = lista.get(i);
        h.tv.setText(u.getNombre());
        h.itemView.setOnClickListener(x -> listener.click(u));
    }
    @Override public int getItemCount() { return lista.size(); }
}
