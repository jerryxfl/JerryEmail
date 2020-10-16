package com.edu.cdp.ui.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.edu.cdp.R;
import com.edu.cdp.base.BaseDialog;

public class ConfirmDialog extends BaseDialog {
    private TextView mTitle;
    private TextView mContent;
    private Button mCancel, mConfirm;
    private ConfirmClickListener confirmClickListener;
    private CancelClickListener cancelClickListener;

    public ConfirmDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int setCustomContentView() {
        return R.layout.confirm_dialog;
    }

    @Override
    protected boolean setCanceledOnTouchOutside() {
        return true;
    }

    @Override
    protected boolean setCancelable() {
        return true;
    }

    @Override
    protected void initWindow(Window window) {

    }

    @Override
    protected void initView() {
        mTitle = findViewById(R.id.title);
        mContent = findViewById(R.id.content);
        mCancel = findViewById(R.id.cancel);
        mConfirm = findViewById(R.id.confirm);
    }

    @Override
    protected void initEvent() {
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cancelClickListener != null) {
                    cancelClickListener.onCancelClick(ConfirmDialog.this);
                }
            }
        });

        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (confirmClickListener != null) {
                    confirmClickListener.onConfirmClick(ConfirmDialog.this);
                }
            }
        });
    }


    public void setTitle(String title) {
        mTitle.setText(title);
    }

    public void setContent(String content) {
        mContent.setText(content);
    }


    public void setConfirmClickListener(ConfirmClickListener confirmClickListener) {
        this.confirmClickListener = confirmClickListener;
    }

    public void setCancelClickListener(CancelClickListener cancelClickListener) {
        this.cancelClickListener = cancelClickListener;
    }

    public interface ConfirmClickListener {
        void onConfirmClick(ConfirmDialog confirmDialog);
    }

    public interface CancelClickListener {
        void onCancelClick(ConfirmDialog confirmDialog);
    }

    public static class Builder {
        Context context;
        ConfirmDialog confirmDialog;
        String title = "标题";
        String content = "内容";
        ConfirmClickListener confirmClickListener;
        CancelClickListener cancelClickListener;
        Listener listener;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setContent(String content) {
            this.content = content;
            return this;
        }

        public Builder setConfirmClickListener(ConfirmClickListener confirmClickListener) {
            this.confirmClickListener = confirmClickListener;
            return this;
        }

        public Builder setCancelClickListener(CancelClickListener cancelClickListener) {
            this.cancelClickListener = cancelClickListener;
            return this;
        }

        public Builder setShowAndDismissListener(Listener listener) {
            this.listener = listener;
            return this;
        }

        public ConfirmDialog build() {
            confirmDialog = new ConfirmDialog(context);
            confirmDialog.create();
            confirmDialog.setTitle(title);
            confirmDialog.setContent(content);
            confirmDialog.setConfirmClickListener(confirmClickListener);
            confirmDialog.setCancelClickListener(cancelClickListener);
            confirmDialog.setDialogListener(listener);
            return confirmDialog;
        }
    }
}
