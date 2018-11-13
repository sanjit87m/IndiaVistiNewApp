 package org.bsc.com.india;

 import android.app.ProgressDialog;
 import android.content.Context;
 import android.os.AsyncTask;
 import android.os.Environment;
 import android.widget.ProgressBar;
 import android.widget.Toast;
 import org.bsc.com.india.PdfView.DownloadListener;


 import java.io.BufferedInputStream;
 import java.io.File;
 import java.io.FileNotFoundException;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.RandomAccessFile;
 import java.net.HttpURLConnection;
 import java.net.MalformedURLException;
 import java.net.URL;
 import java.util.List;
 import java.util.Map;

 public class FileDownloader extends AsyncTask<Void, Integer, Void> {
	RandomAccessFile output;
	InputStream input;
	String urlString;
	int lenghtOfFile;
	Context mContext;
	private int progressValue = 0;
	DownloadListener downloadListener;
	 String DESTINATION_PATH;
	// String fileType;
	 private ProgressDialog mDialog;

	 public FileDownloader(Context context, String urlString, String fileName, DownloadListener downloadListener) {
		this.mContext = context;
		this.urlString = urlString;
		this.downloadListener = downloadListener;

		 DESTINATION_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Download"+File.separator +"BSCVisit"+ File.separator+fileName;
			mDialog = new ProgressDialog(mContext);
			mDialog.setMessage("Downloading file...");
			mDialog.setCancelable(false);
			mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			mDialog.show();

	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@SuppressWarnings("deprecation")
	@Override
	protected Void doInBackground(Void... params) {

		try {
			long downloadedSize = 0;
			URL url = new URL(urlString);
			lenghtOfFile = url.openConnection().getContentLength();
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			File outputFileCache = new File(DESTINATION_PATH);
			if (outputFileCache.exists()) {
					connection.setRequestProperty("Range", "bytes="+ outputFileCache.length() + "-");
					connection.setAllowUserInteraction(true);
			}
			connection.setConnectTimeout(14000);
			connection.setReadTimeout(20000);
			connection.connect();

			if (connection.getResponseCode() / 100 != 2)
				throw new Exception("Invalid response code!");
			else {
				
				// The bellow statement only for Nexus
				Map<String, List<String>> map = connection.getHeaderFields();
				String connectionField = connection.getHeaderField("content-range");
				if (connectionField != null) {
					String[] connectionRanges = connectionField.substring("bytes=".length()).split("-");
					downloadedSize = Long.valueOf(connectionRanges[0]);
				}
				if (connectionField == null && outputFileCache.exists())
					outputFileCache.delete();

				long fileLength = connection.getContentLength()+ downloadedSize;
				input = new BufferedInputStream(connection.getInputStream());
				output = new RandomAccessFile(outputFileCache, "rw");
				output.seek(downloadedSize);

				byte data[] = new byte[1024];
				int count = 0;
				int __progress = 0;
				while ((count = input.read(data, 0, 1024)) != -1
						&& __progress != 100) {
						downloadedSize += count;
						output.write(data, 0, count);
						__progress = (int) ((downloadedSize * 100) / fileLength);
						publishProgress((int) ((downloadedSize * 100) / lenghtOfFile));
				}
				output.close();
				input.close();

			}

		} catch (NumberFormatException e) {}
		catch (MalformedURLException e) {} 
		catch (FileNotFoundException e) {} 
		catch (IOException e) {} 
		catch (Exception e) {}
		return null;
	}

	int value = 0;

	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
		progressValue = values[0];
		mDialog.setProgress(values[0]);
	}


	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		if ((progressValue >= 100)) {
			mDialog.dismiss();
			Toast.makeText(mContext,"Download completed",Toast.LENGTH_SHORT).show();
			downloadListener.onSuccess();
		}
	}
		
	}

