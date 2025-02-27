class FolderManagementGrailsPlugin {
    // the plugin version
    def version = '19.0-SNAPSHOT'
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = '2.5.4 > *'
    // the other plugins this plugin depends on
    def dependsOn = [:]
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
            'grails-app/views/error.gsp'
    ]

    // TODO Fill in these fields
    def author = 'Florian Guitton'
    def authorEmail = 'f.guitton@imperial.ac.uk'
    def title = 'Folder Management and Annotation for tranSMART'
    def description = '''\\
Adds folder management features to tranSMART, allowing files to be attached to studies and analyses. Also contains annotation domain and controller
'''

    // URL to the plugin's documentation
    def documentation = ''

    def doWithWebDescriptor = { xml ->
        // TODO Implement additions to web.xml (optional), this event occurs before
    }

    def doWithSpring = {
        // TODO Implement runtime spring config (optional)
    }

    def doWithDynamicMethods = { ctx ->
        // TODO Implement registering dynamic methods to classes (optional)
    }

    def doWithApplicationContext = { applicationContext ->
        // TODO Implement post initialization spring config (optional)
    }

    def onChange = { event ->
        // TODO Implement code that is executed when any artefact that this plugin is
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.
    }

    def onConfigChange = { event ->
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }
}
