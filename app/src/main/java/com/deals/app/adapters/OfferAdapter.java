package com.deals.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.deals.app.R;
import com.deals.app.models.Offer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OfferAdapter extends RecyclerView.Adapter<OfferAdapter.OfferViewHolder> {
    private Context context;
    private List<Offer> offerList;
    private SimpleDateFormat dateFormat;

    public OfferAdapter(Context context, List<Offer> offerList) {
        this.context = context;
        this.offerList = offerList;
        this.dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
    }

    @NonNull
    @Override
    public OfferViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_offer, parent, false);
        return new OfferViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OfferViewHolder holder, int position) {
        Offer offer = offerList.get(position);

        holder.titleTextView.setText(offer.getTitle());
        holder.businessNameTextView.setText(offer.getBusinessName());
        holder.descriptionTextView.setText(offer.getDescription());
        holder.categoryTextView.setText(offer.getCategory());
        holder.cityTextView.setText(offer.getCity());

        holder.originalPriceTextView.setText(String.format("₹%.2f", offer.getOriginalPrice()));
        holder.discountedPriceTextView.setText(String.format("₹%.2f", offer.getDiscountedPrice()));
        holder.discountPercentageTextView.setText(String.format("%d%% OFF", offer.getDiscountPercentage()));

        Date expirationDate = new Date(offer.getExpirationDate());
        holder.expirationDateTextView.setText("Expires: " + dateFormat.format(expirationDate));

        // Set expiration warning if offer expires soon
        long daysUntilExpiration = (offer.getExpirationDate() - System.currentTimeMillis()) / (1000 * 60 * 60 * 24);
        if (daysUntilExpiration <= 3) {
            holder.expirationDateTextView.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
        } else {
            holder.expirationDateTextView.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
        }
    }

    @Override
    public int getItemCount() {
        return offerList.size();
    }

    public static class OfferViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, descriptionTextView, businessNameTextView, categoryTextView, cityTextView;
        TextView originalPriceTextView, discountedPriceTextView, discountPercentageTextView, expirationDateTextView;
        ImageView offerImageView;

        public OfferViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            businessNameTextView = itemView.findViewById(R.id.businessNameTextView);
            categoryTextView = itemView.findViewById(R.id.categoryTextView);
            cityTextView = itemView.findViewById(R.id.cityTextView);
            originalPriceTextView = itemView.findViewById(R.id.originalPriceTextView);
            discountedPriceTextView = itemView.findViewById(R.id.discountedPriceTextView);
            discountPercentageTextView = itemView.findViewById(R.id.discountPercentageTextView);
            expirationDateTextView = itemView.findViewById(R.id.expirationDateTextView);
            offerImageView = itemView.findViewById(R.id.offerImageView);
        }
    }
}