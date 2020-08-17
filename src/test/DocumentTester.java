package test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.google.gson.JsonObject;

import hw5.Document;

class DocumentTester {
	
	/*
	 * Things to consider testing:
	 * 
	 * Invalid JSON
	 * 
	 * Properly parses embedded documents
	 * Properly parses arrays
	 * Properly parses primitives (done!)
	 * 
	 * Object to embedded document
	 * Object to array
	 * Object to primitive
	 */
	
	@Test
	public void testParsePrimitive() {
		String json = "{ \"key\":\"value\" }";//setup
		JsonObject results = Document.parse(json); //call method to be tested
		assertTrue(results.getAsJsonPrimitive("key").getAsString().equals("value")); //verify results
	}
	
	@Test
	public void testComplexParse() {
		String json = "{ \"Teammates\": [\"Li\", \"Zeng\"], \"GPA\": \"4.0\" }";
		JsonObject results = Document.parse(json);
		assertTrue(results.getAsJsonPrimitive("GPA").getAsString().equals("4.0"));
		assertTrue(results.getAsJsonArray("Teammates").toString().equals("[\"Li\",\"Zeng\"]"));
	}
	
	
	@Test
	public void testObject() {
		String json = "{\"State\":\"Florida\",\"City\":\"Orlando\"}";
		JsonObject jsonO = Document.parse(json);
		JsonObject newJsonO = new JsonObject();
		newJsonO.add("State", jsonO);
		String result = Document.toJsonString(newJsonO);
		assertTrue(result.equals("{" + "\"State\":" + json + "}"));
	}
	
	
	@Test
	public void testArray() {
		String json = "{\"GPA\":\"4.0\",\"Teammates\":[\"Li\",\"Zeng\"]}";
		JsonObject jsonO = Document.parse(json);
		assertTrue(Document.toJsonString(jsonO).equals(json));
	}


}
