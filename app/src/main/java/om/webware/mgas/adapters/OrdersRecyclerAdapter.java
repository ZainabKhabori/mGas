package om.webware.mgas.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import om.webware.mgas.R;
import om.webware.mgas.api.Feedbacks;
import om.webware.mgas.api.Location;
import om.webware.mgas.api.Locations;
import om.webware.mgas.api.Order;
import om.webware.mgas.api.OrderService;
import om.webware.mgas.api.OrderServices;
import om.webware.mgas.api.Service;
import om.webware.mgas.api.Services;
import om.webware.mgas.fragments.dialogs.WaitDialogFragment;
import om.webware.mgas.tools.DatabaseHelper;

public class OrdersRecyclerAdapter extends RecyclerView.Adapter<OrdersRecyclerAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Order> orders;
    private OnItemClickListener onItemClickListener;

    private boolean rtl;
    private WaitDialogFragment waitDialogFragment;

    public interface OnItemClickListener { void onItemClick(View view, int index); }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView textViewLocation;
        private TextView textViewStatus;
        private TextView textViewDate;
        private TextView textViewItems;
        private TextView textViewCost;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewLocation = itemView.findViewById(R.id.textViewLocation);
            textViewStatus = itemView.findViewById(R.id.textViewStatus);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewItems = itemView.findViewById(R.id.textViewItems);
            textViewCost = itemView.findViewById(R.id.textViewCost);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(onItemClickListener != null) {
                onItemClickListener.onItemClick(v, getAdapterPosition());
            }
        }

        public TextView getTextViewLocation() {
            return textViewLocation;
        }

        public TextView getTextViewStatus() {
            return textViewStatus;
        }

        public TextView getTextViewDate() {
            return textViewDate;
        }

        public TextView getTextViewItems() {
            return textViewItems;
        }

        public TextView getTextViewCost() {
            return textViewCost;
        }
    }

    public OrdersRecyclerAdapter(Context context, ArrayList<Order> orders) {
        this.context = context;
        this.orders = orders;

        rtl = context.getResources().getConfiguration().getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public OrdersRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.recycler_orders_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrdersRecyclerAdapter.ViewHolder viewHolder, int i) {
        Order order = orders.get(i);
        DatabaseHelper helper = new DatabaseHelper(context);

        String locationWhere = "where id='" + order.getLocationId() + "'";
        Locations locations = (Locations)helper.select(DatabaseHelper.Tables.LOCATIONS, locationWhere);
        Location location = locations.getLocations().get(0);

        String whereServices = "where orderId='" + order.getId() + "'";
        OrderServices orderServices = (OrderServices)helper.select(DatabaseHelper.Tables.ORDER_SERVICES, whereServices);

        String whereFeedback = "where orderId='" + order.getId() + "'";
        Feedbacks feedbacks = (Feedbacks)helper.select(DatabaseHelper.Tables.FEEDBACK, whereFeedback);

        order.setServices(orderServices);
        order.setFeedbacks(feedbacks);

        String address = location.getCity() + ", " + location.getProvince() + ", " + location.getGovernorate()
                + ", " + location.getCountry();
        viewHolder.getTextViewLocation().setText(address);

        String status = order.getStatus().toUpperCase().substring(0, 1) + order.getStatus().substring(1);
        viewHolder.getTextViewStatus().setText(status);

        if(rtl) {
            viewHolder.getTextViewStatus().setText(order.getArabicStatus());
        } else {
            viewHolder.getTextViewStatus().setText(status);
        }

        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(order.getOrderDate());
        viewHolder.getTextViewDate().setText(date);

        ArrayList<String> tokens = new ArrayList<>();
        for(OrderService orderService : order.getServices().getOrderServices()) {
            String where = "where id='" + orderService.getServiceId() + "'";
            Services services = (Services)helper.select(DatabaseHelper.Tables.SERVICES, where);
            Service service = services.getServices().get(0);
            String item = orderService.getQuantity() + context.getString(R.string.multiple) + " ";

            if(rtl) {
                item += service.getArabicType() + " " + service.getArabicCylinderSize();
            } else {
                item += service.getCylinderSize() + " " + service.getType();
            }
            tokens.add(item);
        }

        String stairs = context.getString(R.string.stairs);
        if(order.isClimbStairs()) {
            stairs += " " + context.getString(R.string.yes);
        } else {
            stairs += " " + context.getString(R.string.no);
        }
        tokens.add(stairs);

        String deliveryOptionWhere = "where id='" + order.getDeliveryOptionId() + "'";
        Services deliveryOptionServices = (Services)helper.select(DatabaseHelper.Tables.SERVICES, deliveryOptionWhere);
        Service deliveryOption = deliveryOptionServices.getServices().get(0);

        if(rtl) {
            tokens.add(deliveryOption.getArabicType());
        } else {
            tokens.add(deliveryOption.getType());
        }

        String items = TextUtils.join(", ", tokens);
        viewHolder.getTextViewItems().setText(items);

        DecimalFormat format = (DecimalFormat)DecimalFormat.getInstance(Locale.US);
        format.applyPattern("0.000");
        String cost = format.format(order.getTotalCost()) + " " + context.getString(R.string.OMR);
        viewHolder.textViewCost.setText(cost);
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }
}
