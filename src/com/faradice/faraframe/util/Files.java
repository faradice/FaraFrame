/*
 * Copyright (c) 2002 deCODE Genetics Inc.
 * All Rights Reserved
 *
 * This software is the confidential and proprietary information of
 * deCODE Genetics Inc.  ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with deCODE.
 *
 */
package com.faradice.faraframe.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.WeakHashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import com.faradice.faraframe.log.Log;

/**
 * Collection of file management methods
 */
public final class Files {
    /**
     * Create an FileOutputStream that is named after the weekday of the specified date and located in the specified folder.
     * If the file exists and it's last modification date is more that 2 days prior to the specified date it will be wiped, else
     * it will be appended.
     * @param path The folder to locate file in. If it doesn't exists, it will be created.
     * @param now The date to name the file by
     * @return The FileOutputStream to use
     * @throws FileNotFoundException
     */
    public static FileOutputStream createWeekdayOutputStream(String path, Date now) throws FileNotFoundException {
        File logdir = new File(path);
        if (!logdir.exists()) {
            logdir.mkdirs();
        }

        // Construct log file name based on current date
        String filename = String.format(Locale.ENGLISH, "%s/%tA.log", path, now);
        Log.echo.fine("Log results to: %s", filename);
        File file = new File(filename);
        // If file is older than two days, we don't want to append, rather create new
        boolean append = file.lastModified()+(2*24*60*60*1000) > now.getTime();

        // Add a header for when the run completed
        FileOutputStream outstream = new FileOutputStream(filename, append);
        return outstream;
    }
   
   /**
    * Create a digest of the content for the specified file. The digest is extremely
    * sensitive for changes in the file and can be used to verify that the file has not
    * been tampered with. Note that is is assumed the file can fit into memory.
    * @param file The file to create digest for
    * @return The digest
    */
   public static String createDigest(File file) {
       try {
           byte[] bytes = new byte[(int)file.length()];
           FileInputStream is = new FileInputStream(file);
           try {
               int len = is.read(bytes);
               assert len == bytes.length;
               return ByteArray.DJBHash(bytes);
           } finally {
               is.close();
           }
       } catch (Exception ex) {
           ex.printStackTrace();
           throw new RuntimeException("Error Creating Digest for " + file.getName(), ex);
       }
   }
   
   /**
    * Confirm that the currenct content of the specified file corresponds with
    * the specified digest.
    * @param file The file to check.
    * @param digest The digest for the file
    * @return True if file content gives the same digest as specified.
    */
   public static boolean confirmDigest(File file, String digest) {
       String newDigest = createDigest(file);
       return digest.equals(newDigest);
   }
    
    
    /**
     * Use the specified file and try to find an existing parent path - the rest must
     * then be non existing.
     * @param file  The file locator
     * @return The path-part of the the file path that doens't exists
     */
    public static String findLastNonExistingPart(File file) {
        String missing = null;
        while (!file.exists()) {
            missing = missing == null ? file.getName() : file.getName() + "/" + missing;
            file = file.getParentFile();
        }
        
        return missing;
    }
    
    /**
     * Reads a file and returns the byte array for the file
     * @param fileName
     * @return a byte array containing the file
     * @throws Exception
     */
    public static byte[] fileToBytes(String fileName) throws Exception {
        return stream2bytes(new FileInputStream(fileName));
    }

    /**
     * Reads a file and returns the byte array for the file
     * @param file
     * @return a byte array containing the file
     * @throws IOException
     */
    public static byte[] fileToBytes(File file) throws IOException {
        return stream2bytes(new FileInputStream(file));
    }
    
    /**
     * Writes the given byte array to a file.
     *
     * @param fileBytes   the the byte array to be written
     * @param filename    name of the file that will be created
     * @throws Exception On exceptions
     */
    public static void bytesToFile(byte[] fileBytes, String filename) throws Exception {
        FileOutputStream fileOutputStream = new FileOutputStream(filename);
        try {
            fileOutputStream.write(fileBytes);
        } finally {
            fileOutputStream.close();
        }
    }

