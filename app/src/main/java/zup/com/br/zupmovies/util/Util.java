package zup.com.br.zupmovies.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;

import zup.com.br.zupmovies.R;

/**
 * @author ton1n8o - antoniocarlos.dev@gmail.com on 3/6/16.
 */
public class Util {

    /**
     * Creates a AlertDialog.
     *
     * @param ctx     Context
     * @param title   dialog title
     * @param message dialog message
     * @return AlertDialog.Builder.
     */
    public static AlertDialog.Builder createDialog(Context ctx, String title, String message) {
        AlertDialog.Builder dialogo = new AlertDialog.Builder(ctx);
        dialogo.setTitle(title);
        dialogo.setMessage(message);
        dialogo.setNeutralButton(ctx.getResources().getString(R.string.lbl_ok), null);
        return dialogo;
    }

    /**
     * Createa a ProgressDialog.
     *
     * @param ctx     Context
     * @param title   dialog title
     * @param message dialog message
     * @return ProgressDialog
     */
    public static ProgressDialog createProgressDialog(Context ctx, @NonNull String title, String message) {
        ProgressDialog p = ProgressDialog.show(ctx, title, message, true);
        p.setCancelable(false);
        return p;
    }

    public static byte[] extractImageByteArray(@NonNull BitmapDrawable bd) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bd.getBitmap().compress(Bitmap.CompressFormat.PNG, 50, baos);
        return baos.toByteArray();
    }

    public static void setText(@NonNull TextView textView, String text) {
        if (TextUtils.isEmpty(text)) {
            textView.setText("");
        }
        textView.setText(text);
    }

}
