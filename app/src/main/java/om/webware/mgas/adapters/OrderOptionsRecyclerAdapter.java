package om.webware.mgas.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import om.webware.mgas.R;

public class OrderOptionsRecyclerAdapter extends RecyclerView.Adapter<OrderOptionsRecyclerAdapter.ViewHolder> {

    private ArrayList<String> texts;
    private Context context;
    private int selectedIndex;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener { void onItemClick(View view, int index); }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView textViewOption;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewOption = itemView.findViewById(R.id.textViewOption);
            textViewOption.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(onItemClickListener != null) {
                selectedIndex = getAdapterPosition();
                notifyDataSetChanged();
                onItemClickListener.onItemClick(v, selectedIndex);
            }
        }

        TextView getTextViewOption() {
            return textViewOption;
        }
    }

    public OrderOptionsRecyclerAdapter(ArrayList<String> texts, Context context, int selectedIndex) {
        this.texts = texts;
        this.context = context;
        this.selectedIndex = selectedIndex;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public OrderOptionsRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_order_options_item,
                viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderOptionsRecyclerAdapter.ViewHolder viewHolder, int i) {
        String text = texts.get(i);
        viewHolder.getTextViewOption().setText(text);

        ColorStateList colorStateList;
        if(i == selectedIndex) {
            colorStateList = ContextCompat.getColorStateList(context, R.color.colorAccent);
        } else {
            colorStateList = ContextCompat.getColorStateList(context, R.color.textColorPrimary);
        }
        viewHolder.getTextViewOption().setBackgroundTintList(colorStateList);
    }

    @Override
    public int getItemCount() {
        return texts.size();
    }
}
