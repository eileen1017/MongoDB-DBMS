package test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;

import org.junit.jupiter.api.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import hw5.DB;
import hw5.DBCollection;
import hw5.DBCursor;
import hw5.Document;

class CollectionTester {
	
	/**
	 * Things to consider testing
	 * 
	 * Queries:
	 * 	Find all
	 * 	Find with relational select
	 * 		Conditional operators
	 * 		Embedded documents
	 * 		Arrays
	 * 	Find with relational project
	 * 
	 * Inserts
	 * Updates
	 * Deletes
	 * 
	 * getDocument (done?)
	 * drop
	 * @throws Exception 
	 */
	
	@Test
	public void testGetDocument() throws Exception {
		DB db = new DB("data");
		DBCollection test = db.getCollection("test");
		JsonObject primitive = test.getDocument(0);
		assertTrue(primitive.getAsJsonPrimitive("key").getAsString().equals("value"));
	}
	
	
	
	@Test
	public void testGetComplex() throws Exception {
		DB db = new DB("data");
		DBCollection test = db.getCollection("test");
		JsonObject object = test.getDocument(1);

		assertTrue(object.getAsJsonObject("embedded").isJsonObject());
		assertTrue(!object.getAsJsonObject("embedded").isJsonPrimitive());

		JsonObject embedded = object.getAsJsonObject("embedded");
		assertTrue(embedded.getAsJsonPrimitive("key2").getAsString().equals("value2"));
	  
		JsonArray array = test.getDocument(2).getAsJsonArray("array");
		for (int i = 0; i < array.size(); i++) {
			if (i == 0) {
			  assertTrue(array.get(i).getAsString().equals("one"));
			}
			if (i == 1) {
			  assertTrue(array.get(i).getAsString().equals("two"));
			}
			if (i == 2) {
			  assertTrue(array.get(i).getAsString().equals("three"));
			}
		} 
	 }
	
	
	
	@Test
	void testInsert() {
		DB db = new DB("data");
		if (new File("testfiles/data/test2.json").exists()){
			new File("testfiles/data/test2.json").delete();
		}
		DBCollection test = new DBCollection(db, "test2");
		String json = "{ \"key1\": \"value2\" }";
		JsonObject q1 = Document.parse(json);
	
		json = "{ \"key2\": \"value2\" }";
		JsonObject q2 = Document.parse(json);
		
		json = "{ \"key3\": \"value3\" }";
		JsonObject q3 = Document.parse(json);
		
		test.insert(q1, q2, q3);
		assertTrue(test.count() == 3);
		new File("testfiles/data/test2.json").delete();
	}
	
	

	@Test
	void testRemove() {
		DB db = new DB("data");
		if (new File("testfiles/data/test3.json").exists()){
			new File("testfiles/data/test3.json").delete();
		}
		DBCollection test = new DBCollection(db, "test3");
		
		String json = "{ \"key1\": \"value1\", \"key2\": \"value2\" }";
		JsonObject q1 = Document.parse(json);
	
		json = "{ \"key2\": \"value2\" }";
		JsonObject q2 = Document.parse(json);
		
		json = "{ \"key3\": \"value3\" }";
		JsonObject q3 = Document.parse(json);
		
		test.insert(q1, q2, q3, q3);
		
		json = "{ \"key3\": \"value3\" }";
		JsonObject query = Document.parse(json);
		
		test.remove(query, true);
				
		assertTrue(test.count() == 2);
		
		new File("testfiles/data/test3.json").delete();
	}
	
	@Test
	 public void testNonExistCollection() throws Exception {
	  DB db = new DB("data");
	  DBCollection test = db.getCollection("exist");
	  assertTrue(new File("testfiles/data/exist.json").exists());
	  assertFalse(new File("testfiles/data/nonexist.json").exists());
	  new File("testfiles/data/exist.json").delete();
	 }

	 
}
