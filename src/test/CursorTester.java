package test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.google.gson.JsonObject;

import hw5.DB;
import hw5.DBCollection;
import hw5.DBCursor;
import hw5.Document;

class CursorTester {
	
	@BeforeAll
	 public static void setup() throws Exception {
	  
	  DB db = new DB("data");
	  DBCollection c1 = db.getCollection("test4");
	  
	  // simple embedded data
	  String s1 = "{ student: \"li\", age: 16, more: { height: 180, weight: 110, hobby: \"game\" }, sex: \"F\" }";
	  String s2 = "{ student: \"zeng\", age: 18, more: { height: 150, weight: 120, hobby: \"eat\" }, sex: \"F\" }";
	  String s3 = "{ student: \"tian\", age: 6, more: { height: 170, weight: 100, hobby: \"coding\" }, sex: \"F\" }";
	  String s4 = "{ student: \"chen\", age: 30, more: { height: 146, weight: 130, hobby: \"sleep\" }, sex: \"M\" }";
	  String s5 = "{ student: \"zhang\", age: 23, more: { height: 165, weight: 115, hobby: \"game\" }, sex: \"M\" }";
	  
	  c1.insert(Document.parse(s1), Document.parse(s2), Document.parse(s3), Document.parse(s4), Document.parse(s5));
	  

	  DBCollection c2 = db.getCollection("test5");
	  
	  // array data
	  s1 = "{ food: \"taco\", price: 5, ingredients: [\"beef\", \"pickle\"], size: [ 5, 6 ] }";
	  s2 = "{ food: \"noodle\", price: 25, ingredients: [\"chicken\", \"pepper\"], size: [ 10, 2 ] }";
	  s3 = "{ food: \"salad\", price: 20, ingredients: [\"spinach\", \"chicken\"], size: [ 7, 9 ] }";
	  s4 = "{ food: \"steak\", price: 50, ingredients: [\"beef\", \"mushroom\"], size: [ 7, 9 ] }";
	  s5 = "{ food: \"soup\", price: 10, ingredients: [\"clam\", \"potato\"], size: [ 4, 6 ] }";
	  
	  c2.insert(Document.parse(s1), Document.parse(s2), Document.parse(s3), Document.parse(s4), Document.parse(s5));
	  
	 }

	/**
	 * Things to consider testing:
	 * 
	 * hasNext (done?)
	 * count (done?)
	 * next (done?)
	 * @throws Exception 
	 */

	@Test
	public void testFindAll() throws Exception {
		DB db = new DB("data");
		DBCollection test = db.getCollection("test");
		DBCursor results = test.find();
		
		assertTrue(results.count() == 3);
		assertTrue(results.hasNext());
		JsonObject d1 = results.next(); //pull first document
		//verify contents?
		assertTrue(results.hasNext());//still more documents
		JsonObject d2 = results.next(); //pull second document
		//verify contents?
		assertTrue(results.hasNext()); //still one more document
		JsonObject d3 = results.next();//pull last document
		assertFalse(results.hasNext());//no more documents
	}
	

	 @Test
	 public void testFind() {
		 DB db = new DB("data");
		 DBCollection collection = new DBCollection(db, "test4");
	  
		 String query = "{ sex: \"F\" }";
	  
		 DBCursor cursor = collection.find(Document.parse(query));
	  
		 assertTrue(cursor.count() == 3);
		 assertTrue(cursor.hasNext());
	  
		 JsonObject d1 = cursor.next();
		 assertTrue(cursor.hasNext());
	  	
		 JsonObject d2 = cursor.next();
		 assertTrue(cursor.hasNext());
	  
		 JsonObject d3 = cursor.next();
		 assertTrue(!cursor.hasNext());
		 
		 query = "{ \"age\": 16 }";
		 DBCursor cursor2 = collection.find(Document.parse(query));
		 assertTrue(cursor2.count() == 1);
	 }
	 
