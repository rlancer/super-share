package com.robertlancer.servlets;

import com.google.api.client.http.HttpResponse;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.robertlancer.util.DriveUtil;
import com.robertlancer.util.Resources;
import com.robertlancer.util.ServiceFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class DocumentViewer extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.length() <= 1) {
            sendError(404, resp);
            return;
        }

        String fileTitle = pathInfo.substring(1).replace("-", " ");

        String folderId = System.getProperty("folder");
        String email = System.getProperty("email");

        Drive drive = ServiceFactory.getDriveService(email);
        List<File> items = getFiles(folderId, email);

        File fileToOutput = null;

        for (File file : items) {

            if (file.getTitle().equalsIgnoreCase(fileTitle))
                fileToOutput = file;
        }

        if (fileToOutput == null) {
            sendError(404, resp);
            return;
        } else {

            byte[] bytes = null;

            String mimeType = fileToOutput.getMimeType();
            if (fileToOutput.getExportLinks() != null) {
                bytes = DriveUtil.exportGoogleDocAs(email, fileToOutput, "application/pdf");
                resp.setContentType("application/pdf");
            } else {
                bytes = DriveUtil.downloadFile(email, fileToOutput);
                resp.setContentType(mimeType);
            }

            resp.getOutputStream().write(bytes);
        }

        // mailViewReport(req);
    }

    public static List<File> getFiles(String folderId, String email) {
        try {
            Drive drive = ServiceFactory.getDriveService(email);
            List<File> items = drive.files().list().setQ("'" + folderId + "' in parents").execute().getItems();
            return items;
        } catch (Exception ex) {
            return null;
        }
    }

    public void sendError(int code, HttpServletResponse resp) {
        try {
            resp.setStatus(code);
            if (code == 404)
                resp.getWriter().write("Sorry could not find the document you were looking for.");
            else
                resp.getWriter().write("An internal error occurred please try again soon.");

        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
