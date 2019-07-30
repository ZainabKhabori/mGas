package om.webware.mgas.fragments.dialogs;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import om.webware.mgas.R;
import om.webware.mgas.adapters.CardsRecyclerAdapter;
import om.webware.mgas.api.BankCard;
import om.webware.mgas.api.BankCards;
import om.webware.mgas.tools.DatabaseHelper;

/**
 * Created by Zainab on 6/29/2019.
 */

public class PaymentDialogFragment extends DialogFragment implements View.OnClickListener,
        CardsRecyclerAdapter.OnItemClickListener {

    private BankCards cards;
    private int selected;
    private OnPaymentMethodSelectedListener listener;

    public interface OnPaymentMethodSelectedListener { void onPaymentMethodSelected(int index, BankCard card); }

    public PaymentDialogFragment() {
        //
    }

    public static PaymentDialogFragment createDialog() {
        return new PaymentDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dialog_payment, container);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getDialog().getWindow().setBackgroundDrawable(ContextCompat.getDrawable(getContext(),
                R.drawable.round_edge_dialog_background));

        RecyclerView recyclerViewCards = view.findViewById(R.id.recyclerViewCards);
        Button buttonConfirm = view.findViewById(R.id.buttonConfirm);

        buttonConfirm.setOnClickListener(this);

        DatabaseHelper helper = new DatabaseHelper(getContext());
        cards = (BankCards)helper.select(DatabaseHelper.Tables.BANK_CARDS, null);

        cards.getBankCards().add(0, new BankCard(null, null, getString(R.string.cash_payment),
                0, 0, 0));
        cards.getBankCards().add(1, new BankCard(null, null, getString(R.string.credit_card_payment),
                0, 0, 0));

        CardsRecyclerAdapter adapter = new CardsRecyclerAdapter(getContext(), cards.getBankCards());
        adapter.setOnItemClickListener(this);

        RecyclerView.LayoutManager manager = new LinearLayoutManager(getContext());

        recyclerViewCards.setAdapter(adapter);
        recyclerViewCards.setLayoutManager(manager);

        selected = 0;
    }

    @Override
    public void onClick(View v) {
        if(listener != null) {
            listener.onPaymentMethodSelected(selected, cards.getBankCards().get(selected));
        }
    }

    @Override
    public void onItemClick(View view, int index) {
        selected = index;
    }

    public void setListener(OnPaymentMethodSelectedListener listener) {
        this.listener = listener;
    }
}