    /**
     * Takes an InputStream and converts to a byte array
     * @param in
     * @return the byte array
     * @throws IOException
     */
    public static byte[] stream2bytes(InputStream in) throws IOException {
        BufferedInputStream bis = new BufferedInputStream(in);
        // the default buffer size is the same size as in the default char size in BufferedInputStream
        byte[] buffer = new byte[2048];
        ByteArrayOutputStream baos = new ByteArrayOutputStream(2048);
        int size = 0;
        while ((size = bis.read(buffer)) != -1) {
            baos.write(buffer, 0, size);
        }
        return baos.toByteArray();
    }
    
    /**
     * Create a string with file content
     * @param path The path to the file
     * @return The content of the file as string
     * @throws java.io.IOException
     */
    public static String fileToString(String path) throws java.io.IOException {
    	final File file = new File(path);
        final StringBuilder text = new StringBuilder((int)file.length());
        final BufferedReader reader = new BufferedReader(new FileReader(file));
        try {
	        final char[] buf = new char[32*1024];
	        while(reader.read(buf) != -1){
	            text.append(buf);
	        }
	        return text.toString();
        } finally {
        	reader.close();
        }
    }
    
    /**
     * Create a string with file content
     * @param stream The stream to read
     * @return The content of the file as string
     * @throws java.io.IOException
     */
    public static String streamToString(InputStream stream) throws java.io.IOException {
        final StringBuilder text = new StringBuilder(10*1024);
        final BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        try {
	        final char[] buf = new char[32*1024];
	        while(reader.read(buf) != -1){
	            text.append(buf);
	        }
	        return text.toString();
        } finally {
        	reader.close();
        }
    }
    

    // ---------------------------------------------
    // Copy stuff
    // ---------------------------------------------
    
    /**
     * Copies given source file to the destination file.
     * If destination file does not exists it is crreated. 
     * @param src Source file
     * @param dest Destination file
     * @return true in all cases ?
     * @throws Exception
     */
    public static final boolean copyFile(String src, String dest) throws Exception {
        // Use unbuffered streams, because we're going to use a large buffer
        FileInputStream input = new FileInputStream(src);
        try {
            FileOutputStream output = new FileOutputStream(dest);
            try {
                // Read from input stream an byte array and write to the output stream
                int bytesRead;
                byte[] buffer = new byte[34 * 1024];
                while ((bytesRead = input.read(buffer, 0, buffer.length)) > 0)
                    output.write(buffer, 0, bytesRead);
                output.flush();
            } finally {
                output.close();
            }
        } finally {
            input.close();
        }

        return true;
    }
    
    /**
     * Copies given source file to the destination file.
     * @param src Source file
     * @param dest Destination file
     * @return true in all cases ?
     * @throws Exception
     */
    public static final boolean copyFile(File src, String dest) throws Exception {
        return copyFile(src.getAbsolutePath(), dest);
    }

    /**
     * Copies given source folder to the destination folder.
     * @param src Source folder
     * @param dest Destination folder
     * @param listener
     * @return true if ok
     * @throws Exception
     */
    public static final boolean copyFolder(File src, String dest, StatusListener listener) throws Exception {
        try {
            File fdest = new File(dest);
            if (listener != null)
                listener.pushPath(src.getName());
            if (fdest.exists() || fdest.mkdir()) {
                String[] files = src.list();
                if (files != null) {
                    for (int i = 0; i < files.length; i++) {
                        if (listener != null)
                            listener.update((i * 100) / files.length, files[i]);
                        if (!copy(src + "/" + files[i], dest + "/" + files[i], listener))
                            return false;
                    }
                }
                return true;
            }
        } finally {
            if (listener != null)
                listener.popPath();
        }

        return false;
    }

    
    /**
     * The static method that actually performs the file copy.
     * Before copying the file, however, it performs a lot of tests to make
     * sure everything is as it should be.
     * @param from_name
     * @param to_name
     * @throws IOException
     */
    public static final void copy(String from_name, String to_name) throws IOException {
        copy(from_name, to_name, false);
    }

