package hw5;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;

public class DBCollection {

	DB db;
	String name;
	public String fname;
	ArrayList<JsonObject> document;
	/**
	 * Constructs a collection for the given database
	 * with the given name. If that collection doesn't exist
	 * it will be created.
	 * @throws FileNotFoundException 
	 */
	public DBCollection(DB database, String name){
		this.db = database;
		this.name = name;
		this.fname = this.db.fname.toString() + "/" + this.name + ".json";
		this.document = new ArrayList<>();
		File f = new File(this.fname);
		if (!f.exists()) {
			try {
				f.createNewFile();
				return;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		BufferedReader bReader;
		try {
			bReader = new BufferedReader(new FileReader(f));
			StringBuilder sb = new StringBuilder();
			String line = bReader.readLine();
			while (line != null) {
				if (line.trim().isEmpty()) {
					this.document.add(Document.parse(new String(sb)));
					sb = new StringBuilder();
				} else {
					sb.append(line);
				}
				line = bReader.readLine();
			}
			bReader.close();
			if (sb.length() != 0) {
				this.document.add(Document.parse(new String(sb)));
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
		
	}
	
	/**
	 * Returns a cursor for all of the documents in
	 * this collection.
	 */
	public DBCursor find() {
		return new DBCursor(this, null, null);
	}
	
	/**
	 * Finds documents that match the given query parameters.
	 * 
	 * @param query relational select
	 * @return
	 */
	public DBCursor find(JsonObject query) {
		return new DBCursor(this, query, null);
	}
	
	/**
	 * Finds documents that match the given query parameters.
	 * 
	 * @param query relational select
	 * @param projection relational project
	 * @return
	 */
	public DBCursor find(JsonObject query, JsonObject projection) {
		return new DBCursor(this, query, projection);
	}
	
	/**
	 * Inserts documents into the collection
	 * Must create and set a proper id before insertion
	 * When this method is completed, the documents
	 * should be permanently stored on disk.
	 * @param documents
	 */
	public void insert(JsonObject... documents) {
		for (JsonObject jObject: documents) {
			if (!jObject.has("_id")) {
				int hashCode = jObject.hashCode();
				jObject.addProperty("_id", hashCode);
			}
			this.document.add(jObject);
		}
		
		this.updateDisk();
		
	}
	
	
	/**
	 * Locates one or more documents and replaces them
	 * with the update document.
	 * @param query relational select for documents to be updated
	 * @param update the document to be used for the update
	 * @param multi true if all matching documents should be updated
	 * 				false if only the first matching document should be updated
	 */
	public void update(JsonObject query, JsonObject update, boolean multi) {
		DBCursor cursor = this.find(query);
		if (multi) {
			while (cursor.hasNext()) {
				JsonObject jObject = cursor.next();
				int position = this.document.indexOf(jObject);
				this.document.set(position, update);
			}
		} else {
			if (cursor.hasNext()) {
				JsonObject jObject = cursor.next();
				int position = this.document.indexOf(jObject);
				this.document.set(position, update);
			}
		}
		this.updateDisk();
	}
	
	/**
	 * Removes one or more documents that match the given
	 * query parameters
	 * @param query relational select for documents to be removed
	 * @param multi true if all matching documents should be updated
	 * 				false if only the first matching document should be updated
	 */
	public void remove(JsonObject query, boolean multi) {
		DBCursor cursor = this.find(query);
		if (multi) {
			while (cursor.hasNext()) {
				JsonObject jObject = cursor.next();
				this.document.remove(jObject);
			}
		} else {
			if (cursor.hasNext()) {
				JsonObject jObject = cursor.next();
				this.document.remove(jObject);
			}
		}
		this.updateDisk();
	}
	
	/**
	 * Returns the number of documents in this collection
	 */
	public long count() {
		return this.document.size();
	}
	
	public String getName() {
		return this.name;
	}
	
	/**
	 * Returns the ith document in the collection.
	 * Documents are separated by a line that contains only a single tab (\t)
	 * Use the parse function from the document class to create the document object
	 */
	public JsonObject getDocument(int i) {
		return this.document.get(i);
	}
	
	/**
	 * Drops this collection, removing all of the documents it contains from the DB
	 */
	public void drop() {
		new File(this.fname).delete();
		this.document.clear();
	}
	
	//helper 
	public void updateDisk() {
		File file = new File(this.fname);
        FileWriter fr = null;
        try {
            fr = new FileWriter(file);
            for (JsonObject jObject: this.document) {
            	fr.write(Document.toJsonString(jObject));
            	fr.write("\n\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            //close resources
            try {
                fr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
	}
	
}
