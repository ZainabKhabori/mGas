package om.webware.mgas.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

import om.webware.mgas.R;
import om.webware.mgas.api.OrderService;
import om.webware.mgas.api.Service;
import om.webware.mgas.api.Services;
import om.webware.mgas.tools.DatabaseHelper;

public class OrderViewServicesRecyclerAdapter extends RecyclerView.Adapter<OrderViewServicesRecyclerAdapter.ViewHolder> {

    private Context context;
    private ArrayList<OrderService> orderServices;

    private DatabaseHelper helper;
    private boolean rtl;

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewItem;
        private TextView textViewCost;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewItem = itemView.findViewById(R.id.textViewItem);
            textViewCost = itemView.findViewById(R.id.textViewCost);
        }

        TextView getTextViewItem() {
            return textViewItem;
        }

        TextView getTextViewCost() {
            return textViewCost;
        }
    }

    public OrderViewServicesRecyclerAdapter(Context context, ArrayList<OrderService> orderServices) {
        this.context = context;
        this.orderServices = orderServices;

        helper = new DatabaseHelper(context);
        rtl = context.getResources().getConfiguration().getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
    }

    @NonNull
    @Override
    public OrderViewServicesRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.recycler_order_view_service_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewServicesRecyclerAdapter.ViewHolder viewHolder, int i) {
        OrderService orderService = orderServices.get(i);

        String where = "where id='" + orderService.getServiceId() + "'";
        Services services = (Services)helper.select(DatabaseHelper.Tables.SERVICES, where);
        Service service = services.getServices().get(0);

        String item = rtl? service.getArabicType() + ", " + service.getArabicCylinderSize() :
                service.getType() + ", " + service.getCylinderSize();
        item += " " + context.getString(R.string.multiple) + orderService.getQuantity();

        viewHolder.getTextViewItem().setText(item);

        double cost = service.getCharge() * orderService.getQuantity();
        String itemCost = new DecimalFormat("0.000").format(cost) + " " + context.getString(R.string.OMR);

        viewHolder.getTextViewCost().setText(itemCost);
    }

    @Override
    public int getItemCount() {
        return orderServices.size();
    }
}
