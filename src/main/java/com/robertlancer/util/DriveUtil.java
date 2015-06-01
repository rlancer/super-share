package com.robertlancer.util;

import com.google.api.services.drive.model.File;
import com.google.appengine.api.urlfetch.*;

import java.io.IOException;
import java.net.URL;

public class DriveUtil {

    public static byte[] exportGoogleDocAs(String email, String id, String mimeType) {
        com.google.api.services.drive.Drive drive = ServiceFactory.getDriveService(email);
        try {
            return exportGoogleDocAs(email, drive.files().get(id).execute(), mimeType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] exportGoogleDocAs(String email, File file, String mimeType) {
        com.google.api.services.drive.Drive drive = ServiceFactory.getDriveService(email);
        String accessToken = ServiceFactory.getAccessToken(drive);
        String downloadURL = file.getExportLinks().get(mimeType);
        return downloadFile(downloadURL, accessToken);
    }

    public static byte[] downloadFile(String email, File file) {
        com.google.api.services.drive.Drive drive = ServiceFactory.getDriveService(email);
        String accessToken = ServiceFactory.getAccessToken(drive);
        String downloadURL = file.getDownloadUrl();
        return downloadFile(downloadURL, accessToken);
    }

    public static byte[] downloadFile(String downloadURL, String accessToken) {
        try {
            HTTPRequest request = new HTTPRequest(new URL(downloadURL), HTTPMethod.GET, FetchOptions.Builder.withDeadline(Double.MAX_VALUE));
            request.addHeader(new HTTPHeader("Authorization", "Bearer " + accessToken));
            HTTPResponse response = URLFetchServiceFactory.getURLFetchService().fetch(request);
            return response.getContent();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return null;
        }
    }
}