	 @Test
	 public void testEmbedded() {
		 DB db = new DB("data");
		 DBCollection collection = new DBCollection(db, "test4");
		 String query = "{\"more.height\":180}";
	  
		 DBCursor cursor2 = collection.find(Document.parse(query));
		 assertTrue(cursor2.count() == 1);
	  
		 query = "{\"more.height\":{$lt:180}}";
		 DBCursor cursor = collection.find(Document.parse(query));
	  
		 assertTrue(cursor.count() == 4);
		 assertTrue(cursor.hasNext());
	  
	 }
	 
	 @Test
	 public void testLT() {
		 DB db = new DB("data");
		 DBCollection collection = new DBCollection(db, "test4");
		 
		 String query = "{\"age\":{\"$lt\":\"20\"}}";
		 DBCursor cursor2 = collection.find(Document.parse(query));
		 assertTrue(cursor2.count() == 3);
	 }
	 
	 @Test
	 public void testLTE() {
		 DB db = new DB("data");
		 DBCollection collection = new DBCollection(db, "test4");
		 
		 String query = "{\"age\":{\"$lte\":\"23\"}}";
		 DBCursor cursor2 = collection.find(Document.parse(query));
		 assertTrue(cursor2.count() == 4);
	 }
	 
	 @Test
	 public void testGT() {
		 DB db = new DB("data");
		 DBCollection collection = new DBCollection(db, "test4");
		 
		 String query = "{\"age\":{\"$gt\":\"20\"}}";
		 DBCursor cursor3 = collection.find(Document.parse(query));
		 assertTrue(cursor3.count() == 2);	
	 }
	 
	 @Test
	 public void testGTE() {
		 DB db = new DB("data");
		 DBCollection collection = new DBCollection(db, "test4");
		 
		 String query = "{\"age\":{\"$gte\":\"23\"}}";
		 DBCursor cursor3 = collection.find(Document.parse(query));
		 assertTrue(cursor3.count() == 2);	
	 }
	 
	 @Test
	 public void testMultiQuery() {
		 DB db = new DB("data");
		 DBCollection collection = new DBCollection(db, "test4");
		 
		 String query = "{\"age\":{\"$lte\":\"23\"},\"more.height\":{\"$lte\":\"170\"}}";
		 DBCursor cursor3 = collection.find(Document.parse(query));
		 assertTrue(cursor3.count() == 3);	
	 }
	 
	 @Test
	 public void testField() {
		 DB db = new DB("data");
		 DBCollection collection = new DBCollection(db, "test4");
		 
		 String query = "{\"age\":{\"$lte\":\"23\"}}";
		 JsonObject q = Document.parse(query);
		 String field = "{\"more\":\"1\"}";
		 JsonObject f = Document.parse(field);
		 DBCursor cursor3 = collection.find(q,f);
		 assertTrue(cursor3.next().toString().equals("{\"more\":{\"height\":180,\"weight\":110,\"hobby\":\"game\"}}"));
	 }
	 
	 @Test
	public void testArray() throws Exception {
		 DB db = new DB("data");
		 DBCollection collection = new DBCollection(db, "test5");
		
		 String query = "{\"ingredients\":{\"$in\":\"beef\"}}";
		 DBCursor cursor = collection.find(Document.parse(query));
		 assertTrue(cursor.count() == 2);
		
		 query = "{\"size\":{\"$in\":6}}";
		 DBCursor cursor2 = collection.find(Document.parse(query));
		 assertTrue(cursor2.count() == 2);
		
	}
	 
	 @Test
	 public void testArray2() {
		 DB db = new DB("data");
		 DBCollection collection = new DBCollection(db, "test5");
		
		 String query = "{\"ingredients\":{\"$nin\":\"beef\"}}";
		 DBCursor cursor = collection.find(Document.parse(query));
		 assertTrue(cursor.count() == 3);
		
		 query = "{\"size\":{\"$nin\":6}}";
		 DBCursor cursor2 = collection.find(Document.parse(query));
		 assertTrue(cursor2.count() == 3);
	 }
	 
	 
	 
	 @AfterAll
	 public static void clean() {
		 new File("testfiles/data/test4.json").delete();
		 new File("testfiles/data/test5.json").delete();
	 }


	

}
