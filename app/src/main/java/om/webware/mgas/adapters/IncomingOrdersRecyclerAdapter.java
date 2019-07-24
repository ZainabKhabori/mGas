package om.webware.mgas.adapters;

import android.content.Context;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import om.webware.mgas.R;
import om.webware.mgas.api.Location;
import om.webware.mgas.api.Locations;
import om.webware.mgas.api.Order;
import om.webware.mgas.api.OrderService;
import om.webware.mgas.api.Service;
import om.webware.mgas.api.Services;
import om.webware.mgas.api.User;
import om.webware.mgas.customViews.CircularImageView;
import om.webware.mgas.tools.DatabaseHelper;

public class IncomingOrdersRecyclerAdapter extends RecyclerView.Adapter<IncomingOrdersRecyclerAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Order> orders;
    private ArrayList<User> users;
    private ArrayList<Location> locations;
    private OnItemClickListener onItemClickListener;

    private boolean rtl;

    public interface OnItemClickListener { void onItemClick(View view, int index); }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private CircularImageView imageViewDp;
        private TextView textViewConsumerName;
        private TextView textViewTotalCost;
        private TextView textViewDetails;
        private TextView textViewStatus;
        private TextView textViewDeliveryTime;
        private ImageButton imageButtonLocation;
        private ImageButton imageButtonAccept;

        private CountDownTimer timer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageViewDp = itemView.findViewById(R.id.imageViewDp);
            textViewConsumerName = itemView.findViewById(R.id.textViewConsumerName);
            textViewTotalCost = itemView.findViewById(R.id.textViewTotalCost);
            textViewDetails = itemView.findViewById(R.id.textViewDetails);
            textViewStatus = itemView.findViewById(R.id.textViewStatus);
            textViewDeliveryTime = itemView.findViewById(R.id.textViewDeliveryTime);
            imageButtonLocation = itemView.findViewById(R.id.imageButtonLocation);
            imageButtonAccept = itemView.findViewById(R.id.imageButtonAccept);

            imageButtonLocation.setOnClickListener(this);
            imageButtonAccept.setOnClickListener(this);
        }

        public CircularImageView getImageViewDp() {
            return imageViewDp;
        }

        public TextView getTextViewConsumerName() {
            return textViewConsumerName;
        }

        public TextView getTextViewTotalCost() {
            return textViewTotalCost;
        }

        public TextView getTextViewDetails() {
            return textViewDetails;
        }

        public TextView getTextViewStatus() {
            return textViewStatus;
        }

        public TextView getTextViewDeliveryTime() {
            return textViewDeliveryTime;
        }

        public ImageButton getImageButtonLocation() {
            return imageButtonLocation;
        }

        public ImageButton getImageButtonAccept() {
            return imageButtonAccept;
        }

        public CountDownTimer getTimer() {
            return timer;
        }

        public void setTimer(CountDownTimer timer) {
            this.timer = timer;
        }

        @Override
        public void onClick(View v) {
            if(onItemClickListener != null) {
                onItemClickListener.onItemClick(v, getAdapterPosition());
            }
        }
    }

    public IncomingOrdersRecyclerAdapter(Context context, ArrayList<Order> orders, ArrayList<User> users,
                                         ArrayList<Location> locations) {
        this.context = context;
        this.orders = orders;
        this.users = users;
        this.locations = locations;

        rtl = context.getResources().getConfiguration().getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void updateData(ArrayList<Order> orders, ArrayList<User> users, ArrayList<Location> locations) {
        this.orders = orders;
        this.users = users;
        this.locations = locations;
    }

    @NonNull
    @Override
    public IncomingOrdersRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.recycler_incoming_orders_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final IncomingOrdersRecyclerAdapter.ViewHolder viewHolder, int i) {
        User user = users.get(i);
        Order order = orders.get(i);

        if(user.getDisplayPicThumb() != null && user.getDisplayPicUrl() != null) {
            Picasso.get().load(user.getDisplayPicUrl()).into(viewHolder.getImageViewDp());
        }

        String name = user.getfName() + " " + user.getlName();
        viewHolder.getTextViewConsumerName().setText(name);

        DecimalFormat format = (DecimalFormat)DecimalFormat.getInstance(Locale.US);
        format.applyPattern("0.000");
        String formatted = format.format(order.getTotalCost());
        String totalCost = formatted + " " + context.getString(R.string.OMR);
        viewHolder.getTextViewTotalCost().setText(totalCost);

        DatabaseHelper helper = new DatabaseHelper(context);
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

        String stairs = context.getString(R.string.stairs) + ":";
        if(order.isClimbStairs()) {
            stairs += " " + context.getString(R.string.yes);
        } else {
            stairs += " " + context.getString(R.string.no);
        }
        tokens.add(stairs);

        Location location = locations.get(i);
        String[] address = location.getAddressLine1().split(", ");
        String addressBuilding = address[address.length - 1];
        String building = context.getString(R.string.building) + " " + addressBuilding.split(" ")[0];
        tokens.add(building);

        String details = TextUtils.join(", ", tokens);
        viewHolder.getTextViewDetails().setText(details);

        String status = order.getStatus().substring(0, 1).toUpperCase() + order.getStatus().substring(1);

        if(rtl) {
            viewHolder.getTextViewStatus().setText(order.getArabicStatus());
        } else {
            viewHolder.getTextViewStatus().setText(status);
        }

        String deliveryWhere = "where id='" + order.getDeliveryOptionId() + "'";
        Services services = (Services)helper.select(DatabaseHelper.Tables.SERVICES, deliveryWhere);
        Service option = services.getServices().get(0);

        int hours;
        if(option.getType().toLowerCase().contains("three") || option.getType().contains("3")) {
            hours = 3;
        } else {
            hours = 1;
        }

        long milliseconds = hours * 3600000;
        long difference = order.getOrderDate().getTime() - new Date().getTime();
        long time = milliseconds - difference;

        if(viewHolder.getTimer() != null) {
            viewHolder.getTimer().cancel();
        }

        CountDownTimer timer = new CountDownTimer(time, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long seconds = millisUntilFinished / 1000;
                long minutes = seconds / 60;
                long hours = minutes / 60;

                String hr = String.format(Locale.US, "%02d", (hours % 24));
                String min = String.format(Locale.US, "%02d", (minutes % 60));
                String sec = String.format(Locale.US, "%02d", (seconds % 60));

                String remaining = hr + ":" + min + ":" + sec;

                viewHolder.getTextViewDeliveryTime().setText(remaining);
            }

            @Override
            public void onFinish() {
                viewHolder.getTextViewDeliveryTime().setTextColor(Color.RED);
            }
        }.start();

        viewHolder.setTimer(timer);
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }
}
