# XMLTagMatcherFunction Plugin for Graylog

[![Build Status](https://travis-ci.org/jtkkex/graylog-plugin-function-tagmatcher.svg?branch=master)](https://travis-ci.org/jtkkex/graylog-plugin-function-tagmatcher)

This plugin implements a pipeline processing function tagmatcher(string tag, string source) : String.

The function can be used in pipelines to extract a simple field from and XML message.

**Required Graylog version:** 2.0 and later

Installation
------------

[Download the plugin](https://github.com/jtkkex/graylog-plugin-function-tagmatcher/releases)
and place the `.jar` file in your Graylog plugin directory. The plugin directory
is the `plugins/` folder relative from your `graylog-server` directory by default
and can be configured in your `graylog.conf` file.

Restart `graylog-server` and you are done.

Development
-----------

You can improve your development experience for the web interface part of your plugin
dramatically by making use of hot reloading. To do this, do the following:

* `git clone https://github.com/Graylog2/graylog2-server.git`
* `cd graylog2-server/graylog2-web-interface`
* `ln -s $YOURPLUGIN plugin/`
* `npm install && npm start`

Usage
-----

Notes:
* the tag may not contain characters < or \>
* the function returns only the first match within the string
* the match may contain other tags within

An example pipeline function:

```
rule "test rule"
when
    true
then
    let matchsource = to_string($message.message);
    let matchresult = tagmatcher("test",matchsource);
    set_field("matchresult",matchresult);
end
```

This pipeline function would make the following extractions:

| example message | matchresult |
|-----------------|-------------|
|<test\>ddd</test\>ggg|ddd|
|ff<test\>ddd</test\>ggg|ddd|
|fff<test\>ddd</test\>|ddd|
|<test\>ddd<test\>ddd</test\>ggg</test\>ggg|ddd<test\>ddd|
|<test\>ddd</tet\>ggg| |
|<test\>ddd<testi\>ddd</testi\>ggg</test\>ggg|ddd<testi\>ddd</testi\>ggg|

Getting started
---------------

This project is using Maven 3 and requires Java 8 or higher.

* Clone this repository.
* Run `mvn package` to build a JAR file.
* Optional: Run `mvn jdeb:jdeb` and `mvn rpm:rpm` to create a DEB and RPM package respectively.
* Copy generated JAR file in target directory to your Graylog plugin directory.
* Restart the Graylog.

Plugin Release
--------------

We are using the maven release plugin:

```
$ mvn release:prepare
[...]
$ mvn release:perform
```

This sets the version numbers, creates a tag and pushes to GitHub. Travis CI will build the release artifacts and upload to GitHub automatically.
