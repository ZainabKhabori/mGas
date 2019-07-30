package om.webware.mgas.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import om.webware.mgas.R;
import om.webware.mgas.api.Feedback;
import om.webware.mgas.api.User;
import om.webware.mgas.customViews.CircularImageView;
import om.webware.mgas.server.Server;

public class OrderViewFeedbackRecyclerAdapter extends RecyclerView.Adapter<OrderViewFeedbackRecyclerAdapter.ViewHolder> {

    private ArrayList<Feedback> feedbacks;
    private ArrayList<User> users;

    private boolean netAvailable;

    class ViewHolder extends RecyclerView.ViewHolder {

        private CircularImageView imageViewAuthorDp;
        private TextView textViewAuthorName;
        private TextView textViewFeedback;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageViewAuthorDp = itemView.findViewById(R.id.imageViewAuthorDp);
            textViewAuthorName = itemView.findViewById(R.id.textViewAuthorName);
            textViewFeedback = itemView.findViewById(R.id.textViewFeedback);
        }

        CircularImageView getImageViewAuthorDp() {
            return imageViewAuthorDp;
        }

        TextView getTextViewAuthorName() {
            return textViewAuthorName;
        }

        TextView getTextViewFeedback() {
            return textViewFeedback;
        }
    }

    public OrderViewFeedbackRecyclerAdapter(Context context, ArrayList<Feedback> feedbacks, ArrayList<User> users) {
        this.feedbacks = feedbacks;
        this.users = users;

        netAvailable = Server.getNetworkAvailability(context);
    }

    @NonNull
    @Override
    public OrderViewFeedbackRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.recycler_order_view_feedback_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Feedback feedback = feedbacks.get(i);
        User author = users.get(i);

        if(author.getDisplayPicThumb() != null && author.getDisplayPicUrl() != null) {
            if(netAvailable) {
                byte[] thumb = author.getDisplayPicThumb();
                Bitmap bitmap = BitmapFactory.decodeByteArray(thumb, 0, thumb.length);
                int width = viewHolder.getImageViewAuthorDp().getWidth();
                int height = viewHolder.getImageViewAuthorDp().getHeight();
                Bitmap scaled = Bitmap.createScaledBitmap(bitmap, width, height, false);
                viewHolder.getImageViewAuthorDp().setImageBitmap(scaled);
            } else {
                Picasso.get().load(author.getDisplayPicUrl()).into(viewHolder.getImageViewAuthorDp());
            }
        }

        String name = author.getfName() + " " + author.getlName();
        viewHolder.getTextViewAuthorName().setText(name);

        viewHolder.getTextViewFeedback().setText(feedback.getMessage());
    }

    @Override
    public int getItemCount() {
        return feedbacks.size();
    }
}
