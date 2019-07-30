package om.webware.mgas.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import java.util.ArrayList;

import om.webware.mgas.R;
import om.webware.mgas.api.BankCard;

/**
 * Created by Zainab on 6/29/2019.
 */

public class CardsRecyclerAdapter extends RecyclerView.Adapter<CardsRecyclerAdapter.ViewHolder> {

    private Context context;
    private ArrayList<BankCard> cards;
    private OnItemClickListener onItemClickListener;

    private int selected = 0;

    public interface OnItemClickListener { void onItemClick(View view, int index); }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private RadioButton radioButtonCard;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            radioButtonCard = itemView.findViewById(R.id.radioButtonCard);
            radioButtonCard.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onItemClickListener != null) {
                selected = getAdapterPosition();
                notifyDataSetChanged();
                onItemClickListener.onItemClick(v, selected);
            }
        }

        RadioButton getRadioButtonCard() {
            return radioButtonCard;
        }
    }

    public CardsRecyclerAdapter(Context context, ArrayList<BankCard> cards) {
        this.context = context;
        this.cards = cards;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public CardsRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.recycler_cards_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardsRecyclerAdapter.ViewHolder viewHolder, int i) {
        viewHolder.getRadioButtonCard().setChecked(i == selected);

        if(i > 1) {
            String cardNumber = cards.get(i).getCardNo();
            String coded = cardNumber.replace(cardNumber.substring(0, cardNumber.length() - 4), "x");
            viewHolder.getRadioButtonCard().setText(coded);
        } else {
            boolean rtl = context.getResources().getConfiguration()
                    .getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;

            Drawable icon;
            if(i == 0) {
                icon = ContextCompat.getDrawable(context, R.drawable.ic_cash);
            } else {
                icon = ContextCompat.getDrawable(context, R.drawable.ic_card_add);
            }

            if(rtl) {
                viewHolder.getRadioButtonCard()
                        .setCompoundDrawablesWithIntrinsicBounds(null, null, icon, null);
            } else {
                viewHolder.getRadioButtonCard()
                        .setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
            }
            viewHolder.getRadioButtonCard().setText(cards.get(i).getCardNo());
        }
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }
}
