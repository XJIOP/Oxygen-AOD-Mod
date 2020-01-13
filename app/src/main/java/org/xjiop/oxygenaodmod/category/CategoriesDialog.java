package org.xjiop.oxygenaodmod.category;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.xjiop.oxygenaodmod.Helper;
import org.xjiop.oxygenaodmod.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.xjiop.oxygenaodmod.Application.ALLOWED_CATEGORY;

public class CategoriesDialog extends DialogFragment {

    private static final String TAG = "DBG | CategoriesDialog";

    private final List<CategoriesDummy.Item> categories = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        for(String s : Helper.categoryList) {
            categories.add(new CategoriesDummy.Item(s, ALLOWED_CATEGORY.contains(s)));
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Context context = getContext();

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.categories);

        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_categories, null);

        CategoriesAdapter adapter = new CategoriesAdapter(categories);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        builder.setView(view);

        builder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                Set<String> selected = new HashSet<>();
                for(CategoriesDummy.Item item : categories) {
                    if(item.checked) {
                        selected.add(item.name);
                    }
                }
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
                settings.edit().putStringSet("categories", selected).apply();
                ALLOWED_CATEGORY = new ArrayList<>(selected);
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {}
        });

        return builder.create();
    }
}
