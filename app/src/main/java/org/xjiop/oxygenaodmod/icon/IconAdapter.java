package org.xjiop.oxygenaodmod.icon;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.xjiop.oxygenaodmod.Helper;
import org.xjiop.oxygenaodmod.R;

import java.util.List;

public class IconAdapter extends RecyclerView.Adapter<IconAdapter.ViewHolder> {

    int mSelectedItem = -1;

    private final List<IconDummy.Item> mValues;
    private final IconAdapterInterface mListener;

    IconAdapter(List<IconDummy.Item> items, IconAdapterInterface listener) {
        mValues = items;
        mListener = listener;
    }

    @NonNull
    @Override
    public IconAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.icon_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);

        Context context = holder.mView.getContext();

        holder.iconView.setImageResource(holder.mItem.id);
        holder.titleView.setText(Helper.iconName(context, holder.mItem.icon));
        holder.radioButtonView.setChecked(position == mSelectedItem);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null) {
                    mSelectedItem = holder.getAdapterPosition();
                    mListener.onClick();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    void setCurrentIcon(int position) {
        mSelectedItem = position;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        IconDummy.Item mItem;

        final ImageView iconView;
        final TextView titleView;
        final RadioButton radioButtonView;

        ViewHolder(View view) {
            super(view);
            mView = view;

            iconView = view.findViewById(R.id.icon);
            titleView = view.findViewById(R.id.title);
            radioButtonView = view.findViewById(R.id.radio);
        }
    }
}
