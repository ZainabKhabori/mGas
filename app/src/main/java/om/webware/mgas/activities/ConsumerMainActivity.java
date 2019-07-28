package om.webware.mgas.activities;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.rd.PageIndicatorView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import om.webware.mgas.R;
import om.webware.mgas.adapters.MakeOrderPagerAdapter;
import om.webware.mgas.api.Consumer;
import om.webware.mgas.api.Location;
import om.webware.mgas.api.Locations;
import om.webware.mgas.api.Order;
import om.webware.mgas.api.OrderService;
import om.webware.mgas.api.OrderServices;
import om.webware.mgas.api.Service;
import om.webware.mgas.api.Services;
import om.webware.mgas.api.User;
import om.webware.mgas.fragments.dialogs.AlertDialogFragment;
import om.webware.mgas.fragments.pager.MakeOrderPagerFragment;
import om.webware.mgas.server.MGasSocket;
import om.webware.mgas.tools.DatabaseHelper;
import om.webware.mgas.tools.DatabaseKeys;
import om.webware.mgas.tools.RouteMapper;
import om.webware.mgas.tools.SavedObjects;

public class ConsumerMainActivity extends ConsumerDrawerBaseActivity implements OnMapReadyCallback,
        MakeOrderPagerFragment.OnTabClickedListener, ViewPager.OnPageChangeListener,
        MakeOrderPagerFragment.OnOptionChangedListener, CartActivity.OnItemDeletedListener,
        RouteMapper.RouteMappingCompleteListener, AdapterView.OnItemSelectedListener {

    private ViewPager viewPagerMakeOrder;
    private PageIndicatorView indicatorMakeOrder;
    private TextView textViewMakeOrderToggle;
    private RelativeLayout relativeLayoutMakeOrder;
    private Button buttonAddToCart;
    private Button buttonViewCart;
    private ImageView imageViewShrink;

    private MakeOrderPagerAdapter adapter;

    private GoogleMap map;
    private HashMap<String, Marker> markers;
    private ArrayList<String> busyDrivers;
    private Marker currentLoc;

    private Socket socket;

    private DatabaseHelper helper;

    private String type;
    private String size;
    private String deliveryMethod;
    private boolean climbStairs;
    private int quantity;
    private Order order;
    private double itemCost;
    private double totalCost;
    private ArrayList<Service> services;
    private ArrayList<OrderService> orderServices;
    private ArrayList<String> orderServiceIds;

    private Spinner spinnerLocations;
    private ArrayAdapter spinnerAdapter;

    private User user;
    private Locations locations;

    private boolean methodChanged = false;
    private boolean stairsChanged = false;
    private String prevMethod;

    public static final double CLIMB_STAIRS_CHARGE = 2.000;
    public static int START_HEIGHT;
    public static int FINAL_HEIGHT;
    public static Object ACTIVITY;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout frameLayoutActivityContent = findViewById(R.id.frameLayoutActivityContent);
        getLayoutInflater().inflate(R.layout.activity_consumer_main, frameLayoutActivityContent);

        ACTIVITY = this;

        viewPagerMakeOrder = findViewById(R.id.viewPagerMakeOrder);
        indicatorMakeOrder = findViewById(R.id.pageIndicatorViewMakeOrder);
        textViewMakeOrderToggle = findViewById(R.id.textViewMakeOrderToggle);
        relativeLayoutMakeOrder = findViewById(R.id.relativeLayoutMakeOrder);
        buttonAddToCart = findViewById(R.id.buttonAddToCart);
        buttonViewCart = findViewById(R.id.buttonViewCart);
        imageViewShrink = findViewById(R.id.imageViewShrink);

        spinnerLocations = findViewById(R.id.spinnerLocations);

        helper = new DatabaseHelper(this);
        locations = (Locations)helper.select(DatabaseHelper.Tables.LOCATIONS, null);

        ArrayList<String> items = new ArrayList<>();

        for(Location location : locations.getLocations()) {
            items.add(location.getLocationName() + " (" + location.getCity() + ")");
        }

        spinnerAdapter = new ArrayAdapter(this, R.layout.spinner_locations_selected_item, items);
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_locations_dropdown_item);

        totalCost = 0;
        orderServices = new ArrayList<>();
        orderServiceIds = new ArrayList<>();

        final boolean rtl = getResources().getConfiguration().getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;

        adapter = new MakeOrderPagerAdapter(getSupportFragmentManager(), this);
        viewPagerMakeOrder.setAdapter(adapter);
        viewPagerMakeOrder.addOnPageChangeListener(this);
        indicatorMakeOrder.setViewPager(viewPagerMakeOrder);

        if(SavedObjects.getSavedObjects().containsKey("NEW_ORDER")) {
            order = (Order)SavedObjects.getSavedObjects().get("NEW_ORDER");
        } else {
            order = new Order();
        }

        user = (User)helper.select(DatabaseHelper.Tables.USERS, null);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        markers = new HashMap<>();
        busyDrivers = new ArrayList<>();
    }

    @Override
    protected void onResume() {
        super.onResume();

        socket = MGasSocket.socket;
        socket.on("updatedLocation", updatedLocation);
        socket.on("changeDriverStatus", changeDriverStatus);
        socket.emit("clientOnline", user.getId());
    }

    @Override
    protected void onPause() {
        super.onPause();

        socket.off();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the User will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the User has
     * installed Google Play services and returned to the app.
     */
    @SuppressWarnings("unchecked")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMinZoomPreference(8);

        spinnerLocations.setAdapter(spinnerAdapter);

        Consumer consumer = (Consumer)helper.select(DatabaseHelper.Tables.CONSUMERS, null);
        String where = "where id='" + consumer.getMainLocationId() + "'";
        Locations main = (Locations)helper.select(DatabaseHelper.Tables.LOCATIONS, where);

        Location mainLoc = main.getLocations().get(0);
        String mainLocItem = mainLoc.getLocationName() + " (" + mainLoc.getCity() + ")";

        int mainIndex = spinnerAdapter.getPosition(mainLocItem);
        spinnerLocations.setSelection(mainIndex);

        spinnerLocations.setOnItemSelectedListener(this);

        LatLng latLng = new LatLng(mainLoc.getLatitude(), mainLoc.getLongitude());
        currentLoc = map.addMarker(new MarkerOptions().position(latLng));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 19));

