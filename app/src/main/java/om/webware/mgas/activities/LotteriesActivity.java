package om.webware.mgas.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.nio.charset.StandardCharsets;

import om.webware.mgas.R;
import om.webware.mgas.adapters.LotteriesRecyclerAdapter;
import om.webware.mgas.api.Lotteries;
import om.webware.mgas.fragments.dialogs.ErrorDialogFragment;
import om.webware.mgas.fragments.dialogs.PromoCodeDialogFragment;
import om.webware.mgas.fragments.dialogs.WaitDialogFragment;
import om.webware.mgas.server.MGasApi;
import om.webware.mgas.server.Server;

public class LotteriesActivity extends ConsumerDrawerBaseActivity implements LotteriesRecyclerAdapter.OnItemClickListener {

    private LinearLayout linearLayoutLotteries;
    private TextView textViewNoLotteries;
    private RecyclerView recyclerViewLotteries;

    private WaitDialogFragment waitDialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lotteries);

        linearLayoutLotteries = findViewById(R.id.linearLayoutLotteries);
        textViewNoLotteries = findViewById(R.id.textViewNoLotteries);
        recyclerViewLotteries = findViewById(R.id.recyclerViewLotteries);

        waitDialogFragment = WaitDialogFragment.createDialog();
        waitDialogFragment.setCancelable(false);
        waitDialogFragment.show(getSupportFragmentManager(), "WAIT_DIALOG");

        if(Server.getNetworkAvailability(this)) {
            makeRequest();
        } else {
            waitDialogFragment.dismiss();
            String msg = getString(R.string.no_network_connection_available);
            ErrorDialogFragment error = ErrorDialogFragment.creteDialog(msg);
            error.setCancelable(false);
            error.show(getSupportFragmentManager(), "NETWORK_ERROR_DIALOG");
        }
    }

    private void makeRequest() {
        Server.request(LotteriesActivity.this, Request.Method.GET, MGasApi.ACTIVE_LOTTERIES,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.startsWith(MGasApi.IISNODE_ERROR)) {
                            makeRequest();
                        } else {
                            Lotteries lotteries = new Lotteries(response);

                            if(lotteries.getLotteries().size() > 0) {
                                LotteriesRecyclerAdapter adapter =
                                        new LotteriesRecyclerAdapter(lotteries.getLotteries());
                                adapter.setOnItemClickListener(LotteriesActivity.this);
                                RecyclerView.LayoutManager manager =
                                        new LinearLayoutManager(LotteriesActivity.this);

                                recyclerViewLotteries.setAdapter(adapter);
                                recyclerViewLotteries.setLayoutManager(manager);
                            } else {
                                linearLayoutLotteries.setGravity(Gravity.CENTER);
                                textViewNoLotteries.setVisibility(View.VISIBLE);
                            }

                            waitDialogFragment.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String err = new String(error.networkResponse.data, StandardCharsets.UTF_8);

                        waitDialogFragment.dismiss();

                        ErrorDialogFragment fragment = ErrorDialogFragment.creteDialog(err);
                        fragment.setCancelable(false);
                        fragment.show(getSupportFragmentManager(), "ERROR_DIALOG");
                    }
                });
    }

    @Override
    public void onItemClick(View view, int index) {
        PromoCodeDialogFragment fragment = PromoCodeDialogFragment.createDialog();
        fragment.show(getSupportFragmentManager(), "LOTTERY_PARTICIPATE_DIALOG");
    }
}
