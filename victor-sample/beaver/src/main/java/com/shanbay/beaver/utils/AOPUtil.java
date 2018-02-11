package com.shanbay.beaver.utils;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.ToggleButton;

/**
 * Created by chan on 2017/6/7.
 */

public class AOPUtil {

    @Nullable
    public static String getActivityTitle(Activity activity) {

        String title = "";

        if (activity == null) {
            return title;
        }

        ActionBar actionBar = activity.getActionBar();
        if (actionBar != null) {
            title = actionBar.getTitle().toString();
        }
        if (!TextUtils.isEmpty(title)) {
            return title;
        }


        title = activity.getTitle().toString();
        if (!TextUtils.isEmpty(title)) {
            return title;
        }

        try {
            PackageManager packageManager = activity.getPackageManager();
            if (packageManager != null) {
                ActivityInfo activityInfo = packageManager.getActivityInfo(activity.getComponentName(), 0);
                if (activityInfo != null) {
                    title = activityInfo.loadLabel(packageManager).toString();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return title;
    }

    @Nullable
    public static String getViewId(View view) {
        String textureId = null;
        try {
            if (view.getId() != View.NO_ID) {
                textureId = view.getContext().getResources().getResourceEntryName(view.getId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return textureId;
    }

    @Nullable
    public static String getViewText(View view) {
        if (view instanceof CheckBox) {
            CompoundButton compoundButton = (CompoundButton) view;
            return compoundButton.getText().toString();
        }

        if (view instanceof SwitchCompat) {
            SwitchCompat switchCompat = (SwitchCompat) view;
            return switchCompat.getTextOn().toString();
        }

        if (view instanceof ToggleButton) {
            ToggleButton toggleButton = (ToggleButton) view;
            if (toggleButton.isChecked()) {
                return toggleButton.getTextOn().toString();
            } else {
                return toggleButton.getTextOff().toString();
            }
        }

        if (view instanceof RadioButton) {
            RadioButton radioButton = (RadioButton) view;
            return radioButton.getText().toString();
        }

        if (view instanceof Button) {
            Button button = (Button) view;
            return button.getText().toString();
        }

        if (view instanceof CheckedTextView) {
            CheckedTextView textView = (CheckedTextView) view;
            return textView.getText().toString();
        }

        if (view instanceof TextView) {
            TextView textView = (TextView) view;
            return textView.getText().toString();
        }

        return "";
    }

    public static String getViewValue(View view) {
        if (view instanceof CompoundButton) {
            CompoundButton compoundButton = (CompoundButton) view;
            return String.valueOf(compoundButton.isChecked());
        }

        return "";
    }

    public static Activity getActivityFromContext(Context context) {

        if (context == null) {
            return null;
        }

        if (context instanceof Activity) {
            return (Activity) context;
        }

        if (context instanceof ContextWrapper) {
            while (!(context instanceof Activity) && context instanceof ContextWrapper) {
                context = ((ContextWrapper) context).getBaseContext();
            }

            if (context instanceof Activity) {
                return (Activity) context;
            }
        }

        return null;
    }
}
