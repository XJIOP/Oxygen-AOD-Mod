package org.xjiop.oxygenaodmod.icon;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.xjiop.oxygenaodmod.Helper;
import org.xjiop.oxygenaodmod.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.xjiop.oxygenaodmod.Application.ICON;

public class IconDialog extends DialogFragment {

    private static final String TAG = "DBG | IconDialog";

    private final List<IconDummy.Item> icons = new ArrayList<>();
    private IconAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        for (Map.Entry s : Helper.iconList().entrySet()) {
            icons.add(new IconDummy.Item(Integer.parseInt(s.getValue().toString()), s.getKey().toString()));
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Context context = getContext();

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.icon);

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_icon, null);

        adapter = new IconAdapter(icons, new IconAdapterInterface() {
            @Override
            public void onClick() {

                Fragment targetFragment = getTargetFragment();
                if (targetFragment != null) {

                    String icon = icons.get(adapter.mSelectedItem).icon;

                    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
                    settings.edit().putString("icon", icon).apply();
                    ICON = Helper.iconList().get(icon);

                    Intent intent = new Intent();
                    intent.putExtra("summary", Helper.iconName(context, icon));
                    targetFragment.onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                }

                dismiss();
            }
        });

        for (int i=0; i < icons.size(); i++) {
            if (icons.get(i).id == ICON) {
                adapter.setCurrentIcon(i);
                break;
            }
        }

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        builder.setView(view);

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {}
        });

        return builder.create();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);

        adapter = null;
    }
}
