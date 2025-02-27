/*************************************************************************
 * tranSMART - translational medicine data mart
 * 
 * Copyright 2008-2012 Janssen Research & Development, LLC.
 * 
 * This product includes software developed at Janssen Research & Development, LLC.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License 
 * as published by the Free Software  * Foundation, either version 3 of the License, or (at your option) any later version, along with the following terms:
 * 1.	You may convey a work based on this program in accordance with section 5, provided that you retain the above notices.
 * 2.	You may convey verbatim copies of this program code as you receive it, in any medium, provided that you retain the above notices.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS    * FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 *
 ******************************************************************/
  
package com.recomdata.transmart.data.export.util;

import com.recomdata.transmart.data.export.exception.FTPAuthenticationException;
import com.recomdata.transmart.data.export.exception.InvalidFTPParamsException;
import grails.util.Holders;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * @author SMunikuntla
 */
public class FTPUtil {
	
    private static Logger logger = LoggerFactory.getLogger(FTPUtil.class);

    @SuppressWarnings("rawtypes")
    private static final Map config = Holders.getFlatConfig();
	
    private static final String FTP_SERVER = (String) config.get("com.recomdata.transmart.data.export.ftp.server");
    private static final String FTP_SERVER_PORT = (String) config.get("com.recomdata.transmart.data.export.ftp.serverport");
    private static final String FTP_SERVER_USER_NAME = (String) config.get("com.recomdata.transmart.data.export.ftp.username");
    private static final String FTP_SERVER_PASSWORD = (String) config.get("com.recomdata.transmart.data.export.ftp.password");
    private static final String FTP_SERVER_REMOTE_PATH = (String) config.get("com.recomdata.transmart.data.export.ftp.remote.path");
    private static FTPClient ftp;

    /**
     * Validates if all the required params to connect to FTP server are not empty.
     */
    private static void validate() throws InvalidFTPParamsException {
        if (StringUtils.isEmpty(FTP_SERVER)
            || StringUtils.isEmpty(FTP_SERVER_USER_NAME)
            || StringUtils.isEmpty(FTP_SERVER_PASSWORD)) {
            throw new InvalidFTPParamsException("Invalid FTP Connection Params");
        }
    }

    /**
     * Connects to the specified FTP Server:Port details in the configuration file.
     */
    private static void connect() throws InvalidFTPParamsException {
        validate();
        ftp = new FTPClient();
        try {
            int reply;
            if (NumberUtils.isNumber(FTP_SERVER_PORT)
                && Integer.parseInt(FTP_SERVER_PORT) > 0) {
                ftp.connect(FTP_SERVER, Integer.parseInt(FTP_SERVER_PORT));
            }
            else {
                ftp.connect(FTP_SERVER);
            }
            logger.info("Connected to {} on {}", FTP_SERVER, ftp.getRemotePort());

            // After connection attempt, you should check the reply code to verify success
            reply = ftp.getReplyCode();

            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                logger.error("FTP server refused connection.");
            }
        }
        catch (IOException e) {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                }
                catch (IOException f) {
                    logger.error(e.getMessage(), e);
                }
            }
            logger.error("Could not connect to FTP server.", e);
        }
    }

    /**
     * Performs the login operation for the provided user credentials
     */
    private static void login() throws FTPAuthenticationException {
        try {
            if (StringUtils.isNotEmpty(FTP_SERVER_USER_NAME) && StringUtils.isNotEmpty(FTP_SERVER_PASSWORD)) {
                if (!ftp.login(FTP_SERVER_USER_NAME, FTP_SERVER_PASSWORD)) {
                    ftp.logout();
                    throw new FTPAuthenticationException(
                        "Credentials failed to Authenticate on the FTP server");
                }
                logger.info("Remote system is {}", ftp.getSystemType());
            }
        }
        catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * Uploads a given file to the connected FTP Server
     * 
     * @return remote FTP location of the file
     */
    public static String uploadFile(boolean binaryTransfer, File localFile) {
        String remote = null;
        boolean uploadComplete = false;
        try {
            connect();
            login();

            if (binaryTransfer) {
                ftp.setFileType(FTP.BINARY_FILE_TYPE);
            }
            // Use passive mode as default because most of us are behind firewalls these days.
            ftp.enterLocalPassiveMode();
            ftp.setUseEPSVwithIPv4(false);

            InputStream input = new FileInputStream(localFile);

            remote = FTP_SERVER_REMOTE_PATH + localFile.getName();
            uploadComplete = ftp.storeFile(remote, input);

            input.close();
        }
        catch (InvalidFTPParamsException e) {
            logger.error("Invalid FTP Params to connect");
        }
        catch (FTPAuthenticationException e) {
            logger.error(e.getMessage());
        }
        catch (FileNotFoundException e) {
            logger.error("Not able to load/read the localFile");
        }
        catch (IOException e) {
            logger.error("IOException during FTP upload process");
        }
        finally {
            if (!uploadComplete) {
                remote = null;
            }
        }

        return remote;
    }
	
    public static InputStream downloadFile(boolean binaryTransfer, String filename) {
        try {
            String remote = FTP_SERVER_REMOTE_PATH + filename;
            connect();
            login();

            if (binaryTransfer) {
                ftp.setFileType(FTP.BINARY_FILE_TYPE);
            }
            // Use passive mode as default because most of us are behind firewalls these days
            ftp.enterLocalPassiveMode();
            ftp.setUseEPSVwithIPv4(false);

            return ftp.retrieveFileStream(remote);
        }
        catch (InvalidFTPParamsException e) {
            logger.error("Invalid FTP Params to connect");
	}
        catch (FTPAuthenticationException e) {
            logger.error(e.getMessage());
        }
        catch (FileNotFoundException e) {
            logger.error("Not able to load/read the localFile");
        }
        catch (IOException e) {
            logger.error("IOException during FTP upload process");
	}
	
        return null;
    }
	
    public static boolean deleteFile(String filename) {
        String remote = null;
        try{
            remote = FTP_SERVER_REMOTE_PATH + filename;
            connect();
            login();

            return ftp.deleteFile(remote);
        }
        catch (InvalidFTPParamsException e) {
            logger.error("Invalid FTP Params to connect");
        }
        catch (FTPAuthenticationException e) {
            logger.error(e.getMessage());
        }
        catch (IOException e) {
            logger.error("IOException during FTP delete process for " + remote);
        }

        return false;
    }
}