    /**
     * The static method that actually performs the file copy.
     * Before copying the file, however, it performs a lot of tests to make
     * sure everything is as it should be.
     * @param from_name
     * @param to_name
     * @param replace
     * @throws IOException
     */
    public static final void copy(String from_name, String to_name, boolean replace) throws IOException {
        // If the destination is a directory, use the source file name
        // as the destination file name

        File from_file = new File(from_name);  // Get File objects from Strings
        File to_file = new File(to_name);

        // First make sure the source file exists, is a file, and is readable.
        if (!from_file.exists())
            abort("FileCopy: no such source file: " + from_name);
        if (!from_file.isFile())
            abort("FileCopy: can't copy directory: " + from_name);
        if (!from_file.canRead())
            abort("FileCopy: source file is unreadable: " + from_name);

        // If the destination is a directory, use the source file name
        // as the destination file name
        if (to_file.isDirectory())
            to_file = new File(to_file, from_file.getName());

        // If the destination exists, make sure it is a writeable file
        // and ask before overwriting it.  If the destination doesn't
        // exist, make sure the directory exists and is writeable.
        if (to_file.exists()) {
            if (!to_file.canWrite())
                abort("FileCopy: destination file is unwriteable: " + to_name);
            // Check the response.  If not a Yes, abort the copy.
            if (!replace)
                abort("FileCopy: existing file was not overwritten.");
        } else {
            // if file doesn't exist, check if directory exists and is writeable.
            // If getParent() returns null, then the directory is the current dir.
            // so look up the user.dir system property to find out what that is.
            String parent = to_file.getParent();  // Get the destination directory
            if (parent == null) parent = System.getProperty("user.dir"); // or CWD
            File dir = new File(parent);          // Convert it to a file.
            if (!dir.exists())
                abort("FileCopy: destination directory doesn't exist: " + parent);
            if (dir.isFile())
                abort("FileCopy: destination is not a directory: " + parent);
            if (!dir.canWrite())
                abort("FileCopy: destination directory is unwriteable: " + parent);
        }

        // If we've gotten this far, then everything is okay.
        // So we copy the file, a buffer of bytes at a time.
        FileInputStream from = null;  // Stream to read from source
        FileOutputStream to = null;   // Stream to write to destination
        // Always close the streams, even if exceptions were thrown
        try {
            from = new FileInputStream(from_file);  // Create input stream
            to = new FileOutputStream(to_file);     // Create output stream
            byte[] buffer = new byte[4096];         // A buffer to hold file contents
            int bytes_read;                         // How many bytes in buffer
            // Read a chunk of bytes into the buffer, then write them out,
            // looping until we reach the end of the file (when read() returns -1).
            // Note the combination of assignment and comparison in this while
            // loop.  This is a common I/O programming idiom.

            while ((bytes_read = from.read(buffer)) != -1) // Read bytes until EOF
                to.write(buffer, 0, bytes_read);            //   write bytes
        }
        finally {
            if (from != null) try {
                from.close();
            } catch (IOException e) {
                // Best effor close, ignore any errors
            }
            if (to != null) try {
                to.close();
            } catch (IOException e) {
                // Best effort close, ignore any errors
            }
        }
    }

    /**
     * Copies file or a folder to its destination  
     * @param src
     * @param dest
     * @param listener
     * @return true if ok
     * @throws Exception
     */
    public static final boolean copy(String src, String dest, StatusListener listener) throws Exception {
        File fsrc = new File(src);
        if (fsrc.isDirectory())
            return copyFolder(fsrc, dest, listener);
		return copyFile(fsrc, dest);
    }
    
    // ---------------------------------------------
    // Move stuff
    // ---------------------------------------------

    /**
     * Moves given source file to the destination file.
     * If destination file exists it is overwritten. 
     * @param src
     * @param dest
     * @return True if file was moved, else false
     * @throws Exception
     */
    public static final boolean moveFile(String src, String dest) throws Exception {
        return moveFile(new File(src), dest);
    }

    /**
     * Moves given source file to the destination file.
     * If destination file exists it is overwritten. 
     * @param src
     * @param dest
     * @return True if file was moved, else false
     * @throws Exception
     */
    public static final boolean moveFile(File src, String dest) throws Exception {
        if (copyFile(src.getAbsolutePath(), dest)) {
            src.delete();
            return true;
        }
        return false;
    }

    /**
     * Moves the given folder and its content to a new location 
     * @param src
     * @param dest
     * @param listener
     * @return true if the move succeded
     * @throws Exception
     */
    public static final boolean moveFolder(File src, String dest, StatusListener listener) throws Exception {
        if (copyFolder(src, dest, listener)) {
            deleteFolder(src);
            return true;
        }
        return false; // Couldn't move the folder
    }

