
package com.deals.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.deals.app.R;
import com.deals.app.models.Offer;
import java.util.List;

public class BusinessOfferAdapter extends RecyclerView.Adapter<BusinessOfferAdapter.BusinessOfferViewHolder> {
    private Context context;
    private List<Offer> offerList;
    private OnOfferDeleteListener deleteListener;

    public interface OnOfferDeleteListener {
        void onOfferDelete(Offer offer);
    }

    public BusinessOfferAdapter(Context context, List<Offer> offerList, OnOfferDeleteListener deleteListener) {
        this.context = context;
        this.offerList = offerList;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public BusinessOfferViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_business_offer, parent, false);
        return new BusinessOfferViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BusinessOfferViewHolder holder, int position) {
        Offer offer = offerList.get(position);
        
        holder.titleTextView.setText(offer.getTitle());
        holder.descriptionTextView.setText(offer.getDescription());
        holder.categoryTextView.setText(offer.getCategory());
        holder.originalPriceTextView.setText("$" + offer.getOriginalPrice());
        holder.discountedPriceTextView.setText("$" + offer.getDiscountedPrice());
        
        // Calculate discount percentage
        double discountPercentage = ((offer.getOriginalPrice() - offer.getDiscountedPrice()) / offer.getOriginalPrice()) * 100;
        holder.discountPercentageTextView.setText(String.format("%.0f%% OFF", discountPercentage));
        
        holder.expirationDateTextView.setText("Expires: " + offer.getExpirationDate());
        holder.statusTextView.setText(offer.isActive() ? "Active" : "Inactive");
        
        holder.deleteButton.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onOfferDelete(offer);
            }
        });
    }

    @Override
    public int getItemCount() {
        return offerList.size();
    }

    public class BusinessOfferViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, descriptionTextView, categoryTextView;
        TextView originalPriceTextView, discountedPriceTextView, discountPercentageTextView;
        TextView expirationDateTextView, statusTextView;
        Button deleteButton;

        public BusinessOfferViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            categoryTextView = itemView.findViewById(R.id.categoryTextView);
            originalPriceTextView = itemView.findViewById(R.id.originalPriceTextView);
            discountedPriceTextView = itemView.findViewById(R.id.discountedPriceTextView);
            discountPercentageTextView = itemView.findViewById(R.id.discountPercentageTextView);
            expirationDateTextView = itemView.findViewById(R.id.expirationDateTextView);
            statusTextView = itemView.findViewById(R.id.statusTextView);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}
