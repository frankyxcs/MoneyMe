package com.devmoroz.moneyme.export.drive;


import android.content.Context;

import com.devmoroz.moneyme.R;
import com.devmoroz.moneyme.export.ExportParams;
import com.devmoroz.moneyme.utils.Preferences;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class GoogleDriveClient {

    private final Context context;
    private GoogleApiClient googleApiClient;

    public GoogleDriveClient(Context context) {
        this.context = context;
    }

    public ConnectionResult connect() { //throws ImportExportException {
        if (googleApiClient == null) {
            String googleDriveAccount = Preferences.getGoogleDriveAccount(context);
            if (googleDriveAccount == null) {
                //throw new ImportExportException(context.getString(R.string.google_drive_account_required));
            }
            googleApiClient = new GoogleApiClient.Builder(context)
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    .addScope(Drive.SCOPE_APPFOLDER)
                    .setAccountName(googleDriveAccount)
                    .build();
        }
        return googleApiClient.blockingConnect(1, TimeUnit.MINUTES);
    }

    public void disconnect() {
        if (googleApiClient != null) {
            googleApiClient.disconnect();
        }
    }

    public void doDriveBackup(String fileName, byte[] fileBytes) {
        try {
            String targetFolder = getDriveFolderName();
            ConnectionResult connectionResult = connect();
            if (connectionResult.isSuccess()) {
                DriveFolder folder = getDriveFolder(targetFolder);
                Status status = createFile(folder, fileName, fileBytes);
                if (status.isSuccess()) {
                    handleSuccess(fileName);
                } else {
                    handleFailure(status);
                }
            } else {
                handleConnectionResult(connectionResult);
            }
        } catch (Exception e) {
            handleError(e);
        }
    }

    public void doDriveRestore(DriveId driveId) {
        try {
            String targetFolder = getDriveFolderName();
            ConnectionResult connectionResult = connect();
            if (connectionResult.isSuccess()) {
                DriveFolder folder = getDriveFolder(targetFolder);
                DriveFile file = driveId.asDriveFile();
                DriveApi.DriveContentsResult contentsResult = file.open(googleApiClient, DriveFile.MODE_READ_ONLY, null).await();
                if (contentsResult.getStatus().isSuccess()) {
                    DriveContents contents = contentsResult.getDriveContents();
                    try {
                        //do restore
                    } finally {
                        contents.discard(googleApiClient);
                    }
                } else {
                    handleFailure(contentsResult.getStatus());
                }
            } else {
                handleConnectionResult(connectionResult);
            }
        } catch (Exception e) {
            handleError(e);
        }
    }

    public void getDriveListFiles() {
        try {
            String targetFolder = getDriveFolderName();
            ConnectionResult connectionResult = connect();
            if (connectionResult.isSuccess()) {
                DriveFolder folder = getDriveFolder(targetFolder);
                Query query = new Query.Builder()
                        .addFilter(Filters.and(
                                Filters.eq(SearchableField.MIME_TYPE, ExportParams.ExportType.Backup.getMimeType()),
                                Filters.eq(SearchableField.TRASHED, false)
                        ))
                        .build();
                DriveApi.MetadataBufferResult metadataBufferResult = folder.queryChildren(googleApiClient, query).await();
                if (metadataBufferResult.getStatus().isSuccess()) {
                    List<DriveFileInfo> driveFiles = fetchFiles(metadataBufferResult);
                    handleSuccess(driveFiles);
                } else {
                    handleFailure(metadataBufferResult.getStatus());
                }
            } else {
                handleConnectionResult(connectionResult);
            }
        } catch (Exception e) {
            handleError(e);
        }
    }

    protected List<DriveFileInfo> fetchFiles(DriveApi.MetadataBufferResult metadataBufferResult) {
        List<DriveFileInfo> files = new ArrayList<DriveFileInfo>();
        MetadataBuffer metadataBuffer = metadataBufferResult.getMetadataBuffer();
        if (metadataBuffer == null) return files;
        try {
            for (Metadata metadata : metadataBuffer) {
                if (metadata == null) continue;
                String title = metadata.getTitle();
                if (!title.endsWith(".backup")) continue;
                files.add(new DriveFileInfo(metadata.getDriveId(), title, metadata.getCreatedDate()));
            }
        } finally {
            metadataBuffer.close();
        }
        Collections.sort(files);
        return files;
    }

    protected String getDriveFolderName() {
        String folder = Preferences.getDriveBackupFolder(context);
        // check the backup folder registered on preferences
        if (folder == null || folder.equals("")) {
            return context.getString(R.string.app_name);
        }
        return folder;
    }

    protected DriveFolder getDriveFolder(String targetFolder) throws IOException {
        DriveFolder folder = getOrCreateDriveFolder(targetFolder);
        if (folder == null) {
            //throw new ImportExportException(context.getString(R.string.gdocs_folder_not_found));
        }
        return folder;
    }

    public DriveFolder getOrCreateDriveFolder(String targetFolder) throws IOException {
        Query query = new Query.Builder().addFilter(Filters.and(
                Filters.eq(SearchableField.TRASHED, false),
                Filters.eq(SearchableField.TITLE, targetFolder),
                Filters.eq(SearchableField.MIME_TYPE, "application/vnd.google-apps.folder")
        )).build();
        DriveApi.MetadataBufferResult result = Drive.DriveApi.query(googleApiClient, query).await();
        if (result.getStatus().isSuccess()) {
            DriveId driveId = fetchDriveId(result);
            if (driveId != null) {
                return driveId.asDriveFolder();
            }
        }
        return createDriveFolder(targetFolder);
    }

    private DriveFolder createDriveFolder(String targetFolder) {
        MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                .setTitle(targetFolder).build();
        DriveFolder.DriveFolderResult result = Drive.DriveApi.getRootFolder(googleApiClient).createFolder(googleApiClient, changeSet).await();
        if (result.getStatus().isSuccess()) {
            Preferences.storeDriveBackupFolder(context, targetFolder);
            return result.getDriveFolder();
        } else {
            return null;
        }
    }

    private DriveId fetchDriveId(DriveApi.MetadataBufferResult result) {
        MetadataBuffer buffer = result.getMetadataBuffer();
        try {
            for (Metadata metadata : buffer) {
                if (metadata == null) continue;
                return metadata.getDriveId();
            }
        } finally {
            buffer.close();
        }
        return null;
    }

    public Status createFile(DriveFolder folder, String fileName, byte[] bytes) throws IOException {
        MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                .setTitle(fileName)
                .setMimeType(ExportParams.ExportType.Backup.getMimeType()).build();
        // Create a file in the root folder
        DriveApi.DriveContentsResult contentsResult = Drive.DriveApi.newDriveContents(googleApiClient).await();
        Status contentsResultStatus = contentsResult.getStatus();
        if (contentsResultStatus.isSuccess()) {
            DriveContents contents = contentsResult.getDriveContents();
            contents.getOutputStream().write(bytes);
            DriveFolder.DriveFileResult fileResult = folder.createFile(googleApiClient, changeSet, contents).await();
            return fileResult.getStatus();
        } else {
            return contentsResultStatus;
        }
    }

    private void handleConnectionResult(ConnectionResult connectionResult) {
    }

    private void handleError(Exception e) {
    }

    private void handleFailure(Status status) {
    }

    private void handleSuccess(String fileName) {
    }

    private void handleSuccess(List<DriveFileInfo> files) {

    }
}
