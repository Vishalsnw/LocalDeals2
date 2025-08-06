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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BusinessOfferAdapter extends RecyclerView.Adapter<BusinessOfferAdapter.BusinessOfferViewHolder> {
    private Context context;
    private List<Offer> offerList;
    private SimpleDateFormat dateFormat;
    private OnOfferDeleteListener deleteListener;

    public interface OnOfferDeleteListener {
        void onDelete(Offer offer);
    }

    public BusinessOfferAdapter(Context context, List<Offer> offerList, OnOfferDeleteListener deleteListener) {
        this.context = context;
        this.offerList = offerList;
        this.deleteListener = deleteListener;
        this.dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
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

        holder.originalPriceTextView.setText(String.format(Locale.getDefault(), "₹%.2f", offer.getOriginalPrice()));
        holder.discountedPriceTextView.setText(String.format(Locale.getDefault(), "₹%.2f", offer.getDiscountedPrice()));
        holder.discountPercentageTextView.setText(String.format(Locale.getDefault(), "%d%% OFF", offer.getDiscountPercentage()));

        Date expirationDate = new Date(offer.getExpirationDate());
        holder.expirationDateTextView.setText("Expires: " + dateFormat.format(expirationDate));

        // Check if offer is expired
        boolean isExpired = offer.getExpirationDate() < System.currentTimeMillis();
        holder.statusTextView.setText(isExpired ? "EXPIRED" : "ACTIVE");
        holder.statusTextView.setTextColor(context.getResources().getColor(
            isExpired ? android.R.color.holo_red_dark : android.R.color.holo_green_dark));

        holder.deleteButton.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDelete(offer);
            }
        });
    }

    @Override
    public int getItemCount() {
        return offerList != null ? offerList.size() : 0;
    }

    public static class BusinessOfferViewHolder extends RecyclerView.ViewHolder {
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