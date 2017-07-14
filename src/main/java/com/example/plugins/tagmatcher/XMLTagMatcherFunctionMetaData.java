package com.example.plugins.tagmatcher;

import org.graylog2.plugin.PluginMetaData;
import org.graylog2.plugin.ServerStatus;
import org.graylog2.plugin.Version;

import java.net.URI;
import java.util.Collections;
import java.util.Set;

/**
 * Implement the PluginMetaData interface here.
 */
public class XMLTagMatcherFunctionMetaData implements PluginMetaData {
    private static final String PLUGIN_PROPERTIES = "com.example.plugins.graylog-plugin-function-tagmatcher/graylog-plugin.properties";

    @Override
    public String getUniqueId() {
        return "com.example.plugins.tagmatcher.XMLTagMatcherFunctionPlugin";
    }

    @Override
    public String getName() {
        return "XMLTagMatcherFunction";
    }

    @Override
    public String getAuthor() {
        return "Jouni Karvo <kex@iki.fi>";
    }

    @Override
    public URI getURL() {
        return URI.create("https://github.com/jtkkex/graylog-plugin-function-tagmatcher");
    }

    @Override
    public Version getVersion() {
        return Version.fromPluginProperties(getClass(), PLUGIN_PROPERTIES, "version", Version.from(0, 0, 1));
    }

    @Override
    public String getDescription() {
        // TODO Insert correct plugin description
        return "Pipeline function to match XML tags like <key>mykey</key>";
    }

    @Override
    public Version getRequiredVersion() {
        return Version.fromPluginProperties(getClass(), PLUGIN_PROPERTIES, "graylog.version", Version.from(2, 2, 3));
    }

    @Override
    public Set<ServerStatus.Capability> getRequiredCapabilities() {
        return Collections.emptySet();
    }
}
