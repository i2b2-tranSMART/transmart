/**
 * @author JIsikoff
 *
 */
import com.recomdata.datasetexplorer.proxy.XmlHttpProxy
import com.recomdata.datasetexplorer.proxy.XmlHttpProxyServlet
import groovy.util.logging.Slf4j

import javax.servlet.ServletContext
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Slf4j('logger')
class ProxyController {

    static defaultAction = 'proxy'

    def proxy = {
        def post = false
        if (request.getMethod() == 'POST')
            post = true
        doProcess(request, response, post)
    }


    private doProcess(HttpServletRequest req, HttpServletResponse res, boolean isPost) {

        boolean allowXDomain = true
        boolean requireSession = false
        String responseContentType = 'text/xml;charset=UTF-8';  //changed from text/json in jmaki source
        boolean rDebug = false
        XmlHttpProxy xhp = new XmlHttpProxy(); 
        ServletContext ctx
        println(this)

        StringBuilder bodyContent = new StringBuilder(); 
        OutputStream out = null
        PrintWriter writer = null
        String serviceKey = null

        /*try {
            BufferedReader inp = req.getReader()
            String line = null
            while ((line = inp.readLine()) != null) {
                if (bodyContent == null) bodyContent = new StringBuffer()
                bodyContent.append(line)
                //logger.trace(line)
            }
        }
        catch (Exception e) {
            println(e)
            logger.error(e.toString())
        }*/

        BufferedReader bufferedReader = null
        try {
            InputStream inputStream = req.getInputStream()
            if (inputStream != null) {
                bufferedReader = new BufferedReader(new InputStreamReader(
                        inputStream))
                char[] charBuffer = new char[128]
                int bytesRead = -1
                while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                    bodyContent.append(charBuffer, 0, bytesRead)
                }
            }
            else {
                bodyContent.append('')
            }
        }
        catch (IOException ex) {
            logger.error(ex)
            // throw ex
        }
        finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close()
                }
                catch (IOException ex) {
                    logger.error(ex)
                    //throw ex
                }
            }
        }

        //	println(bodyContent.toString())
        try {
            String urlString = null
            String xslURLString = null
            String userName = null
            String password = null
            String format = 'json'
            String callback = req.getParameter('callback')
            String urlParams = req.getParameter('urlparams')
            String countString = req.getParameter('count')
            // encode the url to prevent spaces from being passed along
            if (urlParams != null) {
                urlParams = urlParams.replace(' ', '+')
            }

            try {
                if (false) {
                    logger.trace('wrong')
                }
                //code for passing the url directly through instead of using configuration file
                else if (req.getParameter('url') != null) {
                    String serviceURL = req.getParameter('url')
                    // build the URL
                    if (urlParams != null && serviceURL.indexOf('?') == -1) {
                        serviceURL += '?'
                    }
                    else if (urlParams != null) {
                        serviceURL += '&'
                    }
                    urlString = serviceURL
                    if (urlParams != null) urlString += urlParams
                }
                else {
                    writer = res.getWriter()
                    if (serviceKey == null) writer.write('XmlHttpProxyServlet Error: id parameter specifying serivce required.')
                    else writer.write("XmlHttpProxyServlet Error : service for id '" + serviceKey + "' not  found.")
                    writer.flush()
                    return
                }
            }
            catch (Exception ex) {
                logger.error('XmlHttpProxyServlet Error loading service: ' + ex)
            }

            Map paramsMap = new HashMap()
            paramsMap.put('format', format)
            // do not allow for xdomain unless the context level setting is enabled.
            if (callback != null && allowXDomain) {
                paramsMap.put('callback', callback)
            }
            if (countString != null) {
                paramsMap.put('count', countString)
            }

            InputStream xslInputStream = null

            if (urlString == null) {
                writer = res.getWriter()
                writer.write('XmlHttpProxyServlet parameters:  id[Required] urlparams[Optional] format[Optional] callback[Optional]')
                writer.flush()
                return
            }
            // default to JSON
            res.setContentType(responseContentType)
            out = res.getOutputStream()
            // get the stream for the xsl stylesheet
            if (xslURLString != null) {
                // check the web root for the resource
                URL xslURL = null
                xslURL = ctx.getResource(resourcesDir + 'xsl/' + xslURLString)
                // if not in the web root check the classpath
                if (xslURL == null) {
                    xslURL = XmlHttpProxyServlet.class.getResource(classpathResourcesDir + 'xsl/' + xslURLString)
                }
                if (xslURL != null) {
                    xslInputStream = xslURL.openStream()
                }
                else {
                    String message = 'Could not locate the XSL stylesheet provided for service id ' + serviceKey + '. Please check the XMLHttpProxy configuration.'
                    logger.debug(message)
                    try {
                        out.write(message.getBytes())
                        out.flush()
                        return
                    }
                    catch (java.io.IOException iox) {
                    }
                }
            }
            //	println('url:'+urlString)
            //	println('body:'+bodyContent)
            if (!isPost) {
                logger.trace('proxying to:' + urlString)
                xhp.doGet(urlString, out, xslInputStream, paramsMap, userName, password)
            }
            else {
                if (bodyContent == null || bodyContent.length() == 0) logger.debug('XmlHttpProxyServlet attempting to post to url ' + urlString + ' with no body content')
                logger.trace('proxying to:' + urlString)
                xhp.doPost(urlString, out, xslInputStream, paramsMap, bodyContent.toString(), req.getContentType(), userName, password)
            }
        }
        catch (Exception iox) {
            iox.printStackTrace()
            logger.trace('XmlHttpProxyServlet: caught ' + iox)
            try {
                writer = res.getWriter()
                writer.write('XmlHttpProxyServlet error loading service for ' + serviceKey + ' . Please notify the administrator.')
                writer.flush()
            }
            catch (java.io.IOException ix) {
                ix.printStackTrace()
            }
            return
        }
        finally {
            try {
                //if (out != null) out.close()
                //if (writer != null) writer.close()
            }
            catch (java.io.IOException iox) {
            }
        }
    }
}
