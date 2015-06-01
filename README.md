Super Share
===========

Super Share allows you to combine Google Drive and App Engine to create custom shares for your domain.

##Prerequisites

+   [Perform Google Apps Domain-Wide Delegation of Authority](https://developers.google.com/drive/web/delegation)
+   Create a project in the [Google Cloud Console](http://console.developers.google.com)

##Get the code

    mkdir super-share
    cd super-share
    git clone https://github.com/rlancer/super-share.git

## Add in an appengine-web.xml

```xml
<appengine-web-app xmlns="http://appengine.google.com/ns/1.0">
    <application>cloud-console-project-id</application>
    <version>1</version>
    <threadsafe>true</threadsafe>
    <sessions-enabled>false</sessions-enabled>
    <precompilation-enabled>true</precompilation-enabled>
    <system-properties>
        <property name="file.encoding" value="UTF-8"/>
        <property name="groovy.source.encoding" value="UTF-8"/>
        <property name="email" value="rob@see.pub"/>
        <property name="folder" value="0B1oGh-0oKVSZfktmYmh4Y1dOc0lDOFo3VjVwUVFuc1A4dEcwajdzLS1Va2NXZGlpNFZaR1U"/>
    </system-properties>
</appengine-web-app>
```

## Add your private key

