// configuration for plugin testing - will not be included in the plugin zip
log4j = {

    error  'org.codehaus.groovy.grails.web.servlet',  //  controllers
           'org.codehaus.groovy.grails.web.pages', //  GSP
           'org.codehaus.groovy.grails.web.sitemesh', //  layouts
           'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
           'org.codehaus.groovy.grails.web.mapping', // URL mapping
           'org.codehaus.groovy.grails.commons', // core / classloading
           'org.codehaus.groovy.grails.plugins', // plugins
           'org.codehaus.groovy.grails.orm.hibernate', // hibernate integration
           'org.springframework',
           'org.hibernate',
           'net.sf.ehcache.hibernate'
    info   'org.transmartproject'
    debug  'heim'

    // uncomment to debug queries
    //trace 'org.hibernate.type'
    //debug 'org.hibernate.SQL'
}

grails.views.default.codec='none' // none, html, base64
grails.views.gsp.encoding='UTF-8'

grails.databinding.convertEmptyStringsToNull = false
grails.databinding.trimStrings = false
