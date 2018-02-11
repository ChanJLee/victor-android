package com.shanbay.beaver.aop;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.shanbay.beaver.annotation.DoNotModify;
import com.shanbay.beaver.event.DialogViewEvent;
import com.shanbay.beaver.event.ViewEvent;
import com.shanbay.beaver.service.BeaverService;
import com.shanbay.beaver.utils.AOPUtil;

/**
 * Created by chan on 2017/5/28.
 * 这个类不可以更换包名 更换名称 以及方法
 */
@DoNotModify
public class BeaverViewAOP {

    private static boolean sEnable = true;

    @DoNotModify
    public static void onClick(View view) {
        if (!sEnable) {
            return;
        }
        startBeaverService(view.getContext(), view);
    }

    @DoNotModify
    public static void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (!sEnable) {
            return;
        }
        startBeaverService(buttonView.getContext(), buttonView);
    }

    @DoNotModify
    public static void onClick(DialogInterface dialogInterface, int which) {

        if (!sEnable) {
            return;
        }

        Dialog dialog = null;
        if (dialogInterface instanceof Dialog) {
            dialog = (Dialog) dialogInterface;
        }

        if (dialog == null) {
            return;
        }

        DialogViewEvent viewEvent = new DialogViewEvent();
        bindPage(viewEvent, dialog.getContext());
        bindDialog(dialog, viewEvent, which);
    }

    public static void enable(boolean enable) {
        sEnable = enable;
    }

    private static void startBeaverService(Context context, View view) {
        Intent intent = new Intent(context, BeaverService.class);
        ViewEvent viewEvent = new ViewEvent();
        bindView(viewEvent, view);
        bindPage(viewEvent, view.getContext());
        intent.putExtra(BeaverService.KEY_EVENT, BeaverService.EVENT_CHECK_POINT);
        intent.putExtra(BeaverService.KEY_DATA, viewEvent);
        context.startService(intent);
    }

    private static void bindPage(ViewEvent event, Context context) {
        Activity activity = AOPUtil.getActivityFromContext(context);
        if (activity == null) {
            return;
        }

        event.screenName = activity.getClass().getCanonicalName();
        event.screenTitle = AOPUtil.getActivityTitle(activity);
    }

    private static void bindPage(DialogViewEvent event, Context context) {
        Activity activity = AOPUtil.getActivityFromContext(context);
        if (activity == null) {
            return;
        }

        event.screenName = activity.getClass().getCanonicalName();
        event.screenTitle = AOPUtil.getActivityTitle(activity);
    }

    private static void bindDialog(DialogInterface dialog, DialogViewEvent event, int which) {
        switch (which) {
            case DialogInterface.BUTTON_NEGATIVE:
                event.viewType = "negative";
                break;
            case DialogInterface.BUTTON_NEUTRAL:
                event.viewType = "neutral";
                break;
            case DialogInterface.BUTTON_POSITIVE:
                event.viewType = "positive";
                break;
        }

        if (dialog instanceof android.app.AlertDialog) {
            bindDialog((android.app.AlertDialog) dialog, event, which);
        } else if (dialog instanceof android.support.v7.app.AlertDialog) {
            bindDialog((android.support.v7.app.AlertDialog) dialog, event, which);
        }
    }

    private static void bindDialog(android.app.AlertDialog dialog, DialogViewEvent event, int which) {
        Button button = dialog.getButton(which);

        if (button != null) {
            if (!TextUtils.isEmpty(button.getText())) {
                event.text = (String) button.getText();
            }
            return;
        }

        ListView listView = dialog.getListView();
        if (listView != null) {
            ListAdapter listAdapter = listView.getAdapter();
            Object object = listAdapter.getItem(which);
            if (object != null) {
                if (object instanceof String) {
                    event.text = (String) object;
                }
            }
        }
    }

    private static void bindDialog(android.support.v7.app.AlertDialog dialog, DialogViewEvent event, int which) {
        Button button = dialog.getButton(which);

        if (button != null) {
            if (!TextUtils.isEmpty(button.getText())) {
                event.text = (String) button.getText();
            }
            return;
        }

        ListView listView = dialog.getListView();
        if (listView != null) {
            ListAdapter listAdapter = listView.getAdapter();
            Object object = listAdapter.getItem(which);
            if (object != null) {
                if (object instanceof String) {
                    event.text = (String) object;
                }
            }
        }
    }

    private static void bindView(ViewEvent event, View view) {
        event.text = AOPUtil.getViewText(view);
        event.viewId = AOPUtil.getViewId(view);
        event.value = AOPUtil.getViewValue(view);
        event.viewType = view.getClass().getCanonicalName();
    }
}
