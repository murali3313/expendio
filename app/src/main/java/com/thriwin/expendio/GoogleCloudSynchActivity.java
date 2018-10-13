package com.thriwin.expendio;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveClient;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResource;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;

import static com.thriwin.expendio.RecurringExpensesAlarmReceiver.genaralTips;
import static com.thriwin.expendio.Utils.getStoredNameForGoogle;
import static com.thriwin.expendio.Utils.isEmpty;
import static com.thriwin.expendio.Utils.isNull;
import static java.lang.String.format;

public class GoogleCloudSynchActivity extends GeneralActivity implements PopupMenu.OnMenuItemClickListener {
    private static final int REQUEST_CODE_OPEN_ITEM = 6767;
    private static final int REQUEST_CODE_SIGN_IN_WRITE = 0;
    private static final int REQUEST_CODE_SIGN_IN_READ_SETTINGS_ONLY = 1;
    private static final int REQUEST_CODE_SIGN_IN_READ_EXPENSES_ONLY = 2;
    private static final int REQUEST_CODE_GOOGLE_SIGIN_SETUP = 3;

    private TaskCompletionSource<DriveId> mOpenItemTaskSource;
    private View progressBar;
    GoogleSignInAccount lastSignedInAccount;
    Button googleSyncSetup;
    Button stopGoogleSync;
    LinearLayout actionAvailabe;
    private LinearLayout googleOwnershipContainer;

    @Override
    public void onBackPressed() {
        if (Utils.isSyncingEnabled()) {
            super.onBackPressed();
        } else {
            createDialog();
        }
    }

    private void createDialog() {

        AlertDialog.Builder alertDlg = new AlertDialog.Builder(this);
        alertDlg.setMessage("You have not activated backgroud cloud sync. \n 1) Please setup a Google account" +
                "\n 2) Select appropriate ownership and press \nUPDATE ACCOUNT OWNERSHIP & ACTIVATE SYNCHING button");
        alertDlg.setIcon(R.mipmap.ic_launcher);
        alertDlg.setTitle("Expendio");
        alertDlg.setPositiveButton("Will setup later", (dialog, id) -> GoogleCloudSynchActivity.super.onBackPressed());

        alertDlg.setNegativeButton("Stay back", (dialog, which) -> {
        });
        alertDlg.create().show();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.google_cloud_sync);
        super.onCreate(savedInstanceState);

        progressBar = findViewById(R.id.indeterminateBar);
        googleSyncSetup = findViewById(R.id.setupGoogleSync);
        actionAvailabe = findViewById(R.id.actionsAvailable);
        googleOwnershipContainer = findViewById(R.id.googleOwnershipContainer);
        stopGoogleSync = findViewById(R.id.stopGoogleSync);
        updateDisplayName();


