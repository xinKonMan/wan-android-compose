package com.xinkon.wancompose.base;

/**
 * Created by wangtinghao on 2021/11/01
 **/

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewbinding.ViewBinding;

import org.jetbrains.annotations.NotNull;

public abstract class BaseDialogFragment<VB extends ViewBinding> extends AppCompatDialogFragment {
    private final String TAG = this.getClass().getSimpleName();
    public static final float DEFAULT_DIM_AMOUNT = 0.2F;

    private DialogInterface.OnDismissListener mOnDismissListener;
    private DialogInterface.OnShowListener mOnShowListener;
    protected VB binding;

    protected abstract VB inflateViewBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState);

    public BaseDialogFragment<VB> show(FragmentManager fragmentManager) {
        Log.i(TAG, "show");
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.add(this, TAG);
        ft.commitAllowingStateLoss();
        return this;
    }

    protected void onCreateViewAfterBinding(View view) {

    }

    /**
     * 点外围是否可取消
     */
    protected boolean isCancelableOutside() {
        return true;
    }

    protected boolean isCanCancelableDialog() {
        return true;
    }

    /**
     * 默认弹窗位置为中心
     */
    public int getGravity() {
        return Gravity.CENTER;
    }

    /**
     * 默认高为包裹内容
     */
    public int getDialogHeight() {
        return WindowManager.LayoutParams.WRAP_CONTENT;
    }

    /**
     * 默认宽为包裹内容
     */
    public int getDialogWidth() {
        return WindowManager.LayoutParams.WRAP_CONTENT;
    }

    public int getBgColor() {
        return Color.TRANSPARENT;
    }

    /**
     * 默认透明度
     */
    public float getDimAmount() {
        return DEFAULT_DIM_AMOUNT;
    }

    /**
     * 获取弹窗显示动画
     */
    protected int getDialogAnimRes() {
        return 0;
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        mOnDismissListener = onDismissListener;
    }

    public void setOnShowListener(DialogInterface.OnShowListener onShowListener) {
        mOnShowListener = onShowListener;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        try {
            Dialog dialog = getDialog();
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        } catch (Exception e) {
            Log.e(TAG, "onCreateView: ", e);
        }
        binding = inflateViewBinding(inflater, container, savedInstanceState);
        onCreateViewAfterBinding(binding.getRoot());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Dialog dialog = getDialog();
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(isCancelableOutside());
        dialog.setCancelable(isCanCancelableDialog());
        if (dialog.getWindow() != null && getDialogAnimRes() > 0) {
            dialog.getWindow().setWindowAnimations(getDialogAnimRes());
        }
    }

    @Override
    public void onStart() {
        try {
            super.onStart();
        } catch (Exception e) {
            dismiss();
            e.printStackTrace();
        }

        Window window = getDialog().getWindow();
        if (window != null) {
            //设置窗体背景色透明
            window.setBackgroundDrawable(new ColorDrawable(getBgColor()));
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            //设置宽高
            layoutParams.width = getDialogWidth();
            layoutParams.height = getDialogHeight();
            //透明度
            layoutParams.dimAmount = getDimAmount();
            //位置
            layoutParams.gravity = getGravity();
            window.setAttributes(layoutParams);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDismiss(@NotNull DialogInterface dialog) {
        dismissAllowingStateLoss();
        if (mOnDismissListener != null) {
            mOnDismissListener.onDismiss(dialog);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().setOnCancelListener(null);
    }
}
