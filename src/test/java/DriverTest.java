

import org.junit.Assert;
import org.junit.Test;
import org.junit.Before;

/** 
* Driver Tester. 
* 
* @author <Authors name> 
* @since <pre>. 27, 2019</pre>
* @version 1.0 
*/
public class DriverTest { 
private Driver driver;
@Before
public void before() throws Exception {
    driver = new Driver("Vasya Pupkin", 3.5, "1", "No violation");
}


/**
* 
* Method: getFIO() 
* 
*/ 
@Test
public void testGetFIO() throws Exception {
    Assert.assertEquals(driver.getFIO(), "Vasya Pupkin");
} 

/** 
* 
* Method: getExperience() 
* 
*/ 
@Test
public void testGetExperience() throws Exception { 
//TODO: Test goes here...
    Assert.assertEquals(driver.getExperience(), 3.5, 0.01);
} 

/** 
* 
* Method: getClassification() 
* 
*/ 
@Test
public void testGetClassification() throws Exception { 
//TODO: Test goes here...
    Assert.assertEquals(driver.getClassification(), "1");
} 

/** 
* 
* Method: setFIO(String FIO) 
* 
*/ 
@Test
public void testSetFIO() throws Exception { 
//TODO: Test goes here...
    driver.setFIO("Bla bla");
    Assert.assertEquals(driver.getFIO(), "Bla bla");
} 

/** 
* 
* Method: setExperience(int experience) 
* 
*/ 
@Test
public void testSetExperience() throws Exception { 
//TODO: Test goes here...
    driver.setExperience(2.5);
    Assert.assertEquals(driver.getExperience(), 2.5,0.01);

} 

/** 
* 
* Method: setClassification(String classification) 
* 
*/ 
@Test
public void testSetClassification() throws Exception { 
//TODO: Test goes here...
driver.setClassification("M");
    Assert.assertEquals(driver.getClassification(), "M");
} 

/** 
* 
* Method: getViolations() 
* 
*/ 
@Test
public void testGetViolations() throws Exception { 
//TODO: Test goes here...
    Assert.assertEquals(driver.getViolations(), "No violation");
} 

/** 
* 
* Method: setViolations(String violations) 
* 
*/ 
@Test
public void testSetViolations() throws Exception { 
//TODO: Test goes here...
    driver.setViolations("Very Bad");
    Assert.assertEquals(driver.getViolations(), "Very Bad");
} 


} 
