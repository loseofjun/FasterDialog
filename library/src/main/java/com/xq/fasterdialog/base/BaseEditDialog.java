package com.xq.fasterdialog.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import java.util.ArrayList;
import java.util.List;

public class BaseEditDialog<T extends BaseEditDialog> extends BaseNormalDialog<T>{

    private OnEditListner listener;

    private SparseArray<EditText> array_edit = new SparseArray<>();
    private SparseArray<CharSequence> array_text = new SparseArray();
    private SparseArray<CharSequence> array_hint = new SparseArray();

    public BaseEditDialog(@NonNull Context context) {
        super(context);
    }

    public BaseEditDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        //隐藏所有EditText
        goneAllEditText();

        for (int index=0;index < array_hint.size();index++)
        {
            int key = array_hint.keyAt(index);
            CharSequence hint = array_hint.get(key);
            if (!TextUtils.isEmpty(hint))
            {
                EditText editText = findViewById(context.getResources().getIdentifier("edit" + key, "id", context.getPackageName()));
                array_edit.put(key,editText);
                setHintToView(editText,hint);
                setTextToView(editText,array_text.get(key));
            }
        }

        if (TextUtils.isEmpty(positiveText))
            setPositiveText(SURE);
        setPositiveListener(new OnDialogClickListener() {
            @Override
            public void onClick(BaseDialog dialog) {
                if (listener != null)
                {
                    SparseArray<CharSequence> array = new SparseArray<>();
                    for (int index=0;index < array_edit.size();index++)
                    {
                        int key = array_edit.keyAt(index);
                        EditText editText = array_edit.get(key);
                        array.put(key,editText.getText().toString());
                    }
                    listener.onEdit(BaseEditDialog.this,array);
                }
            }
        });

    }
    protected void setHintToView(EditText editText, CharSequence text){
        if (editText.getParent().getParent() instanceof TextInputLayout)
        {
            if (((TextInputLayout) editText.getParent().getParent()).isHintEnabled())
                ((TextInputLayout) editText.getParent().getParent()).setHint(text);
            else
                editText.setHint(text);
            ((TextInputLayout) editText.getParent().getParent()).setVisibility(View.VISIBLE);

        }
        else
        {
            editText.setHint(text);
            editText.setVisibility(View.VISIBLE);
        }
    }


    private void goneAllEditText() {
        List<EditText> list_view = getAllEditText(rootView);
        for (EditText et : list_view)
        {
            if (et.getParent().getParent() instanceof TextInputLayout)
                 ((TextInputLayout) et.getParent().getParent()).setVisibility(View.GONE);
            else
                et.setVisibility(View.GONE);
        }
    }

    private List<EditText> getAllEditText(View view) {
        List<EditText> allchildren = new ArrayList<>();
        if (view instanceof ViewGroup)
        {
            ViewGroup vp = (ViewGroup) view;
            for (int i = 0; i < vp.getChildCount(); i++)
            {
                View viewchild = vp.getChildAt(i);
                if (viewchild instanceof EditText)
                    allchildren.add((EditText) viewchild);
                //再次 调用本身（递归）
                allchildren.addAll(getAllEditText(viewchild));
            }
        }
        return allchildren;
    }

    //确认键监听已被默认占用，不建议再自行设置
    @Deprecated
    @Override
    public T setPositiveListener(OnDialogClickListener positiveListener) {
        this.positiveListener = positiveListener;
        bindDialogClickListenerWithView(positiveView, positiveListener,false);
        return (T) this;
    }

    public T setOnEditListner(OnEditListner listener) {
        this.listener = listener;
        return (T) this;
    }

    public T setErro(int no,CharSequence text){
        EditText editText = array_edit.get(no);
        if (editText.getParent().getParent() instanceof TextInputLayout)
            ((TextInputLayout) editText.getParent().getParent()).setError(text);
        else
            editText.setError(text);
        return (T) this;
    }

    public T setText(int no,CharSequence text) {
        array_text.put(no,text);
        return (T) this;
    }

    public T setText1(CharSequence text) {
        setText(1,text);
        return (T) this;
    }

    public T setText2(CharSequence text) {
        array_text.put(2,text);
        return (T) this;
    }

    public T setHint(int no,CharSequence text) {
        array_hint.put(no,text);
        return (T) this;
    }

    public T setHint1(CharSequence hint) {
        setHint(1,hint);
        return (T) this;
    }

    public T setHint2(CharSequence hint) {
        setHint(2,hint);
        return (T) this;
    }

    public static interface OnEditListner{
        public void onEdit(BaseEditDialog dialog,SparseArray<CharSequence> array);
    }

}

