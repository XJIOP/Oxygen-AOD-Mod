package org.xjiop.oxygenaodmod.category;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.xjiop.oxygenaodmod.Helper;
import org.xjiop.oxygenaodmod.R;

import java.util.List;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.ViewHolder> {

    private final List<CategoriesDummy.Item> mValues;

    CategoriesAdapter(List<CategoriesDummy.Item> items) {
        mValues = items;
    }

    @NonNull
    @Override
    public CategoriesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.categories_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);

        Context context = holder.mView.getContext();

        holder.titleView.setText(Helper.categoryName(context, holder.mItem.name));
        holder.checkBoxView.setChecked(holder.mItem.checked);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean isChecked = !holder.checkBoxView.isChecked();

                holder.checkBoxView.setChecked(isChecked);
                holder.mItem.checked = isChecked;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        CategoriesDummy.Item mItem;

        final TextView titleView;
        final CheckBox checkBoxView;

        ViewHolder(View view) {
            super(view);
            mView = view;

            titleView = view.findViewById(R.id.title);
            checkBoxView = view.findViewById(R.id.checkbox);
        }
    }
}
