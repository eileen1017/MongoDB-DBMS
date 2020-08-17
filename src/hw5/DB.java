package hw5;

import java.io.File;
import java.io.IOException;

public class DB {

	String fname;
	File file;
	/**
	 * Creates a database object with the given name.
	 * The name of the database will be used to locate
	 * where the collections for that database are stored.
	 * For example if my database is called "library",
	 * I would expect all collections for that database to
	 * be in a directory called "library".
	 * 
	 * If the given database does not exist, it should be
	 * created.
	 */
	public DB(String name) {
		this.fname = "testfiles/" + name;
		this.file = new File(this.fname);
		if (!this.file.exists()) {
			this.file.mkdir();
		}
	}
	
	/**
	 * Retrieves the collection with the given name
	 * from this database. The collection should be in
	 * a single file in the directory for this database.
	 * 
	 * Note that it is not necessary to read any data from
	 * disk at this time. Those methods are in DBCollection.
	 */
	public DBCollection getCollection(String name) throws Exception {
		try {
			return new DBCollection(this, name);
		} catch (Exception e){
			throw new Exception("Failed to get collection.");
		}
	}
	
	/**
	 * Drops this database and all collections that it contains
	 */
	public void dropDatabase() {
		if (this.file.exists()) {
			if (this.file.isDirectory()) {
				if (this.file.list().length == 0) {
					this.file.delete();
				} else {
					String files[] = this.file.list();
					for (String fname: files) {
						File fileDelete = new File(this.file.getPath(), fname);
						fileDelete.delete();
					}
					this.file.delete();
				}
			} else {
				this.file.delete();
			}
		}
	}
	
	
}
