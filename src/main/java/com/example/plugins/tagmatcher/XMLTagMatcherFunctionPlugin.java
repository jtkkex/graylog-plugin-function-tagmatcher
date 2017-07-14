package com.example.plugins.tagmatcher;

import org.graylog2.plugin.Plugin;
import org.graylog2.plugin.PluginMetaData;
import org.graylog2.plugin.PluginModule;

import java.util.Collection;
import java.util.Collections;

/**
 * Implement the Plugin interface here.
 */
public class XMLTagMatcherFunctionPlugin implements Plugin {
    @Override
    public PluginMetaData metadata() {
        return new XMLTagMatcherFunctionMetaData();
    }

    @Override
    public Collection<PluginModule> modules () {
        return Collections.<PluginModule>singletonList(new XMLTagMatcherFunctionModule());
    }
}
