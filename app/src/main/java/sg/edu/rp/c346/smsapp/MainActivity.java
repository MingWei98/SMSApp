package sg.edu.rp.c346.smsapp;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    EditText etTo;
    EditText etContent;
    Button btnSend;
    Button btnSendMsg;

    BroadcastReceiver br = new MessageReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        this.registerReceiver(br, filter);

        etTo = findViewById(R.id.editTextTo);
        etContent = findViewById(R.id.editTextContent);
        btnSend = findViewById(R.id.buttonSend);
        btnSendMsg = findViewById(R.id.buttonSendMsg);

        checkPermission();

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String receiver = etTo.getText().toString();
                String content = etContent.getText().toString();

                // Enhancement 1
                String[] receiverList = receiver.split(",");

                for (int i=0; i<receiverList.length; i++){
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(receiverList[i], null, content, null, null);
                }

            }
        });

        btnSendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String receiver = etTo.getText().toString();
                String content = etContent.getText().toString();
                
                // Enhancement 2
                Uri smsUri = Uri.parse("tel:" + receiver);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setType("vnd.android-dir/mms-sms");//here setType will set the previous data null.
                intent.putExtra("address", receiver);
                intent.putExtra("sms_body", content);

                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }

            }
        });
    }

    private void checkPermission() {
        int permissionSendSMS = ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS);
        int permissionRecvSMS = ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECEIVE_SMS);
        if (permissionSendSMS != PackageManager.PERMISSION_GRANTED &&
                permissionRecvSMS != PackageManager.PERMISSION_GRANTED) {
            String[] permissionNeeded = new String[]{Manifest.permission.SEND_SMS,
                    Manifest.permission.RECEIVE_SMS};
            ActivityCompat.requestPermissions(this, permissionNeeded, 1);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(br);
    }
}
