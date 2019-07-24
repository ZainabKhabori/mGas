package om.webware.mgas.fragments.pager;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import om.webware.mgas.R;
import om.webware.mgas.adapters.OrderOptionsRecyclerAdapter;
import om.webware.mgas.api.Service;
import om.webware.mgas.api.Services;
import om.webware.mgas.tools.DatabaseHelper;
import om.webware.mgas.tools.SavedObjects;

/**
 * Created by Zainab on 6/23/2019.
 */

public class MakeOrderPagerFragment extends Fragment implements View.OnClickListener,
        OrderOptionsRecyclerAdapter.OnItemClickListener {

    private Context context;

    private LinearLayout linearLayoutTabs;
    private TextView textViewServiceType;
    private TextView textViewCylinderType;
    private TextView textViewDeliveryOptions;
    private TextView textViewConfirm;
    private TextView textViewLabel;
    private TextView textViewItemCost;
    private TextView textViewTotalCost;
    private RecyclerView recyclerViewOrderOptions;
    private OrderOptionsRecyclerAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private EditText editTextQuantity;
    private ImageView imageViewAdd;
    private ImageView imageViewMinus;
    private CheckBox checkBoxClimbStairs;

    private String tab;

    private ArrayList<String> names;
    private ArrayList<String> arabicNames;
    private ArrayList<String> sizes;
    private ArrayList<String> arabicSizes;
    private ArrayList<String> deliveryMethods;
    private ArrayList<String> arabicDeliveryMethods;

    private OnTabClickedListener onTabClickedListener;
    private OnOptionChangedListener onOptionChangedListener;
    private ArrayList<Integer> tabs;

    public interface OnTabClickedListener { void onTabClicked(int index); }
    public interface OnOptionChangedListener { void onOptionChanged(String option, Object value); }

    public MakeOrderPagerFragment() {
        //
    }

    public static MakeOrderPagerFragment createFragment(String tab) {
        MakeOrderPagerFragment fragment = new MakeOrderPagerFragment();
        Bundle args = new Bundle();
        args.putString("TAB", tab);
        fragment.setArguments(args);
        return fragment;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pager_make_order, container, false);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tab = getArguments().getString("TAB");

        linearLayoutTabs = view.findViewById(R.id.linearLayoutTabs);
        textViewServiceType = view.findViewById(R.id.textViewServiceType);
        textViewCylinderType = view.findViewById(R.id.textViewCylinderType);
        textViewDeliveryOptions = view.findViewById(R.id.textViewDeliveryOptions);
        textViewConfirm = view.findViewById(R.id.textViewConfirm);
        textViewLabel = view.findViewById(R.id.textViewLabel);
        textViewItemCost = view.findViewById(R.id.textViewItemCost);
        textViewTotalCost = view.findViewById(R.id.textViewTotalCost);
        recyclerViewOrderOptions = view.findViewById(R.id.recyclerViewOrderOptions);
        editTextQuantity = view.findViewById(R.id.editTextQuantity);
        imageViewAdd = view.findViewById(R.id.imageViewAdd);
        imageViewMinus = view.findViewById(R.id.imageViewMinus);
        checkBoxClimbStairs = view.findViewById(R.id.checkBoxClimbStairs);

        LinearLayout linearLayoutSecond = view.findViewById(R.id.linearLayoutSecond);
        LinearLayout linearLayoutQuantity = view.findViewById(R.id.linearLayoutQuantity);
        LinearLayout linearLayoutCost = view.findViewById(R.id.linearLayoutCost);
        TextView textViewLabelSecond = view.findViewById(R.id.textViewLabelSecond);

        onTabClickedListener = (OnTabClickedListener)context;
        onOptionChangedListener = (OnOptionChangedListener)context;
        tabs = new ArrayList<>();

        for(int i = 0; i < linearLayoutTabs.getChildCount(); i++) {
            tabs.add(i, linearLayoutTabs.getChildAt(i).getId());
            linearLayoutTabs.getChildAt(i).setOnClickListener(this);
        }

        if(!tab.equals("confirmOrder")) {
            names = new ArrayList<>();
            arabicNames = new ArrayList<>();
            sizes = new ArrayList<>();
            arabicSizes = new ArrayList<>();
            deliveryMethods = new ArrayList<>();
            arabicDeliveryMethods = new ArrayList<>();

            boolean rtl = getResources().getConfiguration().getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;

            DatabaseHelper helper = new DatabaseHelper(context);
            Services fromDb = (Services)helper.select(DatabaseHelper.Tables.SERVICES, null);

            for(Service service : fromDb.getServices()) {
                if(service.getType().toLowerCase().contains("within")) {
                    if(!deliveryMethods.contains(service.getType())) {
                        deliveryMethods.add(service.getType());
                        arabicDeliveryMethods.add(service.getArabicType());
                    }
                } else {
                    if(!names.contains(service.getType())) {
                        names.add(service.getType());
                        arabicNames.add(service.getArabicType());
                    }
                }

                if(service.getCylinderSize() != null && !sizes.contains(service.getCylinderSize())) {
                    sizes.add(service.getCylinderSize());
                    arabicSizes.add(service.getArabicCylinderSize());
                }
            }

            int indexType = SavedObjects.getSavedObjects().containsKey("SELECTED_TYPE")?
                    (int)SavedObjects.getSavedObjects().get("SELECTED_TYPE") : 0;

            int indexSize = SavedObjects.getSavedObjects().containsKey("SELECTED_SIZE")?
                    (int)SavedObjects.getSavedObjects().get("SELECTED_SIZE") : 0;

            int indexMethod = SavedObjects.getSavedObjects().containsKey("SELECTED_METHOD")?
                    (int)SavedObjects.getSavedObjects().get("SELECTED_METHOD") : 0;

            boolean climbStairs = SavedObjects.getSavedObjects().containsKey("SELECTED_STAIRS")?
                    (boolean)SavedObjects.getSavedObjects().get("SELECTED_STAIRS"):
                    checkBoxClimbStairs.isChecked();

            int quantity = SavedObjects.getSavedObjects().containsKey("SELECTED_QUANTITY")?
                    (int)SavedObjects.getSavedObjects().get("SELECTED_QUANTITY"):
                    Integer.parseInt(editTextQuantity.getText().toString());

            onOptionChangedListener.onOptionChanged("type", names.get(indexType));
            onOptionChangedListener.onOptionChanged("size", sizes.get(indexSize));
            onOptionChangedListener.onOptionChanged("deliveryMethod", deliveryMethods.get(indexMethod));
            onOptionChangedListener.onOptionChanged("climbStairs", climbStairs);
            onOptionChangedListener.onOptionChanged("quantity", quantity);

            if(tab.equals("serviceType")) {
                String text = getString(R.string.service_type) + ":";
                textViewLabel.setText(text);
                ArrayList<String> recyclerNames = rtl ? arabicNames : names;
                adapter = new OrderOptionsRecyclerAdapter(recyclerNames, context, indexType);

                textViewServiceType.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.colorAccent));
            } else if(tab.equals("cylinderSize")) {
                String text = getString(R.string.cylinder_type) + ":";
                textViewLabel.setText(text);
                ArrayList<String> recyclerSizes = rtl ? arabicSizes : sizes;
                adapter = new OrderOptionsRecyclerAdapter(recyclerSizes, context, indexSize);

                textViewCylinderType.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.colorAccent));
                textViewLabelSecond.setText(getString(R.string.quantity));
                linearLayoutSecond.setVisibility(View.VISIBLE);
                linearLayoutQuantity.setVisibility(View.VISIBLE);
                imageViewAdd.setOnClickListener(this);
                imageViewMinus.setOnClickListener(this);

                if(quantity > 1) {
                    imageViewMinus.setVisibility(View.VISIBLE);
                }
            } else {
                String text = getString(R.string.delivery_options) + ":";
                textViewLabel.setText(text);
                ArrayList<String> recyclerMethods = rtl ? arabicDeliveryMethods : deliveryMethods;
                adapter = new OrderOptionsRecyclerAdapter(recyclerMethods, context, indexMethod);

                textViewDeliveryOptions.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.colorAccent));
                textViewLabelSecond.setText(getString(R.string.climb_stairs));
                linearLayoutSecond.setVisibility(View.VISIBLE);
                checkBoxClimbStairs.setVisibility(View.VISIBLE);
                checkBoxClimbStairs.setOnClickListener(this);
            }

            adapter.setOnItemClickListener(this);
            layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            recyclerViewOrderOptions.setAdapter(adapter);
            recyclerViewOrderOptions.setLayoutManager(layoutManager);
        } else {
            textViewConfirm.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.colorAccent));
            recyclerViewOrderOptions.setVisibility(View.GONE);
            linearLayoutCost.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        if(tabs.contains(v.getId())) {
            onTabClickedListener.onTabClicked(tabs.indexOf(v.getId()));
        } else if(v.getId() == checkBoxClimbStairs.getId()) {
            onOptionChangedListener.onOptionChanged("climbStairs", checkBoxClimbStairs.isChecked());
            SavedObjects.getSavedObjects().put("SELECTED_STAIRS", checkBoxClimbStairs.isChecked());
        } else if(v.getId() == imageViewAdd.getId()) {
            int quantity = Integer.parseInt(editTextQuantity.getText().toString());
            quantity++;
            editTextQuantity.setText(String.valueOf(quantity));
            if(quantity > 1) {
                imageViewMinus.setVisibility(View.VISIBLE);
            }
            onOptionChangedListener.onOptionChanged("quantity", quantity);
            SavedObjects.getSavedObjects().put("SELECTED_QUANTITY", quantity);
        } else if(v.getId() == imageViewMinus.getId()) {
            int quantity = Integer.parseInt(editTextQuantity.getText().toString());
            quantity--;
            if(quantity <= 1) {
                if(quantity == 1) {
                    editTextQuantity.setText(String.valueOf(quantity));
                }
                imageViewMinus.setVisibility(View.INVISIBLE);
            } else {
                editTextQuantity.setText(String.valueOf(quantity));
            }
            onOptionChangedListener.onOptionChanged("quantity", quantity);
            SavedObjects.getSavedObjects().put("SELECTED_QUANTITY", quantity);
        }
    }

    @Override
    public void onItemClick(View view, int index) {
        if(tab.equals("serviceType")) {
            onOptionChangedListener.onOptionChanged("type", names.get(index));
            SavedObjects.getSavedObjects().put("SELECTED_TYPE", index);
        } else if(tab.equals("cylinderSize")) {
            onOptionChangedListener.onOptionChanged("size", sizes.get(index));
            SavedObjects.getSavedObjects().put("SELECTED_SIZE", index);
        } else {
            onOptionChangedListener.onOptionChanged("deliveryMethod", deliveryMethods.get(index));
            SavedObjects.getSavedObjects().put("SELECTED_METHOD", index);
        }
    }

    public TextView getTextViewTotalCost() {
        return textViewTotalCost;
    }

    public TextView getTextViewItemCost() {
        return textViewItemCost;
    }
}
