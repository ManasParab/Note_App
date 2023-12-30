package com.example.to_donote;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class card_adapter extends RecyclerView.Adapter<card_adapter.CardViewHolder> {

    private final List<task_card> cardTaskList;
    private OnTaskCardClickListener onTaskCardClickListener;

    public card_adapter(List<task_card> cardTaskList) {
        this.cardTaskList = cardTaskList;
    }

    // Interface for click events on task_card items
    public interface OnTaskCardClickListener {
        void onTaskCardClick(task_card taskCard, int position);
    }

    public void setOnTaskCardClickListener(OnTaskCardClickListener listener) {
        onTaskCardClickListener = listener;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_card, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        task_card taskCard = cardTaskList.get(position);

        holder.checkBox.setChecked(taskCard.isChecked());
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> taskCard.setChecked(isChecked));

        holder.titleTextView.setText(taskCard.getTitle());
        setupDescriptionTextView(holder.itemView.getContext(), holder.descriptionTextView, taskCard.getDescription());
        holder.dateTime.setText(taskCard.getDateTime());

        // Long click listeners
        setupLongClickListener(holder.itemView.findViewById(R.id.card), taskCard, holder);
        setupLongClickListener(holder.titleTextView, taskCard, holder);
        setupLongClickListener(holder.descriptionTextView, taskCard, holder);

        // Adjust visibility
        if (taskCard.getDescription() == null || taskCard.getDescription().isEmpty()) {
            holder.descriptionTextView.setVisibility(View.GONE);
            holder.line.setVisibility(View.GONE);
        } else {
            holder.descriptionTextView.setVisibility(View.VISIBLE);
            holder.line.setVisibility(View.VISIBLE);
        }
    }

    private CharSequence truncateText(Context context, String text, int maxLength, boolean isExpanded) {
        if (text != null && text.length() > maxLength && !isExpanded) {
            String moreText = "... Read More";
            String truncated = text.substring(0, maxLength);
            SpannableString spannable = new SpannableString(truncated + moreText);

            int start = truncated.length();
            int end = start + moreText.length();
            int color = Color.parseColor("#ffaa00"); // Parse the color string
            spannable.setSpan(new ForegroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            return spannable;
        } else if (isExpanded) {
            String lessText = " Read Less";
            SpannableString spannable = new SpannableString(text + lessText);

            int start = text.length();
            int end = start + lessText.length();
            int color = Color.parseColor("#ffaa00"); // Parse the color string
            spannable.setSpan(new ForegroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            return spannable;
        }
        return text;
    }

    private void setupDescriptionTextView(Context context, TextView textView, String fullText) {
        Boolean isExpanded = (Boolean) textView.getTag();
        if (isExpanded == null) isExpanded = false;

        CharSequence text = truncateText(context, fullText, 200, isExpanded);
        textView.setText(text);
        textView.setOnClickListener(v -> {
            Boolean newIsExpanded = !(Boolean) textView.getTag();
            textView.setText(truncateText(context, fullText, 200, newIsExpanded));
            textView.setTag(newIsExpanded);
        });

        textView.setTag(isExpanded); // Initially not expanded
    }


    private void setupLongClickListener(View view, task_card taskCard, CardViewHolder holder) {
        view.setOnLongClickListener(v -> {
            if (onTaskCardClickListener != null) {
                onTaskCardClickListener.onTaskCardClick(taskCard, holder.getAdapterPosition());
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return cardTaskList.size();
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        TextView titleTextView;
        TextView descriptionTextView;
        TextView dateTime;
        View line;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.firstCheckBox);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            dateTime = itemView.findViewById(R.id.dateTime);
            line = itemView.findViewById(R.id.line);
        }
    }
}