/*        Consumer consumer = (Consumer)helper.select(DatabaseHelper.Tables.CONSUMERS, null);

        String where = "where " + DatabaseKeys.FIELD_ID + "='" + consumer.getMainLocationId() + "'";
        Locations locations = (Locations)helper.select(DatabaseHelper.Tables.LOCATIONS, where);

        Location location = locations.getLocations().get(0);
        LatLng dest = new LatLng(location.getLatitude(), location.getLongitude());

        map.addMarker(new MarkerOptions().position(dest));

        MarkerOptions options = new MarkerOptions();
        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
        map.addMarker(options.position(latLng));

        RouteMapper mapper = new RouteMapper(this, map, latLng, dest);
        mapper.mapRoutes();*/
    }

    @Override
    public void onTabClicked(int index) {
        viewPagerMakeOrder.setCurrentItem(index, true);
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {
        //
    }

    @Override
    public void onPageSelected(int i) {
        if(i == 3) {
            buttonAddToCart.setVisibility(View.GONE);
            buttonViewCart.setVisibility(View.VISIBLE);

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)imageViewShrink.getLayoutParams();
            params.addRule(RelativeLayout.BELOW, buttonViewCart.getId());
            imageViewShrink.setLayoutParams(params);

            MakeOrderPagerFragment fragment = adapter.getCurrentFragment();
            String formattedItemCost = getString(R.string.item_cost) + " " + new DecimalFormat("0.000")
                    .format(itemCost) + " " + getString(R.string.OMR);
            fragment.getTextViewItemCost().setText(formattedItemCost);

            String formattedTotalCost = getString(R.string.total_cost) + " " + new DecimalFormat("0.000")
                    .format(totalCost) + " " + getString(R.string.OMR);
            fragment.getTextViewTotalCost().setText(formattedTotalCost);
        } else {
            buttonAddToCart.setVisibility(View.VISIBLE);
            buttonViewCart.setVisibility(View.GONE);

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)imageViewShrink.getLayoutParams();
            params.addRule(RelativeLayout.BELOW, buttonAddToCart.getId());
            imageViewShrink.setLayoutParams(params);
        }
    }

    @Override
    public void onPageScrollStateChanged(int i) {
        //
    }

    @Override
    public void onOptionChanged(String option, Object value) {
        itemCost = 0;

        switch (option) {
            case "type":
                type = (String) value;
                break;
            case "size":
                size = (String) value;
                break;
            case "deliveryMethod":
                if(deliveryMethod != null && !deliveryMethod.equals(value) && !orderServices.isEmpty()) {
                    methodChanged = true;
                    prevMethod = deliveryMethod;
                }
                deliveryMethod = (String) value;
                break;
            case "quantity":
                quantity = (int) value;
                break;
            default:
                if(!orderServices.isEmpty() && climbStairs != (boolean) value) {
                    stairsChanged = true;
                }
                climbStairs = (boolean) value;
                order.setClimbStairs(climbStairs);
                break;
        }

        if(type != null && size != null && deliveryMethod != null && quantity > 0) {
            services = getServices();
            itemCost = calculateItemCost();

            order.setDeliveryOptionId(services.get(1).getId());
            order.setClimbStairs(climbStairs);
        }
    }

    @Override
    public void onRouteMappingComplete(ArrayList<LatLng> points) {
        MarkerOptions options = new MarkerOptions();
        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        for(LatLng position : points) {
            map.addMarker(options.position(position));
        }
    }

    @Override
    public void onItemDeleted(int index, double totalCost) {
        orderServices.remove(index);
        orderServiceIds.remove(index);
        this.totalCost = totalCost;

        String json = new Gson().toJson(orderServices);
        order.setServices(new OrderServices(json));
        order.setTotalCost(this.totalCost);

        if(orderServices.isEmpty()) {
            itemCost = calculateItemCost();
        }

        onPageSelected(3);
    }

    @SuppressWarnings({"unchecked", "ConstantConditions"})
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == 1 && resultCode == RESULT_OK) {
            String item = data.getStringExtra("ITEM");
            spinnerAdapter.add(item);
            spinnerAdapter.notifyDataSetChanged();

            int index = spinnerAdapter.getPosition(item);
            spinnerLocations.setSelection(index);

            Location location = new Gson().fromJson(data.getStringExtra("LOCATION_JSON"), Location.class);
            locations.getLocations().add(location);
        } else if(requestCode == 2 && resultCode == RESULT_OK) {
            totalCost = 0;
            orderServices.clear();
            orderServiceIds.clear();
            shrinkMakeOrderDrawer();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Location location = locations.getLocations().get(position);

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        currentLoc.remove();
        currentLoc = map.addMarker(new MarkerOptions().position(latLng));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 19));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //
    }

    public void expandOrdersLayoutAction(View view) {
        START_HEIGHT = relativeLayoutMakeOrder.getHeight();

        textViewMakeOrderToggle.setVisibility(View.GONE);
        Drawable background = ContextCompat.getDrawable(this, R.drawable.round_top_edges_background);
        relativeLayoutMakeOrder.setBackground(background);

        viewPagerMakeOrder.setVisibility(View.INVISIBLE);
        indicatorMakeOrder.setVisibility(View.INVISIBLE);
        if(viewPagerMakeOrder.getCurrentItem() == 3) {
            buttonViewCart.setVisibility(View.INVISIBLE);
        } else {
            buttonAddToCart.setVisibility(View.INVISIBLE);
        }
        imageViewShrink.setVisibility(View.INVISIBLE);

        int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(START_HEIGHT, View.MeasureSpec.UNSPECIFIED);
        relativeLayoutMakeOrder.measure(widthSpec, heightSpec);

        FINAL_HEIGHT = relativeLayoutMakeOrder.getMeasuredHeight();

        viewPagerMakeOrder.setVisibility(View.GONE);
        indicatorMakeOrder.setVisibility(View.GONE);
        if(viewPagerMakeOrder.getCurrentItem() == 3) {
            buttonViewCart.setVisibility(View.GONE);
        } else {
            buttonAddToCart.setVisibility(View.GONE);
        }
        imageViewShrink.setVisibility(View.GONE);

        ValueAnimator animator = ValueAnimator.ofInt(START_HEIGHT, FINAL_HEIGHT);
        animator.addUpdateListener(expandAnimation);
        animator.start();
    }

    public void shrinkMakeOrderLayoutAction(View view) {
        shrinkMakeOrderDrawer();
    }

    public void addToCartAction(View view) {
        if(user.getUserType().equals("guest")) {
            String title = getString(R.string.you_are_a_guest);
            String body = getString(R.string.guest_not_allowed_action);

            AlertDialogFragment fragment = AlertDialogFragment.createDialog(title, body);
            fragment.show(getSupportFragmentManager(), "NOT_ALLOWED_ACTION_ALERT");
        } else {
            totalCost += itemCost;

            if(orderServices.isEmpty()) {
                itemCost -= services.get(1).getCharge();
                if(climbStairs) {
                    itemCost -= CLIMB_STAIRS_CHARGE;
                }
            }

            OrderService orderService = new OrderService(null, services.get(0).getId(), quantity);

            if(!orderServiceIds.contains(orderService.getServiceId())) {
                orderServiceIds.add(orderService.getServiceId());
                orderServices.add(orderService);
            } else {
                for(OrderService service : orderServices) {
                    if(service.getServiceId().equals(orderService.getServiceId())) {
                        service.setQuantity(service.getQuantity() + 1);
                    }
                }
            }

            String json = new Gson().toJson(orderServices);
            order.setServices(new OrderServices(json));

            order.setTotalCost(totalCost);
            order.setClimbStairs(climbStairs);
            Toast.makeText(this, "Item added to cart", Toast.LENGTH_LONG).show();
        }
    }

    public void viewCartAction(View view) {
        if(user.getUserType().equals("guest")) {
            String title = getString(R.string.you_are_a_guest);
            String body = getString(R.string.guest_not_allowed_action);

            AlertDialogFragment fragment = AlertDialogFragment.createDialog(title, body);
            fragment.show(getSupportFragmentManager(), "NOT_ALLOWED_ACTION_ALERT");
        } else {
            if(order.getServices() == null) {
                ArrayList<OrderServices> services = new ArrayList<>();
                order.setServices(new OrderServices(new Gson().toJson(services)));
            }

            order.setConsumerId(user.getId());

            String orderJson = new Gson().toJson(order);
            SavedObjects.getSavedObjects().put("NEW_ORDER", order);

            Intent intent = new Intent(this, CartActivity.class);
            intent.putExtra("ORDER", orderJson);
            intent.putExtra("DELIVERY_LOC", (String)spinnerLocations.getSelectedItem());
            startActivityForResult(intent, 2);
        }
    }

    public void addLocationAction(View view) {
        Intent intent = new Intent(this, AddLocationActivity.class);
        startActivityForResult(intent, 1);
    }

    private void shrinkMakeOrderDrawer() {
        viewPagerMakeOrder.setVisibility(View.GONE);
        indicatorMakeOrder.setVisibility(View.GONE);
        buttonAddToCart.setVisibility(View.GONE);
        buttonViewCart.setVisibility(View.GONE);
        imageViewShrink.setVisibility(View.GONE);

        ValueAnimator animator = ValueAnimator.ofInt(FINAL_HEIGHT, START_HEIGHT);
        animator.addUpdateListener(shrinkAnimation);
        animator.start();
    }

    private ArrayList<Service> getServices() {
        String typeSize = "where " + DatabaseKeys.FIELD_TYPE + "='" + type + "' and " +
                DatabaseKeys.FIELD_CYLINDER_SIZE + "='" + size + "' order by " +
                DatabaseKeys.FIELD_DATE_MODIFIED + " desc";

        String method = "where " + DatabaseKeys.FIELD_TYPE + "='" + deliveryMethod + "' order by " +
                DatabaseKeys.FIELD_DATE_MODIFIED + " desc";

        Services serviceTypeCylinder = (Services)helper.select(DatabaseHelper.Tables.SERVICES, typeSize);
        Services serviceMethod = (Services)helper.select(DatabaseHelper.Tables.SERVICES, method);

        ArrayList<Service> fromDb = new ArrayList<>();

        fromDb.add(serviceTypeCylinder.getServices().get(0));
        fromDb.add(serviceMethod.getServices().get(0));

        if(methodChanged) {
            String prevMethod = "where " + DatabaseKeys.FIELD_TYPE + "='" + this.prevMethod + "' order by " +
                    DatabaseKeys.FIELD_DATE_MODIFIED + " desc";

            Services prevMethodData = (Services)helper.select(DatabaseHelper.Tables.SERVICES, prevMethod);
            fromDb.add(prevMethodData.getServices().get(0));
        }

        return fromDb;
    }

    private double calculateItemCost() {
        double cost = services.get(0).getCharge() * quantity;

        if(orderServices.isEmpty()) {
            cost += services.get(1).getCharge();
        }

        if(methodChanged) {
            totalCost -= services.get(2).getCharge();
            totalCost += services.get(1).getCharge();
            order.setTotalCost(totalCost);
        }

        if(orderServices.isEmpty() && climbStairs) {
            cost += CLIMB_STAIRS_CHARGE;
        }

        if(stairsChanged) {
            if(climbStairs) {
                totalCost += CLIMB_STAIRS_CHARGE;
            } else {
                totalCost -= CLIMB_STAIRS_CHARGE;
            }

            stairsChanged = false;
            order.setTotalCost(totalCost);
        }

        return cost;
    }

    private ValueAnimator.AnimatorUpdateListener expandAnimation = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            int  value = (int)animation.getAnimatedValue();
            ViewGroup.LayoutParams params = relativeLayoutMakeOrder.getLayoutParams();
            params.height = value;
            relativeLayoutMakeOrder.setLayoutParams(params);

            AnimatorSet set = new AnimatorSet();

            if(value == FINAL_HEIGHT) {
                viewPagerMakeOrder.setVisibility(View.VISIBLE);
                indicatorMakeOrder.setVisibility(View.VISIBLE);

                ObjectAnimator viewPagerVisible = ObjectAnimator.ofFloat(viewPagerMakeOrder, View.ALPHA, 0, 1);
                ObjectAnimator indicatorVisible = ObjectAnimator.ofFloat(indicatorMakeOrder, View.ALPHA, 0, 1);

                ObjectAnimator buttonVisible;
                if(viewPagerMakeOrder.getCurrentItem() == 3) {
                    buttonViewCart.setVisibility(View.VISIBLE);
                    buttonVisible = ObjectAnimator.ofFloat(buttonViewCart, View.ALPHA, 0, 1);
                } else {
                    buttonAddToCart.setVisibility(View.VISIBLE);
                    buttonVisible = ObjectAnimator.ofFloat(buttonAddToCart, View.ALPHA, 0, 1);
                }

                imageViewShrink.setVisibility(View.VISIBLE);
                ObjectAnimator imageViewVisible = ObjectAnimator.ofFloat(imageViewShrink, View.ALPHA, 0, 1);

                set.playTogether(viewPagerVisible, indicatorVisible, buttonVisible, imageViewVisible);
                set.start();
            }
        }
    };

    private ValueAnimator.AnimatorUpdateListener shrinkAnimation = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            int  value = (int)animation.getAnimatedValue();
            ViewGroup.LayoutParams params = relativeLayoutMakeOrder.getLayoutParams();
            params.height = value;
            relativeLayoutMakeOrder.setLayoutParams(params);

            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(textViewMakeOrderToggle, View.ALPHA, 0, 1);

            if(value == START_HEIGHT) {
                textViewMakeOrderToggle.setVisibility(View.VISIBLE);
                relativeLayoutMakeOrder.setBackground(ContextCompat.getDrawable(ConsumerMainActivity.this,
                        R.drawable.round_top_edges_background_solid));

                objectAnimator.start();
            }
        }
    };

    private Emitter.Listener updatedLocation = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JsonObject json = new Gson().fromJson(args[0].toString(), JsonObject.class);

                    String driverId = json.get("driverId").getAsString();
                    double lat = json.get("lat").getAsDouble();
                    double lng = json.get("lng").getAsDouble();

                    Marker marker = markers.get(driverId);

                    if(marker != null) {
                        marker.remove();
                    }

                    LatLng latLng = new LatLng(lat, lng);
                    MarkerOptions options = new MarkerOptions();
                    options.position(latLng);

                    if(busyDrivers.contains(driverId)) {
                        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    } else {
                        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                    }

                    marker = map.addMarker(options);
                    markers.put(driverId, marker);
                }
            });
        }
    };

    private Emitter.Listener changeDriverStatus = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JsonObject json = new Gson().fromJson(args[0].toString(), JsonObject.class);

                    String driverId = json.get("driverId").getAsString();
                    String status = json.get("status").getAsString();

                    switch (status) {
                        case "busy":
                            busyDrivers.add(driverId);
                            break;
                        case "online":
                            busyDrivers.remove(driverId);
                            break;
                    }
                }
            });
        }
    };
}
