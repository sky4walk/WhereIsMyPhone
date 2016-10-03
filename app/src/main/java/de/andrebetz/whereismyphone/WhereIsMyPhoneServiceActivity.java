// WhereIsMyPhone@andrebetz.de
package de.andrebetz.whereismyphone;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;



public class WhereIsMyPhoneServiceActivity extends Activity {
    private static EditText mEditTextMailAddress;
    private static EditText mEditTextMailUser;
    private static EditText mEditTextMailPassword;
    private static EditText mEditTextMailTo;
    private static EditText mEditTextMailHost;
    private static EditText mEditTextMailSubjaectAdd;
    private static EditText mEditTextMailPort;
    private static EditText mEditTextMailDelay;
    private static Button   mButtonStartService;
    private static Button   mButtonStopService;
    private DataFile mDataFile;
    private String mFileName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whereismyphone);

        mDataFile = new DataFile();
        mEditTextMailAddress        = (EditText) findViewById(R.id.et_id_mail);
        mEditTextMailPassword       = (EditText) findViewById(R.id.et_id_pw);
        mEditTextMailUser           = (EditText) findViewById(R.id.et_id_user);
        mEditTextMailTo             = (EditText) findViewById(R.id.et_id_mailTo);
        mEditTextMailHost           = (EditText) findViewById(R.id.et_id_host);
        mEditTextMailSubjaectAdd    = (EditText) findViewById(R.id.et_id_subjectAdd);
        mEditTextMailPort           = (EditText) findViewById(R.id.et_id_Port);
        mEditTextMailDelay          = (EditText) findViewById(R.id.et_id_Delay);
        mButtonStartService         = (Button)   findViewById(R.id.start_service);
        mButtonStopService          = (Button)   findViewById(R.id.stop_Service);

        getWritePermissions();

        mFileName = DataFile.getFileNameWithPath(getApplicationContext());
        if ( DataFile.file_exists( mFileName ) ) {
            mDataFile.LoadBrewRecipe(mFileName);
        }

        mEditTextMailAddress.setText(mDataFile.getEmailAddress());
        mEditTextMailPassword.setText(mDataFile.getEmailPassword());
        mEditTextMailUser.setText(mDataFile.getEmailUser());
        mEditTextMailTo.setText(mDataFile.getEmailTo());
        mEditTextMailHost.setText(mDataFile.getEmailHost());
        mEditTextMailSubjaectAdd.setText(mDataFile.getEmailSub());
        mEditTextMailPort.setText(mDataFile.getEmailPort());
        mEditTextMailDelay.setText(mDataFile.getEmailUpdate());

        //starting service
        mButtonStartService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDataFile.setEmailAddress(mEditTextMailAddress.getText().toString());
                mDataFile.setEmailPassword(mEditTextMailPassword.getText().toString());
                mDataFile.setEmailUser(mEditTextMailUser.getText().toString());
                mDataFile.setEmailTo(mEditTextMailTo.getText().toString());
                mDataFile.setEmailHost(mEditTextMailHost.getText().toString());
                mDataFile.setEmailSub(mEditTextMailSubjaectAdd.getText().toString());
                mDataFile.setEmailUpdate(mEditTextMailDelay.getText().toString());
                mDataFile.setEmailPort(mEditTextMailPort.getText().toString());
                mDataFile.SaveSettings(mFileName);

                Intent intent = new Intent(WhereIsMyPhoneServiceActivity.this, WhereIsMyPhoneService.class);
                intent.putExtra(Constants.emailAddressTag,          mDataFile.getEmailAddress());
                intent.putExtra(Constants.emailPasswordTag,         mDataFile.getEmailPassword());
                intent.putExtra(Constants.emailUserTag,             mDataFile.getEmailUser());
                intent.putExtra(Constants.emailToTag,               mDataFile.getEmailTo());
                intent.putExtra(Constants.emailHostTag,             mDataFile.getEmailHost());
                intent.putExtra(Constants.emailSubTag,              mDataFile.getEmailSub());
                intent.putExtra(Constants.emailUpdateTag,           mDataFile.getEmailUpdate());
                intent.putExtra(Constants.emailPortTag,             mDataFile.getEmailPort());
                startService(intent);
            }
        });

        //service onDestroy callback method will be called
        mButtonStopService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WhereIsMyPhoneServiceActivity.this, WhereIsMyPhoneService.class);
                stopService(intent);
            }
        });
    }

    public void getWritePermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (
                    ContextCompat.checkSelfPermission(
                            this.getApplicationContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED ||
                            ContextCompat.checkSelfPermission(
                                    this.getApplicationContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE)
                                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{   android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        1);
            } else {
                //do something
            }
        } else {
            //do something
        }
    }
}
