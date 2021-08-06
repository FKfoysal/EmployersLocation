package gub.foysal.employerslocation.Method;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import gub.foysal.employerslocation.CommentActivity;
import gub.foysal.employerslocation.HomePageActivity;
import gub.foysal.employerslocation.R;

@TargetApi(Build.VERSION_CODES.M)
public class FingerprintHandeler extends FingerprintManager.AuthenticationCallback {

    private Context context;

    public FingerprintHandeler(Context context){

        this.context = context;

    }
    public void startAuth(FingerprintManager fingerprintManager, FingerprintManager.CryptoObject cryptoObject){

        CancellationSignal cancellationSignal = new CancellationSignal();
        fingerprintManager.authenticate(cryptoObject, cancellationSignal, 0, this, null);

    }
    @Override
    public void onAuthenticationError(int errorCode, CharSequence errString) {

        this.update("There was an Auth Error. " + errString, false);

    }

    @Override
    public void onAuthenticationFailed() {

        this.update("Auth Failed. ", false);

    }
    @Override
    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {

        this.update("Error: " + helpString, false);

    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {

        this.update("You can now access the app.", true);

    }

    private void update(String s, final boolean b) {

        TextView paraLabel = (TextView) ((Activity) context).findViewById(R.id.fingur_mass_id);
        ImageView imageView = (ImageView) ((Activity) context).findViewById(R.id.fingur_image_id);
        Button  ok= ((Activity) context).findViewById(R.id.ok_id);

        paraLabel.setText(s);

        if (b == false) {

            paraLabel.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));

        } else {
            paraLabel.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
            imageView.setImageResource(R.drawable.ic_check_circle_black_24dp);
            ok.setVisibility(View.VISIBLE);
        }

    }

}
