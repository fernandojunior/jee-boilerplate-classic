package foo.bar;

import org.junit.Test;

import junit.framework.TestCase;

public class BarTest extends TestCase {

	protected void setUp() {
		// runs before every test invocation
	}

	protected void tearDown() {
		// runs after every test method.
	}

	@Test
	public void testHelloWorldMessage() {
		assertTrue("Hello World" == new HelloWorld().message);
	}

}
