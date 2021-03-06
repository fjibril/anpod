package com.blork.anpod.model;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.blork.anpod.provider.PicturesContentProvider;
import com.blork.anpod.util.Utils;

// TODO: Auto-generated Javadoc
/**
 * The Class Picture.
 */
public class Picture {

	/** The apod ID. */
	public int id;
	
	/** The uid. */
	public String uid;
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Picture [id=" + id + ", title=" + title + "]";
	}

	/** The title. */
	public String title;
	
	/** The credit. */
	public String credit;
	
	/** The info. */
	public String info;
	
	/** The imgur id. */
	public String imgurId;
	
	/** The image. */
	public Bitmap image;
	
	
	public Uri uri;

	
	/**
	 * Gets the full size image url.
	 *
	 * @return the full size image url
	 */
	public String getFullSizeImageUrl() {
		return "http://i.imgur.com/" + this.imgurId + ".jpg";
	}
	
	/**
	 * Gets the thumbnail image url.
	 *
	 * @return the thumbnail image url
	 */
	public String getThumbnailImageUrl() {
		return "http://i.imgur.com/" + this.imgurId + "t.jpg";
	}
	
	/**
	 * Save image.
	 *
	 * @param context the context
	 * @param internal the internal
	 * @param sd the sd
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws UnsupportedSavingException the unsupported saving exception
	 */
	public void saveImage(Context context, boolean internal, boolean sd) throws IOException {
		
		if(internal){
			Log.i(Utils.TAG, "Saving image to internal storage");

			String fileName = "apod.jpg";

			FileOutputStream fileOutputStream = context.openFileOutput(fileName, Context.MODE_WORLD_READABLE);
			BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);
			image.compress(CompressFormat.JPEG, 75, bos);
			bos.flush();
			bos.close();
		}

		if(sd){
			boolean sdAvailable = false;
			boolean sdWriteable = false;
			String state = Environment.getExternalStorageState();
			if (Environment.MEDIA_MOUNTED.equals(state)) {
				sdAvailable = sdWriteable = true;
			} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
				sdAvailable = true;
				sdWriteable = false;
			} else {
				sdAvailable = sdWriteable = false;
			}

			if (sdAvailable && sdWriteable) {

				Log.i(Utils.TAG, "SD card is writable");

				try{			
					File dir = new File(Environment.getExternalStorageDirectory().toString()+"/APOD");
					dir.mkdir();

					File imageFile = new File(dir.toString()+"/"+URLEncoder.encode(title)+".jpg");


					FileOutputStream fileOutputStream = new FileOutputStream(imageFile);
					BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);


					image.compress(CompressFormat.JPEG, 75, bos);
					bos.flush();
					bos.close();

				}catch(Exception e){
					e.printStackTrace();
				}
			}

		}
	}


	Uri save(Context context) {
		Log.d("", "saving " + this.title);
		ContentValues values = new ContentValues();
		values.put(PicturesContentProvider.ID, id);
		
		values.put(PicturesContentProvider.TITLE, title);
		values.put(PicturesContentProvider.CREDIT, credit);
		values.put(PicturesContentProvider.INFO, info);
		values.put(PicturesContentProvider.IMGURURL, imgurId);
		values.put(PicturesContentProvider.UID, uid);

		try {
			List<Picture> pictures = PictureFactory.getLocalPictures(context);
			if (pictures.contains(this)) {
				return null;
			}
			
			Uri uri = context.getContentResolver().insert(PicturesContentProvider.CONTENT_URI, values);
			return uri;
		} catch (Exception e) {
			return null;
		}
		
	}

	/**
	 * Instantiates a new picture.
	 *
	 * @param uid the uid
	 * @param title the title
	 * @param credit the credit
	 * @param info the info
	 * @param imgurId the imgur id
	 */
	Picture(int aid, String uid, String title, String credit,
			String info, String imgurId) {
		this.id = aid;
		this.uid = uid;
		this.title = title;
		this.credit = credit;
		this.info = info;
		this.imgurId = imgurId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uid == null) ? 0 : uid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Picture other = (Picture) obj;
		if (uid == null) {
			if (other.uid != null)
				return false;
		} else if (!uid.equals(other.uid))
			return false;
		return true;
	}
	
	
}
