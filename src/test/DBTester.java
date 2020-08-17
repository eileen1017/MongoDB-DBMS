package test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;

import org.junit.jupiter.api.Test;

import hw5.DB;

class DBTester {
	
	/**
	 * Things to consider testing:
	 * 
	 * Properly creates directory for new DB (done)
	 * Properly accesses existing directory for existing DB
	 * Properly accesses collection
	 * Properly drops a database
	 * Special character handling?
	 */
	
	@Test
	public void testCreateDB() {
		DB hw5 = new DB("hw5"); //call method
		assertTrue(new File("testfiles/hw5").exists()); //verify results
		new File("testfiles/hw5").delete();
	}
	
	@Test
	public void testAddMultDB() {
		DB db1 = new DB("t1");
		DB db2 = new DB("t2");
		DB db3 = new DB("t3");
		DB db4 = new DB("t4");
		DB db5 = new DB("t5");
	  
		assertTrue(new File("testfiles/t1").exists());
		assertTrue(new File("testfiles/t2").exists());
	  	assertTrue(new File("testfiles/t3").exists());
	  	assertTrue(new File("testfiles/t4").exists());
	  	assertTrue(new File("testfiles/t5").exists());
	  	
	  	new File("testfiles/t1").delete();
	    new File("testfiles/t2").delete();
	    new File("testfiles/t3").delete();
	    new File("testfiles/t4").delete();
	    new File("testfiles/t5").delete();
	  
	}
	
	@Test
	public void testDuplicateDB() {
		DB db1 = new DB("foundDB");
		assertTrue(new File("testfiles/foundDB").exists());
		
		
		DB duplicateDB = new DB("foundDB");
		assertTrue(new File("testfiles/foundDB").exists());
		
		new File("testfiles/foundDB").delete();
		
	}

}
