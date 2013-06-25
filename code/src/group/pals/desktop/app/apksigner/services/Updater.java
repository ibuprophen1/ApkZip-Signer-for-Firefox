/*
 *    Copyright (C) 2012 Hai Bison
 *
 *    See the file LICENSE at the root directory of this project for copying
 *    permission.
 */

package group.pals.desktop.app.apksigner.services;

import group.pals.desktop.app.apksigner.i18n.Messages;
import group.pals.desktop.app.apksigner.services.INotification.Message;
import group.pals.desktop.app.apksigner.utils.Network;
import group.pals.desktop.app.apksigner.utils.SpeedTracker;
import group.pals.desktop.app.apksigner.utils.Sys;
import group.pals.desktop.app.apksigner.utils.Texts;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Application updater service.
 * 
 * @author Hai Bison
 * @since v1.6 beta
 */
public class Updater extends Thread {

    private static final String CLASSNAME = Updater.class.getName();

    /**
     * The URL pointing to `update.properties` file.
     */
    public static final String URL_UPDATE_PROPERTIES = "https://apk-signer.googlecode.com/hg/bin/update.properties";

    /**
     * The app version code.
     */
    public static final String KEY_APP_VERSION_CODE = "app_version_code";

    /**
     * The app version name.
     */
    public static final String KEY_APP_VERSION_NAME = "app_version_name";

    /**
     * The download URI.
     */
    public static final String KEY_DOWNLOAD_URI = "download_uri";

    /**
     * The SHA-1 of new file.
     */
    public static final String KEY_SHA1 = "SHA-1";

    /**
     * Maximum filesize allowed for `update.properties`.
     */
    public static final int MAX_UPDATE_PROPERTIES_FILESIZE = 9 * 1024;

    /**
     * Maximum filesize allowed for the new version.
     */
    public static final int MAX_UPDATE_FILESIZE = Sys.DEBUG ? Integer.MAX_VALUE
            : 9 * 1024 * 1024;

    /**
     * File handling buffer (reading, writing...).
     */
    private static final int FILE_BUFFER = 99 * 1024;

    /**
     * The service has done its jobs.
     */
    public static final int MSG_DONE = 0;

    /**
     * There is a local update file available.
     */
    public static final int MSG_LOCAL_UPDATE_AVAILABLE = 1;

    /**
     * The update filesize exceeds limit.
     */
    public static final int MSG_UPDATE_FILESIZE_EXCEEDS_LIMIT = 2;

    /**
     * The update progress (percentage done...)
     */
    public static final int MSG_UPDATE_PROGRESS = 3;

    /**
     * The update cancelled.
     */
    public static final int MSG_UPDATE_CANCELLED = 4;

    private INotification mNotification;

    /**
     * Sets the notification.
     * 
     * @param notification
     *            the notification to set.
     * @return the instance of this service, to allow chaining multiple calls
     *         into a single statement.
     */
    public Updater setNotification(INotification notification) {
        mNotification = notification;
        return this;
    }// setNotification()

    /**
     * Gets the notification.
     * 
     * @return the notification.
     */
    public INotification getNotification() {
        return mNotification;
    }// getNotification()

    /**
     * Sends notification to listener.
     * 
     * @param msgId
     *            the message ID.
     * @param shortMsg
     *            the short message.
     * @param detailedMsg
     *            the detailed message.
     * @return the result of {@link INotification#onMessage(Message)}, or
     *         {@code false} if there is no listener.
     */
    private boolean sendNotification(int msgId, String shortMsg,
            String detailedMsg) {
        if (getNotification() == null)
            return false;

        Message msg = new Message();
        msg.id = msgId;
        msg.shortMessage = shortMsg;
        msg.detailedMessage = detailedMsg;

        return getNotification().onMessage(msg);
    }// sendNotification()

    @Override
    public void run() {
        try {
            /*
             * DOWNLOAD UPDATE.PROPERTIES AND PARSE INFO TO MEMORY
             */
            final Properties updateProperties = downloadUpdateProperties();
            if (updateProperties == null || isInterrupted())
                return;

            try {
                if (Integer.parseInt(Messages.getString(KEY_APP_VERSION_CODE)) >= Integer
                        .parseInt(updateProperties
                                .getProperty(KEY_APP_VERSION_CODE))
                        && !Sys.DEBUG)
                    return;
            } catch (Throwable t) {
                /*
                 * Can be number format exception or NPE...
                 */
                return;
            }

            if (Sys.DEBUG)
                System.out.printf("%s >> %s\n", CLASSNAME, updateProperties);

            /*
             * CHECK TO SEE IF THE UPDATE FILE HAS BEEN DOWNLOADED BEFORE
             */
            if (isInterrupted() || checklocalUpdateFile(updateProperties))
                return;

            /*
             * DOWNLOAD THE UPDATE FILE
             */
            downloadUpdateFile(updateProperties);
        } finally {
            sendNotification(MSG_DONE, null, null);
        }
    }// run()

