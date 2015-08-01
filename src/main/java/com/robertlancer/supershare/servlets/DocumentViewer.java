package com.robertlancer.supershare.servlets;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.Permission;
import com.google.api.services.gmail.Gmail;
import com.robertlancer.supershare.util.DriveBackoff;
import com.robertlancer.supershare.util.Mail;
import com.robertlancer.supershare.util.Mime;
import com.robertlancer.supershare.util.ServiceFactory;

import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;

public class DocumentViewer extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    String pathInfo = req.getPathInfo();

    if (pathInfo == null || pathInfo.length() <= 1) {
      sendError(404, resp);
      return;
    }

    String fileTitle = pathInfo.substring(1).replace("-", " ").replace("'", "");

    if (fileTitle.equalsIgnoreCase("favicon.ico"))
      return;

    String folderId = System.getProperty("folder");
    String email = System.getProperty("email");

    File fileToOutput = getFile(fileTitle, folderId, email);

    if (fileToOutput == null) {
      sendError(404, resp);
      return;
    } else {
      boolean anyoneHasAccess = false;
      for (Permission permission : fileToOutput.getPermissions()) {
        if (permission.getType().equalsIgnoreCase("anyone")) {
          anyoneHasAccess = true;
          break;
        }
      }

      if (!anyoneHasAccess) {
        Permission permission = new Permission();
        permission.setType("anyone");
        permission.setRole("reader");
        permission.setWithLink(true);

        Drive.Permissions.Insert updatePermissionReq = ServiceFactory.getDriveService(email).permissions().insert(fileToOutput.getId(), permission);
        Permission inserted = new DriveBackoff<Permission>().execute(updatePermissionReq, false);
      }

      resp.getWriter().write(outputFile(fileToOutput));
      sendViewAlert(fileToOutput, req);
    }
  }

  public static void sendViewAlert(File fileToOutput, HttpServletRequest req) {

    boolean sendAlert = fileToOutput.getDescription().contains("#SSALERT");

    if (!sendAlert)
      return;

    String email = fileToOutput.getOwners().get(0).getEmailAddress();
    Gmail gmail = ServiceFactory.getGmailService(email);

    String ipAddress = req.getRemoteAddr();

    String body = fileToOutput.getTitle() + " was viewed.\n\n" +
      "IP: " + ipAddress + "\n" +
      "User Agent: " + req.getHeader("User-Agent") + "\n" + new Date().toLocaleString();

    try {

      Mail.sendMessage(gmail, "me", Mail.createEmail(email, email, fileToOutput.getTitle(), body));
    } catch (MessagingException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static String outputFile(File fileToOutput) {

    String url = outputFileAsIFrameGetURL(fileToOutput);

    String iframe = "<iframe src='" + url + "' frameborder=0 style='width:100%;height:100%;' /></iframe>";

    StringBuilder output = new StringBuilder();
    output.append("<html>");
    output.append("<head><title>" + fileToOutput.getTitle() + "</title>");
    output.append("<link rel=\"icon\" type=\"image/png\"\n" +
      " href=\"" + fileToOutput.getIconLink() + "\" />");
    output.append("</head>");
    output.append("<body>");
    output.append("<style>\n");
    output.append("html, body { overflow:hidden; height:100%; padding:0px; margin:0px; }");
    output.append("\n</style>");
    output.append(iframe);
    output.append("</body></html>");

    return output.toString();
  }

  public static String outputFileAsIFrameGetURL(File fileToOutput) {

    String domain = fileToOutput.getOwners().get(0).getEmailAddress().split("@")[1];
    String id = fileToOutput.getId();

    switch (fileToOutput.getMimeType()) {
      case Mime.DOCUMENT:
        return "https://docs.google.com/a/" + domain + "/document/d/" + id + "/preview";
      case Mime.SPREADSHEET:
        return "https://docs.google.com/a/" + domain + "/spreadsheet/ccc?key=" + id + "&output=html&widget=true&chrome=false";
      case Mime.PRESENTATION:
        return "https://docs.google.com/a/" + domain + "/presentation/d/" + id + "/preview";
      case Mime.DRAWING:
        return "https://docs.google.com/a/" + domain + "/drawings/d/" + id + "/preview";
      default:
        return "https://docs.google.com/a/" + domain + "/file/d/" + id + "/preview";
    }
  }

  public static File getFile(String title, String folderId, String email) throws IOException {

    Drive drive = ServiceFactory.getDriveService(email);
    Drive.Files.List request = drive.files().list().setQ("'" + folderId + "' in parents and trashed = false and title contains '" + title + "'").setFields("items(description,owners,id,downloadUrl,iconLink,mimeType,permissions,title)").setMaxResults(1000);
    List<File> items = new DriveBackoff<FileList>().execute(request, false).getItems();

    if (items.isEmpty())
      return null;

    if (items.size() == 1)
      return items.get(0);
    else {
      for (File file : items) {
        if (file.getTitle().equalsIgnoreCase(title))
          return file;
      }
    }

    return items.get(0);
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
