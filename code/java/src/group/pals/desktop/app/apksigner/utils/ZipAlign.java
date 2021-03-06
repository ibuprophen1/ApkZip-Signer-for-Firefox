/*
 *    Copyright (C) 2012 Hai Bison
 *
 *    See the file LICENSE at the root directory of this project for copying
 *    permission.
 */

package group.pals.desktop.app.apksigner.utils;

import group.pals.desktop.app.apksigner.i18n.Messages;
import group.pals.desktop.app.apksigner.i18n.R;
import group.pals.desktop.app.apksigner.services.BaseThread;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * ZipAlign.
 * <p>
 * This file is ported from <a href=
 * "https://android.googlesource.com/platform/build/+/master/tools/zipalign/"
 * >AOSP's ZipAlign</a> tool.
 * </p>
 * <p>
 * <h1>Quote from original README</h1>
 * </p>
 * <p>
 * 
 * <pre>
 * The purpose of zipalign is to ensure that all uncompressed data starts
 * with a particular alignment relative to the start of the file.  This
 * allows those portions to be accessed directly with mmap() even if they
 * contain binary data with alignment restrictions.
 * 
 * Some data needs to be word-aligned for easy access, others might benefit
 * from being page-aligned.  The adjustment is made by altering the size of
 * the "extra" field in the zip Local File Header sections.  Existing data
 * in the "extra" fields may be altered by this process.
 * 
 * Compressed data isn't very useful until it's uncompressed, so there's no
 * need to adjust its alignment.
 * 
 * Alterations to the archive, such as renaming or deleting entries, will
 * potentially disrupt the alignment of the modified entry and all later
 * entries.  Files added to an "aligned" archive will not be aligned.
 * </pre>
 * 
 * </p>
 * <p>
 * <h1>Notes</h1>
 * </p>
 * <p>
 * <ul>
 * <li>The tool modifies the "extra" field of all entries which are not
 * compressed ({@link ZipEntry#STORED}).</li>
 * 
 * <li>Only the "extra" fields in local file headers are modified. The ones in
 * central directory are not touched.</li>
 * </ul>
 * </p>
 * <p>
 * See <a href="http://en.wikipedia.org/wiki/Zip_(file_format)">Zip (file
 * format) - Wikipedia</a> for further information..
 * </p>
 * 
 * @author Hai Bison
 * @since v1.6.9 beta
 */
public class ZipAlign {

    /**
     * The minimum size of a ZIP entry's header.
     */
    public static final int ZIP_ENTRY_HEADER_LEN = 30;

    /**
     * Default version to work with ZIP files.
     */
    public static final int ZIP_ENTRY_VERSION = 20;

    /**
     * The offset of extra field length in a ZIP entry's header.
     */
    public static final int ZIP_ENTRY_OFFSET_EXTRA_LEN = 28;

    /**
     * The size of field extra length, in a ZIP entry's header.
     */
    public static final int ZIP_ENTRY_FIELD_EXTRA_LEN_SIZE = 2;

    /**
     * @see <a
     *      href="https://android.googlesource.com/platform/build/+/master/tools/zipalign/ZipEntry.h">ZipEntry.h</a>
     */
    public static final int ZIP_ENTRY_USES_DATA_DESCR = 0x0008;

    /**
     * @see <a
     *      href="https://android.googlesource.com/platform/build/+/master/tools/zipalign/ZipEntry.h">ZipEntry.h</a>
     */
    public static final int ZIP_ENTRY_DATA_DESCRIPTOR_LEN = 16;

    /**
     * Default alignment value.
     * <p>
     * See <a
     * href="http://developer.android.com/tools/help/zipalign.html">zipalign
     * </a>.
     * </p>
     */
    public static final int DEFAULT_ALIGNMENT = 4;

    /**
     * Used to append to newly aligned APK's file name.
     */
    public static final String ALIGNED = "ALIGNED";

    /**
     * Private helper class.
     * 
     * @author Hai Bison
     * @since v1.6.9 beta
     */
    private static class XEntry {

        public final ZipEntry entry;
        public final long headerOffset;
        public final int flags;
        public final int padding;

        /**
         * Creates new instance.
         * 
         * @param entry
         *            the entry.
         * @param headerOffset
         *            the offset of the header.
         * @param flags
         *            the flags.
         * @param padding
         *            the padding of the "extra" field.
         */
        public XEntry(ZipEntry entry, long headerOffset, int flags, int padding) {
            this.entry = entry;
            this.headerOffset = headerOffset;
            this.flags = flags;
            this.padding = padding;
        }// XEntry()
    }// XEntry

    /**
     * Extended class of {@link FilterOutputStream}, which has some helper
     * methods for writing data to ZIP stream.
     * 
     * @author Hai Bison
     * @since v1.6.9 beta
     */
    private static class FilterOutputStreamEx extends FilterOutputStream {

        private long totalWritten = 0;

        /**
         * Creates new instance.
         * 
         * @param out
         *            {@link OutputStream}.
         */
        public FilterOutputStreamEx(OutputStream out) {
            super(out);
        }// FilterOutputStreamEx()

        @Override
        public void write(byte[] b) throws IOException {
            out.write(b);
            totalWritten += b.length;
        }// write()

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            out.write(b, off, len);
            totalWritten += len;
        }// write()

        @Override
        public void write(int b) throws IOException {
            out.write(b);
            totalWritten += 1;
        }// write()

        @Override
        public void close() throws IOException {
            // l("\t\tclose() >> totalWritten = %,d", totalWritten);
            super.close();
        }// close()

        /**
         * Writes a 32-bit int to the output stream in little-endian byte order.
         * 
         * @param v
         *            the data to write.
         * @throws IOException
         */
        public void writeInt(long v) throws IOException {
            write((int) ((v >>> 0) & 0xff));
            write((int) ((v >>> 8) & 0xff));
            write((int) ((v >>> 16) & 0xff));
            write((int) ((v >>> 24) & 0xff));
        }// writeInt()

        /**
         * Writes a 16-bit short to the output stream in little-endian byte
         * order.
         * 
         * @param v
         *            the data to write.
         * @throws IOException
         */
        public void writeShort(int v) throws IOException {
            write((v >>> 0) & 0xff);
            write((v >>> 8) & 0xff);
        }// writeShort()

    }// FilterOutputStreamEx

    /**
     * To align ZIP files :-)
     * 
     * @author Hai Bison
     * @since v1.6.9 beta
     */
    public static class ZipAligner extends BaseThread {

        private final File mInputFile;
        private final int mAlignment;
        private final File mOutputFile;

        /**
         * Creates new instance with alignment value of
         * {@link ZipAlign#DEFAULT_ALIGNMENT}.
         * 
         * @param input
         *            the input file.
         * @param output
         *            the output file.
         */
        public ZipAligner(File input, File output) {
            this(input, DEFAULT_ALIGNMENT, output);
        }// ZipAligner()

        /**
         * Creates new instance.
         * 
         * @param input
         *            the input file.
         * @param alignment
         *            the alignment, {@link ZipAlign#DEFAULT_ALIGNMENT} is
         *            highly recommended.
         * @param output
         *            the output file.
         */
        public ZipAligner(File input, int alignment, File output) {
            mInputFile = input;
            mAlignment = alignment;
            mOutputFile = output;

            setName(Messages.getString(R.string.apk_aligner_thread));
        }// ZipAligner()

        private ZipFile mZipFile;
        private RandomAccessFile mRafInput;
        private FilterOutputStreamEx mOutputStream;
        private List<XEntry> mXEntries = new ArrayList<XEntry>();
        private long mInputFileOffset = 0;
        private int mTotalPadding = 0;

        /**
         * 0 >> 100
         */
        private double mProgress = 0;

        @Override
        public void run() {
            L.d("%s >> starting", ZipAligner.class.getSimpleName());

            try {
                openFiles();
                if (!isInterrupted())
                    copyAllEntries();
                if (!isInterrupted())
                    buildCentralDirectory();
            } catch (Exception e) {
                mOutputFile.delete();
                sendNotification(
                        MSG_ERROR,
                        Texts.NULL,
                        Messages.getString(R.string.pmsg_error_details,
                                e.getMessage(), L.printStackTrace(e)));
            } finally {
                try {
                    closeFiles();
                } catch (Exception e) {
                    mOutputFile.delete();
                    sendNotification(
                            MSG_ERROR,
                            Texts.NULL,
                            Messages.getString(R.string.pmsg_error_details,
                                    e.getMessage(), L.printStackTrace(e)));
                }
            }

            if (isInterrupted())
                sendNotification(MSG_ERROR, Texts.NULL,
                        Messages.getString(R.string.cancelled));

            sendNotification(MSG_DONE);

            L.d("%s >> finishing", ZipAligner.class.getSimpleName());
        }// run()

        /**
         * Opens files.
         * <p>
         * This takes 5% of total.
         * </p>
         * 
         * @throws IOException
         */
        private void openFiles() throws IOException {
            sendNotification(MSG_INFO, Texts.NULL, String.format(
                    "%s\n\n",
                    Messages.getString(R.string.pmsg_aligning_apk,
                            mInputFile.getName(), mAlignment)));

            mZipFile = new ZipFile(mInputFile);
            mRafInput = new RandomAccessFile(mInputFile, "r");
            mOutputStream = new FilterOutputStreamEx(new BufferedOutputStream(
                    new FileOutputStream(mOutputFile), Files.FILE_BUFFER));

            sendNotification(MSG_INFO, mProgress = 5);
        }// openFiles()

        /**
         * Copies all entries, aligning them if needed.
         * <p>
         * This takes 80% of total.
         * </p>
         * 
         * @throws IOException
         */
        private void copyAllEntries() throws IOException {
            final int entryCount = mZipFile.size();
            if (entryCount == 0) {
                sendNotification(MSG_INFO, mProgress += 80);
                return;
            }

            final float progress = 80f / entryCount;

            final Enumeration<? extends ZipEntry> entries = mZipFile.entries();
            while (entries.hasMoreElements() && !isInterrupted()) {
                final ZipEntry entry = entries.nextElement();

                int flags = entry.getMethod() == ZipEntry.STORED ? 0 : 1 << 3;
                flags |= 1 << 11;

                final long outputEntryHeaderOffset = mOutputStream.totalWritten;
                if (Sys.DEBUG)
                    L.d("\t\toutputEntryHeaderOffset = %,d",
                            outputEntryHeaderOffset);

                final int inputEntryHeaderSize = ZIP_ENTRY_HEADER_LEN
                        + (entry.getExtra() != null ? entry.getExtra().length
                                : 0)
                        + entry.getName().getBytes(Texts.UTF8).length;
                final long inputEntryDataOffset = mInputFileOffset
                        + inputEntryHeaderSize;

                sendNotification(
                        MSG_INFO,
                        Texts.NULL,
                        String.format("%,15d  %s", inputEntryDataOffset,
                                entry.getName()));

                final int padding;

                if (entry.getMethod() != ZipEntry.STORED) {
                    /*
                     * The entry is compressed, copy it without padding.
                     */
                    padding = 0;
                } else {
                    /*
                     * Copy the entry, adjusting as required. We assume that the
                     * file position in the new file will be equal to the file
                     * position in the original.
                     */
                    long newOffset = inputEntryDataOffset + mTotalPadding;
                    if (Sys.DEBUG)
                        L.d("\t\t\tnewOffset = %,d", newOffset);
                    padding = (int) ((mAlignment - (newOffset % mAlignment)) % mAlignment);
                    mTotalPadding += padding;
                }

                final XEntry xentry = new XEntry(entry,
                        outputEntryHeaderOffset, flags, padding);
                mXEntries.add(xentry);

                if (Sys.DEBUG)
                    L.d("\t'%s' >> header = %,d, padding = %,d",
                            entry.getName(), inputEntryHeaderSize, padding);

                /*
                 * Modify the original header, add padding to `extra` field and
                 * copy it to output.
                 */
                byte[] extra = entry.getExtra();
                if (extra == null) {
                    extra = new byte[padding];
                    Arrays.fill(extra, (byte) 0);
                } else {
                    byte[] newExtra = new byte[extra.length + padding];
                    System.arraycopy(extra, 0, newExtra, 0, extra.length);
                    Arrays.fill(newExtra, extra.length, newExtra.length,
                            (byte) 0);
                    extra = newExtra;
                }
                entry.setExtra(extra);

                /*
                 * Now write the header to output.
                 */

                mOutputStream.writeInt(ZipOutputStream.LOCSIG);
                mOutputStream.writeShort(ZIP_ENTRY_VERSION);
                mOutputStream.writeShort(flags);
                mOutputStream.writeShort(entry.getMethod());

                int modDate;
                int time;
                GregorianCalendar cal = new GregorianCalendar();
                cal.setTime(new Date(entry.getTime()));
                int year = cal.get(Calendar.YEAR);
                if (year < 1980) {
                    modDate = 0x21;
                    time = 0;
                } else {
                    modDate = cal.get(Calendar.DATE);
                    modDate = (cal.get(Calendar.MONTH) + 1 << 5) | modDate;
                    modDate = ((cal.get(Calendar.YEAR) - 1980) << 9) | modDate;
                    time = cal.get(Calendar.SECOND) >> 1;
                    time = (cal.get(Calendar.MINUTE) << 5) | time;
                    time = (cal.get(Calendar.HOUR_OF_DAY) << 11) | time;
                }

                mOutputStream.writeShort(time);
                mOutputStream.writeShort(modDate);

                mOutputStream.writeInt(entry.getCrc());
                mOutputStream.writeInt(entry.getCompressedSize());
                mOutputStream.writeInt(entry.getSize());

                mOutputStream
                        .writeShort(entry.getName().getBytes(Texts.UTF8).length);
                mOutputStream.writeShort(entry.getExtra().length);
                mOutputStream.write(entry.getName().getBytes(Texts.UTF8));
                mOutputStream.write(entry.getExtra(), 0,
                        entry.getExtra().length);

                /*
                 * Copy raw data.
                 */

                mInputFileOffset += inputEntryHeaderSize;

                final long sizeToCopy;
                if ((flags & ZIP_ENTRY_USES_DATA_DESCR) != 0)
                    sizeToCopy = (entry.isDirectory() ? 0 : entry
                            .getCompressedSize())
                            + ZIP_ENTRY_DATA_DESCRIPTOR_LEN;
                else
                    sizeToCopy = entry.isDirectory() ? 0 : entry
                            .getCompressedSize();

                if (sizeToCopy > 0) {
                    mRafInput.seek(mInputFileOffset);

                    long totalSizeCopied = 0;
                    final byte[] buf = new byte[Files.FILE_BUFFER];
                    while (totalSizeCopied < sizeToCopy) {
                        int read = mRafInput.read(
                                buf,
                                0,
                                (int) Math.min(Files.FILE_BUFFER, sizeToCopy
                                        - totalSizeCopied));
                        if (read <= 0)
                            break;

                        mOutputStream.write(buf, 0, read);
                        totalSizeCopied += read;
                    }// while
                }// if

                mInputFileOffset += sizeToCopy;

                if (padding == 0)
                    sendNotification(MSG_INFO, mProgress += progress,
                            Texts.NULL, String.format("  (%s, %s)\n",
                                    Messages.getString(R.string.compressed),
                                    Messages.getString(R.string.passed)));
                else
                    sendNotification(
                            MSG_INFO,
                            mProgress += progress,
                            Texts.NULL,
                            String.format("  (%s, %s)\n",
                                    Messages.getString(R.string.aligned),
                                    Texts.sizeToStr(padding)));
            }// while
        }// copyAllEntries()

        /**
         * Builds central directory.
         * <p>
         * This takes 10% of total.
         * </p>
         * 
         * @throws IOException
         */
        private void buildCentralDirectory() throws IOException {
            final long centralDirOffset = mOutputStream.totalWritten;

            L.d("\tWriting Central Directory at %,d", centralDirOffset);

            for (XEntry xentry : mXEntries) {
                if (isInterrupted())
                    return;

                /*
                 * Write entry.
                 */
                final ZipEntry entry = xentry.entry;

                int modDate;
                int time;
                GregorianCalendar cal = new GregorianCalendar();
                cal.setTime(new Date(entry.getTime()));
                int year = cal.get(Calendar.YEAR);
                if (year < 1980) {
                    modDate = 0x21;
                    time = 0;
                } else {
                    modDate = cal.get(Calendar.DATE);
                    modDate = (cal.get(Calendar.MONTH) + 1 << 5) | modDate;
                    modDate = ((cal.get(Calendar.YEAR) - 1980) << 9) | modDate;
                    time = cal.get(Calendar.SECOND) >> 1;
                    time = (cal.get(Calendar.MINUTE) << 5) | time;
                    time = (cal.get(Calendar.HOUR_OF_DAY) << 11) | time;
                }

                mOutputStream.writeInt(ZipFile.CENSIG); // CEN header signature
                mOutputStream.writeShort(ZIP_ENTRY_VERSION); // version made by
                mOutputStream.writeShort(ZIP_ENTRY_VERSION); // version needed
                                                             // to
                // extract
                mOutputStream.writeShort(xentry.flags); // general purpose bit
                                                        // flag
                mOutputStream.writeShort(entry.getMethod()); // compression
                                                             // method
                mOutputStream.writeShort(time);
                mOutputStream.writeShort(modDate);
                mOutputStream.writeInt(entry.getCrc()); // crc-32
                mOutputStream.writeInt(entry.getCompressedSize()); // compressed
                                                                   // size
                mOutputStream.writeInt(entry.getSize()); // uncompressed size
                final byte[] nameBytes = entry.getName().getBytes(Texts.UTF8);
                mOutputStream.writeShort(nameBytes.length);
                mOutputStream.writeShort(entry.getExtra() != null ? entry
                        .getExtra().length - xentry.padding : 0);
                final byte[] commentBytes;
                if (entry.getComment() != null) {
                    commentBytes = entry.getComment().getBytes(Texts.UTF8);
                    mOutputStream.writeShort(Math.min(commentBytes.length,
                            0xffff));
                } else {
                    commentBytes = null;
                    mOutputStream.writeShort(0);
                }
                mOutputStream.writeShort(0); // starting disk number
                mOutputStream.writeShort(0); // internal file attributes
                                             // (unused)
                mOutputStream.writeInt(0); // external file attributes (unused)
                mOutputStream.writeInt(xentry.headerOffset); // relative offset
                                                             // of
                // local
                // header
                mOutputStream.write(nameBytes);
                if (entry.getExtra() != null)
                    mOutputStream.write(entry.getExtra(), 0,
                            entry.getExtra().length - xentry.padding);
                if (commentBytes != null)
                    mOutputStream.write(commentBytes, 0,
                            Math.min(commentBytes.length, 0xffff));
            }// for xentry

            if (isInterrupted())
                return;

            sendNotification(MSG_INFO, mProgress += 5);

            /*
             * Write the end of central directory.
             */
            final long centralDirSize = mOutputStream.totalWritten
                    - centralDirOffset;
            L.d("\tWriting End of Central Directory, its size = %,d",
                    centralDirSize);

            final int entryCount = mXEntries.size();

            mOutputStream.writeInt(ZipFile.ENDSIG); // END record signature
            mOutputStream.writeShort(0); // number of this disk
            mOutputStream.writeShort(0); // central directory start disk
            mOutputStream.writeShort(entryCount); // number of directory entries
                                                  // on
            // disk
            mOutputStream.writeShort(entryCount); // total number of directory
                                                  // entries
            mOutputStream.writeInt(centralDirSize); // length of central
                                                    // directory
            mOutputStream.writeInt(centralDirOffset); // offset of central
            // directory
            if (mZipFile.getComment() != null) { // zip file comment
                final byte[] bytes = mZipFile.getComment().getBytes(Texts.UTF8);
                mOutputStream.writeShort(bytes.length);
                mOutputStream.write(bytes);
            } else {
                mOutputStream.writeShort(0);
            }

            mOutputStream.flush();

            sendNotification(MSG_INFO, mProgress += 5);
        }// buildCentralDirectory()

        /**
         * Closes all files.
         * <p>
         * This takes 5% of total.
         * </p>
         * 
         * @throws IOException
         */
        private void closeFiles() throws IOException {
            try {
                mZipFile.close();
            } finally {
                try {
                    mRafInput.close();
                } finally {
                    mOutputStream.close();
                }
            }

            sendNotification(MSG_INFO, mProgress = 100, Texts.NULL,
                    String.format("\n%s",
                            Messages.getString(R.string.pmsg_alignment_done,
                                    mOutputFile.getName())));
        }// closeFiles()
    }// ZipAligner

    /**
     * The ZIP alignment verifier.
     * 
     * @author Hai Bison
     * @since v1.6.9 beta
     */
    public static class ZipAlignmentVerifier extends BaseThread {

        private final File mInputFile;
        private final int mAlignment;
        private ZipFile mZipFile;
        private RandomAccessFile mRafInput;

        /**
         * 0 >> 100
         */
        private double mProgress = 0;
        private boolean mFoundBad = false;

        /**
         * Creates new instance with alignment value of
         * {@link ZipAlign#DEFAULT_ALIGNMENT}.
         * 
         * @param inputFile
         *            the input file to verify.
         */
        public ZipAlignmentVerifier(File inputFile) {
            this(inputFile, DEFAULT_ALIGNMENT);
        }// ZipAlignmentVerifier()

        /**
         * Creates new instance.
         * 
         * @param input
         *            the input file.
         * @param alignment
         *            the alignment, {@link ZipAlign#DEFAULT_ALIGNMENT} is
         *            highly recommended.
         */
        public ZipAlignmentVerifier(File inputFile, int alignment) {
            mInputFile = inputFile;
            mAlignment = alignment;

            setName(Messages.getString(R.string.apk_alignment_verifier_thread));
        }// ZipAlignmentVerifier()

        @Override
        public void run() {
            L.d("%s >> starting", ZipAlignmentVerifier.class.getSimpleName());

            try {
                openFiles();
                if (!isInterrupted())
                    verify();
            } catch (Exception e) {
                mFoundBad = true;
                sendNotification(
                        MSG_ERROR,
                        Texts.NULL,
                        Messages.getString(R.string.pmsg_error_details,
                                e.getMessage(), L.printStackTrace(e)));
            } finally {
                try {
                    closeFiles();
                } catch (Exception e) {
                    mFoundBad = true;
                    sendNotification(
                            MSG_ERROR,
                            Texts.NULL,
                            Messages.getString(R.string.pmsg_error_details,
                                    e.getMessage(), L.printStackTrace(e)));
                }
            }

            if (isInterrupted())
                sendNotification(MSG_ERROR, Texts.NULL,
                        Messages.getString(R.string.cancelled));

            sendNotification(MSG_DONE);

            L.d("%s >> finishing", ZipAlignmentVerifier.class.getSimpleName());
        }// run()

        /**
         * Opens files.
         * <p>
         * This takes 5% of total.
         * </p>
         * 
         * @throws IOException
         */
        private void openFiles() throws IOException {
            sendNotification(MSG_INFO, Texts.NULL, String.format("%s\n\n",
                    Messages.getString(
                            R.string.pmsg_verifying_alignment_of_apk,
                            mInputFile.getName(), mAlignment)));

            mZipFile = new ZipFile(mInputFile);
            mRafInput = new RandomAccessFile(mInputFile, "r");

            sendNotification(MSG_INFO, mProgress = 5);
        }// openFiles()

        /**
         * Verifies input file.
         * <p>
         * This takes 90% of total.
         * </p>
         * 
         * @throws IOException
         */
        private void verify() throws IOException {
            final int entryCount = mZipFile.size();
            if (entryCount == 0) {
                sendNotification(MSG_INFO, mProgress += 90);
                return;
            }

            final Enumeration<? extends ZipEntry> entries = mZipFile.entries();
            final float progress = 90f / entryCount;
            long dataOffset = 0;

            while (entries.hasMoreElements() && !isInterrupted()) {
                final ZipEntry entry = entries.nextElement();

                mRafInput.seek(dataOffset + ZIP_ENTRY_OFFSET_EXTRA_LEN);
                final byte[] buf = new byte[ZIP_ENTRY_FIELD_EXTRA_LEN_SIZE];
                if (mRafInput.read(buf) != buf.length) {
                    mFoundBad = true;
                    throw new IOException("Reading extra field length failed");
                }
                /*
                 * Fetches unsigned 16-bit value from byte array at specified
                 * offset. The bytes are assumed to be in Intel (little-endian)
                 * byte order.
                 */
                final int extraLen = (buf[0] & 0xff) | ((buf[1] & 0xff) << 8);

                final int headerSize = ZIP_ENTRY_HEADER_LEN + extraLen
                        + entry.getName().getBytes(Texts.UTF8).length;

                if (entry.getMethod() != ZipEntry.STORED) {
                    /*
                     * The entry is compressed.
                     */
                    sendNotification(
                            MSG_INFO,
                            mProgress += progress,
                            Texts.NULL,
                            String.format("%,15d  %s  (%s - %s)\n", dataOffset
                                    + headerSize, entry.getName(),
                                    Messages.getString(R.string.ok),
                                    Messages.getString(R.string.compressed)));
                } else {
                    /*
                     * The entry is not compressed.
                     */
                    if ((dataOffset + headerSize) % mAlignment != 0) {
                        sendNotification(
                                MSG_INFO,
                                mProgress += progress,
                                Texts.NULL,
                                String.format(
                                        "%,15d  %s  (%s - %s)\n",
                                        dataOffset + headerSize,
                                        entry.getName(),
                                        Messages.getString(R.string.BAD),
                                        Texts.sizeToStr((dataOffset + headerSize)
                                                % mAlignment)));
                        mFoundBad = true;
                    } else {
                        sendNotification(
                                MSG_INFO,
                                mProgress += progress,
                                Texts.NULL,
                                String.format("%,15d  %s  (%s)\n", dataOffset
                                        + headerSize, entry.getName(),
                                        Messages.getString(R.string.ok)));
                    }
                }

                int flags = entry.getMethod() == ZipEntry.STORED ? 0 : 1 << 3;
                flags |= 1 << 11;
                final long dataSize;
                if ((flags & ZIP_ENTRY_USES_DATA_DESCR) != 0)
                    dataSize = (entry.isDirectory() ? 0 : entry
                            .getCompressedSize())
                            + ZIP_ENTRY_DATA_DESCRIPTOR_LEN;
                else
                    dataSize = entry.isDirectory() ? 0 : entry
                            .getCompressedSize();

                if (Sys.DEBUG)
                    L.d("size = %,8d, compressed = %,8d, crc32 = %08x, data mHeaderOffset = %,8d >> %,8d"
                            + " >> Entry '%s'", entry.getSize(),
                            entry.getCompressedSize(), entry.getCrc(),
                            dataOffset, dataOffset + headerSize,
                            entry.getName());

                dataOffset += headerSize + dataSize;
            }// while
        }// verify()

        /**
         * Closes source files.
         * <p>
         * This takes 5% of total.
         * </p>
         * 
         * @throws IOException
         */
        private void closeFiles() throws IOException {
            mZipFile.close();
            mRafInput.close();

            sendNotification(
                    MSG_INFO,
                    mProgress = 100,
                    Texts.NULL,
                    String.format(
                            "\n%s",
                            mFoundBad ? Messages
                                    .getString(R.string.verification_failed)
                                    : Messages
                                            .getString(R.string.verification_succesful)));
        }// closeFiles()

    }// ZipAlignmentVerifier

}
