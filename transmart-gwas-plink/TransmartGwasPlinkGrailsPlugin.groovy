import grails.util.Holders
import org.springframework.stereotype.Component

class TransmartGwasPlinkGrailsPlugin {
    // the plugin version
    def version = '19.0-SNAPSHOT'
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = '2.5.4 > *'
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
            'grails-app/views/error.gsp'
    ]

    def title = 'Transmart Gwas Plink Plugin' // Headline display name of the plugin
    def author = 'Alexander Bondarev'
    def authorEmail = 'alexander.bondarev@thomsonreuters.com'
    def description = '''\
GWAS Plink integration plug-in
'''
    def dependsOn = [:]

    // URL to the plugin's documentation
    def documentation = 'http://grails.org/plugin/transmart-gwas-plink'

    // Extra (optional) plugin metadata

    // License: one of 'APACHE', 'GPL2', 'GPL3'
//    def license = 'APACHE'

    // Details of company behind the plugin (if there is one)
//    def organization = [ name: 'My Company', url: 'http://www.my-company.com/' ]

    // Any additional developers beyond the author specified above.
//    def developers = [ [ name: 'Joe Bloggs', email: 'joe@bloggs.net' ]]

    // Location of the plugin's issue tracker.
//    def issueManagement = [ system: 'JIRA', url: 'http://jira.grails.org/browse/GPMYPLUGIN' ]

    // Online location of the plugin's browseable source code.
//    def scm = [ url: 'http://svn.codehaus.org/grails-plugins/' ]

    def doWithWebDescriptor = { xml ->
        // TODO Implement additions to web.xml (optional), this event occurs before
    }

    def doWithSpring = {
        xmlns context: 'http://www.springframework.org/schema/context'

        context.'component-scan'('base-package': 'com.thomsonreuters.lsps.transmart.jobs') {
            context.'include-filter'(
                    type: 'annotation',
                    expression: Component.canonicalName)
        }
    }

    def doWithDynamicMethods = { ctx ->
        // TODO Implement registering dynamic methods to classes (optional)
    }

    def doWithApplicationContext = { ctx ->
        def config = Holders.config
        if (config.grails.plugin.transmartGwasPlink.enabled) {
            def extensionsRegistry = ctx.getBean('transmartExtensionsRegistry')
            extensionsRegistry.registerAnalysisTabExtension('transmartGwasPlink', '/gwasPlink/loadScripts', 'addGwasPlinkAnalysis')
        }
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

    def onShutdown = { event ->
        // TODO Implement code that is executed when the application shuts down (optional)
    }
}