        stopGoogleSync.setOnClickListener(v -> {
            if (!isNull(lastSignedInAccount)) {
                GoogleSignInClient googleSignInClient = buildGoogleSignInClient(GoogleCloudSynchActivity.this);
                Task<Void> signOutTask = googleSignInClient.signOut();
                signOutTask.addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showToast(R.string.logoutSuccessfull);
                        updateDisplayName();
                        Utils.enableSyncing(false);
                    }
                });

            }
        });

        googleSyncSetup.setOnClickListener(v -> {
            GoogleSignInClient googleSignInClient = buildGoogleSignInClient(GoogleCloudSynchActivity.this);
            googleSignInClient.signOut();
            progressBar.setVisibility(View.VISIBLE);
            Intent signInIntent = googleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, REQUEST_CODE_GOOGLE_SIGIN_SETUP);
        });
        findViewById(R.id.syncGoogleAccountSettings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInAndWriteSettingsToGoogleSync();
            }
        });
        findViewById(R.id.pullSettingsFromGoogleAccount).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View sheetView = View.inflate(GoogleCloudSynchActivity.this, R.layout.bottom_restore_settings_confirmation, null);
                BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(GoogleCloudSynchActivity.this);
                mBottomSheetDialog.setContentView(sheetView);
                ((View) sheetView.getParent()).setBackgroundColor(getResources().getColor(R.color.transparentOthers));
                mBottomSheetDialog.show();

                mBottomSheetDialog.findViewById(R.id.removeContinue).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        progressBar.setVisibility(View.VISIBLE);
                        signInAndReadSettingsFromGoogleSync();
                        mBottomSheetDialog.cancel();
                    }
                });

                mBottomSheetDialog.findViewById(R.id.removeCancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mBottomSheetDialog.cancel();
                    }
                });
            }
        });
        findViewById(R.id.pullExpensesFromGoogle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View sheetView = View.inflate(GoogleCloudSynchActivity.this, R.layout.bottom_restore_expenses_confirmation, null);
                BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(GoogleCloudSynchActivity.this);
                mBottomSheetDialog.setContentView(sheetView);
                ((View) sheetView.getParent()).setBackgroundColor(getResources().getColor(R.color.transparentOthers));
                mBottomSheetDialog.show();

                mBottomSheetDialog.findViewById(R.id.removeContinue).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        progressBar.setVisibility(View.VISIBLE);
                        signInAndReadExpensesFromGoogleSync();
                        mBottomSheetDialog.cancel();
                    }
                });

                mBottomSheetDialog.findViewById(R.id.removeCancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mBottomSheetDialog.cancel();
                    }
                });
            }
        });

        findViewById(R.id.syncGoogleAccountExpense).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);

                Handler handler = new Handler(new Handler.Callback() {
                    @Override
                    public boolean handleMessage(Message msg) {

                        progressBar.setVisibility(View.GONE);
                        if ((Boolean) msg.obj) {
                            showToast(R.string.AllExpensesAreBackedUp);
                        } else {
                            showToast(R.string.AllExpensesBackedUpFailure);
                        }
                        return false;
                    }
                });

                List<String> allExpensesStorageKeys = Utils.getAllExpensesStorageKeys();
                ExpenseUploader expenseUploader = new ExpenseUploader(GoogleCloudSynchActivity.this, allExpensesStorageKeys, handler);
                expenseUploader.start();


            }
        });
        View collapseButton = findViewById(R.id.collapseExpand);
        View manualHeader = findViewById(R.id.manualHeader);
        View settingManualConatiner = findViewById(R.id.manualSettingContainer);
        manualHeader.setOnClickListener(getOnClickListener(settingManualConatiner, collapseButton));
        collapseButton.setOnClickListener(getOnClickListener(settingManualConatiner, collapseButton));
        settingManualConatiner.setVisibility(View.VISIBLE);
        collapseExpand(settingManualConatiner, collapseButton);

        updateGoogleAccountOwnership();
    }

    private void updateGoogleAccountOwnership() {
        LinearLayout otherAccountContainer = findViewById(R.id.otherAccountContainer);
        RadioButton myAccountOption = findViewById(R.id.myAccount);
        RadioButton otherAccountOption = findViewById(R.id.otherAccount);
        EditText primaryUserName = findViewById(R.id.othersName);
        EditText yourName = findViewById(R.id.yourName);

        final String storedNameForPrimaryGoogle = Utils.getStoredNameForPrimaryGoogle();
        if (!Utils.getStoredNameForGoogle().equalsIgnoreCase("Yours-")) {
            otherAccountContainer.setVisibility(View.VISIBLE);
            otherAccountOption.setChecked(true);
            defaultAccountNameIfPrimaryUserisNotSet(storedNameForPrimaryGoogle, primaryUserName, yourName);
        }

        myAccountOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otherAccountContainer.setVisibility(View.GONE);
            }
        });

        otherAccountOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otherAccountContainer.setVisibility(View.VISIBLE);
                defaultAccountNameIfPrimaryUserisNotSet(storedNameForPrimaryGoogle, primaryUserName, yourName);
            }
        });

        findViewById(R.id.updateGoogleOwnership).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (otherAccountOption.isChecked()) {
                    String yourNameString = yourName.getText().toString();
                    String primaryName = primaryUserName.getText().toString();
                    if (isEmpty(yourNameString) || isEmpty(primaryName)) {
                        showToast(R.string.bothYourNameAndPrimaryUserNamesAreMandatory);
                    } else {
                        Utils.setStoredNameForGoogle(yourNameString);
                        Utils.setStoredNameForPrimaryGoogle(primaryName);
                        Utils.enableSyncing(true);
                        showToast(R.string.ownershipUpdatedSuccessfully);
                    }
                } else {
                    Utils.setStoredNameForGoogle("");
                    Utils.setStoredNameForPrimaryGoogle("");
                    Utils.enableSyncing(true);
                    showToast(R.string.ownershipUpdatedSuccessfully);
                }

            }
        });
    }

    private void defaultAccountNameIfPrimaryUserisNotSet(String storedNameForPrimaryGoogle, EditText primaryUserName, EditText yourName) {
        if (isEmpty(storedNameForPrimaryGoogle) || storedNameForPrimaryGoogle.equalsIgnoreCase("PrimaryUser-")) {
            primaryUserName.setText(lastSignedInAccount.getDisplayName().substring(0, 7));
        } else {
            primaryUserName.setText(storedNameForPrimaryGoogle.replace("-", ""));
        }
        String storedNameForGoogle = Utils.getStoredNameForGoogle();
        if (!storedNameForGoogle.equalsIgnoreCase("Yours-"))
            yourName.setText(storedNameForGoogle.replace("-", ""));

    }

    private void signInAndReadExpensesFromGoogleSync() {
        if (!isNetworkAvailable()) {
            Utils.showToast(GoogleCloudSynchActivity.this, R.string.noNetworkForGoogleSync);
            progressBar.setVisibility(View.GONE);
            return;
        }
        GoogleSignInClient GoogleSignInClient = buildGoogleSignInClient(this);
        Task<GoogleSignInAccount> googleSignInAccountTask = GoogleSignInClient.silentSignIn();
        if (googleSignInAccountTask.isSuccessful()) {
            readAllExpenses();
        } else {
            progressBar.setVisibility(View.GONE);
            startActivityForResult(GoogleSignInClient.getSignInIntent(), REQUEST_CODE_SIGN_IN_READ_EXPENSES_ONLY);
        }
    }

    private void readAllExpenses() {
        getDriveClient(GoogleCloudSynchActivity.this).requestSync().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                final Task<DriveFolder> appFolderTask = getDriveResourceClient(GoogleCloudSynchActivity.this).getAppFolder();
                appFolderTask.addOnSuccessListener(driveFolder -> {
                    Task<MetadataBuffer> metadataBufferTask = getDriveResourceClient(GoogleCloudSynchActivity.this).queryChildren(driveFolder, new Query.Builder().build());
                    metadataBufferTask.addOnSuccessListener(metadata -> {
                        try {
                            for (int i = 0; ; i++) {
                                String fileName = metadata.get(i).getTitle();
                                String modifiedTime = String.valueOf(metadata.get(i).getModifiedDate().getTime());
                                if (!fileName.contains("settings.json") && !Utils.lastBackgroundSyncDoneFor(fileName).equalsIgnoreCase(modifiedTime)) {
                                    retrieveExpenseContents(GoogleCloudSynchActivity.this, getDriveFile(metadata, fileName), fileName, false, modifiedTime, progressBar);
                                }
                            }
                        } catch (Exception e) {

                        } finally {
                            metadata.release();
                        }
                    });
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showToast(R.string.settingsSyncFailure);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    public static void readExpense(Context context, String expenseKey) {

        getDriveClient(context).requestSync().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                final Task<DriveFolder> appFolderTask = getDriveResourceClient(context).getAppFolder();
                appFolderTask.addOnSuccessListener(driveFolder -> {
                    Task<MetadataBuffer> metadataBufferTask = getDriveResourceClient(context).queryChildren(driveFolder, new Query.Builder().build());
                    metadataBufferTask.addOnSuccessListener(metadata -> {
                        try {
                            for (int i = 0; ; i++) {
                                String fileName = metadata.get(i).getTitle();
                                String modifiedTime = String.valueOf(metadata.get(i).getModifiedDate().getTime());
                                if (fileName.contains(expenseKey) && !fileName.startsWith(Utils.getStoredNameForGoogle()) && !Utils.lastBackgroundSyncDoneFor(fileName).equalsIgnoreCase(modifiedTime)) {
                                    retrieveExpenseContents(context, getDriveFile(metadata, fileName), fileName, true, modifiedTime, null);
                                }
                            }
                        } catch (Exception e) {

                        }
                    });
                });
            }
        });
    }

    @NonNull
    private View.OnClickListener getOnClickListener(View settingManualConatiner, View collapseButton) {
        return v -> {
            collapseExpand(settingManualConatiner, collapseButton);
        };
    }

    private void collapseExpand(View settingManualConatiner, View collapseButton) {
        if (settingManualConatiner.getVisibility() == View.VISIBLE) {
            settingManualConatiner.setVisibility(View.GONE);
            collapseButton.setBackgroundResource(R.drawable.ic_expand);
        } else {
            settingManualConatiner.setVisibility(View.VISIBLE);
            collapseButton.setBackgroundResource(R.drawable.ic_collapse);

        }
    }

    private void updateDisplayName() {
        lastSignedInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (isValidAccount(lastSignedInAccount)) {
            googleSyncSetup.setText(format(getString(R.string.syncDetails), lastSignedInAccount.getDisplayName()));
            stopGoogleSync.setVisibility(View.VISIBLE);
            actionAvailabe.setVisibility(View.VISIBLE);
            googleOwnershipContainer.setVisibility(View.VISIBLE);
        } else {
            googleSyncSetup.setText(R.string.setupGoogleSync);
            stopGoogleSync.setVisibility(View.GONE);
            actionAvailabe.setVisibility(View.GONE);
            googleOwnershipContainer.setVisibility(View.GONE);
        }
        TextView lastSettingsBackedOn = findViewById(R.id.lastBackedUpOn);
        lastSettingsBackedOn.setText(Utils.getLastSettingBackedOn());

    }

    private void signInAndReadSettingsFromGoogleSync() {
        if (!isNetworkAvailable()) {
            Utils.showToast(GoogleCloudSynchActivity.this, R.string.noNetworkForGoogleSync);
            progressBar.setVisibility(View.GONE);
            return;
        }
        GoogleSignInClient GoogleSignInClient = buildGoogleSignInClient(this);
        Task<GoogleSignInAccount> googleSignInAccountTask = GoogleSignInClient.silentSignIn();
        if (googleSignInAccountTask.isSuccessful()) {
            readSettingsOnly();
        } else {
            progressBar.setVisibility(View.GONE);
            startActivityForResult(GoogleSignInClient.getSignInIntent(), REQUEST_CODE_SIGN_IN_READ_SETTINGS_ONLY);
        }
    }

    private void signInAndWriteSettingsToGoogleSync() {
        if (!isNetworkAvailable()) {
            showToast(R.string.noNetworkForGoogleSync);
            progressBar.setVisibility(View.GONE);
            return;
        }
        GoogleSignInClient GoogleSignInClient = buildGoogleSignInClient(this);
        Task<GoogleSignInAccount> googleSignInAccountTask = GoogleSignInClient.silentSignIn();
        if (googleSignInAccountTask.isSuccessful()) {
            saveSettingsFileToDrive();

        } else {
            progressBar.setVisibility(View.GONE);
            startActivityForResult(GoogleSignInClient.getSignInIntent(), REQUEST_CODE_SIGN_IN_WRITE);
        }
    }


    public static boolean silentSignInAndWriteSettingsToGoogleSync(Context activity) {
        if (!isNetworkAvailable()) {
            return false;
        }
        GoogleSignInAccount lastSignedInAccount = GoogleSignIn.getLastSignedInAccount(activity);
        if (isValidAccount(lastSignedInAccount)) {
            GoogleSignInClient GoogleSignInClient = buildGoogleSignInClient(activity);
            Task<GoogleSignInAccount> googleSignInAccountTask = GoogleSignInClient.silentSignIn();
            if (googleSignInAccountTask.isSuccessful()) {
                saveFileToDriveBackground(activity);
            } else {
                return false;
            }
        } else {
            return false;
        }
        return true;

    }

    public static void silentSignInAndWriteMyExpenseToGoogleSync(Context activity, String expenseKey) {
        if (!isNetworkAvailable()) {
            return;
        }
        GoogleSignInAccount lastSignedInAccount = GoogleSignIn.getLastSignedInAccount(activity);
        if (isValidAccount(lastSignedInAccount)) {
            GoogleSignInClient GoogleSignInClient = buildGoogleSignInClient(activity);
            Task<GoogleSignInAccount> googleSignInAccountTask = GoogleSignInClient.silentSignIn();
            if (googleSignInAccountTask.isSuccessful()) {
                saveExpenseFileToDriveBackground(activity, expenseKey);
            }
        }
    }

    private static boolean isValidAccount(GoogleSignInAccount lastSignedInAccount) {
//        return true;
        return !isNull(lastSignedInAccount);
    }


    private void saveSettingsFileToDrive() {

        final Task<DriveFolder> appFolderTask = getDriveResourceClient(GoogleCloudSynchActivity.this).getAppFolder();
        appFolderTask.addOnSuccessListener(driveFolder -> {
            Task<MetadataBuffer> metadataBufferTask = getDriveResourceClient(GoogleCloudSynchActivity.this).queryChildren(driveFolder, new Query.Builder().build());
            metadataBufferTask.addOnSuccessListener(metadata -> {
                Task<Void> voidTask = deleteContents(GoogleCloudSynchActivity.this, metadata, getStoredNameForGoogle() + "settings.json");
                if (!isNull(voidTask)) {
                    voidTask.addOnSuccessListener(aVoid -> {
                        AfterDeletionCreateSettingFile();
                    });
                } else {
                    AfterDeletionCreateSettingFile();
                }
            });
        });


    }

    private void AfterDeletionCreateSettingFile() {
        final Task<DriveFolder> appFolderTask1 = getDriveResourceClient(GoogleCloudSynchActivity.this).getAppFolder();
        final Task<DriveContents> createSettingsTask = getDriveResourceClient(GoogleCloudSynchActivity.this).createContents();
        Tasks.whenAll(appFolderTask1, createSettingsTask)
                .continueWithTask(task -> {
                    DriveFolder parent = appFolderTask1.getResult();
                    DriveContents settings = createSettingsTask.getResult();

                    OutputStream outputStream = settings.getOutputStream();
                    try (Writer writer = new OutputStreamWriter(outputStream)) {
                        writer.write(Utils.getAllSettingsInfo());
                    }

                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                            .setTitle(getStoredNameForGoogle() + "settings.json")
                            .setMimeType("text/json")
                            .setStarred(true)
                            .build();

                    return getDriveResourceClient(GoogleCloudSynchActivity.this)
                            .createFile(parent, changeSet, settings);
                }).addOnSuccessListener(this,
                driveFile -> {
                    showToast(getString(R.string.settingsSyncSuccess));
                    Utils.markSettingsForSyncing(false);
                    updateDisplayName();
                    progressBar.setVisibility(View.GONE);
                    getDriveClient(GoogleCloudSynchActivity.this).requestSync();
                })
                .addOnFailureListener(this, e -> {
                    showToast(getString(R.string.settingsSyncFailure));
                    progressBar.setVisibility(View.GONE);
                });
    }

    private static Task<Void> deleteContents(Context activity, MetadataBuffer metadata, String fileName) {
        DriveResource d = null;

        int i = 0;
        try {
            for (; ; i++) {
                Metadata metadataInfo = metadata.get(i);
                if (metadataInfo.getTitle().startsWith(fileName)) {
                    d = metadataInfo.getDriveId().asDriveResource();
                    return getDriveResourceClient(activity).delete(d);
                }
            }
        } catch (Exception e) {

        } finally {
            metadata.release();
        }

        return null;
    }


    private static void saveFileToDriveBackground(Context activity) {
        final Task<DriveFolder> appFolderTask = getDriveResourceClient(activity).getAppFolder();
        final Task<DriveContents> createSettingsTask = getDriveResourceClient(activity).createContents();
        Task<Void> voidTask = Tasks.whenAll(appFolderTask, createSettingsTask);
        voidTask.continueWithTask(task -> {
            DriveFolder parent = appFolderTask.getResult();
            DriveContents settings = createSettingsTask.getResult();

            OutputStream outputStream = settings.getOutputStream();
            try (Writer writer = new OutputStreamWriter(outputStream)) {
                writer.write(Utils.getAllSettingsInfo());
            }

            MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                    .setTitle(getStoredNameForGoogle() + "settings.json")
                    .setMimeType("text/json")
                    .setStarred(true)
                    .build();
            return getDriveResourceClient(activity)
                    .createFile(parent, changeSet, settings);
        });
        voidTask.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Utils.markSettingsForSyncing(false);
            }
        });
    }

    private static void saveExpenseFileToDriveBackground(Context activity, String expenseKey) {

        final Task<DriveFolder> appFolderTask = getDriveResourceClient(activity).getAppFolder();
        appFolderTask.addOnSuccessListener(driveFolder -> {
            Task<MetadataBuffer> metadataBufferTask = getDriveResourceClient(activity).queryChildren(driveFolder, new Query.Builder().build());
            metadataBufferTask.addOnSuccessListener(metadata -> {
                Task<Void> voidTask = deleteContents(activity, metadata, getExpenseFileName(expenseKey));
                if (!isNull(voidTask)) {
                    voidTask.addOnSuccessListener(aVoid -> {
                        afterDeletionCreateExpenseFile(activity, expenseKey);
                    });
                } else {
                    afterDeletionCreateExpenseFile(activity, expenseKey);
                }
            });
        });


    }

    private static void afterDeletionCreateExpenseFile(Context activity, String expenseKey) {
        final Task<DriveFolder> appFolderTask = getDriveResourceClient(activity).getAppFolder();
        final Task<DriveContents> dataTask = getDriveResourceClient(activity).createContents();
        Tasks.whenAll(appFolderTask, dataTask)
                .continueWithTask(task -> {
                    DriveFolder parent = appFolderTask.getResult();
                    DriveContents settings = dataTask.getResult();

                    OutputStream outputStream = settings.getOutputStream();
                    try (Writer writer = new OutputStreamWriter(outputStream)) {
                        writer.write(Utils.getExpenseInfo(expenseKey));
                    }

                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                            .setTitle(getExpenseFileName(expenseKey))
                            .setMimeType("text/json")
                            .setStarred(true)
                            .build();
                    return getDriveResourceClient(activity)
                            .createFile(parent, changeSet, settings);
                }).addOnSuccessListener(new OnSuccessListener<DriveFile>() {
            @Override
            public void onSuccess(DriveFile driveFile) {
                Utils.expenseSyncingDone(expenseKey);
                if (!Utils.isExpenseForSyncing()) {
                    getDriveClient(activity).requestSync().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            if (!ExpendioSettings.loadExpendioSettings().getBlockSync()) {
                                NotificationScheduler.showNotification(activity, HomeScreenActivity.class,
                                        "Expendio", "Backing up your expenses was successful...", genaralTips.get(Utils.getTipsIndex()), "HOME");
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if (((ApiException) e).getStatusCode() == 1507) {
                                Utils.expenseSyncingDone(expenseKey);
                            }
                            if (!ExpendioSettings.loadExpendioSettings().getBlockSync()) {
                                NotificationScheduler.showNotification(activity, HomeScreenActivity.class,
                                        "Expendio", "Backup is successful, but will take some time for it to reach the Google servers...", genaralTips.get(Utils.getTipsIndex()), "HOME");
                            }
                        }
                    });
                }
            }
        });
    }

    @NonNull
    private static String getExpenseFileName(String expenseKey) {
        return Utils.getStoredNameForGoogle() + expenseKey + ".json";
    }

    private static GoogleSignInClient buildGoogleSignInClient(Context activity) {
        GoogleSignInOptions signInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestScopes(Drive.SCOPE_APPFOLDER)
                        .build();
        return GoogleSignIn.getClient(activity, signInOptions);
    }


    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_SIGN_IN_WRITE:
                    saveSettingsFileToDrive();
                    break;

                case REQUEST_CODE_SIGN_IN_READ_SETTINGS_ONLY:
                    readSettingsOnly();
                    break;
                case REQUEST_CODE_SIGN_IN_READ_EXPENSES_ONLY:
                    readAllExpenses();
                    break;
                case REQUEST_CODE_GOOGLE_SIGIN_SETUP:
                    updateDisplayName();
                    showToast(R.string.googleAccountSetupSuccesfully);
                    break;
            }
        } else {
            showToast(R.string.googleAccountFailure);
        }

        progressBar.setVisibility(View.GONE);
    }

    private void readSettingsOnly() {
        getDriveClient(GoogleCloudSynchActivity.this).requestSync().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                settingsRead();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (((ApiException) e).getStatusCode() == 1507) {
                    showToast(R.string.settingsSyncFailure);
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void settingsRead() {
        final Task<DriveFolder> appFolderTask = getDriveResourceClient(GoogleCloudSynchActivity.this).getAppFolder();
        appFolderTask.addOnSuccessListener(driveFolder -> {
            Task<MetadataBuffer> metadataBufferTask = getDriveResourceClient(GoogleCloudSynchActivity.this).queryChildren(driveFolder, new Query.Builder().build());
            metadataBufferTask.addOnSuccessListener(metadata -> retrieveContents(getDriveFile(metadata, getStoredNameForGoogle() + "settings.json")));
        });
    }

    private static DriveFile getDriveFile(MetadataBuffer metadata, String fileName) {
        DriveFile d = null;

        int i = 0;
        try {
            for (; ; i++) {
                Metadata metadataInfo = metadata.get(i);
                if (metadataInfo.getTitle().startsWith(fileName)) {
                    d = metadataInfo.getDriveId().asDriveFile();
                    break;
                }
            }
        } catch (Exception e) {

        }
        return d;
    }

    @NonNull
    private static DriveResourceClient getDriveResourceClient(Context activity) {
        return Drive.getDriveResourceClient(activity, GoogleSignIn.getLastSignedInAccount(activity));
    }

    @NonNull
    private static DriveClient getDriveClient(Context activity) {
        return Drive.getDriveClient(activity, GoogleSignIn.getLastSignedInAccount(activity));

    }


    private void retrieveContents(DriveFile file) {
        Task<DriveContents> openFileTask = getDriveResourceClient(GoogleCloudSynchActivity.this).openFile(file, DriveFile.MODE_READ_ONLY);
        openFileTask
                .addOnSuccessListener(new OnSuccessListener<DriveContents>() {
                    @Override
                    public void onSuccess(DriveContents driveContents) {
                        DriveContents contents = driveContents;
                        try (BufferedReader reader = new BufferedReader(
                                new InputStreamReader(contents.getInputStream()))) {
                            StringBuilder builder = new StringBuilder();
                            String line;
                            while ((line = reader.readLine()) != null) {
                                builder.append(line).append("\n");
                            }

                            Utils.readAndSaveSettings(builder.toString());
                            showToast(R.string.googleRestoreSettingSuccess);
                            progressBar.setVisibility(View.GONE);


                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

    }

    private static void retrieveExpenseContents(Context context, DriveFile file, String storedInGoogleFileName, boolean isBackground, String modifiedTime, View progressBar) {
        Task<DriveContents> openFileTask = getDriveResourceClient(context).openFile(file, DriveFile.MODE_READ_ONLY);

        openFileTask.addOnSuccessListener(new OnSuccessListener<DriveContents>() {
            @Override
            public void onSuccess(DriveContents driveContents) {
                DriveContents contents = driveContents;
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(contents.getInputStream()))) {
                    StringBuilder builder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        builder.append(line).append("\n");
                    }

                    String expenseKey = getExpenseKey(storedInGoogleFileName);
                    Utils.saveExpense(expenseKey, builder.toString(), getExpenseFor(storedInGoogleFileName));
                    Utils.lastBackgroundSyncDone(storedInGoogleFileName, modifiedTime);
                    if (isBackground) {
                        if (!ExpendioSettings.loadExpendioSettings().getBlockSync()) {
                            String fromUser = storedInGoogleFileName.substring(0, storedInGoogleFileName.indexOf("-"));
                            fromUser = fromUser.toLowerCase().contains("yours") ? Utils.getStoredNameForPrimaryGoogle() : fromUser;

                            NotificationScheduler.showNotification(context, HomeScreenActivity.class,
                                    "Expendio", "Expenses from user " +
                                            fromUser + " pulled into your phone."
                                    , genaralTips.get(Utils.getTipsIndex()), "HOME");
                        }
                    } else {
                        Utils.showToast(context, R.string.googleRestoreExpensesSuccess);
                        progressBar.setVisibility(View.GONE);
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).addOnFailureListener(e -> {
            if (!isBackground) {
                Utils.showToast(context, R.string.googleRestoreExpensesFailed);
            }
        });


    }

    private static String getExpenseKey(String storedInGoogleFileName) {
        String keyName;
        String whoAreYou = Utils.getStoredNameForGoogle();
        String whoisPrimary = Utils.getStoredNameForPrimaryGoogle();
        if (whoAreYou.equalsIgnoreCase("Yours-")) {
            if (storedInGoogleFileName.startsWith(whoAreYou)) {
                keyName = storedInGoogleFileName.replaceFirst(whoAreYou, "");
            } else {
                keyName = storedInGoogleFileName;
            }
        } else {
            if (storedInGoogleFileName.startsWith(whoAreYou)) {
                keyName = storedInGoogleFileName.replaceFirst(whoAreYou, "");
            } else if (storedInGoogleFileName.startsWith("Yours-")) {
                keyName = storedInGoogleFileName.replaceFirst("Yours-", whoisPrimary);
            } else {
                keyName = storedInGoogleFileName;
            }

        }
        return keyName.replace(".json", "");
    }

    private static String getExpenseFor(String storedInGoogleFileName) {
        String whoSpent;
        String whoAreYou = Utils.getStoredNameForGoogle();
        String whoisPrimary = Utils.getStoredNameForPrimaryGoogle();
        if (whoAreYou.equalsIgnoreCase("Yours-")) {
            if (storedInGoogleFileName.startsWith(whoAreYou)) {
                whoSpent = "You";
            } else {
                whoSpent = storedInGoogleFileName.substring(0, storedInGoogleFileName.indexOf("-"));
            }
        } else {
            if (storedInGoogleFileName.startsWith(whoAreYou)) {
                whoSpent = "You";
            } else if (storedInGoogleFileName.startsWith("Yours-")) {
                whoSpent = whoisPrimary;
            } else {
                whoSpent = storedInGoogleFileName.substring(0, storedInGoogleFileName.indexOf("-"));
            }

        }
        return whoSpent.replace(".json", "");
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return false;
    }
}
