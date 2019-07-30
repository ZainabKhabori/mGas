package om.webware.mgas.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import java.util.ArrayList;

import om.webware.mgas.R;

/**
 * Created by Zainab on 7/09/2019.
 */

public class DeliverToRecyclerAdapter extends RecyclerView.Adapter<DeliverToRecyclerAdapter.ViewHolder> {

    private Context context;
    private ArrayList<String> names;
    private OnItemClickListener onItemClickListener;

    private int selected = 0;

    public interface OnItemClickListener { void onItemClick(View view, int index); }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private RadioButton radioButtonLocation;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            radioButtonLocation = itemView.findViewById(R.id.radioButtonLocation);
            radioButtonLocation.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onItemClickListener != null) {
                selected = getAdapterPosition();
                notifyDataSetChanged();
                onItemClickListener.onItemClick(v, selected);
            }
        }

        RadioButton getRadioButtonLocation() {
            return radioButtonLocation;
        }
    }

    public DeliverToRecyclerAdapter(Context context, ArrayList<String> names) {
        this.context = context;
        this.names = names;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public DeliverToRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.recycler_deliver_to_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeliverToRecyclerAdapter.ViewHolder viewHolder, int i) {
        viewHolder.getRadioButtonLocation().setChecked(i == selected);

        if(i == 0) {
            boolean rtl = context.getResources().getConfiguration()
                    .getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;

            Drawable icon = ContextCompat.getDrawable(context, R.drawable.ic_add_location);
            if(rtl) {
                viewHolder.getRadioButtonLocation()
                        .setCompoundDrawablesWithIntrinsicBounds(null, null, icon, null);
            } else {
                viewHolder.getRadioButtonLocation()
                        .setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
            }
        }

        viewHolder.getRadioButtonLocation().setText(names.get(i));
    }

    @Override
    public int getItemCount() {
        return names.size();
    }
}
