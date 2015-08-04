Super Share
=========== 

[Super Share Presentation](http://share.robertlancer.com/Super-Share)

Super Share is a more elegant way to share documents from Google Drive for Google Apps users. 

It deploys to Google App Engine and is designed to be efficient enough to run within App Engine's free daily quota. 

**This system automatically changes certain file permissions to include Anyone with the link can view**  

+ share.example.com/file-title URL pattern 
+ Receive alerts for when files are viewed, enabled per a file 
+ Supports partial matches, ie: "Road" will match "RoadTrip.mp4" 
+ No Google Drive branding 
+ Runs on Google App Engine
+ Will easily run within the free daily App Engine quota
+ Open Source with the MIT License. Feel free to fork it and customize it

Demos
+ [Picture](http://share.robertlancer.com/KoreanBBQ.jpg)
+ [Spreadsheet](http://share.robertlancer.com/Spreadsheet)
+ [Video](http://share.robertlancer.com/Roadtrip.mp4)
+ [PDF](http://share.robertlancer.com/Comic-Book.pdf)
+ [Google Doc](http://share.robertlancer.com/Press)
+ [Google Slides](http://share.robertlancer.com/Super-Share)

##Prerequisites

+   [Perform Google Apps Domain-Wide Delegation of Authority](https://developers.google.com/drive/web/delegation)
+   At a minimum you will need to have the *https://www.googleapis.com/auth/drive* and *https://www.googleapis.com/auth/gmail.compose* scopes enabled  
+   Create a project in the [Google Cloud Console](http://console.developers.google.com)

##Get the code

    git clone https://github.com/rlancer/super-share.git
    cd super-share

##Arange your Content

+ Create a folder in Drive
+ Add in the files you would like to share underneath that folder
+ Files will automatically have their permission updated to *Anyone with the link* can view

## Add in an appengine-web.xml

Create the appengine-web.xml underneath the *webapp/WEB-INF* directory. Adjust the properties to match your environment's settings. 

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
        <property name="serviceAccountEmailAddress" value="service account email address"/>
        <property name="allowPartialMatches" value="true"/>
    </system-properties>
</appengine-web-app>
```

## Add your Private Key

The private key comes from performing domain wide delegation of authority

Copy the .p12 file to *src/main/webapp/WEB-INF/privatekey* directory

## Enable View Alert Emails for a File

Simply edit the description field of the file in Drive to include the text *#SSALERT* and 
the owner will receive an email from themselves when the file is viewed.
Emails include the IP address and User Agent of the viewer 


## Run the App Locally

    gradle appengineRun

A local server should be spawned. Visit http://localhost:SERVER_PORT/file-name in your browser. If you see your file displayed then everything works!

## Deploy to App Engine

    gradle appengineUpdate

## Set up your Sub-domain

Generally we will want our users to visit *shares.our-domain.com/file-name*

To set this up we will need to go to our Google Apps admin panel and wire up the Super Share app to our domain

Login to [admin.google.com](https://admin.google.com)

Go to the App Engine section - If you cant see it try clicking on More

Next add your App Engine Id and map it to point to your sub-domain

Follow the setup instructions to wire up your sub-domain in your DNS settings

## Roadmap 

+ Support for static HTML websites 
+ Support for downloading files 

