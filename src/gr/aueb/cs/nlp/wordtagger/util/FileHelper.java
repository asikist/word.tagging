package gr.aueb.cs.nlp.wordtagger.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * 
 * @author Thomas Asikis
 * @license Copyright (c) 2016 Thomas Asikis
 *			Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * 			The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * 			THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
public class FileHelper {

	/**
	 * Getting a Stream Reader in order to read.
	 * 
	 * @param path
	 *            the path of the file.
	 * @param the
	 *            encoding, the file uses
	 * @return the input stream reader, which can be used to read files
	 * @throws UnsupportedEncodingException
	 * @throws FileNotFoundException
	 */
	public static InputStreamReader getInputReader(String path, String encoding)
			throws UnsupportedEncodingException, FileNotFoundException {
		return new InputStreamReader(new FileInputStream(path), encoding);
	}

	/**
	 * Getting a Stream Reader in order to read. Using the default system
	 * charset.
	 * 
	 * @param path
	 *            the path of the file.
	 * @return the input stream reader, which can be used to read files
	 * @throws UnsupportedEncodingException
	 * @throws FileNotFoundException
	 */
	public static InputStreamReader getInputReader(String path)
			throws UnsupportedEncodingException, FileNotFoundException {
		return new InputStreamReader(new FileInputStream(path), Charset.defaultCharset());
	}
	
	public static InputStream getInputStream(String path)
			throws UnsupportedEncodingException, FileNotFoundException {
		return new FileInputStream(path);
	}

	public static void readFile(String path, CallBack<String> cb) {
		try (BufferedReader br = new BufferedReader(FileHelper.getInputReader(path, "UTF-8"))) {
			String line;
			while ((line = br.readLine()) != null) {
				cb.call(line);
			}
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void readFile(InputStream in, CallBack<String> cb) {
		try (BufferedReader br = new BufferedReader( new InputStreamReader(in, "UTF-8"))) {
			String line;
			while ((line = br.readLine()) != null) {
				cb.call(line);
			}
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void toFile(String path, String toWrite) {
		try {

			File file = new File(path);

			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(toWrite);
			bw.close();

			// System.out.println("Done");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String fromFile(String path) {
		File file = new File(path);
		byte[] data = new byte[(int) file.length()];
		String result = "";
		try {

			FileInputStream fis = new FileInputStream(file);
			fis.read(data);
			fis.close();
			result = new String(data, "UTF-8");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	public static void append(String fileName, String text) throws Exception {
		File f = new File(fileName);
		long fileLength = f.length();
		RandomAccessFile raf = new RandomAccessFile(f, "rw");
		raf.seek(fileLength);
		// raf.write(StandardCharsets.UTF_8.encode(text).array());
		raf.writeUTF(text);
		raf.close();
	}

	public static void append(String fileName, byte[] bytes) throws Exception {
		File f = new File(fileName);
		long fileLength = f.length();
		RandomAccessFile raf = new RandomAccessFile(f, "rw");
		raf.seek(fileLength);
		raf.write(bytes);
		raf.close();
	}

	public static void serialize(Serializable obj, String path) throws IOException {
		Logger log = Logger.getAnonymousLogger();
		FileOutputStream fileOut = new FileOutputStream(path);
		ObjectOutputStream out = new ObjectOutputStream(fileOut);
		out.writeObject(obj);
		out.close();
		fileOut.close();
		log.log(Level.INFO, "Serialized data is saved in " + path);
	}

	public static Object deserialize(String path) throws IOException, ClassNotFoundException {
		Logger log = Logger.getAnonymousLogger();
		FileInputStream fileIn = new FileInputStream(path);
		ObjectInputStream in = new ObjectInputStream(fileIn);
		Object result = in.readObject();
		in.close();
		fileIn.close();
		log.log(Level.INFO, "Deserialized from " + path);
		return result;
	}

	public static void zipFile( String path, int buffer, String outputPath) {

		try {
			BufferedInputStream origin = new BufferedInputStream(new FileInputStream(new File(path)));
			FileOutputStream dest = new FileOutputStream(outputPath);
			ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
			out.setMethod(ZipOutputStream.DEFLATED);
			byte data[] = new byte[buffer];
			// get a list of files from current directory
			System.out.println("Adding: " + path);
			FileInputStream fi = new FileInputStream(path);
			origin = new BufferedInputStream(fi, buffer);
			ZipEntry entry = new ZipEntry(new File(path).getName());
			out.putNextEntry(entry);
			int count;
			while ((count = origin.read(data, 0, buffer)) != -1) {
				out.write(data, 0, count);
			}
			origin.close();

			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static InputStream unzip(String path, int buffer) {
		ZipFile zipfile;
		try {
			zipfile = new ZipFile(path);
			Enumeration<? extends ZipEntry> e = zipfile.entries();
			BufferedInputStream is = null;
			ZipEntry entry;

			while (e.hasMoreElements()) {
				entry = (ZipEntry) e.nextElement();
				System.out.println("Extracting: " + entry);
				is = new BufferedInputStream(zipfile.getInputStream(entry));
				return is;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
	
	 /**
	   * Determine whether a file is a ZIP File.
	   */
	  public static boolean isZipFile(File file) throws IOException {
	      if(file.isDirectory()) {
	          return false;
	      }
	      if(!file.canRead()) {
	          throw new IOException("Cannot read file "+file.getAbsolutePath());
	      }
	      if(file.length() < 4) {
	          return false;
	      }
	      DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
	      int test = in.readInt();
	      in.close();
	      return test == 0x504b0304;
	  }

	/**
	 * Combines 2 files vertically in a new file,
	 * 
	 * @param pathA
	 *            the first File to Combine;
	 * @param pathB
	 *            the Second File To combine;
	 * @param newPath
	 *            the new path of the resulting file;
	 */
	public static void combineFiles(String newPath, boolean overwrite, String... paths) {
		File file = new File(newPath);
		if (file.exists() && overwrite) {
			Logger.getAnonymousLogger().log(Level.WARNING, "File already exists, overwriting it");
			file.delete();
		} else if (file.exists() && !overwrite) {
			Logger.getAnonymousLogger().log(Level.WARNING, "File already exists, appending to it");
		}
		for (String path : paths) {
			readFile(path, new CallBack<String>() {
				@Override
				public void call(String t) {
					toFile(newPath, t + "\n");
				}
			});
		}
	}

	public static void main(String[] args) throws IOException {
	}

}
