Super Share
===========

Super Share is a more elegant way to share document from Google Drive

+ Shares live on your domain with the URL pattern of share.example.com/file-title
+ No Google Drive Chrome or branding
+ Runs on Google App Engine
+ Will easily run within the free daily App Engine quota
+ Open Source with the MIT License feel free to fork it and customize it

Demos
+ [Spreadsheet](http://share.robertlancer.com/Spreadsheet)
+ [Video](http://share.robertlancer.com/Roadtrip.mp4)
+ [PDF](http://share.robertlancer.com/Comic-Book.pdf)
+ [Google Doc](http://share.robertlancer.com/Press)
+ [Google Slides](http://share.robertlancer.com/Super-Share)

##Prerequisites

+   [Perform Google Apps Domain-Wide Delegation of Authority](https://developers.google.com/drive/web/delegation)
+   Create a project in the [Google Cloud Console](http://console.developers.google.com)

##Get the code

    git clone https://github.com/rlancer/super-share.git
    cd super-share

##Arange your content

+ Create a folder in Drive
+ Add in the files you would like to share underneath that folder. 
+ The files should have the permission of *Anyone with the link* can view
+ **If the files do not have that permission level it will automatically be modified to match that permission level** 

## Add in an appengine-web.xml

Create the appengine-web.xml underneath the *webapp/WEB-INF* directory

```xml
<appengine-web-app xmlns="http://appengine.google.com/ns/1.0">
    <application>cloud-console-project-id</application>
    <version>1</version>
    <threadsafe>true</threadsafe>
    <sessions-enabled>false</sessions-enabled>
    <precompilation-enabled>true</precompilation-enabled>
    <system-properties>
        <property name="file.encoding" value="UTF-8"/>
        <property name="email" value="email_account_which_owns_the_folder@yourdomain.com"/>
        <property name="folder" value="id_of_folder_which_will_store_the_documents"/>
    </system-properties>
</appengine-web-app>
```

## Add your private key

The private key comes from performing domain wide delegation of authority.

Copy the .p12 file to *webapp/WEB-INF/privatekeys* directory


## Run the app locally

    gradle appengineRun

A local server should be spawned, visit http://localhost:8080/file-name in your browser, if you see your file displayed then everything works!

## Deploy to App Engine

    gradle appengineUpdate

## Set up your sub-domain

Generally we will want our users to visit *shares.our-domain.com/file-name*

To set this up we will need to go to our Google Apps admin panel and wire up the Super Share app to our domain.

Login to [admin.google.com](https://admin.google.com)

Go to the App Engine section, if you cant see it try clicking on More

Next add your App Engine Id and map it to point to your sub-domain

Follow the setup instructions to wire up your sub-domain in your DNS settings
