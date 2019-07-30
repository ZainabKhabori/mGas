package om.webware.mgas.fragments.dialogs;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import om.webware.mgas.R;
import om.webware.mgas.api.User;
import om.webware.mgas.customViews.PinEntryEditText;
import om.webware.mgas.server.MGasApi;
import om.webware.mgas.server.Server;
import om.webware.mgas.tools.DatabaseHelper;

public class PromoCodeDialogFragment extends DialogFragment implements View.OnClickListener {

    private PinEntryEditText editTextCode;

    private WaitDialogFragment waitDialogFragment;

    public PromoCodeDialogFragment() {
        //
    }

    public static PromoCodeDialogFragment createDialog() {
        return new PromoCodeDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dialog_promo_code, container);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getDialog().getWindow().setBackgroundDrawable(ContextCompat.getDrawable(getContext(),
                R.drawable.round_edge_dialog_background));

        editTextCode = view.findViewById(R.id.editTextCode);
        Button buttonParticipate = view.findViewById(R.id.buttonParticipate);

        editTextCode.requestFocus();
        buttonParticipate.setOnClickListener(this);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onClick(View v) {
        String code = editTextCode.getText().toString();

        waitDialogFragment = WaitDialogFragment.createDialog();
        waitDialogFragment.setCancelable(false);
        waitDialogFragment.show(getFragmentManager(), "PROMO_USE_WAIT");

        DatabaseHelper helper = new DatabaseHelper(getContext());
        User user = (User)helper.select(DatabaseHelper.Tables.USERS, null);

        makeRequest(user.getId(), user.getToken(), code);
    }

    private void makeRequest(final String id, final String token, final String code) {
        String url = MGasApi.makeUrl(MGasApi.PROMOTION_CODES_USE, id);

        HashMap<String, String> header = new HashMap<>();
        header.put("authorization", "Bearer " + token);

        JsonObject body = new JsonObject();
        body.addProperty("code", code);

        Server.request(getContext(), Request.Method.POST, url, header, body.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.startsWith(MGasApi.IISNODE_ERROR)) {
                            makeRequest(id, token, code);
                        } else {
                            JsonObject res = new Gson().fromJson(response, JsonObject.class);
                            String msg = res.get("msg").toString();

                            waitDialogFragment.dismiss();
                            dismiss();
                            Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        waitDialogFragment.dismiss();

                        if(error.networkResponse.statusCode == 409) {
                            Log.v("SPLASH_PROMO_CODE", new String(error.networkResponse.data, StandardCharsets.UTF_8));
                        }
                    }
                });
    }
}