    /**
     * Downloads the `update.properties` file from server.
     * 
     * @return the {@link Properties} object containing update information. Or
     *         {@code null} if an error occurred.
     */
    private Properties downloadUpdateProperties() {
        HttpURLConnection conn = Network
                .openHttpConnection(URL_UPDATE_PROPERTIES);
        if (conn == null)
            return null;

        try {
            conn.connect();
            InputStream inputStream = conn.getInputStream();
            if (conn.getContentLength() > MAX_UPDATE_PROPERTIES_FILESIZE) {
                inputStream.close();
                return null;
            }

            Properties result = new Properties();
            result.load(inputStream);
            inputStream.close();

            return result;
        } catch (IOException e) {
            /*
             * Ignore it.
             */
            e.printStackTrace();
            return null;
        } catch (NullPointerException e) {
            /*
             * Ignore it.
             */
            return null;
        }
    }// downloadUpdateProperties()

    /**
     * Checks to see if there is update file which has been downloaded before.
     * 
     * @param updateProperties
     *            the update information.
     * @return {@code true} or {@code false}.
     */
    private boolean checklocalUpdateFile(Properties updateProperties) {
        URL downloadUri;
        try {
            downloadUri = new URL(updateProperties.getProperty(
                    KEY_DOWNLOAD_URI, Texts.EMPTY));
        } catch (MalformedURLException e) {
            /*
             * Ignore it.
             */
            e.printStackTrace();
            return false;
        }

        File file = new File(new File(downloadUri.getFile()).getName());
        if (file.isFile()) {
            /*
             * Check SHA-1.
             */
            try {
                MessageDigest md = MessageDigest.getInstance(Texts.SHA1);

                final byte[] buf = new byte[FILE_BUFFER];
                int read;
                FileInputStream inputStream = new FileInputStream(file);
                try {
                    while ((read = inputStream.read(buf)) > 0) {
                        if (isInterrupted())
                            return false;
                        md.update(buf, 0, read);
                    }
                } finally {
                    inputStream.close();
                }

                BigInteger bi = new BigInteger(1, md.digest());
                final boolean result = updateProperties.getProperty(KEY_SHA1)
                        .equalsIgnoreCase(
                                String.format("%0" + (md.getDigestLength() * 2)
                                        + "x", bi));
                if (result)
                    sendNotification(MSG_LOCAL_UPDATE_AVAILABLE,
                            Messages.getString("msg_local_update_available"),
                            String.format(Messages
                                    .getString("pmsg_local_update_available"),
                                    file.getAbsolutePath(), updateProperties
                                            .getProperty(KEY_APP_VERSION_NAME)));
                return result;
            } catch (NoSuchAlgorithmException e) {
                /*
                 * Never catch this.
                 */
                e.printStackTrace();
                return false;
            } catch (FileNotFoundException e) {
                /*
                 * Never catch this.
                 */
                e.printStackTrace();
                return false;
            } catch (IOException e) {
                /*
                 * Ignore it.
                 */
                return false;
            } catch (NullPointerException e) {
                return false;
            }
        }// file.isFile()
        else
            return false;
    }// checklocalUpdateFile()

