import static org.junit.Assert.*;

import org.junit.Test;

import de.vogella.junit.first.MyClass;


public class GreedOnInvestmentTest {

	@Test
	public void testGetCheapestConfig() {
		GreedOnInvestment tester = new GreedOnInvestment();
		assertEquals("Result", 50, tester.multiply(10, 5));
	}

}
