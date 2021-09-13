package com.example.linebot.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.example.linebot.googledrive.DriveFolderId;

public class DatabaseConnection {

    private static final String url = "jdbc:mysql://us-cdbr-east-03.cleardb.com:3306/heroku_c2d822aef8f917a?useUnicode=true&characterEncoding=utf8";
    private Connection con = null;
    private Statement sm = null;

    public DatabaseConnection() {
        try {
            this.con = DriverManager.getConnection(url, "b2a87e1b082bd1", "7614cdbb");
            this.sm = con.createStatement();
        } catch (SQLException e) {
            if (con != null) {
                closeDB();
            }
            e.printStackTrace();
        }
    }

    public void closeDB() {
        try {
            con.close();
            sm.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected Connection getConnection() {
        return this.con;
    }

    protected Statement getStatement() {
        return this.sm;
    }

    public boolean changeMode(final Frag frag, final String userId, final String lineId) {
        ResultSet rs = null;
        try {
            rs = sm.executeQuery(
                    "SELECT * FROM mode WHERE userId='" + userId + "' AND line_id='" + lineId + "' LIMIT 1");
            if (!rs.next()) {
                sm.executeUpdate("INSERT INTO mode (userId,frag,line_id) VALUES ('" + userId + "'," + frag.getValue()
                        + ",'" + lineId + "')");
                return true;
            }
            sm.executeUpdate("UPDATE mode SET frag=" + frag.getValue() + ",line_id = '" + lineId + "' WHERE userId='"
                    + userId + "' AND line_id='" + lineId + "'");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;

    }

    public int checkMode(final String userId, final String lineId) {
        ResultSet rs = null;
        int frag = IFragCollection.DISABLE;
        try {
            rs = sm.executeQuery(
                    "SELECT * FROM mode WHERE userId='" + userId + "' AND line_id='" + lineId + "' LIMIT 1");
            if (rs.next()) {
                frag = rs.getInt("frag");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return frag;
    }

    public void changeInterval(final String siteName, final boolean frag) {
        ResultSet rs = null;
        int fragValue;
        if (frag) {
            fragValue = 1;
        } else {
            fragValue = 0;
        }
        try {
            rs = sm.executeQuery("SELECT * FROM parse_interval WHERE site_name='" + siteName + "' LIMIT 1");
            if (rs.next()) {
                sm.executeUpdate(
                        "UPDATE parse_interval SET frag =" + fragValue + " WHERE site_name='" + siteName + "'");
                return;
            }
            sm.executeUpdate(
                    "INSERT INTO parse_interval (site_name,frag) VALUE ('" + siteName + "'," + fragValue + ")");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isInterVal(final String siteName) {
        ResultSet rs = null;
        try {
            rs = sm.executeQuery("SELECT * FROM parse_interval WHERE site_name='" + siteName + "' LIMIT 1");
            if (rs.next()) {
                if (rs.getInt("frag") == 0) {
                    return false;
                }
                if (rs.getInt("frag") == 1) {
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public GoogleKeyset getGoogleKeyset(final String lineId) {

        GoogleKeyset keyset = new GoogleKeyset();
        ResultSet rs = null;
        try {
            rs = sm.executeQuery("SELECT * FROM google_search_keyset WHERE line_id='" + lineId + "' LIMIT 1");
            if (rs.next()) {
                keyset.setLineId(rs.getString("line_id")).setApiKey(rs.getString("api_key"))
                        .setEngineKey(rs.getString("engine_key")).setRoomType(rs.getInt("roomtype"));
                return keyset;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isBackupRoom(final String lineId) {

        ResultSet rs = null;
        try {
            rs = sm.executeQuery("SELECT * FROM backup_select WHERE line_id='" + lineId + "' LIMIT 1");
            if (rs.next()) {
                if (rs.getInt("backup_type") == 1) {
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public void changeBackup(final boolean isBackup, final String lineId) {

        ResultSet rs = null;
        int i = 0;
        if (isBackup) {
            i = 1;
        }
        try {
            rs = sm.executeQuery("SELECT * FROM backup_select WHERE line_id='" + lineId + "' LIMIT 1");
            if (rs.next()) {
                sm.executeUpdate("UPDATE backup_select SET backup_type=" + i + " WHERE line_id='" + lineId + "'");
                return;
            }
            sm.executeUpdate("INSERT INTO backup_select (line_id,backup_type) VALUES ('" + lineId + "', " + i + ")");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setTokenDir(final String lineId, final String identifer) {

        String tokenDir = null;
        ResultSet rs = null;
        switch (identifer) {
            case "default": {
                tokenDir = "tokens/default";
                break;
            }
            case "private": {
                tokenDir = "tokens/private";
                break;
            }
            case "family": {
                tokenDir = "tokens/private/family";
                break;
            }
            default: {
                tokenDir = "tokens/default";
                break;
            }
        }
        try {
            rs = sm.executeQuery("SELECT * FROM token WHERE line_id = '" + lineId + "' LIMIT 1");
            if (rs.next()) {
                sm.executeUpdate("UPDATE token SET token_dir = '" + tokenDir + "' WHERE line_id ='" + lineId + "'");
                return;
            }
            sm.executeUpdate("INSERT INTO token (line_id,token_dir) VALUES ('" + lineId + "','" + tokenDir + "')");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getTokenDir(final String lineId) {

        ResultSet rs = null;
        try {
            rs = sm.executeQuery("SELECT * FROM token WHERE line_id = '" + lineId + "' LIMIT 1");
            if (rs.next()) {
                return rs.getString("token_dir");
            }
            sm.executeUpdate("INSERT INTO token (line_id,token_dir) VALUES ('" + lineId + "','tokens/default')");
            return "tokens/default";
        } catch (SQLException e) {
            e.printStackTrace();
            return "tokens/default";
        }
    }

    public void setRootFolderId(final String tokenDir, final String rootFolderId) {

        ResultSet rs = null;
        try {
            rs = sm.executeQuery("SELECT * FROM root_folder_id WHERE token_dir = '" + tokenDir + "' LIMIT 1");
            if (rs.next()) {
                sm.executeUpdate("UPDATE root_folder_id SET folder_id = '" + rootFolderId + "' WHERE token_dir = '"
                        + tokenDir + "'");
                return;
            }
            sm.executeUpdate("INSERT INTO root_folder_id (token_dir,folder_id) VALUES ('" + tokenDir + "','"
                    + rootFolderId + "')");
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public String getRootFolderId(final String tokenDir) {

        ResultSet rs = null;
        try {
            rs = sm.executeQuery("SELECT * FROM root_folder_id WHERE token_dir = '" + tokenDir + "' LIMIT 1");
            if (rs.next()) {
                return rs.getString("folder_id");
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public DriveFolderId getAllFolderIds(final String lineId) {

        final DriveFolderId folderId = new DriveFolderId();
        ResultSet rs = null;
        try {
            rs = sm.executeQuery("SELECT * FROM folder_ids WHERE line_id = '" + lineId + "' LIMIT 1");
            if (rs.next()) {
                folderId.setRootFolderId(getRootFolderId(getTokenDir(lineId)));
                folderId.setParentFolderId(rs.getString("parent_id"));
                folderId.setImageFolderId(rs.getString("image_id"));
                folderId.setVideoFolderId(rs.getString("video_id"));
                folderId.setAudioFolderId(rs.getString("audio_id"));
                folderId.setFileFolderId(rs.getString("file_id"));
                return folderId;
            }
            System.out.println("フォルダIDが登録されていません");
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return folderId;

    }

    public void setAllFolderIds(final String lineId, DriveFolderId folderIds) {

        setRootFolderId(getTokenDir(lineId), folderIds.getRootFolderId());
        ResultSet rs = null;
        try {
            rs = sm.executeQuery("SELECT * FROM folder_ids WHERE line_id = '" + lineId + "' LIMIT 1");
            if (rs.next()) {
                sm.executeUpdate("UPDATE folder_ids SET parent_id = '" + folderIds.getParentFolderId()
                        + "',image_id = '" + folderIds.getImageFolderId() + "',video_id = '"
                        + folderIds.getVideoFolderId() + "',audio_id = '" + folderIds.getAudioFolderId()
                        + "',file_id = '" + folderIds.getFileFolderId() + "' WHERE line_id = '" + lineId + "'");
                return;
            }
            sm.executeUpdate("INSERT INTO folder_ids (line_id,parent_id,image_id,video_id,audio_id,file_id) VALUES ('"
                    + lineId + "','" + folderIds.getParentFolderId() + "','" + folderIds.getImageFolderId() + "','"
                    + folderIds.getVideoFolderId() + "','" + folderIds.getAudioFolderId() + "','"
                    + folderIds.getFileFolderId() + "')");
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void setFolderId(final String lineId, final DriveFolderId folderId, final String identifer) {

        try {
            switch (identifer) {
                case "parent": {
                    sm.executeUpdate("UPDATE folder_ids SET parent_id = '" + folderId.getParentFolderId()
                            + "'WHERE line_id = '" + lineId + "'");
                    return;
                }
                case "image": {
                    sm.executeUpdate("UPDATE folder_ids SET image_id = '" + folderId.getImageFolderId()
                            + "'WHERE line_id = '" + lineId + "'");
                    return;
                }
                case "video": {
                    sm.executeUpdate("UPDATE folder_ids SET video_id = '" + folderId.getVideoFolderId()
                            + "'WHERE line_id = '" + lineId + "'");
                    return;
                }
                case "audio": {
                    sm.executeUpdate("UPDATE folder_ids SET audio_id = '" + folderId.getAudioFolderId()
                            + "'WHERE line_id = '" + lineId + "'");
                    return;
                }
                case "file": {
                    sm.executeUpdate("UPDATE folder_ids SET file_id = '" + folderId.getFileFolderId()
                            + "'WHERE line_id = '" + lineId + "'");
                    return;
                }
                default: {
                    return;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> getImageUrl(final String userId, final String lineId) {

        ResultSet rs = null;
        try {
            rs = sm.executeQuery(
                    "SELECT * FROM image_url WHERE user_id = '" + userId + "' AND line_id = '" + lineId + "'");
            final ArrayList<String> urlList = new ArrayList<>();
            while (rs.next()) {
                urlList.add(rs.getString("url"));
            }
            return urlList;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public void saveImageUrl(final ArrayList<String> urlList, final String userId, final String lineId) {

        for (String url : urlList) {
            try {
                sm.executeUpdate("INSERT INTO image_url (user_id,line_id,url) VALUES ('" + userId + "','" + lineId
                        + "','" + url.replace("\\", "\\\\") + "')");
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public void deleteImageUrl(final String userId, final String lineId) {
        ResultSet rs = null;
        try {
            rs = sm.executeQuery(
                    "SELECT * FROM image_url WHERE user_id = '" + userId + "' AND line_id = '" + lineId + "'");
            if (rs.next()) {
                sm.executeUpdate(
                        "DELETE FROM image_url WHERE user_id = '" + userId + "' AND line_id = '" + lineId + "'");
            }
            return;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public String getImageSearchWord(final String userId, final String lineId) {

        ResultSet rs = null;
        try {
            rs = sm.executeQuery(
                    "SELECT * FROM image_count WHERE user_id = '" + userId + "' AND line_id = '" + lineId + "'");
            if (rs.next()) {
                return rs.getString("word");
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public int getImageCount(final String userId, final String lineId) {

        ResultSet rs = null;
        try {
            rs = sm.executeQuery(
                    "SELECT * FROM image_count WHERE user_id = '" + userId + "' AND line_id = '" + lineId + "'");
            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return 0;
    }

    public void saveImageCount(final String userId, final String lineId, final String word) {

        ResultSet rs = null;
        try {
            rs = sm.executeQuery(
                    "SELECT * FROM image_count WHERE user_id = '" + userId + "' AND line_id = '" + lineId + "'");
            if (rs.next()) {
                final int count = getImageCount(userId, lineId) + 1;
                sm.executeUpdate("UPDATE image_count SET count = " + count + " WHERE user_id = '" + userId
                        + "' AND line_id = '" + lineId + "'");
                return;
            }
            sm.executeUpdate("INSERT INTO image_count (user_id,line_id,count,word) VALUES ('" + userId + "','" + lineId
                    + "',1,'" + word + "')");

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void saveImageCount(final String userId, final String lineId) {

        ResultSet rs = null;
        try {
            rs = sm.executeQuery(
                    "SELECT * FROM image_count WHERE user_id = '" + userId + "' AND line_id = '" + lineId + "'");
            if (rs.next()) {
                final int count = getImageCount(userId, lineId) + 1;
                sm.executeUpdate("UPDATE image_count SET count = " + count + " WHERE user_id = '" + userId
                        + "' AND line_id = '" + lineId + "'");
                return;
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void deleteImageCount(final String userId, final String lineId) {

        ResultSet rs = null;
        try {
            rs = sm.executeQuery(
                    "SELECT * FROM image_count WHERE user_id = '" + userId + "' AND line_id = '" + lineId + "'");
            if (rs.next()) {
                sm.executeUpdate(
                        "DELETE FROM image_count WHERE user_id = '" + userId + "' AND line_id = '" + lineId + "'");
                return;
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public boolean checkRegistration(final String lineId) {
        ResultSet rs = null;
        try {
            rs = sm.executeQuery("SELECT * FROM authorize_list WHERE line_id = '" + lineId + "' LIMIT 1");
            if (rs.next()) {
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }

    public String editRegistration(final String lineId, final boolean delete) {
        ResultSet rs = null;
        try {
            rs = sm.executeQuery("SELECT * FROM authorize_list WHERE line_id = '" + lineId + "' LIMIT 1");
            if (rs.next()) {
                if (!delete) {
                    return "既に登録済みです";
                }
                if (delete) {
                    sm.executeUpdate("DELETE FROM authorize_list WHERE line_id = '" + lineId + "'");
                    return "許可を取り消しました";
                }
            }
            if (!delete) {
                sm.executeUpdate("INSERT INTO authorize_list (line_id) VALUES ('" + lineId + "')");
                return "許可登録しました";
            }
            if (delete) {
                return "データベースに登録されていないため取り消し操作をキャンセルしました";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "例外が発生しました";
        }

        return "エラー";

    }

}
