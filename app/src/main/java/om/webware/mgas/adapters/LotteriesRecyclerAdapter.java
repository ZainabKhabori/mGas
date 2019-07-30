package om.webware.mgas.adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import om.webware.mgas.R;
import om.webware.mgas.api.Lottery;

/**
 * Created by Zainab on 6/29/2019.
 */

public class LotteriesRecyclerAdapter extends RecyclerView.Adapter<LotteriesRecyclerAdapter.ViewHolder> {

    private ArrayList<Lottery> lotteries;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener { void onItemClick(View view, int index); }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView textViewStart;
        private TextView textViewEnd;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewStart = itemView.findViewById(R.id.textViewStart);
            textViewEnd = itemView.findViewById(R.id.textViewEnd);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(onItemClickListener != null) {
                onItemClickListener.onItemClick(v, getAdapterPosition());
            }
        }

        public TextView getTextViewStart() {
            return textViewStart;
        }

        public TextView getTextViewEnd() {
            return textViewEnd;
        }
    }

    public LotteriesRecyclerAdapter(ArrayList<Lottery> lotteries) {
        this.lotteries = lotteries;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public LotteriesRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.recycler_lotteries_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LotteriesRecyclerAdapter.ViewHolder viewHolder, int i) {

        SimpleDateFormat format = new SimpleDateFormat("EEE, d/M/yyyy", Locale.getDefault());

        Date start = lotteries.get(i).getStartDate();
        Date end = lotteries.get(i).getEndDate();

        String startDate = format.format(start);
        String endDate = format.format(end);

        viewHolder.textViewStart.setText(startDate);
        viewHolder.textViewEnd.setText(endDate);
    }

    @Override
    public int getItemCount() {
        return lotteries.size();
    }
}
