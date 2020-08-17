package hw5;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class DBCursor implements Iterator<JsonObject>{

	DBCollection collection;
	JsonObject query;
	JsonObject fields;
	ArrayList<JsonObject> document;
	long counter;
	
	public DBCursor(DBCollection collection, JsonObject query, JsonObject fields) {
		
		// initialization
		this.collection = collection;
		this.query = query;
		this.fields = fields;
		this.document = new ArrayList<>();
		
		// find dealing with null query and null fields
		if (this.query == null && this.fields == null) {
			this.document = this.collection.document;
		}
		
		// find dealing with not null query and null fields
		else if (this.query != null && this.fields == null) {
			if (this.query.toString().equals("")) {
				this.document = this.collection.document;
			}
			for (JsonObject jObject: this.collection.document) {
				// check if jObject contains same JSon as query
				if (satisfyQuery(jObject, this.query)) {
					this.document.add(jObject);
				}
			}
		// find dealing with not null query and not null fields
		} else {
			ArrayList<JsonObject> queryDocuments = new ArrayList<>();
			if (this.query.toString().equals("")) {
				queryDocuments = this.collection.document;
			}
			for (JsonObject jObject: this.collection.document) {
				if (satisfyQuery(jObject, this.query)) {
					queryDocuments.add(jObject);
				}
			}
			
			for (JsonObject jO : queryDocuments) {
				// get corresponding fields
				if (getFields(jO, fields) != null) {
					this.document.add(getFields(jO, fields));
				}
			}
		}
	}
	
	
	/**
	 * Returns true if there are more documents to be seen
	 */
	public boolean hasNext() {
		return this.counter < this.count();
	}

	/**
	 * Returns the next document
	 */
	public JsonObject next() {
		Iterator<JsonObject> iterator = this.document.iterator(); 
		this.counter++;
		return (JsonObject) iterator.next();
	}
	
	
	/**
	 * Returns the total number of documents
	 */
	public long count() {
		return this.document.size();
	}
	
	//helper
	public boolean satisfyQuery(JsonObject jObject, JsonObject query) {
		for (String q : query.keySet()) {
			String[] parseQuery = q.split("\\.");
			
			// check length if one 
			// if query not match return false or if query not exists
			if (parseQuery.length == 1) {
				if (jObject.has(q)) {
					if (!queryComparison(jObject.get(q), query.get(q))) {
						return false;
					}
				} else {
					return false;
				}
			} 
			// check length bigger than 1
			// check condition for both embedded object and array
			else if (parseQuery.length > 1){
				JsonElement docCopy = jObject;
				int counter = 0;
				while (counter < parseQuery.length - 1) {
					
					// check embedded object
					if (docCopy.isJsonObject() && docCopy.getAsJsonObject().has(parseQuery[counter])) {
						JsonElement docObject = docCopy.getAsJsonObject().get(parseQuery[counter]);
						if (docObject.isJsonNull() || docObject.isJsonPrimitive()) {
							return false;
						}
						docCopy = docObject;
						counter++;
						continue;
					}
					
					// check array 
					if (docCopy.isJsonArray() && isInteger(parseQuery[counter])) {
						int position = Integer.parseInt(parseQuery[counter]);
						if (position >= 0 && position < docCopy.getAsJsonArray().size()) {
							JsonElement docObject = docCopy.getAsJsonArray().get(position);
							if (docObject.isJsonNull() || docObject.isJsonPrimitive()) {
								return false;
							}
							docCopy = docObject;
							counter++;
							continue;
						} else {
							return false;
						}
					}
					return false;
				}
				
				// post process
				if (docCopy.isJsonObject() && docCopy.getAsJsonObject().has(parseQuery[counter])) {
					
					JsonElement docObject = docCopy.getAsJsonObject().get(parseQuery[counter]);
					if (!queryComparison(docCopy.getAsJsonObject().get(parseQuery[counter]), query.get(q))) {
						return false;
					}
				}
				
				if (docCopy.isJsonArray() && isInteger(parseQuery[counter])) {
					int position = Integer.parseInt(parseQuery[counter]);
					if (position >= 0 && position < docCopy.getAsJsonArray().size()) {
						JsonElement docObject = docCopy.getAsJsonArray().get(position);
						if (!queryComparison(docObject, query.get(q))) {
							return false;
						}
					} else {
						return false;
					}
				}
			} else {
				return false;
			}
		}
		return true;
	}
	
	
	// reference: https://stackoverflow.com/questions/19216864/how-to-check-if-each-element-in-a-string-array-is-an-integer
	public static boolean isInteger(String s) {
		  try { 
		      Integer.parseInt(s); 
		   } catch(NumberFormatException e) { 
		      return false; 
		   }
		   // only got here if we didn't return false
		   return true;
		}
	
	public boolean queryComparison(JsonElement a, JsonElement b) {
		if (b.isJsonPrimitive()) {
			if (!a.getAsJsonPrimitive().equals(b.getAsJsonPrimitive())) {
				return false;
			}
		} 
		// if query is JSon object
		// check comparison operator 
		else if (b.isJsonObject()) {
			for (String objectString : b.getAsJsonObject().keySet()) {
				JsonElement value = b.getAsJsonObject().get(objectString);
				if (value.isJsonPrimitive()) {
					if (objectString.compareTo("$eq") == 0) {
						if (!a.getAsJsonPrimitive().equals(value.getAsJsonPrimitive())) {
							return false;
						} 
					} else if (objectString.compareTo("$gt") == 0) {
						
						// integer comparison
						if (a.getAsJsonPrimitive().isNumber()) {
							if (a.getAsDouble() <= value.getAsDouble()) {
								return false;
							}
						}
						// string comparison
						else if (a.getAsJsonPrimitive().isString() && value.getAsJsonPrimitive().isString()) {
							if (a.getAsString().hashCode() <= value.getAsString().hashCode()) {
								return false;
							}
						}
					}
					// same as above 
					else if (objectString.compareTo("$gte") == 0) {
						if (a.getAsJsonPrimitive().isNumber()) {
							if (a.getAsDouble() < value.getAsDouble()) {
								return false;
							}
						}
						else if (a.getAsJsonPrimitive().isString() && value.getAsJsonPrimitive().isString()) {
							if (a.getAsString().hashCode() < value.getAsString().hashCode()) {
								return false;
							}
						}
					}else if (objectString.compareTo("$lt") == 0) {
						if (a.getAsJsonPrimitive().isNumber()) {
							if (a.getAsDouble() >= value.getAsDouble()) {
								return false;
							}
						}
						else if (a.getAsJsonPrimitive().isString() && value.getAsJsonPrimitive().isString()) {
							if (a.getAsString().hashCode() >= value.getAsString().hashCode()) {
								return false;
							}
						}
					}else if (objectString.compareTo("$lte") == 0) {
						if (a.getAsJsonPrimitive().isNumber()) {
							if (a.getAsDouble() > value.getAsDouble()) {
								return false;
							}
						}
						else if (a.getAsJsonPrimitive().isString() && value.getAsJsonPrimitive().isString()) {
							if (a.getAsString().hashCode() > value.getAsString().hashCode()) {
								return false;
							}
						}
					}else if (objectString.compareTo("$ne") == 0) {
						if (a.getAsJsonPrimitive().equals(value.getAsJsonPrimitive())) {
							return false;
						} 
					}
				} 
			}
			
			// check array in and not in operators
			if (a.isJsonArray()) {
				boolean flag = false;
				JsonArray aValue = a.getAsJsonArray();
				for (String objectString : b.getAsJsonObject().keySet()) {
					JsonElement bValue = b.getAsJsonObject().get(objectString);
					if (objectString.compareTo("$in") == 0) {
						for (int i = 0; i < aValue.size(); i++) {
							if (aValue.get(i).equals(bValue)) {
								flag = true;
							}
						}
						return flag;
					} else if (objectString.compareTo("$nin") == 0) {
						for (int i = 0; i < aValue.size(); i++) {
							if (aValue.get(i).equals(bValue)) {
								return false;
							}
						}
					}
				}
			}
		} 
		return true;
	}
	
	// check if we can get fields of corresponding query
	public JsonObject getFields(JsonObject jObject, JsonObject fields) {
		JsonObject res = new JsonObject();
		Set<String> fKeys = fields.keySet();
		for (String fkey: fKeys) {
			String[] fk = fkey.split("\\.");
			if (fk.length == 1) {
				if (jObject.has(fk[0])) {
					res.add(fk[0], jObject.get(fk[0]));
				}
			} else {
				JsonElement docCopy = jObject;
				ArrayList<String> queryType = new ArrayList<>();
				for (int i = 0; i <= fk.length - 1; i++) {
					// check if JSon object and add indicator
					if (docCopy.isJsonObject() && docCopy.getAsJsonObject().has(fk[i])) {
						JsonElement docObject = docCopy.getAsJsonObject().get(fk[i]);
						if (docObject.isJsonNull() || docObject.isJsonPrimitive()) {
							return null;
						}
						
						docCopy = docObject;
						queryType.add("object");
						continue;
					}
					// check if JSon array and add indicator
					if (docCopy.isJsonArray() && isInteger(fk[i])) {
						int position = Integer.parseInt(fk[i]);
						if (position >= 0 && position < docCopy.getAsJsonArray().size()) {
							JsonElement docObject = docCopy.getAsJsonArray().get(position);
							if (docObject.isJsonNull() || docObject.isJsonPrimitive()) {
								return null;
							}
							queryType.add("array");
							docCopy = docObject;
							continue;
						} else {
							return null;
						}
					}
					return null;
				}
				
				if (docCopy == null) {
					return null;
				}
				
				// post-processing
				int count = fk.length - 1;
				while (count >= 0) {
					if (queryType.get(count) == "object") {
						JsonObject objectValues = new JsonObject();
						objectValues.add(fk[count], docCopy);
						res = (JsonObject) ((JsonElement) objectValues);
					} else {
						JsonArray arrayValues = new JsonArray();
						arrayValues.add(docCopy);
						res = (JsonObject) ((JsonElement) arrayValues);
					}
				}
				
			}
			
			
		}
		return res;
	}
	
	

}