    /**
     * Moves the file or folder and its content to a new location 
     * @param src
     * @param dest
     * @param listener
     * @return true if ok
     * @throws Exception
     */
    public static final boolean move(String src, String dest, StatusListener listener) throws Exception {
        File fsrc = new File(src);
        if (fsrc.isDirectory())
            return moveFolder(fsrc, dest, listener);
		return moveFile(fsrc, dest);
    }

    
    // ---------------------------------------------
    // Delete stuff
    // ---------------------------------------------
    /**
     * Delete a specified folder and all its contents.
     * @param src The File descriptior for the folder.
     * @return true if folder is deleted, else false.
     */
    public static final boolean deleteFolder(File src) {
        if (src != null) {
            deleteFolderContents(src);
            src.delete();
            return true;
        }

        return false;
    }

    /**
     * Delete the content of a specified folder.
     * @param src The path to a specfic folder.
     * @throws FileNotFoundException If the folder is not found.
     */
    public static final void deleteFolderContents(String src) throws java.io.FileNotFoundException {
        File fsrc = new File(src);
        if (!fsrc.isDirectory() || !fsrc.exists())
            throw new java.io.FileNotFoundException("The directory " + src + " was not found");
        deleteFolderContents(fsrc);
    }

    /**
     * Delete the content of a specified folder.
     * @param src The file descriptor for the folder.
     */
    public static final void deleteFolderContents(File src) {
        if (src != null) {
            File[] files = src.listFiles();
            for (File file : files) {
                if (file.isDirectory())
                    deleteFolder(file);
                else
                    file.delete();
            }
        }
    }


    /**
     * Deletes the given file
     * @param filename
     */
    public static final void delete(String filename) {
        delete(filename, false);
    }

    /**
     * Deletes the given file or folder
     * @param filename
     * @param force set true to delete one emply folders 
     */
    public static final void delete(String filename, boolean force) {
        // Create a File object to represent the filename
        File f = new File(filename);
        // Make sure the file or directory exists and isn't write protected
        if (!f.exists()) fail("Delete: no such file or directory: " + filename);
        if (!f.canWrite()) fail("Delete: write protected: " + filename);
        // If it is a directory, make sure it is empty
        if (f.isDirectory()) {
            String[] files = f.list();
            if (!force && files.length > 0)
                fail("Delete: directory not empty: " + filename);
            else {
                deleteFolder(f);
            }
            
        }
        // If we passed all the tests, then attempt to delete it
        boolean success = f.delete();
        // And throw an exception if it didn't work for some (unknown) reason.
        // For example, because of a bug with Java 1.1.1 on Linux,
        // directory deletion always fails
        if (!success) fail("Delete: deletion failed");
    }

    // ---------------------------------------------
    // Temp stuff
    // ---------------------------------------------
    
    /**
     * @return a temp folder that always ends with a file separator  
     * for every OS.  By default the end separator is OS dependent
     */
    public static final String getTempFolder() {
		String tempdir = System.getProperty("java.io.tmpdir");
		if (!(tempdir.endsWith("/") || tempdir.endsWith("\\"))) {
			tempdir = tempdir + System.getProperty("file.separator");
		}
		return tempdir;
    }

    /**
     * @return a string containing a name of a unique temp folder,
     * which of course can never exists at current time!
     */
    public static final String getUniqueTempFolder() {
		return getTempFolder() + System.currentTimeMillis()+"_"+new Random().nextInt();
    }
    
    // ---------------------------------------------
    // Zip stuff
    // ---------------------------------------------

    /**
     * Unzips a WHOLE zipFile to a given path.
     * @param zipFile The ziped input file
     * @param outPath optional, if the files are to be written to a specific directory. Defaults to current directory.
     * @throws IOException
     */
    public static final void unzip(String zipFile, String outPath) throws IOException {
        InputStream in = new BufferedInputStream(new FileInputStream(zipFile));
        ZipInputStream zin = new ZipInputStream(in);
        ZipEntry e;
        while ((e = zin.getNextEntry()) != null) {
            unzip(zin, e.getName(), outPath); // unzip all the files
        }
        zin.close();
    }

