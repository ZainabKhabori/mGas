package om.webware.mgas.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

import om.webware.mgas.R;
import om.webware.mgas.api.OrderService;
import om.webware.mgas.api.Services;
import om.webware.mgas.tools.DatabaseHelper;

/**
 * Created by Zainab on 6/28/2019.
 */

public class CartRecyclerAdapter extends RecyclerView.Adapter<CartRecyclerAdapter.ViewHolder> {

    private Context context;
    private ArrayList<OrderService> orderServices;
    private OnItemClickListener onItemClickListener;

    private double cost;

    public interface OnItemClickListener { void onItemClick(View view, int index, double charge); }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView textViewType;
        private TextView textViewSize;
        private TextView textViewCost;
        private TextView textViewQuantity;
        private ImageView imageViewDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewType = itemView.findViewById(R.id.textViewType);
            textViewSize = itemView.findViewById(R.id.textViewSize);
            textViewCost = itemView.findViewById(R.id.textViewCost);
            textViewQuantity = itemView.findViewById(R.id.textViewQuantity);
            imageViewDelete = itemView.findViewById(R.id.imageViewDelete);

            imageViewDelete.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(onItemClickListener != null) {
                onItemClickListener.onItemClick(v, getAdapterPosition(), cost);
            }
        }

        public TextView getTextViewType() {
            return textViewType;
        }

        public TextView getTextViewSize() {
            return textViewSize;
        }

        public TextView getTextViewCost() {
            return textViewCost;
        }

        public TextView getTextViewQuantity() {
            return textViewQuantity;
        }
    }

    public CartRecyclerAdapter(Context context, ArrayList<OrderService> orderServices) {
        this.context = context;
        this.orderServices = orderServices;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public CartRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.recycler_cart_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartRecyclerAdapter.ViewHolder holder, int i) {
        String id = orderServices.get(i).getServiceId();
        String where = "where id='" + id + "'";
        DatabaseHelper helper = new DatabaseHelper(context);
        Services services = (Services)helper.select(DatabaseHelper.Tables.SERVICES, where);

        holder.getTextViewType().setText(services.getServices().get(0).getType());
        holder.getTextViewSize().setText(services.getServices().get(0).getCylinderSize());

        cost = orderServices.get(i).getQuantity() * services.getServices().get(0).getCharge();
        String costFormatted = new DecimalFormat("0.000").format(cost);
        String costStr = costFormatted + " " + context.getString(R.string.OMR);
        holder.getTextViewCost().setText(costStr);

        String quantity = "X " + orderServices.get(i).getQuantity();
        holder.getTextViewQuantity().setText(quantity);
    }

    @Override
    public int getItemCount() {
        return orderServices.size();
    }
}
