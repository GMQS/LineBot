package com.example.linebot.googledrive;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.security.GeneralSecurityException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import com.example.linebot.database.DatabaseConnection;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.About;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;

public class AutoBackup {

    private static final String APPLICATION_NAME = "Drive Auto backup";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";
    private Drive service;
    private String lineId;
    private DatabaseConnection dc;

    public AutoBackup(final String lineId, final DatabaseConnection dc) throws SQLException {
        this.lineId = lineId;
        this.dc = dc;
        // Build a new authorized API client service.
        try {
            NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            this.service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                    .setApplicationName(APPLICATION_NAME).build();
        } catch (GeneralSecurityException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public DriveFolderId checkFolderExists(final String folderName) throws SQLException{
        DriveFolderId folderId = dc.getAllFolderIds(lineId);
        final String rootFolderId = folderId.getRootFolderId();
        if (rootFolderId == null) {
            System.out.println("データベースからルートフォルダIDを取得できませんでした ->　全フォルダ作成");
            return createAllFolder(folderName);
        }
        try {
            service.files().get(rootFolderId).execute();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("ルートフォルダIDでファイルオブジェクトを取得できませんでした ->　全フォルダ作成");
            return createAllFolder(folderName);
        }

        final String parentFolderId = folderId.getParentFolderId();
        File parentFolder = null;
        try {
            parentFolder = service.files().get(parentFolderId).execute();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("パレントフォルダIDでファイルオブジェクトを取得できませんでした ->　パレントフォルダ作成");
            return createParentFolder(folderId, folderName);
        }

        try {
            service.files().get(folderId.getImageFolderId()).execute();
        } catch (IOException e) {
            System.out.println("画像フォルダIDでファイルオブジェクトを取得できませんでした ->　画像フォルダ作成");
            folderId.setImageFolderId(createImageFolder(parentFolderId));
            dc.setFolderId(lineId, folderId, "image");
        }

        try {
            service.files().get(folderId.getVideoFolderId()).execute();
        } catch (IOException e) {
            System.out.println("動画フォルダIDでファイルオブジェクトを取得できませんでした ->　動画フォルダ作成");
            folderId.setVideoFolderId(createVideoFolder(parentFolderId));
            dc.setFolderId(lineId, folderId, "video");
        }

        try {
            service.files().get(folderId.getAudioFolderId()).execute();
        } catch (IOException e) {
            System.out.println("オーディオフォルダIDでファイルオブジェクトを取得できませんでした ->　オーディオフォルダ作成");
            folderId.setAudioFolderId(createAudioFolder(parentFolderId));
            dc.setFolderId(lineId, folderId, "audio");
        }

        try {
            service.files().get(folderId.getFileFolderId()).execute();
        } catch (IOException e) {
            System.out.println("ファイルフォルダIDでファイルオブジェクトを取得できませんでした ->　ファイルフォルダ作成");
            folderId.setFileFolderId(createFileFolder(parentFolderId));
            dc.setFolderId(lineId, folderId, "file");
        }

        if (!parentFolder.getName().equals(folderName)) {
            // グループ名を取得してフォルダ名をグループ名にリネームする
            final File parentFolderMetadata = new File();
            parentFolderMetadata.setName(folderName);
            try {
                service.files().update(parentFolderId, parentFolderMetadata).execute();
                System.out.println("フォルダ名をリネームしました ->" + folderName);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return folderId;
    }

    public String getSharedUrl(final String folderName) throws SQLException {
        final DriveFolderId folderId = checkFolderExists(folderName);
        return "https://drive.google.com/drive/folders/" + folderId.getParentFolderId() + "?usp=sharing";
    }

    public void uploadImageFile(final String path, final String folderName, final String timeStamp) {
        try {
            if (!dc.isBackupRoom(lineId)) {
                return;
            }
            final DriveFolderId folderId = checkFolderExists(folderName);
            upload(folderId.getImageFolderId(), path, ".jpg", "image/jpeg", timeStamp);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void uploadVideoFile(final String path, final String folderName, final String timeStamp) {
        try {
            if (!dc.isBackupRoom(lineId)) {
                return;
            }
            final DriveFolderId folderId = checkFolderExists(folderName);
            upload(folderId.getVideoFolderId(), path, ".mp4", "video/mpeg4", timeStamp);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void uploadAudioFile(final String path, final String folderName, final String timeStamp) {
        try {
            if (!dc.isBackupRoom(lineId)) {
                return;
            }
            final DriveFolderId folderId = checkFolderExists(folderName);
            upload(folderId.getAudioFolderId(), path, ".mp3", "audio/mpeg3", timeStamp);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void uploadFile(final String path, final String fileName, final String folderName, final String timeStamp) {
        try {
            if (!dc.isBackupRoom(lineId)) {
                return;
            }
            final DriveFolderId folderId = checkFolderExists(folderName);
            upload(folderId.getFileFolderId(), path, "_" + fileName, "file/any", timeStamp);

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public String checkStorage() {
        try {
            About about = service.about().get().setFields("*").execute();
            Long usage = about.getStorageQuota().getUsage();
            Long limit = about.getStorageQuota().getLimit();
            return "ストレージ" + "\n" + convertSize(limit) + "/" + convertSize(usage) + "使用中";

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "ストレージ" + "\n" + "容量を取得できませんでした";
        }
    }

    private String convertSize(Long size) {
        if (1024L > size) {
            return size + " B";
        }
        if (1024L * 1024L > size) {
            double d = size;
            d = d / 1024;
            BigDecimal bd = new BigDecimal(String.valueOf(d));
            double value = bd.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
            return value + " KB";
        }
        if (1024L * 1024L * 1024L > size) {
            double d = size;
            d = d / 1024 / 1024;
            BigDecimal bd = new BigDecimal(String.valueOf(d));
            double value = bd.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
            return value + " MB";
        }
        if (1024L * 1024L * 1024L * 1024L > size) {
            double d = size;
            d = d / 1024 / 1024 / 1024;
            BigDecimal bd = new BigDecimal(String.valueOf(d));
            double value = bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            return value + " GB";
        }
        return size + " B";

    }

    private void upload(final String folderId, final String path, final String fileName, final String contentType,
            final String timeStamp) throws SQLException {
        File fileMetadata = new File();
        fileMetadata.setName("LINE " + timeStamp + fileName);
        fileMetadata.setParents(Collections.singletonList(folderId));
        java.io.File filePath = new java.io.File(path);
        FileContent mediaContent = new FileContent(contentType, filePath);
        try {
            service.files().create(fileMetadata, mediaContent).setFields("id, parents").execute();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private DriveFolderId createAllFolder(final String folderName) throws SQLException {
        DriveFolderId folderId = new DriveFolderId();
        String[] titles = { "画像", "動画", "オーディオ", "ファイル" };
        try {
            File rootFolderMetadata = new File();
            rootFolderMetadata.setName("LINE Backup");
            rootFolderMetadata.setMimeType("application/vnd.google-apps.folder");
            File rootFolder = service.files().create(rootFolderMetadata).setFields("id").execute();
            folderId.setRootFolderId(rootFolder.getId());

            File parentFolderMetadata = new File();
            parentFolderMetadata.setName(folderName);
            parentFolderMetadata.setMimeType("application/vnd.google-apps.folder");
            parentFolderMetadata.setParents(Collections.singletonList(rootFolder.getId()));
            File parentFolder = service.files().create(parentFolderMetadata).setFields("id, parents").execute();
            folderId.setParentFolderId(parentFolder.getId());

            // 読み取り専用権限を付与
            Permission parentFolderPermission = new Permission().setType("anyone").setRole("reader");
            service.permissions().create(parentFolder.getId(), parentFolderPermission).setFields("id").execute();

            for (int i = 0; titles.length > i; i++) {
                File folderMetadata = new File();
                folderMetadata.setName(titles[i]);
                folderMetadata.setMimeType("application/vnd.google-apps.folder");
                folderMetadata.setParents(Collections.singletonList(parentFolder.getId()));
                File folder = service.files().create(folderMetadata).setFields("id, parents").execute();
                switch (i) {
                case 0: {
                    folderId.setImageFolderId(folder.getId());
                    break;
                }
                case 1: {
                    folderId.setVideoFolderId(folder.getId());
                    break;
                }
                case 2: {
                    folderId.setAudioFolderId(folder.getId());
                    break;
                }
                case 3: {
                    folderId.setFileFolderId(folder.getId());
                    break;
                }
                }
            }
            dc.setAllFolderIds(lineId, folderId);
            return folderId;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private DriveFolderId createParentFolder(final DriveFolderId folderId, final String folderName) throws SQLException {
        try {
            final File parentFolderMetadata = new File();
            parentFolderMetadata.setName(folderName);
            parentFolderMetadata.setMimeType("application/vnd.google-apps.folder");
            parentFolderMetadata.setParents(Collections.singletonList(folderId.getRootFolderId()));
            final File parentFolder = service.files().create(parentFolderMetadata).setFields("id, parents").execute();

            // 読み取り専用権限を付与
            Permission parentFolderPermission = new Permission().setType("anyone").setRole("reader");
            service.permissions().create(parentFolder.getId(), parentFolderPermission).setFields("id").execute();

            final String parentFolderId = parentFolder.getId();
            folderId.setParentFolderId(parentFolderId);
            folderId.setImageFolderId(createImageFolder(parentFolderId));
            folderId.setVideoFolderId(createVideoFolder(parentFolderId));
            folderId.setAudioFolderId(createAudioFolder(parentFolderId));
            folderId.setFileFolderId(createFileFolder(parentFolderId));

            dc.setAllFolderIds(lineId, folderId);
            return folderId;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    private String createImageFolder(final String parentFolderId) {
        try {
            File imageFolderMetadata = new File();
            imageFolderMetadata.setName("画像");
            imageFolderMetadata.setMimeType("application/vnd.google-apps.folder");
            imageFolderMetadata.setParents(Collections.singletonList(parentFolderId));
            File imageFolder = service.files().create(imageFolderMetadata).setFields("id, parents").execute();
            return imageFolder.getId();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    private String createVideoFolder(final String parentFolderId) {
        try {
            File videoFolderMetadata = new File();
            videoFolderMetadata.setName("動画");
            videoFolderMetadata.setMimeType("application/vnd.google-apps.folder");
            videoFolderMetadata.setParents(Collections.singletonList(parentFolderId));
            File videoFolder = service.files().create(videoFolderMetadata).setFields("id, parents").execute();
            return videoFolder.getId();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    private String createAudioFolder(final String parentFolderId) {
        try {
            File audioFolderMetadata = new File();
            audioFolderMetadata.setName("オーディオ");
            audioFolderMetadata.setMimeType("application/vnd.google-apps.folder");
            audioFolderMetadata.setParents(Collections.singletonList(parentFolderId));
            File audioFolder = service.files().create(audioFolderMetadata).setFields("id, parents").execute();
            return audioFolder.getId();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    private String createFileFolder(final String parentFolderId) {
        try {
            File fileFolderMetadata = new File();
            fileFolderMetadata.setName("ファイル");
            fileFolderMetadata.setMimeType("application/vnd.google-apps.folder");
            fileFolderMetadata.setParents(Collections.singletonList(parentFolderId));
            File fileFolder = service.files().create(fileFolderMetadata).setFields("id, parents").execute();
            return fileFolder.getId();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Creates an authorized Credential object.
     * 
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     * @throws SQLException
     */
    private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException, SQLException {
        // Load client secrets.
        InputStream in = AutoBackup.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
                clientSecrets, SCOPES)
                        .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(dc.getTokenDir(lineId))))
                        .setAccessType("offline").build();

        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

}
