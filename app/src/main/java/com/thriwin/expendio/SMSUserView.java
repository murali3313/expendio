package com.thriwin.expendio;

import android.content.Context;
import android.support.design.widget.BottomSheetDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import static com.thriwin.expendio.ExpenseShareActivity.SEND_SMS_CODE;

class SMSUserView extends LinearLayout {
    private User User;
    private ExpenseShareActivity expenseShareActivity;
    private String expenseStorageKey;
    SMSenderService smsSenderService;
    TextView userName;
    EditText userPhone;


    public SMSUserView(Context context, LinearLayout smsDetails, User User, ExpenseShareActivity expenseShareActivity, String expenseStorageKey) {
        super(context, null);
        this.User = User;
        this.expenseShareActivity = expenseShareActivity;
        this.expenseStorageKey = expenseStorageKey;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.sms_user_view, this);
        smsSenderService = new SMSenderService();

        userName = findViewById(R.id.userName);

        userPhone = findViewById(R.id.userPhone);

        userName.setText(User.getName());
        userPhone.setText(User.getNumber());


        EditText phoneNumber = findViewById(R.id.userPhone);

        findViewById(R.id.sync).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                User.setNumber(phoneNumber.getText().toString());
                expenseShareActivity.requestSMSPermission(SMSUserView.this, SEND_SMS_CODE);
            }
        });

    }

    public void prepareToSendMessage() {
        View sheetView = View.inflate(getContext(), R.layout.bottom_send_sms_confirmation, null);
        BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(expenseShareActivity);
        mBottomSheetDialog.setContentView(sheetView);
        mBottomSheetDialog.show();

        mBottomSheetDialog.findViewById(R.id.removeContinue).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                smsSenderService.sendExpenses(User, expenseStorageKey);

                Utils.showToast(getContext(), R.string.smsSendSuccesfully);

                mBottomSheetDialog.cancel();
            }
        });

        mBottomSheetDialog.findViewById(R.id.removeCancel).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetDialog.cancel();
            }
        });
    }

    public User getSmsUser() {
        User user = new User();
        user.setName(userName.getText().toString());
        user.setNumber(userPhone.getText().toString());
        return user;
    }

    public void setName(String name) {
        userName.setText(name);
        User.setName(name);
    }
}
