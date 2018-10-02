package com.thriwin.expendio;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveClient;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.OpenFileActivityOptions;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import static com.thriwin.expendio.Utils.isNull;
import static java.lang.String.format;

public class GoogleCloudSynchActivity extends GeneralActivity {
    private static final int REQUEST_CODE_OPEN_ITEM = 6767;
    private static final int REQUEST_CODE_SIGN_IN_WRITE = 0;
    private static final int REQUEST_CODE_SIGN_IN_READ_SETTINGS_ONLY = 1;
    private static final int REQUEST_CODE_GOOGLE_SIGIN_SETUP = 3;

    private TaskCompletionSource<DriveId> mOpenItemTaskSource;
    private View progressBar;
    GoogleSignInAccount lastSignedInAccount;
    Button googleSyncSetup;
    Button stopGoogleSync;
    LinearLayout actionAvailabe;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.google_cloud_sync);
        super.onCreate(savedInstanceState);

        progressBar = findViewById(R.id.indeterminateBar);
        googleSyncSetup = findViewById(R.id.setupGoogleSync);
        actionAvailabe = findViewById(R.id.actionsAvailable);
        updateDisplayName();


        stopGoogleSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isNull(lastSignedInAccount)) {
                    GoogleSignInClient googleSignInClient = buildGoogleSignInClient(GoogleCloudSynchActivity.this);
                    Task<Void> signOutTask = googleSignInClient.signOut();
                    signOutTask.addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            showToast(R.string.logoutSuccessfull);
                            updateDisplayName();
                        }
                    });

                }
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
                        signInAndReadFromGoogleSync();
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

        View collapseButton = findViewById(R.id.collapseExpand);
        View manualHeader = findViewById(R.id.manualHeader);
        View settingManualConatiner = findViewById(R.id.manualSettingCOntainer);
        settingManualConatiner.setVisibility(View.GONE);

        manualHeader.setOnClickListener(getOnClickListener(settingManualConatiner, collapseButton));
        collapseButton.setOnClickListener(getOnClickListener(settingManualConatiner, collapseButton));
    }

    @NonNull
    private View.OnClickListener getOnClickListener(View settingManualConatiner, View collapseButton) {
        return v -> {
            if (settingManualConatiner.getVisibility() == View.VISIBLE) {
                settingManualConatiner.setVisibility(View.GONE);
                collapseButton.setBackgroundResource(R.drawable.ic_expand);
            } else {
                settingManualConatiner.setVisibility(View.VISIBLE);
                collapseButton.setBackgroundResource(R.drawable.ic_collapse);

            }
        };
    }

    private void updateDisplayName() {
        lastSignedInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (isValidAccount(lastSignedInAccount)) {
            googleSyncSetup.setText(format(getString(R.string.syncDetails), lastSignedInAccount.getDisplayName()));
        } else {
            googleSyncSetup.setText(R.string.setupGoogleSync);
        }
        stopGoogleSync = findViewById(R.id.stopGoogleSync);
        if (!isNull(lastSignedInAccount)) {
            stopGoogleSync.setVisibility(View.VISIBLE);
            actionAvailabe.setVisibility(View.VISIBLE);
        } else {
            stopGoogleSync.setVisibility(View.GONE);
            actionAvailabe.setVisibility(View.GONE);

        }
    }

    private void signInAndReadFromGoogleSync() {
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
            saveFileToDrive();

        } else {
            progressBar.setVisibility(View.GONE);
            startActivityForResult(GoogleSignInClient.getSignInIntent(), REQUEST_CODE_SIGN_IN_WRITE);
        }
    }


    public static void silentSignInAndWriteSettingsToGoogleSync(Context activity) {
        if (!isNetworkAvailable()) {
            return;
        }
        GoogleSignInAccount lastSignedInAccount = GoogleSignIn.getLastSignedInAccount(activity);
        if (isValidAccount(lastSignedInAccount)) {
            GoogleSignInClient GoogleSignInClient = buildGoogleSignInClient(activity);
            Task<GoogleSignInAccount> googleSignInAccountTask = GoogleSignInClient.silentSignIn();
            if (googleSignInAccountTask.isSuccessful()) {
                saveFileToDriveBackground(activity);
            }
        }
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
            } else {
                Utils.showToast(activity, R.string.expenseSyncFailure);
            }
        }
    }

    private static boolean isValidAccount(GoogleSignInAccount lastSignedInAccount) {
        return !isNull(lastSignedInAccount) && !lastSignedInAccount.isExpired();
    }


    private void saveFileToDrive() {
        final Task<DriveFolder> appFolderTask = getDriveResourceClient(GoogleCloudSynchActivity.this).getAppFolder();
        final Task<DriveContents> createSettingsTask = getDriveResourceClient(GoogleCloudSynchActivity.this).createContents();
        Tasks.whenAll(appFolderTask, createSettingsTask)
                .continueWithTask(task -> {
                    DriveFolder parent = appFolderTask.getResult();
                    DriveContents settings = createSettingsTask.getResult();

                    OutputStream outputStream = settings.getOutputStream();
                    try (Writer writer = new OutputStreamWriter(outputStream)) {
                        writer.write(Utils.getAllSettingsInfo());
                    }

                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                            .setTitle("settings.json")
                            .setMimeType("text/json")
                            .setStarred(true)
                            .build();
                    return getDriveResourceClient(GoogleCloudSynchActivity.this)
                            .createFile(parent, changeSet, settings);
                })
                .addOnSuccessListener(this,
                        driveFile -> {
                            showToast(getString(R.string.settingsSyncSuccess));
                            progressBar.setVisibility(View.GONE);
                        })
                .addOnFailureListener(this, e -> {
                    showToast(getString(R.string.settingsSyncFailure));
                    progressBar.setVisibility(View.GONE);
                });
    }


    private static void saveFileToDriveBackground(Context activity) {
        final Task<DriveFolder> appFolderTask = getDriveResourceClient(activity).getAppFolder();
        final Task<DriveContents> createSettingsTask = getDriveResourceClient(activity).createContents();
        Tasks.whenAll(appFolderTask, createSettingsTask)
                .continueWithTask(task -> {
                    DriveFolder parent = appFolderTask.getResult();
                    DriveContents settings = createSettingsTask.getResult();

                    OutputStream outputStream = settings.getOutputStream();
                    try (Writer writer = new OutputStreamWriter(outputStream)) {
                        writer.write(Utils.getAllSettingsInfo());
                    }

                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                            .setTitle("settings.json")
                            .setMimeType("text/json")
                            .setStarred(true)
                            .build();
                    return getDriveResourceClient(activity)
                            .createFile(parent, changeSet, settings);
                });
    }

    private static void saveExpenseFileToDriveBackground(Context activity, String expenseKey) {

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
                            .setTitle(Utils.getStoredNameForGoogle() + expenseKey + ".json")
                            .setMimeType("text/json")
                            .setStarred(true)
                            .build();
                    return getDriveResourceClient(activity)
                            .createFile(parent, changeSet, settings);
                });

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
                    saveFileToDrive();
                    break;

                case REQUEST_CODE_SIGN_IN_READ_SETTINGS_ONLY:
                    readSettingsOnly();
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
        final Task<DriveFolder> appFolderTask = getDriveResourceClient(GoogleCloudSynchActivity.this).getAppFolder();
        appFolderTask.addOnSuccessListener(driveFolder -> {
            Task<MetadataBuffer> metadataBufferTask = getDriveResourceClient(GoogleCloudSynchActivity.this).queryChildren(driveFolder, new Query.Builder().addFilter(Filters.eq(SearchableField.TITLE, "settings.json")).build());
            metadataBufferTask.addOnSuccessListener(metadata -> retrieveContents(getDriveFile(metadata, "settings.json")));
        });
    }

    private DriveFile getDriveFile(MetadataBuffer metadata, String fileName) {
        DriveFile d = null;

        int i = 0;
        try {
            for (; ; i++) {
                Metadata metadataInfo = metadata.get(0);
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
                .continueWithTask(task -> {
                    DriveContents contents = task.getResult();
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


                    }
                    Task<Void> discardTask = getDriveResourceClient(GoogleCloudSynchActivity.this).discardContents(contents);
                    return discardTask;
                })
                .addOnFailureListener(e -> {
                    showToast(R.string.googleRestoreSettingFailed);
                    progressBar.setVisibility(View.GONE);

                });
    }

    private Task<DriveId> pickFile() {
        OpenFileActivityOptions openOptions =
                new OpenFileActivityOptions.Builder()
                        .setSelectionFilter(Filters.eq(SearchableField.MIME_TYPE, "text/plain"))
                        .setActivityTitle(getString(R.string.select_file))
                        .build();

        mOpenItemTaskSource = new TaskCompletionSource<>();
        getDriveClient(GoogleCloudSynchActivity.this)
                .newOpenFileActivityIntentSender(openOptions)
                .continueWith((Continuation<IntentSender, Void>) task -> {
                    startIntentSenderForResult(
                            task.getResult(), REQUEST_CODE_OPEN_ITEM, null, 0, 0, 0);
                    return null;
                });
        return mOpenItemTaskSource.getTask();
    }


}
