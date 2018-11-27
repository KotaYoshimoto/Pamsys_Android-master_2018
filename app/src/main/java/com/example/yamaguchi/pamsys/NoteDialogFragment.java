package com.example.yamaguchi.pamsys;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;


/**
 * A simple {@link Fragment} subclass.
 */
public class NoteDialogFragment extends DialogFragment {

    EditText noteEditText;

    public NoteDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Activityで生成されたDialogのサイズを，リサイズして最適化する．
        Dialog dialog = getDialog();
        WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int dialogWidth = (int) (displayMetrics.widthPixels * 0.8);
        int dialogHeight = (int) (displayMetrics.heightPixels * 0.8);

        layoutParams.width = dialogWidth;
        layoutParams.height = dialogHeight;
        dialog.getWindow().setAttributes(layoutParams);

    }

    @Override
    public Dialog onCreateDialog(Bundle saveInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        //Viewの部分と紐づけしている．
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_note_dialog,null);
        builder.setView(view);
        noteEditText = view.findViewById(R.id.noteEditText);
        noteEditText.setText(getArguments().getString("note"));

        builder.setTitle("備考欄");
        //備考欄に書かれた内容を登録する
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String message = noteEditText.getText().toString();
                AnFinishedServiceResultActivity callingActivity = (AnFinishedServiceResultActivity) getActivity();
                callingActivity.onReturnNote(message);

                NoteDialogFragment.this.dismiss();
            }
        });
        //備考欄に書かれた内容を無視して，元のアクティビティに戻る
        builder.setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                NoteDialogFragment.this.dismiss();
            }
        });

        return builder.create();
    }

}