    /**
     * Unzips ONE  file from a zipFile archive to a given path.
     * @param zipFile
     * @param outPath optional, if the files are to be written to a specific directory. Defaults to current directory.
     * @param extractFile the file to be extracted.
     * @throws IOException
     */
    public static final void unzip(String zipFile, String outPath, String extractFile) throws IOException {
        InputStream in = new BufferedInputStream(new FileInputStream(zipFile));
        ZipInputStream zin = new ZipInputStream(in);
        ZipEntry e;
        while ((e = zin.getNextEntry()) != null) {
            if (e.getName().equals(extractFile)) { // unzip the requested file
                unzip(zin, extractFile, outPath);
            }
        }
        zin.close();
    }
    
    /**
     * Unzips ONE file from the ZipStream  (zipFile)
     * @param zin ZipInputStream zin i.e. (new ZipInputStream(new BufferedInputStream(new FileInputStream(ZIPFILE))))
     * @param unzipFile the file to be extracted from the zipFile.
     * @param outPath optional, if the files are to be written to a specific directory.
     * @throws IOException
     */
    public static final void unzip(ZipInputStream zin, String unzipFile, String outPath) throws IOException {
        FileOutputStream out = new FileOutputStream(outPath + "/" + unzipFile);
        try {
            byte[] b = new byte[1024 * 1024];
            int len = 0;
            while ((len = zin.read(b)) != -1) {
                out.write(b, 0, len);
            }
            out.flush();
        } finally {
            out.close();
        }
    }

    /**
     * Unzips ONE file from the ZipStream  (zipFile) to current directory.
     * @param zin ZipInputStream zin i.e. (new ZipInputStream(new BufferedInputStream(new FileInputStream(ZIPFILE))))
     * @param unzipFile the file to be extracted from the zipFile.
     * @throws IOException
     */
    public static final void unzip(ZipInputStream zin, String unzipFile) throws IOException {
        unzip(zin, unzipFile, null);
    }

    /**
     * Zip the contents of a folder to a specifed location and specifed archive name
     * @param folder The folde to zip
     * @param archive_location The location of the archive to create
     * @param archive_name The archive name
     * @param listener The listener to listen to status of
     * @throws Exception 
     */
    public static final void zipFolder(File folder, String archive_location,
                                       String archive_name, StatusListener listener) throws Exception {
        try {
            if (listener != null)
                listener.pushPath(folder.getName());

            // If destination location doesen't exists create it
            // Also if archive exists in destincation location remove the archive
            File f = new File(archive_location);
            if (f.exists()) {
                try {
                    Files.delete(archive_location + '/' + archive_name + ".zip");
                } catch (Exception ex) {
                    // Ignore any errors due to delete
                }
            } else
                f.mkdir();

            // Create the archive
            ZipOutputStream archive = createZipArchive(archive_location + '/' + archive_name);

            // Add every file in the folder to the zip archive
            String[] files = folder.list();
            if (files != null) {
                for (int i = 0; i < files.length; i++) {
                    if (listener != null)
                        listener.update((i * 100) / files.length, files[i]);
                    zip(folder + "/" + files[i], archive);
                }
            }

            // Take care to close the stream
            archive.close();
        } finally {
            if (listener != null)
                listener.popPath();
        }
    }

    /**
     * Create an Zip archive Stream
     * @param archive The name of the archive to create
     * @return The zip output stream
     * @throws Exception 
     */
    public static final ZipOutputStream createZipArchive(String archive) throws Exception {
        FileOutputStream f = new FileOutputStream(archive + ".zip");
        ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(f));
        return out;
    }

    /**
     * Zip the content of a file to a specified archive
     * @param file_name The name of the file to zip 
     * @param archive The zip output stream to use
     * @throws Exception 
     */
    public static final void zip(String file_name, ZipOutputStream archive) throws Exception {
        // Add an new Zip Archive Entry and create an unbufferred input stream
        archive.putNextEntry(new ZipEntry(file_name));
        FileInputStream in = new FileInputStream(file_name);
        try {
            // Read the input file into a byte array and add to the zip file
            int bytes_read;
            byte[] buffer = new byte[32 * 1024];
            while ((bytes_read = in.read(buffer, 0, buffer.length)) > 0)
                archive.write(buffer, 0, bytes_read);
        } finally {
            in.close();
        }
        archive.closeEntry();
    }


    /** A convenience method to throw an exception */
    protected static final void fail(String msg) throws IllegalArgumentException {
        throw new IllegalArgumentException(msg);
    }

    /** A convenience method to throw an exception */
    private static final void abort(String msg) throws IOException {
        throw new IOException(msg);
    }

}
