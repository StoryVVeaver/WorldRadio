package by.roman.worldradio0.business_logic.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.Locale;

import by.roman.worldradio0.R;

public class TimerWheelAdapter extends RecyclerView.Adapter<TimerWheelAdapter.ViewHolder> {
    private final Context context;
    private final int baseCount;
    private final int repeatFactor;
    private int selectedPosition = -1;

    public TimerWheelAdapter(Context context, int baseCount) {
        this.context = context;
        this.baseCount = baseCount;
        this.repeatFactor = 1000;
    }

    @Override
    public int getItemCount() {
        return baseCount * repeatFactor;
    }

    @NonNull
    @Override
    public TimerWheelAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_wheel, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimerWheelAdapter.ViewHolder holder, int position) {
        int realValue = position % baseCount;
        holder.textItem.setText(String.format(Locale.getDefault(), "%02d", realValue));

        if (position == selectedPosition) {
            holder.textItem.setAlpha(1.0f);
            holder.textItem.setTextColor(Color.WHITE);
        } else {
            holder.textItem.setAlpha(0.5f);
            holder.textItem.setTextColor(Color.GRAY);
        }
    }

    public void setSelectedPosition(int position) {
        selectedPosition = position;
        notifyDataSetChanged();
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public int getRealValue() {
        if (selectedPosition < 0) return 0;
        return selectedPosition % baseCount;
    }

    public int getBaseCount() {
        return baseCount;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView textItem;
        ViewHolder(View itemView) {
            super(itemView);
            textItem = itemView.findViewById(R.id.textItem);
        }
    }
}