    /**
     * Downloads the update file.
     * 
     * @param updateProperties
     *            the update information.
     */
    private void downloadUpdateFile(Properties updateProperties) {
        HttpURLConnection conn = Network
                .openHttpConnection(Sys.DEBUG ? "http://dlc.sun.com.edgesuite.net/virtualbox/4.2.14/VirtualBox-4.2.14-86644-Linux_amd64.run"
                        : updateProperties.getProperty(KEY_DOWNLOAD_URI));
        if (conn == null)
            return;

        try {
            conn.connect();
            InputStream inputStream = conn.getInputStream();
            try {
                final int contentLength = conn.getContentLength();
                if (contentLength == 0)
                    return;
                if (contentLength > 0 && contentLength > MAX_UPDATE_FILESIZE) {
                    sendNotification(
                            MSG_UPDATE_FILESIZE_EXCEEDS_LIMIT,
                            Messages.getString("msg_cancelled_update"),
                            String.format(
                                    Messages.getString("pmsg_update_filesize_exceeds_limit"),
                                    Texts.sizeToStr(contentLength), Texts
                                            .sizeToStr(MAX_UPDATE_FILESIZE)));
                    return;
                }

                File targetFile = new File(
                        new File(conn.getURL().getFile()).getName());
                if (Sys.DEBUG)
                    System.out.printf("%s >> %s\n", CLASSNAME,
                            targetFile.getAbsolutePath());

                if (contentLength > 0
                        && targetFile.getParentFile() != null
                        && targetFile.getParentFile().getFreeSpace() <= contentLength * 1.5) {
                    sendNotification(MSG_UPDATE_FILESIZE_EXCEEDS_LIMIT,
                            Messages.getString("msg_cancelled_update"),
                            String.format(Messages
                                    .getString("pmsg_available_space_is_low"),
                                    Texts.sizeToStr(targetFile.getParentFile()
                                            .getFreeSpace())));
                    return;
                }

                OutputStream outputStream = new FileOutputStream(targetFile);
                final long[] totalRead = { 0 };
                final SpeedTracker speedTracker = new SpeedTracker();
                final Timer timer = new Timer();
                timer.schedule(new TimerTask() {

                    @Override
                    public void run() {
                        if (contentLength > 0) {
                            sendNotification(
                                    MSG_UPDATE_PROGRESS,
                                    String.format(
                                            Messages.getString("pmsg_updating_with_percentage"),
                                            Texts.percentToStr(totalRead[0]
                                                    * 100f / contentLength),
                                            Texts.sizeToStr(totalRead[0]),
                                            Texts.sizeToStr(speedTracker
                                                    .calcInstantaneousSpeed())),
                                    null);
                        }// contentLength > 0
                        else {
                            sendNotification(
                                    MSG_UPDATE_PROGRESS,
                                    String.format(Messages
                                            .getString("pmsg_updating"), Texts
                                            .sizeToStr(totalRead[0]), Texts
                                            .sizeToStr(speedTracker
                                                    .calcInstantaneousSpeed())),
                                    null);
                        }// //contentLength == 0
                    }// run()
                }, 999, 999);
                try {
                    final MessageDigest md = MessageDigest
                            .getInstance(Texts.SHA1);
                    byte[] buf = new byte[FILE_BUFFER];
                    int read;
                    long tick = System.nanoTime();
                    while ((read = inputStream.read(buf)) > 0) {
                        if (isInterrupted()) {
                            outputStream.close();
                            targetFile.delete();
                            return;
                        }

                        outputStream.write(buf, 0, read);
                        totalRead[0] += read;

                        md.update(buf, 0, read);

                        tick = System.nanoTime() - tick;
                        speedTracker.add(tick > 0 ? totalRead[0] / (tick / 1e6)
                                : totalRead[0] / 1e6);

                        long freeSpace = 0;
                        if (targetFile.getParentFile() != null) {
                            freeSpace = targetFile.getParentFile()
                                    .getFreeSpace();
                            if (freeSpace < totalRead[0] * 1.5) {
                                sendNotification(
                                        MSG_UPDATE_FILESIZE_EXCEEDS_LIMIT,
                                        Messages.getString("msg_cancelled_update"),
                                        String.format(
                                                Messages.getString("pmsg_available_space_is_low"),
                                                Texts.sizeToStr(freeSpace)));
                                outputStream.close();
                                targetFile.delete();
                                return;
                            }
                        }

                        tick = System.nanoTime();
                    }// while

                    outputStream.close();
                    timer.cancel();

                    /*
                     * CHECK SHA-1
                     */
                    BigInteger bi = new BigInteger(1, md.digest());
                    if (updateProperties.getProperty(KEY_SHA1)
                            .equalsIgnoreCase(
                                    String.format("%0"
                                            + (md.getDigestLength() * 2) + "x",
                                            bi))) {
                        sendNotification(
                                MSG_UPDATE_FILESIZE_EXCEEDS_LIMIT,
                                Messages.getString("msg_update_finished"),
                                String.format(
                                        Messages.getString("pmsg_update_finished"),
                                        targetFile.getAbsolutePath(),
                                        updateProperties
                                                .getProperty(KEY_APP_VERSION_NAME)));
                    } else {
                        targetFile.delete();
                        sendNotification(
                                MSG_UPDATE_CANCELLED,
                                Messages.getString("msg_update_cancelled"),
                                Messages.getString("msg_update_cancelled_because_wrong_checksum"));
                    }
                } catch (NoSuchAlgorithmException e) {
                    /*
                     * Never catch this.
                     */
                } finally {
                    outputStream.close();
                    timer.cancel();
                }
            } finally {
                if (inputStream != null)
                    inputStream.close();
            }
        } catch (Throwable t) {
            /*
             * Ignore it.
             */
            t.printStackTrace();
        }
    }// downloadUpdateFile()
}