# My Browser4 Plugin

A Browser4 plugin that provides custom functionality


- JDK 17 or later
- Maven 3.9 or later (or use the included Maven wrapper)


```bash
./mvnw package
```

This produces a thin JAR at `target/jpa3-0.0.1-SNAPSHOT.jar`.



```bash
curl -X POST http://localhost:8080/api/plugins/install \
  -F "file=@target/jpa3-0.0.1-SNAPSHOT.jar"
```


Copy the JAR into the `plugins/` directory of your Browser4 installation:

```bash
cp target/jpa3-0.0.1-SNAPSHOT.jar /path/to/browser4/plugins/
```

After installation, restart the Browser4 application to activate the plugin.


```bash
# List installed plugins
curl http://localhost:8080/api/plugins

# Check logs for plugin activation
# Look for: "PluginManager: Found X PluginMount bean(s)"
```


```
src/main/kotlin/${pluginPackage}/
├── MyPlugin.kt                  # Plugin lifecycle (Browser4Plugin)
├── config/
│   └── PluginAutoConfiguration.kt  # Spring auto-config + mount points
├── integration/
│   ├── MyBrowseEventHandler.kt  # Browse-phase event handlers (17 hooks)
│   └── MyLoadEventHandler.kt    # Load-phase event handlers (9 hooks)
└── tools/
    └── MyToolExecutor.kt        # Custom LLM agent tools (optional)

src/main/resources/META-INF/
└── browser4-plugin.json                                 # Plugin manifest + discovery (required)
```



- **LoadEventMount** (9 hooks): URL normalization, fetch, parse
- **BrowseEventMount** (17 hooks): navigation, scroll, interaction, RPA
- **CrawlEventMount** (2 hooks): URL filtering, result handling
- **ToolMount**: register custom LLM agent tools


`onDocumentSteady` fires when the page is fully loaded and stable — ideal for
custom RPA actions like clicking buttons, filling forms, or extracting data.


All Browser4 and Spring Boot dependencies use `provided` scope because the
host Browser4 application provides them at runtime.


- [Browser4 Plugin Development Guide](https://browser4.io/docs/plugin-development)
- [Browser4 GitHub](https://github.com/platonai/browser4)
